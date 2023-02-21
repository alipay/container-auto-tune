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
package org.eclipse.jifa.common;

import java.util.concurrent.TimeUnit;

public interface Constant {
    String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    String HEADER_CONTENT_LENGTH_KEY = "Content-Length";
    String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    String HEADER_AUTHORIZATION = "authorization";
    String CONTENT_TYPE_URL_FORM = "application/x-www-form-urlencoded; charset=UTF-8";
    String CONTENT_TYPE_JSON_FORM = "application/json; charset=UTF-8";
    String CONTENT_TYPE_FILE_FORM = "application/octet-stream";

    String COOKIE_AUTHORIZATION = "grace-authorization";
    String HEADER_AUTHORIZATION_PREFIX = "Bearer ";

    int HTTP_GET_OK_STATUS_CODE = 200;
    int HTTP_POST_CREATED_STATUS_CODE = 201;

    int HTTP_BAD_REQUEST_STATUS_CODE = 400;
    int HTTP_FORBIDDEN_STATUS_CODE = 403;
    int HTTP_UNAUTHORIZED = 401;
    int HTTP_POST_CREATED_STATUS = 201;
    int HTTP_INTERNAL_SERVER_ERROR_STATUS_CODE = 500;

    String LINE_SEPARATOR = System.lineSeparator();

    String EMPTY_STRING = "";

    String FILE_TYPE = "type";
    String PAGE = "page";
    String PAGE_SIZE = "pageSize";

    String UNKNOWN_STRING = "UNKNOWN";
    String DEFAULT_WORKSPACE = System.getProperty("user.home") + java.io.File.separator + "grace_workspace";

    long STALE_THRESHOLD = TimeUnit.HOURS.toMillis(6);
}
