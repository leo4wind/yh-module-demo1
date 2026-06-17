package com.clinicaltrial.ddd.subject.application.command;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo;

import java.time.LocalDate;

/**
 * Application command for screening a subject.
 * <p>
 * Carries the data required to initiate the screening evaluation
 * for a new subject in a clinical trial project.
 * </p>
 */
public class ScreenSubjectCommand {

    private final ProjectId projectId;
    private final Long siteId;
    private final LocalDate screeningDate;
    private final ScreeningInfo.ScreeningResult screeningResult;
    private final String remarks;
    private final Long userId;
    private final String blh;
    private final String syxh;
    private final String name;
    private final String gender;
    private final Integer age;

    /**
     * Constructs a new ScreenSubjectCommand.
     *
     * @param projectId       the project the subject belongs to
     * @param siteId          the site identity
     * @param screeningDate   the date of screening
     * @param screeningResult the result of screening (PASS/FAIL)
     * @param remarks         optional remarks (may be null)
     * @param userId          the user identity of the subject
     * @param blh             病历号 (medical record number); may be null
     * @param syxh            试验序号 (trial sequence number); may be null
     * @param name            受试者姓名; may be null
     * @param gender          性别; may be null
     * @param age             年龄; may be null
     */
    public ScreenSubjectCommand(ProjectId projectId,
                                 Long siteId,
                                 LocalDate screeningDate,
                                 ScreeningInfo.ScreeningResult screeningResult,
                                 String remarks,
                                 Long userId,
                                 String blh,
                                 String syxh,
                                 String name,
                                 String gender,
                                 Integer age) {
        this.projectId = projectId;
        this.siteId = siteId;
        this.screeningDate = screeningDate;
        this.screeningResult = screeningResult;
        this.remarks = remarks;
        this.userId = userId;
        this.blh = blh;
        this.syxh = syxh;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    public ScreenSubjectCommand(ProjectId projectId,
                                 Long siteId,
                                 LocalDate screeningDate,
                                 ScreeningInfo.ScreeningResult screeningResult,
                                 String remarks,
                                 Long userId,
                                 String blh,
                                 String syxh) {
        this(projectId, siteId, screeningDate, screeningResult, remarks, userId,
                blh, syxh, null, null, null);
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public LocalDate getScreeningDate() {
        return screeningDate;
    }

    public ScreeningInfo.ScreeningResult getScreeningResult() {
        return screeningResult;
    }

    public String getRemarks() {
        return remarks;
    }

    public Long getUserId() {
        return userId;
    }

    public String getBlh() {
        return blh;
    }

    public String getSyxh() {
        return syxh;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public Integer getAge() {
        return age;
    }
}
