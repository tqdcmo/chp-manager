package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.ShiftReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface DashboardRepository extends CrudRepository<ShiftReport, Long> {

    @Query("SELECT COALESCE(SUM(r.powerGenerated), 0) FROM ShiftReport r " +
            "WHERE r.createdBy = :operatorName AND DATE(r.createdAt) = CURRENT_DATE")
    BigDecimal getTodayPowerGeneratedByOperator(@Param("operatorName") String operatorName);

    @Query("SELECT COALESCE(SUM(r.gasConsumed), 0) FROM ShiftReport r " +
            "WHERE DATE(r.createdAt) = CURRENT_DATE")
    BigDecimal getTodayGasConsumed();

    @Query("SELECT COUNT(r) FROM ShiftReport r WHERE DATE(r.createdAt) = CURRENT_DATE")
    Long getTodayErrorCount(); // فرض بر اینکه هر گزارش ممکن است یک خطا داشته باشد
}
