/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.controller.model.K8sAccessTokenModel;
import com.alipay.autotuneservice.dao.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.UserInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.alipay.autotuneservice.infrastructure.rpc.model.TenantItem;
import com.alipay.autotuneservice.infrastructure.rpc.model.UserInfoBasic;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.AbsLockAction;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.ResultCode;
import com.alipay.autotuneservice.model.common.UserInfo;
import com.alipay.autotuneservice.model.exception.ClientException;
import com.alipay.autotuneservice.service.UserInfoService;
import com.alipay.autotuneservice.util.ConvertUtils;
import com.alipay.autotuneservice.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author dutianze
 * @version UserInfoServiceImpl.java, v 0.1 2022年03月07日 20:45 dutianze
 */
@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private K8sAccessTokenInfo k8sAccessTokenInfo;
    @Autowired
    private RedisClient        redisClient;

    private static final String USER_REGISTER_KEY = "autotune_user_register_lock_";

    @Override
    public UserInfo registerByAccountId(@Nullable String tenantCode, UserInfoBasic user) throws InterruptedException {
        // find tenant
        Optional<TenantItem> tenantItemOptional;
        if (StringUtils.isEmpty(tenantCode)) {
            tenantItemOptional = Optional.ofNullable(user.getTenantItems().get(0));
        } else {
            tenantItemOptional = user.getTenantItems()
                    .stream().filter(item -> tenantCode.equals(item.getTenantCode())).findFirst();
        }
        TenantItem tenantItem = tenantItemOptional.orElseThrow(
                () -> new ClientException(ResultCode.UNAUTHORIZED, "Not Fount Tenant Code " + tenantCode));
        // find user
        UserInfo userModel = userInfoRepository.findByAccountIdAndTenantCode(user.getUserEmail(), tenantItem.getTenantCode());
        if (userModel != null) {
            return userModel;
        }
        // register
        AtomicReference<UserInfo> userInfoAtomicReference = new AtomicReference<>();
        ObjectUtil.tryLock(redisClient, USER_REGISTER_KEY + user.getUserEmail(), 10, new AbsLockAction() {
            @Override
            public void doInLock(String resourceName) {
                UserInfo userModel = userInfoRepository.findByAccountIdAndTenantCode(user.getUserEmail(), tenantItem.getTenantCode());
                if (userModel != null) {
                    userInfoAtomicReference.set(userModel);
                    return;
                }
                UserInfo userInfo = new UserInfo(user.getUserEmail(), tenantItem.getTenantCode(),
                        tenantItem.getProductAccountId(), tenantItem.getPlanCode());
                UserInfo sameTenantCode = userInfoRepository.findFirstByTenantCode(tenantItem.getTenantCode());
                if (sameTenantCode != null) {
                    userInfo.setAccessToken(sameTenantCode.getAccessToken());
                } else {
                    userInfo.generateAccessToken();
                }
                userInfoAtomicReference.set(userInfoRepository.save(userInfo));
            }

            @Override
            public void tryLockFail(String resourceName) {
                log.info("registerByAccountId tryLockFail, user:{}", user);
            }
        });
        UserInfo userInfo = userInfoAtomicReference.get();
        if (userInfo == null) {
            throw new ClientException(ResultCode.UNAUTHORIZED, "User login fail");
        }
        return userInfo;
    }

    @Override
    public boolean saveAccessTokenInfo(K8sAccessTokenModel k8sAccessTokenModel) {
        K8sAccessTokenInfoRecord record = ConvertUtils.convert2K8sAccessTokenInfoRecord(k8sAccessTokenModel);
        return k8sAccessTokenInfo.insertOrUpdate(record);
    }

    @Override
    public boolean checkTokenValidity(String accessToken) {
        UserInfo userInfo = userInfoRepository.findByAccessToken(accessToken);
        return userInfo != null && userInfo.isValid();
    }

    @Override
    public K8sAccessTokenModel getK8sTokeInfo(String accessToken, String clusterName) {
        K8sAccessTokenInfoRecord record = k8sAccessTokenInfo.selectByTokenAndCusterName(accessToken, clusterName);
        return ConvertUtils.convert2K8sAccessTokenModel(record);
    }
}