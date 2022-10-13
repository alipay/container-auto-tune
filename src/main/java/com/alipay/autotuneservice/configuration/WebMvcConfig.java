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

import com.alipay.autotuneservice.infrastructure.rpc.SaasFactoryClient;
import com.alipay.autotuneservice.infrastructure.rpc.model.AccountResponse;
import com.alipay.autotuneservice.infrastructure.rpc.model.UserInfoBasic;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.model.common.UserInfo;
import com.alipay.autotuneservice.model.exception.ClientException;
import com.alipay.autotuneservice.service.UserInfoService;
import com.alipay.autotuneservice.util.GsonUtil;
import com.alipay.autotuneservice.util.TraceIdGenerator;
import com.alipay.autotuneservice.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author dutianze
 * @version WebMvcConfig.java, v 0.1 2022年03月07日 14:49 dutianze
 */
@Slf4j
@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserInfoService     userService;
    @Autowired
    private SaasFactoryClient   accountAuthClient;
    @Autowired
    private RedisClient         redisClient;
    @Value("${application.productCode}")
    private String              productCode;
    @Autowired
    private ConstantsProperties constantProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor()).excludePathPatterns("")
            .excludePathPatterns("/static/**").addPathPatterns("/api/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
            "classpath:/META-INF/resources/webjars/");
    }

    private class SecurityInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                 Object handler) throws IOException {
            TraceIdGenerator.generateAndSet();
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            NoLogin noLoginInMethod = method.getAnnotation(NoLogin.class);
            NoLogin noLoginInClass = method.getDeclaringClass().getAnnotation(NoLogin.class);
            if (noLoginInMethod != null || noLoginInClass != null) {
                return true;
            }
            try {
                // cookie
                CookieCache cookieCache = new CookieCache(request);
                Optional<String> authTokenOptional = cookieCache.get(CookieCache.TOKEN_KEY);
                Optional<String> tenantOptional = cookieCache.get(CookieCache.LOGIN_TENANT);
                // check
                if (!authTokenOptional.isPresent()) {
                    throw new ClientException(ResultCode.UNAUTHORIZED);
                }

                // use cache
                UserInfo userInfoCached = redisClient.get(buildCacheKey(authTokenOptional.get()),
                    UserInfo.class);
                if (userInfoCached != null
                    && userInfoCached.tenantCodeIsValid(tenantOptional.orElse(""))) {
                    UserUtil.setUser(userInfoCached);
                    return true;
                }

                // login or else register 
                AccountResponse<UserInfoBasic> accountResponse = accountAuthClient
                    .getUserInfoBasic(authTokenOptional.get(), productCode);
                if (!accountResponse.isSuccess()) {
                    throw new ClientException(ResultCode.UNAUTHORIZED);
                }
                UserInfo userInfo = userService.registerByAccountId(tenantOptional.orElse(null),
                    accountResponse.getData());

                // set cache
                redisClient.setEx(buildCacheKey(authTokenOptional.get()), userInfo, 10,
                    TimeUnit.MINUTES);
                UserUtil.setUser(userInfo);
                return true;
            } catch (ClientException e) {
                log.warn("auth fail", e);
            } catch (Exception e) {
                log.error("getUserInfo error", e);
            }
            // delete cookie
            Cookie cookie = new Cookie(CookieCache.TOKEN_KEY, "");
            cookie.setDomain(constantProperties.getSaasTenantUrl());
            //cookie.setDomain("anttrmstest.com");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            // redirect to login
            response.setContentType(constantProperties.getSaasTenantUrl());
            response.getWriter().write(
                GsonUtil.toJson(ServiceBaseResult.failureResult(HttpStatus.UNAUTHORIZED.value(),
                    "need login")));
            return false;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                    Object handler, Exception ex) {
            TraceIdGenerator.clear();
            UserUtil.clear();
        }
    }

    private String buildCacheKey(String token) {
        return "tmaster_authToken_" + token;
    }

    private static class CookieCache {

        private static final String TOKEN_KEY    = "authToken";
        private static final String LOGIN_TENANT = "loginTenant";

        Map<String, String>         cookiesMap   = new LinkedHashMap<>();

        public CookieCache(HttpServletRequest request) {
            Cookie[] cookies = request.getCookies();
            if (ArrayUtils.isEmpty(cookies)) {
                return;
            }
            cookiesMap = Arrays.stream(cookies)
                    .collect(Collectors.toMap(Cookie::getName, Cookie::getValue, (e, n) -> e));
        }

        private Optional<String> get(String cookieName) {
            return Optional.ofNullable(cookiesMap.get(cookieName));
        }
    }
}