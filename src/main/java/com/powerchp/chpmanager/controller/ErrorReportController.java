package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.ErrorReport;
import com.powerchp.chpmanager.repository.ErrorReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/error")
public class ErrorReportController {

    @Autowired
    private ErrorReportRepository errorReportRepository;

    // فرم گزارش خطا - نام اپراتور از کاربر لاگین شده گرفته و در مدل قرار داده می‌شود
    @GetMapping("/new")
    public String showForm(Model model) {
        ErrorReport errorReport = new ErrorReport();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();
        errorReport.setOperatorName(loggedInUsername);

        model.addAttribute("errorReport", errorReport);
        return "error_form";
    }

    // ذخیره گزارش خطا - نام اپراتور همیشه از کاربر لاگین شده جایگزین می‌شود تا تغییر نکند
    @PostMapping("/save")
    public String saveError(@ModelAttribute("errorReport") ErrorReport errorReport) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();

        errorReport.setOperatorName(loggedInUsername);
        errorReportRepository.save(errorReport);

        return "redirect:/error/list?success=true";
    }

    // نمایش لیست همه گزارش‌های خطا همراه با نام کاربر لاگین شده
    @GetMapping("/list")
    public String listErrors(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();

        model.addAttribute("errors", errorReportRepository.findAll()); // همه گزارش‌ها
        model.addAttribute("loggedInUsername", loggedInUsername);      // نام کاربر برای منطق در قالب

        return "error_list";
    }

    // نمایش فرم ویرایش گزارش خطا - فقط اگر مالک گزارش باشد
    @GetMapping("/edit/{id}")
    public String editError(@PathVariable Long id, Model model) {
        ErrorReport error = errorReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("گزارش خطا یافت نشد: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();

        if (!error.getOperatorName().equals(loggedInUsername)) {
            return "redirect:/error/list?error=not_authorized";
        }

        model.addAttribute("errorReport", error);
        return "error_form";
    }

    // حذف گزارش خطا - فقط اگر مالک گزارش باشد
    @GetMapping("/delete/{id}")
    public String deleteError(@PathVariable Long id) {
        ErrorReport error = errorReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("گزارش خطا یافت نشد: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();

        if (!error.getOperatorName().equals(loggedInUsername)) {
            return "redirect:/error/list?error=not_authorized";
        }

        errorReportRepository.deleteById(id);
        return "redirect:/error/list?success=deleted";
    }
}
