package com.powerchp.chpmanager.repository;

import com.powerchp.chpmanager.model.EngineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EngineStatusRepository extends JpaRepository<EngineStatus, Long> {

    // این متد خودکار جدیدترین رکورد (براساس تاریخ و زمان نزولی) برای موتور مشخص را برمی‌گرداند
    Optional<EngineStatus> findTopByEngineNumberOrderByDateDescTimeDesc(int engineNumber);

}
