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

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author huangkaifei
 * @version : HttpUtil.java, v 0.1 2022年04月11日 2:58 PM huangkaifei Exp $
 */
@Slf4j
public class HttpUtil {

    public static String buildInnerUrl(String path, Map<String, String> parameters) {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https");
        builder.setHost(getHostUrlWithoutSchema(SystemUtil.getDomainUrl()));
        builder.setPath(path);
        parameters.forEach(builder::addParameter);
        try {
            return builder.build().toURL().toString();
        } catch (Exception e) {
            throw new ServerException(ResultCode.BUILD_URL_ERROR);
        }
    }

    public static String getHostUrlWithoutSchema(String url) {
        if (StringUtils.contains(url, "//")) {
            return url.split("//")[1];
        }
        return "";
    }

    public static String buildUrl(String path, Map<String, String> parameters) {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https");
        builder.setHost(getHostUrlWithoutSchema(SystemUtil.getWebUrl()));
        builder.setPath(path);
        parameters.forEach(builder::addParameter);
        try {
            return builder.build().toURL().toString();
        } catch (Exception e) {
            throw new ServerException(ResultCode.BUILD_URL_ERROR);
        }
    }

    public static URI makeRequestUrl(String url, Map<String, String> condition)
            throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);

        condition.forEach(uriBuilder::addParameter);
        return uriBuilder.build();
    }

    public static String callGetApi(URI url, int timeOut, Header... headers) throws IOException {
        log.info("callGetApi start. url={}", url);
        return Request.Get(url).connectTimeout(timeOut).socketTimeout(timeOut).setHeaders(headers)
            .execute().returnContent().asString(Consts.UTF_8);
    }

    public static String callPostRequest(URI uri, int timeOut, Map<String, Object> map,
                                         Header... headers) {
        try {
            return callPostRequest(uri, timeOut, JSON.toJSONString(map), headers);
        } catch (Exception e) {
            log.error("callPostRequest occurs an error.", e);
            return null;
        }
    }

    public static String callPostRequest(URI uri, int timeOut, String payload, Header... headers) {
        try {
            log.info("callPostRequest start. uri={}", uri);
            HttpClient httpClient = HttpClientBuilder.create().build();
            StringEntity stringEntity = new StringEntity(payload, "UTF-8");
            HttpPost request = new HttpPost(uri);
            request.addHeader("Content-Type", "application/json");
            request.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(request);
            if (response == null) {
                log.error("callPostRequest response is null.");
                return null;
            }
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                log.info("callPostRequest success.");
                try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
                    response.getEntity().writeTo(out);
                    return out.toString();
                } catch (Exception e) {
                    log.error("callPostRequest write response occurs an error.", e);
                }

            }
            return null;
        } catch (IOException e) {
            log.error("callPostRequest occurs an error.", e);
            return null;
        }
    }

}
