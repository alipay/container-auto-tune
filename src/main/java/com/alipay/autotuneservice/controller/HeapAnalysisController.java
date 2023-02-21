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
package com.alipay.autotuneservice.controller;

import com.alipay.autotuneservice.heap.HeapAnalysisService;
import com.alipay.autotuneservice.heap.HeapDumpAnalyzer;
import com.alipay.autotuneservice.heap.impl.HeapDumpAnalyzerImpl;
import com.alipay.autotuneservice.heap.model.ClassLoaderVO;
import com.alipay.autotuneservice.heap.model.ClassVO;
import com.alipay.autotuneservice.heap.model.DominatorTree;
import com.alipay.autotuneservice.heap.model.DominatorTree.Item;
import com.alipay.autotuneservice.heap.model.GCRoot;
import com.alipay.autotuneservice.heap.model.HeapVO;
import com.alipay.autotuneservice.heap.model.Histogram;
import com.alipay.autotuneservice.heap.model.Histogram.Grouping;
import com.alipay.autotuneservice.heap.model.JavaObject;
import com.alipay.autotuneservice.heap.model.Model;
import com.alipay.autotuneservice.heap.model.Model.ClassLoader;
import com.alipay.autotuneservice.heap.model.Model.DirectByteBuffer;
import com.alipay.autotuneservice.heap.model.Model.DuplicatedClass;
import com.alipay.autotuneservice.heap.model.Model.FieldView;
import com.alipay.autotuneservice.heap.model.Model.InspectorView;
import com.alipay.autotuneservice.heap.model.Model.UnreachableObject;
import com.alipay.autotuneservice.heap.util.SearchType;
import com.alipay.autotuneservice.heap.util.pageutil.PageView;
import com.alipay.autotuneservice.heap.util.pageutil.PagingRequest;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version HeapAnalysisController.java, v 0.1 2022年12月07日 11:22 上午 huoyuqi
 */

@Slf4j
@RestController
@RequestMapping("/api/heap")
public class HeapAnalysisController {

    @Autowired
    private HeapAnalysisService analyzer;

    @GetMapping("/heapFileAnalysis")
    public ServiceBaseResult<HeapVO> heapFileAnalysis(@RequestParam(value = "fileName") String fileName,
                                                      @RequestParam(value = "s3Key") String s3Key) {
        HeapDumpAnalyzerImpl heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.heapAnalysis(heapDumpAnalyzer));
    }

    @GetMapping("/heapFileAnalysis1")
    public ServiceBaseResult<HeapVO> heapFileAnalysis1(@RequestParam(value = "fileName") String fileName,
                                                       @RequestParam(value = "s3Key") String s3Key) {
        HeapDumpAnalyzerImpl heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer1(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.heapAnalysis(heapDumpAnalyzer));
    }

    /**
     * gcRoot
     */
    @GetMapping("/gcRoot/class")
    public ServiceBaseResult<PageView<GCRoot>> gcClass(@RequestParam(value = "fileName") String fileName,
                                                       @RequestParam(value = "s3Key") String s3Key,
                                                       @RequestParam(value = "rootTypeIndex") Integer rootTypeIndex,
                                                       @RequestParam(value = "page") Integer page,
                                                       @RequestParam(value = "pageSize") Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getClassesOfGCRoot(rootTypeIndex, page, pageSize));
    }

    @GetMapping("/gcRoot/class/object")
    public ServiceBaseResult<PageView<JavaObject>> gcType(@RequestParam(value = "fileName") String fileName,
                                                          @RequestParam(value = "s3Key") String s3Key,
                                                          @RequestParam(value = "rootTypeIndex") Integer rootTypeIndex,
                                                          @RequestParam(value = "classIndex") Integer classIndex,
                                                          @RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getObjectsOfGCRoot(rootTypeIndex, classIndex, page, pageSize));
    }

    @GetMapping("/gcRoot/ObjectId")
    public ServiceBaseResult<PageView<JavaObject>> gcObject(@RequestParam(value = "fileName") String fileName,
                                                            @RequestParam(value = "s3Key") String s3Key,
                                                            @RequestParam(value = "objectId") Integer objectId,
                                                            @RequestParam(value = "page", required = false) Integer page,
                                                            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getOutboundOfObject(objectId, page, pageSize));
    }

    @GetMapping("/dominatorTre")
    public ServiceBaseResult<PageView<? extends Item>> dominatorTre(@RequestParam(value = "fileName") String fileName,
                                                                    @RequestParam(value = "s3Key") String s3Key,
                                                                    @RequestParam(value = "grouping") DominatorTree.Grouping group,
                                                                    @RequestParam(value = "page", required = false) Integer page,
                                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer
                        .getRootsOfDominatorTree(group, "retainedHeap", false, null, SearchType.BY_NAME, page, pageSize));
    }

    @GetMapping("/dominatorTree/children")
    public ServiceBaseResult<PageView<? extends Item>> children(@RequestParam(value = "fileName") String fileName,
                                                                @RequestParam(value = "s3Key") String s3Key,
                                                                @RequestParam(value = "grouping") DominatorTree.Grouping group,
                                                                @RequestParam(value = "parentObjectId") Integer parentObjectId,
                                                                @RequestParam(value = "idPathInResultTree", required = false)
                                                                        int[] idPathInResultTree,
                                                                @RequestParam(value = "page", required = false) Integer page,
                                                                @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer
                        .getChildrenOfDominatorTree(group, "retainedHeap", true, parentObjectId, idPathInResultTree, page, pageSize));
    }

    @GetMapping("/classView1")
    public ServiceBaseResult<PageView<ClassVO>> classView1(@RequestParam(value = "fileName") String fileName,
                                                           @RequestParam(value = "s3Key") String s3Key,
                                                           @RequestParam(value = "grouping") Grouping group,
                                                           @RequestParam(value = "ids", required = false) int[] ids,
                                                           @RequestParam(value = "searchText", required = false) String searchText,
                                                           @RequestParam(value = "searchType", required = false)
                                                                   SearchType searchType,
                                                           @RequestParam(value = "page", required = false) Integer page,
                                                           @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        PageView<Histogram.Item> items = heapDumpAnalyzer.getHistogram(group, ids, "retainedSize", true, searchText, searchType, page,
                pageSize);

        List<ClassVO> classVOS = items.getData().stream().map(item -> {
            InspectorView inspectorView = heapDumpAnalyzer.getInspectorView(item.getObjectId());
            Long address = inspectorView.getObjectAddress();
            return new ClassVO(item, Long.toHexString(address));
        }).collect(Collectors.toList());

        return ServiceBaseResult
                .invoker()
                .makeResult(() -> new PageView<>(new PagingRequest(page, pageSize), items.getTotalSize(), classVOS));
    }

    @GetMapping("/classView")
    public ServiceBaseResult<PageView<Histogram.Item>> classView(@RequestParam(value = "fileName") String fileName,
                                                                 @RequestParam(value = "s3Key") String s3Key,
                                                                 @RequestParam(value = "grouping") Grouping group,
                                                                 @RequestParam(value = "ids", required = false) int[] ids,
                                                                 @RequestParam(value = "searchText", required = false) String searchText,
                                                                 @RequestParam(value = "searchType", required = false)
                                                                         SearchType searchType,
                                                                 @RequestParam(value = "page", required = false) Integer page,
                                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer
                        .getHistogram(group, ids, "retainedSize", true, searchText, searchType, page, pageSize));
    }

    @GetMapping("/classView/children")
    public ServiceBaseResult<PageView<Model.Histogram.Item>> classViewChild(
            @RequestParam(value = "fileName") String fileName,
            @RequestParam(value = "s3Key") String s3Key,
            @RequestParam(value = "grouping") Grouping group,
            @RequestParam(value = "ids", required = false) int[] ids,
            @RequestParam(value = "parentObjectId") Integer parentObjectId,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "pageSize") Integer pageSize) {

        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer
                        .getChildrenOfHistogram(group, ids, "retainedSize", true, parentObjectId, page, pageSize));
    }

    @GetMapping("/UnreachableClasses")
    public ServiceBaseResult<PageView<UnreachableObject.Item>> UnreachableClasses(@RequestParam(value = "fileName") String fileName,
                                                                                  @RequestParam(value = "s3Key") String s3Key,
                                                                                  @RequestParam(value = "page") Integer page,
                                                                                  @RequestParam(value = "pageSize") Integer pageSize) {

        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getUnreachableObjects(page, pageSize));
    }

    @GetMapping("/duplicatedClasses")
    public ServiceBaseResult<PageView<DuplicatedClass.ClassItem>> duplicatedClasses(
            @RequestParam(value = "fileName") String fileName,
            @RequestParam(value = "s3Key") String s3Key,
            @RequestParam(value = "searchType") SearchType searchType,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "pageSize") Integer pageSize,
            @RequestParam(value = "searchText", required = false) String searchText) {

        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getDuplicatedClasses(searchText, searchType, page, pageSize));
    }

    @GetMapping("/duplicatedClasses/classLoaders")
    public ServiceBaseResult<PageView<DuplicatedClass.ClassLoaderItem>> classLoaders(
            @RequestParam(value = "fileName") String fileName,
            @RequestParam(value = "s3Key") String s3Key,
            @RequestParam(value = "index") Integer index,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "pageSize") Integer pageSize) {

        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getClassloadersOfDuplicatedClass(index, page, pageSize));
    }

    @GetMapping("/classesLoaderView")
    public ServiceBaseResult<PageView<ClassLoaderVO>> classesLoaderView(@RequestParam(value = "fileName") String fileName,
                                                                        @RequestParam(value = "s3Key") String s3Key,
                                                                        @RequestParam(value = "page") Integer page,
                                                                        @RequestParam(value = "pageSize") Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        PageView<ClassLoader.Item> classLoaders = heapDumpAnalyzer.getClassLoaders(page, pageSize);
        List<ClassLoaderVO> classLoaderVOS = constructClassLoaderVO(heapDumpAnalyzer, classLoaders);
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> new PageView<>(new PagingRequest(page, pageSize), classLoaders.getTotalSize(), classLoaderVOS));
    }

    @GetMapping("/classesLoaderView/children")
    public ServiceBaseResult<PageView<ClassLoaderVO>> classesLoaderChildView(@RequestParam(value = "fileName") String fileName,
                                                                             @RequestParam(value = "s3Key") String s3Key,
                                                                             @RequestParam(value = "index") Integer index,
                                                                             @RequestParam(value = "page") Integer page,
                                                                             @RequestParam(value = "pageSize") Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        PageView<ClassLoader.Item> classLoaders = heapDumpAnalyzer.getChildrenOfClassLoader(index, page, pageSize);
        List<ClassLoaderVO> classLoaderVOS = constructClassLoaderVO(heapDumpAnalyzer, classLoaders);
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> new PageView<>(new PagingRequest(page, pageSize), classLoaders.getTotalSize(), classLoaderVOS));
    }

    private List<ClassLoaderVO> constructClassLoaderVO(HeapDumpAnalyzer heapDumpAnalyzer, PageView<ClassLoader.Item> classLoaders) {
        return classLoaders.getData().stream().map(c -> {
            InspectorView inspectorView = heapDumpAnalyzer.getInspectorView(c.getObjectId());
            return new ClassLoaderVO(c, inspectorView.getShallowSize(), inspectorView.getRetainedSize());
        }).collect(Collectors.toList());
    }

    @GetMapping("/outsideOfHeap")
    public ServiceBaseResult<PageView<DirectByteBuffer.Item>> outsideOfHeap(@RequestParam(value = "fileName") String fileName,
                                                                            @RequestParam(value = "s3Key") String s3Key,
                                                                            @RequestParam(value = "page") Integer page,
                                                                            @RequestParam(value = "pageSize") Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getDirectByteBuffers(page, pageSize));
    }

    @GetMapping("/search/id")
    public ServiceBaseResult<Integer> searchId(@RequestParam(value = "fileName") String fileName,
                                               @RequestParam(value = "s3Key") String s3Key,
                                               @RequestParam(value = "objectId") Integer objectId) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.mapAddressToId(objectId));
    }

    @GetMapping("/search/value")
    public ServiceBaseResult<String> getValue(@RequestParam(value = "fileName") String fileName,
                                              @RequestParam(value = "s3Key") String s3Key,
                                              @RequestParam(value = "objectId") Integer objectId) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getObjectValue(objectId));
    }

    @GetMapping("/search/objectView")
    public ServiceBaseResult<InspectorView> getObjectView(@RequestParam(value = "fileName") String fileName,
                                                          @RequestParam(value = "s3Key") String s3Key,
                                                          @RequestParam(value = "objectId") Integer objectId) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getInspectorView(objectId));
    }

    @GetMapping("/search/fields")
    public ServiceBaseResult<PageView<FieldView>> getfields(@RequestParam(value = "fileName") String fileName,
                                                            @RequestParam(value = "s3Key") String s3Key,
                                                            @RequestParam(value = "objectId") Integer objectId,
                                                            @RequestParam(value = "page") Integer page,
                                                            @RequestParam(value = "pageSize") Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getFields(objectId, page, pageSize));
    }

    @GetMapping("/search/staticFields")
    public ServiceBaseResult<PageView<FieldView>> getStaticFields(@RequestParam(value = "fileName") String fileName,
                                                                  @RequestParam(value = "s3Key") String s3Key,
                                                                  @RequestParam(value = "objectId") Integer objectId,
                                                                  @RequestParam(value = "page") Integer page,
                                                                  @RequestParam(value = "pageSize") Integer pageSize) {
        HeapDumpAnalyzer heapDumpAnalyzer = analyzer.constructHeapDumpAnalyzer(fileName, s3Key);
        if (null == heapDumpAnalyzer) {
            return null;
        }
        return ServiceBaseResult
                .invoker()
                .makeResult(() -> heapDumpAnalyzer.getStaticFields(objectId, page, pageSize));
    }

}