package com.clinicaltrial.ddd.trial.application.command;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectType;

import java.util.Date;

/**
 * Application command for creating a new clinical trial project.
 * <p>
 * Carries the data needed to initialize a project aggregate through the
 * {@link com.clinicaltrial.ddd.trial.application.service.ProjectApplicationService}.
 * </p>
 */
public class CreateProjectCommand {

    private final String title;
    private final ProjectType type;
    private final String abbreviation;
    private final String prefix;
    private final boolean openScreen;
    private final Integer expectedSubjectSize;
    private final String clinicalNumber;
    private final String registrationNo;
    private final Date expectStartAt;
    private final Date expectEndAt;
    private final String purpose;
    private final String createUserId;

    /**
     * Creates a new CreateProjectCommand.
     */
    public CreateProjectCommand(String title, ProjectType type, String abbreviation,
                                 String prefix, boolean openScreen, Integer expectedSubjectSize,
                                 String clinicalNumber, String registrationNo,
                                 Date expectStartAt, Date expectEndAt,
                                 String purpose, String createUserId) {
        this.title = title;
        this.type = type;
        this.abbreviation = abbreviation;
        this.prefix = prefix;
        this.openScreen = openScreen;
        this.expectedSubjectSize = expectedSubjectSize;
        this.clinicalNumber = clinicalNumber;
        this.registrationNo = registrationNo;
        this.expectStartAt = expectStartAt;
        this.expectEndAt = expectEndAt;
        this.purpose = purpose;
        this.createUserId = createUserId;
    }

    public String getTitle() { return title; }
    public ProjectType getType() { return type; }
    public String getAbbreviation() { return abbreviation; }
    public String getPrefix() { return prefix; }
    public boolean isOpenScreen() { return openScreen; }
    public Integer getExpectedSubjectSize() { return expectedSubjectSize; }
    public String getClinicalNumber() { return clinicalNumber; }
    public String getRegistrationNo() { return registrationNo; }
    public Date getExpectStartAt() { return expectStartAt; }
    public Date getExpectEndAt() { return expectEndAt; }
    public String getPurpose() { return purpose; }
    public String getCreateUserId() { return createUserId; }
}
