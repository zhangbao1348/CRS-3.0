package com.crs.controller;

import com.crs.entity.HotelRoomTypeAllocation;
import com.crs.service.HotelRoomTypeAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hotel-room-type-allocations")
@CrossOrigin(origins = "*")
public class HotelRoomTypeAllocationController {
    
    @Autowired
    private HotelRoomTypeAllocationService hotelRoomTypeAllocationService;
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelRoomTypeAllocation>> getAllocationsByHotelId(@PathVariable Integer hotelId) {
        List<HotelRoomTypeAllocation> allocations = hotelRoomTypeAllocationService.getAllocationsByHotelId(hotelId);
        return ResponseEntity.ok(allocations);
    }
    
    @GetMapping("/hotel/{hotelId}/room-type/{roomTypeId}")
    public ResponseEntity<HotelRoomTypeAllocation> getAllocationByHotelAndRoomType(@PathVariable Integer hotelId, @PathVariable Integer roomTypeId) {
        HotelRoomTypeAllocation allocation = hotelRoomTypeAllocationService.getAllocationByHotelAndRoomType(hotelId, roomTypeId);
        return ResponseEntity.ok(allocation);
    }
    
    @GetMapping("/hotel/{hotelId}/allocated")
    public ResponseEntity<List<HotelRoomTypeAllocation>> getAllocatedRoomTypesByHotelId(@PathVariable Integer hotelId) {
        List<HotelRoomTypeAllocation> allocations = hotelRoomTypeAllocationService.getAllocatedRoomTypesByHotelId(hotelId);
        return ResponseEntity.ok(allocations);
    }
    
    @PostMapping
    public ResponseEntity<HotelRoomTypeAllocation> createAllocation(@RequestBody HotelRoomTypeAllocation allocation) {
        HotelRoomTypeAllocation createdAllocation = hotelRoomTypeAllocationService.createAllocation(allocation);
        return ResponseEntity.ok(createdAllocation);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HotelRoomTypeAllocation> updateAllocation(@PathVariable Integer id, @RequestBody HotelRoomTypeAllocation allocation) {
        allocation.setId(id);
        HotelRoomTypeAllocation updatedAllocation = hotelRoomTypeAllocationService.updateAllocation(allocation);
        return ResponseEntity.ok(updatedAllocation);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllocation(@PathVariable Integer id) {
        hotelRoomTypeAllocationService.deleteAllocation(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> deleteAllocationsByHotelId(@PathVariable Integer hotelId) {
        hotelRoomTypeAllocationService.deleteAllocationsByHotelId(hotelId);
        return ResponseEntity.ok().build();
    }
}