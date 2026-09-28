package com.attendease.config;

import com.attendease.entity.*;
import com.attendease.repository.AttendanceRecordRepository;
import com.attendease.repository.ClassSessionRepository;
import com.attendease.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Inserts demo data on first start so Swagger shows results immediately. */
@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final StudentRepository students;
    private final ClassSessionRepository sessions;
    private final AttendanceRecordRepository records;

    @Value("${attendease.seed-demo:false}")
    private boolean seed;

    public DemoDataSeeder(StudentRepository students, ClassSessionRepository sessions,
                          AttendanceRecordRepository records) {
        this.students = students;
        this.sessions = sessions;
        this.records = records;
    }

    @Override
    public void run(String... args) {
        if (!seed || students.count() > 0) {
            return;
        }
        double[] ratio = {1.0, 0.8, 0.7, 0.5, 0.9};   // target present ratio per student
        Student[] st = new Student[5];
        for (int i = 0; i < 5; i++) {
            Student s = new Student();
            s.setRollNo(String.format("22CS%03d", i + 1));
            s.setName("Student " + (i + 1));
            s.setEmail("student" + (i + 1) + "@college.edu");
            s.setDepartment("CSE");
            s.setAccessToken("demo-token-" + (i + 1));
            st[i] = students.save(s);
        }
        for (int d = 1; d <= 10; d++) {
            ClassSession c = new ClassSession();
            c.setSubjectCode("CS101");
            c.setSubjectName("Data Structures");
            c.setFacultyName("Dr. Rao");
            c.setSessionDate(LocalDate.now().minusDays(d));
            c.setPeriod(1);
            c.setTopic("Lecture " + d);
            c = sessions.save(c);
            for (int i = 0; i < 5; i++) {
                AttendanceRecord r = new AttendanceRecord();
                r.setSession(c);
                r.setStudent(st[i]);
                r.setStatus(d <= Math.round(ratio[i] * 10) ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT);
                r.setMarkedAt(LocalDateTime.now());
                records.save(r);
            }
        }
        System.out.println(">>> AttendEase demo data loaded. Student tokens: demo-token-1 ... demo-token-5");
    }
}
