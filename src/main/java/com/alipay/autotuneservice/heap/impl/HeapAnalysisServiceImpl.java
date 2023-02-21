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
package com.alipay.autotuneservice.heap.impl;

import com.alipay.autotuneservice.gc.service.CacheUtils;
import com.alipay.autotuneservice.heap.HeapAnalysisService;
import com.alipay.autotuneservice.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.SnapshotFactory;
import org.eclipse.mat.util.ConsoleProgressListener;
import org.eclipse.mat.util.IProgressListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.alipay.autotuneservice.gc.service.CacheUtils.getOrBuild;
import static jodd.io.FileUtil.deleteFile;

/**
 * @author huoyuqi
 * @version HeapAnalysisServiceImpl.java, v 0.1 2022年12月07日 9:10 下午 huoyuqi
 */
@Service
@Slf4j
public class HeapAnalysisServiceImpl implements HeapAnalysisService {

    @Autowired
    private FileUtils fileUtils;

    @Override
    public HeapDumpAnalyzerImpl constructHeapDumpAnalyzer(String fileName, String s3Key) {
        try {
            if (CacheUtils.cache.getIfPresent(fileName + s3Key) != null) {
                return (HeapDumpAnalyzerImpl) CacheUtils.cache.getIfPresent(fileName + s3Key);
            }
            File file = fileUtils.constructFile(fileName, s3Key);
            if (file == null) {
                throw new RuntimeException("constructFile is null");
            }
            HeapDumpAnalyzerImpl analyzer = getOrBuild(fileName + s3Key, key -> buildHeapDumpAnalyzer(file));
            deleteFile(file);
            return analyzer;
        } catch (Exception e) {
            log.error("constructHeapDumpAnalyzer occurs an error", e);
            return null;
        }
    }

    @Override
    public HeapDumpAnalyzerImpl constructHeapDumpAnalyzer1(String fileName, String s3Key) {

        if (CacheUtils.cache.getIfPresent(fileName + s3Key) != null) {
            return (HeapDumpAnalyzerImpl) CacheUtils.cache.getIfPresent(fileName + s3Key);
        }

        log.info("cache is invalide ------------------------------------  fileName: {}, s3Key: {}", fileName, s3Key);
        HeapDumpAnalyzerImpl analyzer = getOrBuild(fileName + s3Key, key -> buildHeapDumpAnalyzer1());
        return analyzer;
    }

    private HeapDumpAnalyzerImpl buildHeapDumpAnalyzer(File file) {
        IProgressListener listener = new ConsoleProgressListener(System.out);
        Map<String, String> argsMap = new HashMap<>();
        argsMap.put("keep_unreachable_objects", "true");
        argsMap.put("heap_layout", "");
        try {
            ISnapshot iSnapshot = SnapshotFactory.openSnapshot(file, argsMap, listener);
            HeapDumpAnalyzerImpl heapDumpAnalyzer = new HeapDumpAnalyzerImpl(new AnalysisContext(iSnapshot));
            heapDumpAnalyzer.setSnapshot(iSnapshot);
            return heapDumpAnalyzer;
        } catch (Exception e) {
            log.error("buildHeapDumpAnalyzer occurs an error", e);
            return null;
        }
    }

    private HeapDumpAnalyzerImpl buildHeapDumpAnalyzer1() {
        IProgressListener listener = new ConsoleProgressListener(System.out);
        Map<String, String> argsMap = new HashMap<>();
        argsMap.put("keep_unreachable_objects", "true");
        argsMap.put("heap_layout", "");
        try {
            ISnapshot iSnapshot = SnapshotFactory.openSnapshot(
                    new File("/Users/huoyuqi/workspace/tune/tmaster/autotune-service/HEAP_1022781065426489344.hprof"),
                    argsMap,
                    listener);
            HeapDumpAnalyzerImpl heapDumpAnalyzer = new HeapDumpAnalyzerImpl(new AnalysisContext(iSnapshot));
            heapDumpAnalyzer.setSnapshot(iSnapshot);
            return heapDumpAnalyzer;
        } catch (Exception e) {
            log.error("buildHeapDumpAnalyzer occurs an error", e);
            return null;
        }
    }
}