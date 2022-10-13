/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.meter.model.MetricData;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * @author huangkaifei
 * @version : MeterUtil.java, v 0.1 2022年09月22日 10:13 PM huangkaifei Exp $
 */
@Slf4j
public class MeterUtil {

    public static String getMetricData(String requestPrefix, String queryPql) {
        log.info("getMetricData enter, requestPrefix={}, pql={}", requestPrefix, queryPql);
        try {
            String encodePql = URL.encode(queryPql);
            String url = String.format("%s%s", requestPrefix, encodePql);
            Request request = Request.Get(url);
            HttpResponse httpResponse = request.execute().returnResponse();
            if (httpResponse.getEntity() == null) {
                log.info("getMetricData  response is null.");
            }
            String strRes = EntityUtils.toString(httpResponse.getEntity());
            log.info("getMetricData end, res={}", strRes);
            return strRes;
        } catch (Exception e) {
            log.error("getMetricData occurs an error.", e);
            return "";
        }
    }

    public static List<MetricData> parsePrometheusData(String prometheusData) {
        log.info("parsePrometheusData, input={}", prometheusData);
        if (StringUtils.isEmpty(prometheusData)) {
            return Lists.newArrayList();
        }

        try {
            List<MetricData> resList = Lists.newArrayList();
            JSONObject jsonObject = JSON.parseObject(prometheusData);
            if (!StringUtils.equals(jsonObject.getString("status"), "success")) {
                return resList;
            }
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("result");
            for (int i = 0; i < jsonArray.size(); i++) {
                try {
                    JSONObject object = jsonArray.getJSONObject(i);
                    JSONArray values = object.getJSONArray("value");
                    long meterTime = values.getDouble(0).longValue() * 1000L;
                    double meterVal = Double.parseDouble(values.getString(1));
                    MetricData build = MetricData.builder().timestamp(meterTime).value(meterVal).build();
                    resList.add(build);
                } catch (Exception e) {
                    log.error("parsePrometheusData build MetricData occurs an error.", e);
                }
            }
            log.info("parsePrometheusData res={}", JSON.toJSONString(resList));
            return resList;
        } catch (Exception e) {
            log.error("parsePrometheusData occurs an error.", e);
            return Lists.newArrayList();
        }
    }
}