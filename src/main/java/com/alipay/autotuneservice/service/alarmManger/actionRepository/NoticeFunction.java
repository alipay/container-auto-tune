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
package com.alipay.autotuneservice.service.alarmManger.actionRepository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.NotifyRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.NotifyRecord;
import com.alipay.autotuneservice.model.notice.NoticeButtonType;
import com.alipay.autotuneservice.model.notice.NoticeRequest;
import com.alipay.autotuneservice.model.notice.NoticeType;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmContext;
import com.alipay.autotuneservice.service.alarmManger.model.AlarmNoticeModel;
import com.alipay.autotuneservice.service.notification.NoticeDefAction;
import com.alipay.autotuneservice.service.notifyGroup.model.NotifyStatus;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huoyuqi
 * @version NoticeFunction.java, v 0.1 2022年12月27日 11:40 上午 huoyuqi
 */
@Slf4j
public class NoticeFunction extends AbstractFunction {

    @Override
    public AviatorObject call(Map<String, Object> env) {
        log.info("NoticeFunction enter");
        AlarmContext alarmContext = (AlarmContext) env.get("alarmContext");
        NoticeDefAction noticeDefAction = alarmContext.getNoticeDefAction();
        //融合消息通知方
        Map<NoticeType, List<String>> noticeMap = constructNoticeMap(alarmContext.getAlarmNotices(), alarmContext.getNotifyRepository());
        noticeDefAction.sendAlarmMessage(new NoticeRequest(alarmContext.getAppId(), alarmContext.getAppName(),
                alarmContext.getJudgeActionExecuteModel().getResultMessage(), noticeMap, NoticeButtonType.DETAIL));
        return new AviatorString("notice完成");
    }

    @Override
    public String getName() {
        return "NOTICE";
    }

    private Map<NoticeType, List<String>> constructNoticeMap(List<AlarmNoticeModel> alarmNoticeModels, NotifyRepository notifyRepository) {
        List<Integer> ids = alarmNoticeModels.stream().map(AlarmNoticeModel::getNotifyId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            List<NotifyRecord> records = notifyRepository.getByIds(ids);
            Map<NoticeType, List<String>> resultMap = new HashMap<>();
            records.forEach(r -> {
                Map<NoticeType, List<String>> map = JSON.parseObject(r.getContext(), new TypeReference<Map<NoticeType, List<String>>>() {});
                //多个通知组融合  EMAIL:xx@qq.com...  WECHAT:微信号
                if (MapUtils.isNotEmpty(map) && NotifyStatus.ON.name().equals(r.getStatus())) {
                    map.forEach((k, v) -> {
                        if (resultMap.containsKey(k)) {
                            resultMap.get(k).addAll(v);
                        }
                        resultMap.put(k, v);
                    });
                }

            });
            return resultMap;
        }
        return null;
    }

}