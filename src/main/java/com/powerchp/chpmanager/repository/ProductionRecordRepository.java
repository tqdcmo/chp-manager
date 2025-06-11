package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.ProductionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ProductionRecordRepository extends JpaRepository<ProductionRecord, Long> {

    @Query("SELECT SUM(pr.powerGenerated) FROM ProductionRecord pr WHERE pr.shift.id = :shiftId")
    Double sumPowerGeneratedByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT SUM(pr.gasConsumed) FROM ProductionRecord pr WHERE pr.shift.id = :shiftId")
    Double sumGasConsumedByShiftId(@Param("shiftId") Long shiftId);



    @Query("SELECT COALESCE(SUM(p.powerGenerated), 0) FROM ProductionRecord p WHERE p.date BETWEEN :startDate AND :endDate AND p.shift.operatorName = :operatorName")
    Double sumPowerGeneratedByOperatorBetweenDates(@Param("operatorName") String operatorName,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
    @Query("SELECT COALESCE(SUM(p.powerGenerated), 0) FROM ProductionRecord p WHERE p.date = :date AND p.shift.operatorName = :operatorName")
    Double sumPowerGeneratedByDateAndOperator(@Param("date") LocalDate date, @Param("operatorName") String operatorName);


}
