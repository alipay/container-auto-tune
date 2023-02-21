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
package com.alipay.autotuneservice.model;

/**
 * @author huangkaifei
 * @version : ArthasHtmlType.java, v 0.1 2022年12月12日 3:45 PM huangkaifei Exp $
 */
public enum ArthasHtmlType {
    SHOW("arthas-show.flt", "arthas-show.html"),
    TERMINAL("arthas-terminal.flt", "arthas-terminal.html"),
    ERROR("arthas-error.flt", "arthas-error.html");;

    private String htmlTemplateName;
    private String htmlFileName;

    ArthasHtmlType(String htmlTemplateName, String htmlFileName) {
        this.htmlTemplateName = htmlTemplateName;
        this.htmlFileName = htmlFileName;
    }

    public String getHtmlTemplateName() {
        return htmlTemplateName;
    }

    public String getHtmlFileName() {
        return htmlFileName;
    }
}