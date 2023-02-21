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

import com.alipay.autotuneservice.heap.HeapDumpAnalyzer;
import com.alipay.autotuneservice.heap.impl.AnalysisContext.ClassLoaderExplorerData;
import com.alipay.autotuneservice.heap.impl.AnalysisContext.DirectByteBufferData;
import com.alipay.autotuneservice.heap.model.BigObject;
import com.alipay.autotuneservice.heap.model.ClassLoaderVO;
import com.alipay.autotuneservice.heap.model.ClassVO;
import com.alipay.autotuneservice.heap.model.Details;
import com.alipay.autotuneservice.heap.model.DominatorTree;
import com.alipay.autotuneservice.heap.model.DominatorTree.Grouping;
import com.alipay.autotuneservice.heap.model.DominatorTree.Item;
import com.alipay.autotuneservice.heap.model.GCRoot;
import com.alipay.autotuneservice.heap.model.HeapVO;
import com.alipay.autotuneservice.heap.model.JavaObject;
import com.alipay.autotuneservice.heap.model.Model;
import com.alipay.autotuneservice.heap.model.Model.ClassLoader;
import com.alipay.autotuneservice.heap.model.Model.DirectByteBuffer;
import com.alipay.autotuneservice.heap.model.Model.DuplicatedClass;
import com.alipay.autotuneservice.heap.model.Model.DuplicatedClass.ClassLoaderItem;
import com.alipay.autotuneservice.heap.model.Model.FieldView;
import com.alipay.autotuneservice.heap.model.Model.InspectorView;
import com.alipay.autotuneservice.heap.model.Model.LeakReport;
import com.alipay.autotuneservice.heap.model.Model.UnreachableObject;
import com.alipay.autotuneservice.heap.util.Cacheable;
import com.alipay.autotuneservice.heap.util.ExoticTreeFinder;
import com.alipay.autotuneservice.heap.util.Helper;
import com.alipay.autotuneservice.heap.util.ReflectionUtil;
import com.alipay.autotuneservice.heap.util.SearchType;
import com.alipay.autotuneservice.heap.util.pageutil.PageView;
import com.alipay.autotuneservice.heap.util.pageutil.PageViewBuilder;
import com.alipay.autotuneservice.heap.util.pageutil.PagingRequest;
import com.alipay.autotuneservice.heap.util.virtual.VirtualClassItem;
import com.alipay.autotuneservice.heap.util.virtual.VirtualClassLoaderItem;
import com.alipay.autotuneservice.heap.util.virtual.VirtualDefaultItem;
import com.alipay.autotuneservice.heap.util.virtual.VirtualPackageItem;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.DiagnosisLab;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jifa.common.Constant;
import org.eclipse.jifa.common.JifaException;
import org.eclipse.jifa.common.util.EscapeUtil;
import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.internal.snapshot.SnapshotQueryContext;
import org.eclipse.mat.parser.model.XClassHistogramRecord;
import org.eclipse.mat.parser.model.XClassLoaderHistogramRecord;
import org.eclipse.mat.query.Bytes;
import org.eclipse.mat.query.Column;
import org.eclipse.mat.query.IDecorator;
import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.IResultPie;
import org.eclipse.mat.query.IResultTable;
import org.eclipse.mat.query.IResultTree;
import org.eclipse.mat.query.refined.RefinedResultBuilder;
import org.eclipse.mat.query.refined.RefinedTable;
import org.eclipse.mat.query.refined.RefinedTree;
import org.eclipse.mat.query.results.CompositeResult;
import org.eclipse.mat.query.results.TextResult;
import org.eclipse.mat.report.QuerySpec;
import org.eclipse.mat.report.SectionSpec;
import org.eclipse.mat.report.Spec;
import org.eclipse.mat.snapshot.ClassHistogramRecord;
import org.eclipse.mat.snapshot.Histogram;
import org.eclipse.mat.snapshot.HistogramRecord;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.SnapshotFactory;
import org.eclipse.mat.snapshot.SnapshotInfo;
import org.eclipse.mat.snapshot.UnreachableObjectsHistogram;
import org.eclipse.mat.snapshot.model.Field;
import org.eclipse.mat.snapshot.model.GCRootInfo;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IInstance;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.snapshot.model.IObjectArray;
import org.eclipse.mat.snapshot.model.IPrimitiveArray;
import org.eclipse.mat.snapshot.model.ObjectReference;
import org.eclipse.mat.snapshot.query.SnapshotQuery;
import org.eclipse.mat.util.ConsoleProgressListener;
import org.eclipse.mat.util.IProgressListener;

import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.heap.model.JavaObject.typeOf;
import static com.alipay.autotuneservice.heap.util.SearchPredicate.createPredicate;

/**
 * @author t-rex
 * @version HeapDumpAnalyzerImpl.java, v 0.1 2022年01月13日 4:35 下午 t-rex
 */
@Slf4j
public class HeapDumpAnalyzerImpl implements HeapDumpAnalyzer {

    static final Provider        PROVIDER = new ProviderImpl();
    static       SnapshotInfo    snapshotInfo;
    private      AnalysisContext context;
    private      ISnapshot       iSnapshot;

    public ISnapshot getSnapshot() {
        return iSnapshot;
    }

    public void setSnapshot(ISnapshot iSnapshot) {
        this.iSnapshot = iSnapshot;
    }

    public HeapDumpAnalyzerImpl(AnalysisContext context) {this.context = context;}

    private static <V> V $(RV<V> rv) {
        try {
            return rv.run();
        } catch (Throwable t) {
            throw new AnalysisException(t);
        }
    }

    @Override
    public HeapVO heapAnalysis(HeapDumpAnalyzerImpl heapDumpAnalyzer) {

        try {
            ISnapshot iSnapshot = heapDumpAnalyzer.getSnapshot();
            //1.1概况信息
            snapshotInfo = iSnapshot.getSnapshotInfo();
            Details details = heapDumpAnalyzer.summary();
            //1.2 环形图 大对象
            Collection<IClass> classes = iSnapshot.getClasses();
            List<Long> retainedHeapSize = new ArrayList<>();
            for (IClass c : classes) {
                long retainedHeapSize1 = c.getRetainedHeapSize();
                retainedHeapSize.add(retainedHeapSize1);
            }
            List<BigObject> bigObjects = heapDumpAnalyzer.getBigObjects();

            //2.泄露报表
            LeakReport leakReport = heapDumpAnalyzer.getLeakReport();

            //3 根对象
            List<GCRoot> gcRoots = heapDumpAnalyzer.getGCRoots();

            //4.支配树
            PageView<? extends Item> dominatorTree = heapDumpAnalyzer.getRootsOfDominatorTree(Grouping.NONE, "retainedHeap", false, "",
                    SearchType.BY_NAME, 1, 25);

            //5.类视图
            PageView<ClassVO> classVOPageView = null;
            PageView<com.alipay.autotuneservice.heap.model.Histogram.Item> classView = null;
            try {
                classView = heapDumpAnalyzer.getHistogram(
                        com.alipay.autotuneservice.heap.model.Histogram.Grouping.BY_CLASS, null, "retainedSize", true,
                        "", SearchType.BY_NAME, 1, 25);

                List<ClassVO> classVOS = classView.getData().stream().map(item -> {
                    InspectorView inspectorView = heapDumpAnalyzer.getInspectorView(item.getObjectId());
                    Long address = inspectorView.getObjectAddress();
                    return new ClassVO(item, Long.toHexString(address));
                }).collect(Collectors.toList());

                classVOPageView = new PageView<>(new PagingRequest(classView.getPage(), classView.getPageSize()),
                        classView.getTotalSize(), classVOS);
            } catch (Exception e) {
                log.error("HeapDumpAnalyzerImpl construct classVO occurs an error", e);
            }

            //6.重复类
            PageView<DuplicatedClass.ClassItem> duplicateClass = getDuplicatedClasses("", SearchType.BY_NAME, 1, 25);

            //7.类加载器视图
            PageView<ClassLoaderVO> classLoaderView = null;
            try {
                PageView<ClassLoader.Item> classLoaders = heapDumpAnalyzer.getClassLoaders(1, 25);
                List<ClassLoaderVO> classLoaderList = classLoaders.getData().stream().map(c -> {
                    InspectorView inspectorView = heapDumpAnalyzer.getInspectorView(c.getObjectId());
                    return new ClassLoaderVO(c, inspectorView.getShallowSize(), inspectorView.getRetainedSize());
                }).collect(Collectors.toList());

                classLoaderView = new PageView<>(new PagingRequest(classLoaders.getPage(), classLoaders.getPageSize()),
                        classLoaders.getTotalSize(), classLoaderList);

            } catch (Exception e) {
                log.error("HeapDumpAnalyzerImpl construct classLoaderVO occurs an error", e);
            }

            //8.堆外内存
            PageView<DirectByteBuffer.Item> heapOut = getDirectByteBuffers(1, 25);

            //9.系统属性
            Map<String, String> systemProperties = heapDumpAnalyzer.getSystemProperties();

            leakReport = convertToLeakReport(leakReport);

            HeapVO heapVO = new HeapVO(details, bigObjects, leakReport, gcRoots, dominatorTree, classVOPageView, null, duplicateClass,
                    classLoaderView, heapOut, systemProperties, classView);

            //10 诊断问题
            DiagnosisReport diagnosisReport = null;
            try {
                diagnosisReport = DiagnosisLab.memLogDiagnosis(heapVO);
            } catch (Exception e) {
                log.error("HeapDumpAnalyzerImpl diagnosisiReport classLoaderVO occurs an error", e);
            }
            heapVO.setDiagnosisReport(diagnosisReport);

            return heapVO;

        } catch (Exception e) {
            log.error("heapAnalysis occurs an error", e);
            return null;
        }
    }

    private static LeakReport convertToLeakReport(LeakReport leakReport) {
        leakReport.setRecords(leakReport.getRecords().stream().map(item -> {
            if (item != null) {
                item.setDesc(item.getDesc().replaceAll("可疑点", "Problem Suspect").replace("一个实例占用了", "occupies").replace("内存被由类加载器",
                        "bytes. The memory is accumulated in one instance of").replace("加载的一个实例", "loaded by").replace("所累积.",
                        "which occupies").replace("被类加载器", "").replace("类加载器", "One instance of").replace("加载的", "loaded by").replace("字节.",
                        "b").replace("个", "").replace("实例占了", "occupy").replace("关键字", "Keywords"));
                return item;
            }
            return null;
        }).collect(Collectors.toList()));

        return leakReport;
    }

    private void $(R e) {
        $(() -> {
            e.run();
            return null;
        });
    }

    interface R {
        void run() throws Exception;
    }

    interface RV<V> {
        V run() throws Exception;
    }

    /**
     * 1.大对象概览
     *
     * @return
     */
    @Override
    public List<BigObject> getBigObjects() {
        return $(() -> {
            IResultPie result = queryByCommand(context, "pie_biggest_objects");

            List<? extends IResultPie.Slice> slices = result.getSlices();
            return slices
                    .stream()
                    .map(slice -> new BigObject(slice.getLabel(), slice.getContext() != null
                            ? slice.getContext().getObjectId() :
                            Helper.ILLEGAL_OBJECT_ID,
                            slice.getValue(), slice.getDescription()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * 2.获取 GC 根对象
     *
     * @return
     */
    public List<GCRoot> getGCRoots() {
        return $(() -> {
            IResultTree tree = queryByCommand(context, "gc_roots");
            return tree.getElements().stream().map(e -> {
                GCRoot item = new GCRoot();
                item.setClassName((String) tree.getColumnValue(e, 0));
                item.setObjects((Integer) tree.getColumnValue(e, 1));
                return item;
            }).collect(Collectors.toList());
        });
    }

    /**
     * 2.1 获取 GC 根对象的子 class
     *
     * @param
     * @return
     */
    @Override
    public PageView<GCRoot> getClassesOfGCRoot(int rootTypeIndex, int page, int pageSize) {
        return $(() -> {
            IResultTree tree = queryByCommand(context, "gc_roots");
            Object root = tree.getElements().get(rootTypeIndex);
            List<?> classes = tree.getChildren(root);
            return PageViewBuilder.build(classes, new PagingRequest(page, pageSize), clazz -> {
                GCRoot item = new GCRoot();
                item.setClassName((String) tree.getColumnValue(clazz, 0));
                item.setObjects((Integer) tree.getColumnValue(clazz, 1));
                item.setObjectId(tree.getContext(clazz).getObjectId());
                return item;
            });
        });
    }

    /**
     * 2.1.1 GCROOTS --> Classes ---> objects
     *
     * @param
     * @return
     */
    @Override
    public PageView<JavaObject> getObjectsOfGCRoot(int rootTypeIndex, int classIndex, int page, int pageSize) {
        return $(() -> {
            IResultTree tree = queryByCommand(context, "gc_roots");
            Object root = tree.getElements().get(rootTypeIndex);
            List<?> classes = tree.getChildren(root);
            Object clazz = classes.get(classIndex);
            List<?> objects = tree.getChildren(clazz);

            return PageViewBuilder.build(objects, new PagingRequest(page, pageSize),
                    o -> $(() -> {
                                JavaObject ho = new JavaObject();
                                int objectId = tree.getContext(o).getObjectId();
                                IObject object = context.snapshot.getObject(objectId);
                                ho.setLabel(object.getDisplayName());
                                ho.setObjectId(objectId);
                                ho.setShallowSize(object.getUsedHeapSize());
                                ho.setRetainedSize(object.getRetainedHeapSize());
                                ho.setObjectType(typeOf(object));
                                ho.setGCRoot(context.snapshot.isGCRoot(objectId));
                                ho.setSuffix(Helper.suffix(object.getGCRootInfo()));
                                ho.setHasOutbound(true);
                                ho.setHasInbound(true);
                                return ho;
                            }
                    ));
        });
    }

    @Override
    public PageView<JavaObject> getOutboundOfObject(int objectId, int page, int pageSize) {
        return $(() -> queryIOBoundsOfObject(context, objectId, page, pageSize, true));
    }

    private PageView<JavaObject> queryIOBoundsOfObject(AnalysisContext context, int objectId, int page, int pageSize, boolean outbound)
            throws SnapshotException {
        ISnapshot snapshot = context.snapshot;
        int[] ids = outbound ? snapshot.getOutboundReferentIds(objectId) : snapshot.getInboundRefererIds(objectId);

        return PageViewBuilder.build(ids, new PagingRequest(page, pageSize),
                id -> {
                    try {
                        JavaObject o = new JavaObject();
                        IObject object = context.snapshot.getObject(id);
                        o.setObjectId(id);
                        o.setLabel(object.getDisplayName());
                        o.setShallowSize(object.getUsedHeapSize());
                        o.setRetainedSize(object.getRetainedHeapSize());
                        o.setObjectType(typeOf(object));
                        o.setGCRoot(snapshot.isGCRoot(id));
                        o.setHasOutbound(true);
                        o.setHasInbound(true);
                        o.setPrefix(Helper.prefix(snapshot, outbound ? objectId : id, outbound ? id : objectId));
                        o.setSuffix(Helper.suffix(snapshot, id));
                        return o;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * 3.支配树
     *
     * @param groupBy        枚举
     * @param sortBy         排序 ex: Retained Heap
     * @param ascendingOrder 布尔
     * @param searchText     关键字文本
     * @param searchType     搜索种类
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageView<? extends Item> getRootsOfDominatorTree(Grouping groupBy, String sortBy,
                                                            boolean ascendingOrder, String searchText,
                                                            SearchType searchType, int page,
                                                            int pageSize) {
        return $(() -> {
            Map<String, Object> args = new HashMap<>();
            IResultTree tree = queryByCommand(context, "dominator_tree -groupBy " + groupBy.name(), args);
            switch (groupBy) {
                case NONE:
                    return buildDefaultItems(context.snapshot, tree, tree.getElements(), ascendingOrder, sortBy,
                            searchText, searchType, new PagingRequest(page, pageSize));
                case BY_CLASS:
                    return buildClassItems(context.snapshot, tree, tree.getElements(), ascendingOrder, sortBy,
                            searchText, searchType, new PagingRequest(page, pageSize));

                case BY_CLASSLOADER:
                    return buildClassLoaderItems(context.snapshot, tree, tree.getElements(), ascendingOrder, sortBy,
                            searchText, searchType, new PagingRequest(page, pageSize));
                case BY_PACKAGE:
                    return buildPackageItems(context.snapshot, tree, tree.getElements(), ascendingOrder, sortBy,
                            searchText, searchType, new PagingRequest(page, pageSize));
                default:
                    throw new AnalysisException("Should not reach here");
            }
        });
    }

    /**
     * 3.1 支配子树
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
    @Override
    public PageView<? extends Item> getChildrenOfDominatorTree(Grouping groupBy,
                                                               String sortBy, boolean ascendingOrder,
                                                               int parentObjectId,
                                                               int[] idPathInResultTree, int page,
                                                               int pageSize) {
        return $(() -> {
            Map<String, Object> args = new HashMap<>();
            IResultTree tree = queryByCommand(context, "dominator_tree -groupBy " + groupBy.name(), args);
            switch (groupBy) {
                case NONE:
                    Object parent = Helper.fetchObjectInResultTree(tree, idPathInResultTree);
                    return
                            buildDefaultItems(context.snapshot, tree, tree.getChildren(parent), ascendingOrder, sortBy,
                                    null, null, new PagingRequest(page, pageSize));
                case BY_CLASS:
                    Object object = Helper.fetchObjectInResultTree(tree, idPathInResultTree);
                    List<?> elements = object == null ? Collections.emptyList() : tree.getChildren(object);
                    return buildClassItems(context.snapshot, tree, elements, ascendingOrder, sortBy, null, null, new PagingRequest(page
                            , pageSize));
                case BY_CLASSLOADER:
                    List<?> children = new ExoticTreeFinder(tree)
                            .setGetChildrenCallback(tree::getChildren)
                            .setPredicate((theTree, theNode) -> theTree.getContext(theNode).getObjectId())
                            .findChildrenOf(parentObjectId);

                    if (children != null) {
                        return buildClassLoaderItems(context.snapshot, tree, children, ascendingOrder, sortBy, null,
                                null, new PagingRequest(page, pageSize));
                    } else {
                        return PageView.empty();
                    }
                case BY_PACKAGE:
                    Object targetParentNode = new ExoticTreeFinder(tree)
                            .setGetChildrenCallback(node -> {
                                Map<String, ?> subPackages = ReflectionUtil.getFieldValueOrNull(node, "subPackages");
                                if (subPackages != null) {
                                    return new ArrayList<>(subPackages.values());
                                } else {
                                    return null;
                                }
                            })
                            .setPredicate((theTree, theNode) -> {
                                try {
                                    java.lang.reflect.Field
                                            field =
                                            theNode.getClass().getSuperclass().getSuperclass().getDeclaredField("label");
                                    field.setAccessible(true);
                                    String labelName = (String) field.get(theNode);
                                    return labelName.hashCode();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                return null;
                            })
                            .findTargetParentNode(parentObjectId);
                    if (targetParentNode != null) {
                        Map<String, ?> packageMap = ReflectionUtil.getFieldValueOrNull(targetParentNode, "subPackages");
                        List<?> elems = new ArrayList<>();
                        if (packageMap != null) {
                            if (packageMap.size() == 0) {
                                elems = ReflectionUtil.getFieldValueOrNull(targetParentNode, "classes");
                            } else {
                                elems = new ArrayList<>(packageMap.values());
                            }
                        }
                        if (elems != null) {
                            return
                                    buildPackageItems(context.snapshot, tree, elems, ascendingOrder, sortBy, null, null,
                                            new PagingRequest(page, pageSize));
                        } else {
                            return PageView.empty();
                        }
                    } else {
                        return PageView.empty();
                    }
                default:
                    throw new AnalysisException("Should not reach here");
            }
        });
    }

    /**
     * 4. 类视图
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
    @Override
    public PageView<com.alipay.autotuneservice.heap.model.Histogram.Item> getHistogram(
            com.alipay.autotuneservice.heap.model.Histogram.Grouping groupingBy,
            int[] ids, String sortBy, boolean ascendingOrder,
            String searchText, SearchType searchType,
            int page, int pageSize) {
        return $(() -> {
            Map<String, Object> args = new HashMap<>();
            if (ids != null) {
                args.put("objects", Helper.buildHeapObjectArgument(ids));
            }
            IResult result = queryByCommand(context, "histogram -groupBy " + groupingBy.name(), args);
            switch (groupingBy) {
                case BY_CLASS:

                    Histogram h = (Histogram) result;
                    List<ClassHistogramRecord> records =
                            (List<ClassHistogramRecord>) h.getClassHistogramRecords();
                    return PageViewBuilder.<ClassHistogramRecord, com.alipay.autotuneservice.heap.model.Histogram.Item>fromList(records)
                            .beforeMap(record -> $(() -> record
                                    .calculateRetainedSize(context.snapshot, true, true, Helper.VOID_LISTENER)))
                            .paging(new PagingRequest(page, pageSize))
                            .map(record -> new com.alipay.autotuneservice.heap.model.Histogram.Item(record.getClassId(), record.getLabel(),
                                    com.alipay.autotuneservice.heap.model.Histogram.ItemType.CLASS_LOADER,
                                    record.getNumberOfObjects(),
                                    record.getUsedHeapSize(),
                                    record.getRetainedHeapSize(),
                                    0,
                                    0,
                                    0,
                                    0
                            ))
                            .sort(com.alipay.autotuneservice.heap.model.Histogram.Item.sortBy(sortBy, ascendingOrder))
                            .filter(createPredicate(searchText, searchType))
                            .done();
                case BY_CLASSLOADER:
                    Histogram.ClassLoaderTree ct = (Histogram.ClassLoaderTree) result;
                    @SuppressWarnings("unchecked")
                    PageViewBuilder<? extends XClassLoaderHistogramRecord, com.alipay.autotuneservice.heap.model.Histogram.Item> builder =
                            PageViewBuilder.fromList((List<? extends XClassLoaderHistogramRecord>) ct.getElements());
                    return builder
                            .beforeMap(record -> $(() -> record.calculateRetainedSize(context.snapshot, true, true,
                                    Helper.VOID_LISTENER)))
                            .paging(new PagingRequest(page, pageSize))
                            .map(record ->
                                    new com.alipay.autotuneservice.heap.model.Histogram.Item(record.getClassLoaderId(), record.getLabel(),
                                            com.alipay.autotuneservice.heap.model.Histogram.ItemType.CLASS_LOADER,
                                            record.getNumberOfObjects(),
                                            record.getUsedHeapSize(),
                                            record.getRetainedHeapSize(), 0, 0, 0, 0
                                    ))
                            .sort(com.alipay.autotuneservice.heap.model.Histogram.Item.sortBy(sortBy, ascendingOrder))
                            .filter(createPredicate(searchText, searchType))
                            .done();
                case BY_SUPERCLASS:
                    Histogram.SuperclassTree st = (Histogram.SuperclassTree) result;
                    //noinspection unchecked
                    return PageViewBuilder.<HistogramRecord, com.alipay.autotuneservice.heap.model.Histogram.Item>fromList(
                            (List<HistogramRecord>) st.getElements())
                            .paging(new PagingRequest(page, pageSize))
                            .map(e -> {
                                com.alipay.autotuneservice.heap.model.Histogram.Item item
                                        = new com.alipay.autotuneservice.heap.model.Histogram.Item();
                                int objectId = st.getContext(e).getObjectId();
                                item.setType(com.alipay.autotuneservice.heap.model.Histogram.ItemType.SUPER_CLASS);
                                item.setObjectId(objectId);
                                item.setLabel((String) st.getColumnValue(e, 0));
                                item.setNumberOfObjects((Long) st.getColumnValue(e, 1));
                                item.setShallowSize(((Bytes) st.getColumnValue(e, 2)).getValue());
                                return item;
                            })
                            .sort(com.alipay.autotuneservice.heap.model.Histogram.Item.sortBy(sortBy, ascendingOrder))
                            .filter(createPredicate(searchText, searchType))
                            .done();
                case BY_PACKAGE:
                    Histogram.PackageTree pt = (Histogram.PackageTree) result;
                    //noinspection unchecked
                    return
                            PageViewBuilder.<HistogramRecord, com.alipay.autotuneservice.heap.model.Histogram.Item>fromList(
                                    (List<HistogramRecord>) pt.getElements())
                                    .paging(new PagingRequest(page, pageSize))
                                    .map(e -> {

                                        com.alipay.autotuneservice.heap.model.Histogram.Item item
                                                = new com.alipay.autotuneservice.heap.model.Histogram.Item();
                                        String label = (String) pt.getColumnValue(e, 0);
                                        item.setLabel(label);

                                        if (e instanceof XClassHistogramRecord) {
                                            int objectId = pt.getContext(e).getObjectId();
                                            item.setObjectId(objectId);
                                            item.setType(com.alipay.autotuneservice.heap.model.Histogram.ItemType.CLASS);
                                        } else {
                                            item.setObjectId(label.hashCode());
                                            item.setType(com.alipay.autotuneservice.heap.model.Histogram.ItemType.PACKAGE);
                                        }

                                        if (label.matches("^int(\\[\\])*") || label.matches("^char(\\[\\])*") ||
                                                label.matches("^byte(\\[\\])*") || label.matches("^short(\\[\\])*") ||
                                                label.matches("^boolean(\\[\\])*") ||
                                                label.matches("^double(\\[\\])*") ||
                                                label.matches("^float(\\[\\])*") || label.matches("^long(\\[\\])*") ||
                                                label.matches("^void(\\[\\])*")) {
                                            item.setType(com.alipay.autotuneservice.heap.model.Histogram.ItemType.CLASS);
                                        }
                                        item.setNumberOfObjects((Long) pt.getColumnValue(e, 1));
                                        item.setShallowSize(((Bytes) pt.getColumnValue(e, 2)).getValue());

                                        return item;
                                    })
                                    .sort(com.alipay.autotuneservice.heap.model.Histogram.Item.sortBy(sortBy, ascendingOrder))
                                    .filter(createPredicate(searchText, searchType))
                                    .done();
                default:
                    throw new AnalysisException("Should not reach here");
            }

        });
    }

    /**
     * 类子视图
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
    @Override
    public PageView<Model.Histogram.Item> getChildrenOfHistogram(com.alipay.autotuneservice.heap.model.Histogram.Grouping groupBy,
                                                                 int[] ids,
                                                                 String sortBy, boolean ascendingOrder,
                                                                 int parentObjectId, int page, int pageSize) {
        return $(() -> {
            Map<String, Object> args = new HashMap<>();
            if (ids != null) {
                args.put("objects", Helper.buildHeapObjectArgument(ids));
            }
            IResult result = queryByCommand(context, "histogram -groupBy " + groupBy.name(), args);
            switch (groupBy) {
                case BY_CLASS: {
                    throw new AnalysisException("Should not reach here");
                }
                case BY_CLASSLOADER: {
                    Histogram.ClassLoaderTree tree = (Histogram.ClassLoaderTree) result;
                    List<?> elems = tree.getElements();
                    List<? extends ClassHistogramRecord> children = null;
                    for (Object elem : elems) {
                        if (elem instanceof XClassLoaderHistogramRecord) {
                            if (((XClassLoaderHistogramRecord) elem).getClassLoaderId() == parentObjectId) {
                                children = (List<? extends ClassHistogramRecord>) ((XClassLoaderHistogramRecord) elem)
                                        .getClassHistogramRecords();
                                break;
                            }
                        }
                    }
                    if (children != null) {
                        //noinspection unchecked
                        return PageViewBuilder.<ClassHistogramRecord, Model.Histogram.Item>fromList(
                                (List<ClassHistogramRecord>) children)
                                .beforeMap(record -> $(() -> record
                                        .calculateRetainedSize(context.snapshot, true, true, Helper.VOID_LISTENER)))
                                .paging(new PagingRequest(page, pageSize))
                                .map(record -> new Model.Histogram.Item(record.getClassId(), record.getLabel(),
                                        Model.Histogram.ItemType.CLASS_LOADER,
                                        record.getNumberOfObjects(),
                                        record.getUsedHeapSize(),
                                        record.getRetainedHeapSize(), 0, 0, 0, 0))
                                .sort(Model.Histogram.Item.sortBy(sortBy, ascendingOrder))
                                .done();
                    } else {
                        return PageView.empty();
                    }
                }
                case BY_SUPERCLASS: {
                    Histogram.SuperclassTree st = (Histogram.SuperclassTree) result;
                    List<?> children = new ExoticTreeFinder(st)
                            .setGetChildrenCallback(node -> {
                                Map<String, ?> subClasses = org.eclipse.jifa.common.util.ReflectionUtil.getFieldValueOrNull(node,
                                        "subClasses");
                                if (subClasses != null) {
                                    return new ArrayList<>(subClasses.values());
                                }
                                return null;
                            })
                            .setPredicate((theTree, theNode) -> theTree.getContext(theNode).getObjectId())
                            .findChildrenOf(parentObjectId);

                    if (children != null) {
                        //noinspection unchecked
                        return PageViewBuilder.<HistogramRecord, Model.Histogram.Item>fromList(
                                (List<HistogramRecord>) children)
                                .paging(new PagingRequest(page, pageSize))
                                .map(e -> {
                                    Model.Histogram.Item item = new Model.Histogram.Item();
                                    int objectId = st.getContext(e).getObjectId();
                                    item.setType(Model.Histogram.ItemType.SUPER_CLASS);
                                    item.setObjectId(objectId);
                                    item.setLabel((String) st.getColumnValue(e, 0));
                                    item.setNumberOfObjects((Long) st.getColumnValue(e, 1));
                                    item.setShallowSize(((Bytes) st.getColumnValue(e, 2)).getValue());
                                    return item;
                                })
                                .sort(Model.Histogram.Item.sortBy(sortBy, ascendingOrder))
                                .done();
                    } else {
                        return PageView.empty();
                    }
                }
                case BY_PACKAGE: {
                    Histogram.PackageTree pt = (Histogram.PackageTree) result;
                    Object targetParentNode = new ExoticTreeFinder(pt)
                            .setGetChildrenCallback(node -> {
                                Map<String, ?> subPackages = org.eclipse.jifa.common.util.ReflectionUtil.getFieldValueOrNull(node,
                                        "subPackages");
                                if (subPackages != null) {
                                    return new ArrayList<>(subPackages.values());
                                } else {
                                    return null;
                                }
                            })
                            .setPredicate((theTree, theNode) -> {
                                if (!(theNode instanceof XClassHistogramRecord)) {
                                    try {
                                        java.lang.reflect.Field
                                                field = theNode.getClass().getSuperclass().getDeclaredField("label");
                                        field.setAccessible(true);
                                        String labelName = (String) field.get(theNode);
                                        return labelName.hashCode();
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                }
                                return null;
                            })
                            .findTargetParentNode(parentObjectId);
                    if (targetParentNode != null) {
                        Map<String, ?> packageMap = org.eclipse.jifa.common.util.ReflectionUtil.getFieldValueOrNull(targetParentNode,
                                "subPackages");
                        List<?> elems = new ArrayList<>();
                        if (packageMap != null) {
                            if (packageMap.size() == 0) {
                                elems = org.eclipse.jifa.common.util.ReflectionUtil.getFieldValueOrNull(targetParentNode, "classes");
                            } else {
                                elems = new ArrayList<>(packageMap.values());
                            }
                        }
                        //noinspection unchecked
                        return PageViewBuilder.<HistogramRecord, Model.Histogram.Item>fromList(
                                (List<HistogramRecord>) elems)
                                .paging(new PagingRequest(page, pageSize))
                                .map(e -> {

                                    Model.Histogram.Item item = new Model.Histogram.Item();
                                    String label = (String) pt.getColumnValue(e, 0);
                                    item.setLabel(label);

                                    if (e instanceof XClassHistogramRecord) {
                                        int objectId = pt.getContext(e).getObjectId();
                                        item.setObjectId(objectId);
                                        item.setType(Model.Histogram.ItemType.CLASS);
                                    } else {
                                        item.setObjectId(label.hashCode());
                                        item.setType(Model.Histogram.ItemType.PACKAGE);
                                    }

                                    if (label.matches("^int(\\[\\])*") || label.matches("^char(\\[\\])*") ||
                                            label.matches("^byte(\\[\\])*") || label.matches("^short(\\[\\])*") ||
                                            label.matches("^boolean(\\[\\])*") ||
                                            label.matches("^double(\\[\\])*") ||
                                            label.matches("^float(\\[\\])*") || label.matches("^long(\\[\\])*") ||
                                            label.matches("^void(\\[\\])*")) {
                                        item.setType(Model.Histogram.ItemType.CLASS);
                                    }
                                    item.setNumberOfObjects((Long) pt.getColumnValue(e, 1));
                                    item.setShallowSize(((Bytes) pt.getColumnValue(e, 2)).getValue());

                                    return item;
                                })
                                .sort(Model.Histogram.Item.sortBy(sortBy, ascendingOrder))
                                .done();

                    } else {
                        return PageView.empty();
                    }
                }
                default: {
                    throw new AnalysisException("Should not reach here");
                }
            }
        });
    }

    /**
     * 不可达类
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageView<UnreachableObject.Item> getUnreachableObjects(int page, int pageSize) {
        return $(() -> {
            UnreachableObjectsHistogram histogram =
                    (UnreachableObjectsHistogram) context.snapshot.getSnapshotInfo().getProperty(
                            UnreachableObjectsHistogram.class.getName());

            if (histogram == null) {
                return null;
            }
            List<?> total = new ArrayList<>(histogram.getRecords());
            total.sort((Comparator<Object>) (o1, o2) -> {
                long v2 = ((Bytes) histogram.getColumnValue(o2, 2)).getValue();
                long v1 = ((Bytes) histogram.getColumnValue(o1, 2)).getValue();
                return Long.compare(v2, v1);
            });

            return PageViewBuilder.build(total, new PagingRequest(page, pageSize), record -> {
                UnreachableObject.Item r = new UnreachableObject.Item();
                r.setClassName((String) histogram.getColumnValue(record, 0));
                r.setObjectId(Helper.fetchObjectId(histogram.getContext(record)));
                r.setObjects((Integer) histogram.getColumnValue(record, 1));
                r.setShallowSize(((Bytes) histogram.getColumnValue(record, 2)).getValue());
                return r;
            });
        });
    }

    /**
     * 重复类
     *
     * @param searchText
     * @param searchType
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageView<DuplicatedClass.ClassItem> getDuplicatedClasses(String searchText, SearchType searchType, int page, int pageSize) {
        return $(() -> {
            IResultTree result = queryByCommand(context, "duplicate_classes");
            List<?> classes = result.getElements();
            classes.sort((o1, o2) -> ((List<?>) o2).size() - ((List<?>) o1).size());
            PageViewBuilder<?, DuplicatedClass.ClassItem>
                    builder = PageViewBuilder.fromList(classes);
            return builder.paging(new PagingRequest(page, pageSize))
                    .map(r -> {
                        DuplicatedClass.ClassItem item = new DuplicatedClass.ClassItem();
                        item.setLabel((String) result.getColumnValue(r, 0));
                        item.setCount((Integer) result.getColumnValue(r, 1));
                        return item;
                    })
                    .filter(createPredicate(searchText, searchType))
                    .done();
        });
    }

    /**
     * 重复类子类
     *
     * @param index
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageView<ClassLoaderItem> getClassloadersOfDuplicatedClass(int index, int page, int pageSize) {
        return $(() -> {
            IResultTree result = queryByCommand(context, "duplicate_classes");
            List<?> classes = result.getElements();
            classes.sort((o1, o2) -> ((List<?>) o2).size() - ((List<?>) o1).size());
            List<?> classLoaders = (List<?>) classes.get(index);
            return PageViewBuilder.build(classLoaders, new PagingRequest(page, pageSize), r -> {
                ClassLoaderItem item = new ClassLoaderItem();
                item.setLabel((String) result.getColumnValue(r, 0));
                item.setDefinedClassesCount((Integer) result.getColumnValue(r, 2));
                item.setInstantiatedObjectsCount((Integer) result.getColumnValue(r, 3));
                GCRootInfo[] roots;
                try {
                    roots = ((IClass) r).getGCRootInfo();
                } catch (SnapshotException e) {
                    throw new JifaException(e);
                }
                int id = ((IClass) r).getClassLoaderId();
                item.setObjectId(id);
                item.setGCRoot(context.snapshot.isGCRoot(id));
                item.setSuffix(roots != null ? GCRootInfo.getTypeSetAsString(roots) : null);
                return item;
            });
        });
    }

    /**
     * 5. 类加载器视图
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageView<ClassLoader.Item> getClassLoaders(int page, int pageSize) {
        return $(() -> {
            ClassLoaderExplorerData data = queryClassLoader(context);
            IResultTree result = data.result;
            return PageViewBuilder.build(data.items, new PagingRequest(page, pageSize), e -> {
                ClassLoader.Item r = new ClassLoader.Item();
                r.setObjectId(result.getContext(e).getObjectId());
                r.setPrefix(((IDecorator) result).prefix(e));
                r.setLabel((String) result.getColumnValue(e, 0));
                r.setDefinedClasses((Integer) result.getColumnValue(e, 1));
                r.setNumberOfInstances((Integer) result.getColumnValue(e, 2));
                r.setClassLoader(true);
                // FIXME
                r.setHasParent(false);
                return r;
            });
        });
    }

    /**
     * 类加载器视图 子类
     *
     * @param classLoaderId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageView<ClassLoader.Item> getChildrenOfClassLoader(int classLoaderId, int page, int pageSize) {
        return $(() -> {
            ClassLoaderExplorerData data = queryClassLoader(context);
            IResultTree result = data.result;
            Object o = data.classLoaderIdMap.get(classLoaderId);
            List<?> children = result.getChildren(o);
            return PageViewBuilder.build(children, new PagingRequest(page, pageSize), e -> {
                ClassLoader.Item r = new ClassLoader.Item();
                r.setObjectId(result.getContext(e).getObjectId());
                r.setPrefix(((IDecorator) result).prefix(e));
                r.setLabel((String) result.getColumnValue(e, 0));
                r.setNumberOfInstances((Integer) result.getColumnValue(e, 2));
                if (!(e instanceof IClass)) {
                    r.setClassLoader(true);
                    r.setDefinedClasses((Integer) result.getColumnValue(e, 1));
                    // FIXME
                    r.setHasParent(false);
                }
                return r;
            });
        });
    }

    /**
     * 堆外内存
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PageView<DirectByteBuffer.Item> getDirectByteBuffers(int page, int pageSize) {
        return $(() -> {
            DirectByteBufferData data = queryDirectByteBufferData(context);
            RefinedTable resultContext = data.resultContext;
            return PageViewBuilder.build(new PageViewBuilder.Callback<Object>() {
                @Override
                public int totalSize() {
                    return data.summary.totalSize;
                }

                @Override
                public Object get(int index) {
                    return resultContext.getRow(index);
                }
            }, new PagingRequest(page, pageSize), row -> {
                DirectByteBuffer.Item item = new DirectByteBuffer.Item();
                item.objectId = resultContext.getContext(row).getObjectId();
                item.label = data.label(row);
                item.position = data.position(row);
                item.limit = data.limit(row);
                item.capacity = data.capacity(row);
                return item;
            });
        });
    }

    private DirectByteBufferData queryDirectByteBufferData(
            AnalysisContext context) throws SnapshotException {
        DirectByteBufferData data = context.directByteBufferData.get();
        if (data != null) {
            return data;
        }

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (context) {
            data = context.directByteBufferData.get();
            if (data != null) {
                return data;
            }

            data = new DirectByteBufferData();
            IResult result = queryByCommand(context, "oql", DirectByteBufferData.ARGS);
            IResultTable table;
            if (result instanceof IResultTable) {
                table = (IResultTable) result;

                RefinedResultBuilder builder =
                        new RefinedResultBuilder(new SnapshotQueryContext(context.snapshot), table);
                builder.setSortOrder(3, Column.SortDirection.DESC);
                data.resultContext = (RefinedTable) builder.build();
                DirectByteBuffer.Summary summary = new DirectByteBuffer.Summary();
                summary.totalSize = data.resultContext.getRowCount();

                for (int i = 0; i < summary.totalSize; i++) {
                    Object row = data.resultContext.getRow(i);
                    summary.position += data.position(row);
                    summary.limit += data.limit(row);
                    summary.capacity += data.capacity(row);
                }
                data.summary = summary;
            } else {
                data.summary = new DirectByteBuffer.Summary();
            }
            context.directByteBufferData = new SoftReference<>(data);
            return data;
        }
    }

    @Override
    public Integer mapAddressToId(long address) {
        return $(() -> context.snapshot.mapAddressToId(address));
    }

    @Override
    public String getObjectValue(int objectId) {
        return $(() -> {
            IObject object = context.snapshot.getObject(objectId);
            String text = object.getClassSpecificName();
            return text != null ? EscapeUtil.unescapeJava(text) : Constant.EMPTY_STRING;
        });
    }

    @Override
    public InspectorView getInspectorView(int objectId) {
        return $(() -> {
            InspectorView view = new InspectorView();

            ISnapshot snapshot = context.snapshot;
            IObject object = snapshot.getObject(objectId);

            view.setObjectAddress(object.getObjectAddress());
            IClass iClass = object instanceof IClass ? (IClass) object : object.getClazz();
            view.setName(iClass.getName());
            view.setObjectType(typeOf(object));
            view.setGCRoot(snapshot.isGCRoot(objectId));

            // class name and address of the object
            IClass clazz = object.getClazz();
            view.setClassLabel(clazz.getTechnicalName());
            view.setClassGCRoot(clazz.getGCRootInfo() != null);

            // super class name
            if (iClass.getSuperClass() != null) {
                view.setSuperClassName(iClass.getSuperClass().getName());
            }

            // class loader name and address
            IObject classLoader = snapshot.getObject(iClass.getClassLoaderId());
            view.setClassLoaderLabel(classLoader.getTechnicalName());
            view.setClassLoaderGCRoot(classLoader.getGCRootInfo() != null);

            view.setShallowSize(object.getUsedHeapSize());
            view.setRetainedSize(object.getRetainedHeapSize());
            // gc root
            GCRootInfo[] gcRootInfo = object.getGCRootInfo();
            view.setGcRootInfo(
                    gcRootInfo != null ? "GC root: " + GCRootInfo.getTypeSetAsString(object.getGCRootInfo())
                            : "no GC root");

            //HeapLayout heapLayout = snapshot.getSnapshotInfo().getHeapLayout();
            //view.setLocationType(locationTypeOf(heapLayout.genOf(view.getObjectAddress())));
            return view;
        });
    }

    @Override
    public PageView<FieldView> getFields(int objectId, int page, int pageSize) {
        return $(() -> {
            ISnapshot snapshot = context.snapshot;
            IObject object = snapshot.getObject(objectId);

            PagingRequest pagingRequest = new PagingRequest(page, pageSize);
            if (object instanceof IPrimitiveArray) {
                List<FieldView> fvs = new ArrayList<>();
                IPrimitiveArray pa = (IPrimitiveArray) object;
                int firstIndex = (pagingRequest.getPage() - 1) * pagingRequest.getPageSize();
                int lastIndex = Math.min(firstIndex + pagingRequest.getPageSize(), pa.getLength());
                for (int i = firstIndex; i < lastIndex; i++) {
                    fvs.add(new FieldView(pa.getType(), "[" + i + "]", pa.getValueAt(i).toString()));
                }
                return new PageView<>(pagingRequest, pa.getLength(), fvs);
            } else if (object instanceof IObjectArray) {
                List<FieldView> fvs = new ArrayList<>();
                IObjectArray oa = (IObjectArray) object;
                int firstIndex = (pagingRequest.getPage() - 1) * pagingRequest.getPageSize();
                int lastIndex = Math.min(firstIndex + pagingRequest.getPageSize(), oa.getLength());
                for (int i = firstIndex; i < lastIndex; i++) {
                    long[] refs = oa.getReferenceArray(i, 1);
                    int refObjectId = 0;
                    if (refs[0] != 0) {
                        refObjectId = snapshot.mapAddressToId(refs[0]);
                    }
                    String value = null;
                    if (refObjectId != 0) {
                        value = getObjectValue(snapshot.getObject(refObjectId));
                    }
                    fvs.add(new FieldView(IObject.Type.OBJECT, "[" + i + "]", value, refObjectId));
                }
                return new PageView<>(pagingRequest, oa.getLength(), fvs);
            }

            List<Field> fields = new ArrayList<>();
            boolean isClass = object instanceof IClass;
            IClass clazz = isClass ? (IClass) object : object.getClazz();
            if (object instanceof IInstance) {
                fields.addAll(((IInstance) object).getFields());
            } else if (object instanceof IClass) {
                do {
                    List<Field> staticFields = clazz.getStaticFields();
                    for (Field staticField : staticFields) {
                        if (staticField.getName().startsWith("<")) {
                            fields.add(staticField);
                        }
                    }
                } while ((clazz = clazz.getSuperClass()) != null);

            }
            return buildPageViewOfFields(fields, page, pageSize);

        });
    }

    @Override
    public PageView<FieldView> getStaticFields(int objectId, int page, int pageSize) {
        return $(() -> {
            ISnapshot snapshot = context.snapshot;
            IObject object = snapshot.getObject(objectId);
            boolean isClass = object instanceof IClass;
            IClass clazz = isClass ? (IClass) object : object.getClazz();

            List<Field> fields = new ArrayList<>();
            do {
                List<Field> staticFields = clazz.getStaticFields();
                for (Field staticField : staticFields) {
                    if (!staticField.getName().startsWith("<")) {
                        fields.add(staticField);
                    }
                }
            } while (!isClass && (clazz = clazz.getSuperClass()) != null);
            return buildPageViewOfFields(fields, page, pageSize);
        });
    }

    private String getObjectValue(IObject o) {
        String text = o.getClassSpecificName();
        return text != null ? EscapeUtil.unescapeJava(text) : o.getTechnicalName();
    }

    private PageView<FieldView> buildPageViewOfFields(List<Field> fields, int page, int pageSize) {
        return PageViewBuilder.build(fields, new PagingRequest(page, pageSize), field -> {
            FieldView fv = new FieldView();
            fv.fieldType = field.getType();
            fv.name = field.getName();
            Object value = field.getValue();
            if (value instanceof ObjectReference) {
                try {
                    fv.objectId = ((ObjectReference) value).getObjectId();
                    fv.value = getObjectValue(((ObjectReference) value).getObject());
                } catch (SnapshotException e) {
                    throw new AnalysisException(e);
                }
            } else if (value != null) {
                fv.value = value.toString();
            }
            return fv;
        });
    }

    /**
     * 系统属性
     *
     * @return
     */
    public Map<String, String> getSystemProperties() {
        return $(() -> {
            IResultTable result = queryByCommand(context, "system_properties");
            Map<String, String> map = new HashMap<>();
            int count = result.getRowCount();
            for (int i = 0; i < count; i++) {
                Object row = result.getRow(i);
                map.put((String) result.getColumnValue(row, 1), (String) result.getColumnValue(row, 2));
            }
            return map;
        });
    }

    /**
     * .泄露报表
     */
    public LeakReport getLeakReport() {
        return $(() -> {
            AnalysisContext.LeakReportData data = context.leakReportData.get();
            if (data == null) {
                synchronized (context) {
                    data = context.leakReportData.get();
                    if (data == null) {
                        IResult result = queryByCommand(context, "leakhunter");
                        data = new AnalysisContext.LeakReportData();
                        data.result = result;
                        context.leakReportData = new SoftReference<>(data);
                    }
                }
            }
            IResult result = data.result;
            LeakReport report = new LeakReport();
            if (result instanceof TextResult) {
                report.setInfo(((TextResult) result).getText());
            } else if (result instanceof SectionSpec) {
                report.setUseful(true);
                SectionSpec sectionSpec = (SectionSpec) result;
                report.setName(sectionSpec.getName());
                List<Spec> specs = sectionSpec.getChildren();
                for (int i = 0; i < specs.size(); i++) {
                    QuerySpec spec = (QuerySpec) specs.get(i);
                    String name = spec.getName();
                    if (name == null || name.isEmpty()) {
                        continue;
                    }
                    // LeakHunterQuery_Overview
                    if (name.startsWith("Overview")) {
                        IResultPie irtPie = (IResultPie) spec.getResult();
                        List<? extends IResultPie.Slice> pieSlices = irtPie.getSlices();

                        List<LeakReport.Slice> slices = new ArrayList<>();
                        for (IResultPie.Slice slice : pieSlices) {
                            slices.add(
                                    new LeakReport.Slice(slice.getLabel(),
                                            Helper.fetchObjectId(slice.getContext()),
                                            slice.getValue(), slice.getDescription()));
                        }
                        report.setSlices(slices);
                    }
                    // LeakHunterQuery_ProblemSuspect
                    // LeakHunterQuery_Hint
                    else if (name.startsWith("Problem Suspect") || name.startsWith("Hint") || name.startsWith("可疑点")) {
                        LeakReport.Record suspect = new LeakReport.Record();
                        suspect.setIndex(i);
                        suspect.setName(name);
                        CompositeResult cr = (CompositeResult) spec.getResult();
                        List<CompositeResult.Entry> entries = cr.getResultEntries();
                        for (CompositeResult.Entry entry : entries) {
                            String entryName = entry.getName();
                            if (entryName == null || entryName.isEmpty()) {
                                IResult r = entry.getResult();
                                if (r instanceof QuerySpec &&
                                        // LeakHunterQuery_ShortestPaths
                                        ((QuerySpec) r).getName().equals("Shortest Paths To the Accumulation Point")) {
                                    IResultTree tree = (IResultTree) ((QuerySpec) r).getResult();
                                    RefinedResultBuilder builder = new RefinedResultBuilder(
                                            new SnapshotQueryContext(context.snapshot), tree);
                                    RefinedTree rst = (RefinedTree) builder.build();
                                    List<?> elements = rst.getElements();
                                    List<LeakReport.ShortestPath> paths = new ArrayList<>();
                                    suspect.setPaths(paths);
                                    for (Object row : elements) {
                                        paths.add(buildPath(context.snapshot, rst, row));
                                    }
                                }
                            }
                            // LeakHunterQuery_Description
                            // LeakHunterQuery_Overview
                            else if ((entryName.startsWith("Description") || entryName.startsWith("Overview"))) {
                                TextResult desText = (TextResult) entry.getResult();
                                suspect.setDesc(desText.getText());
                            }
                        }
                        List<LeakReport.Record> records = report.getRecords();
                        if (records == null) {
                            report.setRecords(records = new ArrayList<>());
                        }
                        records.add(suspect);
                    }
                }
            }
            return report;
        });
    }

    /**
     * belong to 7.泄露报表
     *
     * @param snapshot
     * @param rst
     * @param row
     * @return
     * @throws SnapshotException
     */
    private LeakReport.ShortestPath buildPath(ISnapshot snapshot, RefinedTree rst,
                                              Object row) throws SnapshotException {
        LeakReport.ShortestPath shortestPath = new LeakReport.ShortestPath();
        shortestPath.setLabel((String) rst.getColumnValue(row, 0));
        shortestPath.setShallowSize(((Bytes) rst.getColumnValue(row, 1)).getValue());
        shortestPath.setRetainedSize(((Bytes) rst.getColumnValue(row, 2)).getValue());
        int objectId = rst.getContext(row).getObjectId();
        shortestPath.setObjectId(objectId);
        IObject object = snapshot.getObject(objectId);
        shortestPath.setGCRoot(snapshot.isGCRoot(objectId));
        shortestPath.setObjectType(typeOf(object));

        if (rst.hasChildren(row)) {
            List<LeakReport.ShortestPath> children = new ArrayList<>();
            shortestPath.setChildren(children);
            for (Object c : rst.getChildren(row)) {
                children.add(buildPath(snapshot, rst, c));
            }
        }
        return shortestPath;
    }

    /**
     * belong 类视图
     *
     * @param context
     * @return
     * @throws Exception
     */
    private ClassLoaderExplorerData queryClassLoader(AnalysisContext context) throws Exception {
        ClassLoaderExplorerData classLoaderExplorerData = context.classLoaderExplorerData.get();
        if (classLoaderExplorerData != null) {
            return classLoaderExplorerData;
        }
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (context) {
            classLoaderExplorerData = context.classLoaderExplorerData.get();
            if (classLoaderExplorerData != null) {
                return classLoaderExplorerData;
            }
            IResultTree result = queryByCommand(context, "ClassLoaderExplorerQuery");
            classLoaderExplorerData = new ClassLoaderExplorerData();
            classLoaderExplorerData.result = result;

            Map<Integer, Object> classLoaderIdMap = new HashMap<>();
            for (Object r : result.getElements()) {
                classLoaderIdMap.put(result.getContext(r).getObjectId(), r);
            }
            classLoaderExplorerData.classLoaderIdMap = classLoaderIdMap;

            classLoaderExplorerData.items = result.getElements();
            classLoaderExplorerData.items.sort((Comparator<Object>) (o1, o2) -> Integer
                    .compare((int) result.getColumnValue(o2, 1), (int) result.getColumnValue(o1, 1)));

            for (Object item : classLoaderExplorerData.items) {
                classLoaderExplorerData.definedClasses += (int) result.getColumnValue(item, 1);
                classLoaderExplorerData.numberOfInstances += (int) result.getColumnValue(item, 2);
            }
            context.classLoaderExplorerData = new SoftReference<>(classLoaderExplorerData);
            return classLoaderExplorerData;
        }
    }

    private PageView<DominatorTree.DefaultItem> buildDefaultItems(ISnapshot snapshot, IResultTree tree,
                                                                  List<?> elements,
                                                                  boolean ascendingOrder, String sortBy,
                                                                  String searchText, SearchType searchType,
                                                                  PagingRequest pagingRequest) {
        final AtomicInteger afterFilterCount = new AtomicInteger(0);
        List<DominatorTree.DefaultItem> items = elements.stream()
                .map(e -> $(() -> new VirtualDefaultItem(snapshot, tree, e)))
                .filter(createPredicate(searchText, searchType))
                .peek(filtered -> afterFilterCount.incrementAndGet())
                .sorted(DominatorTree.DefaultItem.sortBy(sortBy, ascendingOrder))
                .skip(pagingRequest.from())
                .limit(pagingRequest.getPageSize())
                .collect(Collectors.toList());
        return new PageView(pagingRequest, afterFilterCount.get(), items);
    }

    private PageView<DominatorTree.ClassItem> buildClassItems(ISnapshot snapshot, IResultTree tree, List<?> elements,
                                                              boolean ascendingOrder,
                                                              String sortBy,
                                                              String searchText, SearchType searchType,
                                                              PagingRequest pagingRequest) {
        final AtomicInteger afterFilterCount = new AtomicInteger(0);
        List<DominatorTree.ClassItem> items = elements.stream()
                .map(e -> $(() -> new VirtualClassItem(snapshot, tree, e)))
                .filter(createPredicate(searchText, searchType))
                .peek(filtered -> afterFilterCount.incrementAndGet())
                .sorted(DominatorTree.ClassItem.sortBy(sortBy, ascendingOrder))
                .skip(pagingRequest.from())
                .limit(pagingRequest.getPageSize())
                .collect(Collectors.toList());
        return new PageView(pagingRequest, afterFilterCount.get(), items);
    }

    private PageView<DominatorTree.ClassLoaderItem> buildClassLoaderItems(ISnapshot snapshot, IResultTree tree,
                                                                          List<?> elements, boolean ascendingOrder,
                                                                          String sortBy,
                                                                          String searchText, SearchType searchType,
                                                                          PagingRequest pagingRequest) {
        final AtomicInteger afterFilterCount = new AtomicInteger(0);
        List<DominatorTree.ClassLoaderItem> items = elements.stream()
                .map(e -> $(() -> new VirtualClassLoaderItem(snapshot, tree, e)))
                .filter(createPredicate(searchText, searchType))
                .peek(filtered -> afterFilterCount.incrementAndGet())
                .sorted(DominatorTree.ClassLoaderItem.sortBy(sortBy, ascendingOrder))
                .skip(pagingRequest.from())
                .limit(pagingRequest.getPageSize())
                .collect(Collectors.toList());
        return new PageView(pagingRequest, afterFilterCount.get(), items);
    }

    private PageView<DominatorTree.PackageItem> buildPackageItems(ISnapshot snapshot, IResultTree tree,
                                                                  List<?> elements,
                                                                  boolean ascendingOrder, String sortBy,
                                                                  String searchText, SearchType searchType,
                                                                  PagingRequest pagingRequest) {
        final AtomicInteger afterFilterCount = new AtomicInteger(0);
        List<DominatorTree.PackageItem> items = elements.stream()
                .map(e -> $(() -> new VirtualPackageItem(snapshot, tree, e)))
                .filter(createPredicate(searchText, searchType))
                .peek(filtered -> afterFilterCount.incrementAndGet())
                .sorted(DominatorTree.PackageItem.sortBy(sortBy, ascendingOrder))
                .skip(pagingRequest.from())
                .limit(pagingRequest.getPageSize())
                .collect(Collectors.toList());
        return new PageView(pagingRequest, afterFilterCount.get(), items);
    }

    @Override
    public ISnapshot getSnapShot(File file) {
        Map<String, String> argsMap = new HashMap<>();
        argsMap.put("keep_unreachable_objects", "true");
        argsMap.put("heap_layout", "");
        try {
            IProgressListener listener = new ConsoleProgressListener(System.out);

            ISnapshot snapshot = SnapshotFactory.openSnapshot(file, argsMap, listener);

            snapshotInfo = snapshot.getSnapshotInfo();

            return snapshot;
        } catch (SnapshotException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Details summary() {
        return new Details(snapshotInfo.getJvmInfo(), snapshotInfo.getIdentifierSize(),
                snapshotInfo.getCreationDate().getTime(), snapshotInfo.getNumberOfObjects(),
                snapshotInfo.getNumberOfGCRoots(), snapshotInfo.getNumberOfClasses(),
                snapshotInfo.getNumberOfClassLoaders(), snapshotInfo.getUsedHeapSize(), false);
    }

    private <Res extends IResult> Res queryByCommand(AnalysisContext context,
                                                     String command) throws SnapshotException {
        return queryByCommand(context, command, null, ProgressListener.NoOpProgressListener);
    }

    @Cacheable
    protected <Res extends IResult> Res queryByCommand(AnalysisContext context,
                                                       String command,
                                                       Map<String, Object> args) throws SnapshotException {
        return queryByCommand(context, command, args, ProgressListener.NoOpProgressListener);
    }

    @SuppressWarnings("unchecked")
    private <Res extends IResult> Res queryByCommand(AnalysisContext context, String command,
                                                     Map<String, Object> args,
                                                     ProgressListener listener) throws SnapshotException {
        SnapshotQuery query = SnapshotQuery.parse(command, context.snapshot);
        if (args != null) {
            args.forEach((k, v) -> $(() -> query.setArgument(k, v)));
        }
        return (Res) query.execute(new ProgressListenerImpl(listener));
    }

    private static class ProviderImpl implements Provider {
        @Override
        public HeapDumpAnalyzer provide(Path path, Map<String, String> arguments,
                                        ProgressListener listener) {
            return new HeapDumpAnalyzerImpl(new AnalysisContext(
                    $(() -> SnapshotFactory.openSnapshot(path.toFile(), arguments, new ProgressListenerImpl(listener)))
            ));
        }
    }

}