# AttendEase (Spring Boot 3 / Java 17)

## Run
    mvn clean
    mvn spring-boot:run        <-- note: "spring-boot:run" (colon, no spaces)

Swagger UI:  http://localhost:8080/swagger-ui.html
H2 console:  http://localhost:8080/h2-console   (JDBC URL: jdbc:h2:file:./data/attendease, user: sa, no password)

## Try it in Swagger
1. Click **Authorize**, enter `faculty123` (X-API-Key). Change it in application.properties.
2. Demo data is preloaded (5 students, subject CS101, 10 sessions).
   - GET /api/reports/subjects/CS101/summary  -> percentages + shortage flag
   - GET /api/reports/subjects/CS101/shortage -> only students below 75%
3. Student view: GET /api/me/attendance with header X-Student-Token = `demo-token-4`
   (no faculty key needed; a student only sees their own data).
4. Mark attendance: POST /api/sessions (create) then POST /api/sessions/{id}/attendance
   body: {"records":[{"studentId":1,"status":"PRESENT"}]}   (second time -> 409)
5. Correct: PATCH /api/sessions/{sessionId}/attendance/{studentId}
   body: {"status":"PRESENT","remark":"Marked wrongly"}

## Rules enforced
- Attendance once per student per session (service check + DB unique constraint session_id+student_id)
- percentage = present / recorded sessions * 100; shortage = percentage < attendease.min-threshold (default 75,
  overridable per request with ?threshold=)
