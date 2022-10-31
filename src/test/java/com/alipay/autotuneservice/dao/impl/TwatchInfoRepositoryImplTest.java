package com.alipay.autotuneservice.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.TwatchInfoRepository;
import com.alipay.autotuneservice.dynamodb.bean.TwatchInfoDo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TwatchInfoRepositoryImplTest {

    @Autowired
    private TwatchInfoRepository twatchInfoRepository;

    //@Test
    void insert() {
        TwatchInfoDo twatchInfoDo = build();
        twatchInfoRepository.insert(twatchInfoDo);
    }

    private TwatchInfoDo build(){
        String str = "{\n"
                + "  \"containerId\": \"xxxx\",\n"
                + "  \"nameSpace\": \"tmaster\",\n"
                + "  \"containerName\": \"test-container\",\n"
                + "  \"agentName\": \"test-agent\",\n"
                + "  \"gmtModified\": 1667213287856,\n"
                + "  \"podName\": \"test=pod\",\n"
                + "  \"dtPeriod\": 1667213287856,\n"
                + "  \"nodeName\": \"test-node\",\n"
                + "  \"nodeIp\": \"xxx\",\n"
                + "  \"imageId\": \"test-iamge\",\n"
                + "  \"labels\": \"\",\n"
                + "  \"type\": \"container\",\n"
                + "  \"containerStarted\": 1667213235,\n"
                + "  \"command\": \"java -jar test.jar\"\n"
                + "}";

        return JSON.parseObject(str, new TypeReference<TwatchInfoDo>() {});
    }
}