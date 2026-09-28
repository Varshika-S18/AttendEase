package com.attendease.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record StudentRequest(
        @NotBlank String rollNo,
        @NotBlank String name,
        @NotBlank @Email String email,
        String department) {
}
