package com.staylo;

import com.staylo.dto.StudentDTO;
import com.staylo.entity.Student;
import com.staylo.entity.User;
import com.staylo.exception.ResourceNotFoundException;
import com.staylo.exception.StayloException;
import com.staylo.repository.StudentRepository;
import com.staylo.repository.UserRepository;
import com.staylo.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private StudentService studentService;

    private User testUser;
    private Student testStudent;
    private StudentDTO.Request validRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@test.com")
                .password("encoded")
                .role(User.Role.STUDENT)
                .build();

        testStudent = Student.builder()
                .id(1L)
                .user(testUser)
                .enrollmentNo("CS2024001")
                .course("B.Tech")
                .year(2)
                .contactNo("9876543210")
                .build();

        validRequest = StudentDTO.Request.builder()
                .userId(1L)
                .enrollmentNo("CS2024001")
                .course("B.Tech")
                .year(2)
                .contactNo("9876543210")
                .build();
    }

    @Test
    @DisplayName("Should register student successfully")
    void testRegisterStudentSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentRepository.existsByEnrollmentNo("CS2024001")).thenReturn(false);
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        StudentDTO.Response response = studentService.registerStudent(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEnrollmentNo()).isEqualTo("CS2024001");
        assertThat(response.getName()).isEqualTo("John Doe");
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when enrollment number already exists")
    void testRegisterDuplicateEnrollment() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentRepository.existsByEnrollmentNo("CS2024001")).thenReturn(true);

        assertThatThrownBy(() -> studentService.registerStudent(validRequest))
                .isInstanceOf(StayloException.class)
                .hasMessageContaining("Enrollment number already exists");

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void testRegisterUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        validRequest.setUserId(99L);

        assertThatThrownBy(() -> studentService.registerStudent(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Should throw exception when student profile already exists for user")
    void testRegisterDuplicateStudentForUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studentRepository.existsByEnrollmentNo(any())).thenReturn(false);
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(testStudent));

        assertThatThrownBy(() -> studentService.registerStudent(validRequest))
                .isInstanceOf(StayloException.class)
                .hasMessageContaining("Student profile already exists");
    }

    @Test
    @DisplayName("Should get student by ID successfully")
    void testGetStudentById() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        StudentDTO.Response response = studentService.getStudentById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCourse()).isEqualTo("B.Tech");
    }

    @Test
    @DisplayName("Should throw exception when student not found by ID")
    void testGetStudentByIdNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
