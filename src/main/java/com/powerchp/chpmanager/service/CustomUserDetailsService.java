package com.powerchp.chpmanager.service;

import com.powerchp.chpmanager.model.Employee;
import com.powerchp.chpmanager.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("کاربر یافت نشد: " + username));

        return User.builder()
                .username(employee.getUsername())
                .password(employee.getPassword())  // پسورد هش شده
                .roles(employee.getRole().replace("ROLE_", "")) // حذف پیشوند ROLE_
                .disabled(!employee.isActive())
                .build();
    }
}
