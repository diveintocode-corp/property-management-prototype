package com.example.app.mapper;

import com.example.app.model.Tenant;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TenantMapper {
    List<Tenant> findAll();
    Tenant findById(Long id);
    void insert(Tenant tenant);
    void update(Tenant tenant);
    void delete(Long id);
}