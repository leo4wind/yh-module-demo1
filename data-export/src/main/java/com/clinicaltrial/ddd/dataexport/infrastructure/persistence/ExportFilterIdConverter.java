package com.clinicaltrial.ddd.dataexport.infrastructure.persistence;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFilterId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ExportFilterIdConverter implements AttributeConverter<ExportFilterId, Long> {

    @Override
    public Long convertToDatabaseColumn(ExportFilterId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public ExportFilterId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new ExportFilterId(dbData) : null;
    }
}
