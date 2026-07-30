#!/usr/bin/env bash

set -Eeuo pipefail

PORT=5018
READY_URL="http://localhost:${PORT}/v3/api-docs"
OPEN_URL="http://localhost:${PORT}/swagger-ui.html"

echo "Iniciando Pendix..."

(
    for intento in {1..60}; do
        if curl -fsS "${READY_URL}" >/dev/null 2>&1; then
            echo
            echo "Pendix está funcionando en el puerto ${PORT}."
            echo "Abriendo Swagger UI..."

            xdg-open "${OPEN_URL}" >/dev/null 2>&1 &
            exit 0
        fi

        sleep 1
    done

    echo
    echo "No fue posible abrir Swagger después de 60 segundos."
    echo "Revisa los mensajes de Spring Boot."
) &

exec ./gradlew bootRun
