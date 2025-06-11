package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.ProductionRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ProductionRepository extends CrudRepository<ProductionRecord, Long> {

    // جمع برق تولیدی کل امروز
    @Query("SELECT COALESCE(SUM(p.powerGenerated), 0) FROM ProductionRecord p WHERE p.date = :date")
    int sumPowerGeneratedByDate(@Param("date") LocalDate date);

    // جمع گاز مصرفی کل امروز
    @Query("SELECT COALESCE(SUM(p.gasConsumed), 0) FROM ProductionRecord p WHERE p.date = :date")
    int sumGasConsumedByDate(@Param("date") LocalDate date);

    // جمع برق تولیدی امروز برای یک اپراتور خاص
    @Query("SELECT COALESCE(SUM(p.powerGenerated), 0) FROM ProductionRecord p " +
            "WHERE p.date = :date AND p.shift.operatorName = :operatorUsername")
    int sumPowerGeneratedByDateAndOperator(@Param("date") LocalDate date,
                                           @Param("operatorUsername") String operatorUsername);


    // جمع برق تولیدی بین دو تاریخ (برای کل اپراتورها)
    @Query("SELECT COALESCE(SUM(p.powerGenerated), 0) FROM ProductionRecord p " +
            "WHERE p.date BETWEEN :startDate AND :endDate")
    int sumPowerGeneratedBetweenDates(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
    @Query("SELECT COALESCE(SUM(p.powerGenerated), 0) FROM ProductionRecord p WHERE p.date >= :startDate AND p.date <= :endDate AND p.shift.operatorName = :operator")
    Integer sumPowerGeneratedBetweenDatesAndOperator(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     @Param("operator") String operator);

}
