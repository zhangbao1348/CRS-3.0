package com.crs.controller;

import com.crs.entity.ChannelCode;
import com.crs.repository.ChannelCodeRepository;
import com.crs.repository.ChannelHotelMappingRepository;
import com.crs.repository.ChannelPublishRecordRepository;
import com.crs.repository.ChannelRateCodeMappingRepository;
import com.crs.repository.ChannelRoomTypeMappingRepository;
import com.crs.repository.TenantChannelRepository;
import com.crs.service.ChannelCodeService;
import com.crs.util.CodeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 渠道码控制器
 * 提供渠道码的CRUD操作API
 */
@RestController
@RequestMapping("/api/channel-codes")
public class ChannelCodeController {

    @Autowired
    private ChannelCodeService channelCodeService;

    @Autowired
    private ChannelCodeRepository channelCodeRepository;

    @Autowired
    private ChannelHotelMappingRepository channelHotelMappingRepository;

    @Autowired
    private ChannelRoomTypeMappingRepository channelRoomTypeMappingRepository;

    @Autowired
    private ChannelRateCodeMappingRepository channelRateCodeMappingRepository;

    @Autowired
    private ChannelPublishRecordRepository channelPublishRecordRepository;

    @Autowired
    private TenantChannelRepository tenantChannelRepository;

    private Integer getCurrentTenantId() {
        Integer tenantId = com.crs.util.TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context missing");
        }
        return tenantId;
    }

    /**
     * 获取所有渠道码（树形结构）
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllChannelCodes() {
        try {
            List<Map<String, Object>> treeData = channelCodeService.getAllChannelCodesAsTree();
            return ResponseEntity.ok(treeData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 兼容性接口：根据 groupId 获取所有渠道码（实际使用当前登录租户 ID）
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Map<String, Object>>> getChannelCodesByGroupId(@PathVariable Integer groupId) {
        // 忽略路径中的 groupId，直接使用当前租户上下文
        return getAllChannelCodes();
    }

    /**
     * 获取第三级（叶子节点）渠道码
     */
    @GetMapping("/third-level")
    public ResponseEntity<List<ChannelCode>> getThirdLevelChannelCodes() {
        try {
            // 获取第3级渠道码，如果没有3级则获取第2级中没有子节点的
            Integer tenantId = getCurrentTenantId();
            List<ChannelCode> level3 = channelCodeRepository.findByTenantIdAndLevel(tenantId, 3);
            if (level3.isEmpty()) {
                // 回退到第2级
                level3 = channelCodeRepository.findByTenantIdAndLevel(tenantId, 2);
            }
            if (level3.isEmpty()) {
                level3 = channelCodeRepository.findByTenantIdAndLevel(tenantId, 1);
            }
            return ResponseEntity.ok(level3);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 根据父ID获取渠道码
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<ChannelCode>> getChannelCodesByParentId(@PathVariable Integer parentId) {
        try {
            List<ChannelCode> channelCodes = channelCodeService.getChannelCodesByParentId(null, parentId);
            return ResponseEntity.ok(channelCodes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 根据ID获取渠道码
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChannelCode> getChannelCodeById(@PathVariable Integer id) {
        try {
            ChannelCode channelCode = channelCodeService.getChannelCodeById(id);
            if (channelCode != null) {
                return ResponseEntity.ok(channelCode);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 兼容性接口：根据 groupId 和 ID 获取渠道码
     */
    @GetMapping("/group/{groupId}/{id}")
    public ResponseEntity<ChannelCode> getChannelCodeByGroupIdAndId(@PathVariable Integer groupId, @PathVariable Integer id) {
        return getChannelCodeById(id);
    }

    /**
     * 创建渠道码
     */
    @PostMapping
    public ResponseEntity<?> createChannelCode(@RequestBody ChannelCode channelCode) {
        try {
            if (channelCode.getCode() == null || channelCode.getName() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "渠道码编码和名称为必填项"));
            }
            if (!CodeValidator.isValid(channelCode.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            if (!channelCodeService.isCodeUnique(null, channelCode.getCode(), null)) {
                return ResponseEntity.badRequest().body(Map.of("error", "渠道码编码已存在"));
            }
            ChannelCode createdChannelCode = channelCodeService.createChannelCode(channelCode);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChannelCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 更新渠道码
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChannelCode(@PathVariable Integer id, @RequestBody ChannelCode channelCode) {
        try {
            if (channelCode.getCode() != null && !CodeValidator.isValid(channelCode.getCode())) {
                return ResponseEntity.badRequest().body(Map.of("error", CodeValidator.ERROR_MESSAGE));
            }
            if (!channelCodeService.isCodeUnique(null, channelCode.getCode(), id)) {
                return ResponseEntity.badRequest().body(Map.of("error", "渠道码编码已存在"));
            }
            channelCode.setId(id);
            ChannelCode updatedChannelCode = channelCodeService.updateChannelCode(channelCode);
            if (updatedChannelCode != null) {
                return ResponseEntity.ok(updatedChannelCode);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 兼容性接口：根据 groupId 和 ID 更新渠道码
     */
    @PutMapping("/group/{groupId}/{id}")
    public ResponseEntity<?> updateChannelCodeByGroupId(@PathVariable Integer groupId, @PathVariable Integer id, @RequestBody ChannelCode channelCode) {
        return updateChannelCode(id, channelCode);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChannelCode(@PathVariable Integer id) {
        try {
            // 获取渠道码对象以取得其业务编码
            ChannelCode channelCode = channelCodeRepository.findByTenantIdAndId(getCurrentTenantId(), id);
            if (channelCode == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "渠道码不存在"));
            }
            
            // 检查当前节点及子节点的配置、映射和发布引用。
            long refCount = countSubtreeReferences(channelCode);
            if (refCount > 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "该渠道码或其子节点已被 " + refCount + " 处渠道业务引用，无法删除"));
            }
            channelCodeService.deleteChannelCode(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 兼容性接口：根据 groupId 和 ID 删除渠道码
     */
    @DeleteMapping("/group/{groupId}/{id}")
    public ResponseEntity<?> deleteChannelCodeByGroupId(@PathVariable Integer groupId, @PathVariable Integer id) {
        return deleteChannelCode(id);
    }

    /**
     * 检查渠道码CODE是否唯一
     */
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Boolean>> checkCodeUnique(@RequestParam String code, @RequestParam(required = false) Integer id) {
        try {
            boolean isUnique = channelCodeService.isCodeUnique(null, code, id);
            return ResponseEntity.ok().body(Map.of("unique", isUnique));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /** 计算整个子树的渠道业务引用，避免删除父节点绕过引用约束。 */
    private long countSubtreeReferences(ChannelCode node) {
        Integer tenantId = getCurrentTenantId();
        String code = node.getCode();
        long count = channelHotelMappingRepository.countByTenantIdAndChannelCode(tenantId, code)
                + channelRoomTypeMappingRepository.countByTenantIdAndChannelCode(tenantId, code)
                + channelRateCodeMappingRepository.countByTenantIdAndChannelCode(tenantId, code)
                + channelPublishRecordRepository.countByTenantIdAndChannelCode(tenantId, code)
                + (tenantChannelRepository.existsByTenantIdAndChannelCode(tenantId, code) ? 1 : 0);
        for (ChannelCode child : channelCodeService.getChannelCodesByParentId(tenantId, node.getId())) {
            count += countSubtreeReferences(child);
        }
        return count;
    }

    /**
     * 批量创建渠道码（按租户）
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ChannelCode>> batchCreateChannelCodes(
            @RequestParam(required = false) Integer tenantId,
            @RequestBody List<ChannelCode> channelCodes) {
        try {
            List<ChannelCode> createdChannelCodes = channelCodeService.batchCreateChannelCodes(tenantId, channelCodes);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChannelCodes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 为指定租户初始化默认渠道码
     */
    @PostMapping("/init-default/{tenantId}")
    public ResponseEntity<List<ChannelCode>> initDefaultChannelCodesForTenant(@PathVariable Integer tenantId) {
        try {
            List<ChannelCode> defaultCodes = channelCodeService.initDefaultChannelCodesForTenant(tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(defaultCodes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
