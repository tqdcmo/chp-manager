package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    /**
     * دریافت لیست شیفت‌هایی که متعلق به یک اپراتور خاص هستند
     * بر اساس نام کاربری ذخیره‌شده در فیلد operatorName
     */
    List<Shift> findByOperatorName(String operatorName);
}
