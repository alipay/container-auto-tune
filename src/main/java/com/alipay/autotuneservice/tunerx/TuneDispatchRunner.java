package com.alipay.autotuneservice.tunerx;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.base.cache.LocalCache;
import com.alipay.autotuneservice.dao.TuneLogInfo;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneLogInfoRecord;
import com.alipay.autotuneservice.model.pipeline.PipelineStatus;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.model.tunepool.MetaDataType;
import com.alipay.autotuneservice.model.tunepool.TuneConsistencyRq;
import com.alipay.autotuneservice.service.PodService;
import com.alipay.autotuneservice.service.pipeline.TunePipelineService;
import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 最终一致性执行器,用于check、下发任务
 */
@Service
@Slf4j
public class TuneDispatchRunner extends TuneAbstractRunner {

    private ObservableEmitter<TuneConsistencyRq> jemitter;
    @Autowired
    private LocalCache<Object, Object>           localCache;
    @Autowired
    private PodService                           podService;
    @Autowired
    private TuneChangeRunner                     tuneChangeRunner;
    @Autowired
    private AsyncTaskExecutor                    webTaskExecutor;
    @Autowired
    private TuneLogInfo                          tuneLogInfo;
    @Autowired
    private TunePipelineService                  tunePipelineService;
    @Autowired
    private TunePipelineRepository               tunePipelineRepository;

    @PostConstruct
    public void init() {
        ExecutorScheduler observeScheduler = new ExecutorScheduler(webTaskExecutor);
        ExecutorScheduler subscribeExecutor = new ExecutorScheduler(webTaskExecutor);
        //整体进程分为两部分：1、执行不一致检查。 2、进行一致性性修复任务下发(分批)
        Observable.create((ObservableEmitter<TuneConsistencyRq> emitter) -> jemitter = emitter)
                .observeOn(observeScheduler)
                .subscribeOn(subscribeExecutor)
                .filter(tuneConsistencyRq -> {
                    //判断整体状态是否为暂停态
                    TunePlan tunePlan = tunePipelineService.findByPipelineId(tuneConsistencyRq.getPipelineId());
                    if (tunePlan == null) {
                        return Boolean.FALSE;
                    }
                    if (tunePlan.getTunePlanStatus() == TunePlanStatus.PAUSE) {
                        log.info(String.format("pipeline=[%s] pause!!", tuneConsistencyRq.getPipelineId()));
                    }
                    return !tunePlan.getTunePlanStatus().isFinal();
                })
                .filter(tuneConsistencyRq -> {
                    int podNum = podService.getAppPodNum(tuneConsistencyRq.getAppId());
                    if (podNum <= 1) {
                        log.error(String.format("tuneDispatchRunner is error-->podNum=[%s]", podNum));
                        return Boolean.FALSE;
                    }
                    //获取配置文件，比对pod数量
                    Map<MetaDataType, MetaData> metaDataMap = tuneConsistencyRq.getMetaDataMap();
                    metaDataMap.forEach((dataType, metaData) -> {
                        List<TuneConsistencyRq.ChangeRq> changeRqs = Lists.newArrayList();
                        if (dataType == MetaDataType.DEFAULT) {
                            return;
                        }
                        //分别判断比例数是否相符
                        int podJvmNum = podService.getAppPodNumByJvmId(tuneConsistencyRq.getAppId(), (int) metaData.getJvmMarketId());
                        //分别判断比例数是否相符
                        TuneConsistencyRq.ChangeRq changeRq = getChangeRq(tuneConsistencyRq, dataType, metaData, metaData.getReplicas());
                        switch (metaData.getType()) {
                            case NUMBER:
                                if (metaData.getReplicas() <= podJvmNum) {
                                    //更新完成
                                    log.info("{},{},{}更新完成", metaData.getJvmMarketId(), metaData.getReplicas(), podJvmNum);
                                    break;
                                }
                                //TODO 存在负数,需要考虑在内
                                changeRq = getChangeRq(tuneConsistencyRq, dataType, metaData, metaData.getReplicas() - podJvmNum);
                                changeRqs.add(changeRq);
                                break;
                            case RATIO:
                                if ((podJvmNum / podNum) * 100 >= metaData.getReplicas()) {
                                    //更新完成
                                    break;
                                }
                                long num = (podNum * metaData.getReplicas()) / 100 - podJvmNum;
                                //存在负数,需要考虑在内
                                changeRq = getChangeRq(tuneConsistencyRq, dataType, metaData, num <= 1 ? 1 : num);
                                changeRqs.add(changeRq);
                                break;
                            default:
                                break;
                        }
                        if (CollectionUtils.isEmpty(changeRqs)) {
                            changePoolToT(changeRq);
                            //更新log
                            TuneLogInfoRecord record = generateRecord(tuneConsistencyRq);
                            record.setActionDesc("SUCCESS");
                            tuneLogInfo.updateChangePodInfo(record, Lists.newArrayList());
                            return;
                        }
                        tuneConsistencyRq.getChangeRqs().addAll(changeRqs);
                    });
                    if (CollectionUtils.isEmpty(tuneConsistencyRq.getChangeRqs())) {
                        return Boolean.FALSE;
                    }
                    return Boolean.TRUE;
                })
                .filter(tuneConsistencyRq -> {
                    //判断是否为空
                    if (tuneConsistencyRq.checkEmpty()) {
                        return Boolean.FALSE;
                    }
                    //判断任务是否有提交记录
                    Long time = (Long) localCache.get(tuneConsistencyRq.generateUnionKey());
                    if (time == null) {
                        return Boolean.TRUE;
                    }
                    //等待30分钟一次的调度
                    if (isGray(tuneConsistencyRq.getPipelineId())) {
                        return Boolean.TRUE;
                    }
                    return time - System.currentTimeMillis() >= 1800000;
                })
                .subscribe(tuneConsistencyRq -> {
                    //记录分发日志
                    localCache.put(tuneConsistencyRq.generateUnionKey(), System.currentTimeMillis(), 10 * 60);
                    log.info(Thread.currentThread().getName() + ":" + JSONObject.toJSONString(tuneConsistencyRq));
                    //下发分批策略，获取下当前应用的最大分批策略
                    List<TuneConsistencyRq.ChangeRq> changeRqs = tuneConsistencyRq.getChangeRqs();
                    changeRqs.forEach(changeRq -> {
                        tuneChangeRunner.fire(changeRq);
                    });
                });
    }

    private TuneConsistencyRq.ChangeRq getChangeRq(TuneConsistencyRq tuneConsistencyRq, MetaDataType dataType, MetaData metaData,
                                                   long changeNum) {
        TuneConsistencyRq.ChangeRq changeRq = new TuneConsistencyRq.ChangeRq();
        BeanUtils.copyProperties(tuneConsistencyRq, changeRq);
        changeRq.setMetaData(metaData);
        changeRq.setMetaDataType(dataType);
        changeRq.setPipelineId(tuneConsistencyRq.getPipelineId());
        changeRq.setChangeNum(changeNum);
        return changeRq;
    }

    private TuneLogInfoRecord generateRecord(TuneConsistencyRq consistencyRq) {
        TuneLogInfoRecord record = new TuneLogInfoRecord();
        record.setPipelineId(consistencyRq.getPipelineId());
        record.setAppId(consistencyRq.getAppId());
        MetaData metaData = consistencyRq.getMetaDataMap().get(MetaDataType.TUNE);
        record.setJvmMarketId((int) metaData.getJvmMarketId());
        record.setBatchNo(Integer.parseInt(metaData.getDesc()));
        record.setChangetTime(LocalDateTime.now());
        record.setActionDesc("SUCCESS");
        return record;
    }

    public void fire(TuneConsistencyRq tuneConsistencyRq) {
        try {
            this.jemitter.onNext(tuneConsistencyRq);
        } catch (Exception e) {
            log.error("tuneDispatchRunner-->fire error", e);
        }
    }

    private boolean isGray(Integer pipelineId) {
        try {
            TunePipeline tunePipeline = tunePipelineRepository.findByMachineIdAndPipelineId(pipelineId);
            if (tunePipeline == null) {
                return false;
            }
            return tunePipeline.getPipelineStatus().equals(PipelineStatus.GRAY);
        } catch (Exception e) {
            return false;
        }
    }
}