package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageCrfBindingId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StageCrfBindingIdConverter implements AttributeConverter<StageCrfBindingId, Long> {

    @Override
    public Long convertToDatabaseColumn(StageCrfBindingId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public StageCrfBindingId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new StageCrfBindingId(dbData) : null;
    }
}
