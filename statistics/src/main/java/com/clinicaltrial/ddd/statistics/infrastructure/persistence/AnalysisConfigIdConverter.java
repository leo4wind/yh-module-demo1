package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisConfigId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AnalysisConfigIdConverter implements AttributeConverter<AnalysisConfigId, Long> {

    @Override
    public Long convertToDatabaseColumn(AnalysisConfigId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public AnalysisConfigId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new AnalysisConfigId(dbData) : null;
    }
}
