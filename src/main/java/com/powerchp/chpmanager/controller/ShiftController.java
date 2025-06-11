package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.Shift;
import com.powerchp.chpmanager.service.ShiftService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/shift")
public class ShiftController {

    private final ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    private boolean isAuthorized(Shift shift, String username, boolean admin) {
        return shift != null && (admin || shift.getOperatorName().equals(username));
    }

    @GetMapping({"", "/"})
    public String redirectToMyShifts() {
        return "redirect:/shift/my";
    }

    @GetMapping("/list")
    public String listAllShifts(Model model, Principal principal, Authentication auth) {
        if (principal == null || auth == null) return "redirect:/login";

        List<Shift> shifts = shiftService.findAllShifts();
        model.addAttribute("shifts", shifts);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("isAdmin", isAdmin(auth));

        return "shift_list";
    }

    @GetMapping("/my")
    public String listMyShifts(Model model, Principal principal, Authentication auth) {
        if (principal == null || auth == null) {
            return "redirect:/login";
        }

        String currentUser = principal.getName();
        List<Shift> shifts = shiftService.findShiftsByOperatorName(currentUser);

        model.addAttribute("shifts", shifts);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", isAdmin(auth));

        return "shift_list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Shift shift = new Shift();
        shift.setStartTime(java.time.LocalDateTime.now());
        shift.setEndTime(shift.getStartTime().plusHours(8));
        shift.setOperatorName(principal.getName());

        model.addAttribute("shift", shift);
        return "shift_form";
    }

    @PostMapping("/save")
    public String saveShift(@ModelAttribute Shift shift, Principal principal, Authentication auth, RedirectAttributes redirectAttributes) {
        if (principal == null || auth == null) return "redirect:/login";

        String username = principal.getName();
        boolean admin = isAdmin(auth);
        shift.setOperatorName(username);

        Long excludeId = (shift.getId() != null) ? shift.getId() : -1L;
        if (shiftService.hasOverlappingShift(username, shift.getStartTime(), shift.getEndTime(), excludeId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "تداخل زمانی با شیفت دیگری وجود دارد.");
            return "redirect:/shift/my";
        }

        if (shift.getId() == null) {
            shiftService.saveShift(shift);
            redirectAttributes.addFlashAttribute("successMessage", "شیفت جدید با موفقیت ثبت شد.");
        } else {
            Shift existing = shiftService.findById(shift.getId());
            if (!isAuthorized(existing, username, admin)) {
                redirectAttributes.addFlashAttribute("errorMessage", "دسترسی به ویرایش ندارید.");
                return "redirect:/shift/my";
            }
            existing.setStartTime(shift.getStartTime());
            existing.setEndTime(shift.getEndTime());
            existing.setPowerGenerated(shift.getPowerGenerated());
            existing.setGasConsumed(shift.getGasConsumed());
            shiftService.saveShift(existing);
            redirectAttributes.addFlashAttribute("successMessage", "شیفت با موفقیت به‌روزرسانی شد.");
        }

        return "redirect:/shift/my";
    }

    @GetMapping("/edit/{id}")
    public String editShift(@PathVariable Long id, Model model, Principal principal, Authentication auth, RedirectAttributes redirectAttributes) {
        if (principal == null || auth == null) return "redirect:/login";

        Shift shift = shiftService.findById(id);
        if (!isAuthorized(shift, principal.getName(), isAdmin(auth))) {
            redirectAttributes.addFlashAttribute("errorMessage", "دسترسی غیرمجاز به ویرایش.");
            return "redirect:/shift/my";
        }

        model.addAttribute("shift", shift);
        return "shift_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteShift(@PathVariable Long id, Principal principal, Authentication auth, RedirectAttributes redirectAttributes) {
        if (principal == null || auth == null) return "redirect:/login";

        Shift shift = shiftService.findById(id);
        if (!isAuthorized(shift, principal.getName(), isAdmin(auth))) {
            redirectAttributes.addFlashAttribute("errorMessage", "دسترسی غیرمجاز به حذف.");
            return "redirect:/shift/my";
        }

        shiftService.deleteShift(shift);
        redirectAttributes.addFlashAttribute("successMessage", "گزارش با موفقیت حذف شد.");
        return "redirect:/shift/my";
    }

    // ✅ همه کاربران وارد شده می‌توانند گزارش PDF هر شیفتی را دانلود کنند
    @GetMapping("/report/{id}")
    public void generatePdfReport(@PathVariable Long id, Principal principal, Authentication auth, HttpServletResponse response) throws IOException {
        if (principal == null || auth == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "ابتدا وارد شوید.");
            return;
        }

        Shift shift = shiftService.findById(id);
        if (shift == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "گزارش یافت نشد.");
            return;
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (InputStream fontStream = getClass().getResourceAsStream("/fonts/Vazirmatn-Regular.ttf");
                 PDPageContentStream cs = new PDPageContentStream(document, page)) {

                if (fontStream == null) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "فونت فارسی بارگذاری نشد.");
                    return;
                }

                var font = PDType0Font.load(document, fontStream);

                cs.beginText();
                cs.setFont(font, 14);
                cs.setLeading(22f);
                cs.newLineAtOffset(50, 700);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

                cs.showText("گزارش شیفت شماره: " + shift.getId()); cs.newLine();
                cs.showText("اپراتور: " + shift.getOperatorName()); cs.newLine();
                cs.showText("زمان شروع: " + dtf.format(shift.getStartTime())); cs.newLine();
                cs.showText("زمان پایان: " + dtf.format(shift.getEndTime())); cs.newLine();
                cs.showText("توان تولیدی: " + shift.getPowerGenerated() + " کیلووات‌ساعت"); cs.newLine();
                cs.showText("مصرف گاز: " + shift.getGasConsumed() + " مترمکعب"); cs.newLine();
                cs.showText("مدت زمان: " + shift.getDurationMinutes() + " دقیقه"); cs.newLine();
                cs.showText("راندمان: " + shift.getEfficiency()); cs.newLine();

                cs.endText();
            }

            response.setContentType("application/pdf");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition", "attachment; filename=shift_report_" + id + ".pdf");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

            document.save(response.getOutputStream());
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "خطا در تولید PDF: " + e.getMessage());
        }
    }
}
