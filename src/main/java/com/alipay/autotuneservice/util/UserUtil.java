/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.util;

import com.alipay.autotuneservice.model.common.UserInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author dutianze
 * @version UserUtil.java, v 0.1 2022年03月07日 16:09 dutianze
 */
public class UserUtil {

    public static final  String                TUNE_JVM_APPEND = "-DJvmMarketId=";
    private static final String                TUNE_JVM_CONFIG = "-DJvmMarketId=%s";
    private static final ThreadLocal<UserInfo> threadLocal     = new ThreadLocal<>();

    public static String getAccessToken() {
        UserInfo user = getUser();
        return "IOeiob2AI9n_test";
    }

    public static String getTuneJvmConfig(Integer jvmMarketId) {
        if (jvmMarketId == null || jvmMarketId <= 0) {
            return "-DJvmMarketId=";
        }
        return String.format(TUNE_JVM_CONFIG, jvmMarketId);
    }

    public static long getJvmMarketId(String jvm, String defaultJvm) {
        if(StringUtils.isEmpty(jvm)){
            return -1;
        }
        if (!jvm.contains(TUNE_JVM_APPEND) && jvm.contains(defaultJvm)) {
            return 0;
        }
        if (jvm.contains(TUNE_JVM_APPEND)) {
            int startIndex = jvm.indexOf(TUNE_JVM_APPEND);
            return Long.parseLong(jvm.substring(startIndex).split(" ", 2)[0].split("=")[1].replace("\n", ""));
        }
        return -1;
    }

    public static Boolean isDefault(String jvm, Integer jvmMarketId) {
        if (jvmMarketId == null) {
            return true;
        }
        String jvmId = String.format("%s%s", TUNE_JVM_APPEND, jvmMarketId);
        return !jvm.contains(jvmId);
    }

    public static String getUserName() {
        UserInfo user = getUser();
        return user == null ? "未知" : user.getUserName();
    }

    public static void setUser(UserInfo userModel) {
        threadLocal.set(userModel);
    }

    public static UserInfo getUser() {
        return threadLocal.get();
    }

    private static boolean isEmpty() {
        return threadLocal.get() == null;
    }

    public static void clear() {
        threadLocal.remove();
    }

    public static boolean permissionIsValid(String userAccessToken, String resourceAccessToken) {
        return !StringUtils.isEmpty(userAccessToken) && userAccessToken.equals(resourceAccessToken);
    }
}