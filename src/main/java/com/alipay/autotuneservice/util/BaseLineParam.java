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

import com.alipay.autotuneservice.model.common.JvmParamTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * /**
 * * 删除和增加均按照此规则:
 * * <k,v> -->
 * *     1.参数有"=",按照'='划分，key 为左节点、value为右节点;
 * *     2.没有"="参数直接放在 key
 * *     例如 VM_8G=-Xms5600m -Xmx5600m -Xmn2100m -Xss512k -XX:PermSize=256m -XX:MaxPermSize=256m
 * *     删除-XX:PermSize=256m -XX:MaxPermSize=256m,增加-XX:MetaspaceSize=340m -XX:MaxMetaspaceSize=340m
 * *     线上调用给的时候 origin 不用 set;
 * *     configKey processType configParam 必须有,否则后果自负;
 * *
 * *
 * //* *     伪代码:
 * //* *     BaseLineParam baseLineParam = new BaseLineParam();
 * //* *         BaseLineParam.Param param1 = new Param();
 * //* *         BaseLineParam.Param param2 = new Param();
 * //* *         param1.setConfigKey("VM_8G");
 * //* *         param1.setConfigParam(new HashMap<String, String>() {
 * //* *             {
 * //* *                 put("-XX:PermSize", "");
 * //* *                 put("-XX:MaxPermSize", "");
 * //* *             }
 * //* *         });
 * //* *         param1.setOrigin("-Xms5600m -Xmx5600m -Xmn2100m -Xss512k -XX:PermSize=256m -XX:MaxPermSize=256m");
 * //* *         param1.setProcessType(ProcessType.DELETE);
 * //* *
 * //* *         param2.setConfigKey("VM_8G");
 * //* *         param2.setConfigParam(new HashMap<String, String>() {
 * //* *             {
 * //* *                 put("-XX:MetaspaceSize=340m", "");
 * //* *                 put("-XX:MaxMetaspaceSize=340m","");
 * //* *             }
 * //* *         });
 * //* *
 * //* *         param2.setOrigin("-Xms5600m -Xmx5600m -Xmn2100m -Xss512k -XX:PermSize=256m -XX:MaxPermSize=256m");
 * //* *         param2.setProcessType(ProcessType.ADD);
 * //* *         List<Param> params = new ArrayList<>();
 * //* *         params.add(param1);
 * //* *         params.add(param2);
 * //* *         baseLineParam.setParams(params);
 * //* *         System.out.println(baseLineParam.buildParam());
 * *
 *
 * @author chenqu
 * @version : BaseLineParam, v0.1 2022年04月29日 11:00 上午
 */
@Data
@Slf4j
public class BaseLineParam {
    //一个环境下不同 configkey 的 param(模板信息)
    private List<Param> params = Lists.newLinkedList();

    @Data
    public static class Param {

        private String              configKey;
        private String              origin;
        private ProcessType         processType;
        private Map<String, String> configParam = Maps.newLinkedHashMap();

        public enum ProcessType {
            ADD,
            DELETE;
        }
    }

    /**
     * @return
     */
    public Map<String, String> buildParam() {
        Map<String, String> result = Maps.newLinkedHashMap();
        params.forEach(param -> {
            result.entrySet().forEach(entry -> {
                if (entry.getKey().equals(param.getConfigKey())) {
                    param.setOrigin(entry.getValue());
                }
            });
            Set<String> configParamKetSet = param.configParam.keySet();
            Set<String> tmp = Sets.newHashSet(configParamKetSet);
            Map<String, String> templeModel = Maps.newLinkedHashMap();
            Arrays.stream(StringUtils.split(param.origin, " "))
                    .map(StringUtils::trim)
                    .collect(Collectors.toList())
                    .stream()
                    .forEach(str -> {
                        Optional<String> optional = configParamKetSet.stream().filter(cpk -> StringUtils.contains(str, cpk)).findFirst();
                        if (optional.isPresent()) {
                            String key = optional.get();
                            templeModel.put(key, param.configParam.get(key));
                            param.configParam.remove(key);
                            return;
                        }
                        String array[] = str.split("=", 2);
                        if (array.length > 1 && StringUtils.isNotEmpty(array[1])) {
                            templeModel.put(array[0], array[1]);
                            return;
                        }
                        if (StringUtils.contains(str, "=")) {
                            templeModel.put(str, "");
                            return;
                        }
                        templeModel.put(array[0], "");
                    });
            if (MapUtils.isNotEmpty(param.configParam)) {
                param.configParam.forEach((k, v) -> templeModel.put(k, v));
            }
            //返回聚合
            List<String> data = Lists.newLinkedList();
            templeModel.forEach((k, v) -> {
                if (Param.ProcessType.DELETE == param.processType) {
                    if (tmp.contains(k)) {
                        return;
                    }
                }
                if (StringUtils.isEmpty(v) || StringUtils.equals("no-value", v)) {
                    data.add(k);
                    return;
                }
                String append = "=";
                JvmParamTypeEnum typeEnum = JvmParamTypeEnum.getInstance(k);
                if (typeEnum != null) {
                    append = typeEnum.getAppend();
                }
                data.add(v.isEmpty() ? String.format("%s", k) : String.format("%s%s%s", k, append, v));
            });
            result.put(param.getConfigKey(), String.join(" ", data));
        });
        return result;
    }

    public static void main(String[] args) {
        BaseLineParam baseLineParam = new BaseLineParam();
        Param param = new Param();
        param.setConfigKey("java_opts_base");
        param.setConfigParam(new HashMap<String, String>() {
            {
                put("-Drpc_pool_queue_size_tr",
                        "200");
                put("-Dsun.rmi.dgc.server.gcInterval",
                        "2592000001");
                put("-Xms", "250m");
                put("-Xmn", "150m");
                put("-XX:CMSInitiatingOccupancyFraction", "75");
            }
        });
        param.setOrigin(
                "-server -XX:+UseStringCache -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSScavengeBeforeRemark "
                        + "-XX:+CMSClassUnloadingEnabled -verbose:gc "
                        + "-XX:+UseCMSInitiatingOccupancyOnly -XX:+ExplicitGCInvokesConcurrent -Xloggc:/home/admin/logs/gc.log "
                        + "-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Dsun.rmi.dgc.server.gcInterval=2592000000 -Dsun.rmi.dgc.client"
                        + ".gcInterval=2592000000 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/admin/logs "
                        + "-XX:ErrorFile=/home/admin/logs/hs_err_pid%p.log -Dfile.encoding=UTF-8 -Ddbmode=$DBMODE -Dcom.alipay.ldc"
                        + ".zone=$ZONE -Dcom.alipay.confreg.url=$CONFREG_URL -Drpc_pool_queue_size_tr=10 -Xms500m -Xmx500m -Xmn200m");

        param.setProcessType(Param.ProcessType.ADD);
        List<Param> params = new ArrayList<>();
        params.add(param);
        baseLineParam.setParams(params);
        System.out.println(baseLineParam.buildParam());
    }
}
