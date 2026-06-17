package com.clinicaltrial.ddd.dataexport.infrastructure.persistence;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportExecutionLogId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ExportExecutionLogIdConverter implements AttributeConverter<ExportExecutionLogId, Long> {

    @Override
    public Long convertToDatabaseColumn(ExportExecutionLogId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public ExportExecutionLogId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new ExportExecutionLogId(dbData) : null;
    }
}
