package com.powerchp.chpmanager.service;

import com.powerchp.chpmanager.model.EngineState;
import com.powerchp.chpmanager.model.EngineStatus;
import com.powerchp.chpmanager.repository.EngineStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class EngineStatusService {

    private final EngineStatusRepository engineStatusRepository;

    @Autowired
    public EngineStatusService(EngineStatusRepository engineStatusRepository) {
        this.engineStatusRepository = engineStatusRepository;
    }

    /**
     * ذخیره وضعیت جدید موتور با اطلاعات اپراتور و زمان فعلی
     * حالا ورودی به جای رشته، Enum می‌گیرد
     */
    public void saveEngineStatus(int engineNumber, EngineState engineState, String operatorName) {
        EngineStatus engineStatus = new EngineStatus();
        engineStatus.setEngineNumber(engineNumber);
        engineStatus.setStatus(engineState);
        engineStatus.setOperatorName(operatorName);
        engineStatus.setDate(LocalDate.now());
        engineStatus.setTime(LocalTime.now());

        engineStatusRepository.save(engineStatus);

        System.out.printf("✅ ذخیره شد: موتور %d، وضعیت: %s، اپراتور: %s\n",
                engineNumber,
                engineState.getFaName(),
                operatorName);
    }

    /**
     * دریافت آخرین وضعیت ثبت‌شده موتور به صورت Optional از نوع EngineState
     */
    public Optional<EngineState> getCurrentEngineState(int engineNumber) {
        Optional<EngineStatus> latestStatus = engineStatusRepository
                .findTopByEngineNumberOrderByDateDescTimeDesc(engineNumber);

        return latestStatus.map(EngineStatus::getStatus);
    }
}
