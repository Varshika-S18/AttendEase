package com.attendease.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SessionRequest(
        @NotBlank String subjectCode,
        @NotBlank String subjectName,
        @NotBlank String facultyName,
        @NotNull LocalDate sessionDate,
        @Min(1) @Max(12) Integer period,
        String topic) {
}
