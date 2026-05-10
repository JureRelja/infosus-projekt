#!/usr/bin/env bash
# Kreira PostgreSQL bazu `infosus` i izvršava sup_baza.sql (struktura + šifrarnici).

set -euo pipefail

DB_NAME="${DB_NAME:-infosus}"
DB_USER="${DB_USER:-postgres}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_FILE="${SCRIPT_DIR}/../backend/sup_baza.sql"

if [ ! -f "$SQL_FILE" ]; then
    echo "Greška: ne mogu pronaći $SQL_FILE" >&2
    exit 1
fi

echo "==> Kreiram bazu '${DB_NAME}' (ako ne postoji)..."
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d postgres -tc \
    "SELECT 1 FROM pg_database WHERE datname = '${DB_NAME}'" | grep -q 1 \
    || psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d postgres \
        -c "CREATE DATABASE ${DB_NAME}"

echo "==> Pokrećem sup_baza.sql..."
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -f "$SQL_FILE"

echo "==> Baza '${DB_NAME}' je spremna."
