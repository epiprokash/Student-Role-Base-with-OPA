package com.example.StudentRbacApplication.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.StudentRbacApplication.dto.StudentDto;
import com.example.StudentRbacApplication.model.Student;
import com.example.StudentRbacApplication.service.OpaService;
import com.example.StudentRbacApplication.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final OpaService opaService;
    public StudentController(StudentService studentService, OpaService
    opaService) {
        this.studentService = studentService;
        this.opaService = opaService;
}

@PostMapping("/me")
public ResponseEntity<?> addOrUpdateMyInfo(@RequestBody StudentDto dto,
Authentication auth) {
String username = (String) auth.getPrincipal();
String role =
auth.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_","");

    boolean allowed = opaService.isAllowed(username, role, "student:update", Map.of("target", "self"));
    if (!allowed) return ResponseEntity.status(403).body("forbidden by policy");
    
    Student s = new Student();
    s.setDepartment(dto.getDepartment());
    s.setBloodGroup(dto.getBloodGroup());
    Student persisted = studentService.createOrUpdateForUser(username, s);
    return ResponseEntity.ok(persisted);
    }
    @GetMapping("/all")
    public ResponseEntity<?> all(Authentication auth) {
    // allow OPA check for read if needed. For simplicity, allow authenticated
    return ResponseEntity.ok(studentService.all());
    }
}
