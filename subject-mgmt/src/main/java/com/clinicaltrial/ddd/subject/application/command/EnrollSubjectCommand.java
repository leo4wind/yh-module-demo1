package com.clinicaltrial.ddd.subject.application.command;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.util.List;

/**
 * Application command for enrolling a subject into a clinical trial.
 * <p>
 * Supports two enrollment scenarios:
 * <ol>
 *   <li><strong>Screen-then-enroll:</strong> When {@code subjectId} is provided,
 *       the subject has already been screened and is being enrolled.</li>
 *   <li><strong>Direct enrollment:</strong> When {@code subjectId} is null and
 *       all other fields are populated, a new subject is created and enrolled
 *       directly (used when the project has {@code isOpenScreen=false}).</li>
 * </ol>
 * </p>
 */
public class EnrollSubjectCommand {

    private final SubjectId subjectId;
    private final ProjectId projectId;
    private final Long siteId;
    private final Long userId;
    private final String blh;
    private final String syxh;
    private final List<String> groupSubsetIds;

    /**
     * Constructs an EnrollSubjectCommand for the direct-enrollment scenario
     * (subjectId is null, new subject will be created).
     *
     * @param projectId      the project identity
     * @param siteId         the site identity
     * @param userId         the user identity
     * @param blh            病历号 (medical record number); may be null
     * @param syxh           试验序号 (trial sequence number); may be null
     * @param groupSubsetIds group/subset identifiers; may be null
     */
    public EnrollSubjectCommand(ProjectId projectId,
                                 Long siteId,
                                 Long userId,
                                 String blh,
                                 String syxh,
                                 List<String> groupSubsetIds) {
        this(null, projectId, siteId, userId, blh, syxh, groupSubsetIds);
    }

    /**
     * Constructs an EnrollSubjectCommand for the screen-then-enroll scenario
     * (subjectId is provided, subject already exists in SCREENING status).
     *
     * @param subjectId      the existing screened subject's identity
     * @param projectId      the project identity
     * @param siteId         the site identity
     * @param userId         the user identity
     * @param blh            病历号 (medical record number); may be null
     * @param syxh           试验序号 (trial sequence number); may be null
     * @param groupSubsetIds group/subset identifiers; may be null
     */
    public EnrollSubjectCommand(SubjectId subjectId,
                                 ProjectId projectId,
                                 Long siteId,
                                 Long userId,
                                 String blh,
                                 String syxh,
                                 List<String> groupSubsetIds) {
        this.subjectId = subjectId;
        this.projectId = projectId;
        this.siteId = siteId;
        this.userId = userId;
        this.blh = blh;
        this.syxh = syxh;
        this.groupSubsetIds = groupSubsetIds;
    }

    public SubjectId getSubjectId() {
        return subjectId;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public Long getSiteId() {
        return siteId;
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

    public List<String> getGroupSubsetIds() {
        return groupSubsetIds;
    }
}
