package com.example.app.mapper;

import com.example.app.model.Property;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PropertyMapper {
    List<Property> findAll();
    Property findById(Long id);
    List<Property> findByAreaAndKeyword(String area, String keyword);
    void insert(Property property);
    void update(Property property);
    void delete(Long id);
}