package com.powerchp.chpmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "error_reports")
public class ErrorReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String operatorName;

    @Column(nullable = false)
    private LocalDateTime reportTime;

    @Column(length = 1000)
    private String errorDescription;

    @Column(name = "severity", nullable = false)
    private String errorSeverity;

    @Column(length = 1000)
    private String errorCause;

    @Column(length = 1000)
    private String resolutionMethod;

    private Integer errorDurationMinutes;

    public ErrorReport() {
        this.reportTime = LocalDateTime.now();
    }

    public ErrorReport(String operatorName, LocalDateTime reportTime,
                       String errorDescription, String errorSeverity,
                       String errorCause, String resolutionMethod, Integer errorDurationMinutes) {
        this.operatorName = operatorName;
        this.reportTime = reportTime;
        this.errorDescription = errorDescription;
        this.errorSeverity = errorSeverity;
        this.errorCause = errorCause;
        this.resolutionMethod = resolutionMethod;
        this.errorDurationMinutes = errorDurationMinutes;
    }

    // Getter و Setter ها

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

    public LocalDateTime getReportTime() {
        return reportTime;
    }

    public void setReportTime(LocalDateTime reportTime) {
        this.reportTime = reportTime;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorSeverity() {
        return errorSeverity;
    }

    public void setErrorSeverity(String errorSeverity) {
        this.errorSeverity = errorSeverity;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    public String getResolutionMethod() {
        return resolutionMethod;
    }

    public void setResolutionMethod(String resolutionMethod) {
        this.resolutionMethod = resolutionMethod;
    }

    public Integer getErrorDurationMinutes() {
        return errorDurationMinutes;
    }

    public void setErrorDurationMinutes(Integer errorDurationMinutes) {
        this.errorDurationMinutes = errorDurationMinutes;
    }
}
