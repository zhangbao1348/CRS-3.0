package com.crs.controller;

import com.crs.entity.HotelImage;
import com.crs.entity.Hotel;
import com.crs.service.HotelImageService;
import com.crs.repository.HotelRepository;
import com.crs.util.TenantContext;
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
import java.util.Optional;
import java.util.UUID;

/**
 * HotelImageController 控制器 (REST Controller)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【HotelImageController】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/09-系统设置.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 HotelImageController 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/hotel-images")
@CrossOrigin(origins = "*")
public class HotelImageController {
    
    @Autowired
    private HotelImageService hotelImageService;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    private Integer getCurrentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    private boolean validateHotelTenant(String hotelCode) {
        return hotelRepository.existsByHotelCodeAndTenantId(hotelCode, getCurrentTenantId());
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
    
    @GetMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<List<HotelImage>> getImagesByHotelCode(@PathVariable String hotelCode) {
        if (!validateHotelTenant(hotelCode)) {
            return ResponseEntity.status(403).build();
        }
        List<HotelImage> images = hotelImageService.getImagesByHotelCode(hotelCode);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/by-code/hotel/{hotelCode}/type/{type}")
    public ResponseEntity<List<HotelImage>> getImagesByHotelCodeAndType(@PathVariable String hotelCode, @PathVariable String type) {
        if (!validateHotelTenant(hotelCode)) {
            return ResponseEntity.status(403).build();
        }
        List<HotelImage> images = hotelImageService.getImagesByHotelCodeAndType(hotelCode, type);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/by-code/hotel/{hotelCode}")
    public ResponseEntity<Void> deleteImagesByHotelCode(@PathVariable String hotelCode) {
        if (!validateHotelTenant(hotelCode)) {
            return ResponseEntity.status(403).build();
        }
        hotelImageService.deleteImagesByHotelCode(hotelCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<HotelImage> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("hotelCode") String hotelCode,
            @RequestParam("imageType") String imageType) {
        try {
            if (hotelCode == null || hotelCode.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (!validateHotelTenant(hotelCode)) {
                return ResponseEntity.status(403).build();
            }
            
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
            hotelImage.setHotelCode(hotelCode);
            hotelImage.setImageType(imageType);
            hotelImage.setImagePath(filePath);
            hotelImage.setImageName(originalFilename);
            hotelImage.setSortOrder(0);
            hotelImage.setTenantId(getCurrentTenantId());
            
            // 保存到数据库
            HotelImage savedImage = hotelImageService.createImage(hotelImage);
            
            return ResponseEntity.ok(savedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}