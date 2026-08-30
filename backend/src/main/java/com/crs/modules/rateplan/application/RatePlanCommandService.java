package com.crs.modules.rateplan.application;

import com.crs.entity.RatePlan;
import com.crs.repository.GroupRateCodeRepository;
import com.crs.repository.HotelRepository;
import com.crs.repository.RatePlanRepository;
import com.crs.shared.api.ApiException;
import com.crs.util.CodeValidator;
import com.crs.util.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 价格计划写用例边界。
 *
 * <p>集中处理租户/酒店归属、唯一性和字段白名单，Controller 不再直接持久化
 * 客户端提交的 JPA 实体。</p>
 */
@Service
@Transactional
public class RatePlanCommandService {

    private final RatePlanRepository ratePlanRepository;
    private final GroupRateCodeRepository groupRateCodeRepository;
    private final HotelRepository hotelRepository;

    public RatePlanCommandService(RatePlanRepository ratePlanRepository,
                                  GroupRateCodeRepository groupRateCodeRepository,
                                  HotelRepository hotelRepository) {
        this.ratePlanRepository = ratePlanRepository;
        this.groupRateCodeRepository = groupRateCodeRepository;
        this.hotelRepository = hotelRepository;
    }

    /** 创建始终生成新实体，忽略请求中的主键、租户和审计字段。 */
    public RatePlan create(RatePlan request) {
        Integer tenantId = currentTenantId();
        validateRequiredFields(request);
        validateHotelAccess(tenantId, request.getHotelCode());
        validateUniqueCode(tenantId, request.getHotelCode(), request.getRateCode(), null);
        if (groupRateCodeRepository.findByRateCodeAndGroupId(request.getRateCode(), tenantId) != null) {
            throw ApiException.badRequest("RATE_CODE_CONFLICTS_WITH_GROUP",
                    "此房价码集团已经存在，请更换房价码CODE");
        }

        RatePlan target = new RatePlan();
        copyBusinessFields(request, target);
        target.setTenantId(tenantId);
        return ratePlanRepository.save(target);
    }

    /** 更新租户内现有实体，保留主键、租户和创建时间。 */
    public RatePlan update(Integer id, RatePlan request) {
        Integer tenantId = currentTenantId();
        RatePlan target = ratePlanRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> ApiException.notFound(
                        "RATE_PLAN_NOT_FOUND", "价格计划不存在或无权访问"));
        String hotelCode = isBlank(request.getHotelCode()) ? target.getHotelCode() : request.getHotelCode();
        if (!isBlank(request.getRateCode()) && !target.getRateCode().equals(request.getRateCode())) {
            throw ApiException.badRequest("RATE_CODE_IMMUTABLE", "房价代码保存后不可修改");
        }
        if (!isBlank(request.getRateType()) && !request.getRateType().equals(target.getRateType())) {
            throw ApiException.badRequest("RATE_TYPE_IMMUTABLE", "房价类型保存后不可修改");
        }
        String rateCode = target.getRateCode();
        request.setHotelCode(hotelCode);
        request.setRateCode(rateCode);
        request.setRateType(target.getRateType());
        validateRequiredFields(request);
        validateHotelAccess(tenantId, hotelCode);
        validateUniqueCode(tenantId, hotelCode, rateCode, id);

        copyBusinessFields(request, target);
        return ratePlanRepository.save(target);
    }

    /** 删除当前租户价格计划。 */
    public void delete(Integer id) {
        RatePlan target = findOwned(id);
        ratePlanRepository.delete(target);
    }

    /** 切换启停状态。 */
    public RatePlan changeStatus(Integer id, String status) {
        RatePlan target = findOwned(id);
        target.setStatus(status);
        return ratePlanRepository.save(target);
    }

    private RatePlan findOwned(Integer id) {
        return ratePlanRepository.findByIdAndTenantId(id, currentTenantId())
                .orElseThrow(() -> ApiException.notFound(
                        "RATE_PLAN_NOT_FOUND", "价格计划不存在或无权访问"));
    }

    private void validateRequiredFields(RatePlan request) {
        if (request == null || isBlank(request.getHotelCode())) {
            throw ApiException.badRequest("HOTEL_CODE_REQUIRED", "缺少酒店信息(hotelCode)");
        }
        if (isBlank(request.getRateCode()) || !CodeValidator.isValid(request.getRateCode())) {
            throw ApiException.badRequest("INVALID_RATE_CODE", CodeValidator.ERROR_MESSAGE);
        }
        if (isBlank(request.getRateName())) {
            throw ApiException.badRequest("RATE_NAME_REQUIRED", "价格计划名称不能为空");
        }
    }

    private void validateHotelAccess(Integer tenantId, String hotelCode) {
        if (hotelRepository.findByHotelCodeAndTenantId(hotelCode, tenantId).isEmpty()) {
            throw ApiException.forbidden("HOTEL_ACCESS_DENIED", "酒店不存在或无权访问");
        }
    }

    private void validateUniqueCode(Integer tenantId, String hotelCode, String rateCode, Integer excludeId) {
        ratePlanRepository.findByTenantIdAndHotelCodeAndRateCode(tenantId, hotelCode, rateCode)
                .filter(existing -> excludeId == null || !existing.getId().equals(excludeId))
                .ifPresent(existing -> {
                    throw ApiException.badRequest("RATE_CODE_ALREADY_EXISTS", "价格计划代码在该酒店内已存在");
                });
    }

    private void copyBusinessFields(RatePlan source, RatePlan target) {
        target.setHotelCode(source.getHotelCode());
        target.setSourceGroupRateCode(source.getSourceGroupRateCode());
        target.setRateCode(source.getRateCode());
        target.setRateName(source.getRateName());
        target.setDescription(source.getDescription());
        target.setRateCategory(source.getRateCategory());
        target.setMarketCode(source.getMarketCode());
        target.setSourceCode(source.getSourceCode());
        target.setRateType(source.getRateType());
        target.setParentRateCode(source.getParentRateCode());
        target.setDerivativeLevel(source.getDerivativeLevel());
        target.setDiscount(source.getDiscount());
        target.setRounding(source.getRounding());
        target.setGuaranteeRule(source.getGuaranteeRule());
        target.setCancellationRule(source.getCancellationRule());
        target.setCouponRule(source.getCouponRule());
        target.setPromotionRule(source.getPromotionRule());
        target.setAllowPoints(source.getAllowPoints());
        target.setPointsType(source.getPointsType());
        target.setPointsValue(source.getPointsValue());
        target.setApplicableRoomTypes(source.getApplicableRoomTypes());
        target.setPackages(source.getPackages());
        target.setPersonalMembership(source.getPersonalMembership());
        target.setCompanyMembership(source.getCompanyMembership());
        target.setAdvanceBookingMin(source.getAdvanceBookingMin());
        target.setAdvanceBookingMax(source.getAdvanceBookingMax());
        target.setMinimumStayMin(source.getMinimumStayMin());
        target.setMinimumStayMax(source.getMinimumStayMax());
        target.setBookingStartTime(source.getBookingStartTime());
        target.setBookingEndTime(source.getBookingEndTime());
        target.setCheckinStartTime(source.getCheckinStartTime());
        target.setCheckinEndTime(source.getCheckinEndTime());
        target.setRoomTypeDiffCode(source.getRoomTypeDiffCode());
        target.setPersonDiffCode(source.getPersonDiffCode());
        target.setStatus(isBlank(source.getStatus()) ? "active" : source.getStatus());
    }

    private Integer currentTenantId() {
        Integer tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw ApiException.forbidden("TENANT_CONTEXT_MISSING", "租户上下文丢失");
        }
        return tenantId;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
