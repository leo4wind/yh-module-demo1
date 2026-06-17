package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CrfFieldIdConverter implements AttributeConverter<CrfFieldId, Long> {

    @Override
    public Long convertToDatabaseColumn(CrfFieldId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CrfFieldId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new CrfFieldId(dbData) : null;
    }
}
