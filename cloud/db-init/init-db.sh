#!/bin/sh
set -eu

if [ -z "${DB_HOST:-}" ] || [ -z "${DB_NAME:-}" ] || [ -z "${DB_USER:-}" ] || [ -z "${DB_PASSWORD:-}" ]; then
  echo "DB_HOST, DB_NAME, DB_USER, and DB_PASSWORD must all be set."
  exit 1
fi

echo "Waiting for database connectivity..."
until mariadb-admin ping -h"${DB_HOST}" -u"${DB_USER}" -p"${DB_PASSWORD}" --silent; do
  sleep 2
done

for script in /app/sql/*.sql; do
  echo "Running ${script}..."
  mariadb -h"${DB_HOST}" -u"${DB_USER}" -p"${DB_PASSWORD}" "${DB_NAME}" < "${script}"
done

echo "Database initialization complete."
