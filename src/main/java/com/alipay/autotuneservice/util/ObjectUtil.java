/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.util;


import com.alipay.autotuneservice.infrastructure.saas.common.cache.AbsLockAction;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.google.common.base.Preconditions;
import org.redisson.api.RLock;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    public static void tryLock(RedisClient redisClient, String key, int waitTime, AbsLockAction lockAction) throws InterruptedException {
        RLock lock = redisClient.getLock(key);
        boolean tryLock = lock.tryLock((long) waitTime, TimeUnit.SECONDS);
        if (tryLock) {
            try {
                lockAction.onAcquire(key);
                lockAction.doInLock(key);
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    lockAction.onExit(key);
                }
            }
        } else {
            lockAction.tryLockFail(key);
        }
    }
}