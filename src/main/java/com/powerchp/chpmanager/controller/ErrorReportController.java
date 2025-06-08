package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.ErrorReport;
import com.powerchp.chpmanager.repository.ErrorReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/error")
public class ErrorReportController {

    @Autowired
    private ErrorReportRepository errorReportRepository;

    // فرم گزارش خطا
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("errorReport", new ErrorReport());
        return "error_form";
    }

    // ذخیره گزارش خطا
    @PostMapping("/save")
    public String saveError(@ModelAttribute("errorReport") ErrorReport errorReport) {
        errorReportRepository.save(errorReport);
        return "redirect:/error/list?success=true";
    }

    // نمایش لیست گزارش‌های خطا
    @GetMapping("/list")
    public String listErrors(Model model) {
        model.addAttribute("errors", errorReportRepository.findAll());
        return "error_list";
    }

    // نمایش فرم ویرایش گزارش خطا
    @GetMapping("/edit/{id}")
    public String editError(@PathVariable Long id, Model model) {
        ErrorReport error = errorReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("گزارش خطا یافت نشد: " + id));
        model.addAttribute("errorReport", error);
        return "error_form";
    }

    // حذف گزارش خطا
    @GetMapping("/delete/{id}")
    public String deleteError(@PathVariable Long id) {
        errorReportRepository.deleteById(id);
        return "redirect:/error/list";
    }
}
