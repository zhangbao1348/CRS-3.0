import requests
import json
import time
import subprocess

BASE_URL = "http://localhost:8080/api"
TENANT_A_ID = 101
TENANT_B_ID = 102

def run_mysql_query(query):
    command = f'mysql -u root -p12345678 -D CRS -e "{query}"'
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    return result.stdout

def test_tenant_isolation():
    print(f"--- Starting Isolation Test ---")
    
    # 1. Test Fail-Fast: Request without X-Tenant-Id
    print("\n[Test 1] Fail-Fast: Request without X-Tenant-Id")
    try:
        resp = requests.get(f"{BASE_URL}/hotels")
        print(f"Response Code: {resp.status_code}")
        if resp.status_code in [401, 500]:
            print("SUCCESS: System blocked request with missing tenant context.")
        else:
            print(f"FAILED: System should return 401 or 500 for missing context, got {resp.status_code}")
    except Exception as e:
        print(f"Error: {e}")

    # 2. Setup Data for Tenant A
    print(f"\n[Test 2] Setup Data for Tenant A (ID: {TENANT_A_ID})")
    headers_a = {"X-Tenant-Id": str(TENANT_A_ID), "Content-Type": "application/json"}
    
    hotel_data = {
        "hotelCode": f"H_ISO_A_{int(time.time())}",
        "chineseName": "Isolation Test Hotel A",
        "englishName": "Iso Hotel A",
        "status": "active",
        "city": "TestCity",
        "address": "Test Address 123",
        "phone": "13800000000",
        "province": "TestProvince",
        "starRating": 5,
        "email": "test@test.com",
        "introduction": "Test intro",
        "totalRooms": 100
    }
    resp = requests.post(f"{BASE_URL}/hotels", json=hotel_data, headers=headers_a)
    if resp.status_code == 200:
        hotel_a_code = hotel_data["hotelCode"]
        print(f"Hotel A created: {hotel_a_code}")
    else:
        print(f"FAILED to create Hotel A: {resp.status_code} - {resp.text}")
        return

    # 3. Verify Isolation: Tenant B should not see Tenant A's hotel
    print(f"\n[Test 3] Isolation: Tenant B (ID: {TENANT_B_ID}) check")
    headers_b = {"X-Tenant-Id": str(TENANT_B_ID), "Content-Type": "application/json"}
    resp = requests.get(f"{BASE_URL}/hotels", headers=headers_b)
    hotels = resp.json().get("data", [])
    found = any(h["hotelCode"] == hotel_a_code for h in hotels)
    if not found:
        print("SUCCESS: Tenant B cannot see Tenant A's hotel.")
    else:
        print("FAILED: Data Leakage! Tenant B sees Tenant A's hotel.")

    # 4. IDOR Check: Tenant B tries to access Hotel A by path variable
    print(f"\n[Test 4] IDOR Check: Tenant B accessing Tenant A's hotel by code")
    resp = requests.get(f"{BASE_URL}/hotels/code/{hotel_a_code}", headers=headers_b)
    if resp.status_code == 403 or resp.status_code == 404:
        print(f"SUCCESS: Access denied or Not Found ({resp.status_code}).")
    else:
        print(f"FAILED: IDOR vulnerability found! Tenant B accessed Hotel A. Code: {resp.status_code}")

    # 5. Database Verification
    print(f"\n[Test 5] Database Direct Verification")
    db_result = run_mysql_query(f"SELECT tenant_id FROM hotels WHERE hotel_code = '{hotel_a_code}'")
    print(f"DB Output:\n{db_result}")
    if str(TENANT_A_ID) in db_result:
        print("SUCCESS: Database record has correct tenant_id.")
    else:
        print("FAILED: Database record missing or has wrong tenant_id.")

    # 6. Group Data Sync Test
    print(f"\n[Test 6] Group Rate Plan Sync Test")
    # Create a Rate Type for Tenant A
    rate_type = {"code": "ISO_BAR", "name": "Iso Test BAR", "status": "active"}
    resp = requests.post(f"{BASE_URL}/rate-types", json=rate_type, headers=headers_a)
    
    # Create Group Rate Code
    group_rate = {
        "rateCode": "ISO_G_RATE",
        "rateName": "Iso Group Rate",
        "rateCategory": "ISO_BAR",
        "status": "active",
        "applicableRoomTypes": "[\"ALL\"]"
    }
    resp = requests.post(f"{BASE_URL}/group-rate-codes", json=group_rate, headers=headers_a)
    if resp.status_code == 200:
        group_rate_id = resp.json().get("id")
        print(f"Group Rate Code created with ID: {group_rate_id}")
    else:
        print(f"FAILED Group Rate Code: {resp.text}")
        return

    # Allocate to Hotel A
    allocation = [
        {
            "hotelCode": hotel_a_code,
            "allocated": True,
            "basicInfoEditable": False,
            "priceInfoEditable": True
        }
    ]
    resp = requests.post(f"{BASE_URL}/group-rate-codes/{group_rate_id}/allocate", json=allocation, headers=headers_a)
    if resp.status_code == 200:
        print("SUCCESS: Allocated group rate to hotel.")
    else:
        print(f"FAILED Allocation: {resp.status_code} - {resp.text}")

    # Check if synced in RatePlans
    resp = requests.get(f"{BASE_URL}/rate-plans?hotelCode={hotel_a_code}", headers=headers_a)
    plans = resp.json().get("data", [])
    if any(p["rateCode"] == "ISO_G_RATE" for p in plans):
        print("SUCCESS: Rate plan synced to hotel.")
    else:
        print("FAILED: Rate plan not synced.")

    print("\n--- Isolation Test Completed ---")

if __name__ == "__main__":
    test_tenant_isolation()
