package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // دریافت نام اپراتور لاگین شده
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String operatorName = (auth != null) ? auth.getName() : "ناشناس";

        // اضافه کردن نام اپراتور به مدل
        model.addAttribute("operatorName", operatorName);

        // اضافه کردن داده‌های داشبورد
        model.addAttribute("errorCount", dashboardService.getTodayErrorCount());
        model.addAttribute("powerGenerated", dashboardService.getTodayPowerGenerated());
        model.addAttribute("gasConsumed", dashboardService.getTodayGasConsumed());
        model.addAttribute("shiftTime", dashboardService.getCurrentShiftTime());
        model.addAttribute("engine1Status", dashboardService.getEngineStatus(1));
        model.addAttribute("engine2Status", dashboardService.getEngineStatus(2));

        return "dashboard";
    }
}

