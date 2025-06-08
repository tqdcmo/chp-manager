package com.powerchp.chpmanager.service;

import com.powerchp.chpmanager.model.Employee;
import com.powerchp.chpmanager.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ثبت‌نام کارمند جدید با هش کردن پسورد
    public Employee registerEmployee(Employee employee) {
        // هش کردن رمز عبور
        String encodedPassword = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(encodedPassword);
        // ذخیره در دیتابیس
        return employeeRepository.save(employee);
    }

    // احراز هویت کارمند با نام کاربری و رمز عبور (غیر هش شده)
    public Optional<Employee> authenticate(String username, String rawPassword) {
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            if (employee.isActive() && passwordEncoder.matches(rawPassword, employee.getPassword())) {
                return Optional.of(employee);
            }
        }
        return Optional.empty();
    }
}
