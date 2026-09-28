package com.attendease.controller;

import com.attendease.dto.MyAttendanceDto;
import com.attendease.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@Tag(name = "Student self-service")
public class MyAttendanceController {

    private final AttendanceService service;

    public MyAttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping("/attendance")
    @SecurityRequirements   // no faculty key needed here
    @Operation(summary = "A student views ONLY their own attendance (use X-Student-Token)")
    public MyAttendanceDto myAttendance(@RequestHeader("X-Student-Token") String token,
                                        @RequestParam(required = false) String subjectCode) {
        return service.myAttendance(token, subjectCode);
    }
}
