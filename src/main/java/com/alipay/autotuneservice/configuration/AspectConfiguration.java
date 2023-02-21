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

import com.alipay.autotuneservice.base.cache.LocalCache;
import com.alipay.autotuneservice.configuration.ResourcePermission.ResourceType;
import com.alipay.autotuneservice.dao.AppInfoRepository;
import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.dao.TunePlanRepository;
import com.alipay.autotuneservice.model.common.AppInfo;
import com.alipay.autotuneservice.model.exception.ResourceAccessForbiddenException;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.tune.TunePlan;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dutianze
 * @version AspectConfiguration.java, v 0.1 2022年05月27日 16:03 dutianze
 */
@Slf4j
@Aspect
@Component
public class AspectConfiguration {

    @Autowired
    private TunePipelineRepository     tunePipelineRepository;
    @Autowired
    private AppInfoRepository          appInfoRepository;
    @Autowired
    private TunePlanRepository         tunePlanRepository;
    @Autowired
    private LocalCache<Object, Object> localCache;

    private static final String RESOURCE_PERMISSION_PREFIX = "TMASTER_RESOURCE_PERMISSION_PREFIX";
    private static final String METHOD_CACHED_PREFIX       = "TMASTER_METHOD_CACHED_PREFIX";

    private final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    @Around("@annotation(ResourcePermission)")
    public Object permissionCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            boolean isValid = this.permissionCheckIsValid(joinPoint);
            if (!isValid) {
                throw new ResourceAccessForbiddenException();
            }
        } catch (Exception e) {
            log.error("permissionCheck error", e);
            throw new ResourceAccessForbiddenException();
        }
        return joinPoint.proceed();
    }

    private boolean permissionCheckIsValid(ProceedingJoinPoint joinPoint) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ResourcePermission resourcePermission = signature.getMethod().getAnnotation(ResourcePermission.class);
        String accessToken = UserUtil.getAccessToken();
        if (accessToken == null) {
            log.info("user accessToken is null, skip permission check");
            return true;
        }
        ResourceType type = resourcePermission.type();
        Map<String, Object> parameters = getParameters(joinPoint);
        String path = resourcePermission.path();
        Integer id = (Integer) parameters.get(path);

        switch (type) {
            case APP_ID:
                AppInfo appInfo = this.resourceCached(() -> appInfoRepository.findById(id), id);
                if (appInfo == null || UserUtil.permissionIsValid(accessToken, appInfo.getAccessToken())) {
                    return true;
                }
                break;
            case PIPELINE_ID: {
                TunePipeline tunePipeline = this.resourceCached(() -> tunePipelineRepository.findByPipelineId(id), id);
                if (tunePipeline == null || UserUtil.permissionIsValid(accessToken, tunePipeline.getAccessToken())) {
                    return true;
                }
                break;
            }
            case PLAN_ID: {
                TunePlan tunePlan = this.resourceCached(() -> tunePlanRepository.findTunePlanById(id), id);
                if (tunePlan == null || UserUtil.permissionIsValid(accessToken, tunePlan.getAccessToken())) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private Map<String, Object> getParameters(ProceedingJoinPoint joinPoint) throws Exception {
        Object[] parameters = joinPoint.getArgs();
        String[] parameterNames = parameterNameDiscoverer
                .getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        if (parameterNames == null) {
            return new HashMap<>();
        }
        Map<String, Object> parameterMap = new HashMap<>(parameterNames.length);
        for (int i = 0; i < parameterNames.length; ++i) {
            Object parameter = parameters[i];
            if (parameter != null && !ClassUtils.isPrimitiveOrWrapper(parameter.getClass())) {
                Field[] allFields = parameter.getClass().getDeclaredFields();
                for (Field field : allFields) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    parameterMap.put(parameterNames[i] + "." + field.getName(),
                            field.get(parameter));
                }
            } else {
                parameterMap.put(parameterNames[i], parameters[i]);
            }
        }
        return parameterMap;
    }

    @SuppressWarnings("unchecked")
    private <T> T resourceCached(Callable<T> function, Integer key) {
        try {
            T result = function.call();
            Object o = localCache.get(RESOURCE_PERMISSION_PREFIX + key);
            if (o != null) {
                return (T) o;
            }
            localCache.put(RESOURCE_PERMISSION_PREFIX + key, result, 10 * 60);
            return result;
        } catch (Exception e) {
            log.error("resourceCached error", e);
            return null;
        }
    }

    private String buildMethodCachedKey(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] parameters = joinPoint.getArgs();
        if (parameters == null) {
            parameters = new String[] {"void"};
        }
        return Stream.concat(Stream.of(METHOD_CACHED_PREFIX, method.getName()),
                        Arrays.stream(parameters)
                                .map(e -> ObjectUtils.defaultIfNull(e, "null"))
                                .map(Object::hashCode)
                                .map(String::valueOf))
                .collect(Collectors.joining("_"));
    }
}