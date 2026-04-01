# Balance Portal Servlet

This project is a simple customer balance portal built with:

- Java 21
- Jakarta Servlets
- MySQL / Cloud SQL for MySQL
- Plain HTML, CSS, and vanilla JavaScript
- Session-based login with `HttpSession`
- Cloud SQL Java Connector support when `BALANCE_PORTAL_INSTANCE_CONNECTION_NAME` is set

## What it does

- Create accounts
- Sign in and sign out
- Show balance owed
- Show total charges and total payments
- Show the customer transaction ledger

## Active endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/me`
- `GET /api/transactions`

## Key files

- `src/main/java/.../config/AppConfig.java`
- `src/main/java/.../db/ConnectionFactory.java`
- `src/main/java/.../web`
- `src/main/webapp`
- `sql/schema.sql`
- `compose.yaml`
- `Dockerfile`
- `cloud/db-init`
- `cloudbuild.yaml`

## Local Docker

The local stack is:

1. `app` on port `8080`
2. `mysql` on port `3306`

From the project root:

```powershell
docker compose up --build
```

Then open:

```text
http://localhost:8080/
```

Local Docker uses these variables:

```text
BALANCE_PORTAL_DB_URL=jdbc:mysql://mysql:3306/balance_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
BALANCE_PORTAL_DB_NAME=balance_portal
BALANCE_PORTAL_DB_USER=root
BALANCE_PORTAL_DB_PASSWORD=rootpassword
```

## Cloud Run and Cloud SQL

The app now supports the Cloud SQL Java Connector path.

When `BALANCE_PORTAL_INSTANCE_CONNECTION_NAME` is set, the app uses the connector-based Cloud SQL connection instead of the raw `BALANCE_PORTAL_DB_URL`.

For your current Google Cloud setup, the intended app-side variables are:

```text
BALANCE_PORTAL_INSTANCE_CONNECTION_NAME=project-051d6e24-390d-450c-aa4:europe-west1:app-db
BALANCE_PORTAL_DB_NAME=app-db
BALANCE_PORTAL_DB_USER=<from secret>
BALANCE_PORTAL_DB_PASSWORD=<from secret>
BALANCE_PORTAL_DB_IP_TYPES=PUBLIC
```

## Cloud Build scaffold

`cloudbuild.yaml` is aligned to the values you provided:

- region: `europe-west1`
- repo: `my-app-images`
- service: `app-service`
- job: `db-initializer-job`
- instance connection name: `project-051d6e24-390d-450c-aa4:europe-west1:app-db`
- DB name: `app-db`
- app secrets:
  - `app-db-username`
  - `app-db-password`
- init job secrets:
  - `db-initializer-username`
  - `db-initializer-password`

The remaining placeholder is:

```text
_DB_HOST=CHANGE_ME_CLOUD_SQL_PUBLIC_IP
```

That host is only for the database initializer job, which still runs SQL scripts through the MySQL CLI client.

## Manual build

```powershell
mvn clean package
```

That creates:

```text
target/balance-portal-servlet.war
```
