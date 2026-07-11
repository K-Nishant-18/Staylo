package com.staylo.service;

import com.staylo.dto.StudentDTO;
import com.staylo.entity.Student;
import com.staylo.entity.User;
import com.staylo.exception.ResourceNotFoundException;
import com.staylo.exception.StayloException;
import com.staylo.repository.StudentRepository;
import com.staylo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Transactional
    public StudentDTO.Response registerStudent(StudentDTO.Request request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        if (studentRepository.existsByEnrollmentNo(request.getEnrollmentNo())) {
            throw new StayloException("Enrollment number already exists: " + request.getEnrollmentNo());
        }

        if (studentRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new StayloException("Student profile already exists for this user");
        }

        Student student = Student.builder()
                .user(user)
                .enrollmentNo(request.getEnrollmentNo())
                .course(request.getCourse())
                .year(request.getYear())
                .contactNo(request.getContactNo())
                .guardianName(request.getGuardianName())
                .guardianContact(request.getGuardianContact())
                .build();

        return toResponse(studentRepository.save(student));
    }

    public List<StudentDTO.Response> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public StudentDTO.Response getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return toResponse(student);
    }

    public StudentDTO.Response getStudentByEnrollmentNo(String enrollmentNo) {
        Student student = studentRepository.findByEnrollmentNo(enrollmentNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "enrollmentNo", enrollmentNo));
        return toResponse(student);
    }

    @Transactional
    public StudentDTO.Response updateStudent(Long id, StudentDTO.Request request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        student.setCourse(request.getCourse());
        student.setYear(request.getYear());
        student.setContactNo(request.getContactNo());
        student.setGuardianName(request.getGuardianName());
        student.setGuardianContact(request.getGuardianContact());

        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", "id", id);
        }
        studentRepository.deleteById(id);
    }

    // ---- Mapper ----
    private StudentDTO.Response toResponse(Student student) {
        return StudentDTO.Response.builder()
                .id(student.getId())
                .userId(student.getUser().getId())
                .name(student.getUser().getName())
                .email(student.getUser().getEmail())
                .enrollmentNo(student.getEnrollmentNo())
                .course(student.getCourse())
                .year(student.getYear())
                .contactNo(student.getContactNo())
                .guardianName(student.getGuardianName())
                .guardianContact(student.getGuardianContact())
                .build();
    }
}
