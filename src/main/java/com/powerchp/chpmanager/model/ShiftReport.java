package com.powerchp.chpmanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class ShiftReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String createdBy; // نام اپراتور ایجادکننده گزارش

    private BigDecimal powerGenerated;

    private BigDecimal gasConsumed;

    private LocalDateTime createdAt;

    // سایر فیلدها، وضعیت‌ها و اطلاعات موتور

    // --- getters & setters ---
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getCreatedBy() { return createdBy; }

    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public BigDecimal getPowerGenerated() { return powerGenerated; }

    public void setPowerGenerated(BigDecimal powerGenerated) { this.powerGenerated = powerGenerated; }

    public BigDecimal getGasConsumed() { return gasConsumed; }

    public void setGasConsumed(BigDecimal gasConsumed) { this.gasConsumed = gasConsumed; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
