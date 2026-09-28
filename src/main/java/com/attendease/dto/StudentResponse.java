package com.attendease.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StudentResponse(
        Long id,
        String rollNo,
        String name,
        String email,
        String department,
        String accessToken) {   // only filled when the student is first created
}
