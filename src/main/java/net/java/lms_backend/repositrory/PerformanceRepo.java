package net.java.lms_backend.repositrory;

import net.java.lms_backend.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceRepo extends JpaRepository<Performance, Long> {
   List<Performance> findByCourseId(Long courseId) ;
    Performance findByStudentIdAndCourseId(Long studentId, Long courseId);
}
