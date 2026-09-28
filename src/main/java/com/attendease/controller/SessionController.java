package com.attendease.controller;

import com.attendease.dto.SessionRequest;
import com.attendease.dto.SessionResponse;
import com.attendease.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Sessions")
public class SessionController {

    private final AttendanceService service;

    public SessionController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a class session (subject + date + period)")
    public SessionResponse create(@Valid @RequestBody SessionRequest req) {
        return service.createSession(req);
    }

    @GetMapping
    @Operation(summary = "List sessions, optionally by subject code")
    public List<SessionResponse> list(@RequestParam(required = false) String subjectCode) {
        return service.listSessions(subjectCode);
    }
}
