import mysql.connector
from mysql.connector import Error

# 数据库连接信息
config = {
    'host': '8.133.197.147',
    'port': 3306,
    'user': 'root',
    'password': 'waoqi123',
    'database': 'CRS'
}

try:
    # 连接到MySQL数据库
    print("正在连接到MySQL数据库...")
    connection = mysql.connector.connect(**config)
    
    if connection.is_connected():
        db_info = connection.get_server_info()
        print(f"成功连接到MySQL服务器版本: {db_info}")
        cursor = connection.cursor()
        
        # 插入默认集团数据
        print("\n正在插入默认集团数据...")
        group_sql = "INSERT IGNORE INTO groups (group_code, group_name, description) VALUES ('TEST_GROUP', '测试集团', '用于测试的集团数据');"
        cursor.execute(group_sql)
        connection.commit()
        print(f"集团数据插入完成，影响行数: {cursor.rowcount}")
        
        # 插入集团房型测试数据
        print("\n正在插入集团房型测试数据...")
        room_type_sql = """
        INSERT IGNORE INTO group_room_types (group_id, room_type_code, room_type_name, description, status) VALUES 
        (1, 'STD_ROOM', '标准间', '基础标准间，配备基本设施', 'active'),
        (1, 'DELUXE_ROOM', '豪华间', '豪华装修，配备高级设施', 'active'),
        (1, 'SUITE', '套房', '宽敞套房，配备独立客厅', 'active'),
        (1, 'FAMILY_ROOM', '家庭房', '适合家庭入住，配备多张床位', 'active'),
        (1, 'EXECUTIVE_ROOM', '行政房', '行政楼层房间，享受行政礼遇', 'active'),
        (1, 'PRESIDENTIAL_SUITE', '总统套房', '豪华总统套房，顶级配置', 'inactive');
        """
        cursor.execute(room_type_sql)
        connection.commit()
        print(f"集团房型数据插入完成，影响行数: {cursor.rowcount}")
        
        # 查询插入的数据，验证是否成功
        print("\n验证插入的数据...")
        cursor.execute("SELECT * FROM groups WHERE group_code = 'TEST_GROUP';")
        groups = cursor.fetchall()
        print(f"集团数据: {groups}")
        
        cursor.execute("SELECT * FROM group_room_types;")
        room_types = cursor.fetchall()
        print(f"集团房型数据条数: {len(room_types)}")
        for room_type in room_types:
            print(f"房型: {room_type[2]} - {room_type[3]} (状态: {room_type[5]})")
            
except Error as e:
    print(f"数据库操作错误: {e}")
finally:
    # 关闭数据库连接
    if 'connection' in locals() and connection.is_connected():
        cursor.close()
        connection.close()
        print("\n数据库连接已关闭")
