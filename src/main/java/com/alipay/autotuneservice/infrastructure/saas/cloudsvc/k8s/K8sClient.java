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
package com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s;

import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.aliyun.AliyunK8sClient;
import com.alipay.autotuneservice.infrastructure.saas.common.result.CommonResult;
import com.alipay.autotuneservice.infrastructure.saas.common.util.SpringContextUtil;
import io.fabric8.kubernetes.api.model.ListOptionsBuilder;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetricsList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yiqi
 * @date 2022/06/21
 */
public interface K8sClient {

    KubernetesClient getKubernetesClient();

    /**
     * k8sClient 关闭方法，初始化k8sClient时其中一种方法时获取的临时凭证访问的k8s集群
     * 如果不提供销毁的方法，token过期之后，再去访问集群将会报错.
     */
    default void close() {
        getKubernetesClient().close();
    }
    /**
     * 获取集群下所有的Deployment
     *
     * @return deploymentList
     */
    default DeploymentList listDeployment() {
        return getKubernetesClient().apps().deployments().inAnyNamespace().list();
    }

    /**
     * 创建nameSpace
     *
     * @param ns Namespace对象
     * @return 返回Namespace对象
     */
    default Namespace createNameSpace(Namespace ns) {
        return getKubernetesClient().namespaces().create(ns);
    }

    /**
     * 创建或者替换原有的
     *
     * @param ns Namespace对象
     * @return Namespace对象
     */
    default Namespace createOrReplaceNs(Namespace ns) {
        return getKubernetesClient().namespaces().createOrReplace(ns);
    }

    /**
     * 查询集群下所有的NameSpace
     *
     * @return NamespaceList
     */
    default NamespaceList listNameSpace() {
        return getKubernetesClient().namespaces().list();
    }

    /**
     * 获取集群下NameSpace对象
     *
     * @return NameSapce
     */
    default Namespace getNameSpace(String nameSpace) {
        return getKubernetesClient().namespaces().withName(nameSpace).get();
    }

    /**
     * 删除集群中的NameSpace
     *
     * @return 删除成功or失败
     */
    default boolean deleteNameSpace(String nameSpace) {
        return getKubernetesClient().namespaces().withName(nameSpace).delete();
    }

    /**
     * 创建pod
     *
     * @param pod pod对象
     * @return 返回创建的pod
     */
    default Pod createPod(Pod pod, String nameSpace) {
        return getKubernetesClient().pods().inNamespace(nameSpace).create(pod);
    }

    /**
     * 创建并替换pod
     *
     * @param nameSpace 命名空间名称
     * @param podName   pod名称
     * @param map       label值
     * @return
     */
    default boolean createOrReplacePod(String nameSpace, String podName, HashMap<String, String> map) {
        Pod pod = editPod(nameSpace, podName, map);
        getKubernetesClient().pods().inNamespace(nameSpace).createOrReplace(pod);
        return true;
    }

    /**
     * 编辑pod
     *
     * @param nameSpace 命名空间
     * @param podName   pod名称
     * @param map       传入label标签
     * @return 返回的是pod值
     */
    default Pod editPod(String nameSpace, String podName, HashMap<String, String> map) {
        Pod updatePod = getKubernetesClient().pods().inNamespace(nameSpace).withName(podName)
                .edit(p -> new PodBuilder(p)
                        .editMetadata()
                        .addToLabels(map)
                        .and().build()
                );
        return updatePod;
    }

    /**
     * 获取pod名称的元素信息
     *
     * @param nameSpace 命名空间
     * @param podName   pod名称
     * @return podMetrics
     */
    default PodMetrics podMetrics(String nameSpace, String podName) {
        PodMetrics podMetrics = getKubernetesClient().top().pods().metrics(nameSpace, podName);
        return podMetrics;
    }

    /**
     * 获取nameSpace下所有pod的podMetrics
     *
     * @param nameSpace 命名空间
     * @return nameSapce下的podMetrics
     */
    default PodMetricsList listPodMetrics(String nameSpace) {
        PodMetricsList podMetricsList = getKubernetesClient().top().pods().metrics(nameSpace);
        return podMetricsList;
    }

    /**
     * 获取pod cpu采用的单位是核数
     *
     * @param nameSpace 命名空间
     * @param podName   pod名称
     * @return cpu正在使用的核数
     */
    default Double getPodCpu(String nameSpace, String podName) {
        PodMetrics podMetrics1 = podMetrics(nameSpace, podName);
        String cpuUsage = String.valueOf(podMetrics1.getContainers().get(0).getUsage().get("cpu"));
        String unit = cpuUsage.substring(cpuUsage.length() - 1);
        double cpu = Double.parseDouble(cpuUsage.substring(0, cpuUsage.length() - 1));
        switch (unit) {
            case "n":
                return cpu / 1000 / 1000 / 1000;
            case "m":
                return cpu / 1000;
            default:
                return cpu;
        }
    }

    /**
     * 获取pod 内存采用的单位是G
     *
     * @param nameSpace 命名空间
     * @param podName   pod名称
     * @return 内存使用多少
     */
    default Double getPodMem(String nameSpace, String podName) {
        PodMetrics podMetrics1 = podMetrics(nameSpace, podName);
        String memUsage = String.valueOf(podMetrics1.getContainers().get(0).getUsage().get("memory"));
        String unit = memUsage.substring(memUsage.length() - 2);
        double mem = Double.parseDouble(memUsage.substring(0, memUsage.length() - 2));
        switch (unit) {
            case "Ki":
                return mem / 1024 / 1024;
            case "Mi":
                return mem / 1024;
            case "Gi":
                return mem;
            case "Ti":
                return mem * 1024;
            default:
                return mem * 1024 * 1024;
        }
    }

    /**
     * 查询集群下所有的pod
     *
     * @return PodList
     */
    default PodList listPods() {
        return getKubernetesClient().pods().inAnyNamespace().list();
    }

    /**
     * 查询NameSpace下的所有pod
     *
     * @param nameSpace 名称
     * @return 返回list
     */
    default PodList listPodsInNameSpace(String nameSpace) {
        return getKubernetesClient().pods().inNamespace(nameSpace).list();
    }

    /**
     * 获取pod对象
     *
     * @param podName pod名称
     * @return 返回pod对象
     */
    default Pod getPod(String nameSpace, String podName) {
        return getKubernetesClient().pods().inNamespace(nameSpace).withName(podName).get();
    }

    /**
     * 删除pod
     *
     * @param podName pod名称
     * @return true or false
     */
    default boolean deletePod(String nameSpace, String podName) {
        return getKubernetesClient().pods().inNamespace(nameSpace).withName(podName).delete();
    }

    /**
     * 根据nodeName获取Node
     *
     * @param nodeName node名称
     * @return
     */
    default Resource<Node> getNode(String nodeName) {
        return getKubernetesClient().nodes().withName(nodeName);
    }

    /**
     * 获取集群下所有的list
     *
     * @return
     */
    default NodeList listNode(){
        return getKubernetesClient().nodes().list();
    }


    /**
     * 创建k8s service
     *
     * @param namespace   命名空间
     * @param serviceName 服务名称
     * @param port        服务端口号（和目标pod的端口号一致）
     * @param selector    pod标签选择器
     * @return 创建成功的service对象
     */
    default CommonResult<Service> createService(String namespace, String serviceName, Integer port, Map<String, String> selector) {
        //构建service的yaml对象
        CommonResult<Service> result = new CommonResult<>(false);
        ServiceBuilder serviceBuilder = new ServiceBuilder().withNewMetadata()
                .withNamespace(namespace)
                .withName(serviceName)
                .endMetadata()
                .withNewSpec()
                .addNewPort()
                .withProtocol("TCP")
                .withPort(port)
                .withTargetPort(new io.fabric8.kubernetes.api.model.IntOrString(port))
                .endPort()
                .withSelector(selector)
                .endSpec();
        // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
        Service service = getKubernetesClient().services().create(serviceBuilder.build());
        result.setSuccess(true);
        result.setResultSet(service);
        return result;
    }

    /**
     * 删除一个job，job模板中有ttl，此接口暂时不用
     */
    default boolean deleteJob() {
        return true;
    }

    /**
     * todo 查询某个job
     */
    default CommonResult<Job> queryJobStatus(String namespace, String jobName) {
        CommonResult<Job> result = new CommonResult<>(false);
        Job job = getKubernetesClient().batch().v1().jobs().inNamespace(namespace).withName(jobName).get();
        result.setSuccess(true);
        result.setResultSet(job);
        return result;
    }

    /**
     * 根据label查询多个job，比如可查询某次生成用例任务下的所有job
     * labelSelector参数，类似命令行的 kubectl get jobs -l name=xxx
     */
    default CommonResult<JobList> queryJobListBySelector(String namespace, String fieldSelector, String labelSelector) {
        CommonResult<JobList> result = new CommonResult<>(false);
        JobList list = getKubernetesClient().batch().v1().jobs().inNamespace(namespace)
                .list(new ListOptionsBuilder().withFieldSelector(fieldSelector).withLabelSelector(labelSelector).build());
        result.setSuccess(true);
        result.setResultSet(list);

        return result;
    }

    public static class Builder {
        protected String accessKey;
        protected String secretKey;
        protected String caStr;
        protected String endpoint;

        protected String token;
        protected String clusterName;
        protected String kubeConfigPath;

        protected Builder() {
        }

        public static Builder builder() {
            String type = SpringContextUtil.getSdkEnv();
           try {
                return new AliyunK8sClient.AliyunK8sBuilder();
            }catch (Exception e){
                throw new IllegalArgumentException("cloud.type is not support, please check it");
            }
        }

        public Builder withAccessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder withSecretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder withCaStr(String caStr) {
            this.caStr = caStr;
            return this;
        }

        public Builder withEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder withClusterName(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        public Builder withKubeConfigPath(String kubeConfigPath) {
            this.kubeConfigPath = kubeConfigPath;
            return this;
        }

        public Builder withToken(String token) {
            this.token = token;
            return this;
        }

        public K8sClient build() {
            return null;
        }
    }
}
