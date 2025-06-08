package com.powerchp.chpmanager.initializer;

import com.powerchp.chpmanager.model.Employee;
import com.powerchp.chpmanager.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.count() == 0) {
            Employee admin = new Employee();
            admin.setFullName("Admin User");
            admin.setUsername("admin");
            admin.setRole("ROLE_ADMIN");
            admin.setActive(true);
            // رمز هش‌شده
            admin.setPassword(passwordEncoder.encode("admin123"));
            employeeRepository.save(admin);
            System.out.println("Admin user created with username 'admin' and password 'admin123'");
        }
    }
}
