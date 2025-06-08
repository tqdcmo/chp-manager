package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // پیدا کردن کارمند بر اساس نام کاربری
    Optional<Employee> findByUsername(String username);

    // بررسی وجود کارمند فعال با نام کاربری مشخص
    boolean existsByUsernameAndActiveTrue(String username);

    // پیدا کردن کارمند فعال با نام کاربری و رمز عبور مشخص (برای اعتبارسنجی ساده)
    Optional<Employee> findByUsernameAndPasswordAndActiveTrue(String username, String password);
}
