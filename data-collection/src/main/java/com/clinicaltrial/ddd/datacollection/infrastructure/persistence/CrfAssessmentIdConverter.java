package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CrfAssessmentIdConverter implements AttributeConverter<CrfAssessmentId, Long> {

    @Override
    public Long convertToDatabaseColumn(CrfAssessmentId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CrfAssessmentId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new CrfAssessmentId(dbData) : null;
    }
}
