package com.powerchp.chpmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "error_reports")
public class ErrorReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operatorName;

    private LocalDateTime reportTime;

    private String errorDescription;

    private String severity;

    // 🆕 New fields
    private String errorCause;

    private String resolutionMethod;

    private Integer errorDurationMinutes;

    // Constructors
    public ErrorReport() {
    }

    public ErrorReport(String operatorName, LocalDateTime reportTime,
                       String errorDescription, String severity,
                       String errorCause, String resolutionMethod, Integer errorDurationMinutes) {
        this.operatorName = operatorName;
        this.reportTime = reportTime;
        this.errorDescription = errorDescription;
        this.severity = severity;
        this.errorCause = errorCause;
        this.resolutionMethod = resolutionMethod;
        this.errorDurationMinutes = errorDurationMinutes;
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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
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
