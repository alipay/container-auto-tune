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
package com.alipay.autotuneservice.heap;

import com.alipay.autotuneservice.heap.impl.HeapDumpAnalyzerImpl;
import com.alipay.autotuneservice.heap.impl.ProgressListener;
import com.alipay.autotuneservice.heap.model.BigObject;
import com.alipay.autotuneservice.heap.model.Details;
import com.alipay.autotuneservice.heap.model.DominatorTree;
import com.alipay.autotuneservice.heap.model.GCRoot;
import com.alipay.autotuneservice.heap.model.HeapVO;
import com.alipay.autotuneservice.heap.model.Histogram.Grouping;
import com.alipay.autotuneservice.heap.model.Histogram.Item;
import com.alipay.autotuneservice.heap.model.JavaObject;
import com.alipay.autotuneservice.heap.model.Model;
import com.alipay.autotuneservice.heap.model.Model.DirectByteBuffer;
import com.alipay.autotuneservice.heap.model.Model.DuplicatedClass;
import com.alipay.autotuneservice.heap.model.Model.FieldView;
import com.alipay.autotuneservice.heap.model.Model.InspectorView;
import com.alipay.autotuneservice.heap.model.Model.UnreachableObject;
import com.alipay.autotuneservice.heap.util.SearchType;
import com.alipay.autotuneservice.heap.util.pageutil.PageView;
import org.eclipse.mat.snapshot.ISnapshot;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author t-rex
 * @version HeapDumpAnalyzer.java, v 0.1 2022年01月10日 5:22 下午 t-rex
 */
public interface HeapDumpAnalyzer {
    Details summary();

    ISnapshot getSnapShot(File file);

    /**
     * 获取heapVO
     *
     * @param heapDumpAnalyzer
     * @return
     */
    HeapVO heapAnalysis(HeapDumpAnalyzerImpl heapDumpAnalyzer);

    /**
     * 返回跟对象
     *
     * @param rootTypeIndex
     * @param page
     * @param pageSize
     * @return
     */
    PageView<GCRoot> getClassesOfGCRoot(int rootTypeIndex, int page, int pageSize);

    /**
     * 返回根下面的类对象
     *
     * @param rootTypeIndex
     * @param classIndex
     * @param page
     * @param pageSize
     * @return
     */
    PageView<JavaObject> getObjectsOfGCRoot(int rootTypeIndex, int classIndex, int page, int pageSize);

    /**
     * 返回下面具体的类
     *
     * @param objectId
     * @param page
     * @param pageSize
     * @return
     */
    PageView<JavaObject> getOutboundOfObject(int objectId, int page, int pageSize);

    /**
     * 返回支配树
     *
     * @param groupBy
     * @param sortBy
     * @param ascendingOrder
     * @param searchText
     * @param searchType
     * @param page
     * @param pageSize
     * @return
     */
    PageView<? extends DominatorTree.Item> getRootsOfDominatorTree(DominatorTree.Grouping groupBy, String sortBy,
                                                                   boolean ascendingOrder, String searchText,
                                                                   SearchType searchType, int page,
                                                                   int pageSize);

    /**
     * 返回支配树子树
     *
     * @param groupBy
     * @param sortBy
     * @param ascendingOrder
     * @param parentObjectId
     * @param idPathInResultTree
     * @param page
     * @param pageSize
     * @return
     */
    PageView<? extends DominatorTree.Item> getChildrenOfDominatorTree(DominatorTree.Grouping groupBy,
                                                                      String sortBy, boolean ascendingOrder,
                                                                      int parentObjectId,
                                                                      int[] idPathInResultTree, int page,
                                                                      int pageSize);

    /**
     * 获取类视图
     *
     * @param groupingBy
     * @param ids
     * @param sortBy
     * @param ascendingOrder
     * @param searchText
     * @param searchType
     * @param page
     * @param pageSize
     * @return
     */
    PageView<Item> getHistogram(Grouping groupingBy, int[] ids, String sortBy, boolean ascendingOrder, String searchText,
                                SearchType searchType, int page, int pageSize);

    /**
     * 获取类视图的子孩子
     *
     * @param groupBy
     * @param ids
     * @param sortBy
     * @param ascendingOrder
     * @param parentObjectId
     * @param page
     * @param pageSize
     * @return
     */
    PageView<Model.Histogram.Item> getChildrenOfHistogram(Grouping groupBy, int[] ids, String sortBy,
                                                          boolean ascendingOrder,
                                                          int parentObjectId, int page, int pageSize);

    /**
     * 获取不可达类
     *
     * @param page
     * @param pageSize
     * @return
     */
    PageView<UnreachableObject.Item> getUnreachableObjects(int page, int pageSize);

    /**
     * 获取重复类
     *
     * @param searchText
     * @param searchType
     * @param page
     * @param pageSize
     * @return
     */
    PageView<DuplicatedClass.ClassItem> getDuplicatedClasses(String searchText, SearchType searchType, int page, int pageSize);

    /**
     * 获取重复类子类
     *
     * @param index
     * @param page
     * @param pageSize
     * @return
     */
    PageView<DuplicatedClass.ClassLoaderItem> getClassloadersOfDuplicatedClass(int index, int page, int pageSize);

    /**
     * 类加载器视图
     *
     * @param page
     * @param pageSize
     * @return
     */
    PageView<Model.ClassLoader.Item> getClassLoaders(int page, int pageSize);

    /**
     * 类加载器视图子类视图
     *
     * @param classLoaderId
     * @param page
     * @param pageSize
     * @return
     */
    PageView<Model.ClassLoader.Item> getChildrenOfClassLoader(int classLoaderId, int page, int pageSize);

    /**
     * 堆外内存
     *
     * @param page
     * @param pageSize
     * @return
     */
    PageView<DirectByteBuffer.Item> getDirectByteBuffers(int page, int pageSize);

    /**
     * 获取
     *
     * @param address
     * @return
     */
    Integer mapAddressToId(long address);

    /**
     * 获取object 值
     *
     * @param objectId
     * @return
     */
    String getObjectValue(int objectId);

    /**
     * 获取搜索值
     *
     * @param objectId
     * @return
     */
    InspectorView getInspectorView(int objectId);

    /**
     * 获取属性值
     *
     * @param objectId
     * @param page
     * @param pageSize
     * @return
     */
    PageView<FieldView> getFields(int objectId, int page, int pageSize);

    /**
     * 获取静态属性值
     *
     * @param objectId
     * @param page
     * @param pageSize
     * @return
     */
    PageView<FieldView> getStaticFields(int objectId, int page, int pageSize);

    List<BigObject> getBigObjects();

    interface Provider {
        HeapDumpAnalyzer provide(Path path, Map<String, String> arguments, ProgressListener listener);
    }

}