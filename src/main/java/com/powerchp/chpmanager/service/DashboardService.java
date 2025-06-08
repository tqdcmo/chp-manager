package com.powerchp.chpmanager.service;

import com.powerchp.chpmanager.repository.ErrorReportRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ErrorReportRepository errorReportRepository;

    public DashboardService(ErrorReportRepository errorReportRepository) {
        this.errorReportRepository = errorReportRepository;
    }

    public long getTodayErrorCount() {
        return errorReportRepository.countTodayErrors();
    }

    // متدهای دیگر هم می‌تونی اینجا اضافه کنی مثل getPowerGenerated, getGasConsumed و غیره
}
