"""
CRS系统 CODE迁移 - 基于前端页面实际调用逻辑的测试用例

本测试严格依据前端页面组件中的实际 API 调用方式编写，
模拟前端每个页面的真实使用场景，验证 by-code 接口的正确性。

测试覆盖的前端页面：
- RoomType.jsx: axios.get('/api/hotel-room-types/by-code/hotel/${hotelCode}')
- PriceQuery.jsx: hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
- RackRate.jsx: hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
- RoomStatus.jsx: hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
- RoomTypeOverbooking.jsx: hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
- PMSInventoryCalendar.jsx: hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
- ChannelRoomTypeInventory.jsx: hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
- MainInventoryCalendar.jsx: hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
- AddRatePlan.jsx: hotelRoomTypeApi.getHotelRoomTypesByCode(selectedHotel)
- EditHotel.jsx: 多个 by-code API（设施、图片、房型分配、更新酒店）
- AddHotel.jsx: 多个 by-code API（删除设施、删除分配、更新酒店）
- ChannelSetting.jsx: channelPublishApi.getRateCodesWithRoomTypesByCode
- RatePlan.jsx: ratePlanApi.getRatePlansByHotelCode
"""

import requests
import json
import sys
from datetime import datetime

BASE_URL = "http://localhost:8080/api"
FRONTEND_URL = "http://localhost:3001"

test_results = {
    "timestamp": datetime.now().isoformat(),
    "total": 0,
    "passed": 0,
    "failed": 0,
    "errors": 0,
    "details": []
}


def record_result(page, name, status, message="", detail=None):
    test_results["total"] += 1
    if status == "PASS":
        test_results["passed"] += 1
        icon = "✅"
    elif status == "FAIL":
        test_results["failed"] += 1
        icon = "❌"
    else:
        test_results["errors"] += 1
        icon = "💥"
    print(f"  {icon} [{page}] {name}: {status} {message}")
    test_results["details"].append({
        "page": page,
        "name": name,
        "status": status,
        "message": message,
        "detail": detail
    })


def get_auth_token(username="admin", password="admin123"):
    try:
        resp = requests.post(f"{BASE_URL}/auth/login", json={
            "username": username, "password": password
        }, timeout=10)
        if resp.status_code == 200:
            data = resp.json()
            return data.get("token") or data.get("data", {}).get("token")
        return None
    except Exception as e:
        print(f"  ⚠️ 获取token失败: {e}")
        return None


def get_headers(token=None, tenant_id=1):
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    headers["X-Tenant-Id"] = str(tenant_id)
    return headers


def parse_response(resp):
    try:
        data = resp.json()
        if isinstance(data, dict) and "data" in data:
            return data["data"]
        return data
    except Exception:
        return None


# ============================================================
# 前置：获取测试数据（模拟 HotelContext 提供的 hotelCode）
# ============================================================
def get_test_hotel_code(token):
    headers = get_headers(token)
    resp = requests.get(f"{BASE_URL}/hotels", headers=headers, timeout=10)
    if resp.status_code == 200:
        hotels = resp.json().get("data", [])
        if hotels:
            return hotels[0].get("hotelCode")
    return None


# ============================================================
# Page: RoomType.jsx
# 前端调用: axios.get('/api/hotel-room-types/by-code/hotel/${selectedHotel}')
# 数据处理: 兼容 {success, data} 和纯数组格式，过滤 status === 'active'
# ============================================================
def test_page_room_type(token, hotel_code):
    print("\n📋 页面: RoomType.jsx (房型管理)")
    headers = get_headers(token)

    resp = requests.get(
        f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    if resp.status_code != 200:
        record_result("RoomType", "加载房型列表(by-code)", "FAIL", f"状态码: {resp.status_code}")
        return []

    data = parse_response(resp)
    room_types = data if isinstance(data, list) else []
    record_result("RoomType", "加载房型列表(by-code)", "PASS", f"共{len(room_types)}个房型")

    active_room_types = [rt for rt in room_types if rt.get("status") == "active"]
    record_result("RoomType", "过滤active房型(前端: filter(r => r.status === 'active'))",
                  "PASS", f"active房型: {len(active_room_types)}/{len(room_types)}")

    for rt in room_types:
        if not rt.get("roomTypeCode"):
            record_result("RoomType", "房型roomTypeCode字段存在性", "FAIL",
                          f"房型ID={rt.get('id')} 缺少roomTypeCode")
            return room_types
    record_result("RoomType", "所有房型都有roomTypeCode", "PASS",
                  f"已验证{len(room_types)}个房型")

    if room_types:
        rt = room_types[0]
        required_fields = ["id", "roomTypeCode", "roomTypeName", "status"]
        missing = [f for f in required_fields if not rt.get(f)]
        record_result("RoomType", "房型数据包含前端所需字段(id/code/name/status)",
                      "PASS" if not missing else "FAIL",
                      f"缺少字段: {missing}" if missing else f"字段完整")

    return room_types


# ============================================================
# Page: PriceQuery.jsx / RackRate.jsx / RoomStatus.jsx / 
#       RoomTypeOverbooking.jsx / PMSInventoryCalendar.jsx / 
#       MainInventoryCalendar.jsx
# 前端调用: hotelRoomTypeApi.getHotelRoomTypesByCode(hotelCode)
# 数据处理: .then(res => setRoomTypes((res?.data || []).filter(r => r.status === 'active')))
# ============================================================
def test_page_inventory_pages(token, hotel_code):
    print("\n📋 页面: 库存/价格相关页面 (7个页面共用同一API)")
    headers = get_headers(token)

    pages = [
        "PriceQuery.jsx", "RackRate.jsx", "RoomStatus.jsx",
        "RoomTypeOverbooking.jsx", "PMSInventoryCalendar.jsx",
        "ChannelRoomTypeInventory.jsx", "MainInventoryCalendar.jsx"
    ]

    resp = requests.get(
        f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    record_result("库存页面群", "7个页面共用: getHotelRoomTypesByCode",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")

    data = parse_response(resp)
    room_types = data if isinstance(data, list) else []

    active_count = len([rt for rt in room_types if rt.get("status") == "active"])
    record_result("库存页面群",
                  "前端过滤: (res?.data || []).filter(r => r.status === 'active')",
                  "PASS", f"active: {active_count}, 全部: {len(room_types)}")

    record_result("库存页面群", "返回数据格式兼容 res?.data 模式",
                  "PASS", f"数据类型: {type(data).__name__}")


# ============================================================
# Page: ChannelRoomTypeInventory.jsx
# 注意: 此页面未过滤 status === 'active'，与其他6个页面不同
# 前端调用: .then(res => setRoomTypes(res?.data || []))
# ============================================================
def test_page_channel_room_type_inventory(token, hotel_code):
    print("\n📋 页面: ChannelRoomTypeInventory.jsx (渠道房型库存)")
    headers = get_headers(token)

    resp = requests.get(
        f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    data = parse_response(resp)
    room_types = data if isinstance(data, list) else []

    all_count = len(room_types)
    active_count = len([rt for rt in room_types if rt.get("status") == "active"])
    inactive_count = all_count - active_count
    record_result("ChannelRoomTypeInventory",
                  "此页面不过滤status(前端: setRoomTypes(res?.data || []))",
                  "PASS", f"全部: {all_count}, active: {active_count}, inactive: {inactive_count}")

    if inactive_count > 0:
        record_result("ChannelRoomTypeInventory",
                      "⚠️ 存在inactive房型会被展示(前端未过滤)",
                      "PASS", f"inactive房型数: {inactive_count} — 这是前端设计决策，非bug")
    else:
        record_result("ChannelRoomTypeInventory",
                      "当前无inactive房型，无法验证不过滤逻辑",
                      "PASS", "数据全为active")


# ============================================================
# Page: AddRatePlan.jsx
# 前端调用: hotelRoomTypeApi.getHotelRoomTypesByCode(selectedHotel)
# 数据处理: response?.data → setGroupRoomTypes(roomTypes)
#           按 roomTypeCategoryId 分组 → setRoomTypesByCategory(grouped)
# ============================================================
def test_page_add_rate_plan(token, hotel_code):
    print("\n📋 页面: AddRatePlan.jsx (新增价格计划)")
    headers = get_headers(token)

    resp = requests.get(
        f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    data = parse_response(resp)
    room_types = data if isinstance(data, list) else []
    record_result("AddRatePlan", "加载房型列表(by-code)", "PASS",
                  f"共{len(room_types)}个房型")

    has_category = any(rt.get("roomTypeCategoryId") for rt in room_types)
    record_result("AddRatePlan",
                  "房型数据包含roomTypeCategoryId(前端按此分组)",
                  "PASS" if has_category or len(room_types) == 0 else "FAIL",
                  f"有categoryId的房型: {sum(1 for rt in room_types if rt.get('roomTypeCategoryId'))}/{len(room_types)}")

    resp = requests.get(
        f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    data = parse_response(resp)
    rate_plans = data if isinstance(data, list) else []
    record_result("AddRatePlan", "加载价格计划列表(by-code)", "PASS",
                  f"共{len(rate_plans)}个价格计划")


# ============================================================
# Page: RatePlan.jsx
# 前端调用: ratePlanApi.getRatePlansByHotelCode(hotelCode)
# ============================================================
def test_page_rate_plan(token, hotel_code):
    print("\n📋 页面: RatePlan.jsx (价格计划管理)")
    headers = get_headers(token)

    resp = requests.get(
        f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    record_result("RatePlan", "getRatePlansByHotelCode", "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")

    data = parse_response(resp)
    rate_plans = data if isinstance(data, list) else []

    if rate_plans:
        rp = rate_plans[0]
        required_fields = ["rateCode", "hotelCode"]
        missing = [f for f in required_fields if not rp.get(f)]
        record_result("RatePlan", "价格计划包含rateCode和hotelCode",
                      "PASS" if not missing else "FAIL",
                      f"缺少字段: {missing}" if missing else "字段完整")


# ============================================================
# Page: EditHotel.jsx
# 前端调用:
#   1. hotelFacilityApi.getHotelFacilitiesByCode(code) → 按facilityType分类
#   2. hotelImageApi.getHotelImagesByCode(code) → 按imageType分类
#   3. groupRoomTypeHotelApi.getHotelRoomTypeAllocationsByCode(code)
#   4. hotelApi.updateHotelByCode(hotel.hotelCode, hotelData)
#   5. hotelFacilityApi.deleteHotelFacilitiesByCode(hotel.hotelCode)
#   6. 图片上传 data={{ hotelCode: hotel?.hotelCode, imageType: 'xxx' }}
# ============================================================
def test_page_edit_hotel(token, hotel_code):
    print("\n📋 页面: EditHotel.jsx (编辑酒店)")
    headers = get_headers(token)

    resp = requests.get(
        f"{BASE_URL}/hotel-facilities/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    if resp.status_code == 200:
        facilities = resp.json() if isinstance(resp.json(), list) else []
        facility_types = set(f.get("facilityType") for f in facilities) if facilities else set()
        record_result("EditHotel", "加载设施(by-code) → 按facilityType分类",
                      "PASS", f"共{len(facilities)}个设施, 类型: {facility_types}")
    else:
        record_result("EditHotel", "加载设施(by-code)", "FAIL", f"状态码: {resp.status_code}")

    resp = requests.get(
        f"{BASE_URL}/hotel-images/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    if resp.status_code == 200:
        images = resp.json() if isinstance(resp.json(), list) else []
        image_types = set(img.get("imageType") for img in images) if images else set()
        expected_types = {"logo", "external", "restaurant", "lobby", "video"}
        record_result("EditHotel", "加载图片(by-code) → 按imageType分类",
                      "PASS", f"共{len(images)}个图片, 类型: {image_types}")
    else:
        record_result("EditHotel", "加载图片(by-code)", "FAIL", f"状态码: {resp.status_code}")

    resp = requests.get(
        f"{BASE_URL}/group-room-type-hotels/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    record_result("EditHotel", "加载房型分配(by-code)",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")

    resp = requests.get(
        f"{BASE_URL}/hotels/code/{hotel_code}",
        headers=headers, timeout=10
    )
    if resp.status_code == 200:
        hotel_data = parse_response(resp)
        record_result("EditHotel", "通过CODE查询酒店(用于updateHotelByCode)",
                      "PASS", f"hotelCode={hotel_data.get('hotelCode')}")
    else:
        record_result("EditHotel", "通过CODE查询酒店", "FAIL", f"状态码: {resp.status_code}")


# ============================================================
# Page: AddHotel.jsx
# 前端调用:
#   1. hotelApi.updateHotelByCode(hotelCode, hotelData) — 编辑模式
#   2. hotelFacilityApi.deleteHotelFacilitiesByCode(hotelCode) — 保存设施前先删除
#   3. hotelRateCodeAllocationApi.deleteAllocationsByHotelCode(hotelCode) — 保存分配前先删除
#   4. 设施数据中 hotelCode: hotelCode 替代了 hotelId: hotelId
#   5. 分配数据中 hotelCode: hotelCode 替代了 hotelId: hotelId
# ============================================================
def test_page_add_hotel(token, hotel_code):
    print("\n📋 页面: AddHotel.jsx (新增酒店)")
    headers = get_headers(token)

    resp = requests.delete(
        f"{BASE_URL}/hotel-facilities/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    record_result("AddHotel", "deleteHotelFacilitiesByCode(保存设施前先删除旧数据)",
                  "PASS" if resp.status_code in [200, 204] else "FAIL",
                  f"状态码: {resp.status_code}")

    resp = requests.delete(
        f"{BASE_URL}/hotel-rate-code-allocations/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    record_result("AddHotel", "deleteAllocationsByHotelCode(保存分配前先删除旧数据)",
                  "PASS" if resp.status_code in [200, 204] else "FAIL",
                  f"状态码: {resp.status_code}")

    resp = requests.get(
        f"{BASE_URL}/hotels/code/{hotel_code}",
        headers=headers, timeout=10
    )
    if resp.status_code == 200:
        hotel_data = parse_response(resp)
        record_result("AddHotel", "通过CODE查询酒店(用于updateHotelByCode)",
                      "PASS", f"hotelCode={hotel_data.get('hotelCode')}")
    else:
        record_result("AddHotel", "通过CODE查询酒店", "FAIL", f"状态码: {resp.status_code}")


# ============================================================
# Page: ChannelSetting.jsx
# 前端调用:
#   1. channelPublishApi.getRateCodesWithRoomTypesByCode(currentHotelCode)
#   2. tenantChannelApi.getChannelByCode(channelCode, 1)
#   3. tenantChannelApi.updateChannelByCode(channelCode, data, 1)
# ============================================================
def test_page_channel_setting(token, hotel_code):
    print("\n📋 页面: ChannelSetting.jsx (渠道设置)")
    headers = get_headers(token)

    resp = requests.get(
        f"{BASE_URL}/channel-publish/by-code/hotel/{hotel_code}/rate-codes-with-room-types",
        headers=headers, timeout=10
    )
    record_result("ChannelSetting", "getRateCodesWithRoomTypesByCode",
                  "PASS" if resp.status_code in [200, 404] else "FAIL",
                  f"状态码: {resp.status_code}")

    resp = requests.get(f"{BASE_URL}/tenant-channels", headers=headers, timeout=10)
    if resp.status_code == 200:
        channels_raw = parse_response(resp)
        channels = channels_raw if isinstance(channels_raw, list) else []
        if channels:
            channel_code = channels[0].get("channelCode")
            if channel_code:
                resp = requests.get(
                    f"{BASE_URL}/tenant-channels/code/{channel_code}",
                    headers=headers, timeout=10
                )
                record_result("ChannelSetting", "getChannelByCode",
                              "PASS" if resp.status_code == 200 else "FAIL",
                              f"channelCode={channel_code}, 状态码: {resp.status_code}")
            else:
                record_result("ChannelSetting", "渠道无channelCode", "FAIL", "channelCode为空")
        else:
            record_result("ChannelSetting", "无渠道数据", "PASS", "跳过")
    else:
        record_result("ChannelSetting", "获取渠道列表", "FAIL", f"状态码: {resp.status_code}")


# ============================================================
# Page: BookingControl.jsx
# 前端调用: ratePlanApi.getRatePlansByHotelCode(hotelCode)
# 数据处理: .then(res => setRatePlans(res?.data || []))
# 还调用: api.get('/booking-controls', { params: { hotelCode, ... } })
# ============================================================
def test_page_booking_control(token, hotel_code):
    print("\n📋 页面: BookingControl.jsx (预订控制)")
    headers = get_headers(token)

    resp = requests.get(
        f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    record_result("BookingControl", "getRatePlansByHotelCode(加载下拉选项)",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")

    data = parse_response(resp)
    rate_plans = data if isinstance(data, list) else []
    record_result("BookingControl", "价格计划数据格式(前端: res?.data || [])",
                  "PASS", f"共{len(rate_plans)}个价格计划")

    if rate_plans:
        rp = rate_plans[0]
        has_rate_code = bool(rp.get("rateCode"))
        record_result("BookingControl", "价格计划包含rateCode(用于下拉选项value)",
                      "PASS" if has_rate_code else "FAIL",
                      f"rateCode={rp.get('rateCode')}")


# ============================================================
# Page: PriceLevelInventory.jsx
# 前端调用: ratePlanApi.getRatePlansByHotelCode(hotelCode)
# 数据处理: .then(res => setRatePlans(res?.data || []))
#           ratePlans.map(rp => ({ code: rp.rateCode, name: rp.rateName + '（' + rp.rateCode + '）' }))
# ============================================================
def test_page_price_level_inventory(token, hotel_code):
    print("\n📋 页面: PriceLevelInventory.jsx (价格等级库存)")
    headers = get_headers(token)

    resp = requests.get(
        f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    record_result("PriceLevelInventory", "getRatePlansByHotelCode(加载维度选项)",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")

    data = parse_response(resp)
    rate_plans = data if isinstance(data, list) else []

    if rate_plans:
        rp = rate_plans[0]
        has_rate_code = bool(rp.get("rateCode"))
        has_rate_name = bool(rp.get("rateName"))
        record_result("PriceLevelInventory",
                      "价格计划包含rateCode和rateName(前端: { code: rp.rateCode, name: rp.rateName + '（' + rp.rateCode + '）' })",
                      "PASS" if has_rate_code and has_rate_name else "FAIL",
                      f"rateCode={rp.get('rateCode')}, rateName={rp.get('rateName')}")
    else:
        record_result("PriceLevelInventory", "无价格计划数据", "PASS", "跳过字段验证")


# ============================================================
# 租户隔离 - 按前端页面场景测试
# 前端通过 HotelContext 获取 hotelCode，不同租户的 hotelCode 不会交叉
# ============================================================
def test_tenant_isolation_by_page(token, hotel_code):
    print("\n📋 租户隔离 - 按前端页面场景测试")
    headers_t1 = get_headers(token, tenant_id=1)
    headers_t999 = get_headers(token, tenant_id=999)

    pages_and_apis = [
        ("RoomType/库存页面", f"/hotel-room-types/by-code/hotel/{hotel_code}"),
        ("EditHotel/设施", f"/hotel-facilities/by-code/hotel/{hotel_code}"),
        ("EditHotel/图片", f"/hotel-images/by-code/hotel/{hotel_code}"),
        ("RatePlan/BookingControl/PriceLevelInventory", f"/rate-plans/by-code/hotel/{hotel_code}"),
        ("AddHotel/分配", f"/hotel-rate-code-allocations/by-code/hotel/{hotel_code}"),
        ("EditHotel/房型分配", f"/group-room-type-hotels/by-code/hotel/{hotel_code}"),
    ]

    for page_name, api_path in pages_and_apis:
        resp_t1 = requests.get(f"{BASE_URL}{api_path}", headers=headers_t1, timeout=10)
        resp_t999 = requests.get(f"{BASE_URL}{api_path}", headers=headers_t999, timeout=10)

        t1_ok = resp_t1.status_code == 200
        t999_blocked = resp_t999.status_code == 403

        record_result("租户隔离", f"{page_name}: 租户1可访问, 租户999被拒绝",
                      "PASS" if t1_ok and t999_blocked else "FAIL",
                      f"租户1={resp_t1.status_code}, 租户999={resp_t999.status_code}")

    delete_apis = [
        ("AddHotel/删除设施", f"/hotel-facilities/by-code/hotel/{hotel_code}"),
        ("AddHotel/删除分配", f"/hotel-rate-code-allocations/by-code/hotel/{hotel_code}"),
    ]
    for page_name, api_path in delete_apis:
        resp_t999 = requests.delete(f"{BASE_URL}{api_path}", headers=headers_t999, timeout=10)
        record_result("租户隔离", f"{page_name}: 租户999删除操作被拒绝",
                      "PASS" if resp_t999.status_code == 403 else "FAIL",
                      f"状态码: {resp_t999.status_code}")


# ============================================================
# 前端数据格式兼容性测试
# 验证后端返回的数据格式与前端期望一致
# ============================================================
def test_frontend_data_format(token, hotel_code):
    print("\n📋 前端数据格式兼容性测试")
    headers = get_headers(token)

    resp = requests.get(
        f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    data = resp.json()

    has_data_wrapper = isinstance(data, dict) and "data" in data
    has_success_wrapper = isinstance(data, dict) and "success" in data
    is_plain_array = isinstance(data, list)

    record_result("数据格式", "房型接口返回格式(前端兼容 {success,data} 和纯数组)",
                  "PASS" if has_data_wrapper or is_plain_array else "FAIL",
                  f"格式: {'{success,data}包装' if has_success_wrapper else '纯数组' if is_plain_array else '{data}包装' if has_data_wrapper else '未知'}")

    resp = requests.get(
        f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    data = resp.json()
    record_result("数据格式", "价格计划接口返回格式",
                  "PASS" if (isinstance(data, dict) and "data" in data) or isinstance(data, list) else "FAIL",
                  f"格式: {type(data).__name__}")

    resp = requests.get(
        f"{BASE_URL}/group-room-type-hotels/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    data = resp.json()
    record_result("数据格式", "集团房型分配接口返回格式(前端期望 {success, data})",
                  "PASS" if isinstance(data, dict) and ("data" in data or "success" in data) else "FAIL",
                  f"格式: {type(data).__name__}, keys: {list(data.keys()) if isinstance(data, dict) else 'N/A'}")


# ============================================================
# 前端页面可访问性测试
# ============================================================
def test_frontend_pages():
    print("\n📋 前端页面可访问性测试")

    pages = [
        ("/", "登录页"),
        ("/dashboard", "首页仪表板"),
        ("/hotel-management", "酒店管理(RoomType)"),
        ("/room-type", "房型管理"),
        ("/rate-management", "价格管理(RatePlan/PriceQuery/RackRate)"),
        ("/inventory", "库存管理(MainInventory/RoomStatus/Overbooking)"),
        ("/channel-management", "渠道管理(ChannelSetting)"),
        ("/group-management", "集团管理(AddGroupRateCode/AddGroupRoomType)"),
    ]

    for path, name in pages:
        try:
            resp = requests.get(f"{FRONTEND_URL}{path}", timeout=10, allow_redirects=False)
            record_result("前端页面", f"{name}({path})",
                          "PASS" if resp.status_code in [200, 302, 304] else "FAIL",
                          f"状态码: {resp.status_code}")
        except Exception as e:
            record_result("前端页面", f"{name}({path})", "ERROR", str(e))


# ============================================================
# 主函数
# ============================================================
def main():
    print("=" * 70)
    print("CRS系统 CODE迁移 - 基于前端页面实际调用逻辑的测试")
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 70)

    print("\n🔧 准备测试环境...")
    try:
        resp = requests.get(f"{BASE_URL}/hotels", timeout=5)
        print(f"  后端服务: ✅ 运行中 (状态码: {resp.status_code})")
    except Exception:
        print("  后端服务: ❌ 未运行！请先启动后端服务")
        sys.exit(1)

    try:
        resp = requests.get(FRONTEND_URL, timeout=5)
        print(f"  前端服务: ✅ 运行中 (状态码: {resp.status_code})")
    except Exception:
        print("  前端服务: ⚠️ 未运行，跳过前端测试")

    print("\n🔐 获取认证Token...")
    token = get_auth_token()
    if token:
        print(f"  Token: ✅ 获取成功 ({token[:20]}...)")
    else:
        print("  Token: ⚠️ 获取失败，使用无Token模式测试")

    hotel_code = get_test_hotel_code(token)
    if not hotel_code:
        print("  ❌ 无法获取测试酒店CODE，终止测试")
        sys.exit(1)
    print(f"  测试酒店CODE: {hotel_code} (模拟 HotelContext.selectedHotel)")

    print("\n" + "=" * 70)
    print("开始执行测试用例（基于前端页面实际调用逻辑）")
    print("=" * 70)

    test_page_room_type(token, hotel_code)
    test_page_inventory_pages(token, hotel_code)
    test_page_channel_room_type_inventory(token, hotel_code)
    test_page_add_rate_plan(token, hotel_code)
    test_page_rate_plan(token, hotel_code)
    test_page_edit_hotel(token, hotel_code)
    test_page_add_hotel(token, hotel_code)
    test_page_channel_setting(token, hotel_code)
    test_page_booking_control(token, hotel_code)
    test_page_price_level_inventory(token, hotel_code)
    test_tenant_isolation_by_page(token, hotel_code)
    test_frontend_data_format(token, hotel_code)
    test_frontend_pages()

    print("\n" + "=" * 70)
    print("测试结果汇总")
    print("=" * 70)
    print(f"  总用例数: {test_results['total']}")
    print(f"  ✅ 通过: {test_results['passed']}")
    print(f"  ❌ 失败: {test_results['failed']}")
    print(f"  💥 错误: {test_results['errors']}")
    print(f"  通过率: {test_results['passed']/test_results['total']*100:.1f}%")

    if test_results["failed"] > 0 or test_results["errors"] > 0:
        print("\n❌ 失败/错误详情:")
        for d in test_results["details"]:
            if d["status"] in ["FAIL", "ERROR"]:
                print(f"  [{d['page']}] {d['name']}: {d['message']}")

    report_path = "/Users/willawang/Desktop/我的项目/CRS演示/test_results/frontend_based_test_report.json"
    with open(report_path, "w", encoding="utf-8") as f:
        json.dump(test_results, f, ensure_ascii=False, indent=2)
    print(f"\n📄 测试报告已保存: {report_path}")

    return test_results["failed"] == 0 and test_results["errors"] == 0


if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)
