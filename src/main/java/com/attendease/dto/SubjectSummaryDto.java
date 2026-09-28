package com.attendease.dto;

public record SubjectSummaryDto(
        Long studentId,
        String rollNo,
        String studentName,
        String subjectCode,
        String subjectName,
        long totalSessions,
        long present,
        long absent,
        double percentage,
        double threshold,
        boolean shortage) {
}
