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
package com.alipay.autotuneservice.infrastructure.saas.cloudsvc;

import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.mail.MailProvider;
import com.alipay.autotuneservice.infrastructure.saas.cloudsvc.mail.aliyun.AliyunMailProvider;
import com.alipay.autotuneservice.infrastructure.saas.common.util.SpringContextUtil;
import com.alipay.autotuneservice.infrastructure.saas.common.util.SpringPropertiesCache;
import org.springframework.core.env.StandardEnvironment;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 阿里云 近端包工厂
 *
 * @author yiqi
 * @date 2022/05/30
 */
public class AliyunFactory implements MultiCloudSdkFactory {

    private static final Map<String, AliyunFactory> INSTANCES         = new HashMap<>();

    private String                                  mongoUrl;

    private String                                  accessId;

    private String                                  accessKey;

    private static final Map<String, Object>        PROVIDER_MAP      = new ConcurrentHashMap<>();

    private static final String                     AES_KEY           = "7210727024823427";

    private static final String                     GET_ACCESS_KEY    = "getAccessKey";

    private static final String                     GET_ACCESS_SECRET = "getAccessSecret";

    private static final String                     SET_ACCESS_KEY    = "setAccessKey";

    private static final String                     SET_ACCESS_SECRET = "setAccessSecret";

    private AliyunFactory() {
        yamlFileInit();
    }

    private AliyunFactory(String accessKey, String accessSecret) {
        yamlFileInit();
        // 修改properties的值
        loadProperties(accessKey, accessSecret);
    }

    public void loadProperties(String accessKey, String accessSecret) {
        Method[] methods = this.getClass().getDeclaredMethods();

        /**
         * 利用get、set方法,将属性之装载进实例化的对象当中
         * 1、遍历方法,根据自定义规则匹配拿到方法,例如：getOssProperties(),通过反射拿到实例化的ossProperties对象，排除mongoUrl等无关属性
         * 2、判断ossProperties对象是否为空（即有无实例化）
         * 3、通过getAccessKey()和getAccessSecret方法判断Properties对象中有没有accessKey和accessSecret属性
         * 4、通过set方法将accessKey和accessSecret装载进实例化的属性当中
         *
         */
        for (Method method : methods) {
            if ((method.getName().endsWith("Properties") || method.getName().endsWith("Config"))
                && method.getName().startsWith("get")) {
                try {
                    Object propertiesObject = method.invoke(this);
                    if (Objects.nonNull(propertiesObject)) {
                        if (Objects.nonNull(propertiesObject.getClass().getDeclaredMethod(
                            GET_ACCESS_KEY))
                            && Objects.nonNull(propertiesObject.getClass().getDeclaredMethod(
                                GET_ACCESS_SECRET))) {
                            propertiesObject.getClass()
                                .getDeclaredMethod(SET_ACCESS_KEY, String.class)
                                .invoke(propertiesObject, accessKey);
                            propertiesObject.getClass()
                                .getDeclaredMethod(SET_ACCESS_SECRET, String.class)
                                .invoke(propertiesObject, accessSecret);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static AliyunFactory getInstance() {
        return getInstance(DEFAULT_KEY, DEFAULT_KEY);
    }

    public static AliyunFactory getInstance(String accessKey, String accessSecret) {
        String key = accessKey + "_" + accessSecret;
        AliyunFactory instance = INSTANCES.get(key);
        if (Objects.isNull(instance)) {
            instance = accessKey.equals(DEFAULT_KEY) ? new AliyunFactory() : new AliyunFactory(
                accessKey, accessSecret);
            INSTANCES.put(key, instance);
        }
        return instance;
    }

    @Override
    public MailProvider mailProvider() {
        return getInstance(() -> new AliyunMailProvider(), "mailProvider");
    }

    private synchronized <T> T getInstance(Supplier<T> supplier, String cacheKey) {
        String key = String.format("%s_%s_%s", this.accessId, this.accessKey, cacheKey);
        if (PROVIDER_MAP.containsKey(key)) {
            return (T) PROVIDER_MAP.get(key);
        }
        T t = supplier.get();
        PROVIDER_MAP.put(key, t);
        return t;
    }

    @SuppressWarnings("unchecked")
    private void yamlFileInit() {
        try {
            String include = SpringContextUtil.getSdkEnv();
            Map<String, Object> ml = SpringPropertiesCache.get(include);
            // 读取application.yml 中的 aliyun配置
            Map<String, Object> aliyunMap = (HashMap<String, Object>) ml.get("aliyun");
            // 读取oss配置
            Map<String, String> ossMap = (HashMap<String, String>) aliyunMap.get("oss");
            // 读取sms配置
            Map<String, String> mongoMap = (HashMap<String, String>) aliyunMap.get("mongo");
            // 利用spring的能力，解析表达式
            StandardEnvironment standardEnvironment = new StandardEnvironment();
            String accessKeyId = standardEnvironment.resolvePlaceholders((String) aliyunMap
                .get("accessKeyId"));
            String secretAccessKey = standardEnvironment.resolvePlaceholders((String) aliyunMap
                .get("secretAccessKey"));
            this.accessId = accessKeyId;
            this.accessKey = secretAccessKey;
            this.mongoUrl = standardEnvironment.resolvePlaceholders(mongoMap.get("url"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密aes
     *
     * @param input 加密文本
     * @param key   加密key
     * @return
     * @throws Exception
     */
    private String decryptByAES(String input, String key) throws Exception {
        // 算法
        String algorithm = "AES";
        String transformation = "AES";
        // Cipher：密码，获取加密对象
        // transformation:参数表示使用什么类型加密
        Cipher cipher = Cipher.getInstance(transformation);
        // 指定秘钥规则
        // 第一个参数表示：密钥，key的字节数组 长度必须是16位
        // 第二个参数表示：算法
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), algorithm);
        // 对加密进行初始化
        // 第一个参数：表示模式，有加密模式和解密模式
        // 第二个参数：表示秘钥规则
        cipher.init(Cipher.DECRYPT_MODE, sks);
        // 进行解密
        byte[] inputBytes = hexStringToBytes(input);
        byte[] bytes = cipher.doFinal(inputBytes);
        return new String(bytes);
    }

    private byte[] hexStringToBytes(String hexString) {
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("hexString length not valid");
        }
        int length = hexString.length() / 2;
        byte[] resultBytes = new byte[length];
        for (int index = 0; index < length; index++) {
            String result = hexString.substring(index * 2, index * 2 + 2);
            resultBytes[index] = Integer.valueOf(Integer.parseInt(result, 16)).byteValue();
        }
        return resultBytes;
    }

    public String getMongoUrl() {
        return mongoUrl;
    }

    public void setMongoUrl(String mongoUrl) {
        this.mongoUrl = mongoUrl;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
}
