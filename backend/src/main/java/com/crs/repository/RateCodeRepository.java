package com.crs.repository;

import com.crs.entity.RateCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RateCodeRepository extends JpaRepository<RateCode, Integer> {
    
    RateCode findByRateCode(String rateCode);
    
    List<RateCode> findByStatus(RateCode.Status status);
}