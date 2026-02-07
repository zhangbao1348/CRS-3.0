#!/bin/bash

# 批量修复实体类中的JPA导入问题
# 将javax.persistence替换为jakarta.persistence

ENTITY_DIR="src/main/java/com/crs/entity"

# 遍历所有实体类文件
for file in "$ENTITY_DIR"/*.java; do
    if [ -f "$file" ]; then
        echo "修复文件: $file"
        # 使用sed命令替换导入语句
        sed -i '' 's/import javax.persistence/import jakarta.persistence/g' "$file"
    fi
done

echo "JPA导入修复完成!"
