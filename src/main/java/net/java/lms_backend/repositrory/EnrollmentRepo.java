package net.java.lms_backend.repositrory;

import net.java.lms_backend.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepo extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByCourseId(Long courseId);
}
