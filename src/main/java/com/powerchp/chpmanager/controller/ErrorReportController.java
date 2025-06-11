package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.ErrorReport;
import com.powerchp.chpmanager.repository.ErrorReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/error")
public class ErrorReportController {

    @Autowired
    private ErrorReportRepository errorReportRepository;

    // نمایش فرم ایجاد گزارش خطا
    @GetMapping("/new")
    public String showForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        ErrorReport errorReport = new ErrorReport();
        errorReport.setOperatorName(username);

        model.addAttribute("errorReport", errorReport);
        return "error_form";
    }

    // ذخیره یا به‌روزرسانی گزارش خطا
    @PostMapping("/save")
    public String saveError(@ModelAttribute("errorReport") ErrorReport errorReport) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        errorReport.setOperatorName(username);
        errorReportRepository.save(errorReport);
        return "redirect:/error/list?success=true";
    }

    // لیست گزارش‌ها: مدیر همه را می‌بیند، اپراتورها فقط گزارش خودشان را
    @GetMapping("/list")
    public String listErrors(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        List<ErrorReport> errors = isAdmin ?
                errorReportRepository.findAll() :
                errorReportRepository.findByOperatorName(username);

        model.addAttribute("errors", errors);
        model.addAttribute("loggedInUsername", username);
        model.addAttribute("isAdmin", isAdmin);
        return "error_list";
    }

    // ویرایش: فقط مدیر یا صاحب گزارش
    @GetMapping("/edit/{id}")
    public String editError(@PathVariable Long id, Model model) {
        ErrorReport error = errorReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("گزارش خطا یافت نشد: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!error.getOperatorName().equals(username) && !isAdmin) {
            return "redirect:/error/list?error=not_authorized";
        }

        model.addAttribute("errorReport", error);
        return "error_form";
    }

    // حذف: فقط مدیر یا صاحب گزارش
    @GetMapping("/delete/{id}")
    public String deleteError(@PathVariable Long id) {
        ErrorReport error = errorReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("گزارش خطا یافت نشد: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!error.getOperatorName().equals(username) && !isAdmin) {
            return "redirect:/error/list?error=not_authorized";
        }

        errorReportRepository.delete(error);
        return "redirect:/error/list?success=deleted";
    }
}