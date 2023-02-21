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

import com.alipay.autotuneservice.heap.util.Searchable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PageViewBuilder<A, B extends Searchable> {

    // simple builder
    public static <S, R> PageView<R> build(Callback<S> callback, PagingRequest paging, Function<S, R> mapper) {
        List<R> result = IntStream.range(paging.from(), paging.to(callback.totalSize()))
                .mapToObj(callback::get)
                .map(mapper)
                .collect(Collectors.toList());
        return new PageView<>(paging, callback.totalSize(), result);
    }

    public static <R> PageView<R> build(Collection<R> total, PagingRequest paging) {
        List<R> result = total.stream()
                .skip(paging.from())
                .limit(paging.getPageSize())
                .collect(Collectors.toList());
        return new PageView<>(paging, total.size(), result);
    }

    public static <S, R> PageView<R> build(Collection<S> total, PagingRequest paging, Function<S, R> mapper) {
        List<R> result = total.stream()
                .skip(paging.from())
                .limit(paging.getPageSize())
                .map(mapper)
                .collect(Collectors.toList());
        return new PageView<>(paging, total.size(), result);
    }

    public static <S, T, R> PageView<R> build(Collection<S> total, PagingRequest paging, Function<S, T> mapper1,
                                              Function<T, R> mapper2,
                                              Comparator<T> comparator) {
        List<R> result = total.stream()
                .map(mapper1)
                .sorted(comparator)
                .skip(paging.from())
                .limit(paging.getPageSize())
                .map(mapper2)
                .collect(Collectors.toList());
        return new PageView<>(paging, total.size(), result);
    }

    public static <R> PageView<R> build(int[] total, PagingRequest paging, IntFunction<R> mapper) {
        List<R> result = Arrays.stream(total)
                .skip(paging.from())
                .limit(paging.getPageSize())
                .mapToObj(mapper)
                .collect(Collectors.toList());
        return new PageView<>(paging, total.length, result);
    }

    public static <S, R> PageView<R> build(S[] total, PagingRequest paging, Function<S, R> mapper) {
        List<R> result = Arrays.stream(total)
                .skip(paging.from())
                .limit(paging.getPageSize())
                .map(mapper)
                .collect(Collectors.toList());
        return new PageView<>(paging, total.length, result);
    }

    public interface Callback<O> {
        int totalSize();

        O get(int index);
    }

    // complex builder
    private List<A>        list;
    private Function<A, B> mapper;
    private PagingRequest  paging;
    private Comparator<B>  comparator;
    private Predicate<B>   filter;
    private boolean        noPagingNeeded;

    private PageViewBuilder() {

    }

    public static <A, B extends Searchable> PageViewBuilder<A, B> fromList(List<A> list) {
        PageViewBuilder<A, B> builder = new PageViewBuilder<>();
        builder.list = list;
        return builder;
    }

    public PageViewBuilder<A, B> beforeMap(Consumer<A> consumer) {
        this.list.forEach(consumer);
        return this;
    }

    public PageViewBuilder<A, B> map(Function<A, B> mapper) {
        this.mapper = mapper;
        return this;
    }

    public PageViewBuilder<A, B> sort(Comparator<B> mapper) {
        this.comparator = mapper;
        return this;
    }

    public PageViewBuilder<A, B> filter(Predicate<B> mapper) {
        this.filter = mapper;
        return this;
    }

    public PageViewBuilder<A, B> paging(PagingRequest paging) {
        this.paging = paging;
        return this;
    }

    public PageView<B> done() {
        Stream<B> stream = list.stream().map(mapper);

        if (filter != null) {
            stream = stream.filter(filter);
        }
        if (comparator != null) {
            stream = stream.sorted(comparator);
        }
        List<B> processedList = stream.collect(Collectors.toList());
        // paging must be exist since this is PAGEVIEW builder.
        List<B> finalList = processedList
                .stream()
                .skip(paging.from())
                .limit(paging.getPageSize())
                .collect(Collectors.toList());
        return new PageView<>(paging,/*totalSize*/ processedList.size(), /*display list*/finalList);
    }
}
