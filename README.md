# SUP — Sustav upravljanja projektima

Repozitorij za treću domaću zadaću iz kolegija Informacijski sustavi.

Aplikacija se sastoji od:
- **backend** — Spring Boot 4 (Java 21, Gradle), PostgreSQL
- **frontend** — Next.js 16 (React 19, Tailwind)

## Preduvjeti

- Java 21
- PostgreSQL 14+ (s korisnikom `postgres` i lozinkom `postgres` ili uz prilagodbu env varijabli)
- Node.js 20+ i npm
- `psql` u `PATH`-u

Postavke konekcije na bazu nalaze se u `backend/src/main/resources/application.properties`.

## Redoslijed pokretanja

Sve naredbe izvršavaju se iz korijena repozitorija.

### 1. Kreiranje baze

Skripta kreira bazu `infosus` (ako ne postoji) i izvršava `backend/sup_baza.sql`
(tablice, indeksi, šifrarnici).

```bash
./scripts/create-db.sh
```

Po potrebi prilagođavaju se env varijable: `DB_NAME`, `DB_USER`, `DB_HOST`, `DB_PORT`.

```bash
DB_USER=myuser DB_PORT=5433 ./scripts/create-db.sh
```

### 2. Pokretanje backenda

```bash
cd backend
./gradlew bootRun
```

Backend sluša na `http://localhost:8080`. Hibernate (`ddl-auto=update`) uskladit
će shemu s JPA modelima ako je potrebno.

### 3. Učitavanje seed podataka

U novom terminalu, dok backend i dalje radi:

```bash
./scripts/seed-db.sh
```

Skripta izvršava `backend/sup_seed.sql` i unosi testne korisnike, projekte,
zadatke, komentare i ostale entitete.

### 4. Pokretanje frontenda

```bash
cd frontend
npm install   # samo prvi put
npm run dev
```

Frontend je dostupan na `http://localhost:3000`.

## Struktura

```
dz3/
├── backend/              Spring Boot aplikacija
│   ├── sup_baza.sql      DDL + šifrarnici
│   └── sup_seed.sql      Testni podaci
├── frontend/             Next.js aplikacija
├── scripts/
│   ├── create-db.sh      Kreira bazu i pokreće sup_baza.sql
│   └── seed-db.sh        Pokreće sup_seed.sql
└── SUP_specifikacija.md
```

## Pokretanje unit testova (backend)

```bash
cd backend
./gradlew test
```

HTML izvještaj nakon pokretanja:

```
backend/build/reports/tests/test/index.html
```

Pokretanje samo određene grupe testova:

```bash
./gradlew test --tests "com.example.demo.services.*"      # samo servisi
./gradlew test --tests "com.example.demo.controllers.*"   # samo kontroleri
./gradlew test --tests "com.example.demo.repositories.*"  # samo repozitoriji
```

Pokretanje jedne testne klase ili pojedinačne metode:

```bash
./gradlew test --tests "com.example.demo.controllers.TaskControllerTest"
./gradlew test --tests "com.example.demo.services.TaskServiceTest.create_stampsInitialStatusIdOne"
```

## Pokretanje integration testova (backend)

Integration testovi vrte cijeli slice — kontroler → servis → repository → baza — nad H2 bazom u PostgreSQL modu (in-memory, ne dira lokalni Postgres). Konfiguracija je u `backend/src/test resources/application.properties`, lookup podaci se učitavaju iz `backend/src/test/resources/integration-seed.sql`.

```bash
cd backend
./gradlew test --tests "com.example.demo.integration.*"
```

Pokretanje pojedinačne klase:

```bash
./gradlew test --tests "com.example.demo.integration.ProjectFlowIntegrationTest"
./gradlew test --tests "com.example.demo.integration.TaskFlowIntegrationTest"
```

Testovi koriste `@Transactional` rollback — svaki test radi u vlastitoj transakciji koja se odbacuje na kraju, pa nije potrebno ručno čišćenje baze.
