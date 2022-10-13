package com.alipay.autotuneservice.tunerx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePoolInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.TunePoolInfoRecord;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.model.tunepool.MetaDataType;
import com.alipay.autotuneservice.model.tunepool.PoolType;
import com.alipay.autotuneservice.model.tunepool.TuneConsistencyRq;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import com.alipay.autotuneservice.tunerx.watcher.TuneEventWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对tunepool的任务巡检器
 */
@Component
@Slf4j
public class TuneCheckRunner {

    /**
     * 调参最终一致性
     */
    private static final String LOCK_KEY = "tuneConsistRunner";

    @Autowired
    private RedisClient  redisClient;
    @Autowired
    private TunePoolInfo tunePoolInfo;
    @Autowired
    private TuneDispatchRunner     tuneDispatchRunner;
    @Autowired
    private TuneEventWatcher       tuneEventWatcher;
    @Autowired
    private TunePipelineRepository tunePipelineRepository;

    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        redisClient.doExec(LOCK_KEY, () -> {
            log.info(String.format("get %s lock", LOCK_KEY));
            //check状态机驱动
            doChecker();
            //check最终一致性
            doAsyncWork();
        });
    }

    private void doAsyncWork() {
        //获取数据
        List<TunePoolInfoRecord> records = tunePoolInfo.findRunningPool();
        //过滤均为Running的实例
        records = records.stream()
                .filter(record -> !StringUtils.equals(record.getBatchPoolStatus(), record.getExperimentPoolStatus()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        records.forEach(record -> {
            try {
                TuneConsistencyRq tuneConsistencyRq = new TuneConsistencyRq();
                tuneConsistencyRq.setAccessToken(record.getAccessToken());
                tuneConsistencyRq.setAppId(record.getAppId());
                tuneConsistencyRq.setAppName(record.getAppName());
                tuneConsistencyRq.setPipelineId(record.getPipelineId());
                tuneConsistencyRq.setPoolType(
                        StringUtils.equals(record.getBatchPoolStatus(), TunePoolStatus.RUNNABLE.name()) ?
                                PoolType.BATCH : PoolType.EXPERIMENT);
                String config = tuneConsistencyRq.getPoolType() == PoolType.BATCH ?
                        record.getBatchPoolConfig() : record.getExperimentPoolConfig();
                Map<MetaDataType, MetaData> data = JSON.parseObject(config, new TypeReference<Map<MetaDataType, MetaData>>() {
                });
                if (MapUtils.isEmpty(data)) {
                    return;
                }
                tuneConsistencyRq.setMetaDataMap(data);
                tuneDispatchRunner.fire(tuneConsistencyRq);
            } catch (Exception e) {
                //do noting
                log.error("doAsyncWork is error", e);
            }
        });
    }

    private void doChecker() {
        log.info("check状态机驱动");
        //获取waiting的任务
        List<TunePipeline> pipelines = tunePipelineRepository.findPipelineByStatus(Status.RUNNING);
        log.info("check状态机驱动,获取任务数={}", pipelines.size());
        pipelines.parallelStream().forEach(pipeline -> tuneEventWatcher.fire(pipeline));
    }
}