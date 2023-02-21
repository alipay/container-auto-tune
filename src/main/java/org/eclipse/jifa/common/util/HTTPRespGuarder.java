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
package org.eclipse.jifa.common.util;///********************************************************************************
// * Copyright (c) 2020 Contributors to the Eclipse Foundation
// *
// * See the NOTICE file(s) distributed with this work for additional
// * information regarding copyright ownership.
// *
// * This program and the accompanying materials are made available under the
// * terms of the Eclipse Public License 2.0 which is available at
// * http://www.eclipse.org/legal/epl-2.0
// *
// * SPDX-License-Identifier: EPL-2.0
// ********************************************************************************/
//package org.eclipse.jifa.common.util;
//
//import io.vertx.core.http.HttpMethod;
//import io.vertx.core.http.HttpServerResponse;
//import io.vertx.core.json.JsonObject;
//import io.vertx.ext.web.RoutingContext;
//import io.vertx.serviceproxy.ServiceException;
//import org.apache.logging.log4j.util.Strings;
//import org.eclipse.jifa.common.Constant;
//import org.eclipse.jifa.common.ErrorCode;
//import org.eclipse.jifa.common.JifaException;
//import org.eclipse.jifa.common.vo.ErrorResult;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.InvocationTargetException;
//
//import static org.eclipse.jifa.common.util.GsonHolder.GSON;
//
//public class HTTPRespGuarder implements Constant {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPRespGuarder.class);
//
//    public static void ok(io.vertx.reactivex.ext.web.RoutingContext context) {
//        ok(context.getDelegate());
//    }
//
//    public static void ok(io.vertx.reactivex.ext.web.RoutingContext context, int statusCode, Object content) {
//        ok(context.getDelegate(), statusCode, content);
//    }
//
//    public static void ok(io.vertx.reactivex.ext.web.RoutingContext context, Object content) {
//        ok(context.getDelegate(), content);
//    }
//
//    public static void fail(io.vertx.reactivex.ext.web.RoutingContext context, Throwable t) {
//        fail(context.getDelegate(), t);
//    }
//
//    public static void ok(RoutingContext context) {
//        ok(context, commonStatusCodeOf(context.request().method()), null);
//    }
//
//    public static void ok(RoutingContext context, Object content) {
//        ok(context, commonStatusCodeOf(context.request().method()), content);
//    }
//
//    public static void ok(RoutingContext context, int statusCode, Object content) {
//        HttpServerResponse response = context.response();
//        response.putHeader(Constant.HEADER_CONTENT_TYPE_KEY, Constant.CONTENT_TYPE_JSON_FORM).setStatusCode(statusCode);
//        if (content != null) {
//            response.end((content instanceof String) ? (String) content : GSON.toJson(content));
//        } else {
//            response.end();
//        }
//    }
//
//    public static void fail(RoutingContext context, Throwable t) {
//        if (t instanceof InvocationTargetException && t.getCause() != null) {
//            t = t.getCause();
//        }
//        int statusCode = context.statusCode();
//        String resp = Strings.EMPTY;
//        if (statusCode != Constant.HTTP_UNAUTHORIZED) {
//            log(t);
//            // adjust the status code
//            statusCode = statusCodeOf(t);
//            resp = GSON.toJson(new ErrorResult(t));
//        }
//        context.response()
//               .putHeader(Constant.HEADER_CONTENT_TYPE_KEY, Constant.CONTENT_TYPE_JSON_FORM)
//               .setStatusCode(statusCode)
//               .end(resp);
//    }
//
//    private static int statusCodeOf(Throwable t) {
//        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
//        if (t instanceof JifaException) {
//            errorCode = ((JifaException) t).getCode();
//        }
//
//        if (t instanceof IllegalArgumentException) {
//            errorCode = ErrorCode.ILLEGAL_ARGUMENT;
//        }
//
//        if (errorCode == ErrorCode.ILLEGAL_ARGUMENT) {
//            return HTTP_BAD_REQUEST_STATUS_CODE;
//        }
//
//        if (errorCode == ErrorCode.FORBIDDEN) {
//            return HTTP_FORBIDDEN_STATUS_CODE;
//        }
//
//        return HTTP_INTERNAL_SERVER_ERROR_STATUS_CODE;
//    }
//
//    private static void log(Throwable t) {
//        boolean shouldLogError = true;
//
//        if (t instanceof JifaException) {
//            shouldLogError = ((JifaException) t).getCode().isFatal();
//        } else if (t instanceof IllegalArgumentException) {
//            shouldLogError = false;
//        }
//
//        if ( t instanceof  ServiceException) {
//            // FIXME: should we use ServiceException.failureCode?
//            ServiceException se = (ServiceException) t;
//            shouldLogError = se.failureCode() != ErrorCode.RETRY.ordinal();
//            LOGGER.debug("Starting worker for target {}", se.getMessage());
//        }
//
//        if (shouldLogError) {
//            LOGGER.error("Handle http request failed", t);
//        }
//
//        if (t instanceof ServiceException) {
//            JsonObject di = ((ServiceException) t).getDebugInfo();
//            if (di != null) {
//                LOGGER.error(di.toString());
//            }
//        }
//    }
//
//    private static int commonStatusCodeOf(HttpMethod method) {
//        if (method == HttpMethod.POST) {
//            return Constant.HTTP_POST_CREATED_STATUS_CODE;
//        }
//        return Constant.HTTP_GET_OK_STATUS_CODE;
//    }
//}
