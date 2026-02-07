package com.crs.controller;

import com.crs.entity.HotelImage;
import com.crs.service.HotelImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

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
    
    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> viewImage(@PathVariable Integer id) {
        try {
            HotelImage image = hotelImageService.getImageById(id);
            if (image == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            File imgFile = new File(image.getImagePath());
            if (!imgFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            byte[] bytes = Files.readAllBytes(imgFile.toPath());
            String contentType = Files.probeContentType(imgFile.toPath());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping("/upload")
    public ResponseEntity<HotelImage> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("hotelId") Integer hotelId,
            @RequestParam("imageType") String imageType) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            
            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 定义文件保存路径
            String uploadDir = System.getProperty("user.dir") + "/uploads/hotel_images/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // 保存文件
            String filePath = uploadDir + uniqueFilename;
            File dest = new File(filePath);
            file.transferTo(dest);
            
            // 创建HotelImage对象
            HotelImage hotelImage = new HotelImage();
            hotelImage.setHotelId(hotelId);
            hotelImage.setImageType(imageType);
            hotelImage.setImagePath(filePath);
            hotelImage.setImageName(originalFilename);
            hotelImage.setSortOrder(0);
            
            // 保存到数据库
            HotelImage savedImage = hotelImageService.createImage(hotelImage);
            
            return ResponseEntity.ok(savedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}