package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * JPA embeddable mirroring the domain {@link com.clinicaltrial.ddd.trial.domain.model.valueobject.WindowPeriod} value object.
 */
@Embeddable
public class WindowPeriodJpa {

    @Column(name = "window_before_days")
    private Long beforeDays;

    @Column(name = "window_after_days")
    private Long afterDays;

    public WindowPeriodJpa() {
    }

    public WindowPeriodJpa(Long beforeDays, Long afterDays) {
        this.beforeDays = beforeDays;
        this.afterDays = afterDays;
    }

    public Long getBeforeDays() {
        return beforeDays;
    }

    public Long getAfterDays() {
        return afterDays;
    }

    public void setBeforeDays(Long beforeDays) {
        this.beforeDays = beforeDays;
    }

    public void setAfterDays(Long afterDays) {
        this.afterDays = afterDays;
    }
}
