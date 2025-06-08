package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.Shift;
import com.powerchp.chpmanager.repository.ShiftRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/shift")
public class ShiftController {

    @Autowired
    private ShiftRepository shiftRepository;

    @GetMapping("/new")
    public String showForm(Model model, Principal principal) {
        Shift shift = new Shift();
        shift.setOperatorName(principal.getName()); // نام کاربری فعلی را به‌عنوان اپراتور ثبت کن
        model.addAttribute("shift", shift);
        return "shift_form";
    }

    @PostMapping("/save")
    public String saveShift(@ModelAttribute("shift") Shift shift, Principal principal) {
        // اطمینان از اینکه شیفت به کاربر واردشده تعلق دارد
        shift.setOperatorName(principal.getName());
        shiftRepository.save(shift);
        return "redirect:/shift/my?success=true";
    }

    // فقط مدیر ببیند:
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public String listShifts(Model model) {
        model.addAttribute("shifts", shiftRepository.findAll());
        return "shift_list";
    }

    // فقط شیفت‌های خود کاربر
    @GetMapping("/my")
    public String viewMyShifts(Model model, Principal principal) {
        String username = principal.getName();
        List<Shift> myShifts = shiftRepository.findByOperatorName(username);
        model.addAttribute("shifts", myShifts);
        return "my_shifts";
    }

    @GetMapping("/edit/{id}")
    public String editShift(@PathVariable Long id, Model model, Principal principal) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("شیفت یافت نشد: " + id));

        // فقط ادمین یا صاحب شیفت
        if (!principal.getName().equals(shift.getOperatorName()) && !isAdmin(principal)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("shift", shift);
        return "shift_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteShift(@PathVariable Long id, Principal principal) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("شیفت یافت نشد: " + id));

        if (!principal.getName().equals(shift.getOperatorName()) && !isAdmin(principal)) {
            return "redirect:/access-denied";
        }

        shiftRepository.deleteById(id);
        return "redirect:/shift/my";
    }

    @GetMapping("/report/{id}")
    public void downloadShiftReport(@PathVariable Long id, HttpServletResponse response, Principal principal) throws IOException {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("شیفت یافت نشد: " + id));

        if (!principal.getName().equals(shift.getOperatorName()) && !isAdmin(principal)) {
            response.sendRedirect("/access-denied");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=shift_report_" + id + ".pdf");

        String text = "گزارش شیفت #" + id + "\nاپراتور: " + shift.getOperatorName();
        byte[] pdf = createPersianPdf(text);
        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }

    private boolean isAdmin(Principal principal) {
        // ⚠ این فقط برای تست است. اگر از @PreAuthorize استفاده کنی بهتره با نقش بررسی بشه
        return principal.getName().equalsIgnoreCase("admin");
    }

    private byte[] createPersianPdf(String text) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            InputStream fontStream = getClass().getResourceAsStream("/fonts/Vazirmatn-Regular.ttf");
            PDType0Font font = PDType0Font.load(document, fontStream, true);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(font, 14);
                contentStream.newLineAtOffset(50, 700);

                for (String line : text.split("\n")) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -20);
                }

                contentStream.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}
