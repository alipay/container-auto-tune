/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.gc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.controller.model.diagnosis.FileVO;
import com.alipay.autotuneservice.dao.CommandInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.CommandInfoRecord;
import com.alipay.autotuneservice.gc.model.GCBasicVO;
import com.alipay.autotuneservice.gc.model.GCObject;
import com.alipay.autotuneservice.gc.model.GcVO;
import com.alipay.autotuneservice.model.common.CommandStatus;
import com.alipay.autotuneservice.model.rule.RuleAction;
import com.alipay.autotuneservice.service.StorageInfoService;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.DiagnosisLab;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import com.alipay.autotuneservice.util.DateUtils;
import com.alipay.autotuneservice.util.FileUtils;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.eclipse.jifa.gclog.diagnoser.AnalysisConfig;
import org.eclipse.jifa.gclog.diagnoser.GlobalDiagnoser.GlobalAbnormalInfo;
import org.eclipse.jifa.gclog.model.GCModel;
import org.eclipse.jifa.gclog.model.GCTimeStamp;
import org.eclipse.jifa.gclog.model.modeInfo.VmOptions;
import org.eclipse.jifa.gclog.parser.GCLogAnalyzer;
import org.eclipse.jifa.gclog.util.GCLogUtil;
import org.eclipse.jifa.gclog.vo.MemoryStatistics;
import org.eclipse.jifa.gclog.vo.ObjectStatistics;
import org.eclipse.jifa.gclog.vo.PauseStatistics;
import org.eclipse.jifa.gclog.vo.PhaseStatistics;
import org.eclipse.jifa.gclog.vo.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.gc.service.CacheUtils.getOrBuild;
import static org.eclipse.jifa.common.listener.ProgressListener.NoOpProgressListener;

/**
 * @author huoyuqi
 */
@Service
@Slf4j
public class GcLogAnalysisServiceImpl implements GcLogAnalysisService {

    @Autowired
    private StorageInfoService storageInfoService;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private CommandInfoRepository commandInfoRepository;

    @Autowired
    private AsyncTaskExecutor eventExecutor;

    @Override
    public GcVO gcFileAnalysis(Long startTime, Long endTime, String fileName, String s3Key) {
        return constructGcVO(fileName, s3Key, startTime, endTime);
    }

    @Override
    public List<FileVO> getHistory(RuleAction ruleAction, CommandStatus commandStatus, String podName, Long startTime, Long endTime, String appName) {
        List<CommandInfoRecord> records = commandInfoRepository.getByTokenAndName(UserUtil.getAccessToken(), ruleAction, commandStatus,
                podName, startTime, endTime, appName);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.stream().map(r -> {
            Map<String, String> resultMap = JSON.parseObject(r.getResult(), new TypeReference<Map<String, String>>() {});
            return new FileVO(r.getId(), RuleAction.valueOf(r.getRunleAction()), r.getTaskName(), r.getPodName(),
                    DateUtils.asTimestamp(r.getCreatedTime()), CommandStatus.valueOf(r.getStatus()),
                    MapUtils.isNotEmpty(resultMap) ? resultMap.get("fileName") : null,
                    MapUtils.isNotEmpty(resultMap) ? resultMap.get("s3Key") : null);
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean upload(MultipartFile file, String taskName, RuleAction ruleAction, Long startTime, Long endTime, String appName) {
        try {
            Long id = commandInfoRepository.save(taskName, ruleAction, appName);
            eventExecutor.execute(() -> execute(id, file));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean autoUpload(String unionCode, String podName, RuleAction ruleAction, String taskName, Map<String, Object> context, String appName) {
        String sessionId = UUID.randomUUID().toString();
        commandInfoRepository.sendCommand(taskName, podName, unionCode, ruleAction, sessionId, context, appName);
        return true;
    }

    private void execute(Long id, MultipartFile file) {
        try {
            String s3Key = storageInfoService.uploadFileToS3(file.getInputStream(), file.getOriginalFilename());
            Map<String, String> map = new HashMap<>();
            map.put("s3Key", s3Key);
            map.put("fileName", file.getOriginalFilename());
            commandInfoRepository.uStatusAndResult(id, CommandStatus.FINISH, map);
        } catch (Exception e) {
            log.error("execute occurs an error", e);
        }

    }

    @Override
    public Boolean delete(Long id) {
        return commandInfoRepository.deleteById(id);
    }

    @Override
    public GCObject gcObjectSPT(String fileName, String key) {
        try {
            TimeRange timeRange = new TimeRange(0, Integer.MAX_VALUE);
            GCModel gcModel = constructGCModel(fileName, key);
            ObjectStatistics objectStatistics = gcModel.getObjectStatistics(timeRange);
            PauseStatistics pauseStatistics = gcModel.getPauseStatistics(timeRange);
            return new GCObject(objectStatistics.getObjectCreationSpeed(), objectStatistics.getObjectPromotionSpeed(),
                    objectStatistics.getObjectPromotionAvg(), objectStatistics.getObjectPromotionMax(), pauseStatistics.getThroughput());

        } catch (Exception e) {
            log.info("gcObjectSPT occurs an error, fileName is: {}", fileName);
        }
        return null;
    }

    @Override
    public List<GCTimeStamp> getGenFGC(String fileName, String key) {

        try {
            GCModel gcModel = constructGCModel(fileName, key);
            //2.1 构造时间图 heap、duration、 youngGen、 oldGen
            return gcModel.constructFgcTimeGraphData((long) gcModel.getGcModelMetadata().getTimestamp());
        } catch (Exception e) {
            log.info("getYoungGenFGC occurs an error, fileName is: {}", fileName, e);
        }
        return null;
    }

    private GCModel constructGCModel(String fileName, String s3Key) {
        try {
            if (CacheUtils.cache.getIfPresent(fileName + s3Key) != null) {
                return (GCModel) CacheUtils.cache.getIfPresent(fileName + s3Key);
            }
            String filePath = fileName;
            GCModel gcModel = getOrBuild(fileName + s3Key, key -> new GCLogAnalyzer(new File(filePath), NoOpProgressListener).parse());
            return gcModel;
        } catch (Exception e) {
            log.error("constructGCModel occurs an error", e);
            return null;
        }
    }

    private GcVO constructGcVO(String fileName, String s3Key, Long startTime, Long endTime) {
        try {
            //todo 判断是那种类型 1.人工上传 2.查询结果 3.自动上传
            GCModel gcModel = constructGCModel(fileName, s3Key);
            if (gcModel == null) {
                return null;
            }

            Double timestamp = gcModel.getGcModelMetadata().getTimestamp();

            Double start = startTime == null ? 0 : startTime - timestamp;
            Double end = endTime == null ? Integer.MAX_VALUE : endTime - timestamp;
            TimeRange timeRange = new TimeRange(start, end);

            //1.诊断 与问题
            GlobalAbnormalInfo globalAbnormalInfo = gcModel.getGlobalDiagnoseInfo(
                    AnalysisConfig.defaultConfig(gcModel, startTime, endTime));
            //1.1 诊断问题
            DiagnosisReport diagnosisReport = DiagnosisLab.gcLogDiagnosis(gcModel);

            //2.1timeGraphData 时间图
            String[] dataTypes = new String[] {"youngCapacity", "oldCapacity", "Young GC", "Full GC", "metaspaceUsed", "reclamation",
                    "promotion"};
            Map<String, List<Object[]>> timeGraphData = gcModel.getTimeGraphData(dataTypes);

            //2.1 构造时间图 heap、duration、 youngGen、 oldGen metaspaceAllocate
            Map<String, List<Object[]>> constructTimeGraphData = gcModel.constructTimeGraphData();
            timeGraphData.putAll(constructTimeGraphData);

            //2.2 转换成 相应的timeData 并构建时间戳： timestamp + duration
            Map<String, List<GCTimeStamp>> timeData = convertGraphData2GCTimeStamp(timeGraphData, timestamp);

            //3. 暂停信息
            PauseStatistics pauseStatistics = gcModel.getPauseStatistics(timeRange);
            int[] partitions = new int[] {0, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000};
            Map<String, int[]> pauseDistribution = gcModel.getPauseDistribution(timeRange, partitions);

            //4. 堆元空间统计
            MemoryStatistics memoryStatistics = gcModel.getMemoryStatistics(timeRange);

            //5.GC 阶段和原因
            PhaseStatistics phaseStatistics = gcModel.getPhaseStatistics(timeRange);

            //6. 对象统计
            ObjectStatistics objectStatistics = gcModel.getObjectStatistics(timeRange);

            //7. .jvm参数展示
            VmOptions vmOptions = gcModel.getVmOptions();

            //8. basicInfo
            log.info("gcModel metaData is: {}", gcModel.getGcModelMetadata());

            Long timeStamp = (long) gcModel.getGcModelMetadata().getTimestamp();
            GCBasicVO gcBasicVO = new GCBasicVO(fileName, gcModel.getGcModelMetadata().getCollector(), timeStamp, timeStamp,
                    timeStamp + (long) gcModel.getGcModelMetadata().getEndTime(), (long) gcModel.getGcModelMetadata().getEndTime(),
                    startTime, endTime);

            return new GcVO(globalAbnormalInfo, timeGraphData, timeData, pauseStatistics, pauseDistribution, memoryStatistics,
                    phaseStatistics, objectStatistics, vmOptions, gcBasicVO, gcModel.getStorageInfoId(), diagnosisReport);
        } catch (Exception e) {
            log.error("gcAnalysis1 occurs an error", e);
            return null;
        }
    }

    private Map<String, List<GCTimeStamp>> convertGraphData2GCTimeStamp(Map<String, List<Object[]>> constructTimeGraphData, double start) {
        return constructTimeGraphData.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> convertGCTimeStamp(e.getValue(), start)));
    }

    /**
     * 构建GCTimestamp  time before  type
     * time = start + duration
     *
     * @param valueList
     * @param start
     * @return
     */
    private List<GCTimeStamp> convertGCTimeStamp(List<Object[]> valueList, double start) {
        return valueList.stream().map(item -> {
            GCTimeStamp gcTimeStamp = new GCTimeStamp();
            gcTimeStamp.setTime((long) item[0] + (long) start);
            gcTimeStamp.setBefore(item.length >= 2 ? GCLogUtil.convert2Double(item[1]) : 0);
            //1.new object[]{time, value, type}  2.new object[]{time, before, after}  3.new object[]{time, before, after, type}
            if (item.length >= 3) {
                if (item[2] instanceof String) {
                    gcTimeStamp.setType((String) item[2]);
                } else {
                    gcTimeStamp.setAfter(GCLogUtil.convert2Double(item[2]));
                }
            }
            if (item.length == 4) { gcTimeStamp.setType((String) item[3]); }
            return gcTimeStamp;
        }).collect(Collectors.toList());
    }

    private void deleteFile(File file) {
        file.delete();
    }


}