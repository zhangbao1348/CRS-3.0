package com.crs.controller;

import com.crs.entity.ChannelCode;
import com.crs.service.ChannelCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 渠道码控制器
 * 提供渠道码管理的REST API端点
 */
@RestController
@RequestMapping("/api/channel-codes")
@CrossOrigin(origins = "*")
public class ChannelCodeController {
    
    @Autowired
    private ChannelCodeService channelCodeService;
    
    /**
     * 获取所有渠道码列表
     * @return 渠道码列表
     */
    @GetMapping
    public ResponseEntity<List<ChannelCode>> getAllChannelCodes() {
        List<ChannelCode> channelCodes = channelCodeService.getAllChannelCodes();
        return new ResponseEntity<>(channelCodes, HttpStatus.OK);
    }
    
    /**
     * 根据ID获取渠道码详情
     * @param id 渠道码ID
     * @return 渠道码详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChannelCode> getChannelCodeById(@PathVariable Integer id) {
        return channelCodeService.getChannelCodeById(id)
                .map(channelCode -> new ResponseEntity<>(channelCode, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 根据代码获取渠道码详情
     * @param code 渠道码代码
     * @return 渠道码详情
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ChannelCode> getChannelCodeByCode(@PathVariable String code) {
        return channelCodeService.getChannelCodeByCode(code)
                .map(channelCode -> new ResponseEntity<>(channelCode, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 根据名称搜索渠道码
     * @param name 渠道码名称
     * @return 渠道码列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<ChannelCode>> searchChannelCodesByName(@RequestParam String name) {
        List<ChannelCode> channelCodes = channelCodeService.searchChannelCodesByName(name);
        return new ResponseEntity<>(channelCodes, HttpStatus.OK);
    }
    
    /**
     * 根据状态获取渠道码列表
     * @param status 状态
     * @return 渠道码列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ChannelCode>> getChannelCodesByStatus(@PathVariable ChannelCode.Status status) {
        List<ChannelCode> channelCodes = channelCodeService.getChannelCodesByStatus(status);
        return new ResponseEntity<>(channelCodes, HttpStatus.OK);
    }
    
    /**
     * 创建渠道码
     * @param channelCode 渠道码信息
     * @return 创建的渠道码信息
     */
    @PostMapping
    public ResponseEntity<ChannelCode> createChannelCode(@RequestBody ChannelCode channelCode) {
        try {
            ChannelCode createdChannelCode = channelCodeService.createChannelCode(channelCode);
            return new ResponseEntity<>(createdChannelCode, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 更新渠道码
     * @param id 渠道码ID
     * @param channelCode 渠道码信息
     * @return 更新后的渠道码信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChannelCode> updateChannelCode(@PathVariable Integer id, @RequestBody ChannelCode channelCode) {
        try {
            ChannelCode updatedChannelCode = channelCodeService.updateChannelCode(id, channelCode);
            return new ResponseEntity<>(updatedChannelCode, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 删除渠道码
     * @param id 渠道码ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannelCode(@PathVariable Integer id) {
        try {
            channelCodeService.deleteChannelCode(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 批量创建渠道码
     * @param channelCodes 渠道码列表
     * @return 创建的渠道码列表
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ChannelCode>> createBatchChannelCodes(@RequestBody List<ChannelCode> channelCodes) {
        try {
            List<ChannelCode> createdChannelCodes = channelCodeService.createBatchChannelCodes(channelCodes);
            return new ResponseEntity<>(createdChannelCodes, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * 批量更新渠道码状态
     * @param ids 渠道码ID列表
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/batch-status")
    public ResponseEntity<String> batchUpdateChannelCodeStatus(
            @RequestParam List<Integer> ids,
            @RequestParam ChannelCode.Status status) {
        int updatedCount = channelCodeService.batchUpdateChannelCodeStatus(ids, status);
        return new ResponseEntity<>("Updated " + updatedCount + " channel codes", HttpStatus.OK);
    }
    
    /**
     * 获取活跃的渠道码列表
     * @return 活跃的渠道码列表
     */
    @GetMapping("/active")
    public ResponseEntity<List<ChannelCode>> getActiveChannelCodes() {
        List<ChannelCode> channelCodes = channelCodeService.getActiveChannelCodes();
        return new ResponseEntity<>(channelCodes, HttpStatus.OK);
    }
    
    /**
     * 导入渠道码
     * @param channelCodes 渠道码列表
     * @return 导入结果
     */
    @PostMapping("/import")
    public ResponseEntity<String> importChannelCodes(@RequestBody List<ChannelCode> channelCodes) {
        String result = channelCodeService.importChannelCodes(channelCodes);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

