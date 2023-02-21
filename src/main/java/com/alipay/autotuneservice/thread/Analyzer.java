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
package com.alipay.autotuneservice.thread;

import com.alipay.autotuneservice.service.algorithmlab.diagnosis.DiagnosisLab;
import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import com.alipay.autotuneservice.thread.Converter.PushBackBufferedReader;
import com.alipay.autotuneservice.thread.model.DaemonVO;
import com.alipay.autotuneservice.thread.model.DeadLockVO;
import com.alipay.autotuneservice.thread.model.Node;
import com.alipay.autotuneservice.thread.model.StackLengthVO;
import com.alipay.autotuneservice.thread.model.StackRelateVO;
import com.alipay.autotuneservice.thread.model.StackVO;
import com.alipay.autotuneservice.thread.model.ThreadBasicInfo;
import com.alipay.autotuneservice.thread.model.ThreadGroup;
import com.alipay.autotuneservice.thread.model.ThreadGroupVO;
import com.alipay.autotuneservice.thread.model.ThreadRate;
import com.alipay.autotuneservice.thread.model.ThreadSort;
import com.alipay.autotuneservice.thread.model.ThreadType;
import com.alipay.autotuneservice.thread.model.ThreadVO;
import com.alipay.autotuneservice.thread.model.TotalThreadVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version Analyzer.java, v 0.1 2022年01月06日 14:36 dutianze
 */
@Slf4j
@Service
public class Analyzer {

    public ThreadVO readJStacks(File file) {
        if (file != null) {
            File stack = file;
            Date stackDate = new Date(stack.lastModified());

            PushBackBufferedReader br;
            try {
                br = new PushBackBufferedReader(new FileReader(stack));
                Converter converter = new Converter();
                Measure measure = converter.parseJStack(br);
                measure.date = stackDate;
                measure.name = stack.getName();

                getState(measure);

                //1.基本信息
                ThreadBasicInfo basicInfo = new ThreadBasicInfo(measure.getName(), measure.date.getTime(), measure.getJvmInfo());

                //2.所有线程数量
                TotalThreadVO totalThreadVO = constructTotalCount(measure);

                //3.ThreadGroup 线程分组
                ThreadGroupVO threadGroupVO = constructGroupVO(measure);

                //4.获取守护线程
                DaemonVO daemonVO = constructDaemonVO(measure);

                //5.堆栈  柱状图 name count item  表 数量+状态 +item
                StackVO stackVO = constructStack(measure);

                //6.cpu消耗线程
                List<ThreadInfo> cpus = measure.getThreads().stream().filter(p -> p.getState().equals(ThreadType.RUNNABLE.name())).collect(
                        Collectors.toList());

                //7.GC THread
                ThreadSort gcThread = constructGC(measure);

                //8.构建stackLength
                StackLengthVO stackLengthVO = constructLength(measure);

                //9.构建最后方法
                List<ThreadSort> lastMethods = constructLastMethod(measure);

                //10.封装死锁
                List<DeadLockVO> deadLockVOS = constructLockVO(measure);

                //11.构建堆栈
                StackRelateVO stackRelateVO = constructStackRelate(measure);

                ThreadVO threadVO = new ThreadVO(basicInfo, totalThreadVO, threadGroupVO, daemonVO, stackVO, cpus, gcThread, stackLengthVO,
                        lastMethods, deadLockVOS, stackRelateVO);

                DiagnosisReport diagnosisReport = null;
                try {
                    diagnosisReport = DiagnosisLab.threadLogDiagnosis(threadVO);
                } catch (Exception e) {
                    log.error("readJStacks diagnosisReport classLoaderVO occurs an error", e);
                }
                threadVO.setDiagnosisReport(diagnosisReport);

                return threadVO;

            } catch (Exception e) {
                log.error("readJStacks error", e);
            }
        }
        return null;
    }

    //List <list[l1,l2,l3]>
    private StackRelateVO constructStackRelate(Measure measure) {

        try {
            //构建List<List<String>>
            List<List<String>> input = measure.getThreads().stream().map(item -> {
                List<StacktraceItem> items = item.getStacktrace();
                if (CollectionUtils.isEmpty(items)) {
                    return null;
                }
                return combinationStackItem(items);
            }).collect(Collectors.toList());

            input = input.stream().filter(CollectionUtils::isNotEmpty).collect(Collectors.toList());

            Map<String, Node> nodeMap = createTree(input);
            for (Node root : nodeMap.values()) {
                calcNodeCount(root);
            }
            Node tree = nodeMap.get("root");
            return constructStackRelate(tree);
        } catch (Exception e) {
            log.info("constructStackRelate occurs an error", e);
            return null;
        }
    }

    public StackRelateVO constructStackRelate(Node tree) {

        if (MapUtils.isEmpty(tree.getChildrenMap())) {
            return new StackRelateVO(tree.getName(), tree.getSubTreeNodeCount(), null);
        }
        StackRelateVO stackRelateVO = new StackRelateVO();
        stackRelateVO.setName(tree.getName());
        stackRelateVO.setCount(tree.getSubTreeNodeCount());
        for (Node node : tree.getChildrenMap().values()) {
            stackRelateVO.getChildren().add(constructStackRelate(node));
        }
        return stackRelateVO;
    }

    public int calcNodeCount(Node tree) {
        if (tree == null) {
            return 0;
        }
        tree.setSubTreeNodeCount(1);
        if (MapUtils.isEmpty(tree.getChildrenMap())) {
            return 1;
        }
        for (Node node : tree.getChildrenMap().values()) {
            tree.setSubTreeNodeCount(calcNodeCount(node) + tree.getSubTreeNodeCount());
        }
        return tree.getSubTreeNodeCount();
    }

    public Map<String, Node> createTree(List<List<String>> input) {
        AtomicInteger key = new AtomicInteger(0);
        if (CollectionUtils.isEmpty(input)) {
            return null;
        }
        Map<String, Node> rootMap = Maps.newHashMap();
        for (List<String> path : input) {
            Node parent = null;
            Map<String, Node> tmp = rootMap;
            List<Node> children = new ArrayList<>();
            for (String one : path) {
                Node node = tmp.get(one);
                if (node == null) {
                    node = Node.builder().name(one).childrenMap(Maps.newHashMap()).build();
                    if (parent == null) {
                        node.setRoute(Lists.newArrayList(node.getName()));
                        node.setKey(key.getAndIncrement());
                    } else {
                        node.setParent(parent.getName());
                        List<String> route = Lists.newArrayList(parent.getRoute());
                        route.add(node.getName());
                        node.setRoute(route);
                        node.setKey(key.getAndIncrement());
                    }
                    tmp.put(one, node);
                }
                tmp = node.getChildrenMap();
                parent = node;
            }
        }
        return rootMap;
    }

    private List<String> combinationStackItem(List<StacktraceItem> items) {
        //把根节点放入进去
        StacktraceItem stacktraceItem = new StacktraceItem("root", null);
        items.add(stacktraceItem);
        Collections.reverse(items);
        return items.stream().map(
                p -> String.format("%s%s", p.getMethodFqn(), StringUtils.isNotEmpty(p.getFileLine()) ? p.getFileLine() : "")).collect(
                Collectors.toList());
    }

    private List<DeadLockVO> constructLockVO(Measure measure) {
        try {
            Map<String, List<ThreadInfo>> deadLockMap = judgeDeadLock(measure);
            List<DeadLockVO> vos = new ArrayList<>();
            if (MapUtils.isEmpty(deadLockMap)) {
                return null;
            }
            deadLockMap.forEach((k, v) -> {
                List<String> titles = v.stream().map(ThreadInfo::getName).collect(Collectors.toList());
                vos.add(new DeadLockVO(titles, v));
            });
            return vos;
        } catch (Exception e) {
            log.error("constructLockVO occurs an error", e);
            return null;
        }
    }

    private Map<String, List<ThreadInfo>> judgeDeadLock(Measure measure) {
        List<ThreadInfo> threadInfos = measure.getThreads().stream()
                .filter(p -> p.getLock() != null && p.getWaitToLock() != null).collect(Collectors.toList());
        threadInfos.forEach(item -> {
            for (int i = 0; i < threadInfos.size(); i++) {
                if (item.getLock().equals(threadInfos.get(i).getWaitToLock())) {
                    item.setRelateThread(threadInfos.get(i));
                }
            }
        });

        //所有出现的记录放入一个里面  judge 死锁形成循环依赖
        List<ThreadInfo> allInfos = new ArrayList<>();
        Map<String, List<ThreadInfo>> deadLockMap = new HashMap<>();
        threadInfos.forEach(item -> {
            if (CollectionUtils.isNotEmpty(allInfos) && allInfos.contains(item)) {
                return;
            }
            ThreadInfo threadInfo = item;
            List<ThreadInfo> infos = new ArrayList<>();
            while (threadInfo != null && threadInfo.getRelateThread() != null) {
                infos.add(threadInfo);
                threadInfo = threadInfo.getRelateThread();
                if (threadInfo == item) {
                    infos.add(threadInfo);
                    deadLockMap.put(item.getName(), infos);
                    allInfos.addAll(infos);
                    break;
                }
            }
        });
        deadLockMap.forEach((k, v) -> v.stream().map(p -> {
            p.setRelateThread(null);
            return p;
        }).collect(Collectors.toList()));

        measure.getThreads().stream().map(item -> {
            item.setRelateThread(null);
            return item;
        }).collect(Collectors.toList());
        return deadLockMap;
    }

    private List<ThreadSort> constructLastMethod(Measure measure) {
        try {
            List<ThreadSort> sorts = new ArrayList<>();
            Integer totalCount = measure.getThreads().size();
            Map<StacktraceItem, List<ThreadInfo>> methodMap = measure.getThreads().stream()
                    .filter(item -> item.getStacktrace() != null && item.getStacktrace().size() > 0)
                    .collect(Collectors.groupingBy(p -> p.getStacktrace().get(0)));
            methodMap.forEach((k, v) -> {
                sorts.add(new ThreadSort(combinationMethod(k), v.size(), (double) v.size() / totalCount, v));
            });
            return sorts;
        } catch (Exception e) {
            //do nothing
            log.error("constructLastMethod occurs an error", e);
            return null;
        }
    }

    private String combinationMethod(StacktraceItem item) {
        if (item == null) {
            return null;
        }
        String result = String.format("%s(%s)", item.getMethodFqn(), item.getFileLine());
        return result;
    }

    private StackVO constructStack(Measure measure) {
        try {
            List<ThreadRate> rates = new ArrayList<>();
            List<ThreadSort> sorts = new ArrayList<>();

            //获取相应数组
            Map<List<String>, List<ThreadInfo>> identMap = measure.getThreads().stream()
                    .collect(Collectors
                            .groupingBy(item -> item.getStackTraces().stream().filter(p -> !p.contains("-")).collect(Collectors.toList())));
            AtomicInteger i = new AtomicInteger();
            identMap.forEach((k, v) -> {
                rates.add(new ThreadRate("stackTrace", (double) v.size(), v.get(0).getStackTraces()));
                String name = String.format("%d %s", v.size(), v.get(0).getState());
                sorts.add(new ThreadSort(name, v.size(), v.get(0).getStackTraces(), v));
            });

            //升序排序
            List<ThreadRate> resultRates = rates.stream().sorted(Comparator.comparing(ThreadRate::getRate)).collect(Collectors.toList());
            resultRates = resultRates.stream().map(r -> {
                r.setType(String.format("%s%d", r.getType(), i.getAndIncrement()));
                return r;
            }).collect(Collectors.toList());
            List<ThreadSort> resultSorts = sorts.stream().sorted(Comparator.comparing(ThreadSort::getCount)).collect(Collectors.toList());
            return new StackVO(resultRates, resultSorts);
        } catch (Exception e) {
            log.error("constructStack occurs an error", e);
            return null;
        }
    }

    private StackLengthVO constructLength(Measure measure) {
        try {
            Integer totalCount = measure.getThreads().size();
            List<ThreadSort> sorts = new ArrayList<>();
            List<ThreadRate> rates = new ArrayList<>();
            List<ThreadInfo> lowRow = measure.getThreads().stream().filter(p -> p.getStackTraces().size() < 10).collect(
                    Collectors.toList());
            List<ThreadInfo> highRow = measure.getThreads().stream().filter(p -> p.getStackTraces().size() >= 10).collect(
                    Collectors.toList());
            sorts.add(new ThreadSort("< 10 lines", lowRow.size(), lowRow));
            sorts.add(new ThreadSort("between 10 - 100 lines", highRow.size(), highRow));
            rates.add(new ThreadRate("< 10 lines", (double) lowRow.size() / totalCount));
            rates.add(new ThreadRate("between 10 - 100 lines", (double) highRow.size() / totalCount));
            return new StackLengthVO(sorts, rates);
        } catch (Exception e) {
            log.error("constructLength occurs an error");
            return null;
        }
    }

    private ThreadSort constructGC(Measure measure) {
        try {
            double totalCount = measure.getThreads().size();
            List<ThreadInfo> infos = measure.getThreads().stream().filter(p -> p.getGroupName().equals("GC task thread")).collect(
                    Collectors.toList());
            return new ThreadSort("GC Threads", infos.size(), infos.size() / totalCount, infos);
        } catch (Exception e) {
            log.error("constructGC occurs an error", e);
            return null;
        }
    }

    private DaemonVO constructDaemonVO(Measure measure) {
        try {
            double totalCount = measure.getThreads().size();
            List<ThreadSort> sorts = new ArrayList<>();
            List<ThreadInfo> daemons = measure.getThreads().stream().filter(ThreadInfo::isDaemon).collect(Collectors.toList());
            List<ThreadInfo> noDaemons = measure.getThreads().stream().filter(item -> !item.isDaemon()).collect(Collectors.toList());
            sorts.add(new ThreadSort("DAEMON", daemons.size(), daemons.size() / totalCount, daemons));
            sorts.add(new ThreadSort("NON_DAEMON", noDaemons.size(), daemons.size() / totalCount, noDaemons));
            return new DaemonVO(sorts);
        } catch (Exception e) {
            log.info("constructDaemonVO occurs an error", e);
            return null;
        }
    }

    private ThreadGroupVO constructGroupVO(Measure measure) {
        try {
            measure.getThreads().forEach(item -> item.setGroupName(ConvertGroupName.convertName(item.getName())));
            Map<String, List<ThreadInfo>> threadMap = measure.getThreads().stream()
                    .collect(Collectors.groupingBy(ThreadInfo::getGroupName));

            List<ThreadGroup> groups = new ArrayList<>();
            threadMap.forEach((k, v) -> {
                List<ThreadSort> sorts = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(v)) {
                    //现根据状态进行分组，分完组 之后计算每组的count
                    v.stream().collect(Collectors.groupingBy(ThreadInfo::getState)).forEach(
                            (k1, v1) -> sorts.add(new ThreadSort(ThreadType.valueOf(k1), v1.size())));
                }
                groups.add(new ThreadGroup(k, v.size(), v, sorts));
            });

            return new ThreadGroupVO(groups);
        } catch (Exception e) {
            log.error("constructGroupVO occurs an error", e);
            return null;
        }

    }

    private TotalThreadVO constructTotalCount(Measure measure) {
        try {
            double totalCount = measure.getThreads().size();
            Map<String, List<ThreadInfo>> typeMap = measure.getThreads().stream()
                    .collect(Collectors.groupingBy(ThreadInfo::getState));
            List<ThreadSort> threadGroups = new ArrayList<>();
            typeMap.forEach((k, v) -> threadGroups.add(new ThreadSort(ThreadType.valueOf(k), v.size(), v.size() / totalCount, v)));
            return new TotalThreadVO(threadGroups, totalCount);
        } catch (Exception e) {
            log.info("getTotalCount occurs an error", e);
            return null;
        }
    }

    private void getState(Measure measure) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        measure.getThreads().forEach(item -> {
                    item.setIndex(atomicInteger.getAndIncrement());
                    if (item.getState() == null) {
                        item.setState("WAITING");
                        if (StringUtils.equals(item.getState0(), "runnable ")) {
                            item.setState("RUNNABLE");
                        }
                    }
                }
        );
    }

}