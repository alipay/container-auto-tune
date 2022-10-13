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
package com.alipay.autotuneservice.model;

import lombok.Data;

import java.util.List;

/**
 * @author dutianze
 * @version PageResult.java, v 0.1 2021年07月26日 16:38 dutianze
 */
@Data
public class PageResult<T> {

    private int     pageSize;
    private int     pageNum;
    private int     pageIndex;
    private long    total;
    private long    agentTotal;
    /**
     * 开始第几条
     */
    private int     startIndex;
    /**
     * 结束第几条
     */
    private int     endIndex;
    private List<T> data;

    /**
     * 分页
     *
     * @param pageSize pge size
     * @param pageNum  page num
     * @param total    total
     * @param data     data
     * @param <T>      type of element
     * @return
     */
    public static <T> PageResult<T> of(int pageSize, int pageNum, long total, List<T> data) {
        PageResult<T> page = new PageResult<>();
        page.setPageSize(pageSize);
        page.setPageNum(pageNum);
        page.setTotal(total);
        page.setData(data);
        return page;
    }

    /**
     * 分页
     *
     * @param pageSize pge size
     * @param pageNum  page num
     * @param total    total
     * @param data     data
     * @param <T>      type of element
     * @return
     */
    public static <T> PageResult<T> of(int pageSize, int pageNum, int pageIndex, long total,
                                       long agentTotal, int startIndex, int endIndex, List<T> data) {
        PageResult<T> page = new PageResult<>();
        page.setPageSize(pageSize);
        page.setPageNum(pageNum);
        page.setPageIndex(pageIndex);
        page.setTotal(total);
        page.setAgentTotal(agentTotal);
        page.setStartIndex(startIndex);
        page.setEndIndex(endIndex);
        page.setData(data);
        return page;
    }
}