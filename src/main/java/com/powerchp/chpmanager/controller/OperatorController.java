package com.powerchp.chpmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/operator")
public class OperatorController {

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "operator_dashboard";
    }
}
