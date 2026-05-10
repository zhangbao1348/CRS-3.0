import os
import glob
import re

# PRD Context Mapping
prd_map = {
    "Group": "08-集团管理.md",
    "Hotel": "09-系统设置.md",
    "RoomType": "12-房型管理.md",
    "Rate": "10-价格计划管理.md",
    "Price": "10-价格计划管理.md",
    "Inventory": "11-库存管理.md",
    "Channel": "13-渠道管理.md",
    "Overbooking": "11-库存管理.md",
    "Booking": "11-库存管理.md",
    "Reservation": "11-库存管理.md" # or order management PRD if it existed
}

def get_prd(name):
    for k, v in prd_map.items():
        if k in name:
            return v
    return "00-SOW-功能清单.md"

def get_desc_suffix(class_name, package_name):
    if "service" in package_name:
        if class_name.endswith("Impl"):
            return "服务实现类 (Service Implementation)"
        return "服务接口 (Service Interface)"
    if "controller" in package_name:
        return "控制器 (REST Controller)"
    if "dto" in package_name:
        return "数据传输对象 (DTO)"
    if "config" in package_name:
        return "系统配置类 (Configuration)"
    if "util" in package_name:
        return "工具类 (Utility)"
    return "核心类"

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Skip if already has class level docstring
    if re.search(r"/\*\*[\s\S]*?\*/\s*(?:@[A-Za-z0-9_]+(?:\([^)]*\))?\s*)*(?:public\s+|abstract\s+|final\s+)*(?:class|interface|enum)\s+\w+", content):
        return False

    # Extract class/interface name
    match = re.search(r"(?:public\s+|abstract\s+|final\s+)*(class|interface|enum)\s+(\w+)", content)
    if not match:
        return False
        
    type_kind = match.group(1)
    class_name = match.group(2)
    
    prd = get_prd(class_name)
    desc_suffix = get_desc_suffix(class_name, filepath)
        
    docstring = f"""/**
 * {class_name} {desc_suffix}
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【{class_name}】相关的核心业务逻辑、对外接口或数据传输封装。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/{prd}</li>
 *     <li>**模块职责**：遵循单一职责原则，实现 {class_name} 的功能定义。</li>
 * </ul>
 * 
 * @since 2026-05-10
 */
"""
    
    # Insert docstring before class definition and its annotations
    class_def_pattern = r"((?:@[A-Za-z0-9_]+(?:\([^)]*\))?\s*)*)((?:public\s+|abstract\s+|final\s+)*(?:class|interface|enum)\s+" + class_name + r")"
    
    new_content = re.sub(class_def_pattern, docstring + r"\1\2", content, count=1)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        return True
    return False

if __name__ == "__main__":
    directories = [
        "backend/src/main/java/com/crs/service/**/*.java",
        "backend/src/main/java/com/crs/controller/**/*.java",
        "backend/src/main/java/com/crs/dto/**/*.java",
        "backend/src/main/java/com/crs/config/**/*.java",
        "backend/src/main/java/com/crs/util/**/*.java",
        "backend/src/main/java/com/crs/exception/**/*.java"
    ]
    
    files = []
    for d in directories:
        files.extend(glob.glob(d, recursive=True))

    modified = 0
    for f in files:
        if process_file(f):
            modified += 1
            
    print(f"Successfully added class-level comments to {modified} remaining files.")
