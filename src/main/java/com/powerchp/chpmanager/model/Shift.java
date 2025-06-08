package com.powerchp.chpmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operatorName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double powerGenerated;

    private Double gasConsumed;

    // Constructors
    public Shift() {
    }

    public Shift(String operatorName, LocalDateTime startTime, LocalDateTime endTime,
                 Double powerGenerated, Double gasConsumed) {
        this.operatorName = operatorName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.powerGenerated = powerGenerated;
        this.gasConsumed = gasConsumed;
    }

    // Getters and Setters
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
        this.operatorName = operatorName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getPowerGenerated() {
        return powerGenerated;
    }

    public void setPowerGenerated(Double powerGenerated) {
        this.powerGenerated = powerGenerated;
    }

    public Double getGasConsumed() {
        return gasConsumed;
    }

    public void setGasConsumed(Double gasConsumed) {
        this.gasConsumed = gasConsumed;
    }
}
