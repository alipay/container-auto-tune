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

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.eclipse.jifa.common.enums.FileType;

public interface JifaHooks {
    /* Access the server configuration at startup. This will be called by JIFA and configuration passed in.
       This will be called once for each verticle. */
    default void init(JsonObject config) {
    }

    /* Provide custom http server configuration to vertx. */
    default HttpServerOptions serverOptions(Vertx vertx) {
        return new HttpServerOptions();
    }

    /* Access the route configuration before JIFA routes are loaded.
       You could use this to customize redirects, authenticate, etc. */
    default void beforeRoutes(Vertx vertx, Router router) {
    }

    /* Access route configuration after JIFA routes are loaded.
       You could use this to customize error handling, etc. */
    default void afterRoutes(Vertx vertx, Router router) {
    }

    /* Provide custom mapping for directory path, file, and index functionality. */
    default String mapDirPath(FileType fileType, String name, String defaultPath) {
        return defaultPath;
    }

    default String mapFilePath(FileType fileType, String name, String childrenName, String defaultPath) {
        return defaultPath;
    }

    default String mapIndexPath(FileType fileType, String file, String defaultPath) {
        return defaultPath;
    }

    /* An empty default configuration */
    public class EmptyHooks implements JifaHooks {
        // use default implementations
    }
}
