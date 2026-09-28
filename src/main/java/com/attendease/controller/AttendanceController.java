package com.attendease.controller;

import com.attendease.dto.CorrectionRequest;
import com.attendease.dto.MarkRequest;
import com.attendease.dto.RecordDto;
import com.attendease.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions/{sessionId}/attendance")
@Tag(name = "Attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Mark PRESENT/ABSENT for students in a session (each student only once)")
    public List<RecordDto> mark(@PathVariable Long sessionId, @Valid @RequestBody MarkRequest req) {
        return service.markAttendance(sessionId, req);
    }

    @GetMapping
    @Operation(summary = "View attendance entries of a session")
    public List<RecordDto> list(@PathVariable Long sessionId) {
        return service.listSessionAttendance(sessionId);
    }

    @PatchMapping("/{studentId}")
    @Operation(summary = "Correct a past attendance entry (audit trail is kept)")
    public RecordDto correct(@PathVariable Long sessionId, @PathVariable Long studentId,
                             @Valid @RequestBody CorrectionRequest req) {
        return service.correct(sessionId, studentId, req);
    }
}
