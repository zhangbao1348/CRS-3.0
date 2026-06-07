package com.crs.service.inventory;

import com.crs.entity.BookingControl;
import com.crs.entity.InventoryQuota;
import com.crs.entity.Overbooking;
import com.crs.entity.PmsInventory;
import com.crs.entity.RoomStatusRecord;
import com.crs.repository.BookingControlRepository;
import com.crs.repository.InventoryQuotaRepository;
import com.crs.repository.OverbookingRepository;
import com.crs.repository.PmsInventoryRepository;
import com.crs.repository.RoomStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * InventoryDeductionServiceImpl 服务实现类 (Service Implementation)
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【InventoryDeductionServiceImpl】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/11-库存管理.md</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 InventoryDeductionServiceImpl 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
@Service
public class InventoryDeductionServiceImpl implements InventoryDeductionService {

    private static final Logger log = LoggerFactory.getLogger(InventoryDeductionServiceImpl.class);

    private static final String[][] ROOM_STATUS_DIMENSIONS = {
            {"hotel", ""},
            {"room_type", null},
            {"rate", null},
            {"rate_category", null},
            {"channel", null},
            {"channel_room_type", null},
            {"market", null}
    };

    private static final String[][] BOOKING_CONTROL_DIMENSIONS = {
            {"hotel", ""},
            {"rate", null},
            {"channel", null},
            {"rate_category", null},
            {"market", null}
    };

    private static final String[][] QUOTA_DIMENSIONS = {
            {"rate", null},
            {"channel", null},
            {"market", null},
            {"channel_room_type", null},
            {"rate_category", null}
    };

    private final PmsInventoryRepository pmsInventoryRepository;
    private final InventoryQuotaRepository inventoryQuotaRepository;
    private final OverbookingRepository overbookingRepository;
    private final RoomStatusRepository roomStatusRepository;
    private final BookingControlRepository bookingControlRepository;
    private final JdbcTemplate jdbcTemplate;

    public InventoryDeductionServiceImpl(
            PmsInventoryRepository pmsInventoryRepository,
            InventoryQuotaRepository inventoryQuotaRepository,
            OverbookingRepository overbookingRepository,
            RoomStatusRepository roomStatusRepository,
            BookingControlRepository bookingControlRepository,
            JdbcTemplate jdbcTemplate) {
        this.pmsInventoryRepository = pmsInventoryRepository;
        this.inventoryQuotaRepository = inventoryQuotaRepository;
        this.overbookingRepository = overbookingRepository;
        this.roomStatusRepository = roomStatusRepository;
        this.bookingControlRepository = bookingControlRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AvailabilityResult checkAvailability(AvailabilityContext ctx) {
        List<AvailabilityResult.DailyAvailability> dailyDetails = new ArrayList<>();
        int overallMin = Integer.MAX_VALUE;
        String firstRejectReason = null;

        for (LocalDate date = ctx.getCheckInDate(); date.isBefore(ctx.getCheckOutDate()); date = date.plusDays(1)) {
            AvailabilityResult.DailyAvailability daily = checkDailyAvailability(ctx, date);
            dailyDetails.add(daily);

            if (daily.getMinAvailable() != null && daily.getMinAvailable() < overallMin) {
                overallMin = daily.getMinAvailable();
                if (daily.getRejectReason() != null && firstRejectReason == null) {
                    firstRejectReason = daily.getRejectReason();
                }
            }
        }

        if (overallMin == Integer.MAX_VALUE) {
            return AvailabilityResult.unavailable("无法获取库存数据");
        }

        AvailabilityResult result;
        if (overallMin >= ctx.getRequestedRooms()) {
            result = AvailabilityResult.available(overallMin, dailyDetails);
        } else {
            String reason = firstRejectReason != null ? firstRejectReason
                    : "库存不足，可售" + overallMin + "间，需" + ctx.getRequestedRooms() + "间";
            result = AvailabilityResult.unavailable(reason);
        }

        // [TRACE] 记录可用性检查决策快照
        Map<String, Object> availabilitySnapshot = new java.util.LinkedHashMap<>();
        availabilitySnapshot.put("requestedRooms", ctx.getRequestedRooms());
        availabilitySnapshot.put("checkInDate", ctx.getCheckInDate().toString());
        availabilitySnapshot.put("checkOutDate", ctx.getCheckOutDate().toString());
        availabilitySnapshot.put("roomTypeCode", ctx.getRoomTypeCode());
        availabilitySnapshot.put("rateCode", ctx.getRateCode());
        availabilitySnapshot.put("channelCode", ctx.getChannelCode());
        availabilitySnapshot.put("overallMin", overallMin);
        availabilitySnapshot.put("firstRejectReason", firstRejectReason);
        availabilitySnapshot.put("dailyDetails", dailyDetails);
        com.crs.util.TraceContext.recordDecision("availabilityCheck", availabilitySnapshot);

        return result;
    }

    @Override
    public List<AvailabilityResult.DailyAvailability> checkDailyRangeAvailability(AvailabilityContext ctx) {
        List<AvailabilityResult.DailyAvailability> dailyDetails = new ArrayList<>();
        for (LocalDate date = ctx.getCheckInDate(); date.isBefore(ctx.getCheckOutDate()); date = date.plusDays(1)) {
            AvailabilityResult.DailyAvailability daily = checkDailyAvailability(ctx, date);
            dailyDetails.add(daily);
        }
        return dailyDetails;
    }

    private AvailabilityResult.DailyAvailability checkDailyAvailability(AvailabilityContext ctx, LocalDate date) {
        AvailabilityResult.DailyAvailability daily = new AvailabilityResult.DailyAvailability();
        daily.setDate(date);

        java.sql.Date sqlDate = java.sql.Date.valueOf(date);

        String roomStatusReject = checkRoomStatus(ctx, sqlDate);
        if (roomStatusReject != null) {
            daily.setMinAvailable(0);
            daily.setRejectReason(roomStatusReject);
            return daily;
        }

        String bookingControlReject = checkBookingControls(ctx, sqlDate);
        if (bookingControlReject != null) {
            daily.setMinAvailable(0);
            daily.setRejectReason(bookingControlReject);
            return daily;
        }

        int roomTypeAvailable = getRoomTypeAvailable(ctx, sqlDate, daily);
        int hotelAvailable = getHotelAvailable(ctx, sqlDate, daily);

        int pmsFinal = Math.min(roomTypeAvailable, hotelAvailable);
        daily.setPmsAvailable(pmsFinal);

        int minAvailable = pmsFinal;

        for (String[] dim : QUOTA_DIMENSIONS) {
            String dimType = dim[0];
            String dimCode = resolveDimensionCode(dimType, ctx);
            if (dimCode == null) continue;

            Integer remaining = getQuotaRemaining(ctx.getTenantId(), ctx.getHotelCode(), dimType, dimCode, sqlDate);
            if (remaining != null) {
                setQuotaRemainingField(daily, dimType, remaining);
                minAvailable = Math.min(minAvailable, remaining);
            }
        }

        daily.setMinAvailable(minAvailable);
        return daily;
    }

    private String checkRoomStatus(AvailabilityContext ctx, java.sql.Date date) {
        for (String[] dim : ROOM_STATUS_DIMENSIONS) {
            String dimType = dim[0];
            String dimCode;
            if ("hotel".equals(dimType)) {
                dimCode = "";
            } else {
                dimCode = resolveDimensionCode(dimType, ctx);
                if (dimCode == null) continue;
            }

            Optional<RoomStatusRecord> record = roomStatusRepository
                    .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndStatusDate(
                            ctx.getTenantId(), ctx.getHotelCode(), dimType, dimCode, date);

            if (record.isPresent() && !record.get().getIsOpen()) {
                return "房态关闭: " + dimType + "=" + dimCode + " " + date;
            }
        }
        return null;
    }

    private String checkBookingControls(AvailabilityContext ctx, java.sql.Date date) {
        long daysUntilCheckIn = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), date.toLocalDate());

        int maxAdvanceDays = 0;
        int maxMinStay = 0;
        int minMaxStay = Integer.MAX_VALUE;
        boolean hasRules = false;

        for (String[] dim : BOOKING_CONTROL_DIMENSIONS) {
            String dimType = dim[0];
            String dimCode;
            if ("hotel".equals(dimType)) {
                dimCode = "";
            } else {
                dimCode = resolveDimensionCode(dimType, ctx);
                if (dimCode == null) continue;
            }

            Optional<BookingControl> control = bookingControlRepository
                    .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndControlDate(
                            ctx.getTenantId(), ctx.getHotelCode(), dimType, dimCode, date);

            if (control.isPresent()) {
                BookingControl bc = control.get();
                hasRules = true;
                if (bc.getAdvanceBookingDays() != null && bc.getAdvanceBookingDays() > maxAdvanceDays) {
                    maxAdvanceDays = bc.getAdvanceBookingDays();
                }
                if (bc.getMinStay() != null && bc.getMinStay() > maxMinStay) {
                    maxMinStay = bc.getMinStay();
                }
                if (bc.getMaxStay() != null && bc.getMaxStay() < minMaxStay) {
                    minMaxStay = bc.getMaxStay();
                }
            }
        }

        if (!hasRules) return null;

        if (maxAdvanceDays > 0 && daysUntilCheckIn < maxAdvanceDays) {
            return "需提前" + maxAdvanceDays + "天预订";
        }

        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(ctx.getCheckInDate(), ctx.getCheckOutDate());
        if (maxMinStay > 0 && nights < maxMinStay) {
            return "最少需住" + maxMinStay + "晚";
        }
        if (minMaxStay < Integer.MAX_VALUE && nights > minMaxStay) {
            return "最多可住" + minMaxStay + "晚";
        }

        return null;
    }

    private int getRoomTypeAvailable(AvailabilityContext ctx, java.sql.Date date,
                                      AvailabilityResult.DailyAvailability daily) {
        Optional<PmsInventory> pmsOpt = pmsInventoryRepository
                .findByTenantIdAndHotelCodeAndRoomTypeCodeAndInventoryDate(
                        ctx.getTenantId(), ctx.getHotelCode(), ctx.getRoomTypeCode(), date);

        if (pmsOpt.isEmpty()) {
            return 0;
        }

        PmsInventory pms = pmsOpt.get();
        daily.setRoomTypeOverbookCount(pms.getOverbookCount());

        return pms.getAvailableRooms();
    }

    private int getHotelAvailable(AvailabilityContext ctx, java.sql.Date date,
                                   AvailabilityResult.DailyAvailability daily) {
        int hotelOverbook = getOverbookCount(ctx.getTenantId(), ctx.getHotelCode(),
                "hotel", "", date);
        daily.setHotelOverbookCount(hotelOverbook);

        List<PmsInventory> allRoomTypes = pmsInventoryRepository
                .findByTenantIdAndHotelCodeAndInventoryDateBetween(
                        ctx.getTenantId(), ctx.getHotelCode(), date, date);

        int sumOriginalAvailable = 0;
        for (PmsInventory pms : allRoomTypes) {
            sumOriginalAvailable += pms.getAvailableRooms() - pms.getOverbookCount();
        }

        return sumOriginalAvailable + hotelOverbook;
    }

    private int getOverbookCount(Integer tenantId, String hotelCode, String dimensionType,
                                  String dimensionCode, java.sql.Date date) {
        Optional<Overbooking> ob = overbookingRepository
                .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndOverbookDate(
                        tenantId, hotelCode, dimensionType, dimensionCode, date);
        return ob.map(Overbooking::getOverbookCount).orElse(0);
    }

    private Integer getQuotaRemaining(Integer tenantId, String hotelCode, String dimensionType,
                                       String dimensionCode, java.sql.Date date) {
        Optional<InventoryQuota> quota = inventoryQuotaRepository
                .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndQuotaDate(
                        tenantId, hotelCode, dimensionType, dimensionCode, date);

        if (quota.isEmpty() || quota.get().getQuotaLimit() == null) {
            return null;
        }

        return quota.get().getQuotaLimit() - quota.get().getSoldCount();
    }

    private String resolveDimensionCode(String dimensionType, AvailabilityContext ctx) {
        return resolveDimensionCodeInternal(dimensionType, ctx.getRoomTypeCode(), ctx.getRateCode(),
                ctx.getChannelCode(), ctx.getMarketCode(), ctx.getRateCategoryCode());
    }

    private String resolveDimensionCode(String dimensionType, InventoryDeductionContext ctx) {
        return resolveDimensionCodeInternal(dimensionType, ctx.getRoomTypeCode(), ctx.getRateCode(),
                ctx.getChannelCode(), ctx.getMarketCode(), ctx.getRateCategoryCode());
    }

    private String resolveDimensionCode(String dimensionType, InventoryReleaseContext ctx) {
        return resolveDimensionCodeInternal(dimensionType, ctx.getRoomTypeCode(), ctx.getRateCode(),
                ctx.getChannelCode(), ctx.getMarketCode(), ctx.getRateCategoryCode());
    }

    private String resolveDimensionCodeInternal(String dimensionType, String roomTypeCode, String rateCode,
                                                  String channelCode, String marketCode, String rateCategoryCode) {
        return switch (dimensionType) {
            case "room_type" -> roomTypeCode;
            case "rate" -> rateCode;
            case "channel" -> channelCode;
            case "market" -> marketCode;
            case "channel_room_type" -> (channelCode != null && roomTypeCode != null)
                    ? channelCode + ":" + roomTypeCode : null;
            case "rate_category" -> rateCategoryCode;
            default -> null;
        };
    }

    private void setQuotaRemainingField(AvailabilityResult.DailyAvailability daily,
                                         String dimensionType, Integer remaining) {
        switch (dimensionType) {
            case "rate" -> daily.setRateQuotaRemaining(remaining);
            case "channel" -> daily.setChannelQuotaRemaining(remaining);
            case "market" -> daily.setMarketQuotaRemaining(remaining);
            case "channel_room_type" -> daily.setChannelRoomTypeQuotaRemaining(remaining);
            case "rate_category" -> daily.setRateCategoryQuotaRemaining(remaining);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductInventory(InventoryDeductionContext ctx) {
        for (LocalDate date = ctx.getCheckInDate(); date.isBefore(ctx.getCheckOutDate()); date = date.plusDays(1)) {
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);

            deductPmsInventory(ctx, sqlDate);

            for (String[] dim : QUOTA_DIMENSIONS) {
                String dimType = dim[0];
                String dimCode = resolveDimensionCode(dimType, ctx);
                if (dimCode == null) continue;
                deductQuota(ctx.getTenantId(), ctx.getHotelCode(), dimType, dimCode, sqlDate, ctx.getRoomCount());
            }
        }

        // [TRACE] 记录库存扣减决策快照
        Map<String, Object> deductSnapshot = new java.util.LinkedHashMap<>();
        deductSnapshot.put("tenantId", ctx.getTenantId());
        deductSnapshot.put("hotelCode", ctx.getHotelCode());
        deductSnapshot.put("roomTypeCode", ctx.getRoomTypeCode());
        deductSnapshot.put("roomCount", ctx.getRoomCount());
        deductSnapshot.put("checkInDate", ctx.getCheckInDate().toString());
        deductSnapshot.put("checkOutDate", ctx.getCheckOutDate().toString());
        com.crs.util.TraceContext.recordDecision("inventoryDeduct", deductSnapshot);
    }

    private void deductPmsInventory(InventoryDeductionContext ctx, java.sql.Date date) {
        int hotelAvailable = calculateHotelAvailable(ctx.getTenantId(), ctx.getHotelCode(), date);
        if (hotelAvailable < ctx.getRoomCount()) {
            throw new RuntimeException("酒店库存不足，日期：" + date + "，酒店可售" + hotelAvailable + "间，需" + ctx.getRoomCount() + "间");
        }

        int updated = jdbcTemplate.update(
                "UPDATE pms_inventory SET available_rooms = available_rooms - ?, updated_at = NOW() " +
                        "WHERE tenant_id = ? AND hotel_code = ? AND room_type_code = ? AND inventory_date = ? " +
                        "AND available_rooms >= ?",
                ctx.getRoomCount(), ctx.getTenantId(), ctx.getHotelCode(), ctx.getRoomTypeCode(),
                date, ctx.getRoomCount());

        if (updated == 0) {
            throw new RuntimeException("房型库存不足，日期：" + date);
        }
    }

    private int calculateHotelAvailable(Integer tenantId, String hotelCode, java.sql.Date date) {
        Integer hotelOverbook = overbookingRepository
                .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndOverbookDate(
                        tenantId, hotelCode, "hotel", "", date)
                .map(Overbooking::getOverbookCount).orElse(0);

        List<PmsInventory> allRoomTypes = pmsInventoryRepository
                .findByTenantIdAndHotelCodeAndInventoryDateBetween(tenantId, hotelCode, date, date);

        int sumOriginalAvailable = 0;
        for (PmsInventory pms : allRoomTypes) {
            sumOriginalAvailable += pms.getAvailableRooms() - pms.getOverbookCount();
        }
        return sumOriginalAvailable + hotelOverbook;
    }

    private void deductQuota(Integer tenantId, String hotelCode, String dimensionType,
                              String dimensionCode, java.sql.Date date, int roomCount) {
        int updated = jdbcTemplate.update(
                "UPDATE inventory_quota SET sold_count = sold_count + ?, updated_at = NOW() " +
                        "WHERE tenant_id = ? AND hotel_code = ? AND dimension_type = ? AND dimension_code = ? " +
                        "AND quota_date = ? AND quota_limit IS NOT NULL AND (quota_limit - sold_count) >= ?",
                roomCount, tenantId, hotelCode, dimensionType, dimensionCode, date, roomCount);

        if (updated == 0) {
            Optional<InventoryQuota> quota = inventoryQuotaRepository
                    .findByTenantIdAndHotelCodeAndDimensionTypeAndDimensionCodeAndQuotaDate(
                            tenantId, hotelCode, dimensionType, dimensionCode, date);
            if (quota.isPresent() && quota.get().getQuotaLimit() != null) {
                throw new RuntimeException("配额不足: " + dimensionType + "=" + dimensionCode + " 日期：" + date);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseInventory(InventoryReleaseContext ctx) {
        log.info("返还库存: hotelCode={}, roomTypeCode={}, rateCode={}, channelCode={}, checkIn={}, checkOut={}, roomCount={}, reservationCode={}",
                ctx.getHotelCode(), ctx.getRoomTypeCode(), ctx.getRateCode(), ctx.getChannelCode(),
                ctx.getCheckInDate(), ctx.getCheckOutDate(), ctx.getRoomCount(), ctx.getReservationCode());
        for (LocalDate date = ctx.getCheckInDate(); date.isBefore(ctx.getCheckOutDate()); date = date.plusDays(1)) {
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);

            releasePmsInventory(ctx, sqlDate);

            for (String[] dim : QUOTA_DIMENSIONS) {
                String dimType = dim[0];
                String dimCode = resolveDimensionCode(dimType, ctx);
                if (dimCode == null) continue;
                releaseQuota(ctx.getTenantId(), ctx.getHotelCode(), dimType, dimCode, sqlDate, ctx.getRoomCount());
            }
        }
    }

    private void releasePmsInventory(InventoryReleaseContext ctx, java.sql.Date date) {
        int updated = jdbcTemplate.update(
                "UPDATE pms_inventory SET available_rooms = LEAST(available_rooms + ?, physical_rooms + overbook_count), " +
                        "updated_at = NOW() " +
                        "WHERE tenant_id = ? AND hotel_code = ? AND room_type_code = ? AND inventory_date = ?",
                ctx.getRoomCount(), ctx.getTenantId(), ctx.getHotelCode(), ctx.getRoomTypeCode(), date);

        if (updated == 0) {
            log.warn("返还PMS库存时未找到记录: hotelCode={}, roomTypeCode={}, date={}",
                    ctx.getHotelCode(), ctx.getRoomTypeCode(), date);
        }
    }

    private void releaseQuota(Integer tenantId, String hotelCode, String dimensionType,
                               String dimensionCode, java.sql.Date date, int roomCount) {
        int updated = jdbcTemplate.update(
                "UPDATE inventory_quota SET sold_count = GREATEST(sold_count - ?, 0), updated_at = NOW() " +
                        "WHERE tenant_id = ? AND hotel_code = ? AND dimension_type = ? AND dimension_code = ? " +
                        "AND quota_date = ? AND quota_limit IS NOT NULL AND sold_count > 0",
                roomCount, tenantId, hotelCode, dimensionType, dimensionCode, date);

        if (updated == 0) {
            log.debug("返还配额时未找到记录或已售为0: dimensionType={}, dimensionCode={}, date={}",
                    dimensionType, dimensionCode, date);
        }
    }
}
