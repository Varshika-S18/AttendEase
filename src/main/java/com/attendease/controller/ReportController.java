package com.attendease.controller;

import com.attendease.dto.SubjectSummaryDto;
import com.attendease.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports")
public class ReportController {

    private final AttendanceService service;

    public ReportController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping("/subjects/{subjectCode}/summary")
    @Operation(summary = "Attendance % of every student in a subject, with shortage flag")
    public List<SubjectSummaryDto> summary(@PathVariable String subjectCode,
                                           @RequestParam(required = false) Double threshold) {
        return service.summaries(subjectCode, null, threshold);
    }

    @GetMapping("/subjects/{subjectCode}/shortage")
    @Operation(summary = "Students below the minimum attendance threshold in a subject")
    public List<SubjectSummaryDto> shortage(@PathVariable String subjectCode,
                                            @RequestParam(required = false) Double threshold) {
        return service.shortageOnly(subjectCode, threshold);
    }

    @GetMapping("/shortage")
    @Operation(summary = "Eligibility report: all students below threshold across all subjects")
    public List<SubjectSummaryDto> allShortage(@RequestParam(required = false) Double threshold) {
        return service.shortageOnly(null, threshold);
    }

    @GetMapping("/students/{studentId}")
    @Operation(summary = "Attendance % of one student in every subject")
    public List<SubjectSummaryDto> student(@PathVariable Long studentId,
                                           @RequestParam(required = false) Double threshold) {
        return service.summaries(null, studentId, threshold);
    }
}
