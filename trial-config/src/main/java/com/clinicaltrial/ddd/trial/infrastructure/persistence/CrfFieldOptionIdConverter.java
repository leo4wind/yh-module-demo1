package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldOptionId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CrfFieldOptionIdConverter implements AttributeConverter<CrfFieldOptionId, Long> {

    @Override
    public Long convertToDatabaseColumn(CrfFieldOptionId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CrfFieldOptionId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new CrfFieldOptionId(dbData) : null;
    }
}
