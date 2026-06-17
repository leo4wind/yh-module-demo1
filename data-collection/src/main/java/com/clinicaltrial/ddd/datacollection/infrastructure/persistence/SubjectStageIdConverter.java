package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SubjectStageIdConverter implements AttributeConverter<SubjectStageId, Long> {

    @Override
    public Long convertToDatabaseColumn(SubjectStageId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public SubjectStageId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new SubjectStageId(dbData) : null;
    }
}
