package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFormId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CrfFormIdConverter implements AttributeConverter<CrfFormId, Long> {

    @Override
    public Long convertToDatabaseColumn(CrfFormId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CrfFormId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new CrfFormId(dbData) : null;
    }
}
