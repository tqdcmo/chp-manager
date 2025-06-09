package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.EngineState;
import com.powerchp.chpmanager.service.EngineStatusService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/operator")
public class OperatorController {

    private final EngineStatusService engineStatusService;

    public OperatorController(EngineStatusService engineStatusService) {
        this.engineStatusService = engineStatusService;
    }

    /**
     * نمایش داشبورد اپراتور
     */
    @GetMapping("/dashboard")
    public String showDashboard(@ModelAttribute("message") String message,
                                Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // چک کردن لاگین بودن کاربر
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        String operatorName = authentication.getName();
        model.addAttribute("operatorName", operatorName);

        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
        }

        // دریافت وضعیت آخر موتورها به صورت Optional<EngineState>
        Optional<EngineState> engine1StatusOpt = engineStatusService.getCurrentEngineState(1);
        Optional<EngineState> engine2StatusOpt = engineStatusService.getCurrentEngineState(2);

        EngineState engine1Status = engine1StatusOpt.orElse(EngineState.STOPPED);
        EngineState engine2Status = engine2StatusOpt.orElse(EngineState.STOPPED);

        model.addAttribute("engine1Status", engine1Status);
        model.addAttribute("engine2Status", engine2Status);

        // ارسال لیست وضعیت‌ها به مدل برای منوی کشویی
        List<EngineState> engineStates = List.of(EngineState.RUNNING, EngineState.STOPPED, EngineState.MAINTENANCE);
        model.addAttribute("engineStates", engineStates);

        return "operator_dashboard";
    }

    /**
     * ثبت وضعیت جدید موتور توسط اپراتور
     */
    @PostMapping("/engine/update-status")
    public String updateEngineStatus(
            @RequestParam("engineNumber") int engineNumber,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        if (engineNumber < 1 || engineNumber > 2) {
            redirectAttributes.addFlashAttribute("message", "شماره موتور نامعتبر است.");
            return "redirect:/operator/dashboard";
        }

        if (status == null || status.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "وضعیت نامعتبر است.");
            return "redirect:/operator/dashboard";
        }

        String operatorName = authentication.getName();

        try {
            // تبدیل رشته انگلیسی به Enum
            EngineState engineState = EngineState.valueOf(status.trim().toUpperCase());

            // ذخیره وضعیت جدید به سرویس با Enum
            engineStatusService.saveEngineStatus(engineNumber, engineState, operatorName);

            redirectAttributes.addFlashAttribute("message", "وضعیت موتور " + engineNumber + " با موفقیت ثبت شد.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "وضعیت نامعتبر وارد شده است.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "خطا در ثبت وضعیت موتور: " + e.getMessage());
        }

        return "redirect:/operator/dashboard";
    }
}
