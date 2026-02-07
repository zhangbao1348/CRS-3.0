package com.crs.service;

import com.crs.entity.RateCode;
import com.crs.repository.RateCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RateCodeService {
    
    @Autowired
    private RateCodeRepository rateCodeRepository;
    
    public List<RateCode> getAllRateCodes() {
        return rateCodeRepository.findAll();
    }
    
    public RateCode getRateCodeById(Integer id) {
        return rateCodeRepository.findById(id).orElse(null);
    }
    
    public RateCode getRateCodeByCode(String rateCode) {
        return rateCodeRepository.findByRateCode(rateCode);
    }
    
    public List<RateCode> getActiveRateCodes() {
        return rateCodeRepository.findByStatus(RateCode.Status.active);
    }
    
    public RateCode createRateCode(RateCode rateCode) {
        return rateCodeRepository.save(rateCode);
    }
    
    public RateCode updateRateCode(RateCode rateCode) {
        return rateCodeRepository.save(rateCode);
    }
    
    public void deleteRateCode(Integer id) {
        rateCodeRepository.deleteById(id);
    }
}