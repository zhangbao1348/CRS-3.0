package com.crs.controller;

import com.crs.entity.RoomTypeDiff;
import com.crs.service.RoomTypeDiffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房型差价控制器
 * 提供房型差价管理的REST API端点
 */
@RestController
@RequestMapping("/api/room-type-diffs")
@CrossOrigin(origins = "*")
public class RoomTypeDiffController {
    
    @Autowired
    private RoomTypeDiffService roomTypeDiffService;
    
    /**
     * 获取所有房型差价列表
     * @return 房型差价列表
     */
    @GetMapping
    public ResponseEntity<List<RoomTypeDiff>> getAllRoomTypeDiffs() {
        List<RoomTypeDiff> roomTypeDiffs = roomTypeDiffService.getAllRoomTypeDiffs();
        return new ResponseEntity<>(roomTypeDiffs, HttpStatus.OK);
    }
    
    /**
     * 根据ID获取房型差价详情
     * @param id 房型差价ID
     * @return 房型差价详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeDiff> getRoomTypeDiffById(@PathVariable Integer id) {
        return roomTypeDiffService.getRoomTypeDiffById(id)
                .map(roomTypeDiff -> new ResponseEntity<>(roomTypeDiff, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 根据差价体系ID获取房型差价列表
     * @param systemId 差价体系ID
     * @return 房型差价列表
     */
    @GetMapping("/system/{systemId}")
    public ResponseEntity<List<RoomTypeDiff>> getRoomTypeDiffsBySystemId(@PathVariable Integer systemId) {
        List<RoomTypeDiff> roomTypeDiffs = roomTypeDiffService.getRoomTypeDiffsBySystemId(systemId);
        return new ResponseEntity<>(roomTypeDiffs, HttpStatus.OK);
    }
    
    /**
     * 根据差价体系ID和状态获取房型差价列表
     * @param systemId 差价体系ID
     * @param status 状态
     * @return 房型差价列表
     */
    @GetMapping("/system/{systemId}/status/{status}")
    public ResponseEntity<List<RoomTypeDiff>> getRoomTypeDiffsBySystemIdAndStatus(
            @PathVariable Integer systemId,
            @PathVariable RoomTypeDiff.Status status) {
        List<RoomTypeDiff> roomTypeDiffs = roomTypeDiffService.getRoomTypeDiffsBySystemIdAndStatus(systemId, status);
        return new ResponseEntity<>(roomTypeDiffs, HttpStatus.OK);
    }
    
    /**
     * 创建房型差价
     * @param roomTypeDiff 房型差价信息
     * @return 创建的房型差价信息
     */
    @PostMapping
    public ResponseEntity<RoomTypeDiff> createRoomTypeDiff(@RequestBody RoomTypeDiff roomTypeDiff) {
        RoomTypeDiff createdRoomTypeDiff = roomTypeDiffService.createRoomTypeDiff(roomTypeDiff);
        return new ResponseEntity<>(createdRoomTypeDiff, HttpStatus.CREATED);
    }
    
    /**
     * 批量创建房型差价
     * @param roomTypeDiffs 房型差价列表
     * @return 创建的房型差价列表
     */
    @PostMapping("/batch")
    public ResponseEntity<List<RoomTypeDiff>> createBatchRoomTypeDiffs(@RequestBody List<RoomTypeDiff> roomTypeDiffs) {
        List<RoomTypeDiff> createdRoomTypeDiffs = roomTypeDiffService.createBatchRoomTypeDiffs(roomTypeDiffs);
        return new ResponseEntity<>(createdRoomTypeDiffs, HttpStatus.CREATED);
    }
    
    /**
     * 更新房型差价
     * @param id 房型差价ID
     * @param roomTypeDiff 房型差价信息
     * @return 更新后的房型差价信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeDiff> updateRoomTypeDiff(@PathVariable Integer id, @RequestBody RoomTypeDiff roomTypeDiff) {
        try {
            RoomTypeDiff updatedRoomTypeDiff = roomTypeDiffService.updateRoomTypeDiff(id, roomTypeDiff);
            return new ResponseEntity<>(updatedRoomTypeDiff, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 删除房型差价
     * @param id 房型差价ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomTypeDiff(@PathVariable Integer id) {
        try {
            roomTypeDiffService.deleteRoomTypeDiff(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 批量删除房型差价
     * @param systemId 差价体系ID
     * @return 删除结果
     */
    @DeleteMapping("/system/{systemId}")
    public ResponseEntity<Void> deleteRoomTypeDiffsBySystemId(@PathVariable Integer systemId) {
        roomTypeDiffService.deleteRoomTypeDiffsBySystemId(systemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 获取房型差价的weekdays数组
     * @param id 房型差价ID
     * @return weekdays数组
     */
    @GetMapping("/{id}/weekdays")
    public ResponseEntity<List<String>> getRoomTypeDiffWeekdays(@PathVariable Integer id) {
        return roomTypeDiffService.getRoomTypeDiffById(id)
                .map(roomTypeDiff -> {
                    List<String> weekdaysList = roomTypeDiffService.weekdaysStrToList(roomTypeDiff.getWeekdays());
                    return new ResponseEntity<>(weekdaysList, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 更新房型差价的weekdays
     * @param id 房型差价ID
     * @param weekdaysList weekdays数组
     * @return 更新后的房型差价
     */
    @PutMapping("/{id}/weekdays")
    public ResponseEntity<RoomTypeDiff> updateRoomTypeDiffWeekdays(@PathVariable Integer id, @RequestBody List<String> weekdaysList) {
        try {
            RoomTypeDiff updatedRoomTypeDiff = roomTypeDiffService.updateRoomTypeDiffWeekdays(id, weekdaysList);
            return new ResponseEntity<>(updatedRoomTypeDiff, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
