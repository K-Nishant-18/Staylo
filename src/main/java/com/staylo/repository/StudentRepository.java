package com.staylo.repository;

import com.staylo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEnrollmentNo(String enrollmentNo);
    Optional<Student> findByUserId(Long userId);
    boolean existsByEnrollmentNo(String enrollmentNo);

    @Query("SELECT s FROM Student s WHERE s.course = :course AND s.year = :year")
    List<Student> findByCourseAndYear(String course, Integer year);
}
