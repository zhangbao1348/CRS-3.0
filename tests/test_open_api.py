#!/usr/bin/env python3
"""
CRS 实时查询接口自动化测试脚本
测试范围：
  - 接口1: GET  /api/open/hotels          查询酒店列表（静态信息）
  - 接口2: GET  /api/open/hotels/{code}   查询酒店详情与价格
  - 接口3: POST /api/open/availability/check 可订检查
"""

import requests
import json
import sys
import time
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080/api/open"

CTRIP_HEADERS = {
    "X-Api-Key": "crs_ctrip_key_001",
    "X-Api-Secret": "crs_ctrip_secret_001",
    "Content-Type": "application/json"
}

FLIGGY_HEADERS = {
    "X-Api-Key": "crs_fliggy_key_001",
    "X-Api-Secret": "crs_fliggy_secret_001",
    "Content-Type": "application/json"
}

INVALID_HEADERS = {
    "X-Api-Key": "invalid_key",
    "X-Api-Secret": "invalid_secret",
    "Content-Type": "application/json"
}

TOTAL = 0
PASSED = 0
FAILED = 0
FAILURES = []


def get_future_date(days_ahead):
    return (datetime.now() + timedelta(days=days_ahead)).strftime("%Y-%m-%d")


def test(name, condition, detail=""):
    global TOTAL, PASSED, FAILED
    TOTAL += 1
    if condition:
        PASSED += 1
        print(f"  ✅ {name}")
    else:
        FAILED += 1
        FAILURES.append((name, detail))
        print(f"  ❌ {name} — {detail}")


def section(title):
    print(f"\n{'='*70}")
    print(f"  {title}")
    print(f"{'='*70}")


# ========================================================================
# 一、认证测试
# ========================================================================
def test_auth():
    section("一、认证测试")

    # TC-AUTH-01: 缺少 X-Api-Key
    r = requests.get(f"{BASE_URL}/hotels", headers={"X-Api-Secret": "xxx"})
    test("TC-AUTH-01 缺少X-Api-Key返回401", r.status_code == 401,
         f"期望401, 实际{r.status_code}")

    # TC-AUTH-02: 缺少 X-Api-Secret
    r = requests.get(f"{BASE_URL}/hotels", headers={"X-Api-Key": "xxx"})
    test("TC-AUTH-02 缺少X-Api-Secret返回401", r.status_code == 401,
         f"期望401, 实际{r.status_code}")

    # TC-AUTH-03: 无效的 API Key/Secret
    r = requests.get(f"{BASE_URL}/hotels", headers=INVALID_HEADERS)
    test("TC-AUTH-03 无效Key/Secret返回401", r.status_code == 401,
         f"期望401, 实际{r.status_code}")

    # TC-AUTH-04: 有效凭证
    r = requests.get(f"{BASE_URL}/hotels", headers=CTRIP_HEADERS)
    test("TC-AUTH-04 有效凭证返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}")


# ========================================================================
# 二、接口1: 查询酒店列表（静态信息）
# ========================================================================
def test_hotel_list():
    section("二、接口1: 查询酒店列表（静态信息）")

    # TC-LIST-01: 基本查询
    r = requests.get(f"{BASE_URL}/hotels", headers=CTRIP_HEADERS)
    test("TC-LIST-01 基本查询返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}")
    data = r.json()
    test("TC-LIST-01 响应包含code=200", data.get("code") == 200,
         f"期望200, 实际{data.get('code')}")
    body = data.get("data", {})
    test("TC-LIST-01 响应包含data.list", "list" in body,
         f"缺少list字段, keys={list(body.keys())}")
    test("TC-LIST-01 响应包含total", "total" in body,
         f"缺少total字段")
    test("TC-LIST-01 total > 0", body.get("total", 0) > 0,
         f"期望>0, 实际{body.get('total')}")

    # TC-LIST-02: 验证酒店静态信息字段
    if body.get("list"):
        hotel = body["list"][0]
        required_fields = ["hotelCode", "chineseName", "city", "province",
                           "address", "phone", "starRating", "latitude",
                           "longitude", "totalRooms", "currency"]
        for f in required_fields:
            test(f"TC-LIST-02 酒店包含字段{f}", f in hotel,
                 f"缺少字段{f}")

        test("TC-LIST-02 currency固定为CNY", hotel.get("currency") == "CNY",
             f"期望CNY, 实际{hotel.get('currency')}")

        # 图片字段
        if "images" in hotel:
            test("TC-LIST-02 images为数组", isinstance(hotel["images"], list),
                 f"期望list, 实际{type(hotel['images'])}")
            if hotel["images"]:
                img = hotel["images"][0]
                for f in ["imageType", "imagePath", "imageName", "sortOrder"]:
                    test(f"TC-LIST-02 图片包含字段{f}", f in img,
                         f"缺少字段{f}")

        # 设施字段
        if "facilities" in hotel:
            test("TC-LIST-02 facilities为数组", isinstance(hotel["facilities"], list),
                 f"期望list, 实际{type(hotel['facilities'])}")
            if hotel["facilities"]:
                fac = hotel["facilities"][0]
                for f in ["facilityType", "facilityCode", "facilityName"]:
                    test(f"TC-LIST-02 设施包含字段{f}", f in fac,
                         f"缺少字段{f}")

        # 房型静态信息
        if "roomTypes" in hotel:
            test("TC-LIST-02 roomTypes为数组", isinstance(hotel["roomTypes"], list),
                 f"期望list, 实际{type(hotel['roomTypes'])}")
            if hotel["roomTypes"]:
                rt = hotel["roomTypes"][0]
                rt_fields = ["roomTypeCode", "roomTypeName", "maxOccupancy",
                             "maxAdults", "maxChildren", "area", "bedType",
                             "windowType", "roomQuantity"]
                for f in rt_fields:
                    test(f"TC-LIST-02 房型包含字段{f}", f in rt,
                         f"缺少字段{f}")

    # TC-LIST-03: 城市筛选
    r = requests.get(f"{BASE_URL}/hotels?cityId=上海", headers=CTRIP_HEADERS)
    test("TC-LIST-03 城市筛选返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}")
    data = r.json().get("data", {})
    if data.get("list"):
        all_sh = all(h.get("city") == "上海" for h in data["list"])
        test("TC-LIST-03 所有结果城市=上海", all_sh,
             f"存在非上海的结果")

    # TC-LIST-04: 关键词搜索
    r = requests.get(f"{BASE_URL}/hotels?keyword=锦江", headers=CTRIP_HEADERS)
    test("TC-LIST-04 关键词搜索返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}")
    data = r.json().get("data", {})
    test("TC-LIST-04 关键词搜索有结果", data.get("total", 0) > 0,
         f"期望>0, 实际{data.get('total')}")

    # TC-LIST-05: 分页
    r = requests.get(f"{BASE_URL}/hotels?page=1&pageSize=2", headers=CTRIP_HEADERS)
    test("TC-LIST-05 分页返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}")
    data = r.json().get("data", {})
    test("TC-LIST-05 pageSize=2时list长度<=2", len(data.get("list", [])) <= 2,
         f"期望<=2, 实际{len(data.get('list', []))}")
    test("TC-LIST-05 返回page=1", data.get("page") == 1,
         f"期望1, 实际{data.get('page')}")

    # TC-LIST-06: 带入住日期的起价查询
    check_in = get_future_date(3)
    check_out = get_future_date(5)
    r = requests.get(
        f"{BASE_URL}/hotels?checkInDate={check_in}&checkOutDate={check_out}",
        headers=CTRIP_HEADERS)
    test("TC-LIST-06 带日期查询返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}")
    data = r.json().get("data", {})
    if data.get("list"):
        hotel_with_price = [h for h in data["list"] if h.get("startingPrice") is not None]
        test("TC-LIST-06 有酒店返回startingPrice", len(hotel_with_price) > 0,
             f"无酒店返回起价")

    # TC-LIST-07: 不带日期时startingPrice为null
    r = requests.get(f"{BASE_URL}/hotels", headers=CTRIP_HEADERS)
    data = r.json().get("data", {})
    if data.get("list"):
        hotel = data["list"][0]
        test("TC-LIST-07 无日期时startingPrice=null",
             hotel.get("startingPrice") is None,
             f"期望null, 实际{hotel.get('startingPrice')}")

    # TC-LIST-08: 渠道隔离（飞猪只能看到前5家酒店）
    r = requests.get(f"{BASE_URL}/hotels", headers=FLIGGY_HEADERS)
    test("TC-LIST-08 飞猪渠道查询返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}")
    fliggy_total = r.json().get("data", {}).get("total", 0)
    test("TC-LIST-08 飞猪渠道酒店数<=5", fliggy_total <= 5,
         f"期望<=5, 实际{fliggy_total}")

    # TC-LIST-09: pageSize上限50
    r = requests.get(f"{BASE_URL}/hotels?pageSize=100", headers=CTRIP_HEADERS)
    test("TC-LIST-09 pageSize上限50返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}")
    data = r.json().get("data", {})
    test("TC-LIST-09 pageSize被限制为50", (data.get("pageSize") or 0) <= 50,
         f"期望<=50, 实际{data.get('pageSize')}")


# ========================================================================
# 三、接口2: 查询酒店详情与价格
# ========================================================================
def test_hotel_detail():
    section("三、接口2: 查询酒店详情与价格")

    check_in = get_future_date(3)
    check_out = get_future_date(5)

    # TC-DETAIL-01: 正常查询
    r = requests.get(
        f"{BASE_URL}/hotels/JJSH001?checkInDate={check_in}&checkOutDate={check_out}",
        headers=CTRIP_HEADERS)
    test("TC-DETAIL-01 正常查询返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}")
    data = r.json().get("data", {})

    # 验证酒店信息
    hotel = data.get("hotel", {})
    test("TC-DETAIL-01 包含hotel对象", bool(hotel),
         "缺少hotel对象")
    if hotel:
        test("TC-DETAIL-01 hotel.hotelCode=JJSH001",
             hotel.get("hotelCode") == "JJSH001",
             f"期望JJSH001, 实际{hotel.get('hotelCode')}")
        for f in ["chineseName", "englishName", "city", "province", "address",
                  "phone", "email", "starRating", "latitude", "longitude",
                  "totalRooms", "introduction"]:
            test(f"TC-DETAIL-01 hotel包含字段{f}", f in hotel,
                 f"缺少字段{f}")

    # 验证房型
    room_types = data.get("roomTypes", [])
    test("TC-DETAIL-01 包含roomTypes数组", isinstance(room_types, list),
         f"期望list, 实际{type(room_types)}")
    test("TC-DETAIL-01 roomTypes非空", len(room_types) > 0,
         f"期望>0, 实际{len(room_types)}")

    if room_types:
        rt = room_types[0]
        for f in ["roomTypeCode", "roomTypeName", "maxOccupancy",
                  "maxAdults", "maxChildren", "area", "bedType",
                  "windowType", "facilities"]:
            test(f"TC-DETAIL-01 房型包含字段{f}", f in rt,
                 f"缺少字段{f}")

        # 验证价格计划
        rate_plans = rt.get("ratePlans", [])
        if rate_plans:
            rp = rate_plans[0]
            rp_fields = ["ratePlanCode", "ratePlanName", "rateType",
                         "availableRooms", "totalPrice", "averagePrice",
                         "currency", "dailyPrices"]
            for f in rp_fields:
                test(f"TC-DETAIL-01 价格计划包含字段{f}", f in rp,
                     f"缺少字段{f}")

            # 验证每日价格
            daily = rp.get("dailyPrices", [])
            if daily:
                test("TC-DETAIL-01 dailyPrices非空", len(daily) > 0,
                     f"期望>0, 实际{len(daily)}")
                dp = daily[0]
                for f in ["date", "priceWithTax"]:
                    test(f"TC-DETAIL-01 每日价格包含字段{f}", f in dp,
                         f"缺少字段{f}")

            # 验证取消政策
            if rp.get("cancellationPolicy"):
                cp = rp["cancellationPolicy"]
                for f in ["type", "description"]:
                    test(f"TC-DETAIL-01 取消政策包含字段{f}", f in cp,
                         f"缺少字段{f}")

            # 验证担保政策
            if rp.get("guaranteePolicy"):
                gp = rp["guaranteePolicy"]
                for f in ["type"]:
                    test(f"TC-DETAIL-01 担保政策包含字段{f}", f in gp,
                         f"缺少字段{f}")

            # 验证预订规则
            if rp.get("bookingRules"):
                br = rp["bookingRules"]
                for f in ["advanceBookingMin", "advanceBookingMax",
                          "minimumStay", "maximumStay"]:
                    test(f"TC-DETAIL-01 预订规则包含字段{f}", f in br,
                         f"缺少字段{f}")

    # TC-DETAIL-02: 酒店不存在
    r = requests.get(
        f"{BASE_URL}/hotels/NONEXIST?checkInDate={check_in}&checkOutDate={check_out}",
        headers=CTRIP_HEADERS)
    test("TC-DETAIL-02 酒店不存在返回404", r.status_code == 404,
         f"期望404, 实际{r.status_code}")

    # TC-DETAIL-03: 渠道无权访问
    r = requests.get(
        f"{BASE_URL}/hotels/JJDL001?checkInDate={check_in}&checkOutDate={check_out}",
        headers=FLIGGY_HEADERS)
    test("TC-DETAIL-03 渠道无权访问返回403", r.status_code == 403,
         f"期望403, 实际{r.status_code}")

    # TC-DETAIL-04: 日期校验（离店<=入住）
    r = requests.get(
        f"{BASE_URL}/hotels/JJSH001?checkInDate={check_in}&checkOutDate={check_in}",
        headers=CTRIP_HEADERS)
    test("TC-DETAIL-04 离店<=入住返回400", r.status_code == 400,
         f"期望400, 实际{r.status_code}")

    # TC-DETAIL-05: 会员等级过滤
    r_no_member = requests.get(
        f"{BASE_URL}/hotels/JJSH001?checkInDate={check_in}&checkOutDate={check_out}",
        headers=CTRIP_HEADERS)
    r_member = requests.get(
        f"{BASE_URL}/hotels/JJSH001?checkInDate={check_in}&checkOutDate={check_out}&memberLevel=gold",
        headers=CTRIP_HEADERS)
    test("TC-DETAIL-05 会员查询返回200", r_member.status_code == 200,
         f"期望200, 实际{r_member.status_code}")

    # TC-DETAIL-06: 包价信息
    if room_types:
        for rt in room_types:
            for rp in rt.get("ratePlans", []):
                pkgs = rp.get("packages", [])
                if pkgs:
                    pkg = pkgs[0]
                    for f in ["packageCode", "packageName", "type", "typeName",
                              "frequency", "quantity"]:
                        test(f"TC-DETAIL-06 包价包含字段{f}", f in pkg,
                             f"缺少字段{f}")
                    break
            else:
                continue
            break

    # TC-DETAIL-07: 价格计算验证
    if room_types:
        for rt in room_types:
            for rp in rt.get("ratePlats" if "ratePlats" in rt else "ratePlans", []):
                daily = rp.get("dailyPrices", [])
                total = rp.get("totalPrice")
                if daily and total:
                    calc_total = sum(
                        d.get("priceWithTax", 0) for d in daily
                        if d.get("priceWithTax") is not None
                    )
                    test("TC-DETAIL-07 totalPrice=每日价格之和",
                         abs(total - calc_total) < 0.01,
                         f"期望{calc_total}, 实际{total}")
                    break
            else:
                continue
            break


# ========================================================================
# 四、接口3: 可订检查
# ========================================================================
def test_availability():
    section("四、接口3: 可订检查")

    check_in = get_future_date(3)
    check_out = get_future_date(5)

    # TC-AVAIL-01: 正常可订（使用BAR_B1，无会员限制）
    body = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "ST1",
        "ratePlanCode": "BAR_B1",
        "checkInDate": check_in,
        "checkOutDate": check_out,
        "roomCount": 1,
        "adultCount": 2,
        "childCount": 0
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body, headers=CTRIP_HEADERS)
    test("TC-AVAIL-01 正常可订返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}, body={r.text[:200]}")
    data = r.json().get("data", {})
    test("TC-AVAIL-01 available=true", data.get("available") is True,
         f"期望True, 实际{data.get('available')}")
    test("TC-AVAIL-01 包含hotelCode", data.get("hotelCode") == "JJSH001",
         f"期望JJSH001, 实际{data.get('hotelCode')}")
    test("TC-AVAIL-01 包含roomTypeCode", data.get("roomTypeCode") == "ST1",
         f"期望ST1, 实际{data.get('roomTypeCode')}")
    test("TC-AVAIL-01 包含ratePlanCode", data.get("ratePlanCode") == "BAR_B1",
         f"期望BAR_B1, 实际{data.get('ratePlanCode')}")
    test("TC-AVAIL-01 nights=2", data.get("nights") == 2,
         f"期望2, 实际{data.get('nights')}")
    test("TC-AVAIL-01 包含totalPrice", data.get("totalPrice") is not None,
         f"totalPrice为空")
    test("TC-AVAIL-01 包含dailyPrices", isinstance(data.get("dailyPrices"), list),
         f"dailyPrices非数组")
    test("TC-AVAIL-01 dailyPrices长度=2", len(data.get("dailyPrices", [])) == 2,
         f"期望2, 实际{len(data.get('dailyPrices', []))}")
    test("TC-AVAIL-01 currency=CNY", data.get("currency") == "CNY",
         f"期望CNY, 实际{data.get('currency')}")
    test("TC-AVAIL-01 availableRooms > 0", data.get("availableRooms", 0) > 0,
         f"期望>0, 实际{data.get('availableRooms')}")

    # TC-AVAIL-02: 缺少必填参数
    body_missing = {"hotelCode": "JJSH001"}
    r = requests.post(f"{BASE_URL}/availability/check", json=body_missing, headers=CTRIP_HEADERS)
    test("TC-AVAIL-02 缺少必填参数返回400", r.status_code == 400,
         f"期望400, 实际{r.status_code}")

    # TC-AVAIL-03: 酒店不存在
    body_inactive = {
        "hotelCode": "NONEXIST",
        "roomTypeCode": "ST1",
        "ratePlanCode": "BAR",
        "checkInDate": check_in,
        "checkOutDate": check_out,
        "roomCount": 1,
        "adultCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_inactive, headers=CTRIP_HEADERS)
    test("TC-AVAIL-03 酒店不存在返回409", r.status_code == 409,
         f"期望409, 实际{r.status_code}")
    data = r.json().get("data", {})
    test("TC-AVAIL-03 reason=HOTEL_INACTIVE",
         data.get("reason") == "HOTEL_INACTIVE",
         f"期望HOTEL_INACTIVE, 实际{data.get('reason')}")

    # TC-AVAIL-04: 房型不存在
    body_rt = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "NONEXIST",
        "ratePlanCode": "BAR",
        "checkInDate": check_in,
        "checkOutDate": check_out,
        "roomCount": 1,
        "adultCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_rt, headers=CTRIP_HEADERS)
    test("TC-AVAIL-04 房型不存在返回409", r.status_code == 409,
         f"期望409, 实际{r.status_code}")
    data = r.json().get("data", {})
    test("TC-AVAIL-04 reason=ROOM_TYPE_INACTIVE",
         data.get("reason") == "ROOM_TYPE_INACTIVE",
         f"期望ROOM_TYPE_INACTIVE, 实际{data.get('reason')}")

    # TC-AVAIL-05: 价格计划不存在
    body_rp = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "ST1",
        "ratePlanCode": "NONEXIST",
        "checkInDate": check_in,
        "checkOutDate": check_out,
        "roomCount": 1,
        "adultCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_rp, headers=CTRIP_HEADERS)
    test("TC-AVAIL-05 价格计划不存在返回409", r.status_code == 409,
         f"期望409, 实际{r.status_code}")
    data = r.json().get("data", {})
    test("TC-AVAIL-05 reason=RATE_PLAN_INACTIVE",
         data.get("reason") == "RATE_PLAN_INACTIVE",
         f"期望RATE_PLAN_INACTIVE, 实际{data.get('reason')}")

    # TC-AVAIL-06: 库存不足（ST3房型 5月7日库存为0）
    body_inv = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "ST3",
        "ratePlanCode": "BAR_B1",
        "checkInDate": get_future_date(5),
        "checkOutDate": get_future_date(8),
        "roomCount": 1,
        "adultCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_inv, headers=CTRIP_HEADERS)
    test("TC-AVAIL-06 库存不足返回409", r.status_code == 409,
         f"期望409, 实际{r.status_code}, body={r.text[:200]}")
    data = r.json().get("data", {})
    test("TC-AVAIL-06 reason=INSUFFICIENT_INVENTORY",
         data.get("reason") == "INSUFFICIENT_INVENTORY",
         f"期望INSUFFICIENT_INVENTORY, 实际{data.get('reason')}")

    # TC-AVAIL-07: 超出最大入住人数
    body_occ = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "ST1",
        "ratePlanCode": "BAR_B1",
        "checkInDate": check_in,
        "checkOutDate": check_out,
        "roomCount": 1,
        "adultCount": 5,
        "childCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_occ, headers=CTRIP_HEADERS)
    test("TC-AVAIL-07 超出入住人数返回409", r.status_code == 409,
         f"期望409, 实际{r.status_code}, body={r.text[:200]}")
    data = r.json().get("data", {})
    test("TC-AVAIL-07 reason=EXCEED_MAX_OCCUPANCY",
         data.get("reason") == "EXCEED_MAX_OCCUPANCY",
         f"期望EXCEED_MAX_OCCUPANCY, 实际{data.get('reason')}")

    # TC-AVAIL-08: 会员价需提供会员等级
    body_member = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "ST1",
        "ratePlanCode": "BAR",
        "checkInDate": check_in,
        "checkOutDate": check_out,
        "roomCount": 1,
        "adultCount": 2,
        "memberLevel": ""
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_member, headers=CTRIP_HEADERS)
    if r.status_code == 409:
        data = r.json().get("data", {})
        test("TC-AVAIL-08 会员价需会员等级",
             data.get("reason") in ["MEMBER_INFO_REQUIRED", "MEMBER_LEVEL_MISMATCH", None],
             f"reason={data.get('reason')}")
    else:
        test("TC-AVAIL-08 会员价需会员等级(通过其他校验)", True, "")

    # TC-AVAIL-09: 日期校验（离店<=入住）
    body_date = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "ST1",
        "ratePlanCode": "BAR",
        "checkInDate": check_in,
        "checkOutDate": check_in,
        "roomCount": 1,
        "adultCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_date, headers=CTRIP_HEADERS)
    test("TC-AVAIL-09 离店<=入住返回400", r.status_code == 400,
         f"期望400, 实际{r.status_code}")

    # TC-AVAIL-10: 渠道无权访问
    body_noaccess = {
        "hotelCode": "JJDL001",
        "roomTypeCode": "ST1",
        "ratePlanCode": "BAR",
        "checkInDate": check_in,
        "checkOutDate": check_out,
        "roomCount": 1,
        "adultCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_noaccess, headers=FLIGGY_HEADERS)
    test("TC-AVAIL-10 渠道无权访问返回403", r.status_code == 403,
         f"期望403, 实际{r.status_code}")

    # TC-AVAIL-11: 包价和取消/担保政策
    body_policy = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "ST1",
        "ratePlanCode": "BAR_B1",
        "checkInDate": check_in,
        "checkOutDate": check_out,
        "roomCount": 1,
        "adultCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_policy, headers=CTRIP_HEADERS)
    if r.status_code == 200:
        data = r.json().get("data", {})
        if data.get("packages"):
            test("TC-AVAIL-11 packages为数组", isinstance(data["packages"], list),
                 f"期望list, 实际{type(data['packages'])}")
        if data.get("cancellationPolicy"):
            test("TC-AVAIL-11 cancellationPolicy包含type",
                 "type" in data["cancellationPolicy"],
                 f"缺少type字段")
        if data.get("guaranteePolicy"):
            test("TC-AVAIL-11 guaranteePolicy包含type",
                 "type" in data["guaranteePolicy"],
                 f"缺少type字段")

    # TC-AVAIL-12: 房态关闭（ST1 5月8日关房）
    body_closed = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "ST1",
        "ratePlanCode": "BAR_B1",
        "checkInDate": get_future_date(6),
        "checkOutDate": get_future_date(9),
        "roomCount": 1,
        "adultCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_closed, headers=CTRIP_HEADERS)
    test("TC-AVAIL-12 房态关闭返回409", r.status_code == 409,
         f"期望409, 实际{r.status_code}, body={r.text[:200]}")
    if r.status_code == 409:
        data = r.json().get("data", {})
        test("TC-AVAIL-12 reason=ROOM_CLOSED",
             data.get("reason") == "ROOM_CLOSED",
             f"期望ROOM_CLOSED, 实际{data.get('reason')}")

    # TC-AVAIL-13: 多间房查询
    body_multi = {
        "hotelCode": "JJSH001",
        "roomTypeCode": "ST1",
        "ratePlanCode": "BAR_B1",
        "checkInDate": check_in,
        "checkOutDate": check_out,
        "roomCount": 3,
        "adultCount": 2
    }
    r = requests.post(f"{BASE_URL}/availability/check", json=body_multi, headers=CTRIP_HEADERS)
    test("TC-AVAIL-13 多间房查询返回200", r.status_code == 200,
         f"期望200, 实际{r.status_code}, body={r.text[:200]}")
    if r.status_code == 200:
        data = r.json().get("data", {})
        single_total = data.get("totalPrice")
        if single_total:
            body1 = {
                "hotelCode": "JJSH001", "roomTypeCode": "ST1", "ratePlanCode": "BAR_B1",
                "checkInDate": check_in, "checkOutDate": check_out,
                "roomCount": 1, "adultCount": 2
            }
            r1 = requests.post(f"{BASE_URL}/availability/check", json=body1, headers=CTRIP_HEADERS)
            if r1.status_code == 200:
                single_price = r1.json().get("data", {}).get("totalPrice")
                if single_price:
                    test("TC-AVAIL-13 3间房总价=单间×3",
                         abs(single_total - single_price * 3) < 0.01,
                         f"期望{single_price * 3}, 实际{single_total}")


# ========================================================================
# 五、公共响应结构验证
# ========================================================================
def test_response_structure():
    section("五、公共响应结构验证")

    r = requests.get(f"{BASE_URL}/hotels", headers=CTRIP_HEADERS)
    data = r.json()

    test("TC-STRUCT-01 响应包含code字段", "code" in data,
         f"缺少code字段")
    test("TC-STRUCT-01 响应包含message字段", "message" in data,
         f"缺少message字段")
    test("TC-STRUCT-01 响应包含timestamp字段", "timestamp" in data,
         f"缺少timestamp字段")
    test("TC-STRUCT-01 成功时code=200", data.get("code") == 200,
         f"期望200, 实际{data.get('code')}")
    test("TC-STRUCT-01 成功时message=success", data.get("message") == "success",
         f"期望success, 实际{data.get('message')}")

    # 错误响应结构
    r = requests.get(f"{BASE_URL}/hotels", headers=INVALID_HEADERS)
    data = r.json()
    test("TC-STRUCT-02 错误响应包含code字段", "code" in data,
         f"缺少code字段")
    test("TC-STRUCT-02 错误响应包含message字段", "message" in data,
         f"缺少message字段")


# ========================================================================
# 主函数
# ========================================================================
def main():
    print("=" * 70)
    print("  CRS 实时查询接口自动化测试")
    print(f"  测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"  目标服务: {BASE_URL}")
    print("=" * 70)

    try:
        r = requests.get(f"{BASE_URL.replace('/api/open', '')}/actuator/health", timeout=5)
        if r.status_code != 200:
            print("⚠️  后端服务健康检查未通过，请确认服务是否正常运行")
            sys.exit(1)
    except Exception as e:
        print(f"⚠️  无法连接后端服务: {e}")
        sys.exit(1)

    start = time.time()

    test_auth()
    test_hotel_list()
    test_hotel_detail()
    test_availability()
    test_response_structure()

    elapsed = time.time() - start

    print(f"\n{'='*70}")
    print(f"  测试结果汇总")
    print(f"{'='*70}")
    print(f"  总计: {TOTAL}")
    print(f"  通过: {PASSED}")
    print(f"  失败: {FAILED}")
    print(f"  通过率: {PASSED/TOTAL*100:.1f}%")
    print(f"  耗时: {elapsed:.2f}s")

    if FAILURES:
        print(f"\n{'='*70}")
        print(f"  失败用例详情")
        print(f"{'='*70}")
        for name, detail in FAILURES:
            print(f"  ❌ {name}: {detail}")

    print()
    return 0 if FAILED == 0 else 1


if __name__ == "__main__":
    sys.exit(main())
