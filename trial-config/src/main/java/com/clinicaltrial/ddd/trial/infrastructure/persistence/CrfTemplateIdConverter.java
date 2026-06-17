package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CrfTemplateIdConverter implements AttributeConverter<CrfTemplateId, Long> {

    @Override
    public Long convertToDatabaseColumn(CrfTemplateId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CrfTemplateId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new CrfTemplateId(dbData) : null;
    }
}
