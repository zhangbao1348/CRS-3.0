package com.crs.service;

import com.crs.entity.DictionaryType;
import com.crs.repository.DictionaryItemRepository;
import com.crs.repository.DictionaryTypeRepository;
import com.crs.util.CodeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DictionaryTypeService {

    private final DictionaryTypeRepository dictionaryTypeRepository;
    private final DictionaryItemRepository dictionaryItemRepository;

    public DictionaryTypeService(DictionaryTypeRepository dictionaryTypeRepository,
                                 DictionaryItemRepository dictionaryItemRepository) {
        this.dictionaryTypeRepository = dictionaryTypeRepository;
        this.dictionaryItemRepository = dictionaryItemRepository;
    }

    public List<DictionaryType> getDictionaryTypes(Integer tenantId, String keyword) {
        return dictionaryTypeRepository.searchByTenantId(tenantId, keyword);
    }

    public DictionaryType getDictionaryTypeByCode(Integer tenantId, String typeCode) {
        return dictionaryTypeRepository.findByTenantIdAndTypeCode(tenantId, typeCode).orElse(null);
    }

    public DictionaryType createDictionaryType(Integer tenantId, DictionaryType dictionaryType) {
        validateTypeCode(dictionaryType.getTypeCode());
        if (dictionaryTypeRepository.existsByTenantIdAndTypeCode(tenantId, dictionaryType.getTypeCode())) {
            throw new IllegalArgumentException("字典类型编码已存在");
        }

        dictionaryType.setTenantId(tenantId);
        if (dictionaryType.getStatus() == null) {
            dictionaryType.setStatus(DictionaryType.Status.active);
        }
        if (dictionaryType.getBuiltIn() == null) {
            dictionaryType.setBuiltIn(false);
        }
        if (dictionaryType.getSortOrder() == null) {
            dictionaryType.setSortOrder(0);
        }
        return dictionaryTypeRepository.save(dictionaryType);
    }

    public DictionaryType updateDictionaryType(Integer tenantId, Integer id, DictionaryType dictionaryType) {
        DictionaryType existing = dictionaryTypeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("字典类型不存在"));

        existing.setTypeName(dictionaryType.getTypeName());
        existing.setDescription(dictionaryType.getDescription());
        existing.setStatus(dictionaryType.getStatus() == null ? existing.getStatus() : dictionaryType.getStatus());
        existing.setSortOrder(dictionaryType.getSortOrder() == null ? existing.getSortOrder() : dictionaryType.getSortOrder());
        if (dictionaryType.getBuiltIn() != null) {
            existing.setBuiltIn(dictionaryType.getBuiltIn());
        }
        return dictionaryTypeRepository.save(existing);
    }

    public void deleteDictionaryType(Integer tenantId, Integer id) {
        DictionaryType existing = dictionaryTypeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("字典类型不存在"));

        if (Boolean.TRUE.equals(existing.getBuiltIn())) {
            throw new IllegalArgumentException("内置字典类型不允许删除");
        }
        if (dictionaryItemRepository.existsByTenantIdAndTypeCode(tenantId, existing.getTypeCode())) {
            throw new IllegalArgumentException("请先删除或停用该类型下的字典项");
        }
        dictionaryTypeRepository.delete(existing);
    }

    private void validateTypeCode(String typeCode) {
        if (!CodeValidator.isValid(typeCode)) {
            throw new IllegalArgumentException(CodeValidator.ERROR_MESSAGE);
        }
    }
}
