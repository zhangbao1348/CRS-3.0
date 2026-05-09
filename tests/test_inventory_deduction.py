#!/usr/bin/env python3
"""
CRS 订单核心逻辑测试 - 可订检查与下单库存扣减
测试 InventoryDeductionService 的完整功能
"""

import requests
import json
import sys
import subprocess
from datetime import date, timedelta

BASE_URL = "http://localhost:8080/api/open"
HEADERS = {
    "X-Api-Key": "crs_ctrip_key_001",
    "X-Api-Secret": "crs_ctrip_secret_001",
    "Content-Type": "application/json"
}

HOTEL_CODE = "JJSH001"
ROOM_TYPE = "ST1"
RATE_CODE = "BAR"
TODAY = date.today()
TOMORROW = TODAY + timedelta(days=1)
DAY_AFTER = TODAY + timedelta(days=2)

passed = 0
failed = 0
errors = []


def test(name, condition, detail=""):
    global passed, failed
    if condition:
        passed += 1
        print(f"  ✅ {name}")
    else:
        failed += 1
        msg = f"  ❌ {name}" + (f" — {detail}" if detail else "")
        print(msg)
        errors.append(msg)


def check_availability(hotel_code, room_type, rate_code, check_in, check_out, room_count=1, adult_count=1, child_count=0):
    body = {
        "hotelCode": hotel_code,
        "roomTypeCode": room_type,
        "ratePlanCode": rate_code,
        "checkInDate": check_in.strftime("%Y-%m-%d"),
        "checkOutDate": check_out.strftime("%Y-%m-%d"),
        "roomCount": room_count,
        "adultCount": adult_count,
        "childCount": child_count
    }
    return requests.post(f"{BASE_URL}/availability/check", json=body, headers=HEADERS)


def create_reservation(hotel_code, room_type, rate_code, check_in, check_out, room_count=1, adult_count=1, child_count=0, guest_name="测试客人", guest_phone="13800138000"):
    body = {
        "hotelCode": hotel_code,
        "roomTypeCode": room_type,
        "ratePlanCode": rate_code,
        "checkInDate": check_in.strftime("%Y-%m-%d"),
        "checkOutDate": check_out.strftime("%Y-%m-%d"),
        "roomCount": room_count,
        "adultCount": adult_count,
        "childCount": child_count,
        "guestName": guest_name,
        "guestPhone": guest_phone,
        "contactName": guest_name,
        "contactPhone": guest_phone
    }
    return requests.post(f"{BASE_URL}/reservations", json=body, headers=HEADERS)


def cancel_reservation(reservation_code):
    return requests.post(f"{BASE_URL}/reservations/{reservation_code}/cancel", headers=HEADERS)


def query_db(sql):
    result = subprocess.run(
        ["mysql", "-u", "root", "-p12345678", "crs", "-N", "-e", sql],
        capture_output=True, text=True
    )
    return result.stdout.strip()


def reset_pms_inventory(d=None):
    if d is None:
        d = TOMORROW
    ds = d.strftime("%Y-%m-%d")
    subprocess.run(
        ["mysql", "-u", "root", "-p12345678", "crs", "-e",
         f"UPDATE pms_inventory SET available_rooms=10, overbook_count=0, maintenance_rooms=0 "
         f"WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{ds}';"],
        capture_output=True, text=True
    )


def reset_quota(d=None):
    if d is None:
        d = TOMORROW
    ds = d.strftime("%Y-%m-%d")
    subprocess.run(
        ["mysql", "-u", "root", "-p12345678", "crs", "-e",
         f"UPDATE inventory_quota SET sold_count=0 "
         f"WHERE hotel_code='JJSH001' AND quota_date='{ds}';"],
        capture_output=True, text=True
    )


def get_reason(resp):
    data = resp.json()
    if "data" in data and isinstance(data["data"], dict):
        return data["data"].get("reason", "")
    return ""


# ============================================================
print("=" * 70)
print("CRS 订单核心逻辑测试 - 可订检查与库存扣减")
print("=" * 70)

# 重置测试数据
print("\n🔄 重置测试数据...")
for i in range(8):
    d = (TODAY + timedelta(days=i)).strftime("%Y-%m-%d")
    subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
        f"UPDATE pms_inventory SET available_rooms=10, overbook_count=0, maintenance_rooms=0 "
        f"WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{d}';"],
        capture_output=True, text=True)
    subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
        f"UPDATE inventory_quota SET sold_count=0 "
        f"WHERE hotel_code='JJSH001' AND quota_date='{d}';"],
        capture_output=True, text=True)
    subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
        f"INSERT INTO inventory_quota (tenant_id, hotel_code, dimension_type, dimension_code, quota_date, quota_limit, sold_count) "
        f"VALUES (1, 'JJSH001', 'rate', 'BAR', '{d}', 10, 0) "
        f"ON DUPLICATE KEY UPDATE sold_count=0, quota_limit=10;"],
        capture_output=True, text=True)
    subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
        f"DELETE FROM room_status WHERE hotel_code='JJSH001' AND status_date='{d}';"],
        capture_output=True, text=True)
# 删除今天之前创建的测试订单
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    "DELETE FROM reservation WHERE created_at >= CURDATE();"],
    capture_output=True, text=True)

# ============================================================
print("\n📋 第一部分：可订检查基础功能")
print("-" * 50)

# TC-01: 正常可订检查
print("\n[TC-01] 正常可订检查 - 库存充足场景")
resp = check_availability(HOTEL_CODE, ROOM_TYPE, RATE_CODE, TOMORROW, DAY_AFTER)
data = resp.json()
test("HTTP 200", resp.status_code == 200, f"状态码: {resp.status_code}")
if resp.status_code == 200:
    avail_data = data.get("data", {})
    test("可售状态为true", avail_data.get("available") == True)
    test("可售数量>0", (avail_data.get("availableRooms") or 0) > 0)
else:
    test("可售状态为true", False, f"响应: {json.dumps(data, ensure_ascii=False)[:200]}")

# TC-02: 不存在的酒店
print("\n[TC-02] 可订检查 - 不存在的酒店")
resp = check_availability("NOTEXIST", ROOM_TYPE, RATE_CODE, TOMORROW, DAY_AFTER)
test("HTTP 404或409", resp.status_code in [404, 409], f"状态码: {resp.status_code}")

# TC-03: 不存在的房型
print("\n[TC-03] 可订检查 - 不存在的房型")
resp = check_availability(HOTEL_CODE, "NOTEXIST", RATE_CODE, TOMORROW, DAY_AFTER)
test("HTTP 404或409", resp.status_code in [404, 409], f"状态码: {resp.status_code}")

# TC-04: 不适用的房价码
print("\n[TC-04] 可订检查 - 房型不适用该房价码")
resp = check_availability(HOTEL_CODE, ROOM_TYPE, "NOTEXIST", TOMORROW, DAY_AFTER)
test("HTTP 404或409", resp.status_code in [404, 409], f"状态码: {resp.status_code}")

# TC-05: 超出最大入住人数
print("\n[TC-05] 可订检查 - 超出最大入住人数")
resp = check_availability(HOTEL_CODE, ROOM_TYPE, RATE_CODE, TOMORROW, DAY_AFTER, adult_count=10)
test("HTTP 409", resp.status_code == 409, f"状态码: {resp.status_code}")
if resp.status_code == 409:
    reason = get_reason(resp)
    test("错误原因为EXCEED_MAX_OCCUPANCY", "OCCUPANCY" in reason, f"实际reason: {reason}")

# ============================================================
print("\n📋 第二部分：库存扣减与配额检查")
print("-" * 50)

# TC-06: 创建订单 - 正常场景
print("\n[TC-06] 创建订单 - 正常场景（1间1晚）")
resp = create_reservation(HOTEL_CODE, ROOM_TYPE, RATE_CODE, TOMORROW, DAY_AFTER)
data = resp.json()
test("HTTP 200", resp.status_code == 200, f"状态码: {resp.status_code}, 响应: {json.dumps(data, ensure_ascii=False)[:300]}")
reservation_code_1 = None
if resp.status_code == 200:
    res_data = data.get("data", {})
    reservation_code_1 = res_data.get("reservationCode")
    test("订单号存在", reservation_code_1 is not None, f"实际: {reservation_code_1}")
    test("订单状态为confirmed或pending", res_data.get("reservationStatus") in ["confirmed", "pending"])
    test("总价存在且>0", (res_data.get("totalPrice") or 0) > 0)
else:
    test("订单创建成功", False, f"响应: {json.dumps(data, ensure_ascii=False)[:300]}")

# TC-07: 验证PMS库存已扣减
print("\n[TC-07] 验证PMS库存已扣减1间")
avail = query_db(f"SELECT available_rooms FROM pms_inventory WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{TOMORROW.strftime('%Y-%m-%d')}';")
test("PMS available_rooms=9", avail == "9", f"实际: {avail}")

# TC-08: 验证渠道配额已更新
print("\n[TC-08] 验证渠道配额sold_count已增加1")
sold = query_db(f"SELECT sold_count FROM inventory_quota WHERE hotel_code='JJSH001' AND dimension_type='channel' AND dimension_code='CTRIP' AND quota_date='{TOMORROW.strftime('%Y-%m-%d')}';")
test("渠道配额sold_count=1", sold == "1", f"实际: {sold}")

# TC-09: 验证房价码配额已更新
print("\n[TC-09] 验证房价码配额sold_count已增加1")
sold = query_db(f"SELECT sold_count FROM inventory_quota WHERE hotel_code='JJSH001' AND dimension_type='rate' AND dimension_code='BAR' AND quota_date='{TOMORROW.strftime('%Y-%m-%d')}';")
test("房价码配额sold_count=1", sold == "1", f"实际: {sold}")

# ============================================================
print("\n📋 第三部分：取消订单库存返还")
print("-" * 50)

# TC-10: 取消订单
print("\n[TC-10] 取消订单 - 库存应返还")
if reservation_code_1:
    resp = cancel_reservation(reservation_code_1)
    test("取消订单HTTP 200", resp.status_code == 200, f"状态码: {resp.status_code}")
else:
    test("取消订单HTTP 200", False, "无订单号")

# TC-11: 验证PMS库存已返还
print("\n[TC-11] 验证PMS库存已返还1间")
avail = query_db(f"SELECT available_rooms FROM pms_inventory WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{TOMORROW.strftime('%Y-%m-%d')}';")
test("PMS available_rooms=10", avail == "10", f"实际: {avail}")

# TC-12: 验证渠道配额已返还
print("\n[TC-12] 验证渠道配额sold_count已返还")
sold = query_db(f"SELECT sold_count FROM inventory_quota WHERE hotel_code='JJSH001' AND dimension_type='channel' AND dimension_code='CTRIP' AND quota_date='{TOMORROW.strftime('%Y-%m-%d')}';")
test("渠道配额sold_count=0", sold == "0", f"实际: {sold}")

# ============================================================
print("\n📋 第四部分：多间预订与配额边界")
print("-" * 50)

# TC-13: 多间预订
print("\n[TC-13] 多间预订 - 3间1晚")
resp = create_reservation(HOTEL_CODE, ROOM_TYPE, RATE_CODE, TOMORROW, DAY_AFTER, room_count=3, guest_name="多间测试", guest_phone="13900139000")
test("3间预订成功", resp.status_code == 200, f"状态码: {resp.status_code}, 响应: {resp.text[:200]}")
reservation_code_3 = resp.json().get("data", {}).get("reservationCode") if resp.status_code == 200 else None

# TC-14: 验证3间库存已扣减
print("\n[TC-14] 验证PMS库存扣减3间")
avail = query_db(f"SELECT available_rooms FROM pms_inventory WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{TOMORROW.strftime('%Y-%m-%d')}';")
test("PMS available_rooms=7", avail == "7", f"实际: {avail}")

# TC-15: 验证渠道配额sold_count=3
print("\n[TC-15] 验证渠道配额sold_count=3")
sold = query_db(f"SELECT sold_count FROM inventory_quota WHERE hotel_code='JJSH001' AND dimension_type='channel' AND dimension_code='CTRIP' AND quota_date='{TOMORROW.strftime('%Y-%m-%d')}';")
test("渠道配额sold_count=3", sold == "3", f"实际: {sold}")

# TC-16: 超额预订 - 渠道配额上限10，已售3，请求8间应失败
print("\n[TC-16] 超额预订 - 渠道配额不足（上限10，已售3，请求8间）")
resp = create_reservation(HOTEL_CODE, ROOM_TYPE, RATE_CODE, TOMORROW, DAY_AFTER, room_count=8, guest_name="超额测试", guest_phone="13700137000")
test("超额预订被拒绝", resp.status_code == 409, f"状态码: {resp.status_code}")
if resp.status_code == 409:
    reason = get_reason(resp)
    test("错误原因为库存不足", "INSUFFICIENT" in reason or "配额" in resp.text, f"实际reason: {reason}")

# TC-17: 清理 - 取消3间订单
print("\n[TC-17] 清理 - 取消3间订单")
if reservation_code_3:
    resp = cancel_reservation(reservation_code_3)
    test("取消3间订单成功", resp.status_code == 200, f"状态码: {resp.status_code}")

# 验证库存已恢复
avail = query_db(f"SELECT available_rooms FROM pms_inventory WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{TOMORROW.strftime('%Y-%m-%d')}';")
test("PMS库存已恢复为10", avail == "10", f"实际: {avail}")

# ============================================================
print("\n📋 第五部分：多晚连住")
print("-" * 50)

# TC-18: 多晚连住预订
print("\n[TC-18] 多晚连住 - 2间2晚")
check_in_2n = TOMORROW
check_out_2n = TODAY + timedelta(days=3)
resp = create_reservation(HOTEL_CODE, ROOM_TYPE, RATE_CODE, check_in_2n, check_out_2n, room_count=2, guest_name="连住测试", guest_phone="13600136000")
test("2晚连住预订成功", resp.status_code == 200, f"状态码: {resp.status_code}")
reservation_code_2n = resp.json().get("data", {}).get("reservationCode") if resp.status_code == 200 else None

# TC-19: 验证每天的库存都已扣减
print("\n[TC-19] 验证每天的库存都已扣减2间")
for i in range(2):
    d = (check_in_2n + timedelta(days=i)).strftime("%Y-%m-%d")
    avail = query_db(f"SELECT available_rooms FROM pms_inventory WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{d}';")
    test(f"第{i+1}晚({d}) available_rooms=8", avail == "8", f"实际: {avail}")

# TC-20: 取消多晚连住订单
print("\n[TC-20] 取消多晚连住订单 - 每天库存都应返还")
if reservation_code_2n:
    resp = cancel_reservation(reservation_code_2n)
    test("取消2晚连住订单成功", resp.status_code == 200, f"状态码: {resp.status_code}")

    for i in range(2):
        d = (check_in_2n + timedelta(days=i)).strftime("%Y-%m-%d")
        avail = query_db(f"SELECT available_rooms FROM pms_inventory WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{d}';")
        test(f"第{i+1}晚({d}) available_rooms=10", avail == "10", f"实际: {avail}")

# ============================================================
print("\n📋 第六部分：超预订场景")
print("-" * 50)

# TC-21: 超预订 - PMS同步的available_rooms已包含超预订
# 模拟: physical=10, overbook=2, sold=9, maintenance=0 → available_rooms=10+2-9-0=3
print("\n[TC-21] 超预订场景 - PMS同步available_rooms=3（含超预订2间）")
test_date = TODAY + timedelta(days=5)
ds = test_date.strftime("%Y-%m-%d")
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"UPDATE pms_inventory SET available_rooms=3, overbook_count=2, physical_rooms=10 "
    f"WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{ds}';"],
    capture_output=True, text=True)
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"INSERT INTO overbooking (tenant_id, hotel_code, dimension_type, dimension_code, overbook_date, overbook_count) "
    f"VALUES (1, 'JJSH001', 'hotel', '', '{ds}', 3) "
    f"ON DUPLICATE KEY UPDATE overbook_count=3;"],
    capture_output=True, text=True)

resp = check_availability(HOTEL_CODE, ROOM_TYPE, RATE_CODE, test_date, test_date + timedelta(days=1))
if resp.status_code == 200:
    avail_rooms = resp.json().get("data", {}).get("availableRooms", 0)
    # 房型可售数 = available_rooms = 3
    # 酒店可售数 = (3-2) + 酒店超预订3 = 1 + 3 = 4
    # 最终 = min(3, 4) = 3
    test("超预订后可售数=3", avail_rooms == 3, f"实际可售: {avail_rooms}")
else:
    test("超预订后可售数=3", False, f"查询失败: {resp.status_code}")

# TC-22: 超预订 - 请求2间应成功
print("\n[TC-22] 超预订 - 请求2间（可售3间）应成功")
resp = create_reservation(HOTEL_CODE, ROOM_TYPE, RATE_CODE, test_date, test_date + timedelta(days=1), room_count=2, guest_name="超预订测试", guest_phone="13500135000")
test("超预订订单创建成功", resp.status_code == 200, f"状态码: {resp.status_code}, 响应: {resp.text[:200]}")
reservation_code_ob = resp.json().get("data", {}).get("reservationCode") if resp.status_code == 200 else None

# TC-23: 验证超预订后PMS库存
print("\n[TC-23] 验证超预订后PMS库存")
avail = query_db(f"SELECT available_rooms FROM pms_inventory WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{ds}';")
test("超预订后available_rooms=1", avail == "1", f"实际: {avail}")

# TC-24: 清理超预订
print("\n[TC-24] 清理超预订 - 取消订单并恢复库存")
if reservation_code_ob:
    resp = cancel_reservation(reservation_code_ob)
    test("取消超预订订单成功", resp.status_code == 200, f"状态码: {resp.status_code}")

subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"UPDATE pms_inventory SET available_rooms=10, overbook_count=0 "
    f"WHERE hotel_code='JJSH001' AND room_type_code='ST1' AND inventory_date='{ds}';"],
    capture_output=True, text=True)
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"UPDATE overbooking SET overbook_count=0 "
    f"WHERE hotel_code='JJSH001' AND dimension_type='hotel' AND overbook_date='{ds}';"],
    capture_output=True, text=True)

# ============================================================
print("\n📋 第七部分：房态关闭场景")
print("-" * 50)

# TC-25: 关闭房态后应不可订
print("\n[TC-25] 房态关闭 - 设置房型级关房")
test_date_2 = TODAY + timedelta(days=6)
ds2 = test_date_2.strftime("%Y-%m-%d")
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"INSERT INTO room_status (tenant_id, hotel_code, dimension_type, dimension_code, status_date, is_open) "
    f"VALUES (1, 'JJSH001', 'room_type', 'ST1', '{ds2}', 0) "
    f"ON DUPLICATE KEY UPDATE is_open = 0;"],
    capture_output=True, text=True)
resp = check_availability(HOTEL_CODE, ROOM_TYPE, RATE_CODE, test_date_2, test_date_2 + timedelta(days=1))
test("关房后不可订", resp.status_code == 409, f"状态码: {resp.status_code}")
if resp.status_code == 409:
    reason = get_reason(resp)
    test("错误原因为ROOM_CLOSED", "ROOM_CLOSED" in reason, f"实际reason: {reason}")

# 恢复房态
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"DELETE FROM room_status WHERE hotel_code='JJSH001' AND dimension_type='room_type' AND dimension_code='ST1' AND status_date='{ds2}';"],
    capture_output=True, text=True)

# ============================================================
print("\n📋 第八部分：酒店级库存约束")
print("-" * 50)

# TC-26: 酒店级库存约束
print("\n[TC-26] 酒店级库存约束 - 其他房型超卖场景")
test_date_3 = TODAY + timedelta(days=7)
ds3 = test_date_3.strftime("%Y-%m-%d")
# 添加第二个房型ST2
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"INSERT IGNORE INTO pms_inventory (tenant_id, hotel_code, room_type_code, inventory_date, physical_rooms, available_rooms, maintenance_rooms, overbook_count) "
    f"VALUES (1, 'JJSH001', 'ST2', '{ds3}', 5, 5, 0, 0);"],
    capture_output=True, text=True)
# 设置酒店级超预订3间
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"INSERT INTO overbooking (tenant_id, hotel_code, dimension_type, dimension_code, overbook_date, overbook_count) "
    f"VALUES (1, 'JJSH001', 'hotel', '', '{ds3}', 3) "
    f"ON DUPLICATE KEY UPDATE overbook_count=3;"],
    capture_output=True, text=True)
# ST1: available=10, overbook=0 → 原始可售=10
# ST2: available=5, overbook=0 → 原始可售=5
# 酒店可售 = 10 + 5 + 3(酒店超预订) = 18
# ST1可售 = 10, min(10, 18) = 10
resp = check_availability(HOTEL_CODE, ROOM_TYPE, RATE_CODE, test_date_3, test_date_3 + timedelta(days=1), room_count=10)
test("请求10间（酒店可售18间，ST1可售10间）", resp.status_code == 200, f"状态码: {resp.status_code}")

# 模拟ST2超卖: available=-3, overbook=3 → 原始可售 = -3-3 = -6
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"UPDATE pms_inventory SET available_rooms=-3, overbook_count=3 "
    f"WHERE hotel_code='JJSH001' AND room_type_code='ST2' AND inventory_date='{ds3}';"],
    capture_output=True, text=True)
# 酒店可售 = (10-0) + (-3-3) + 3 = 10 + (-6) + 3 = 7
# ST1可售 = 10, min(10, 7) = 7
# 请求10间应失败
resp = check_availability(HOTEL_CODE, ROOM_TYPE, RATE_CODE, test_date_3, test_date_3 + timedelta(days=1), room_count=10)
test("其他房型超卖后请求10间被拒绝", resp.status_code == 409, f"状态码: {resp.status_code}")

# 恢复ST2
subprocess.run(["mysql", "-u", "root", "-p12345678", "crs", "-e",
    f"UPDATE pms_inventory SET available_rooms=5, overbook_count=0 "
    f"WHERE hotel_code='JJSH001' AND room_type_code='ST2' AND inventory_date='{ds3}';"],
    capture_output=True, text=True)

# ============================================================
print("\n📋 第九部分：并发安全验证")
print("-" * 50)

# TC-27: 并发下单 - 同时请求超过库存的订单
print("\n[TC-27] 并发安全 - 渠道配额上限10，同时请求6+6间")
import threading
results = [None, None]

def create_order(idx, room_count):
    try:
        resp = create_reservation(HOTEL_CODE, ROOM_TYPE, RATE_CODE, DAY_AFTER, TODAY + timedelta(days=3),
                                  room_count=room_count, guest_name=f"并发测试{idx}", guest_phone=f"1380013800{idx}")
        results[idx] = (resp.status_code, resp.json())
    except Exception as e:
        results[idx] = (0, str(e))

t1 = threading.Thread(target=create_order, args=(0, 6))
t2 = threading.Thread(target=create_order, args=(1, 6))
t1.start()
t2.start()
t1.join(timeout=30)
t2.join(timeout=30)

success_count = sum(1 for r in results if r and r[0] == 200)
test("并发请求中最多1个成功（配额上限10）", success_count <= 1, f"成功数: {success_count}")

# 清理并发测试的订单
for r in results:
    if r and r[0] == 200:
        rcode = r[1].get("data", {}).get("reservationCode")
        if rcode:
            cancel_reservation(rcode)

# ============================================================
print("\n" + "=" * 70)
print(f"测试结果: ✅ 通过 {passed}  ❌ 失败 {failed}  总计 {passed + failed}")
print("=" * 70)

if errors:
    print("\n失败详情:")
    for e in errors:
        print(e)

sys.exit(0 if failed == 0 else 1)
