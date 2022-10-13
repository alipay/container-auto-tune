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
//package com.alipay.autotuneservice.configuration;
//
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author dutianze
// * @version AwsConfig.java, v 0.1 2022年03月15日 18:56 dutianze
// */
//@Configuration
//public class AwsConfig {
//
//    @Value(value = "${aws.s3.accessKeyId}")
//    private String awsAccessKey;
//
//    @Value(value = "${aws.s3.secretAccessKey}")
//    private String awsAccessSecretKey;
//
//    @Value(value = "${aws.s3.region}")
//    private String awsRegion;
//
//    @Value(value = "${aws.s3.bucketName}")
//    private String bucket;
//
//    public AWSStaticCredentialsProvider getAwsCredentialsProvider() {
//        BasicAWSCredentials awsCred = new BasicAWSCredentials(this.awsAccessKey, this.awsAccessSecretKey);
//        return new AWSStaticCredentialsProvider(awsCred);
//    }
//
//    @Bean
//    public AmazonS3 getAmazonS3Client() {
//        return AmazonS3ClientBuilder
//                .standard()
//                .withRegion(this.awsRegion)
//                .withCredentials(getAwsCredentialsProvider())
//                .build();
//    }
//
//    @Bean(name = "s3GlobalDefaultBucketName")
//    public String s3GlobalDefaultBucketName() {
//        return bucket;
//    }
//}