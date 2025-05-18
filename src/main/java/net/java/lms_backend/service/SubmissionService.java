package net.java.lms_backend.service;

import lombok.Getter;
import lombok.Setter;
import net.java.lms_backend.Repositrory.AssignmentRepository;
import net.java.lms_backend.Repositrory.StudentRepository;
import net.java.lms_backend.Repositrory.SubmissionRepository;
import net.java.lms_backend.dto.SubmissionDTO;
import net.java.lms_backend.entity.Assignment;
import net.java.lms_backend.entity.Submission;
import net.java.lms_backend.mapper.SubmissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final SubmissionMapper submissionMapper;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            StudentRepository studentRepository,
            SubmissionMapper submissionMapper
    ) {
        if (submissionRepository == null) {
            throw new IllegalArgumentException("SubmissionRepository cannot be null");
        }
        if (assignmentRepository == null) {
            throw new IllegalArgumentException("AssignmentRepository cannot be null");
        }
        if (studentRepository == null) {
            throw new IllegalArgumentException("StudentRepository cannot be null");
        }
        if (submissionMapper == null) {
            throw new IllegalArgumentException("SubmissionMapper cannot be null");
        }
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
        this.submissionMapper = submissionMapper;
    }

    // Create a new submission
    public SubmissionDTO createSubmission(Long assignmentId, Long studentId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty."); // 
        }

        // Save the file locally
        String fileName = saveFile(file);

        Submission submission = new Submission();
        submission.setFileName(fileName);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStudent(
                studentRepository.findById(studentId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Student not found with id: " + studentId
                        )) // :contentReference[oaicite:4]{index=4}
        );
        submission.setAssignment(
                assignmentRepository.findById(assignmentId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Assignment not found with id: " + assignmentId
                        )) // :contentReference[oaicite:5]{index=5}
        );

        Submission savedSubmission = submissionRepository.save(submission);
        return new SubmissionDTO(
                savedSubmission.getId(),
                savedSubmission.getSubmittedAt(),
                studentId,
                assignmentId,
                fileName
        );
    }

    private String saveFile(MultipartFile file) {
        try {
            // Define the directory where files will be saved
            String uploadDir = "uploads/";
            String fileName = file.getOriginalFilename();

            // Ensure the directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // :contentReference[oaicite:6]{index=6}
            }

            // Save the file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not save file. Error: " + e.getMessage()); // 
        }
    }

    // Get all submissions
    public List<SubmissionDTO> getAllSubmissions() {
        // Returns an unmodifiable list (Java 16+)
        return submissionRepository.findAll().stream()
                .map(submissionMapper::toDTO)
                .toList(); // :contentReference[oaicite:8]{index=8}
    }

    // Get a submission by ID
    public SubmissionDTO getSubmissionById(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Submission not found with id: " + id
                )); // :contentReference[oaicite:9]{index=9}
        return submissionMapper.toDTO(submission);
    }

    // Delete a submission by ID
    public void deleteSubmission(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Submission not found with id: " + id
            ); // :contentReference[oaicite:10]{index=10}
        }
        submissionRepository.deleteById(id);
    }

    // Patch grade and feedback for a submission
    public SubmissionDTO patchSubmissionGradeAndFeedback(Long submissionId, Double grade, String feedback) {
        if (grade == null || grade < 0) {
            throw new IllegalArgumentException("Grade must be greater than or equal to 0."); // 
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Submission not found with id: " + submissionId
                )); // :contentReference[oaicite:12]{index=12}

        submission.setGrade(grade);
        submission.setFeedback(feedback);
        Submission updatedSubmission = submissionRepository.save(submission);

        return submissionMapper.toDTO(updatedSubmission);
    }

    // Get submissions by assignment ID
    public List<SubmissionDTO> getSubmissionsByAssignmentId(Long assignmentId) {
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        return submissions.stream()
                .map(submissionMapper::toDTO)
                .toList(); // :contentReference[oaicite:13]{index=13}
    }

    // Get average grade by assignment ID
    public double getAverageGradeByAssignmentId(Long assignmentId) {
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);

        // Filter out submissions with null grades
        List<Submission> submissionsWithGrades = submissions.stream()
                .filter(submission -> submission.getGrade() != null)
                .toList(); // :contentReference[oaicite:14]{index=14}

        if (submissionsWithGrades.isEmpty()) {
            throw new IllegalArgumentException("No grades available for this assignment."); // 
        }

        double totalGrade = submissionsWithGrades.stream()
                .mapToDouble(Submission::getGrade)
                .sum();

        return totalGrade / submissionsWithGrades.size();
    }

    // Get total submissions by assignment ID
    public int getTotalSubmissionsByAssignmentId(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId).size();
    }

    // Get number of nongraded submissions by assignment ID
    public double getNonGradedSubmissionsByAssignmentId(Long assignmentId) {
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);

        // Filter out submissions with null grades
        List<Submission> nonGradedSubmissions = submissions.stream()
                .filter(submission -> submission.getGrade() == null)
                .toList(); // :contentReference[oaicite:16]{index=16}

        return nonGradedSubmissions.size();
    }

    // Get submissions by student ID and course ID
    public List<SubmissionDTO> getSubmissionsByStudentIdAndCourseId(Long studentId, Long assignmentId) {
        List<Submission> submissions = submissionRepository.findByStudentIdAndAssignment_Course_Id(assignmentId, studentId);
        return submissions.stream()
                .map(submissionMapper::toDTO)
                .toList(); // :contentReference[oaicite:17]{index=17}
    }
}
