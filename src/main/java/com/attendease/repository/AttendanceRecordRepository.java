package com.attendease.repository;

import com.attendease.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findBySession_Id(Long sessionId);
    List<AttendanceRecord> findBySession_IdAndStudent_IdIn(Long sessionId, Collection<Long> studentIds);
    Optional<AttendanceRecord> findBySession_IdAndStudent_Id(Long sessionId, Long studentId);
    List<AttendanceRecord> findBySession_SubjectCode(String subjectCode);
    List<AttendanceRecord> findByStudent_Id(Long studentId);
}
