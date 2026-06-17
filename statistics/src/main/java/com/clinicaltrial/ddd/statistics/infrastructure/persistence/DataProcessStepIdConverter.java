package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.DataProcessStepId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DataProcessStepIdConverter implements AttributeConverter<DataProcessStepId, Long> {

    @Override
    public Long convertToDatabaseColumn(DataProcessStepId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public DataProcessStepId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new DataProcessStepId(dbData) : null;
    }
}
