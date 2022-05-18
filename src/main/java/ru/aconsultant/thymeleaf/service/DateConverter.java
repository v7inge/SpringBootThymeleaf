package ru.aconsultant.thymeleaf.service;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;

@Converter
public class DateConverter implements AttributeConverter<Long, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(Long millis) {
        return new Timestamp(millis);
    }

    @Override
    public Long convertToEntityAttribute(Timestamp timestamp) {
        return timestamp.getTime();
    }
}
