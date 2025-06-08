package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.Employee;
import com.powerchp.chpmanager.repository.EmployeeRepository;
import com.powerchp.chpmanager.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeService employeeService;

    // نمایش فرم ثبت کارمند جدید
    @GetMapping("/new")
    public String showForm(Model model, Principal principal) {
        if (!employeeService.hasRole(principal.getName(), "ROLE_ADMIN")) {
            throw new AccessDeniedException("شما اجازه انجام این عملیات را ندارید.");
        }
        model.addAttribute("employee", new Employee());
        return "employee_form";
    }

    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee, Principal principal) {
        String username = principal.getName();

        if (!employeeService.hasRole(username, "ROLE_ADMIN")) {
            throw new AccessDeniedException("شما اجازه انجام این عملیات را ندارید.");
        }

        if (employee.getId() != null) {
            // حالت ویرایش
            Optional<Employee> existingOpt = employeeRepository.findById(employee.getId());
            if (existingOpt.isPresent()) {
                Employee existing = existingOpt.get();

                if (existing.getId() == 1L && !username.equals("admin")) {
                    throw new AccessDeniedException("ادمین اصلی قابل ویرایش توسط شما نیست.");
                }

                if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
                    employee.setPassword(existing.getPassword());
                } else {
                    employee.setPassword(passwordEncoder.encode(employee.getPassword()));
                }

                // preserve createdBy
                employee.setCreatedBy(existing.getCreatedBy());

            }
        } else {
            // ثبت جدید
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            employee.setCreatedBy(username); // 👈 ثبت کاربر ایجادکننده
        }

        employeeRepository.save(employee);
        return "redirect:/employee/list";
    }


    @GetMapping("/list")
    public String listEmployees(Model model, Principal principal) {
        if (!employeeService.hasRole(principal.getName(), "ROLE_ADMIN")) {
            throw new AccessDeniedException("شما اجازه انجام این عملیات را ندارید.");
        }
        model.addAttribute("employees", employeeRepository.findAll());
        return "employee_list";
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable Long id, Model model, Principal principal) {
        if (!employeeService.hasRole(principal.getName(), "ROLE_ADMIN")) {
            throw new AccessDeniedException("شما اجازه انجام این عملیات را ندارید.");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("کارمند یافت نشد: " + id));

        if (employee.getId() == 1L && !principal.getName().equals("admin")) {
            throw new AccessDeniedException("ادمین اصلی قابل ویرایش توسط شما نیست.");
        }

        employee.setPassword("");
        model.addAttribute("employee", employee);
        return "employee_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, Principal principal, Model model) {
        if (!employeeService.hasRole(principal.getName(), "ROLE_ADMIN")) {
            throw new AccessDeniedException("شما اجازه انجام این عملیات را ندارید.");
        }

        if (id == 1L) {
            model.addAttribute("errorMessage", "مدیر اصلی قابل حذف نیست.");
            return "redirect:/employee/list";
        }

        employeeRepository.deleteById(id);
        return "redirect:/employee/list";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
