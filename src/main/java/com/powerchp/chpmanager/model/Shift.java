package com.powerchp.chpmanager.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operator_name", nullable = false, length = 100)
    private String operatorName;

    @Column(name = "start_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @Column(name = "power_generated", nullable = false)
    private Double powerGenerated = 0.0;

    @Column(name = "gas_consumed", nullable = false)
    private Double gasConsumed = 0.0;

    @Column(name = "efficiency")
    private Double efficiency = 0.0;

    @Column(name = "description", length = 1000)
    private String description;

    // --- Constructors ---
    public Shift() {
        // Default constructor for JPA
    }

    public Shift(String operatorName, LocalDateTime startTime, LocalDateTime endTime,
                 Double powerGenerated, Double gasConsumed, String description) {
        setOperatorName(operatorName);
        setStartTime(startTime);
        setEndTime(endTime);
        setPowerGenerated(powerGenerated);
        setGasConsumed(gasConsumed);
        setDescription(description);
        updateEfficiency();  // خودکار محاسبه راندمان
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = Objects.requireNonNull(operatorName, "نام اپراتور نمی‌تواند خالی باشد");
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = Objects.requireNonNull(startTime, "زمان شروع نمی‌تواند خالی باشد");
        validateTimes();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = Objects.requireNonNull(endTime, "زمان پایان نمی‌تواند خالی باشد");
        validateTimes();
    }

    public Double getPowerGenerated() {
        return powerGenerated;
    }

    public void setPowerGenerated(Double powerGenerated) {
        this.powerGenerated = powerGenerated != null ? powerGenerated : 0.0;
    }

    public Double getGasConsumed() {
        return gasConsumed;
    }

    public void setGasConsumed(Double gasConsumed) {
        this.gasConsumed = gasConsumed != null ? gasConsumed : 0.0;
    }

    public Double getEfficiency() {
        return efficiency != null ? efficiency : 0.0;
    }

    public void setEfficiency(Double efficiency) {
        // فقط Controller اجازه دارد این را ست کند
        this.efficiency = efficiency != null ? efficiency : 0.0;
    }

    public void updateEfficiency() {
        if (gasConsumed != null && gasConsumed > 0) {
            this.efficiency = powerGenerated / gasConsumed;
        } else {
            this.efficiency = 0.0;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // --- Business Logic ---
    public long getDurationMinutes() {
        return (startTime != null && endTime != null)
                ? Duration.between(startTime, endTime).toMinutes()
                : 0;
    }

    // --- Entity Lifecycle Validation ---
    @PrePersist
    @PreUpdate
    private void validateTimes() {
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("زمان پایان نمی‌تواند قبل از زمان شروع باشد");
        }
    }

    // --- equals & hashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shift shift)) return false;
        return Objects.equals(id, shift.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- toString ---
    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", operatorName='" + operatorName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", powerGenerated=" + powerGenerated +
                ", gasConsumed=" + gasConsumed +
                ", efficiency=" + efficiency +
                ", description='" + description + '\'' +
                '}';
    }
}
