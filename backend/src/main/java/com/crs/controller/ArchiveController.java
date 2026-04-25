package com.crs.controller;

import com.crs.entity.Archive;
import com.crs.service.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 档案控制器
 * 提供档案管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/archives")
public class ArchiveController {
    
    @Autowired
    private ArchiveService archiveService;
    
    /**
     * 获取所有档案
     * @return 档案列表
     */
    @GetMapping
    public ResponseEntity<List<Archive>> getAllArchives() {
        List<Archive> archives = archiveService.getAllArchives();
        return ResponseEntity.ok(archives);
    }
    
    /**
     * 根据ID获取档案
     * @param id 档案ID
     * @return 档案
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getArchiveById(@PathVariable Integer id) {
        Optional<Archive> archive = archiveService.getById(id);
        if (archive.isPresent()) {
            return ResponseEntity.ok(archive.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("档案不存在");
        }
    }
    
    /**
     * 创建档案
     * @param archive 档案
     * @return 创建的档案
     */
    @PostMapping
    public ResponseEntity<?> createArchive(@RequestBody Archive archive) {
        try {
            Archive created = archiveService.create(archive);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新档案
     * @param id 档案ID
     * @param archive 档案
     * @return 更新后的档案
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateArchive(@PathVariable Integer id, @RequestBody Archive archive) {
        try {
            Archive updated = archiveService.update(id, archive);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 删除档案
     * @param id 档案ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArchive(@PathVariable Integer id) {
        try {
            archiveService.delete(id);
            return ResponseEntity.ok("档案删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
