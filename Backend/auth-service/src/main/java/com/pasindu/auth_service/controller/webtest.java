package com.pasindu.auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class webtest {

    @GetMapping("test")
    public String webtest(){
        return "Hi";
    }
}
