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
package com.alipay.autotuneservice.grpc;

import com.alipay.autotuneservice.dao.K8sAccessTokenInfo;
import com.alipay.autotuneservice.dao.jooq.tables.records.K8sAccessTokenInfoRecord;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient;
import io.fabric8.kubernetes.api.model.NamespaceList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author dutianze
 * @date 2022/3/21
 */
@SpringBootTest
class EksClientTest {

    @Autowired
    private K8sAccessTokenInfo k8sAccessTokenInfo;

    @Test
    void listNameSpace() {
        K8sAccessTokenInfoRecord k8AccessTokenRecord = k8sAccessTokenInfo
            .selectByTokenAndCusterName("xx", "xx");
        Assertions.assertNotNull(k8AccessTokenRecord);

        K8sClient eksClient = K8sClient.Builder.builder()
            .withAccessKey(k8AccessTokenRecord.getAccessKeyId())
            .withSecretKey(k8AccessTokenRecord.getSecretAccessKey())
            .withCaStr(k8AccessTokenRecord.getCer())
            .withEndpoint(k8AccessTokenRecord.getEndpoint())
            .withClusterName(k8AccessTokenRecord.getClusterName()).build();

        NamespaceList namespaceList = eksClient.listNameSpace();
        System.out.println(namespaceList);
        Assertions.assertNotNull(namespaceList);
    }
}