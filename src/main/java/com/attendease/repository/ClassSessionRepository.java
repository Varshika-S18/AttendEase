package com.attendease.repository;

import com.attendease.entity.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    boolean existsBySubjectCodeAndSessionDateAndPeriod(String subjectCode, LocalDate sessionDate, Integer period);
    List<ClassSession> findBySubjectCodeOrderBySessionDateDescPeriodAsc(String subjectCode);
    List<ClassSession> findAllByOrderBySessionDateDescPeriodAsc();
}
