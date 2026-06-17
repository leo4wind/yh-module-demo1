package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisResultId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AnalysisResultIdConverter implements AttributeConverter<AnalysisResultId, Long> {

    @Override
    public Long convertToDatabaseColumn(AnalysisResultId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public AnalysisResultId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new AnalysisResultId(dbData) : null;
    }
}
