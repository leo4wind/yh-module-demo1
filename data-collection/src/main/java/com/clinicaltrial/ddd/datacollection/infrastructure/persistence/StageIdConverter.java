package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StageIdConverter implements AttributeConverter<StageId, Long> {

    @Override
    public Long convertToDatabaseColumn(StageId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public StageId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new StageId(dbData) : null;
    }
}
