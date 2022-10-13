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
package com.alipay.autotuneservice.env;

import afu.org.checkerframework.checker.units.qual.A;
import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dao.HelpInfoRepository;
import com.alipay.autotuneservice.dao.JvmMarketInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.JvmMarketInfoRecord;
import com.alipay.autotuneservice.dynamodb.bean.JvmMonitorMetricData;
import com.alipay.autotuneservice.dynamodb.bean.OSS;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.AliyunFactory;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.nosql.aliyun.StandardMongoProvider;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
import com.alipay.autotuneservice.model.common.HelpInfo;
import com.alipay.autotuneservice.model.common.HelpInfo.HelpType;
import com.alipay.autotuneservice.multiCloudAdapter.impl.MongodbImpl;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterDescription;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @author huangkaifei
 * @version : LiteEnvTest.java, v 0.1 2022年08月10日 10:08 AM huangkaifei Exp $
 */
@SpringBootTest
public class LiteEnvTest {

    @Test
    void testMongoDB() {
        StandardMongoProvider standardMongoProvider = new StandardMongoProvider();
        String tableName = "xx";
        List<JvmMonitorMetricData> jvmMonitorMetricData = standardMongoProvider.query()
            .withTableName(tableName).withQueryParam("xx", "xx")
            .queryWithParam(JvmMonitorMetricData.class);
        System.out.println(JSON.toJSONString(jvmMonitorMetricData));
    }

    @Test
    void testMongoDBOSS() {
        StandardMongoProvider standardMongoProvider = new StandardMongoProvider();
        String tableName = "xx";
        List<OSS> ossData = standardMongoProvider.query().withTableName(tableName)
            .withQueryParam("xx", "xx").queryWithParam(OSS.class);
        System.out.println(JSON.toJSONString(ossData));
    }

    @Autowired
    private MongodbImpl mongodb;

    @Test
    void testMongoConnection() {
        ClusterDescription clusterDescription = mongodb.getStandardMongoProvider().getMongoClient()
            .getClusterDescription();
        boolean unknown = StringUtils.equals(clusterDescription.getType().name(), "xx");
        System.out.println(unknown);
    }

    @Autowired
    private RedisClient redisClient;

    // redis
    @Test
    public void testRedis() {
        redisClient.set("xx", "xx");
        System.out.println(redisClient.get("xx"));
    }

    @Autowired
    private HelpInfoRepository helpInfo;
    @Autowired
    private JvmMarketInfo      jvmMarketInfo;

    // mysql
    @Test
    public void testMysql() {
        List<HelpInfo> byHelpType = helpInfo.findByHelpType(HelpType.TWATCH);
        System.out.println(JSON.toJSONString(byHelpType));

        JvmMarketInfoRecord jvmInfo = jvmMarketInfo.getJvmInfo(0);
        System.out.println("xx: " + jvmInfo);
    }

    @Autowired
    private Environment env;

    @Test
    public void testEnv() {
        String property = env.getProperty("xx");
        System.out.println(property);

        AliyunFactory instance = AliyunFactory.getInstance();
        String mongoUrl = instance.getMongoUrl();
        System.out.println("xx " + mongoUrl);
    }
}