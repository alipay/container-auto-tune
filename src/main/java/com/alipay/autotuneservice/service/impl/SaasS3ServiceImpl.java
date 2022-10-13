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
///*
// * Ant Group
// * Copyright (c) 2004-2022 All Rights Reserved.
// */
//package com.alipay.autotuneservice.service.impl;
//
//import com.alipay.autotuneservice.model.ServiceBaseResult;
//import com.alipay.autotuneservice.service.SaasS3Service;
//import com.alipay.cloudsdk.saas.AWSFactory;
//import com.alipay.cloudsdk.saas.s3.S3Provider;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.InputStream;
//
///**
// * @author t-rex
// * @version SaasS3ServiceImpl.java, v 0.1 2022年03月01日 5:26 下午 t-rex
// */
//@Service
//@Slf4j
//public class SaasS3ServiceImpl implements SaasS3Service {
//    private final static S3Provider S_3_PROVIDER = AWSFactory.getInstance().s3();
//
//    @Override
//    public ServiceBaseResult<String> uploadFileToS3(File file) {
//        try {
//            String url = S_3_PROVIDER.uploadObjectTM(file);
//            return ServiceBaseResult.successResult(url);
//        } catch (Exception e) {
//            log.error("uploadFileToS3 error", e);
//            return ServiceBaseResult.failureResult(e.getMessage());
//        }
//    }
//
//    @Override
//    public ServiceBaseResult<InputStream> downLoadFile(String url) {
//        try {
//            byte[] objectBytes = S_3_PROVIDER.getObjectBytes(url);
//            InputStream in = new ByteArrayInputStream(objectBytes);
//            return ServiceBaseResult.successResult(in);
//        } catch (Exception e) {
//            return ServiceBaseResult.failureResult(e.getMessage());
//        }
//    }
//
//    public ServiceBaseResult<String> deleteFile(String url) {
//        try {
//            return ServiceBaseResult.successResult("TODO");
//        } catch (Exception e) {
//            return ServiceBaseResult.failureResult(e.getMessage());
//        }
//    }
//}