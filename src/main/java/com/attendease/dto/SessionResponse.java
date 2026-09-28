package com.attendease.dto;

import java.time.LocalDate;

public record SessionResponse(
        Long id,
        String subjectCode,
        String subjectName,
        String facultyName,
        LocalDate sessionDate,
        Integer period,
        String topic) {
}
