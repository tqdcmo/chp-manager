package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")  // مسیر اصلی برای مدیران
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")  // مسیر کامل: /manager/dashboard
    public String showDashboard(Model model) {
        long errorCount = dashboardService.getTodayErrorCount();
        model.addAttribute("errorCount", errorCount);

        // داده‌های فرضی برای سایر اطلاعات داشبورد
        model.addAttribute("powerGenerated", 12000);
        model.addAttribute("gasConsumed", 2500);
        model.addAttribute("shiftTime", "07:00 - 15:00");
        model.addAttribute("engine1Status", "فعال");
        model.addAttribute("engine2Status", "در تعمیر");

        return "dashboard";  // فایل dashboard.html
    }
}
