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
package com.alipay.autotuneservice.service.algorithmlab.template;

import com.alipay.autotuneservice.model.tune.params.JVMParamEnum;
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import com.alipay.autotuneservice.service.algorithmlab.TuneParamModel;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alipay.autotuneservice.service.algorithmlab.template.TemplateEssentialParams.MEM_SPECS;
import static com.alipay.autotuneservice.service.algorithmlab.template.TemplateEssentialParams.TEMP_ESSENTIA_VALUES;
import static com.alipay.autotuneservice.service.algorithmlab.template.TemplateEssentialParams.TEMP_ESSENTIA_VALUES_BASE;

/**
 * @author hongshu
 * @version TuneLab.java, v 0.1 2022年11月17日 21:15 hongshu
 */
public class TemplateLab {
    /**
     * 根据配置获取必须参数
     * @param garbageCollector
     * @param memCapacity
     * @return
     */
   public static Map<JVMParamEnum, TuneParamModel> buildEssParams(GarbageCollector garbageCollector, int memCapacity){
       Map<String, TuneParamModel> paramModelMap = new HashMap<>(TEMP_ESSENTIA_VALUES_BASE.get(garbageCollector));
       int key = findSpec(memCapacity);
       if(key>0){
           paramModelMap.putAll(TEMP_ESSENTIA_VALUES.get(garbageCollector).keySet().stream().filter(r -> r.startsWith(key+"_"))
                   .map(r -> TEMP_ESSENTIA_VALUES.get(garbageCollector).get(r))
                   .collect(Collectors.toMap(TuneParamModel::getParamName, v -> v, (e, u) -> e)));
       }
       return paramModelMap.keySet().stream().collect(Collectors.toMap(JVMParamEnum::match, paramModelMap::get,(e, u) -> e));
   }


    private static int findSpec(int memCapacity) {
       int size = MEM_SPECS.size();
       if(memCapacity<MEM_SPECS.get(0) || memCapacity>=2*MEM_SPECS.get(size-1)){
           return -1;
       }else if(memCapacity>=MEM_SPECS.get(size-1) && memCapacity<2*MEM_SPECS.get(size-1)){
           return MEM_SPECS.get(size-1);
       }else {
           for(int i=0; i<size; i++){
               if(memCapacity<MEM_SPECS.get(i)){
                   return MEM_SPECS.get(i-1);
               }
           }
       }
       return -1;
    }
}
