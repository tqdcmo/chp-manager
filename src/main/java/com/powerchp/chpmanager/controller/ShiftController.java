package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.Shift;
import com.powerchp.chpmanager.service.ShiftService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDFont;
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
import java.security.Principal;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/shift")
public class ShiftController {

    private final ShiftService shiftService;

    @Autowired
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    // بررسی نقش ادمین
    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    // بررسی مجوز دسترسی به گزارش خاص
    private boolean isAuthorized(Shift shift, String username, boolean isAdmin) {
        return shift != null && (isAdmin || shift.getOperatorName().equals(username));
    }

    @GetMapping({"", "/"})
    public String redirectToList(Model model, Principal principal, Authentication auth) {
        return listAllShifts(model, principal, auth);
    }

    // فهرست همه شیفت‌ها (ویژه ادمین‌ها)
    @GetMapping("/list")
    public String listAllShifts(Model model, Principal principal, Authentication auth) {
        if (principal == null || auth == null) return "redirect:/login";

        model.addAttribute("shifts", shiftService.findAllShifts());
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("isAdmin", isAdmin(auth));
        return "shift_list";
    }

    // فهرست شیفت‌های کاربر جاری
    @GetMapping("/my")
    public String listMyShifts(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        model.addAttribute("shifts", shiftService.findShiftsByOperatorName(principal.getName()));
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("isAdmin", false);
        return "shift_list";
    }

    // فرم ایجاد شیفت جدید
    @GetMapping("/new")
    public String newShiftForm(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Shift shift = new Shift();
        shift.setOperatorName(principal.getName());
        model.addAttribute("shift", shift);
        return "shift_form";
    }

    // ذخیره یا به‌روزرسانی شیفت
    @PostMapping("/save")
    public String saveShift(@ModelAttribute Shift shift, Principal principal, Authentication auth) {
        if (principal == null || auth == null) return "redirect:/login";

        String username = principal.getName();
        boolean admin = isAdmin(auth);

        if (shift.getId() == null) {
            shift.setOperatorName(username);  // فقط خود کاربر مجاز به ثبت است
            shiftService.saveShift(shift);
        } else {
            Shift existing = shiftService.findById(shift.getId());
            if (!isAuthorized(existing, username, admin)) {
                return "redirect:/shift/list?error=unauthorized";
            }

            // به‌روزرسانی داده‌ها
            existing.setPowerGenerated(shift.getPowerGenerated());
            existing.setGasConsumed(shift.getGasConsumed());
            existing.setStartTime(shift.getStartTime());
            existing.setEndTime(shift.getEndTime());

            shiftService.saveShift(existing);
        }

        return "redirect:/shift/list";
    }

    // فرم ویرایش
    @GetMapping("/edit/{id}")
    public String editShiftForm(@PathVariable Long id, Model model, Principal principal, Authentication auth, RedirectAttributes redirectAttributes) {
        if (principal == null || auth == null) return "redirect:/login";

        Shift shift = shiftService.findById(id);
        if (!isAuthorized(shift, principal.getName(), isAdmin(auth))) {
            redirectAttributes.addFlashAttribute("error", "دسترسی به ویرایش ندارید یا گزارش وجود ندارد");
            return "redirect:/shift/list";
        }

        model.addAttribute("shift", shift);
        return "shift_form";
    }

    // حذف شیفت
    @PostMapping("/delete/{id}")
    public String deleteShift(@PathVariable Long id, Principal principal, Authentication auth, RedirectAttributes redirectAttributes) {
        if (principal == null || auth == null) return "redirect:/login";

        Shift shift = shiftService.findById(id);
        if (!isAuthorized(shift, principal.getName(), isAdmin(auth))) {
            redirectAttributes.addFlashAttribute("error", "دسترسی به حذف ندارید یا گزارش وجود ندارد");
            return "redirect:/shift/list";
        }

        shiftService.deleteShift(shift);
        redirectAttributes.addFlashAttribute("message", "گزارش با موفقیت حذف شد");
        return "redirect:/shift/list";
    }

    // تولید فایل PDF گزارش شیفت
    @GetMapping("/report/{id}")
    public void downloadPdfReport(@PathVariable Long id, Principal principal, Authentication auth, HttpServletResponse response) throws IOException {
        if (principal == null || auth == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Shift shift = shiftService.findById(id);
        if (shift == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (PDDocument document = new PDDocument();
             InputStream fontStream = getClass().getResourceAsStream("/fonts/Vazirmatn-Regular.ttf")) {

            if (fontStream == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "فونت یافت نشد");
                return;
            }

            PDFont font = PDType0Font.load(document, fontStream);
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                cs.beginText();
                cs.setFont(font, 14);
                cs.setLeading(20f);
                cs.newLineAtOffset(50, 700);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

                cs.showText("گزارش شیفت شماره: " + shift.getId()); cs.newLine();
                cs.showText("اپراتور: " + shift.getOperatorName()); cs.newLine();
                cs.showText("زمان شروع: " + dtf.format(shift.getStartTime())); cs.newLine();
                cs.showText("زمان پایان: " + dtf.format(shift.getEndTime())); cs.newLine();
                cs.showText("توان تولیدی: " + shift.getPowerGenerated() + " کیلووات ساعت"); cs.newLine();
                cs.showText("مصرف گاز: " + shift.getGasConsumed() + " مترمکعب"); cs.newLine();
                cs.showText("راندمان: " + shift.getEfficiency()); cs.newLine();
                cs.showText("مدت زمان: " + shift.getDurationMinutes() + " دقیقه"); cs.newLine();

                cs.endText();
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=shift_report_" + id + ".pdf");
            document.save(response.getOutputStream());
            response.getOutputStream().flush();

        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "خطا در تولید PDF");
        }
    }

}
