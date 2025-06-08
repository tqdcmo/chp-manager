package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    // اگر بخوایم همه شیفت‌های یک اپراتور رو بگیریم
    List<Shift> findByOperatorName(String operatorName);
}
