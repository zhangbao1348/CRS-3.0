#!/usr/bin/env bash
# -----------------------------------------------------------------------------
# CRS 本地一键启动入口（关联模块：React/Vite 前端、Spring Boot 后端）。
#
# 用法：
#   ./start-local.sh            启动前后端
#   ./start-local.sh --restart  重启由本脚本管理的前后端
#   ./start-local.sh --stop     停止由本脚本管理的前后端
# -----------------------------------------------------------------------------
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$PROJECT_DIR/logs"
PID_DIR="$PROJECT_DIR/pids"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_PORT=3001
BACKEND_PORT=8080
STOP_TIMEOUT_SECONDS=90
FRONTEND_PID_FILE="$PID_DIR/frontend.pid"
BACKEND_PID_FILE="$PID_DIR/backend.pid"
FRONTEND_LOG="$LOG_DIR/frontend.log"
BACKEND_LOG="$LOG_DIR/backend.log"
LOCAL_ENV_FILE="$PROJECT_DIR/.env.local"
STARTED_PIDS=()

usage() {
  cat <<'EOF'
用法：./start-local.sh [--restart|--stop|--help]

  无参数 / --restart  加载 .env.local，构建并启动本地前端（3001）与后端（8080）
  --stop              停止由本脚本记录的 CRS 本地进程
  --help              显示本说明
EOF
}

require_command() {
  local command_name="$1"
  command -v "$command_name" >/dev/null 2>&1 || {
    echo "❌ 缺少命令：$command_name"
    exit 127
  }
}

require_environment_variable() {
  local variable_name="$1"
  if [ -z "${!variable_name:-}" ]; then
    echo "❌ .env.local 缺少必填配置：$variable_name"
    exit 78
  fi
}

load_local_environment() {
  if [ ! -f "$LOCAL_ENV_FILE" ]; then
    echo "❌ 未找到 $LOCAL_ENV_FILE"
    echo "   请复制 .env.example 为 .env.local，并填写本机数据库密码和 JWT 密钥。"
    exit 78
  fi

  local line=""
  local variable_name=""
  local variable_value=""
  while IFS= read -r line || [ -n "$line" ]; do
    line="${line%$'\r'}"
    case "$line" in
      ""|\#*)
        continue
        ;;
      *=*)
        variable_name="${line%%=*}"
        variable_value="${line#*=}"
        ;;
      *)
        echo "❌ .env.local 存在无法解析的行，请使用 KEY=VALUE 格式。"
        exit 78
        ;;
    esac

    if [[ ! "$variable_name" =~ ^[A-Za-z_][A-Za-z0-9_]*$ ]]; then
      echo "❌ .env.local 包含无效变量名：$variable_name"
      exit 78
    fi
    export "$variable_name=$variable_value"
  done < "$LOCAL_ENV_FILE"
  chmod 600 "$LOCAL_ENV_FILE"

  for variable_name in CRS_DB_URL CRS_DB_USERNAME CRS_DB_PASSWORD CRS_JWT_SECRET; do
    require_environment_variable "$variable_name"
  done

  if [ "${#CRS_JWT_SECRET}" -lt 32 ]; then
    echo "❌ CRS_JWT_SECRET 至少需要 32 个字符。"
    exit 78
  fi
}

configure_java17() {
  local java17_home=""
  if [ "$(uname -s)" = "Darwin" ] && [ -x /usr/libexec/java_home ]; then
    java17_home="$(/usr/libexec/java_home -v 17 2>/dev/null || true)"
  fi

  if [ -n "$java17_home" ]; then
    export JAVA_HOME="$java17_home"
    export PATH="$JAVA_HOME/bin:$PATH"
  fi

  local java_major
  java_major="$(java -XshowSettings:properties -version 2>&1 \
    | sed -n 's/^ *java.specification.version = //p' \
    | head -n 1)"
  if [ "$java_major" != "17" ]; then
    echo "❌ CRS 后端要求 Java 17，当前规范版本：${java_major:-未知}"
    exit 78
  fi
}

read_pid() {
  local pid_file="$1"
  [ -f "$pid_file" ] || return 1
  local pid
  pid="$(tr -d '[:space:]' < "$pid_file")"
  [[ "$pid" =~ ^[0-9]+$ ]] || return 1
  printf '%s\n' "$pid"
}

stop_service() {
  local label="$1"
  local port="$2"
  local pid_file="$3"
  local pid
  if ! pid="$(read_pid "$pid_file")"; then
    rm -f "$pid_file"
    return
  fi

  if ! kill -0 "$pid" 2>/dev/null; then
    rm -f "$pid_file"
    return
  fi

  local listener_pids
  listener_pids="$(lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null || true)"
  case $'\n'"$listener_pids"$'\n' in
    *$'\n'"$pid"$'\n'*)
      ;;
    *)
      echo "⚠️ ${label} 的 PID 文件已失效（PID ${pid} 未监听端口 ${port}），不会停止未知进程。"
      rm -f "$pid_file"
      return
      ;;
  esac

  echo "🛑 停止 ${label}（PID: ${pid}）..."
  kill "$pid"
  for _ in $(seq 1 "$STOP_TIMEOUT_SECONDS"); do
    kill -0 "$pid" 2>/dev/null || break
    sleep 1
  done
  if kill -0 "$pid" 2>/dev/null; then
    echo "❌ ${label} 未能在 ${STOP_TIMEOUT_SECONDS} 秒内退出；请手动检查 PID ${pid}。"
    exit 1
  fi
  rm -f "$pid_file"
  echo "✅ $label 已停止"
}

ensure_port_available() {
  local label="$1"
  local port="$2"
  local pid_file="$3"
  local managed_pid=""
  managed_pid="$(read_pid "$pid_file" 2>/dev/null || true)"
  local listener_pids
  listener_pids="$(lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null || true)"

  if [ -z "$listener_pids" ]; then
    return
  fi

  if [ -n "$managed_pid" ] && [ "$listener_pids" = "$managed_pid" ]; then
    stop_service "$label" "$port" "$pid_file"
    return
  fi

  echo "❌ 端口 ${port} 已被其他进程占用（PID: ${listener_pids}），为避免误杀进程，启动已取消。"
  echo "   请释放端口后重试，或确认该服务不是 CRS 本地服务。"
  exit 1
}

wait_for_http() {
  local url="$1"
  local label="$2"
  local log_file="$3"
  echo "⏳ 等待 $label 就绪：$url"
  for second in $(seq 1 120); do
    if curl -fsS --max-time 2 "$url" >/dev/null 2>&1; then
      echo "✅ $label 已就绪（约 ${second} 秒）"
      return
    fi
    sleep 1
  done
  echo "❌ $label 在 120 秒内未就绪，最近日志如下："
  tail -n 60 "$log_file" 2>/dev/null || true
  exit 1
}

cleanup_on_error() {
  local exit_code="$1"
  if [ "$exit_code" -eq 0 ] || [ "${#STARTED_PIDS[@]}" -eq 0 ]; then
    return
  fi
  echo "⚠️ 启动失败，正在停止本次已启动的服务..."
  stop_service "前端" "$FRONTEND_PORT" "$FRONTEND_PID_FILE" || true
  stop_service "后端" "$BACKEND_PORT" "$BACKEND_PID_FILE" || true
}

trap 'cleanup_on_error $?' EXIT

mode="${1:-start}"
case "$mode" in
  start|--restart)
    ;;
  --stop)
    mkdir -p "$PID_DIR"
    require_command lsof
    stop_service "前端" "$FRONTEND_PORT" "$FRONTEND_PID_FILE"
    stop_service "后端" "$BACKEND_PORT" "$BACKEND_PID_FILE"
    exit 0
    ;;
  --help|-h)
    usage
    exit 0
    ;;
  *)
    usage >&2
    exit 64
    ;;
esac

mkdir -p "$LOG_DIR" "$PID_DIR"
for command_name in java mvn node npm curl lsof; do
  require_command "$command_name"
done
load_local_environment
configure_java17

if ! lsof -tiTCP:3306 -sTCP:LISTEN >/dev/null 2>&1; then
  echo "❌ 本地 MySQL（127.0.0.1:3306）未就绪。请先启动 MySQL 后重试。"
  exit 1
fi

ensure_port_available "前端" "$FRONTEND_PORT" "$FRONTEND_PID_FILE"
ensure_port_available "后端" "$BACKEND_PORT" "$BACKEND_PID_FILE"

cd "$PROJECT_DIR"
if [ ! -d "$PROJECT_DIR/node_modules" ]; then
  echo "📦 安装前端依赖..."
  npm ci --no-audit --no-fund
fi

echo "📦 构建后端..."
(cd "$BACKEND_DIR" && mvn -q -DskipTests package)

BACKEND_JAR="$(find "$BACKEND_DIR/target" -maxdepth 1 -type f -name 'crs-backend-*.jar' ! -name '*.original' -print -quit)"
if [ -z "$BACKEND_JAR" ]; then
  echo "❌ 未找到后端 JAR，构建未完成。"
  exit 1
fi

echo "🚀 启动后端（端口 ${BACKEND_PORT}）..."
nohup java -jar "$BACKEND_JAR" --spring.profiles.active="${SPRING_PROFILES_ACTIVE:-dev}" >"$BACKEND_LOG" 2>&1 &
backend_pid=$!
printf '%s\n' "$backend_pid" > "$BACKEND_PID_FILE"
STARTED_PIDS+=("$backend_pid")
wait_for_http "http://127.0.0.1:$BACKEND_PORT/actuator/health" "后端" "$BACKEND_LOG"

echo "🚀 启动前端（端口 ${FRONTEND_PORT}）..."
nohup node "$PROJECT_DIR/node_modules/vite/bin/vite.js" --host 127.0.0.1 --port "$FRONTEND_PORT" >"$FRONTEND_LOG" 2>&1 &
frontend_pid=$!
printf '%s\n' "$frontend_pid" > "$FRONTEND_PID_FILE"
STARTED_PIDS+=("$frontend_pid")
wait_for_http "http://127.0.0.1:$FRONTEND_PORT" "前端" "$FRONTEND_LOG"

echo ""
echo "🎉 CRS 本地服务已启动"
echo "   前端：http://localhost:$FRONTEND_PORT"
echo "   后端：http://localhost:$BACKEND_PORT"
echo "   后端健康检查：http://localhost:$BACKEND_PORT/actuator/health"
echo "   日志目录：$LOG_DIR"
echo "   停止服务：./start-local.sh --stop"
STARTED_PIDS=()
