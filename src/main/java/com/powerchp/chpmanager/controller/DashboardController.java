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

    // متد تبدیل وضعیت انگلیسی به فارسی
    private String convertStatusToPersian(String status) {
        if (status == null) return "نامشخص";
        return switch (status.toUpperCase()) {
            case "RUNNING" -> "در حال کار";
            case "STOPPED" -> "خاموش";
            case "MAINTENANCE" -> "در حال سرویس";
            default -> "نامشخص";
        };
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String operatorName = (auth != null) ? auth.getName() : "ناشناس";

        model.addAttribute("operatorName", operatorName);
        model.addAttribute("errorCount", dashboardService.getTodayErrorCount());
        model.addAttribute("powerGenerated", dashboardService.getTodayPowerGenerated());
        model.addAttribute("gasConsumed", dashboardService.getTodayGasConsumed());
        model.addAttribute("shiftTime", dashboardService.getCurrentShiftTime());

        // گرفتن وضعیت موتورها از سرویس و تبدیل به فارسی
        String engine1Status = convertStatusToPersian(dashboardService.getEngineStatus(1));
        String engine2Status = convertStatusToPersian(dashboardService.getEngineStatus(2));

        model.addAttribute("engine1Status", engine1Status);
        model.addAttribute("engine2Status", engine2Status);

        return "dashboard";
    }
}
