package com.attendease.service;

import com.attendease.dto.*;
import com.attendease.entity.AttendanceRecord;
import com.attendease.entity.AttendanceStatus;
import com.attendease.entity.ClassSession;
import com.attendease.entity.Student;
import com.attendease.exception.ApiException;
import com.attendease.repository.AttendanceRecordRepository;
import com.attendease.repository.ClassSessionRepository;
import com.attendease.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final StudentRepository studentRepo;
    private final ClassSessionRepository sessionRepo;
    private final AttendanceRecordRepository recordRepo;

    @Value("${attendease.min-threshold}")
    private double defaultThreshold;

    public AttendanceService(StudentRepository studentRepo, ClassSessionRepository sessionRepo,
                             AttendanceRecordRepository recordRepo) {
        this.studentRepo = studentRepo;
        this.sessionRepo = sessionRepo;
        this.recordRepo = recordRepo;
    }

    // ---------------- students ----------------
    @Transactional
    public StudentResponse createStudent(StudentRequest req) {
        if (studentRepo.existsByRollNo(req.rollNo())) {
            throw new ApiException(HttpStatus.CONFLICT, "Roll number already exists: " + req.rollNo());
        }
        if (studentRepo.existsByEmail(req.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists: " + req.email());
        }
        Student s = new Student();
        s.setRollNo(req.rollNo());
        s.setName(req.name());
        s.setEmail(req.email());
        s.setDepartment(req.department());
        s.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
        s = studentRepo.save(s);
        return toStudentResponse(s, true);
    }

    public List<StudentResponse> listStudents() {
        return studentRepo.findAll().stream()
                .sorted(Comparator.comparing(Student::getRollNo))
                .map(s -> toStudentResponse(s, false))
                .collect(Collectors.toList());
    }

    // ---------------- sessions ----------------
    @Transactional
    public SessionResponse createSession(SessionRequest req) {
        int period = req.period() == null ? 1 : req.period();
        if (sessionRepo.existsBySubjectCodeAndSessionDateAndPeriod(req.subjectCode(), req.sessionDate(), period)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "A session for this subject, date and period already exists");
        }
        ClassSession c = new ClassSession();
        c.setSubjectCode(req.subjectCode());
        c.setSubjectName(req.subjectName());
        c.setFacultyName(req.facultyName());
        c.setSessionDate(req.sessionDate());
        c.setPeriod(period);
        c.setTopic(req.topic());
        return toSessionResponse(sessionRepo.save(c));
    }

    public List<SessionResponse> listSessions(String subjectCode) {
        List<ClassSession> list = (subjectCode == null || subjectCode.isBlank())
                ? sessionRepo.findAllByOrderBySessionDateDescPeriodAsc()
                : sessionRepo.findBySubjectCodeOrderBySessionDateDescPeriodAsc(subjectCode);
        return list.stream().map(this::toSessionResponse).collect(Collectors.toList());
    }

    // ---------------- marking ----------------
    /** Mark many students for one session, all-or-nothing. A student can be marked only once. */
    @Transactional
    public List<RecordDto> markAttendance(Long sessionId, MarkRequest req) {
        ClassSession session = getSession(sessionId);
        if (session.getSessionDate().isAfter(LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot mark attendance for a future session");
        }

        List<Long> ids = req.records().stream().map(MarkRequest.Entry::studentId).collect(Collectors.toList());
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Duplicate studentId in request");
        }

        Map<Long, Student> students = studentRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(Student::getId, s -> s));
        List<Long> missing = ids.stream().filter(id -> !students.containsKey(id)).sorted().collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Unknown student ids: " + missing);
        }

        List<Long> already = recordRepo.findBySession_IdAndStudent_IdIn(sessionId, ids).stream()
                .map(r -> r.getStudent().getId()).sorted().collect(Collectors.toList());
        if (!already.isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT, "Attendance already marked for student ids: " + already
                    + ". Use the correction endpoint to change it.");
        }

        List<AttendanceRecord> rows = new ArrayList<>();
        for (MarkRequest.Entry e : req.records()) {
            AttendanceRecord r = new AttendanceRecord();
            r.setSession(session);
            r.setStudent(students.get(e.studentId()));
            r.setStatus(e.status());
            r.setMarkedAt(LocalDateTime.now());
            rows.add(r);
        }
        return recordRepo.saveAll(rows).stream().map(this::toRecordDto).collect(Collectors.toList());
    }

    public List<RecordDto> listSessionAttendance(Long sessionId) {
        getSession(sessionId);
        return recordRepo.findBySession_Id(sessionId).stream()
                .sorted(Comparator.comparing(r -> r.getStudent().getRollNo()))
                .map(this::toRecordDto).collect(Collectors.toList());
    }

    /** Correct an existing entry (any past date) and keep an audit trail. */
    @Transactional
    public RecordDto correct(Long sessionId, Long studentId, CorrectionRequest req) {
        getSession(sessionId);
        AttendanceRecord r = recordRepo.findBySession_IdAndStudent_Id(sessionId, studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "No attendance entry to correct for this student and session; mark it first"));
        if (r.getStatus() == req.status()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "New status is the same as the current status");
        }
        r.setPreviousStatus(r.getStatus());
        r.setStatus(req.status());
        r.setCorrected(true);
        r.setCorrectedAt(LocalDateTime.now());
        r.setCorrectionRemark(req.remark());
        return toRecordDto(recordRepo.save(r));
    }

    // ---------------- percentages & shortage ----------------
    /** percentage = present / recorded sessions * 100; shortage when percentage < threshold. */
    public List<SubjectSummaryDto> summaries(String subjectCode, Long studentId, Double thresholdOverride) {
        double threshold = thresholdOverride == null ? defaultThreshold : thresholdOverride;
        if (threshold < 0 || threshold > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "threshold must be between 0 and 100");
        }

        List<AttendanceRecord> records;
        if (subjectCode != null && !subjectCode.isBlank()) {
            records = recordRepo.findBySession_SubjectCode(subjectCode);
        } else if (studentId != null) {
            records = recordRepo.findByStudent_Id(studentId);
        } else {
            records = recordRepo.findAll();
        }
        if (studentId != null) {
            records = records.stream().filter(r -> r.getStudent().getId().equals(studentId))
                    .collect(Collectors.toList());
        }

        // group by (student, subject)
        Map<String, List<AttendanceRecord>> grouped = new LinkedHashMap<>();
        for (AttendanceRecord r : records) {
            String key = r.getStudent().getId() + "|" + r.getSession().getSubjectCode();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
        }

        List<SubjectSummaryDto> out = new ArrayList<>();
        for (List<AttendanceRecord> group : grouped.values()) {
            Student st = group.get(0).getStudent();
            ClassSession se = group.get(0).getSession();
            long total = group.size();
            long present = group.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
            double pct = total == 0 ? 0.0 : Math.round(present * 10000.0 / total) / 100.0;
            out.add(new SubjectSummaryDto(st.getId(), st.getRollNo(), st.getName(), se.getSubjectCode(),
                    se.getSubjectName(), total, present, total - present, pct, threshold, pct < threshold));
        }
        out.sort(Comparator.comparing(SubjectSummaryDto::rollNo).thenComparing(SubjectSummaryDto::subjectCode));
        return out;
    }

    public List<SubjectSummaryDto> shortageOnly(String subjectCode, Double threshold) {
        return summaries(subjectCode, null, threshold).stream()
                .filter(SubjectSummaryDto::shortage).collect(Collectors.toList());
    }

    // ---------------- student self-view ----------------
    public MyAttendanceDto myAttendance(String token, String subjectCode) {
        Student st = studentRepo.findByAccessToken(token)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid student token"));
        List<SubjectSummaryDto> subjects = summaries(subjectCode, st.getId(), null);
        List<RecordDto> records = recordRepo.findByStudent_Id(st.getId()).stream()
                .filter(r -> subjectCode == null || subjectCode.isBlank()
                        || r.getSession().getSubjectCode().equals(subjectCode))
                .sorted(Comparator.comparing((AttendanceRecord r) -> r.getSession().getSessionDate()).reversed())
                .map(this::toRecordDto).collect(Collectors.toList());
        return new MyAttendanceDto(toStudentResponse(st, false), subjects, records);
    }

    // ---------------- helpers ----------------
    private ClassSession getSession(Long id) {
        return sessionRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Session not found: " + id));
    }

    private StudentResponse toStudentResponse(Student s, boolean withToken) {
        return new StudentResponse(s.getId(), s.getRollNo(), s.getName(), s.getEmail(), s.getDepartment(),
                withToken ? s.getAccessToken() : null);
    }

    private SessionResponse toSessionResponse(ClassSession c) {
        return new SessionResponse(c.getId(), c.getSubjectCode(), c.getSubjectName(), c.getFacultyName(),
                c.getSessionDate(), c.getPeriod(), c.getTopic());
    }

    private RecordDto toRecordDto(AttendanceRecord r) {
        return new RecordDto(r.getId(), r.getSession().getId(), r.getSession().getSubjectCode(),
                r.getSession().getSessionDate(), r.getSession().getPeriod(),
                r.getStudent().getId(), r.getStudent().getRollNo(), r.getStudent().getName(),
                r.getStatus(), r.getMarkedAt(), r.isCorrected(), r.getPreviousStatus(),
                r.getCorrectedAt(), r.getCorrectionRemark());
    }
}
