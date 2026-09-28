package com.attendease.dto;

import com.attendease.entity.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CorrectionRequest(
        @NotNull AttendanceStatus status,
        @NotBlank @Size(min = 3, max = 300) String remark) {
}
