package net.java.lms_backend.repositrory;

import net.java.lms_backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepo extends JpaRepository<Attendance,Long> {
    Attendance findByLessonIdAndOtp(Long lessonId, String otp);

    List<Attendance> findByLessonId(Long lessonId);

    Attendance findByLessonIdAndStudentId(Long lessonId, Long studentId);

}
