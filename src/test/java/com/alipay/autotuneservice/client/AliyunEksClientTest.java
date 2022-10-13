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
package com.alipay.autotuneservice.client;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient.Builder;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import org.junit.Test;

/**
 * @author huangkaifei
 * @version : AliyunEksClientTest.java, v 0.1 2022年07月13日 10:50 AM huangkaifei Exp $
 */
public class AliyunEksClientTest {

    @Test
  public void listPod(){
        K8sClient k8sClient = getK8sClient();
        // list pod
        System.out.println(" -------------- list pod -------------------");
        PodList podList1 = k8sClient.listPods();
        podList1.getItems().stream().forEach(item -> {
            System.out.println("podName => " + item.getMetadata().getName());
        });
    }

    @Test
    public void listNode(){
        K8sClient k8sClient = getK8sClient();
        // list pod
        System.out.println(" -------------- list node -------------------");
        NodeList nodeList = k8sClient.listNode();
        nodeList.getItems().stream().forEach(item -> {
            System.out.println("node => " + item.getMetadata().getName());
        });
    }

    @Test
    public void listDeployment(){
        K8sClient k8sClient = getK8sClient();
        // list pod
        System.out.println(" -------------- list deployment -------------------");
        DeploymentList deploymentList = k8sClient.listDeployment();
        deploymentList.getItems().stream().forEach(item -> {
            System.out.println("deployment => " + item.getMetadata().getName());
        });
    }

    @Test
    public void deletePod() {
        K8sClient k8sClient = getK8sClient();

        // delete pod
        System.out.println(" -------------- delete pod -------------------");
        String podName = "twatch-c5jv2";
        boolean res = k8sClient.deletePod("default", podName);
        System.out.println(res);
    }

    private K8sClient getK8sClient() {
        String caStr = "XX";
        String token = "xx";
        String endpoint = "xx";

        String ak = "xx";
        String sk = "xx";
        String clusterName = "xx";
        K8sClient build = K8sClient.Builder.builder().withAccessKey(ak).withSecretKey(sk)
            .withCaStr(caStr).withEndpoint(endpoint).withClusterName(clusterName).build();
        return build;
    }

    private K8sClient getK8sClient1() {
        String caStr = "xx";
        String token = "xx";
        String endpoint = "xx";
        //return Builder.builder().withEndpoint(endpoint).withCaStr(caStr).withToken(token).build()；
        return null;
    }

    @Test
    public void listPod1() {
        K8sClient build1 = Builder.builder().withKubeConfigPath("/.kube/config").build();
        PodList podList = build1.listPods();
        System.out.println(JSON.toJSONString(podList));
    }
}