package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.ProductionRecord;
import com.powerchp.chpmanager.model.EngineState;
import com.powerchp.chpmanager.service.ProductionRecordService;
import com.powerchp.chpmanager.service.EngineStatusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductionController {

    @Autowired
    private ProductionRecordService productionRecordService;

    @Autowired
    private EngineStatusService engineStatusService;

    @PostMapping("/production/save")
    public String saveProductionRecord(@ModelAttribute ProductionRecord record,
                                       RedirectAttributes redirectAttributes) {
        try {
            productionRecordService.save(record);

            redirectAttributes.addFlashAttribute("message", "رکورد با موفقیت ذخیره شد.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "خطا در ذخیره رکورد: " + e.getMessage());
        }
        return "redirect:/operator/dashboard";
    }

    @PostMapping("/engine/update-status")
    public String updateEngineStatus(@RequestParam("engineNumber") int engineNumber,
                                     @RequestParam("status") String status,
                                     RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
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

        try {
            EngineState engineState = EngineState.valueOf(status.trim().toUpperCase());
            String operatorName = authentication.getName();
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
