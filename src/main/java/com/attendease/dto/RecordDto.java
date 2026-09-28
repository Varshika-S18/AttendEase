package com.attendease.dto;

import com.attendease.entity.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RecordDto(
        Long id,
        Long sessionId,
        String subjectCode,
        LocalDate sessionDate,
        Integer period,
        Long studentId,
        String rollNo,
        String studentName,
        AttendanceStatus status,
        LocalDateTime markedAt,
        boolean corrected,
        AttendanceStatus previousStatus,
        LocalDateTime correctedAt,
        String correctionRemark) {
}
