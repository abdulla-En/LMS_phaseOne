package net.java.lms_backend.controller;

import net.java.lms_backend.Service.AssignmentService;
import net.java.lms_backend.dto.AssignmentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;

    // Constructor injection makes the dependency explicit and allows the field to be final.
    public AssignmentController(AssignmentService assignmentService) {
        if (assignmentService == null) {
            throw new IllegalArgumentException("AssignmentService cannot be null");
        }
        this.assignmentService = assignmentService;
    }

    // Create a new assignment
    @PostMapping
    public ResponseEntity<AssignmentDTO> createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        AssignmentDTO createdAssignment = assignmentService.createAssignment(assignmentDTO);
        return ResponseEntity.ok(createdAssignment);
    }

    // Get an assignment by ID
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Long id) {
        AssignmentDTO assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }

    // Get all assignments
    @GetMapping
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments() {
        List<AssignmentDTO> assignments = assignmentService.getAllAssignments();
        return ResponseEntity.ok(assignments);
    }

    // Update an assignment by ID
    @PutMapping("/{id}")
    public ResponseEntity<AssignmentDTO> updateAssignment(@PathVariable Long id, @RequestBody AssignmentDTO assignmentDTO) {
        AssignmentDTO updatedAssignment = assignmentService.updateAssignment(id, assignmentDTO);
        return ResponseEntity.ok(updatedAssignment);
    }

    // Delete an assignment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
