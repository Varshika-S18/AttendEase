package com.attendease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** A student's status in one session. Owned by (child of) both Session and Student. */
@Entity
@Table(name = "attendance_records",
        // Business rule: attendance can be marked only once per student per session
        uniqueConstraints = @UniqueConstraint(name = "uq_record_session_student",
                columnNames = {"session_id", "student_id"}))
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ClassSession session;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AttendanceStatus status;

    @Column(nullable = false)
    private LocalDateTime markedAt;

    // Correction audit trail
    @Column(nullable = false)
    private boolean corrected = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private AttendanceStatus previousStatus;

    private LocalDateTime correctedAt;

    @Column(length = 300)
    private String correctionRemark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ClassSession getSession() { return session; }
    public void setSession(ClassSession session) { this.session = session; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public LocalDateTime getMarkedAt() { return markedAt; }
    public void setMarkedAt(LocalDateTime markedAt) { this.markedAt = markedAt; }
    public boolean isCorrected() { return corrected; }
    public void setCorrected(boolean corrected) { this.corrected = corrected; }
    public AttendanceStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(AttendanceStatus previousStatus) { this.previousStatus = previousStatus; }
    public LocalDateTime getCorrectedAt() { return correctedAt; }
    public void setCorrectedAt(LocalDateTime correctedAt) { this.correctedAt = correctedAt; }
    public String getCorrectionRemark() { return correctionRemark; }
    public void setCorrectionRemark(String correctionRemark) { this.correctionRemark = correctionRemark; }
}
