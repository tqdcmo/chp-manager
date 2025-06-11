package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.service.DashboardService;
import com.powerchp.chpmanager.service.ShiftService;
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
    private final ShiftService shiftService;

    public DashboardController(DashboardService dashboardService, ShiftService shiftService) {
        this.dashboardService = dashboardService;
        this.shiftService = shiftService;
    }

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

        // ایجاد شیفت جاری در صورت نبود
        shiftService.createCurrentShiftIfNotExists();

        model.addAttribute("operatorName", operatorName);
        model.addAttribute("errorCount", dashboardService.getTodayErrorCount());
        model.addAttribute("powerGenerated", dashboardService.getTodayPowerGeneratedByOperator(operatorName));
        model.addAttribute("gasConsumed", dashboardService.getTodayGasConsumed());
        model.addAttribute("shiftTime", dashboardService.getCurrentShiftTime());

        String engine1Status = convertStatusToPersian(dashboardService.getEngineStatus(1));
        String engine2Status = convertStatusToPersian(dashboardService.getEngineStatus(2));

        model.addAttribute("engine1Status", engine1Status);
        model.addAttribute("engine2Status", engine2Status);

        return "dashboard";
    }

}
