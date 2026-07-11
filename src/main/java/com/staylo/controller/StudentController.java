package com.staylo.controller;

import com.staylo.dto.ApiResponse;
import com.staylo.dto.StudentDTO;
import com.staylo.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Students", description = "Student management endpoints")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Register a student")
    public ResponseEntity<ApiResponse<StudentDTO.Response>> register(
            @Valid @RequestBody StudentDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student registered successfully",
                        studentService.registerStudent(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Get all students")
    public ResponseEntity<ApiResponse<List<StudentDTO.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAllStudents()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STUDENT')")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponse<StudentDTO.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentById(id)));
    }

    @GetMapping("/enrollment/{enrollmentNo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Get student by enrollment number")
    public ResponseEntity<ApiResponse<StudentDTO.Response>> getByEnrollment(
            @PathVariable String enrollmentNo) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentByEnrollmentNo(enrollmentNo)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Update student details")
    public ResponseEntity<ApiResponse<StudentDTO.Response>> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully",
                studentService.updateStudent(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a student (Admin only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }
}
