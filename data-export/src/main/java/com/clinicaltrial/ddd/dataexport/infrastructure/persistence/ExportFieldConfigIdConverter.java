package com.clinicaltrial.ddd.dataexport.infrastructure.persistence;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFieldConfigId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ExportFieldConfigIdConverter implements AttributeConverter<ExportFieldConfigId, Long> {

    @Override
    public Long convertToDatabaseColumn(ExportFieldConfigId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public ExportFieldConfigId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new ExportFieldConfigId(dbData) : null;
    }
}
