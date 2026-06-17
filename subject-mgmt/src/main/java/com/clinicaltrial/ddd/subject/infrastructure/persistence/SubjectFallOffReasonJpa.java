package com.clinicaltrial.ddd.subject.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

/**
 * JPA embeddable mirroring the domain {@link com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectFallOffReason} value object.
 */
@Embeddable
public class SubjectFallOffReasonJpa {

    @Column(name = "falloff_reason_code", length = 100)
    private String reasonCode;

    @Column(name = "falloff_reason_description", length = 500)
    private String reasonDescription;

    @Column(name = "falloff_date")
    private LocalDate fallOffDate;

    public SubjectFallOffReasonJpa() {
    }

    public SubjectFallOffReasonJpa(String reasonCode, String reasonDescription, LocalDate fallOffDate) {
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDescription;
        this.fallOffDate = fallOffDate;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public LocalDate getFallOffDate() {
        return fallOffDate;
    }

    public void setFallOffDate(LocalDate fallOffDate) {
        this.fallOffDate = fallOffDate;
    }
}
