package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.model.common.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class UserInfoRepositoryTest {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Test
    void findByAccessToken() {
    }

    @Test
    void findByAccountIdAndTenantCode() {
    }

    @Test
    void findFirstByTenantCode() {
    }

    @Test
    void save() {
    }

    @Test
    void findAll() {
        List<UserInfo> all = userInfoRepository.findAll();
        System.out.println(all.size());
    }
}