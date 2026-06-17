package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class VisitPlanIdConverter implements AttributeConverter<VisitPlanId, Long> {

    @Override
    public Long convertToDatabaseColumn(VisitPlanId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public VisitPlanId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new VisitPlanId(dbData) : null;
    }
}
