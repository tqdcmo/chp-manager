package com.powerchp.chpmanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "production_records")
public class ProductionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double powerGenerated;

    @Column(nullable = false)
    private double gasConsumed;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    // --- Constructors ---

    public ProductionRecord() {
    }

    public ProductionRecord(double powerGenerated, double gasConsumed, LocalDate date, LocalTime time, Shift shift) {
        this.powerGenerated = powerGenerated;
        this.gasConsumed = gasConsumed;
        this.date = date;
        this.time = time;
        this.shift = shift;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPowerGenerated() {
        return powerGenerated;
    }

    public void setPowerGenerated(double powerGenerated) {
        if (powerGenerated < 0) {
            throw new IllegalArgumentException("Power generated cannot be negative");
        }
        this.powerGenerated = powerGenerated;
    }

    public double getGasConsumed() {
        return gasConsumed;
    }

    public void setGasConsumed(double gasConsumed) {
        if (gasConsumed < 0) {
            throw new IllegalArgumentException("Gas consumed cannot be negative");
        }
        this.gasConsumed = gasConsumed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
        this.time = time;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }
        this.shift = shift;
    }

    /**
     * این متد نام اپراتور مرتبط با این رکورد تولید را از شیفت برمی‌گرداند.
     * در صورتی که شیفت یا نام اپراتور مقداردهی نشده باشد، مقدار null برمی‌گرداند.
     */
    @Transient
    public String getOperatorName() {
        if (shift != null) {
            return shift.getOperatorName();
        }
        return null;
    }

    // --- equals و hashCode ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductionRecord)) return false;
        ProductionRecord that = (ProductionRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- toString ---

    @Override
    public String toString() {
        return "ProductionRecord{" +
                "id=" + id +
                ", powerGenerated=" + powerGenerated +
                ", gasConsumed=" + gasConsumed +
                ", date=" + date +
                ", time=" + time +
                ", shiftId=" + (shift != null ? shift.getId() : null) +
                '}';
    }
}
