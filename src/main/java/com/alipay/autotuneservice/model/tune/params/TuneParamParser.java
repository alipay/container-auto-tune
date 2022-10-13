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
package com.alipay.autotuneservice.model.tune.params;

import com.alipay.autotuneservice.controller.model.tuneparam.TuneParamItem;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import static com.alipay.autotuneservice.model.tune.params.JVMParamEnum.EQUAL_SIGN_TYPE_TYPE_PARAM;
import static com.alipay.autotuneservice.model.tune.params.JVMParamEnum.PLUS_SIGN_TYPE_TYPE_PARAM;
import static com.alipay.autotuneservice.model.tune.params.JVMParamEnum.X_TYPE_PARAM;

/**
 * @author huangkaifei
 * @version : TuneParamParser.java, v 0.1 2022年05月17日 10:19 PM huangkaifei Exp $
 */
public class TuneParamParser {
    // ------------------------------------------ JVM参数对应的解析器-------------------------------------------------
    private static final TuneParamParserFunc X_TYPE_PARSER               = new X_TYPE_PARSER();
    private static final TuneParamParserFunc X_TYPE_ITEM_PARSER          = new X_TYPE_ITEM_PARSER();
    private static final TuneParamParserFunc EQUAL_SIGN_TYPE_PARSER      = new EQUAL_SIGN_TYPE_PARSER();
    private static final TuneParamParserFunc EQUAL_SIGN_TYPE_ITEM_PARSER = new EQUAL_SIGN_TYPE_ITEM_PARSER();
    private static final TuneParamParserFunc PLUS_SIGN_TYPE_PARSER       = new PLUS_SIGN_TYPE_PARSER();
    private static final TuneParamParserFunc UNKNOWN_TYPE_PARSER         = new UNKNOWN_TYPE_PARSER();
    private static final TuneParamParserFunc PLUS_SIGN_TYPE_ITEM_PARSER  = new PLUS_SIGN_TYPE_ITEM_PARSER();
    private static final TuneParamParserFunc UNKNOWN_TYPE_ITEM_PARSER    = new UNKNOWN_TYPE_ITEM_PARSER();

    public static TuneParamParserFunc getParser(JVMParamEnum jvmParamEnum) {
        if (X_TYPE_PARAM.contains(jvmParamEnum)) {
            return X_TYPE_PARSER;
        }
        if (EQUAL_SIGN_TYPE_TYPE_PARAM.contains(jvmParamEnum)) {
            return EQUAL_SIGN_TYPE_PARSER;
        }
        if (PLUS_SIGN_TYPE_TYPE_PARAM.contains(jvmParamEnum)) {
            return PLUS_SIGN_TYPE_PARSER;
        }
        return UNKNOWN_TYPE_PARSER;
    }

    public static TuneParamParserFunc getTuneParamItemParser(JVMParamEnum jvmParamEnum) {
        if (X_TYPE_PARAM.contains(jvmParamEnum)) {
            return X_TYPE_ITEM_PARSER;
        }
        if (EQUAL_SIGN_TYPE_TYPE_PARAM.contains(jvmParamEnum)) {
            return EQUAL_SIGN_TYPE_ITEM_PARSER;
        }
        if (PLUS_SIGN_TYPE_TYPE_PARAM.contains(jvmParamEnum)) {
            return PLUS_SIGN_TYPE_ITEM_PARSER;
        }
        return UNKNOWN_TYPE_ITEM_PARSER;
    }

    static final class X_TYPE_PARSER implements
                                    TuneParamParserFunc<JVMParamEnum, String, TuneParamModel> {
        @Override
        public TuneParamModel parse(JVMParamEnum jvmParamEnum, String jvmOption) {
            if (StringUtils.isBlank(jvmOption)) {
                return null;
            }
            TuneParamModel tuneParam = new TuneParamModel();
            jvmOption = jvmOption.trim();
            tuneParam.setParamName(jvmOption.substring(0, 4));
            tuneParam.setParamVal(jvmOption.substring(4));
            tuneParam.setDesc(jvmParamEnum.getDesc());
            tuneParam.setOperator("");
            return tuneParam;
        }
    }

    static final class X_TYPE_ITEM_PARSER implements
                                         TuneParamParserFunc<JVMParamEnum, String, TuneParamItem> {
        @Override
        public TuneParamItem parse(JVMParamEnum jvmParamEnum, String jvmOption) {
            if (jvmParamEnum == JVMParamEnum.UNKNOWN || StringUtils.isBlank(jvmOption)) {
                return null;
            }
            TuneParamItem tuneParamItem = new TuneParamItem();
            jvmOption = jvmOption.trim();
            if (jvmOption.length() >= 4) {
                tuneParamItem.setParamName(jvmOption.substring(0, 4));
            }
            tuneParamItem.setCurrentTuneParam(jvmOption);
            tuneParamItem.setOriginTuneParam(jvmOption);
            return tuneParamItem;
        }
    }

    static final class EQUAL_SIGN_TYPE_PARSER
                                             implements
                                             TuneParamParserFunc<JVMParamEnum, String, TuneParamModel> {
        @Override
        public TuneParamModel parse(JVMParamEnum jvmParamEnum, String jvmOption) {
            if (StringUtils.isBlank(jvmOption)) {
                return null;
            }
            TuneParamModel tuneParam = new TuneParamModel();
            jvmOption = jvmOption.trim();
            String[] split = jvmOption.split("=");
            if (ArrayUtils.isEmpty(split) && split.length > 2) {
                return null;
            }
            tuneParam.setParamName(split[0]);
            tuneParam.setParamVal(split[1]);
            tuneParam.setDesc(jvmParamEnum.getDesc());
            tuneParam.setOperator("=");
            return tuneParam;
        }
    }

    static final class EQUAL_SIGN_TYPE_ITEM_PARSER
                                                  implements
                                                  TuneParamParserFunc<JVMParamEnum, String, TuneParamItem> {
        @Override
        public TuneParamItem parse(JVMParamEnum jvmParamEnum, String rawParam) {
            if (jvmParamEnum == JVMParamEnum.UNKNOWN || StringUtils.isBlank(rawParam)) {
                return null;
            }

            String[] split = rawParam.split("=");
            if (ArrayUtils.isEmpty(split) && split.length > 2) {
                return null;
            }
            TuneParamItem tuneParamItem = new TuneParamItem();
            tuneParamItem.setParamName(split[0]);
            tuneParamItem.setOriginTuneParam(rawParam.trim());
            tuneParamItem.setCurrentTuneParam(rawParam.trim());
            return tuneParamItem;
        }
    }

    static final class PLUS_SIGN_TYPE_PARSER
                                            implements
                                            TuneParamParserFunc<JVMParamEnum, String, TuneParamModel> {
        // -XX:+UseZenGC
        @Override
        public TuneParamModel parse(JVMParamEnum jvmParamEnum, String jvmOption) {
            if (StringUtils.isBlank(jvmOption)) {
                return null;
            }
            TuneParamModel tuneParam = new TuneParamModel();
            tuneParam.setParamName(jvmOption.trim());
            return tuneParam;
        }
    }

    static final class PLUS_SIGN_TYPE_ITEM_PARSER
                                                 implements
                                                 TuneParamParserFunc<JVMParamEnum, String, TuneParamItem> {
        // -XX:+UseZenGC
        @Override
        public TuneParamItem parse(JVMParamEnum jvmParamEnum, String jvmOption) {
            if (jvmParamEnum == JVMParamEnum.UNKNOWN || StringUtils.isBlank(jvmOption)) {
                return null;
            }
            TuneParamItem tuneParamItem = new TuneParamItem();
            tuneParamItem.setParamName(jvmOption.trim());
            tuneParamItem.setOriginTuneParam(jvmOption.trim());
            tuneParamItem.setCurrentTuneParam(jvmOption.trim());
            return tuneParamItem;
        }
    }

    static final class UNKNOWN_TYPE_PARSER implements
                                          TuneParamParserFunc<JVMParamEnum, String, TuneParamModel> {
        @Override
        public TuneParamModel parse(JVMParamEnum jvmParamEnum, String jvmOption) {
            return null;
        }
    }

    static final class UNKNOWN_TYPE_ITEM_PARSER
                                               implements
                                               TuneParamParserFunc<JVMParamEnum, String, TuneParamItem> {
        @Override
        public TuneParamItem parse(JVMParamEnum jvmParamEnum, String jvmOption) {
            TuneParamItem tuneParamItem = new TuneParamItem();
            tuneParamItem.setParamName(jvmOption.trim());
            tuneParamItem.setOriginTuneParam(jvmOption);
            tuneParamItem.setCurrentTuneParam(jvmOption);
            return tuneParamItem;
        }
    }
}