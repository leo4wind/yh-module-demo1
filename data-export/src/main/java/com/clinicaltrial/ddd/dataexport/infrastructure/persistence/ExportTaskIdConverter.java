package com.clinicaltrial.ddd.dataexport.infrastructure.persistence;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ExportTaskIdConverter implements AttributeConverter<ExportTaskId, Long> {

    @Override
    public Long convertToDatabaseColumn(ExportTaskId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public ExportTaskId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new ExportTaskId(dbData) : null;
    }
}
