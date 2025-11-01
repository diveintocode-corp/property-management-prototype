package com.example.app.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Tenant {
    private Long id;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    private String phone;
    
    @Email(message = "Please provide a valid email address")
    private String email;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}