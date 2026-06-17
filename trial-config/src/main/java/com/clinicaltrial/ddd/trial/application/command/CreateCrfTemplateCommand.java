package com.clinicaltrial.ddd.trial.application.command;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;

/**
 * Application command for creating a new CRF template.
 * <p>
 * Carries the data needed to initialize a CrfTemplate aggregate through the
 * {@link com.clinicaltrial.ddd.trial.application.service.CrfTemplateApplicationService}.
 * </p>
 */
public class CreateCrfTemplateCommand {

    private final String name;
    private final String code;
    private final String category;
    private final String estimateTime;
    private final String notice;
    private final String introduce;

    /**
     * Creates a new CreateCrfTemplateCommand.
     */
    public CreateCrfTemplateCommand(String name, String code, String category,
                                     String estimateTime, String notice, String introduce) {
        this.name = name;
        this.code = code;
        this.category = category;
        this.estimateTime = estimateTime;
        this.notice = notice;
        this.introduce = introduce;
    }

    public String getName() { return name; }
    public String getCode() { return code; }
    public String getCategory() { return category; }
    public String getEstimateTime() { return estimateTime; }
    public String getNotice() { return notice; }
    public String getIntroduce() { return introduce; }
}
