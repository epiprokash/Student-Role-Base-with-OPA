package com.example.StudentRbacApplication.dto;
import com.example.StudentRbacApplication.model.Role;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String name;
    private Role role;
}
