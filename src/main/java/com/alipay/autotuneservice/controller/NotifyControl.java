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
package com.alipay.autotuneservice.controller;

import com.alipay.autotuneservice.configuration.NoLogin;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import com.alipay.autotuneservice.service.notifyGroup.NotifyService;
import com.alipay.autotuneservice.service.notifyGroup.model.NotifyStatus;
import com.alipay.autotuneservice.service.notifyGroup.model.NotifyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huoyuqi
 * @version NotifyControl.java, v 0.1 2022年12月28日 1:52 下午 huoyuqi
 */
@Slf4j
@RestController
@RequestMapping("/api/notify")
@NoLogin
public class NotifyControl {

    @Autowired
    private NotifyService notifyService;

    @GetMapping("/insertNotifyGroup")
    public ServiceBaseResult<Boolean> insertNotifyGroup(@RequestParam(value = "groupName") String groupName,
                                                        @RequestParam(value = "status") NotifyStatus status,
                                                        @RequestParam(value = "context", required = false) String context) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {

                    notifyService.insertNotify(groupName, status, context);
                    return true;
                });
    }

    @GetMapping("/updateNotifyGroup")
    public ServiceBaseResult<Boolean> updateNotifyGroup(@RequestParam(value = "notifyId") Integer notifyId,
                                                        @RequestParam(value = "groupName", required = false) String groupName,
                                                        @RequestParam(value = "status", required = false) NotifyStatus status,
                                                        @RequestParam(value = "context", required = false) String context) {
        return ServiceBaseResult.invoker()
                .makeResult(() -> {
                    notifyService.updateNotify(notifyId, groupName, status, context);
                    return true;
                });
    }

    @GetMapping("/listNotifyGroup")
    public ServiceBaseResult<List<NotifyVO>> listNotifyGroup() {
        return ServiceBaseResult.invoker()
                .makeResult(() ->
                        notifyService.getByAccessToken()
                );
    }

    @GetMapping("/deleteGroup")
    public ServiceBaseResult<Boolean> deleteGroup(@RequestParam(value = "notifyId") Integer notifyId) {
        return ServiceBaseResult.invoker()
                .makeResult(() ->
                        notifyService.deleteById(notifyId)
                );
    }

}