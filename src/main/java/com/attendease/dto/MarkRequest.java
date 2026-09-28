package com.attendease.dto;

import com.attendease.entity.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MarkRequest(@NotEmpty List<@Valid Entry> records) {

    public record Entry(@NotNull Long studentId, @NotNull AttendanceStatus status) {
    }
}
