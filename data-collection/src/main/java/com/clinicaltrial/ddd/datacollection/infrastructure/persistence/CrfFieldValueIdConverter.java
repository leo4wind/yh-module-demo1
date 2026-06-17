package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfFieldValueId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CrfFieldValueIdConverter implements AttributeConverter<CrfFieldValueId, Long> {

    @Override
    public Long convertToDatabaseColumn(CrfFieldValueId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CrfFieldValueId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new CrfFieldValueId(dbData) : null;
    }
}
