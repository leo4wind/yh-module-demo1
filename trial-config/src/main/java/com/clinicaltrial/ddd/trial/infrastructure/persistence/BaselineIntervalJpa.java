package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * JPA embeddable mirroring the domain {@link com.clinicaltrial.ddd.trial.domain.model.valueobject.BaselineInterval} value object.
 */
@Embeddable
public class BaselineIntervalJpa {

    @Column(name = "baseline_interval")
    private Long interval;

    @Column(name = "baseline_unit", length = 20)
    private String normalizedUnit;

    public BaselineIntervalJpa() {
    }

    public BaselineIntervalJpa(Long interval, String normalizedUnit) {
        this.interval = interval;
        this.normalizedUnit = normalizedUnit;
    }

    public Long getInterval() {
        return interval;
    }

    public String getNormalizedUnit() {
        return normalizedUnit;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public void setNormalizedUnit(String normalizedUnit) {
        this.normalizedUnit = normalizedUnit;
    }
}
