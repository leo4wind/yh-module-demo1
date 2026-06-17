package com.clinicaltrial.ddd.subject.domain.model.aggregate;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectFallOffReason;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.time.LocalDate;
import java.util.Collections;

/**
 * Shared test fixtures for {@link SubjectTest}.
 * <p>
 * Provides factory methods for creating subjects in various lifecycle states,
 * as well as commonly used value objects. All helpers return fresh instances.
 * </p>
 */
public final class SubjectTestFixtures {

    private SubjectTestFixtures() {
        // utility class
    }

    // ---------------------------------------------------------------
    // Identity / Value object helpers
    // ---------------------------------------------------------------

    static SubjectId aSubjectId() {
        return new SubjectId(1L);
    }

    static ProjectId aProjectId() {
        return new ProjectId(100L);
    }

    static SubjectCode aSubjectCode() {
        return new SubjectCode("TEST", 1);
    }

    static ScreeningInfo screeningPassed() {
        return new ScreeningInfo(LocalDate.of(2026, 6, 16),
                ScreeningInfo.ScreeningResult.PASS, "OK");
    }

    static ScreeningInfo screeningFailed() {
        return new ScreeningInfo(LocalDate.of(2026, 6, 16),
                ScreeningInfo.ScreeningResult.FAIL, "Not eligible");
    }

    static SubjectFallOffReason aReason() {
        return new SubjectFallOffReason("AE", "Adverse Event",
                LocalDate.of(2026, 6, 16));
    }

    // ---------------------------------------------------------------
    // Aggregate helpers in various lifecycle states
    // ---------------------------------------------------------------

    /**
     * Creates a subject ready for screening — no status set yet.
     */
    static Subject subjectForScreening() {
        return Subject.createForEnrollment(
                aProjectId(), 1L, 1L, "BL001", "SY001",
                Collections.emptyList());
    }

    /**
     * Creates a subject that has been screened (status = SCREENING).
     */
    static Subject screenedSubject() {
        Subject s = subjectForScreening();
        s.screen(screeningPassed());
        return s;
    }

    /**
     * Creates a subject that has been enrolled (status = ENROLLED).
     */
    static Subject enrolledSubject() {
        Subject s = screenedSubject();
        s.enroll();
        s.assignCode(aSubjectCode());
        return s;
    }

    /**
     * Creates a subject that is active (status = ACTIVE).
     */
    static Subject activeSubject() {
        Subject s = enrolledSubject();
        s.activate();
        return s;
    }
}
