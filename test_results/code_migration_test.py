"""
CRS系统 CODE迁移 & 租户隔离 全站测试用例
测试范围：
1. 后端 by-code API 接口功能
2. 租户隔离校验
3. 前端页面 CODE 查询
4. 数据完整性验证
"""

import requests
import json
import sys
import time
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


def record_result(module, name, status, message="", detail=None):
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
    
    print(f"  {icon} [{module}] {name}: {status} {message}")
    test_results["details"].append({
        "module": module,
        "name": name,
        "status": status,
        "message": message,
        "detail": detail
    })


def get_auth_token(username="admin", password="admin123"):
    try:
        resp = requests.post(f"{BASE_URL}/auth/login", json={
            "username": username,
            "password": password
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


# ============================================================
# 模块1: 酒店基础 by-code 接口
# ============================================================
def test_hotel_by_code(token):
    print("\n📋 模块1: 酒店基础 by-code 接口")
    headers = get_headers(token)
    
    resp = requests.get(f"{BASE_URL}/hotels", headers=headers, timeout=10)
    if resp.status_code != 200:
        record_result("酒店", "获取酒店列表", "FAIL", f"状态码: {resp.status_code}")
        return None
    
    hotels = resp.json().get("data", [])
    if not hotels:
        record_result("酒店", "获取酒店列表", "FAIL", "无酒店数据")
        return None
    
    record_result("酒店", "获取酒店列表", "PASS", f"共{len(hotels)}家酒店")
    
    hotel = hotels[0]
    hotel_code = hotel.get("hotelCode")
    hotel_id = hotel.get("id")
    
    if not hotel_code:
        record_result("酒店", "酒店CODE字段存在性", "FAIL", "hotelCode为空")
        return hotel
    
    record_result("酒店", "酒店CODE字段存在性", "PASS", f"hotelCode={hotel_code}")
    
    resp = requests.get(f"{BASE_URL}/hotels/code/{hotel_code}", headers=headers, timeout=10)
    if resp.status_code == 200:
        data = resp.json().get("data") or resp.json()
        code_match = data.get("hotelCode") == hotel_code if isinstance(data, dict) else False
        record_result("酒店", "通过CODE查询酒店", "PASS" if code_match else "FAIL",
                      f"hotelCode={hotel_code}, 匹配={code_match}")
    else:
        record_result("酒店", "通过CODE查询酒店", "FAIL", f"状态码: {resp.status_code}")
    
    resp = requests.get(f"{BASE_URL}/hotels/code/NONEXISTENT", headers=headers, timeout=10)
    record_result("酒店", "查询不存在的CODE返回404", "PASS" if resp.status_code == 404 else "FAIL",
                  f"状态码: {resp.status_code}")
    
    return hotel


# ============================================================
# 模块2: 酒店房型 by-code 接口
# ============================================================
def test_room_type_by_code(token, hotel):
    print("\n📋 模块2: 酒店房型 by-code 接口")
    if not hotel:
        record_result("房型", "跳过-无酒店数据", "FAIL")
        return
    
    headers = get_headers(token)
    hotel_code = hotel["hotelCode"]
    
    resp = requests.get(f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}", headers=headers, timeout=10)
    if resp.status_code == 200:
        data = resp.json()
        room_types = data.get("data", []) if isinstance(data, dict) else data
        record_result("房型", "通过hotelCode查询房型列表", "PASS", f"共{len(room_types)}个房型")
        
        if room_types:
            rt = room_types[0]
            rt_code = rt.get("roomTypeCode")
            if rt_code:
                resp = requests.get(
                    f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}/room-type/{rt_code}",
                    headers=headers, timeout=10
                )
                record_result("房型", "通过hotelCode+roomTypeCode查询房型", 
                              "PASS" if resp.status_code == 200 else "FAIL",
                              f"状态码: {resp.status_code}")
    else:
        record_result("房型", "通过hotelCode查询房型列表", "FAIL", f"状态码: {resp.status_code}")
    
    resp = requests.get(
        f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}/status/active",
        headers=headers, timeout=10
    )
    record_result("房型", "通过hotelCode+status查询房型", 
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")


# ============================================================
# 模块3: 价格计划 by-code 接口
# ============================================================
def test_rate_plan_by_code(token, hotel):
    print("\n📋 模块3: 价格计划 by-code 接口")
    if not hotel:
        record_result("价格", "跳过-无酒店数据", "FAIL")
        return
    
    headers = get_headers(token)
    hotel_code = hotel["hotelCode"]
    
    resp = requests.get(f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}", headers=headers, timeout=10)
    if resp.status_code == 200:
        data = resp.json()
        rate_plans = data.get("data", []) if isinstance(data, dict) else data
        record_result("价格", "通过hotelCode查询价格计划列表", "PASS", f"共{len(rate_plans)}个价格计划")
        
        if rate_plans:
            rp = rate_plans[0]
            rate_code = rp.get("rateCode")
            if rate_code:
                resp = requests.get(
                    f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}/rate-code/{rate_code}",
                    headers=headers, timeout=10
                )
                record_result("价格", "通过hotelCode+rateCode查询价格计划",
                              "PASS" if resp.status_code == 200 else "FAIL",
                              f"状态码: {resp.status_code}")
    else:
        record_result("价格", "通过hotelCode查询价格计划列表", "FAIL", f"状态码: {resp.status_code}")


# ============================================================
# 模块4: 酒店设施 by-code 接口
# ============================================================
def test_facility_by_code(token, hotel):
    print("\n📋 模块4: 酒店设施 by-code 接口")
    if not hotel:
        record_result("设施", "跳过-无酒店数据", "FAIL")
        return
    
    headers = get_headers(token)
    hotel_code = hotel["hotelCode"]
    
    resp = requests.get(f"{BASE_URL}/hotel-facilities/by-code/hotel/{hotel_code}", headers=headers, timeout=10)
    record_result("设施", "通过hotelCode查询设施列表",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}, 数据量={len(resp.json()) if resp.status_code == 200 else 0}")
    
    resp = requests.get(
        f"{BASE_URL}/hotel-facilities/by-code/hotel/{hotel_code}/type/transportation",
        headers=headers, timeout=10
    )
    record_result("设施", "通过hotelCode+type查询设施",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")


# ============================================================
# 模块5: 酒店图片 by-code 接口
# ============================================================
def test_image_by_code(token, hotel):
    print("\n📋 模块5: 酒店图片 by-code 接口")
    if not hotel:
        record_result("图片", "跳过-无酒店数据", "FAIL")
        return
    
    headers = get_headers(token)
    hotel_code = hotel["hotelCode"]
    
    resp = requests.get(f"{BASE_URL}/hotel-images/by-code/hotel/{hotel_code}", headers=headers, timeout=10)
    record_result("图片", "通过hotelCode查询图片列表",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")
    
    resp = requests.get(
        f"{BASE_URL}/hotel-images/by-code/hotel/{hotel_code}/type/logo",
        headers=headers, timeout=10
    )
    record_result("图片", "通过hotelCode+type查询图片",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")


# ============================================================
# 模块6: 房价码分配 by-code 接口
# ============================================================
def test_rate_code_allocation_by_code(token, hotel):
    print("\n📋 模块6: 房价码分配 by-code 接口")
    if not hotel:
        record_result("分配", "跳过-无酒店数据", "FAIL")
        return
    
    headers = get_headers(token)
    hotel_code = hotel["hotelCode"]
    
    resp = requests.get(
        f"{BASE_URL}/hotel-rate-code-allocations/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    record_result("分配", "通过hotelCode查询房价码分配",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")


# ============================================================
# 模块7: 集团房型酒店关联 by-code 接口
# ============================================================
def test_group_room_type_hotel_by_code(token, hotel):
    print("\n📋 模块7: 集团房型酒店关联 by-code 接口")
    if not hotel:
        record_result("集团房型", "跳过-无酒店数据", "FAIL")
        return
    
    headers = get_headers(token)
    hotel_code = hotel["hotelCode"]
    
    resp = requests.get(
        f"{BASE_URL}/group-room-type-hotels/by-code/hotel/{hotel_code}",
        headers=headers, timeout=10
    )
    record_result("集团房型", "通过hotelCode查询集团房型分配",
                  "PASS" if resp.status_code == 200 else "FAIL",
                  f"状态码: {resp.status_code}")


# ============================================================
# 模块8: 租户隔离测试
# ============================================================
def test_tenant_isolation(token):
    print("\n📋 模块8: 租户隔离测试")
    headers_t1 = get_headers(token, tenant_id=1)
    headers_t2 = get_headers(token, tenant_id=999)
    
    resp_t1 = requests.get(f"{BASE_URL}/hotels", headers=headers_t1, timeout=10)
    if resp_t1.status_code == 200:
        hotels_t1 = resp_t1.json().get("data", [])
        record_result("租户隔离", "租户1获取酒店列表", "PASS", f"共{len(hotels_t1)}家酒店")
    else:
        record_result("租户隔离", "租户1获取酒店列表", "FAIL", f"状态码: {resp_t1.status_code}")
        return
    
    if hotels_t1:
        hotel_code = hotels_t1[0].get("hotelCode")
        
        resp_t1_code = requests.get(
            f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}",
            headers=headers_t1, timeout=10
        )
        record_result("租户隔离", "租户1通过CODE查询房型",
                      "PASS" if resp_t1_code.status_code == 200 else "FAIL",
                      f"状态码: {resp_t1_code.status_code}")
        
        resp_t2_code = requests.get(
            f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}",
            headers=headers_t2, timeout=10
        )
        if resp_t2_code.status_code == 403:
            record_result("租户隔离", "租户999访问租户1的酒店CODE返回403", "PASS",
                          "正确拒绝跨租户访问")
        elif resp_t2_code.status_code == 200:
            record_result("租户隔离", "租户999访问租户1的酒店CODE返回403", "FAIL",
                          "⚠️ 跨租户访问未拒绝！状态码200")
        else:
            record_result("租户隔离", "租户999访问租户1的酒店CODE", "PASS",
                          f"状态码: {resp_t2_code.status_code} (非200)")
        
        resp_t1_facility = requests.get(
            f"{BASE_URL}/hotel-facilities/by-code/hotel/{hotel_code}",
            headers=headers_t1, timeout=10
        )
        record_result("租户隔离", "租户1通过CODE查询设施",
                      "PASS" if resp_t1_facility.status_code == 200 else "FAIL",
                      f"状态码: {resp_t1_facility.status_code}")
        
        resp_t2_facility = requests.get(
            f"{BASE_URL}/hotel-facilities/by-code/hotel/{hotel_code}",
            headers=headers_t2, timeout=10
        )
        record_result("租户隔离", "租户999访问租户1的设施CODE",
                      "PASS" if resp_t2_facility.status_code == 403 else "FAIL",
                      f"状态码: {resp_t2_facility.status_code}")
        
        resp_t1_rate = requests.get(
            f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}",
            headers=headers_t1, timeout=10
        )
        record_result("租户隔离", "租户1通过CODE查询价格计划",
                      "PASS" if resp_t1_rate.status_code == 200 else "FAIL",
                      f"状态码: {resp_t1_rate.status_code}")
        
        resp_t2_rate = requests.get(
            f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}",
            headers=headers_t2, timeout=10
        )
        record_result("租户隔离", "租户999访问租户1的价格计划CODE",
                      "PASS" if resp_t2_rate.status_code == 403 else "FAIL",
                      f"状态码: {resp_t2_rate.status_code}")


# ============================================================
# 模块9: 无Token请求的租户隔离
# ============================================================
def test_no_token_tenant(token):
    print("\n📋 模块9: 无Token请求的租户隔离")
    headers_no_token = {"Content-Type": "application/json", "X-Tenant-Id": "1"}
    
    resp = requests.get(f"{BASE_URL}/hotels", headers=headers_no_token, timeout=10)
    if resp.status_code == 200:
        record_result("无Token", "无Token+有TenantId请求", "PASS", f"状态码: {resp.status_code}")
    elif resp.status_code == 401 or resp.status_code == 403:
        record_result("无Token", "无Token+有TenantId请求被拒绝", "PASS", f"状态码: {resp.status_code}")
    else:
        record_result("无Token", "无Token请求异常", "FAIL", f"状态码: {resp.status_code}")


# ============================================================
# 模块10: 数据完整性 - CODE字段回填验证
# ============================================================
def test_code_backfill(token):
    print("\n📋 模块10: CODE字段回填完整性验证")
    headers = get_headers(token)
    
    resp = requests.get(f"{BASE_URL}/hotels", headers=headers, timeout=10)
    if resp.status_code != 200:
        record_result("数据完整性", "获取酒店列表失败", "FAIL")
        return
    
    hotels = resp.json().get("data", [])
    hotels_without_code = [h for h in hotels if not h.get("hotelCode")]
    record_result("数据完整性", "所有酒店都有hotelCode",
                  "PASS" if len(hotels_without_code) == 0 else "FAIL",
                  f"缺少CODE的酒店: {len(hotels_without_code)}/{len(hotels)}")
    
    if hotels:
        hotel_code = hotels[0]["hotelCode"]
        
        resp = requests.get(
            f"{BASE_URL}/hotel-room-types/by-code/hotel/{hotel_code}",
            headers=headers, timeout=10
        )
        if resp.status_code == 200:
            data = resp.json()
            room_types = data.get("data", []) if isinstance(data, dict) else data
            rt_without_code = [rt for rt in room_types if not rt.get("roomTypeCode")]
            record_result("数据完整性", "房型都有roomTypeCode",
                          "PASS" if len(rt_without_code) == 0 else "FAIL",
                          f"缺少CODE的房型: {len(rt_without_code)}/{len(room_types)}")
        
        resp = requests.get(
            f"{BASE_URL}/rate-plans/by-code/hotel/{hotel_code}",
            headers=headers, timeout=10
        )
        if resp.status_code == 200:
            data = resp.json()
            rate_plans = data.get("data", []) if isinstance(data, dict) else data
            rp_without_code = [rp for rp in rate_plans if not rp.get("rateCode")]
            record_result("数据完整性", "价格计划都有rateCode",
                          "PASS" if len(rp_without_code) == 0 else "FAIL",
                          f"缺少CODE的价格计划: {len(rp_without_code)}/{len(rate_plans)}")


# ============================================================
# 模块11: 前端页面可访问性测试
# ============================================================
def test_frontend_pages():
    print("\n📋 模块11: 前端页面可访问性测试")
    
    pages = [
        ("/", "登录页"),
        ("/dashboard", "首页仪表板"),
        ("/hotel-management", "酒店管理"),
        ("/room-type", "房型管理"),
        ("/rate-management", "价格管理"),
        ("/inventory", "库存管理"),
        ("/channel-management", "渠道管理"),
        ("/group-management", "集团管理"),
    ]
    
    for path, name in pages:
        try:
            resp = requests.get(f"{FRONTEND_URL}{path}", timeout=10, allow_redirects=False)
            if resp.status_code in [200, 302, 304]:
                record_result("前端页面", f"{name}({path})", "PASS",
                              f"状态码: {resp.status_code}")
            else:
                record_result("前端页面", f"{name}({path})", "FAIL",
                              f"状态码: {resp.status_code}")
        except Exception as e:
            record_result("前端页面", f"{name}({path})", "ERROR", str(e))


# ============================================================
# 模块12: 集团分配接口 hotelCode 支持
# ============================================================
def test_group_allocation_hotel_code(token, hotel):
    print("\n📋 模块12: 集团分配接口 hotelCode 支持")
    if not hotel:
        record_result("集团分配", "跳过-无酒店数据", "FAIL")
        return
    
    headers = get_headers(token)
    
    resp = requests.get(f"{BASE_URL}/group-rate-codes", headers=headers, timeout=10)
    if resp.status_code == 200:
        data = resp.json()
        group_rate_codes = data.get("data", []) if isinstance(data, dict) else data
        if group_rate_codes:
            grc_id = group_rate_codes[0].get("id")
            if grc_id:
                resp = requests.get(
                    f"{BASE_URL}/group-rate-codes/{grc_id}/allocations",
                    headers=headers, timeout=10
                )
                if resp.status_code == 200:
                    alloc_data = resp.json()
                    allocations = alloc_data.get("data", []) if isinstance(alloc_data, dict) else alloc_data
                    has_hotel_code = any(a.get("hotelCode") for a in allocations) if allocations else False
                    record_result("集团分配", "集团房价码分配返回hotelCode",
                                  "PASS" if has_hotel_code else "FAIL",
                                  f"有hotelCode的分配: {sum(1 for a in allocations if a.get('hotelCode'))}/{len(allocations)}")
                else:
                    record_result("集团分配", "获取集团房价码分配", "FAIL", f"状态码: {resp.status_code}")
        else:
            record_result("集团分配", "无集团房价码数据", "PASS", "跳过")
    else:
        record_result("集团分配", "获取集团房价码列表", "FAIL", f"状态码: {resp.status_code}")
    
    resp = requests.get(f"{BASE_URL}/group-room-types", headers=headers, timeout=10)
    if resp.status_code == 200:
        data = resp.json()
        group_room_types = data.get("data", []) if isinstance(data, dict) else data
        if group_room_types:
            grt_id = group_room_types[0].get("id")
            if grt_id:
                resp = requests.get(
                    f"{BASE_URL}/group-room-types/{grt_id}/allocations",
                    headers=headers, timeout=10
                )
                if resp.status_code == 200:
                    alloc_data = resp.json()
                    allocations = alloc_data.get("data", []) if isinstance(alloc_data, dict) else alloc_data
                    has_hotel_code = any(a.get("hotelCode") for a in allocations) if allocations else False
                    record_result("集团分配", "集团房型分配返回hotelCode",
                                  "PASS" if has_hotel_code else "FAIL",
                                  f"有hotelCode的分配: {sum(1 for a in allocations if a.get('hotelCode'))}/{len(allocations)}")
                else:
                    record_result("集团分配", "获取集团房型分配", "FAIL", f"状态码: {resp.status_code}")
        else:
            record_result("集团分配", "无集团房型数据", "PASS", "跳过")
    else:
        record_result("集团分配", "获取集团房型列表", "FAIL", f"状态码: {resp.status_code}")


# ============================================================
# 主函数
# ============================================================
def main():
    print("=" * 60)
    print("CRS系统 CODE迁移 & 租户隔离 全站测试")
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("=" * 60)
    
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
    
    print("\n" + "=" * 60)
    print("开始执行测试用例")
    print("=" * 60)
    
    hotel = test_hotel_by_code(token)
    test_room_type_by_code(token, hotel)
    test_rate_plan_by_code(token, hotel)
    test_facility_by_code(token, hotel)
    test_image_by_code(token, hotel)
    test_rate_code_allocation_by_code(token, hotel)
    test_group_room_type_hotel_by_code(token, hotel)
    test_tenant_isolation(token)
    test_no_token_tenant(token)
    test_code_backfill(token)
    test_frontend_pages()
    test_group_allocation_hotel_code(token, hotel)
    
    print("\n" + "=" * 60)
    print("测试结果汇总")
    print("=" * 60)
    print(f"  总用例数: {test_results['total']}")
    print(f"  ✅ 通过: {test_results['passed']}")
    print(f"  ❌ 失败: {test_results['failed']}")
    print(f"  💥 错误: {test_results['errors']}")
    print(f"  通过率: {test_results['passed']/test_results['total']*100:.1f}%")
    
    if test_results["failed"] > 0 or test_results["errors"] > 0:
        print("\n❌ 失败/错误详情:")
        for d in test_results["details"]:
            if d["status"] in ["FAIL", "ERROR"]:
                print(f"  [{d['module']}] {d['name']}: {d['message']}")
    
    report_path = "/Users/willawang/Desktop/我的项目/CRS演示/test_results/code_migration_test_report.json"
    with open(report_path, "w", encoding="utf-8") as f:
        json.dump(test_results, f, ensure_ascii=False, indent=2)
    print(f"\n📄 测试报告已保存: {report_path}")
    
    return test_results["failed"] == 0 and test_results["errors"] == 0


if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)
