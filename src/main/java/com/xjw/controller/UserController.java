package com.xjw.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping(value = "/test1")
    public String test(@RequestParam("username") String name) {
        log.info("姓名：" + name);
        stringRedisTemplate.opsForValue().set("k1", name);
        return "success: " + name;
    }

    @GetMapping(value = "/test2")
    public String getValue(@RequestParam("username") String name) {
        log.info("姓名：" + name);
        stringRedisTemplate.opsForValue().set("k1", name);
        return "success: " + name;
    }

}
