package net.java.lms_backend.Service;

import net.java.lms_backend.Repositrory.AssignmentRepository;
import net.java.lms_backend.Repositrory.CourseRepository;
import net.java.lms_backend.dto.AssignmentDTO;
import net.java.lms_backend.entity.Assignment;
import net.java.lms_backend.entity.Course;
import net.java.lms_backend.exception.AssignmentNotFoundException;
import net.java.lms_backend.exception.CourseNotFoundException;
import net.java.lms_backend.mapper.AssignmentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final AssignmentMapper assignmentMapper;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            CourseRepository courseRepository,
            AssignmentMapper assignmentMapper
    ) {
        if (assignmentRepository == null) {
            throw new IllegalArgumentException("AssignmentRepository cannot be null");
        }
        if (courseRepository == null) {
            throw new IllegalArgumentException("CourseRepository cannot be null");
        }
        if (assignmentMapper == null) {
            throw new IllegalArgumentException("AssignmentMapper cannot be null");
        }
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.assignmentMapper = assignmentMapper;
    }

    // Create a new assignment
    public AssignmentDTO createAssignment(AssignmentDTO dto) {
        Assignment assignment = assignmentMapper.toEntity(dto);
        Assignment savedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toDTO(savedAssignment);
    }

    // Get an assignment by ID
    public AssignmentDTO getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("Assignment not found with id: " + id));
        return assignmentMapper.toDTO(assignment);
    }

    // Get all assignments
    public List<AssignmentDTO> getAllAssignments() {
        // Stream.toList() returns an unmodifiable List as of Java 16+
        return assignmentRepository.findAll().stream()
                .map(assignmentMapper::toDTO)
                .toList();
    }

    // Update an assignment by ID
    public AssignmentDTO updateAssignment(Long id, AssignmentDTO dto) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("Assignment not found with id: " + id));

        // Update assignment properties
        assignment.setTitle(dto.getTitle());
        assignment.setDueDate(java.time.LocalDate.parse(dto.getDueDate()));
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new CourseNotFoundException("Invalid course ID: " + dto.getCourseId()));
            assignment.setCourse(course);
        }

        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toDTO(updatedAssignment);
    }

    // Delete an assignment by ID
    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new AssignmentNotFoundException("Assignment not found with id: " + id);
        }
        assignmentRepository.deleteById(id);
    }
}
