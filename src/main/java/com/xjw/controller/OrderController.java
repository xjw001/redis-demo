package com.xjw.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/order")
@RestController
public class OrderController {

    @GetMapping(value = "/getOrderInfo")
    public Object test(@RequestParam("orderNo") String orderNo) {
        log.info("orderNo ：" + orderNo);
        Map<String,String> map = new HashMap();
        map.put("orderNo","JG20200918466894921646452736");
        map.put("policyNo","gsbaodanhao0000064");
        return map;
    }
}
