package com.testplatform.converter;

import com.testplatform.entity.enums.TestCaseStatus;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TestCaseStatusConverter implements AttributeConverter<TestCaseStatus, String> {

    @Override
    public String convertToDatabaseColumn(TestCaseStatus status) {
        if (status == null) {
            return null;
        }
        return status.getDescription();
    }

    @Override
    public TestCaseStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (TestCaseStatus status : TestCaseStatus.values()) {
            if (status.getDescription().equals(dbData)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown database value: " + dbData);
    }
} 