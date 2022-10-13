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
package com.alipay.autotuneservice.configuration;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ServiceLoader;

/**
 * @author chenqu
 * @version : LoadServiceManager.java, v 0.1 2022年07月13日 14:23 chenqu Exp $
 */
public class LoadServiceManager {

    private static final String                         LOAD_TYPE_ENV = "cloudType";
    private static final HashMap<String, ServiceLoader> CLASSES       = Maps.newHashMap();
    private static LinkedHashMap<String, Object>        JCONFIG       = Maps.newLinkedHashMap();

    static {
        //读入文件
        try {
            Yaml yaml = new Yaml();
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("application.yml");
            JCONFIG = yaml.load(resourceAsStream);
        } catch (Exception e) {
            //do noting
        }
    }

    public synchronized static <T> T load(Class<T> clazz) {
        try {
            String className = clazz.getName();
            if (CLASSES.containsKey(className)) {
                return (T) compile(CLASSES.get(className));
            }
            ServiceLoader loader = ServiceLoader.load(clazz);
            CLASSES.put(className, loader);
            //根据clazz获取目标工厂
            return (T) compile(loader);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object compile(ServiceLoader serviceLoader) {
        String envProperty = getEnv();
        if (StringUtils.isEmpty(envProperty)) {
            envProperty = System.getenv(LOAD_TYPE_ENV);
        }
        if (StringUtils.isEmpty(envProperty)) {
            //获取resource配置
            envProperty = System.getProperty(LOAD_TYPE_ENV);
        }
        for (Object loader : serviceLoader) {
            if (StringUtils.isEmpty(envProperty)) {
                return loader;
            }
            InjectLoader injectLoader = loader.getClass().getAnnotation(InjectLoader.class);
            if (StringUtils.contains(envProperty.toLowerCase(), injectLoader.loadType()
                .toLowerCase())) {
                return loader;
            }
        }
        throw new RuntimeException("not found service loader");
    }

    private static String getEnv() {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("jconfig", JCONFIG);
        return parser.parseExpression("#jconfig['spring']['profiles']['active']").getValue(context,
            String.class);
    }
}