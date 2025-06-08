package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.Employee;
import com.powerchp.chpmanager.repository.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


    @PostMapping("/login")
    public String processLogin(
            @RequestParam String role,
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session
    ) {
        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            model.addAttribute("error", "تمام فیلدها باید پر شوند.");
            return "login";
        }

        // جستجو در دیتابیس برای کاربر با نام کاربری، رمز عبور و نقش
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);

        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();

            if (!employee.isActive()) {
                model.addAttribute("error", "کاربر غیرفعال است.");
                return "login";
            }

            // مقایسه پسورد ساده (تو حالت واقعی هش پسورد باید چک بشه)
            if (employee.getPassword().equals(password) && employee.getRole().equalsIgnoreCase(role)) {
                // ذخیره اطلاعات در سشن
                session.setAttribute("role", employee.getRole());
                session.setAttribute("username", employee.getUsername());
                session.setAttribute("fullName", employee.getFullName());

                // ریدایرکت بر اساس نقش
                if (role.equalsIgnoreCase("ROLE_ADMIN") || role.equalsIgnoreCase("manager")) {
                    return "redirect:/manager/dashboard";
                } else if (role.equalsIgnoreCase("ROLE_OPERATOR") || role.equalsIgnoreCase("operator")) {
                    return "redirect:/operator/dashboard";
                } else {
                    model.addAttribute("error", "نقش نامعتبر است.");
                    return "login";
                }
            } else {
                model.addAttribute("error", "نام کاربری یا کلمه عبور اشتباه است.");
                return "login";
            }
        } else {
            model.addAttribute("error", "کاربر یافت نشد.");
            return "login";
        }
    }


}
