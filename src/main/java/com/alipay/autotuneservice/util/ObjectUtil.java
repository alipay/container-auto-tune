/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.util;


import com.google.common.base.Preconditions;

import java.util.UUID;

/**
 * @author huangkaifei
 * @version : ObjectUtil.java, v 0.1 2022年05月20日 11:18 PM huangkaifei Exp $
 */
public final class ObjectUtil {

    public static Integer checkIntegerPositive(final Integer num) {
        Preconditions.checkArgument(num != null && num > 0, "Input integer number must be positive.");
        return num;
    }

    public static Integer checkIntegerPositive(final Integer num, String errMsg) {
        Preconditions.checkArgument(num != null && num > 0, errMsg);
        return num;
    }

    public static Boolean checkInteger(Integer num) {
        try {
            Preconditions.checkArgument(num != null && num > 0, "Input integer number must be positive.");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}