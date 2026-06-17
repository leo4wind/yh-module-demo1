package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseEventRuleId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AdverseEventRuleIdConverter implements AttributeConverter<AdverseEventRuleId, Long> {

    @Override
    public Long convertToDatabaseColumn(AdverseEventRuleId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public AdverseEventRuleId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new AdverseEventRuleId(dbData) : null;
    }
}
