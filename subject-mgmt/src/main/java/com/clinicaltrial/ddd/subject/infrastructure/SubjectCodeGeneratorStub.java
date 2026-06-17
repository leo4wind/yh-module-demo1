package com.clinicaltrial.ddd.subject.infrastructure;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.service.SubjectCodeGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class SubjectCodeGeneratorStub implements SubjectCodeGenerator {

    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public SubjectCode generateNextCode(ProjectId projectId) {
        return new SubjectCode("SUBJ", (int) counter.getAndIncrement());
    }
}
