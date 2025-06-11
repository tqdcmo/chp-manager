package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.ErrorReport;
import com.powerchp.chpmanager.repository.ErrorReportRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/error")
public class ErrorReportController {

    @Autowired
    private ErrorReportRepository errorReportRepository;

    @GetMapping("/new")
    public String showForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        ErrorReport errorReport = new ErrorReport();
        errorReport.setOperatorName(username);

        model.addAttribute("errorReport", errorReport);
        return "error_form";
    }

    @PostMapping("/save")
    public String saveError(@ModelAttribute("errorReport") ErrorReport errorReport) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        errorReport.setOperatorName(username);
        if (errorReport.getReportTime() == null) {
            errorReport.setReportTime(java.time.LocalDateTime.now());
        }
        errorReportRepository.save(errorReport);
        return "redirect:/error/list?success=true";
    }

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

    @GetMapping("/edit/{id}")
    public String editError(@PathVariable Long id, Model model) {
        ErrorReport error = errorReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("گزارش خطا یافت نشد: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!error.getOperatorName().equals(username) && !isAdmin) {
            return "redirect:/error/list?error=not_authorized";
        }

        model.addAttribute("errorReport", error);
        return "error_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteError(@PathVariable Long id) {
        ErrorReport error = errorReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("گزارش خطا یافت نشد: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!error.getOperatorName().equals(username) && !isAdmin) {
            return "redirect:/error/list?error=not_authorized";
        }

        errorReportRepository.delete(error);
        return "redirect:/error/list?success=deleted";
    }

    @GetMapping("/download")
    public void downloadErrorReports(HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        List<ErrorReport> errorReports = isAdmin ?
                errorReportRepository.findAll() :
                errorReportRepository.findByOperatorName(username);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=error_reports.pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDType0Font font = PDType0Font.load(document, new ClassPathResource("/fonts/Vazirmatn-Regular.ttf").getFile());

            float y = 750;
            float leading = 18;

            contentStream.beginText();
            contentStream.setFont(font, 11);
            contentStream.newLineAtOffset(50, y);

            for (ErrorReport report : errorReports) {
                if (y <= 100) {
                    contentStream.endText();
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.beginText();
                    contentStream.setFont(font, 11);
                    y = 750;
                    contentStream.newLineAtOffset(50, y);
                }
                contentStream.showText("اپراتور: " + report.getOperatorName());
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("شرح خطا: " + report.getErrorDescription());
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("شدت: " + report.getErrorSeverity());
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("علت: " + report.getErrorCause());
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("راه‌حل: " + report.getResolutionMethod());
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("مدت‌زمان: " + report.getErrorDurationMinutes() + " دقیقه");
                contentStream.newLineAtOffset(0, -leading);
                contentStream.showText("----");
                contentStream.newLineAtOffset(0, -leading);
                y -= leading * 7;
            }

            contentStream.endText();
            contentStream.close();
            document.save(response.getOutputStream());
        }
    }
}
