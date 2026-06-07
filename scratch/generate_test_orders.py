import subprocess
import random
import datetime

def run_mysql_cmd(query):
    # 使用 -N 消除表头，-B 保证以 Tab 分隔
    cmd = ["mysql", "-u", "root", "-p12345678", "-N", "-B", "-e", query]
    res = subprocess.run(cmd, capture_output=True, text=True, encoding='utf-8')
    if res.returncode != 0:
        print(f"执行 SQL 失败: {res.stderr}")
        return []
    lines = res.stdout.strip().split("\n")
    return [line.split("\t") for line in lines if line.strip()]

def main():
    print("正在从数据库拉取元数据...")
    
    # 1. 拉取所有酒店
    hotel_rows = run_mysql_cmd("select hotel_code, chinese_name, tenant_id from CRS.hotels")
    hotels = []
    for r in hotel_rows:
        if len(r) >= 3:
            hotels.append({"code": r[0], "name": r[1], "tenant_id": int(r[2])})
    print(f"找到 {len(hotels)} 个酒店。")
    
    # 2. 拉取所有房型
    room_rows = run_mysql_cmd("select hotel_code, room_type_code, room_type_name from CRS.hotel_room_types where status='active'")
    room_types_by_hotel = {}
    for r in room_rows:
        if len(r) >= 3:
            h_code, rt_code, rt_name = r[0], r[1], r[2]
            room_types_by_hotel.setdefault(h_code, []).append((rt_code, rt_name))
            
    # 3. 拉取所有价格计划
    rate_rows = run_mysql_cmd("select hotel_code, rate_code, rate_name from CRS.rate_plans where status='active'")
    rate_plans_by_hotel = {}
    for r in rate_rows:
        if len(r) >= 3:
            h_code, rp_code, rp_name = r[0], r[1], r[2]
            rate_plans_by_hotel.setdefault(h_code, []).append((rp_code, rp_name))
            
    # 4. 拉取渠道列表 (按 tenant_id 归类)
    channel_rows = run_mysql_cmd("select tenant_id, channel_code, channel_name from CRS.tenant_channels")
    channels_by_tenant = {}
    for r in channel_rows:
        if len(r) >= 3:
            t_id, c_code, c_name = int(r[0]), r[1], r[2]
            channels_by_tenant.setdefault(t_id, []).append((c_code, c_name))

    start_date = datetime.date(2025, 5, 1)
    end_date = datetime.date(2026, 8, 31)
    days_range = (end_date - start_date).days

    names = ["张伟", "王芳", "李伟", "王秀英", "李秀英", "张秀英", "刘洋", "张敏", "李敏", "王静", "王丽", "李丽", "张丽"]
    
    sql_filename = "scratch/insert_reservations.sql"
    print(f"开始生成批量插入 SQL 并写入 {sql_filename} ...")
    
    with open(sql_filename, "w", encoding="utf-8") as f:
        # 写入禁用外键检查及加速导入语句
        f.write("SET FOREIGN_KEY_CHECKS=0;\n")
        f.write("SET UNIQUE_CHECKS=0;\n")
        f.write("START TRANSACTION;\n")
        
        batch_size = 2000
        values_list = []
        
        total_inserted = 0
        
        for hotel in hotels:
            h_code = hotel["code"]
            h_name = hotel["name"]
            t_id = hotel["tenant_id"]
            
            # 获取当前酒店可用的房型和房价码
            r_types = room_types_by_hotel.get(h_code, [("ST1", "标准单人间")])
            r_plans = rate_plans_by_hotel.get(h_code, [("BAR", "基础价格计划")])
            # 获取当前租户可用的渠道
            t_channels = channels_by_tenant.get(t_id, [("CTRIP", "携程")])
            
            print(f"  正在为酒店 {h_code} ({h_name}) 生成 10000 个订单...")
            
            for i in range(1, 10001):
                # 随机入住日期
                rand_days = random.randint(0, days_range)
                checkin = start_date + datetime.timedelta(days=rand_days)
                # 随机连住晚数 (1-4晚)
                nights = random.randint(1, 4)
                checkout = checkin + datetime.timedelta(days=nights)
                
                # 随机创建时间 (入住前 0-30 天)
                creation_lead = random.randint(0, 30)
                created_dt = datetime.datetime.combine(checkin - datetime.timedelta(days=creation_lead), 
                                                      datetime.time(random.randint(0,23), random.randint(0,59), random.randint(0,59)))
                # 确保创建日期不在太早或超出现有范围
                if created_dt.date() < datetime.date(2025, 4, 1):
                    created_dt = datetime.datetime.combine(start_date, datetime.time(12, 0, 0))
                
                # 随机房型、房价码、渠道
                rt_code, rt_name = random.choice(r_types)
                rp_code, rp_name = random.choice(r_plans)
                c_code, c_name = random.choice(t_channels)
                
                adult_count = random.randint(1, 2)
                child_count = random.choice([0, 1])
                room_count = 1
                
                # 随机生成一个价格
                unit_price = random.randint(200, 1200)
                total_price = unit_price * nights * room_count
                original_price = total_price
                
                # 订单状态概率分布: 80% completed/confirmed (active), 20% cancelled
                status_rand = random.random()
                if status_rand < 0.2:
                    res_status = "cancelled"
                    order_status = "cancelled"
                elif checkin < datetime.date(2026, 6, 7):
                    res_status = "completed"
                    order_status = "completed"
                else:
                    res_status = "confirmed"
                    order_status = "active"
                    
                pay_status = "paid" if order_status != "cancelled" else "unpaid"
                
                guest = random.choice(names)
                phone = f"138{random.randint(1000, 9999)}{random.randint(1000, 9999)}"
                email = f"guest_{random.randint(1000, 9999)}@test.com"
                
                res_code = f"RES_TST_{h_code}_{i:05d}"
                
                val_str = (
                    f"({adult_count}, '{c_code}', '{c_name}', '{checkin.isoformat()}', '{checkout.isoformat()}', "
                    f"{nights}, {child_count}, '{guest}', '{phone}', '{email}', {original_price:.2f}, "
                    f"'{h_code}', '{h_name}', {t_id}, 'BAR', '{rp_code}', '{rp_name}', {room_count}, "
                    f"'{rt_code}', '{rt_name}', {total_price:.2f}, '{created_dt.strftime('%Y-%m-%d %H:%M:%S')}', "
                    f"'{created_dt.strftime('%Y-%m-%d %H:%M:%S')}', 'CNY', '{pay_status}', '{res_status}', "
                    f"'system', 'none', '{res_code}', '{order_status}')"
                )
                values_list.append(val_str)
                total_inserted += 1
                
                if len(values_list) >= batch_size:
                    write_batch(f, values_list)
                    values_list = []
                    
        if values_list:
            write_batch(f, values_list)
            
        f.write("COMMIT;\n")
        f.write("SET FOREIGN_KEY_CHECKS=1;\n")
        f.write("SET UNIQUE_CHECKS=1;\n")
        
    print(f"数据生成完成！共生成 {total_inserted} 条订单语句写入了 SQL 文件中。")

def write_batch(file_handle, values_list):
    file_handle.write(
        "INSERT INTO CRS.reservation (\n"
        "  adult_count, channel_code, channel_name, check_in_date, check_out_date,\n"
        "  nights, child_count, contact_name, contact_phone, contact_email,\n"
        "  original_price, hotel_code, hotel_name, tenant_id, market_code,\n"
        "  rate_plan_code, rate_plan_name, room_count, room_type_code, room_type_name,\n"
        "  total_price, created_at, updated_at, currency, payment_status,\n"
        "  reservation_status, created_by, guarantee_type, reservation_code, status\n"
        ") VALUES\n" + ",\n".join(values_list) + ";\n"
    )

if __name__ == "__main__":
    main()
