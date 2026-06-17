package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableDefinitionId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class VariableDefinitionIdConverter implements AttributeConverter<VariableDefinitionId, Long> {

    @Override
    public Long convertToDatabaseColumn(VariableDefinitionId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public VariableDefinitionId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new VariableDefinitionId(dbData) : null;
    }
}
