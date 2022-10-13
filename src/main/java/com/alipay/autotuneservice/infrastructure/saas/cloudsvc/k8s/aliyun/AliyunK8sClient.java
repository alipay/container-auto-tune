package com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.aliyun;

import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.k8s.K8sClient;
import com.aliyun.cs20151215.Client;
import com.aliyun.cs20151215.models.DescribeClusterUserKubeconfigRequest;
import com.aliyun.cs20151215.models.DescribeClusterUserKubeconfigResponse;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.IOHelpers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yiqi
 * @date 2022/06/20
 */
@Slf4j
public class AliyunK8sClient implements K8sClient {

    private String accessKey;
    private String secretKey;
    private String caStr;
    private String endpoint;
    private String clusterName;
    private String token;

    private KubernetesClient kubernetesClient;

    final static Map<String, KubernetesClient> KUBERNETESCLIENT_MAP = new ConcurrentHashMap<>();

    @Override
    public KubernetesClient getKubernetesClient() {
        return kubernetesClient;
    }

    public AliyunK8sClient(String kubeConfigPath) {
        try {
            String kubeConfigFile = "/opt/app/storage/config";
            //String kubeConfigFile = System.getenv("HOME") + kubeConfigPath;
            log.info("AliyunK8sClient HOME_PATH={},kubeConfigPath={},kubeConfigFile={}", System.getenv("HOME"), kubeConfigPath,
                    kubeConfigFile);
            String kubeconfigContents = IOHelpers.readFully(new FileReader(kubeConfigFile));
            Config config = Config.fromKubeconfig(null, kubeconfigContents, kubeConfigFile);
            this.kubernetesClient = new DefaultKubernetesClient(config);
        } catch (Exception e) {
            throw new RuntimeException("构建K8s-Client异常" + e.getMessage());
        }
    }

    public AliyunK8sClient(String caStr, String endpoint, String token) {
        Config config = new ConfigBuilder()
                .withCaCertData(caStr)
                .withOauthToken(token)
                .withMasterUrl(endpoint)
                .build();
        this.kubernetesClient = new DefaultKubernetesClient(config);
    }

    private AliyunK8sClient(String accessKey, String secretKey, String caStr, String endpoint, String clusterName) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.caStr = caStr;
        this.endpoint = endpoint;
        this.clusterName = clusterName;
        this.build();
    }

    public void build() {
        String key = accessKey + "_" + secretKey + "_" + caStr + "_" + endpoint + "_" + clusterName;
        if (KUBERNETESCLIENT_MAP.containsKey(key)) {
            kubernetesClient = KUBERNETESCLIENT_MAP.get(key);
            return;
        }

        try {
            kubernetesClient = initClient();
        } catch (Exception e) {
            throw new RuntimeException("init aliyun k8sClient fail" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private DefaultKubernetesClient initClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setEndpoint(endpoint)
                .setAccessKeyId(accessKey)
                // 您的 AccessKey Secret
                .setAccessKeySecret(secretKey);
        Client client;
        try {
            client = new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        DescribeClusterUserKubeconfigRequest describeClusterUserKubeconfigRequest = new DescribeClusterUserKubeconfigRequest();
        DescribeClusterUserKubeconfigResponse ce9a2b1bdf57b48b2b4c2d30da879841a = client.describeClusterUserKubeconfig(clusterName,
                describeClusterUserKubeconfigRequest);
        HashMap hashMap = new Yaml().loadAs(ce9a2b1bdf57b48b2b4c2d30da879841a.getBody().getConfig(), HashMap.class);
        ArrayList clusters = (ArrayList) hashMap.get("clusters");
        ArrayList users = (ArrayList) hashMap.get("users");
        // 解析yaml格式的文本
        HashMap<String, HashMap<String, String>> cluster = (HashMap<String, HashMap<String, String>>) clusters.get(0);
        HashMap<String, HashMap<String, String>> user = (HashMap<String, HashMap<String, String>>) users.get(0);
        HashMap<String, String> user1 = user.get("user");
        HashMap<String, String> cluster1 = cluster.get("cluster");
        Config conf = new ConfigBuilder()
                .withCaCertData(cluster1.get("certificate-authority-data"))
                .withClientCertData(user1.get("client-certificate-data"))
                .withClientKeyData(user1.get("client-key-data"))
                .withMasterUrl(cluster1.get("server"))
                .build();
        return new DefaultKubernetesClient(conf);
    }

    public static class AliyunK8sBuilder extends K8sClient.Builder {

        @Override
        public K8sClient build() {
            if (StringUtils.isNotEmpty(kubeConfigPath)) {
                return new AliyunK8sClient(kubeConfigPath);
            }
            if (StringUtils.isNotEmpty(token)) {
                return new AliyunK8sClient(caStr, endpoint, token);
            }
            return new AliyunK8sClient(accessKey, secretKey, caStr, endpoint, clusterName);
        }
    }

}
