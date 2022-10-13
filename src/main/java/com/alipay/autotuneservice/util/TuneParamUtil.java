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
package com.alipay.autotuneservice.util;

import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamAttributeEnum;
import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamItem;
import com.alipay.autotuneservice.model.tune.params.JVMParamEnum;
import com.alipay.autotuneservice.model.tune.params.TuneParamParser;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : TuneParamUtil.java, v 0.1 2022年05月17日 9:17 PM huangkaifei Exp $
 */
@Slf4j
public class TuneParamUtil {

    public static List<TuneParamItem> convert2TuneParamItem(String appDefaultJvm) {
        if (StringUtils.isEmpty(appDefaultJvm)) {
            return Lists.newArrayList();
        }
        return Arrays.stream(appDefaultJvm.split("\\s+")).distinct().filter(StringUtils::isNotBlank).map(o -> {
            try {
                JVMParamEnum match = JVMParamEnum.match(o);
                return (TuneParamItem) TuneParamParser.getTuneParamItemParser(JVMParamEnum.match(o)).parse(match, o);
            } catch (Exception e) {
                log.error("convert2TuneParam - parser JVM occurs an error.", e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<TuneParamItem> wrapUpdateTuneParamsWithParamName(List<TuneParamItem> updateTuneParams) {
        if (CollectionUtils.isEmpty(updateTuneParams)) {
            return Lists.newArrayList();
        }
        return updateTuneParams.stream().map(item -> {
            item.getTuneParamName();
            return item;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<TuneParamItem> mergeUpdateTuneParamItem(List<TuneParamItem> originTuneParamItems,
                                                               List<TuneParamItem> updatedTuneParamItems) {
        // 原始参数为空，返回updatedTuneParamItems的属性都为NEW
        if (CollectionUtils.isEmpty(originTuneParamItems)) {
            return Optional.ofNullable(updatedTuneParamItems).orElse(Lists.newArrayList()).stream().filter(Objects::nonNull).map(item -> {
                item.setAttributeEnum(TuneParamAttributeEnum.NEW);
                return item;
            }).collect(Collectors.toList());
        }
        // 原始参数不为空，updatedTuneParamItems为空，返回originTuneParamItems为SAME
        if (CollectionUtils.isEmpty(updatedTuneParamItems)) {
            return originTuneParamItems.stream().filter(Objects::nonNull).map(item -> {
                item.setAttributeEnum(TuneParamAttributeEnum.SAME);
                return item;
            }).collect(Collectors.toList());
        }
        List<TuneParamItem> resultList = new ArrayList<>();
        // -- 原始和更新的都不为空 --
        // merge新增的: origin无，update有
        resultList.addAll(merge4NewParamItem(originTuneParamItems, updatedTuneParamItems));
        // merge相同的: origin有，update有 且相同
        resultList.addAll(merge4SameParamItem(originTuneParamItems, updatedTuneParamItems));
        // merge删除的： origin有，update无
        resultList.addAll(merge4DeletedTuneParamItem(originTuneParamItems, updatedTuneParamItems));
        // merge更新的： origin有，update有
        resultList.addAll(merge4ReplaceParamItem(originTuneParamItems, updatedTuneParamItems));
        return resultList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static List<TuneParamItem> merge4NewParamItem(List<TuneParamItem> originTuneParamItems, List<TuneParamItem> updatedTuneParamItems) {
        return updatedTuneParamItems.stream().map(item -> {
            TuneParamItem originParamItem = findTuneParamItem(originTuneParamItems, item);
            if (originParamItem == null) {
                item.setAttributeEnum(TuneParamAttributeEnum.NEW);
                return item;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static List<TuneParamItem> merge4SameParamItem(List<TuneParamItem> originTuneParamItems,
                                                              List<TuneParamItem> updatedTuneParamItems){
        return updatedTuneParamItems.stream().map(updateTuneParam -> {
                    TuneParamItem sameTuneParamItem = findSameTuneParamItem(originTuneParamItems, updateTuneParam);
                    if (sameTuneParamItem != null){
                        updateTuneParam.setAttributeEnum(TuneParamAttributeEnum.SAME);
                        return updateTuneParam;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static List<TuneParamItem> merge4ReplaceParamItem(List<TuneParamItem> originTuneParamItems,
                                                              List<TuneParamItem> updatedTuneParamItems){
        return originTuneParamItems.stream().map(item -> {
            try {
                TuneParamItem updatedTuneParamItem = findTuneParamItemByItemName(updatedTuneParamItems, item);
                if (updatedTuneParamItem == null || StringUtils.equals(updatedTuneParamItem.getCurrentTuneParam(), item.getOriginTuneParam())) {
                    return null;
                }
                updatedTuneParamItem.setOriginTuneParam(item.getOriginTuneParam());
                updatedTuneParamItem.setAttributeEnum(TuneParamAttributeEnum.REPLACE);
                return updatedTuneParamItem;
            } catch (Exception e) {
                log.error("replaceTuneParamItems occurs an error.", e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static List<TuneParamItem> merge4DeletedTuneParamItem(List<TuneParamItem> originTuneParamItems,
                                                                  List<TuneParamItem> updatedTuneParamItems){
        // merge删除的： origin有，update无
        List<TuneParamItem> collect = originTuneParamItems.stream().filter(
                item -> {
                    TuneParamItem deleteTuneParamItem = findTuneParamItemByItemName(updatedTuneParamItems, item);
                    if (deleteTuneParamItem != null){
                        return false;
                    }
                    return true;
                }).filter(Objects::nonNull).map(item -> {
                    item.setAttributeEnum(TuneParamAttributeEnum.DELETE);
                    return item;
        }).collect(Collectors.toList());
        return collect;
    }

    private static TuneParamItem findTuneParamItem(List<TuneParamItem> sourceItems, TuneParamItem targetItem) {
        if (CollectionUtils.isEmpty(sourceItems) || Objects.isNull(targetItem)) {
            return null;
        }
        return sourceItems.stream().filter(item -> checkTuneParamItemEquals(item, targetItem)).findFirst().orElse(null);
    }

    private static TuneParamItem findTuneParamItemByItemName(List<TuneParamItem> sourceItems,
                                                             TuneParamItem targetItem) {
        if (CollectionUtils.isEmpty(sourceItems) || Objects.isNull(targetItem)) {
            return null;
        }
        for (TuneParamItem item : sourceItems) {
            if (StringUtils.equals(item.getParamName(), targetItem.getParamName())) {
                return item;
            }
        }

        return null;
    }

    private static boolean checkTuneParamItemEquals(TuneParamItem item, TuneParamItem targetItem) {
        if (Objects.isNull(item) || Objects.isNull(targetItem)) {
            return false;
        }
        return StringUtils.equals(item.getParamName(), targetItem.getParamName());
    }

    private static TuneParamItem findSameTuneParamItem(List<TuneParamItem> defaultItems,
                                                       TuneParamItem updateTuneParam) {
        if (CollectionUtils.isEmpty(defaultItems) || Objects.isNull(updateTuneParam)) {
            return null;
        }
        TuneParamItem result = null;
        for (TuneParamItem item : defaultItems) {
            if (checkDefaultAndUpdatedTuneParamItemSame(updateTuneParam, item)) {
                result = item;
                break;
            }
        }
        if (Objects.isNull(result)) {
            return null;
        }
        result.setAttributeEnum(TuneParamAttributeEnum.SAME);
        result.setCurrentTuneParam(updateTuneParam.getCurrentTuneParam());
        return result;
    }

    /**
     * item1的originTuneParam与item2的currentTuneParam相比
     *
     * @param item1 updateTuneParam
     * @param item2 originTuneParam
     * @return
     */
    private static boolean checkDefaultAndUpdatedTuneParamItemSame(TuneParamItem item1,
                                                                   TuneParamItem item2) {
        if (Objects.isNull(item1) || Objects.isNull(item2)) {
            return false;
        }
        if (!StringUtils.equals(item1.getParamName(), item2.getParamName())) {
            return false;
        }
        return StringUtils.equals(item1.getCurrentTuneParam(), item2.getOriginTuneParam());
    }

    /**
     * 从TuneParamItem中提取paramName
     *
     * @param tuneParamItem
     * @return
     */
    public static String extractTuneParamName(TuneParamItem tuneParamItem) {
        if (tuneParamItem == null) {
            return null;
        }
        if (StringUtils.isNotBlank(tuneParamItem.getOriginTuneParam())) {
            return parse2paramName(tuneParamItem.getOriginTuneParam());
        }
        if (StringUtils.isNotBlank(tuneParamItem.getCurrentTuneParam())) {
            return parse2paramName(tuneParamItem.getCurrentTuneParam());
        }
        return null;

    }

    private static String parse2paramName(String rawJvmOption) {
        try {
            JVMParamEnum match = JVMParamEnum.match(rawJvmOption);
            TuneParamItem parse = (TuneParamItem) TuneParamParser.getTuneParamItemParser(
                JVMParamEnum.match(rawJvmOption)).parse(match, rawJvmOption);
            return parse != null ? parse.getParamName() : null;
        } catch (Exception e) {
            log.error("parser occurs an error.", e);
            return null;
        }
    }
}