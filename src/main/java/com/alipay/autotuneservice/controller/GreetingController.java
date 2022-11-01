package com.alipay.autotuneservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangkaifei
 * @version : GreetingController.java, v 0.1 2022年05月13日 2:10 PM huangkaifei Exp $
 */
@Slf4j
@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    @GetMapping()
    public String greeting() {
        return "Welcome to use to TMAESTRO!!!";
    }
}
