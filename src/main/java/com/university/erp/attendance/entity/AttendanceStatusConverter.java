package com.university.erp.attendance.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AttendanceStatusConverter implements AttributeConverter<AttendanceStatus, String> {
    public String convertToDatabaseColumn(AttendanceStatus status) {
        return status == AttendanceStatus.C_WORK ? "C-Work" : status == null ? null : status.name();
    }

    public AttendanceStatus convertToEntityAttribute(String value) {
        if (value == null)
            return null;
        return "C-Work".equals(value) ? AttendanceStatus.C_WORK : AttendanceStatus.valueOf(value);
    }
}
