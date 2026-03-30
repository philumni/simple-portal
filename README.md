# Balance Portal Servlet Example

This sample keeps the stack intentionally plain:

- Java 21
- Jakarta Servlets
- MariaDB
- Static HTML, CSS, and vanilla JavaScript
- AJAX calls to JSON servlet endpoints
- `HttpSession` for login state

## Project shape

- `src/main/java/.../dao`: small JDBC data access classes
- `src/main/java/.../security`: password hashing and session helpers
- `src/main/java/.../web`: JSON servlets
- `src/main/webapp`: static frontend files
- `sql/schema.sql`: minimal schema
- `Dockerfile`: container image for the servlet app
- `compose.yaml`: local app + MariaDB stack

## Minimal schema

The database uses only two tables:

1. `customers`
2. `customer_transactions`

Sessions are handled by the servlet container through `HttpSession`, which keeps the schema easier to read.

## Database setup

Run the schema file in MariaDB:

```sql
source sql/schema.sql;
```

Then set these environment variables before starting your servlet container:

```powershell
$env:BALANCE_PORTAL_DB_URL="jdbc:mariadb://localhost:3306/balance_portal"
$env:BALANCE_PORTAL_DB_USER="root"
$env:BALANCE_PORTAL_DB_PASSWORD="password"
```

## Application flow

1. A customer registers with email, full name, and password.
2. The password is hashed with PBKDF2.
3. The servlet stores the customer id in `HttpSession`.
4. A starter set of sample transactions is inserted for the new account.
5. The frontend loads `/api/me` and `/api/transactions` with AJAX and renders the portal.

## Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/me`
- `GET /api/transactions`

## Running it

This is a standard WAR project. A simple path is:

1. Install Maven if it is not available on your machine.
2. Build with `mvn clean package`.
3. Deploy `target/balance-portal-servlet.war` to Tomcat 10.1+.

I did not run the build in this workspace because Maven is not installed globally here.

## Running with Docker locally

The project includes a multi-stage `Dockerfile` for the servlet app and a `compose.yaml` file that starts:

1. the Tomcat app on port `8080`
2. a MariaDB container on port `3306`

From the project root:

```powershell
docker compose up --build
```

Then open:

```text
http://localhost:8080/
```

The MariaDB container loads `sql/schema.sql` automatically on first startup.

The local Docker setup uses these app environment variables:

```text
BALANCE_PORTAL_DB_URL=jdbc:mariadb://mariadb:3306/balance_portal
BALANCE_PORTAL_DB_USER=root
BALANCE_PORTAL_DB_PASSWORD=rootpassword
```

## Cloud Run note

The servlet app container is a good fit for Cloud Run.

The MariaDB container is not.

For Google Cloud, the usual production pattern is:

1. Build and deploy the app container to Cloud Run.
2. Point the app at an external managed database.

Important: Google Cloud SQL supports MySQL, PostgreSQL, and SQL Server. It does not offer MariaDB as a managed Cloud SQL engine. So if you want to stay on Google Cloud, you have two realistic options:

1. use Cloud Run for the app and connect to an external MariaDB database
2. use Cloud Run for the app and move the database to Cloud SQL for MySQL

If you deploy the app container to Cloud Run, keep using the same environment variables, but set `BALANCE_PORTAL_DB_URL` to the reachable database host for that environment.

Example shape:

```text
BALANCE_PORTAL_DB_URL=jdbc:mariadb://YOUR_DB_HOST:3306/balance_portal
BALANCE_PORTAL_DB_USER=YOUR_DB_USER
BALANCE_PORTAL_DB_PASSWORD=YOUR_DB_PASSWORD
```

## Cloud Run container deployment

Build the container locally:

```powershell
docker build -t balance-portal-servlet .
```

Example Artifact Registry tag:

```text
us-central1-docker.pkg.dev/YOUR_PROJECT_ID/balance-portal/balance-portal-servlet:latest
```

Build and push with Google Cloud Build:

```powershell
gcloud builds submit --tag us-central1-docker.pkg.dev/YOUR_PROJECT_ID/balance-portal/balance-portal-servlet:latest
```

Deploy the app container to Cloud Run:

```powershell
gcloud run deploy balance-portal-servlet `
  --image us-central1-docker.pkg.dev/YOUR_PROJECT_ID/balance-portal/balance-portal-servlet:latest `
  --platform managed `
  --region us-central1 `
  --allow-unauthenticated `
  --set-env-vars BALANCE_PORTAL_DB_URL="jdbc:mariadb://YOUR_DB_HOST:3306/balance_portal",BALANCE_PORTAL_DB_USER="YOUR_DB_USER",BALANCE_PORTAL_DB_PASSWORD="YOUR_DB_PASSWORD"
```

Use Secret Manager instead of plain text environment variables for a real deployment when possible.

## Container build commands

Build the app image:

```powershell
docker build -t balance-portal-servlet .
```

Run the app container by itself:

```powershell
docker run --rm -p 8080:8080 `
  -e BALANCE_PORTAL_DB_URL="jdbc:mariadb://host.docker.internal:3306/balance_portal" `
  -e BALANCE_PORTAL_DB_USER="root" `
  -e BALANCE_PORTAL_DB_PASSWORD="your-password" `
  balance-portal-servlet
```
