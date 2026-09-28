package com.attendease.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/** One class meeting of a subject on a date/period. */
@Entity
@Table(name = "class_sessions",
        uniqueConstraints = @UniqueConstraint(name = "uq_session_subject_date_period",
                columnNames = {"subject_code", "session_date", "period_no"}))
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_code", nullable = false, length = 20)
    private String subjectCode;

    @Column(nullable = false, length = 120)
    private String subjectName;

    @Column(nullable = false, length = 120)
    private String facultyName;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "period_no", nullable = false)
    private Integer period = 1;

    @Column(length = 200)
    private String topic;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    public Integer getPeriod() { return period; }
    public void setPeriod(Integer period) { this.period = period; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
