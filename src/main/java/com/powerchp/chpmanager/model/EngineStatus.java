package com.powerchp.chpmanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "engine_status")
public class EngineStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int engineNumber;

    // بهتر است از enum استفاده شود
    @Enumerated(EnumType.STRING)
    private EngineState status;

    private LocalDate date;

    private LocalTime time;

    private String operatorName;

    // Default constructor for JPA
    public EngineStatus() {}

    // Constructor with parameters for convenience
    public EngineStatus(int engineNumber, EngineState status, LocalDate date, LocalTime time, String operatorName) {
        this.engineNumber = engineNumber;
        this.status = status;
        this.date = date;
        this.time = time;
        this.operatorName = operatorName;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(int engineNumber) {
        this.engineNumber = engineNumber;
    }

    public EngineState getStatus() {
        return status;
    }

    public void setStatus(EngineState status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    // Override toString for debug
    @Override
    public String toString() {
        return "EngineStatus{" +
                "id=" + id +
                ", engineNumber=" + engineNumber +
                ", status=" + status +
                ", date=" + date +
                ", time=" + time +
                ", operatorName='" + operatorName + '\'' +
                '}';
    }

    // Override equals and hashCode for entity comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EngineStatus)) return false;
        EngineStatus that = (EngineStatus) o;
        return engineNumber == that.engineNumber &&
                Objects.equals(id, that.id) &&
                status == that.status &&
                Objects.equals(date, that.date) &&
                Objects.equals(time, that.time) &&
                Objects.equals(operatorName, that.operatorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, engineNumber, status, date, time, operatorName);
    }
}

