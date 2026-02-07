package com.crs.service;

import com.crs.entity.RoomTypeDiff;
import com.crs.repository.RoomTypeDiffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 房型差价服务类
 * 用于处理房型差价相关的业务逻辑
 */
@Service
public class RoomTypeDiffService {
    
    private final RoomTypeDiffRepository roomTypeDiffRepository;
    
    public RoomTypeDiffService(RoomTypeDiffRepository roomTypeDiffRepository) {
        this.roomTypeDiffRepository = roomTypeDiffRepository;
    }
    
    /**
     * 获取所有房型差价列表
     * @return 房型差价列表
     */
    public List<RoomTypeDiff> getAllRoomTypeDiffs() {
        return roomTypeDiffRepository.findAll();
    }
    
    /**
     * 根据ID获取房型差价详情
     * @param id 房型差价ID
     * @return 房型差价详情
     */
    public Optional<RoomTypeDiff> getRoomTypeDiffById(Integer id) {
        return roomTypeDiffRepository.findById(id);
    }
    
    /**
     * 根据差价体系ID获取房型差价列表
     * @param systemId 差价体系ID
     * @return 房型差价列表
     */
    public List<RoomTypeDiff> getRoomTypeDiffsBySystemId(Integer systemId) {
        return roomTypeDiffRepository.findBySystemId(systemId);
    }
    
    /**
     * 根据差价体系ID和状态获取房型差价列表
     * @param systemId 差价体系ID
     * @param status 状态
     * @return 房型差价列表
     */
    public List<RoomTypeDiff> getRoomTypeDiffsBySystemIdAndStatus(Integer systemId, RoomTypeDiff.Status status) {
        return roomTypeDiffRepository.findBySystemIdAndStatus(systemId, status);
    }
    
    /**
     * 创建房型差价
     * @param roomTypeDiff 房型差价信息
     * @return 创建的房型差价信息
     */
    public RoomTypeDiff createRoomTypeDiff(RoomTypeDiff roomTypeDiff) {
        return roomTypeDiffRepository.save(roomTypeDiff);
    }
    
    /**
     * 批量创建房型差价
     * @param roomTypeDiffs 房型差价列表
     * @return 创建的房型差价列表
     */
    public List<RoomTypeDiff> createBatchRoomTypeDiffs(List<RoomTypeDiff> roomTypeDiffs) {
        return roomTypeDiffRepository.saveAll(roomTypeDiffs);
    }
    
    /**
     * 更新房型差价
     * @param id 房型差价ID
     * @param roomTypeDiff 房型差价信息
     * @return 更新后的房型差价信息
     */
    public RoomTypeDiff updateRoomTypeDiff(Integer id, RoomTypeDiff roomTypeDiff) {
        RoomTypeDiff existingRoomTypeDiff = roomTypeDiffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room type diff not found"));
        
        existingRoomTypeDiff.setSystemId(roomTypeDiff.getSystemId());
        existingRoomTypeDiff.setRoomTypeId(roomTypeDiff.getRoomTypeId());
        existingRoomTypeDiff.setCode(roomTypeDiff.getCode());
        existingRoomTypeDiff.setName(roomTypeDiff.getName());
        existingRoomTypeDiff.setValue(roomTypeDiff.getValue());
        existingRoomTypeDiff.setStartDate(roomTypeDiff.getStartDate());
        existingRoomTypeDiff.setEndDate(roomTypeDiff.getEndDate());
        existingRoomTypeDiff.setWeekdays(roomTypeDiff.getWeekdays());
        existingRoomTypeDiff.setStatus(roomTypeDiff.getStatus());
        
        return roomTypeDiffRepository.save(existingRoomTypeDiff);
    }
    
    /**
     * 删除房型差价
     * @param id 房型差价ID
     */
    public void deleteRoomTypeDiff(Integer id) {
        if (!roomTypeDiffRepository.existsById(id)) {
            throw new RuntimeException("Room type diff not found");
        }
        roomTypeDiffRepository.deleteById(id);
    }
    
    /**
     * 批量删除房型差价
     * @param systemId 差价体系ID
     */
    public void deleteRoomTypeDiffsBySystemId(Integer systemId) {
        roomTypeDiffRepository.deleteBySystemId(systemId);
    }

    /**
     * 将weekdays字符串转换为数组
     * @param weekdaysStr weekdays字符串，格式为逗号分隔的数字
     * @return weekdays数组
     */
    public List<String> weekdaysStrToList(String weekdaysStr) {
        if (weekdaysStr == null || weekdaysStr.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.stream(weekdaysStr.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * 将weekdays数组转换为字符串
     * @param weekdaysList weekdays数组
     * @return weekdays字符串，格式为逗号分隔的数字
     */
    public String weekdaysListToStr(List<String> weekdaysList) {
        if (weekdaysList == null || weekdaysList.isEmpty()) {
            return "";
        }
        return weekdaysList.stream()
                .collect(Collectors.joining(","));
    }

    /**
     * 更新房型差价的weekdays
     * @param id 房型差价ID
     * @param weekdaysList weekdays数组
     * @return 更新后的房型差价
     */
    public RoomTypeDiff updateRoomTypeDiffWeekdays(Integer id, List<String> weekdaysList) {
        RoomTypeDiff existingRoomTypeDiff = roomTypeDiffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room type diff not found"));
        
        existingRoomTypeDiff.setWeekdays(weekdaysListToStr(weekdaysList));
        return roomTypeDiffRepository.save(existingRoomTypeDiff);
    }
}
