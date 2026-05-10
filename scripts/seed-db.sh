#!/usr/bin/env bash
# Puni bazu `infosus` testnim podacima iz sup_seed.sql.
# Pokreće se NAKON što je backend prvi put pokrenut (Hibernate ddl-auto=update
# uskladi shemu s JPA modelima).

set -euo pipefail

DB_NAME="${DB_NAME:-infosus}"
DB_USER="${DB_USER:-postgres}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_FILE="${SCRIPT_DIR}/../backend/sup_seed.sql"

if [ ! -f "$SQL_FILE" ]; then
    echo "Greška: ne mogu pronaći $SQL_FILE" >&2
    exit 1
fi

echo "==> Pokrećem sup_seed.sql na bazi '${DB_NAME}'..."
psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -f "$SQL_FILE"

echo "==> Seed podaci su uneseni."
