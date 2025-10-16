package com.example.StudentRbacApplication.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.StudentRbacApplication.service.OpaService;
import com.example.StudentRbacApplication.service.StudentService;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final StudentService studentService;
    private final OpaService opaService;
    public AdminController(StudentService studentService, OpaService
    opaService) {
        this.studentService = studentService;
        this.opaService = opaService;
    }
@DeleteMapping("/student/{id}")
public ResponseEntity<?> deleteStudent(@PathVariable Long id,
Authentication auth) {
String username = (String) auth.getPrincipal();
String role =
auth.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_","");
boolean allowed = opaService.isAllowed(username, role, "student:delete", Map.of("studentId", id));
if (!allowed) return ResponseEntity.status(403).body("forbidden by policy");
    studentService.delete(id);
    return ResponseEntity.ok("deleted");
}
}

