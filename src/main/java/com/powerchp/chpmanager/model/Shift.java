package com.powerchp.chpmanager.model;

import jakarta.persistence.*;
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
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "power_generated", nullable = false)
    private Double powerGenerated = 0.0;

    @Column(name = "gas_consumed", nullable = false)
    private Double gasConsumed = 0.0;

    // --- Constructors ---
    public Shift() {
        // Default constructor for JPA
    }

    public Shift(String operatorName, LocalDateTime startTime, LocalDateTime endTime,
                 Double powerGenerated, Double gasConsumed) {
        this.operatorName = Objects.requireNonNull(operatorName, "Operator name must not be null");
        this.startTime = Objects.requireNonNull(startTime, "Start time must not be null");
        this.setEndTime(endTime);  // از متد ست‌تر اصلاح‌شده استفاده می‌کنیم

        this.powerGenerated = powerGenerated != null ? powerGenerated : 0.0;
        this.gasConsumed = gasConsumed != null ? gasConsumed : 0.0;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = Objects.requireNonNull(operatorName, "Operator name must not be null");
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = Objects.requireNonNull(startTime, "Start time must not be null");
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        Objects.requireNonNull(endTime, "End time must not be null");

        if (this.startTime != null && endTime.isBefore(this.startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time.");
        }

        this.endTime = endTime;
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

    // --- Business Logic Methods ---
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return Duration.between(startTime, endTime).toMinutes();
    }

    public double getEfficiency() {
        return (gasConsumed != null && gasConsumed > 0) ? powerGenerated / gasConsumed : 0.0;
    }

    // --- Entity Lifecycle Callback for Validation ---
    @PrePersist
    @PreUpdate
    private void validateTimes() {
        if (startTime != null && endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time.");
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
                '}';
    }
}
