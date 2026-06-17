package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ProjectIdConverter implements AttributeConverter<ProjectId, Long> {

    @Override
    public Long convertToDatabaseColumn(ProjectId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public ProjectId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new ProjectId(dbData) : null;
    }
}
