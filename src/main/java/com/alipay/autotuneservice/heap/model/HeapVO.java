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
package com.alipay.autotuneservice.heap.model;

import com.alipay.autotuneservice.heap.model.DominatorTree.Item;
import com.alipay.autotuneservice.heap.model.Model.DirectByteBuffer;
import com.alipay.autotuneservice.heap.model.Model.DuplicatedClass.ClassItem;
import com.alipay.autotuneservice.heap.model.Model.LeakReport;
import com.alipay.autotuneservice.heap.model.Model.UnreachableObject;
import com.alipay.autotuneservice.heap.util.pageutil.PageView;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author huoyuqi
 * @version HeapVO.java, v 0.1 2022年12月07日 8:37 下午 huoyuqi
 */
@Data
@AllArgsConstructor
public class HeapVO {

    /**
     * 详情信息
     */
    private Details details;

    /**
     * 大对象
     */
    private List<BigObject> bigObjects;

    /**
     * 泄露报表
     */
    private LeakReport leakReport;

    /**
     * 根对象
     */
    private List<GCRoot> gcRoots;

    /**
     * 支配树
     */
    private PageView<? extends Item> dominatorTree;

    /**
     * 类视图
     */
    private PageView<ClassVO> classVOPageView;

    /**
     * 不可达类
     */
    private PageView<UnreachableObject.Item> unreachableObject;

    /**
     * 重复类
     */
    private PageView<ClassItem> duplicateClass;

    /**
     * 类加载器
     */
    private PageView<ClassLoaderVO> classLoaderView;

    /**
     * 对外内存
     */
    private PageView<DirectByteBuffer.Item> heapOut;

    /**
     * 系统属性
     */
    private Map<String, String> systemProperties;

    /**
     * 类视图
     */
    private PageView<Histogram.Item> classView;

    /**
     * 诊断报告
     */
    private DiagnosisReport diagnosisReport;

    public HeapVO(Details details, List<BigObject> bigObjects, LeakReport leakReport,
                  List<GCRoot> gcRoots,
                  PageView<? extends Item> dominatorTree, PageView<ClassVO> classVOPageView,
                  PageView<UnreachableObject.Item> unreachableObject,
                  PageView<ClassItem> duplicateClass,
                  PageView<ClassLoaderVO> classLoaderView,
                  PageView<DirectByteBuffer.Item> heapOut, Map<String, String> systemProperties,
                  PageView<Histogram.Item> classView) {
        this.details = details;
        this.bigObjects = bigObjects;
        this.leakReport = leakReport;
        this.gcRoots = gcRoots;
        this.dominatorTree = dominatorTree;
        this.classVOPageView = classVOPageView;
        this.unreachableObject = unreachableObject;
        this.duplicateClass = duplicateClass;
        this.classLoaderView = classLoaderView;
        this.heapOut = heapOut;
        this.systemProperties = systemProperties;
        this.classView = classView;
    }
}