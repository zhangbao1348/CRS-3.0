package com.crs.service;

import com.crs.entity.DictionaryItem;
import com.crs.entity.DictionaryType;
import com.crs.repository.DictionaryItemRepository;
import com.crs.repository.DictionaryTypeRepository;
import com.crs.util.CodeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DictionaryItemService {

    private final DictionaryItemRepository dictionaryItemRepository;
    private final DictionaryTypeRepository dictionaryTypeRepository;

    public DictionaryItemService(DictionaryItemRepository dictionaryItemRepository,
                                 DictionaryTypeRepository dictionaryTypeRepository) {
        this.dictionaryItemRepository = dictionaryItemRepository;
        this.dictionaryTypeRepository = dictionaryTypeRepository;
    }

    public List<DictionaryItem> getDictionaryItems(Integer tenantId, String typeCode, String keyword) {
        ensureTypeExists(tenantId, typeCode);
        return dictionaryItemRepository.searchByTenantIdAndTypeCode(tenantId, typeCode, keyword);
    }

    public DictionaryItem createDictionaryItem(Integer tenantId, DictionaryItem dictionaryItem) {
        ensureTypeExists(tenantId, dictionaryItem.getTypeCode());
        validateItemCode(dictionaryItem.getItemCode());
        if (dictionaryItemRepository.existsByTenantIdAndTypeCodeAndItemCode(
                tenantId, dictionaryItem.getTypeCode(), dictionaryItem.getItemCode())) {
            throw new IllegalArgumentException("字典项编码已存在");
        }

        dictionaryItem.setTenantId(tenantId);
        dictionaryItem.setItemValue(normalizeItemValue(dictionaryItem.getItemValue(), dictionaryItem.getItemCode()));
        if (dictionaryItem.getStatus() == null) {
            dictionaryItem.setStatus(DictionaryItem.Status.active);
        }
        if (dictionaryItem.getSortOrder() == null) {
            dictionaryItem.setSortOrder(0);
        }
        if (dictionaryItem.getIsDefault() == null) {
            dictionaryItem.setIsDefault(false);
        }
        if (Boolean.TRUE.equals(dictionaryItem.getIsDefault())) {
            dictionaryItemRepository.clearDefaultFlag(tenantId, dictionaryItem.getTypeCode(), null);
        }
        return dictionaryItemRepository.save(dictionaryItem);
    }

    public DictionaryItem updateDictionaryItem(Integer tenantId, Integer id, DictionaryItem dictionaryItem) {
        DictionaryItem existing = dictionaryItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("字典项不存在"));

        String typeCode = existing.getTypeCode();
        ensureTypeExists(tenantId, typeCode);
        if (dictionaryItem.getItemCode() != null && !dictionaryItem.getItemCode().equals(existing.getItemCode())) {
            validateItemCode(dictionaryItem.getItemCode());
            if (dictionaryItemRepository.existsByTenantIdAndTypeCodeAndItemCodeAndIdNot(
                    tenantId, typeCode, dictionaryItem.getItemCode(), id)) {
                throw new IllegalArgumentException("字典项编码已存在");
            }
            existing.setItemCode(dictionaryItem.getItemCode());
        }

        existing.setItemName(dictionaryItem.getItemName());
        existing.setDescription(dictionaryItem.getDescription());
        existing.setStatus(dictionaryItem.getStatus() == null ? existing.getStatus() : dictionaryItem.getStatus());
        existing.setSortOrder(dictionaryItem.getSortOrder() == null ? existing.getSortOrder() : dictionaryItem.getSortOrder());
        existing.setItemValue(normalizeItemValue(dictionaryItem.getItemValue(), existing.getItemCode()));

        if (dictionaryItem.getIsDefault() != null) {
            existing.setIsDefault(dictionaryItem.getIsDefault());
            if (Boolean.TRUE.equals(dictionaryItem.getIsDefault())) {
                dictionaryItemRepository.clearDefaultFlag(tenantId, typeCode, id);
            }
        }
        return dictionaryItemRepository.save(existing);
    }

    public void deleteDictionaryItem(Integer tenantId, Integer id) {
        DictionaryItem existing = dictionaryItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("字典项不存在"));
        dictionaryItemRepository.delete(existing);
    }

    public List<Map<String, Object>> getActiveDictionaryOptions(Integer tenantId, String typeCode) {
        ensureTypeExists(tenantId, typeCode);
        return dictionaryItemRepository
                .findByTenantIdAndTypeCodeAndStatusOrderBySortOrderAscIdAsc(tenantId, typeCode, DictionaryItem.Status.active)
                .stream()
                .map(item -> {
                    Map<String, Object> option = new LinkedHashMap<>();
                    option.put("code", item.getItemCode());
                    option.put("name", item.getItemName());
                    option.put("value", normalizeItemValue(item.getItemValue(), item.getItemCode()));
                    option.put("sortOrder", item.getSortOrder());
                    return option;
                })
                .toList();
    }

    private void ensureTypeExists(Integer tenantId, String typeCode) {
        DictionaryType dictionaryType = dictionaryTypeRepository.findByTenantIdAndTypeCode(tenantId, typeCode).orElse(null);
        if (dictionaryType == null) {
            throw new IllegalArgumentException("字典类型不存在");
        }
    }

    private void validateItemCode(String itemCode) {
        if (!CodeValidator.isValid(itemCode)) {
            throw new IllegalArgumentException(CodeValidator.ERROR_MESSAGE);
        }
    }

    private String normalizeItemValue(String itemValue, String itemCode) {
        if (itemValue == null || itemValue.trim().isEmpty()) {
            return itemCode;
        }
        return itemValue.trim();
    }
}
