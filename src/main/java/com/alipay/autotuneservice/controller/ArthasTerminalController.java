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

import com.alipay.autotuneservice.model.ArthasHtmlType;
import com.alipay.autotuneservice.service.ReportActionService;
import com.alipay.autotuneservice.service.StorageInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/api/arthas")
public class ArthasTerminalController {

    @Autowired
    private ReportActionService reportActionService;
    @Autowired
    private StorageInfoService  storageInfoService;

    @GetMapping("/{appId}/show")
    public String show(Model model, @PathVariable(value = "appId") Integer appId,
                       @RequestParam(value = "hostName") String hostName) {
        boolean result = reportActionService.checkArthasInstall(appId, hostName);
        log.info("show -  checkArthasInstall={}", result);
        if (result) {
            //执行
            model.addAttribute("appId", appId);
            model.addAttribute("hostName", hostName);
            //return storageInfoService.getArthasHtmlFileContent(ArthasHtmlType.SHOW).getContent();
            return ArthasHtmlType.TERMINAL.getHtmlFileName();
        }
        //触发安装
        reportActionService.arthasInstall(appId, hostName);
        //执行进度条
        model.addAttribute("appId", appId);
        model.addAttribute("hostName", hostName);
        //return storageInfoService.getArthasHtmlFileContent(ArthasHtmlType.SHOW).getContent();
        return ArthasHtmlType.SHOW.getHtmlFileName();

    }

    @GetMapping("/{appId}/terminal")
    public String terminal(Model model, @PathVariable(value = "appId") Integer appId,
                           @RequestParam(value = "hostName") String hostName) {
        log.info("terminal start. appId={}, hostName={}", appId, hostName);
        boolean result = reportActionService.checkArthasInstall(appId, hostName);
        if (result) {
            //执行
            model.addAttribute("appId", appId);
            model.addAttribute("hostName", hostName);
            //return storageInfoService.getArthasHtmlFileContent(ArthasHtmlType.TERMINAL).getContent();
            return ArthasHtmlType.TERMINAL.getHtmlFileName();
        }
        //return storageInfoService.getArthasHtmlFileContent(ArthasHtmlType.TERMINAL).getContent();
        return ArthasHtmlType.ERROR.getHtmlFileName();
    }
}