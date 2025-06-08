package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.Shift;
import com.powerchp.chpmanager.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shift")
public class ShiftController {

    @Autowired
    private ShiftRepository shiftRepository;

    // نمایش فرم ثبت شیفت جدید
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("shift", new Shift());
        return "shift_form";
    }

    // ذخیره شیفت (ثبت یا ویرایش)
    @PostMapping("/save")
    public String saveShift(@ModelAttribute("shift") Shift shift) {
        shiftRepository.save(shift);
        return "redirect:/shift/list?success=true";
    }

    // نمایش لیست شیفت‌ها
    @GetMapping("/list")
    public String listShifts(Model model) {
        model.addAttribute("shifts", shiftRepository.findAll());
        return "shift_list";
    }

    // نمایش فرم ویرایش شیفت
    @GetMapping("/edit/{id}")
    public String editShift(@PathVariable Long id, Model model) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("شیفت یافت نشد: " + id));
        model.addAttribute("shift", shift);
        return "shift_form";
    }

    // حذف شیفت
    @GetMapping("/delete/{id}")
    public String deleteShift(@PathVariable Long id) {
        shiftRepository.deleteById(id);
        return "redirect:/shift/list";
    }
}

