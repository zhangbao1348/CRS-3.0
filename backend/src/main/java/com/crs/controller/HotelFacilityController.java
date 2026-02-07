package com.crs.controller;

import com.crs.entity.HotelFacility;
import com.crs.service.HotelFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hotel-facilities")
@CrossOrigin(origins = "*")
public class HotelFacilityController {
    
    @Autowired
    private HotelFacilityService hotelFacilityService;
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelFacility>> getFacilitiesByHotelId(@PathVariable Integer hotelId) {
        List<HotelFacility> facilities = hotelFacilityService.getFacilitiesByHotelId(hotelId);
        return ResponseEntity.ok(facilities);
    }
    
    @GetMapping("/hotel/{hotelId}/type/{type}")
    public ResponseEntity<List<HotelFacility>> getFacilitiesByType(@PathVariable Integer hotelId, @PathVariable String type) {
        List<HotelFacility> facilities = hotelFacilityService.getFacilitiesByType(hotelId, type);
        return ResponseEntity.ok(facilities);
    }
    
    @PostMapping
    public ResponseEntity<HotelFacility> createFacility(@RequestBody HotelFacility facility) {
        HotelFacility createdFacility = hotelFacilityService.createFacility(facility);
        return ResponseEntity.ok(createdFacility);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HotelFacility> updateFacility(@PathVariable Integer id, @RequestBody HotelFacility facility) {
        facility.setId(id);
        HotelFacility updatedFacility = hotelFacilityService.updateFacility(facility);
        return ResponseEntity.ok(updatedFacility);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Integer id) {
        hotelFacilityService.deleteFacility(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> deleteFacilitiesByHotelId(@PathVariable Integer hotelId) {
        hotelFacilityService.deleteFacilitiesByHotelId(hotelId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    public ResponseEntity<List<HotelFacility>> getAllFacilities() {
        List<HotelFacility> facilities = hotelFacilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }
}