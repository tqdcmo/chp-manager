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
     * یافتن تمام شیفت‌هایی که توسط اپراتور خاص ثبت شده‌اند
     * @param operatorName نام اپراتور
     * @return لیست شیفت‌ها
     */
    List<Shift> findByOperatorName(String operatorName);

    /**
     * یافتن شیفت‌هایی که در لحظه فعلی فعال هستند
     * (زمان فعلی بین startTime و endTime قرار دارد)
     * @param now زمان فعلی
     * @return لیست شیفت‌های فعال
     */
    @Query("SELECT s FROM Shift s WHERE :now BETWEEN s.startTime AND s.endTime")
    List<Shift> findCurrentShifts(@Param("now") LocalDateTime now);

}
