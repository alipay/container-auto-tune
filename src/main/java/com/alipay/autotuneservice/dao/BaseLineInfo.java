/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;


import com.alipay.autotuneservice.dao.jooq.tables.records.BaseLineRecord;
import java.util.List;

/**
 * @author huoyuqi
 * @version BaseLineInfo.java, v 0.1 2022年08月29日 5:34 下午 huoyuqi
 */
public interface BaseLineInfo {

    /**
     * 根据appId 查询 baseLine
     * @param appId
     * @return
     */
    BaseLineRecord getByAppId(Integer appId);

    /**
     * 根据appId 查询相应的版本
     * @param appId
     * @return
     */
    List<BaseLineRecord> selectByAppId(Integer appId);

    /**
     *
     * @param appId
     * @param pipelineId
     * @return
     */
    List<BaseLineRecord> getByAppIdAndPipelineId(Integer appId, Integer pipelineId);

    /**
     * 根据jvmMarketId返回相应版本号
     * @param jvmMarketId
     * @return
     */
    BaseLineRecord getByJvmMarketId(Integer jvmMarketId);

    /**
     * 根据jvmMarketId 批量查询 version
     * @param jvmMarketIds
     * @return
     */
    List<BaseLineRecord> getByJvmMarketId(List<Integer> jvmMarketIds);



    /**
     * 根据appId和version查询相应的基线版本
     * @param appId
     * @param version
     * @return
     */
    BaseLineRecord getByAppIdVersion(Integer appId, Integer version);

    /**
     * 插入baseLine
     * @param record
     * @return
     */
    void insert(BaseLineRecord record);
}