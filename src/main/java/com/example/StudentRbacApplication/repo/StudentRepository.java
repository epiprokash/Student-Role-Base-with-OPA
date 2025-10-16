package com.example.StudentRbacApplication.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.StudentRbacApplication.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
}
