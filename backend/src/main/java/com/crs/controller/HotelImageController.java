package com.crs.controller;

import com.crs.entity.HotelImage;
import com.crs.service.HotelImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hotel-images")
@CrossOrigin(origins = "*")
public class HotelImageController {
    
    @Autowired
    private HotelImageService hotelImageService;
    
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HotelImage>> getImagesByHotelId(@PathVariable Integer hotelId) {
        List<HotelImage> images = hotelImageService.getImagesByHotelId(hotelId);
        return ResponseEntity.ok(images);
    }
    
    @GetMapping("/hotel/{hotelId}/type/{type}")
    public ResponseEntity<List<HotelImage>> getImagesByType(@PathVariable Integer hotelId, @PathVariable String type) {
        List<HotelImage> images = hotelImageService.getImagesByType(hotelId, type);
        return ResponseEntity.ok(images);
    }
    
    @GetMapping("/hotel/{hotelId}/sorted")
    public ResponseEntity<List<HotelImage>> getImagesByHotelIdOrderBySort(@PathVariable Integer hotelId) {
        List<HotelImage> images = hotelImageService.getImagesByHotelIdOrderBySort(hotelId);
        return ResponseEntity.ok(images);
    }
    
    @PostMapping
    public ResponseEntity<HotelImage> createImage(@RequestBody HotelImage image) {
        HotelImage createdImage = hotelImageService.createImage(image);
        return ResponseEntity.ok(createdImage);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<HotelImage> updateImage(@PathVariable Integer id, @RequestBody HotelImage image) {
        image.setId(id);
        HotelImage updatedImage = hotelImageService.updateImage(image);
        return ResponseEntity.ok(updatedImage);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer id) {
        hotelImageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/hotel/{hotelId}")
    public ResponseEntity<Void> deleteImagesByHotelId(@PathVariable Integer hotelId) {
        hotelImageService.deleteImagesByHotelId(hotelId);
        return ResponseEntity.ok().build();
    }
}