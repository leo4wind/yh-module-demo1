package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * JPA embeddable mirroring the domain {@link com.clinicaltrial.ddd.datacollection.domain.model.valueobject.FollowUpPeriod} value object.
 */
@Embeddable
public class FollowUpPeriodJpa {

    @Temporal(TemporalType.DATE)
    @Column(name = "follow_start_at")
    private Date followStartAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "follow_end_at")
    private Date followEndAt;

    public FollowUpPeriodJpa() {
    }

    public FollowUpPeriodJpa(Date followStartAt, Date followEndAt) {
        this.followStartAt = followStartAt;
        this.followEndAt = followEndAt;
    }

    public Date getFollowStartAt() {
        return followStartAt;
    }

    public void setFollowStartAt(Date followStartAt) {
        this.followStartAt = followStartAt;
    }

    public Date getFollowEndAt() {
        return followEndAt;
    }

    public void setFollowEndAt(Date followEndAt) {
        this.followEndAt = followEndAt;
    }
}
