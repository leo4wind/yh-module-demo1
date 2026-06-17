package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AnalysisProjectIdConverter implements AttributeConverter<AnalysisProjectId, Long> {

    @Override
    public Long convertToDatabaseColumn(AnalysisProjectId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public AnalysisProjectId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new AnalysisProjectId(dbData) : null;
    }
}
