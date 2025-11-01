package com.example.app.mapper;

import com.example.app.model.Lease;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface LeaseMapper {
    List<Lease> findAll();
    Lease findById(Long id);
    List<Lease> findByPropertyId(Long propertyId);
    List<Lease> findByTenantId(Long tenantId);
    void insert(Lease lease);
    void update(Lease lease);
    void delete(Long id);
    List<Lease> findActiveLeasesByPropertyId(Long propertyId);
}