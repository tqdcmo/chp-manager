package com.powerchp.chpmanager.repository;
import com.powerchp.chpmanager.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    /**
     * دریافت لیست شیفت‌هایی که متعلق به یک اپراتور خاص هستند
     */
    List<Shift> findByOperatorName(String operatorName);

    /**
     * دریافت لیست شیفت‌های فعالی که زمان فعلی در بازه شروع تا پایان آن‌ها قرار دارد
     */
    @Query("SELECT s FROM Shift s WHERE s.startTime <= :now AND s.endTime >= :now")
    List<Shift> findCurrentShifts(@Param("now") LocalDateTime now);
}
