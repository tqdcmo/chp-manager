package com.powerchp.chpmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";  // نام فایل thymeleaf شما: access-denied.html
    }
}
