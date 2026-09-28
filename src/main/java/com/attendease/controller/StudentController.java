package com.attendease.controller;

import com.attendease.dto.StudentRequest;
import com.attendease.dto.StudentResponse;
import com.attendease.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students")
public class StudentController {

    private final AttendanceService service;

    public StudentController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a student (returns the student's personal access token once)")
    public StudentResponse create(@Valid @RequestBody StudentRequest req) {
        return service.createStudent(req);
    }

    @GetMapping
    @Operation(summary = "List all students")
    public List<StudentResponse> list() {
        return service.listStudents();
    }
}
