package com.attendease.repository;

import com.attendease.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByRollNo(String rollNo);
    boolean existsByEmail(String email);
    Optional<Student> findByAccessToken(String accessToken);
}
