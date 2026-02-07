package com.crs.controller;

import com.crs.entity.RateCode;
import com.crs.service.RateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rate-codes")
@CrossOrigin(origins = "*")
public class RateCodeController {
    
    @Autowired
    private RateCodeService rateCodeService;
    
    @GetMapping
    public ResponseEntity<List<RateCode>> getAllRateCodes() {
        List<RateCode> rateCodes = rateCodeService.getAllRateCodes();
        return ResponseEntity.ok(rateCodes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RateCode> getRateCodeById(@PathVariable Integer id) {
        RateCode rateCode = rateCodeService.getRateCodeById(id);
        return ResponseEntity.ok(rateCode);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<RateCode> getRateCodeByCode(@PathVariable String code) {
        RateCode rateCode = rateCodeService.getRateCodeByCode(code);
        return ResponseEntity.ok(rateCode);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<RateCode>> getActiveRateCodes() {
        List<RateCode> rateCodes = rateCodeService.getActiveRateCodes();
        return ResponseEntity.ok(rateCodes);
    }
    
    @PostMapping
    public ResponseEntity<RateCode> createRateCode(@RequestBody RateCode rateCode) {
        RateCode createdRateCode = rateCodeService.createRateCode(rateCode);
        return ResponseEntity.ok(createdRateCode);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RateCode> updateRateCode(@PathVariable Integer id, @RequestBody RateCode rateCode) {
        rateCode.setId(id);
        RateCode updatedRateCode = rateCodeService.updateRateCode(rateCode);
        return ResponseEntity.ok(updatedRateCode);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRateCode(@PathVariable Integer id) {
        rateCodeService.deleteRateCode(id);
        return ResponseEntity.ok().build();
    }
}