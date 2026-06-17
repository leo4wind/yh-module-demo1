package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CrfVersionIdConverter implements AttributeConverter<CrfVersionId, Long> {

    @Override
    public Long convertToDatabaseColumn(CrfVersionId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CrfVersionId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new CrfVersionId(dbData) : null;
    }
}
