package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SubjectIdConverter implements AttributeConverter<SubjectId, Long> {

    @Override
    public Long convertToDatabaseColumn(SubjectId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public SubjectId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new SubjectId(dbData) : null;
    }
}
