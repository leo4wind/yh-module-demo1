package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.SitePersonnelId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SitePersonnelIdConverter implements AttributeConverter<SitePersonnelId, Long> {

    @Override
    public Long convertToDatabaseColumn(SitePersonnelId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public SitePersonnelId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new SitePersonnelId(dbData) : null;
    }
}
