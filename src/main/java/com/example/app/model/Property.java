package com.example.app.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Property {
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "Area is required")
    private String area;
    
    private String plan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}