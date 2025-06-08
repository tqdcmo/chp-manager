package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.ErrorReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ErrorReportRepository extends JpaRepository<ErrorReport, Long> {

    // جستجو بر اساس نام اپراتور
    List<ErrorReport> findByOperatorName(String operatorName);

    // شمارش خطاهای ثبت‌شده امروز
    @Query("SELECT COUNT(e) FROM ErrorReport e WHERE DATE(e.reportTime) = CURRENT_DATE")
    long countTodayErrors();
}
