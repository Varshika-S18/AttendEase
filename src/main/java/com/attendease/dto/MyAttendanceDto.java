package com.attendease.dto;

import java.util.List;

public record MyAttendanceDto(
        StudentResponse student,
        List<SubjectSummaryDto> subjects,
        List<RecordDto> records) {
}
