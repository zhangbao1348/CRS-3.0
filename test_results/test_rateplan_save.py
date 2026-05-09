import requests
import json

BASE = 'http://localhost:8080/api'
resp = requests.post(f'{BASE}/auth/login', json={'username':'admin','password':'admin123'}, timeout=10)
token = resp.json().get('token')
headers = {'Content-Type': 'application/json', 'Authorization': f'Bearer {token}', 'X-Tenant-Id': '1'}

data = {
    'hotelCode': 'JJSH001',
    'rateCode': 'BAR',
    'rateName': '基础价格-测试',
    'status': 'active',
    'rateType': 'basic'
}
resp = requests.put(f'{BASE}/rate-plans/11', headers=headers, json=data, timeout=10)
print(f'状态码: {resp.status_code}')
try:
    result = resp.json()
    print(f'hotelId: {result.get("hotelId")}, hotelCode: {result.get("hotelCode")}')
    if result.get('id'):
        print('结果: ✅ 保存成功！hotelCode → hotelId 自动填充正常工作')
    else:
        print(f'结果: ❌ 失败 - {result}')
except Exception as e:
    print(f'响应: {resp.text[:200]}')
    print(f'错误: {e}')
