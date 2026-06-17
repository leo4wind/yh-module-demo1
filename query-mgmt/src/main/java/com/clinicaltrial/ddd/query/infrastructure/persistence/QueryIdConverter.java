package com.clinicaltrial.ddd.query.infrastructure.persistence;

import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class QueryIdConverter implements AttributeConverter<QueryId, Long> {

    @Override
    public Long convertToDatabaseColumn(QueryId attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public QueryId convertToEntityAttribute(Long dbData) {
        return dbData != null ? new QueryId(dbData) : null;
    }
}
