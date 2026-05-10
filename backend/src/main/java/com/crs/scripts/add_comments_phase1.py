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
    "Booking": "11-库存管理.md"
}

def get_prd(name):
    for k, v in prd_map.items():
        if k in name:
            return v
    return "00-SOW-功能清单.md"

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
    
    desc_suffix = "实体类" if type_kind == "class" else "数据访问层 (Repository) 接口"
    if "Repository" in class_name:
        desc_suffix = "数据访问层 (Repository) 接口"
        
    docstring = f"""/**
 * {class_name} {desc_suffix}
 * 
 * <p>本核心模块自动生成详细注释。主要负责【{class_name}】相关的核心业务数据承载与持久化映射。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/{prd}</li>
 *     <li>**模块职责**：单一职责原则，提供 {class_name} 数据结构的定义或相关的 CRUD 数据库交互操作。</li>
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
    files = glob.glob("backend/src/main/java/com/crs/entity/**/*.java", recursive=True) + \
            glob.glob("backend/src/main/java/com/crs/repository/**/*.java", recursive=True)

    modified = 0
    for f in files:
        if process_file(f):
            modified += 1
            
    print(f"Successfully added class-level comments to {modified} files.")
