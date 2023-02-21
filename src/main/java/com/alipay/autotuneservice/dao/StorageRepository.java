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
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.controller.model.diagnosis.FileType;
import com.alipay.autotuneservice.model.common.StorageInfo;

import java.util.List;

/**
 * @author dutianze
 * @version StorageRepository.java, v 0.1 2022年04月18日 16:55 dutianze
 */
public interface StorageRepository {

    StorageInfo findByFileName(String fileName);

    StorageInfo save(StorageInfo record);

    /**
     * 根据id 获取storageInfo 相关信息
     * @param id
     * @return
     */
    StorageInfo findById(Long id);

    /**
     * 根据fileName 和 token 进行查询
     *
     * @param fileName
     * @param token
     * @return
     */
    StorageInfo findByNameAndToken(String fileName, String token);

    /**
     * 根据文件类型和token进行查询
     *
     * @param fileType
     * @param token
     * @return
     */
    List<StorageInfo> findByFileTypeAndToken(FileType fileType, String token);

    /**
     * 根据文件名称 和 recordId 删除内容
     * @param fileName
     * @param id
     * @return
     */
    boolean deleteByNameAndToken(String fileName, Long id);
}