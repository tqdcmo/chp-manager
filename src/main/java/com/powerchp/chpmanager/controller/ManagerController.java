package com.powerchp.chpmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/profile")  // مثال: صفحه پروفایل مدیر
    public String showProfile() {
        return "manager_profile";  // فایل manager_profile.html
    }
}
