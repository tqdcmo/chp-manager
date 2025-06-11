package com.powerchp.chpmanager.service;

import com.powerchp.chpmanager.model.Shift;
import com.powerchp.chpmanager.repository.EngineStatusRepository;
import com.powerchp.chpmanager.repository.ErrorReportRepository;
import com.powerchp.chpmanager.repository.ProductionRepository;
import com.powerchp.chpmanager.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DashboardService {

    private final ErrorReportRepository errorReportRepository;
    private final ProductionRepository productionRepository;
    private final EngineStatusRepository engineStatusRepository;
    private final ShiftRepository shiftRepository;

    public DashboardService(
            ErrorReportRepository errorReportRepository,
            ProductionRepository productionRepository,
            EngineStatusRepository engineStatusRepository,
            ShiftRepository shiftRepository
    ) {
        this.errorReportRepository = errorReportRepository;
        this.productionRepository = productionRepository;
        this.engineStatusRepository = engineStatusRepository;
        this.shiftRepository = shiftRepository;
    }

    // 🔴 تعداد خطاهای امروز
    public long getTodayErrorCount() {
        return errorReportRepository.countTodayErrors();
    }

    // 🔵 مجموع برق تولیدی کل امروز (همه اپراتورها)
    public int getTodayPowerGenerated() {
        return productionRepository.sumPowerGeneratedByDate(LocalDate.now());
    }

    // 🟢 مجموع برق تولیدی امروز فقط برای اپراتور فعلی
    public int getTodayPowerGeneratedByOperator(String operatorUsername) {
        return productionRepository.sumPowerGeneratedByDateAndOperator(LocalDate.now(), operatorUsername);
    }

    // 🟡 مجموع گاز مصرفی امروز
    public int getTodayGasConsumed() {
        return productionRepository.sumGasConsumedByDate(LocalDate.now());
    }

    // ⚙️ وضعیت آخر موتور
    public String getEngineStatus(int engineNumber) {
        return engineStatusRepository
                .findTopByEngineNumberOrderByDateDescTimeDesc(engineNumber)
                .map(status -> status.getStatus().name()) // تبدیل Enum به String
                .orElse("نامشخص");
    }

    // ⏱️ زمان شیفت جاری
    public String getCurrentShiftTime() {
        List<Shift> shifts = shiftRepository.findCurrentShifts(LocalDateTime.now());

        if (shifts.isEmpty()) {
            return "نامشخص";
        } else if (shifts.size() > 1) {
            return "تداخل شیفت";
        }

        Shift shift = shifts.get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return shift.getStartTime().format(formatter) + " - " + shift.getEndTime().format(formatter);
    }

    // متد نمونه برای گرفتن برق تولیدی در یک بازه زمانی برای اپراتور (اختیاری)
    public int getPowerGeneratedForOperatorBetween(LocalDate startDate, LocalDate endDate, String operatorUsername) {
        return productionRepository.sumPowerGeneratedBetweenDatesAndOperator(startDate, endDate, operatorUsername);
    }
}
