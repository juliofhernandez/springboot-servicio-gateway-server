package com.microservices.gateway.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class AppController {

//    @GetMapping("/authorized")
//    public Map<String, String> authorized(@RequestParam String code) {
//        Map<String, String> map = new HashMap<>();
//        map.put("code", Objects.requireNonNullElse(code, "error"));
//        return map;
//    }

//    @PostMapping("/logout")
//    public Map<String, String> logout() {
//        return Collections.singletonMap("token", "OK");
//    }
}
