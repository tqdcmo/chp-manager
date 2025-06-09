package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.ProductionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionRecordRepository extends JpaRepository<ProductionRecord, Long> {

    @Query("SELECT SUM(pr.amount) FROM ProductionRecord pr WHERE pr.shift.id = :shiftId")
    Double sumPowerGeneratedByShiftId(@Param("shiftId") Long shiftId);

    @Query("SELECT SUM(pr.gasConsumed) FROM ProductionRecord pr WHERE pr.shift.id = :shiftId")
    Double sumGasConsumedByShiftId(@Param("shiftId") Long shiftId);
}
