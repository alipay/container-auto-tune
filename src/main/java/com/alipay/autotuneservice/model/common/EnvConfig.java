/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.model.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author huangkaifei
 * @version : EnvConfig.java, v 0.1 2022年08月19日 5:49 PM huangkaifei Exp $
 */
public class EnvConfig {

    // ******* Mongo ********* //
    public static final String MONGO_DOMAIN = System.getenv("MONGO_DOMAIN");
    public static final String MONGO_USERNAME = System.getenv("MONGO_USERNAME");
    public static final String MONGO_PASSWORD = System.getenv("MONGO_PASSWORD");
    public static final String MONGO_DATABASE = System.getenv("MONGO_DATABASE");

    // ******* redis ********* //
    public static final String REDIS_HOST = System.getenv("REDIS_HOST");


    public static String buildConfigCacheKey(){
        String key = String.format("MONGO_%s_%S", MONGO_USERNAME, MONGO_PASSWORD);
        return Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
    }
}