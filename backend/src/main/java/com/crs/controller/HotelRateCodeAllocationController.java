package com.crs.controller;

import com.crs.entity.HotelRateCodeAllocation;
import com.crs.service.HotelRateCodeAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hotel-rate-code-allocations")
@CrossOrigin(origins = "*")
public class HotelRateCodeAllocationController {
    
    @Autowired
    private HotelRateCodeAllocationService hotelRateCodeAllocationService;
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelRateCodeAllocation>> getAllocationsByHotelId(@PathVariable Integer hotelId) {
        List<HotelRateCodeAllocation> allocations = hotelRateCodeAllocationService.getAllocationsByHotelId(hotelId);
        return ResponseEntity.ok(allocations);
    }
    
    @GetMapping("/hotel/{hotelId}/rate-code/{rateCodeId}")
    public ResponseEntity<HotelRateCodeAllocation> getAllocationByHotelAndRateCode(@PathVariable Integer hotelId, @PathVariable Integer rateCodeId) {
        HotelRateCodeAllocation allocation = hotelRateCodeAllocationService.getAllocationByHotelAndRateCode(hotelId, rateCodeId);
        return ResponseEntity.ok(allocation);
    }
    
    @GetMapping("/hotel/{hotelId}/allocated")
    public ResponseEntity<List<HotelRateCodeAllocation>> getAllocatedRateCodesByHotelId(@PathVariable Integer hotelId) {
        List<HotelRateCodeAllocation> allocations = hotelRateCodeAllocationService.getAllocatedRateCodesByHotelId(hotelId);
        return ResponseEntity.ok(allocations);
    }
    
    @PostMapping
    public ResponseEntity<HotelRateCodeAllocation> createAllocation(@RequestBody HotelRateCodeAllocation allocation) {
        HotelRateCodeAllocation createdAllocation = hotelRateCodeAllocationService.createAllocation(allocation);
        return ResponseEntity.ok(createdAllocation);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HotelRateCodeAllocation> updateAllocation(@PathVariable Integer id, @RequestBody HotelRateCodeAllocation allocation) {
        allocation.setId(id);
        HotelRateCodeAllocation updatedAllocation = hotelRateCodeAllocationService.updateAllocation(allocation);
        return ResponseEntity.ok(updatedAllocation);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllocation(@PathVariable Integer id) {
        hotelRateCodeAllocationService.deleteAllocation(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> deleteAllocationsByHotelId(@PathVariable Integer hotelId) {
        hotelRateCodeAllocationService.deleteAllocationsByHotelId(hotelId);
        return ResponseEntity.ok().build();
    }
}