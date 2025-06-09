package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.ProductionRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ProductionRepository extends CrudRepository<ProductionRecord, Long> {

    @Query("SELECT COALESCE(SUM(p.powerGenerated), 0) FROM ProductionRecord p WHERE p.date = :date")
    int sumPowerGeneratedByDate(LocalDate date);

    @Query("SELECT COALESCE(SUM(p.gasConsumed), 0) FROM ProductionRecord p WHERE p.date = :date")
    int sumGasConsumedByDate(LocalDate date);
}
