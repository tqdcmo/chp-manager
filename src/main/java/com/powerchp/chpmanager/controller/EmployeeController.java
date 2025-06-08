package com.powerchp.chpmanager.controller;

import com.powerchp.chpmanager.model.Employee;
import com.powerchp.chpmanager.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // اضافه کردن PasswordEncoder

    // نمایش فرم ثبت کارمند جدید
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee_form";
    }

    // ذخیره کارمند (ثبت یا ویرایش)
    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {

        if (employee.getId() != null) {
            // اگر این یک ویرایش است، رمز عبور قبلی را حفظ کنیم مگر اینکه رمز جدید داده شده باشد
            Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employee.getId());
            if (existingEmployeeOpt.isPresent()) {
                Employee existingEmployee = existingEmployeeOpt.get();
                if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
                    // اگر رمز جدید خالی است، رمز قبلی رو نگه دار
                    employee.setPassword(existingEmployee.getPassword());
                } else {
                    // رمز جدید داده شده، هش کن
                    String encodedPassword = passwordEncoder.encode(employee.getPassword());
                    employee.setPassword(encodedPassword);
                }
            }
        } else {
            // ثبت کارمند جدید - رمز را حتما هش کن
            String encodedPassword = passwordEncoder.encode(employee.getPassword());
            employee.setPassword(encodedPassword);
        }

        employeeRepository.save(employee);
        return "redirect:/employee/new?success=true";
    }

    // نمایش لیست همه کارمندان
    @GetMapping("/list")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "employee_list";
    }

    // نمایش فرم ویرایش کارمند
    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("کارمند یافت نشد: " + id));
        // برای ویرایش، رمز را خالی بگذار تا در فرم نشان داده نشود
        employee.setPassword("");
        model.addAttribute("employee", employee);
        return "employee_form";
    }

    // حذف کارمند
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
        return "redirect:/employee/list";
    }

    // داشبورد (صفحه اصلی یا خلاصه)
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // نام فایل HTML مربوط به داشبورد
    }
}
