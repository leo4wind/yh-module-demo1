package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * JPA embeddable mirroring the domain {@link com.clinicaltrial.ddd.datacollection.domain.model.valueobject.AssessmentScore} value object.
 */
@Embeddable
public class AssessmentScoreJpa {

    @Column(name = "auto_score", length = 100)
    private String score;

    @Column(name = "auto_score_result", length = 100)
    private String result;

    public AssessmentScoreJpa() {
    }

    public AssessmentScoreJpa(String score, String result) {
        this.score = score;
        this.result = result;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
