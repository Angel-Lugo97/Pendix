#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-http://localhost:5018}"
EMAIL="${2:-angel.lugo@pendix.com}"
PASSWORD="${3:-1234}"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

assert_status() {
  local expected="$1"
  local actual="$2"
  local description="$3"

  if [[ "$actual" != "$expected" ]]; then
    echo "ERROR: $description"
    echo "Esperado: HTTP $expected"
    echo "Recibido: HTTP $actual"
    exit 1
  fi

  echo "OK: $description (HTTP $actual)"
}

echo "Probando Pendix JWT en $BASE_URL"

STATUS=$(curl -sS -o "$TMP_DIR/no-token.json" -w '%{http_code}' \
  "$BASE_URL/projects")
assert_status "401" "$STATUS" "endpoint protegido rechaza petición sin token"

STATUS=$(curl -sS -o "$TMP_DIR/bad-login.json" -w '%{http_code}' \
  -X POST "$BASE_URL/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"incorrecta\"}")
assert_status "401" "$STATUS" "login rechaza contraseña incorrecta"

STATUS=$(curl -sS -o "$TMP_DIR/login.json" -w '%{http_code}' \
  -X POST "$BASE_URL/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")
assert_status "200" "$STATUS" "login devuelve un token"

TOKEN=$(python3 - "$TMP_DIR/login.json" <<'PY'
import json
import sys

with open(sys.argv[1], encoding="utf-8") as file:
    payload = json.load(file)

token = payload.get("token")
if not token:
    raise SystemExit("ERROR: la respuesta de login no contiene token")

print(token)
PY
)

STATUS=$(curl -sS -o "$TMP_DIR/projects.json" -w '%{http_code}' \
  "$BASE_URL/projects" \
  -H "Authorization: Bearer $TOKEN")
assert_status "200" "$STATUS" "token permite consultar proyectos"

STATUS=$(curl -sS -o "$TMP_DIR/tasks.json" -w '%{http_code}' \
  "$BASE_URL/tasks" \
  -H "Authorization: Bearer $TOKEN")
assert_status "200" "$STATUS" "token permite consultar tareas"

STATUS=$(curl -sS -o "$TMP_DIR/reminders.json" -w '%{http_code}' \
  "$BASE_URL/reminders" \
  -H "Authorization: Bearer $TOKEN")
assert_status "200" "$STATUS" "token permite consultar recordatorios"

STATUS=$(curl -sS -o "$TMP_DIR/swagger.json" -w '%{http_code}' \
  "$BASE_URL/v3/api-docs")
assert_status "200" "$STATUS" "documentación OpenAPI permanece pública"

INVALID_TOKEN="${TOKEN%?}x"
STATUS=$(curl -sS -o "$TMP_DIR/invalid-token.json" -w '%{http_code}' \
  "$BASE_URL/projects" \
  -H "Authorization: Bearer $INVALID_TOKEN")
assert_status "401" "$STATUS" "token alterado es rechazado"

echo
echo "TOKEN JWT:"
echo "$TOKEN"
echo
echo "OK: autenticación JWT y endpoints protegidos funcionan correctamente."
