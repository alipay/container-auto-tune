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
package com.alipay.autotuneservice.heap.util.pageutil;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class PageView<T> {

    public static final PageView<?> EMPTY = new PageView<>(null, 0, Collections.emptyList());

    @SuppressWarnings("unchecked")
    public static <T> PageView<T> empty() {
        return (PageView<T>) EMPTY;
    }

    private List<T> data;

    private int page;

    private int pageSize;

    private int totalSize;

    private int filtered;

    public PageView(PagingRequest request, int totalSize, List<T> data) {
        this.data = data;
        this.page = request != null ? request.getPage() : 0;
        this.pageSize = request != null ? request.getPageSize() : 0;
        this.totalSize = totalSize;
    }

    public PageView() {
    }

}
