package com.clinicaltrial.ddd.subject.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

/**
 * JPA embeddable mirroring the domain {@link com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo} value object.
 */
@Embeddable
public class ScreeningInfoJpa {

    @Column(name = "screening_date")
    private LocalDate screeningDate;

    @Column(name = "screening_result", length = 20)
    private String screeningResult;

    @Column(name = "screening_remarks", length = 500)
    private String remarks;

    public ScreeningInfoJpa() {
    }

    public ScreeningInfoJpa(LocalDate screeningDate, String screeningResult, String remarks) {
        this.screeningDate = screeningDate;
        this.screeningResult = screeningResult;
        this.remarks = remarks;
    }

    public LocalDate getScreeningDate() {
        return screeningDate;
    }

    public void setScreeningDate(LocalDate screeningDate) {
        this.screeningDate = screeningDate;
    }

    public String getScreeningResult() {
        return screeningResult;
    }

    public void setScreeningResult(String screeningResult) {
        this.screeningResult = screeningResult;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
