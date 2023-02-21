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
package com.alipay.autotuneservice.thread.model;

import com.alipay.autotuneservice.service.algorithmlab.diagnosis.report.DiagnosisReport;
import com.alipay.autotuneservice.thread.ThreadInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version ThreadVO.java, v 0.1 2022年12月02日 2:23 下午 huoyuqi
 */
@Data
@AllArgsConstructor
public class ThreadVO {

    /**
     * 1.基本信息
     */
    private ThreadBasicInfo basicInfo;

    /**
     * 2.所有线程数量
     */
    private TotalThreadVO totalThreadVO;

    /**
     * 3.ThreadGroup 线程分组
     */
    private ThreadGroupVO threadGroupVO;

    /**
     * 4.获取守护线程
     */
    private DaemonVO daemonVO;


    /**
     * 5.堆栈
     */
    private StackVO stackVO;

    /**
     * 6.cpu消耗线程
     */
    private List<ThreadInfo> cpuThreads;

    /**
     * 7.GC THread
     */
    private ThreadSort gcThreads;

    /**
     * 8.构建stackLength
     */
    private StackLengthVO stackLengthVO;

    /**
     * 9.构建最后方法
     */
    private List<ThreadSort> lastMethods;

    /**
     * 10.封装死锁
     */
    private List<DeadLockVO> deadLockVOS;

    /**
     * 11.封装堆栈关系
     */
    private StackRelateVO stackRelateVO;

    /**
     * 12 诊断报告
     */
    private DiagnosisReport diagnosisReport;


    public ThreadVO(ThreadBasicInfo basicInfo, TotalThreadVO totalThreadVO, ThreadGroupVO threadGroupVO,
                    DaemonVO daemonVO, StackVO stackVO, List<ThreadInfo> cpuThreads,
                    ThreadSort gcThreads, StackLengthVO stackLengthVO,
                    List<ThreadSort> lastMethods, List<DeadLockVO> deadLockVOS,
                    StackRelateVO stackRelateVO) {
        this.basicInfo = basicInfo;
        this.totalThreadVO = totalThreadVO;
        this.threadGroupVO = threadGroupVO;
        this.daemonVO = daemonVO;
        this.stackVO = stackVO;
        this.cpuThreads = cpuThreads;
        this.gcThreads = gcThreads;
        this.stackLengthVO = stackLengthVO;
        this.lastMethods = lastMethods;
        this.deadLockVOS = deadLockVOS;
        this.stackRelateVO = stackRelateVO;
    }

}