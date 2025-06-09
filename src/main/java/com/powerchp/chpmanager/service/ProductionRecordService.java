package com.powerchp.chpmanager.service;

import com.powerchp.chpmanager.model.ProductionRecord;
import com.powerchp.chpmanager.repository.ProductionRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductionRecordService {

    private final ProductionRecordRepository productionRecordRepository;

    @Autowired
    public ProductionRecordService(ProductionRecordRepository productionRecordRepository) {
        this.productionRecordRepository = productionRecordRepository;
    }

    // ذخیره یا بروزرسانی رکورد تولید
    public ProductionRecord save(ProductionRecord record) {
        return productionRecordRepository.save(record);
    }

    // گرفتن همه رکوردهای تولید
    public List<ProductionRecord> findAll() {
        return productionRecordRepository.findAll();
    }

    // گرفتن رکورد تولید بر اساس شناسه
    public Optional<ProductionRecord> findById(Long id) {
        return productionRecordRepository.findById(id);
    }

    // حذف رکورد تولید بر اساس شناسه
    public void deleteById(Long id) {
        productionRecordRepository.deleteById(id);
    }

    // می‌توانید متدهای سفارشی دیگر مثلاً برای فیلتر بر اساس شیفت یا کاربر اضافه کنید
}
