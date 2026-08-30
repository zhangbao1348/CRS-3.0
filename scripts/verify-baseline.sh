#!/usr/bin/env bash

set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
java17_home=""

if [[ "$(uname -s)" == "Darwin" ]] && [[ -x /usr/libexec/java_home ]]; then
  java17_home="$(/usr/libexec/java_home -v 17 2>/dev/null || true)"
fi

if [[ -z "$java17_home" ]]; then
  echo "未找到 Java 17。请先安装 JDK 17，再执行基线验证。"
  exit 1
fi

export JAVA_HOME="$java17_home"

cd "$project_root"

echo "执行前端生产构建"
npm run build

echo "执行前端自动化测试"
npm test -- --runInBand

echo "检查 ESLint 冻结基线"
node scripts/check-eslint-baseline.mjs

echo "执行后端 Java 17 编译"
mvn -q -f backend/pom.xml -DskipTests compile

echo "执行后端自动化测试"
mvn -q -f backend/pom.xml test

echo "阶段 0 基线验证通过"
