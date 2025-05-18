package net.java.lms_backend.repositrory;

import net.java.lms_backend.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepositery extends JpaRepository<Lesson, Long> {
}
