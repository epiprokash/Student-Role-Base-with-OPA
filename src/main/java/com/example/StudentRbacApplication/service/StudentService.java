package com.example.StudentRbacApplication.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.StudentRbacApplication.model.Student;
import com.example.StudentRbacApplication.model.User;
import com.example.StudentRbacApplication.repo.StudentRepository;
import com.example.StudentRbacApplication.repo.UserRepository;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    
    public StudentService(StudentRepository studentRepository, UserRepository
    userRepository) {
    this.studentRepository = studentRepository;
    this.userRepository = userRepository;
}
    public Student createOrUpdateForUser(String username, Student s) {
        User u = userRepository.findByUsername(username).orElseThrow();
        // try find existing student by user
        List<Student> all = studentRepository.findAll();
        for (Student st : all) {
        if (st.getUser() != null && st.getUser().getId().equals(u.getId()))
        {
        st.setDepartment(s.getDepartment());
        st.setBloodGroup(s.getBloodGroup());
        return studentRepository.save(st);
        }
        }
        s.setUser(u);
        return studentRepository.save(s);
    }

    public List<Student> all() { return studentRepository.findAll(); }
    public void delete(Long id) { studentRepository.deleteById(id); }
    public Student findById(Long id) { return
studentRepository.findById(id).orElseThrow(); }
}
