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
package com.alipay.autotuneservice.Mongodb;

import com.alipay.autotuneservice.dynamodb.bean.HealthCheckData;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.nosql.aliyun.StandardMongoProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huoyuqi
 * @version MongodbTest.java, v 0.1 2022年07月18日 10:58 上午 huoyuqi
 */
@SpringBootTest
public class MongodbTest {

    // mongodb
    @Test
    public void testMongoDB() {
        StandardMongoProvider standardMongoProvider = new StandardMongoProvider();
        DateFormat sdf3 = new SimpleDateFormat("xx");
        Date d = new Date();
        System.out.println(standardMongoProvider.query().withTableName("xx")
            .withQueryParam("xx", "xx").withRangeKey("xx").withFrom("xx").withTo("xx")
            .queryWithRange(HealthCheckData.class));
    }

}