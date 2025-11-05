package com.example.app.service;

import com.example.app.model.Property;
import java.util.List;

public interface PropertyService {
    List<Property> getAllProperties();
    Property getPropertyById(Long id);

    void createProperty(Property property);
    void updateProperty(Property property);
    void deleteProperty(Long id);
}