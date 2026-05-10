import os
import glob
import re

def get_desc_suffix(class_name, package_name):
    if "enums" in package_name:
        return "枚举类 (Enum)"
    if "aspect" in package_name:
        return "切面类 (Aspect)"
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
        
    class_name = match.group(2)
    desc_suffix = get_desc_suffix(class_name, filepath)
        
    docstring = f"""/**
 * {class_name} {desc_suffix}
 * 
 * <p>本核心模块自动生成详细注释。主要负责处理【{class_name}】相关的常量定义或切面逻辑。</p>
 * 
 * <p>关键元数据关联：</p>
 * <ul>
 *     <li>**关联PRD文档**：.kiro/specs/prd/00-SOW-功能清单.md</li>
 *     <li>**模块职责**：遵循项目规范，提供统一的系统枚举或切面增强功能。</li>
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
        "backend/src/main/java/com/crs/enums/**/*.java",
        "backend/src/main/java/com/crs/aspect/**/*.java"
    ]
    
    files = []
    for d in directories:
        files.extend(glob.glob(d, recursive=True))

    modified = 0
    for f in files:
        if process_file(f):
            modified += 1
            
    print(f"Successfully added class-level comments to {modified} remaining files.")
