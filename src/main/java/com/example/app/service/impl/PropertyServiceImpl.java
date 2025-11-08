package com.example.app.service.impl;

import com.example.app.mapper.PropertyMapper;
import com.example.app.model.Property;
import com.example.app.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * PropertyServiceインターフェースの実装クラス。
 * 物件情報の管理に関するビジネスロジックを実装します。
 */
@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    
    private final PropertyMapper propertyMapper;
    
    @Override
    public List<Property> getAllProperties() {
        return propertyMapper.findAll();
    }
    
    @Override
    public Property getPropertyById(Long id) {
        return propertyMapper.findById(id);
    }
    

    
    @Override
    @Transactional
    public void createProperty(Property property) {
        propertyMapper.insert(property);
    }
    
    @Override
    @Transactional
    public void updateProperty(Property property) {
        propertyMapper.update(property);
    }
    
    @Override
    @Transactional
    public void deleteProperty(Long id) {
        propertyMapper.delete(id);
    }
}