package com.powerchp.chpmanager.service;

import com.powerchp.chpmanager.model.Shift;
import com.powerchp.chpmanager.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public List<Shift> findAllShifts() {
        return shiftRepository.findAll();
    }

    public List<Shift> findShiftsByOperatorName(String operatorName) {
        return shiftRepository.findByOperatorName(operatorName);
    }

    public Shift saveShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    public void deleteShift(Shift shift) {
        shiftRepository.delete(shift);
    }

    public Shift findById(Long id) {
        return shiftRepository.findById(id).orElse(null);
    }

    public boolean hasOverlappingShift(String operatorName, LocalDateTime start, LocalDateTime end, Long excludeId) {
        return shiftRepository.findByOperatorName(operatorName).stream()
                .filter(shift -> excludeId == null || !shift.getId().equals(excludeId))
                .anyMatch(shift ->
                        (start.isBefore(shift.getEndTime()) && end.isAfter(shift.getStartTime()))
                );
    }

    /**
     * بررسی می‌کند که آیا شیفت فعلی موجود است یا نه، و اگر نه، آن را درج می‌کند.
     */
    public void createCurrentShiftIfNotExists() {
        LocalDateTime now = LocalDateTime.now();

        // تعریف شیفت‌ها (3 شیفت ثابت)
        LocalTime[][] shiftTimes = {
                {LocalTime.MIDNIGHT, LocalTime.of(8, 0)},         // 00:00 - 08:00
                {LocalTime.of(8, 0), LocalTime.of(16, 0)},        // 08:00 - 16:00
                {LocalTime.of(16, 0), LocalTime.MIDNIGHT}         // 16:00 - 00:00 (روز بعد)
        };

        for (LocalTime[] times : shiftTimes) {
            LocalDateTime start = LocalDateTime.of(LocalDate.now(), times[0]);
            LocalDateTime end = times[1].equals(LocalTime.MIDNIGHT)
                    ? LocalDateTime.of(LocalDate.now().plusDays(1), times[1])
                    : LocalDateTime.of(LocalDate.now(), times[1]);

            if (now.isAfter(start) && now.isBefore(end)) {
                boolean exists = shiftRepository.existsByStartTimeAndEndTime(start, end);
                if (!exists) {
                    Shift shift = new Shift();
                    shift.setStartTime(start);
                    shift.setEndTime(end);
                    shift.setOperatorName("نامشخص"); // اینجا می‌تونی نام اپراتور فعلی رو بذاری

                    shiftRepository.save(shift);
                    System.out.println("✅ شیفت جدید اضافه شد: " + start + " تا " + end);
                } else {
                    System.out.println("ℹ️ شیفت جاری قبلاً وجود دارد.");
                }
                break;
            }
        }
    }
}
