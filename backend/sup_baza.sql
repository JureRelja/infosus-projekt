-- ============================================================
-- SUP (Sustav upravljanja projektima) — PostgreSQL baza podataka
-- TechSolutions d.o.o.
--
-- Skripta za kreiranje strukture baze i punjenje šifrarnika.
-- ============================================================

-- ============================================================
-- 1. BRISANJE POSTOJEĆIH TABLICA (obrnutim redoslijedom)
-- ============================================================

DROP TABLE IF EXISTS povijest_stanja CASCADE;
DROP TABLE IF EXISTS stavka CASCADE;
DROP TABLE IF EXISTS komentar CASCADE;
DROP TABLE IF EXISTS zadatak CASCADE;
DROP TABLE IF EXISTS clan_projekta CASCADE;
DROP TABLE IF EXISTS projekt CASCADE;
DROP TABLE IF EXISTS korisnik CASCADE;
DROP TABLE IF EXISTS stanje_zadatka CASCADE;
DROP TABLE IF EXISTS prioritet CASCADE;
DROP TABLE IF EXISTS status_projekta CASCADE;
DROP TABLE IF EXISTS uloga CASCADE;

-- ============================================================
-- 2. KREIRANJE TABLICA
-- ============================================================

-- 2.1 Šifrarnici (lookup tablice)

CREATE TABLE uloga (
    uloga_id    SERIAL PRIMARY KEY,
    naziv       VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE status_projekta (
    status_projekta_id  SERIAL PRIMARY KEY,
    naziv               VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE prioritet (
    prioritet_id    SERIAL PRIMARY KEY,
    naziv           VARCHAR(50) NOT NULL UNIQUE,
    redoslijed      INTEGER NOT NULL UNIQUE
);

CREATE TABLE stanje_zadatka (
    stanje_zadatka_id   SERIAL PRIMARY KEY,
    naziv               VARCHAR(50) NOT NULL UNIQUE,
    redoslijed          INTEGER NOT NULL UNIQUE
);

-- 2.2 Korisnik

CREATE TABLE korisnik (
    korisnik_id     SERIAL PRIMARY KEY,
    korisnicko_ime  VARCHAR(100) NOT NULL UNIQUE,
    lozinka_hash    VARCHAR(255) NOT NULL,
    ime             VARCHAR(100) NOT NULL,
    prezime         VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    uloga_id        INTEGER NOT NULL REFERENCES uloga(uloga_id),
    aktivan         BOOLEAN NOT NULL DEFAULT TRUE,
    datum_kreiranja TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2.3 Projekt

CREATE TABLE projekt (
    projekt_id          SERIAL PRIMARY KEY,
    naziv               VARCHAR(255) NOT NULL,
    opis                TEXT,
    datum_pocetka       DATE NOT NULL,
    datum_zavrsetka     DATE NOT NULL,
    status_projekta_id  INTEGER NOT NULL REFERENCES status_projekta(status_projekta_id),
    menadzer_id         INTEGER NOT NULL REFERENCES korisnik(korisnik_id),
    datum_kreiranja     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_datum_projekta CHECK (datum_zavrsetka > datum_pocetka)
);

-- 2.4 Član projekta (spojna tablica — M:N korisnik ↔ projekt)

CREATE TABLE clan_projekta (
    clan_projekta_id    SERIAL PRIMARY KEY,
    projekt_id          INTEGER NOT NULL REFERENCES projekt(projekt_id) ON DELETE CASCADE,
    korisnik_id         INTEGER NOT NULL REFERENCES korisnik(korisnik_id),
    datum_dodavanja     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_clan_projekta UNIQUE (projekt_id, korisnik_id)
);

-- 2.5 Zadatak

CREATE TABLE zadatak (
    zadatak_id          SERIAL PRIMARY KEY,
    naziv               VARCHAR(255) NOT NULL,
    opis                TEXT,
    prioritet_id        INTEGER NOT NULL REFERENCES prioritet(prioritet_id),
    stanje_zadatka_id   INTEGER NOT NULL REFERENCES stanje_zadatka(stanje_zadatka_id),
    projekt_id          INTEGER NOT NULL REFERENCES projekt(projekt_id) ON DELETE CASCADE,
    dodijeljeni_id      INTEGER REFERENCES korisnik(korisnik_id),
    kreator_id          INTEGER NOT NULL REFERENCES korisnik(korisnik_id),
    rok                 DATE,
    datum_kreiranja     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2.6 Komentar

CREATE TABLE komentar (
    komentar_id     SERIAL PRIMARY KEY,
    tekst           TEXT NOT NULL,
    zadatak_id      INTEGER NOT NULL REFERENCES zadatak(zadatak_id) ON DELETE CASCADE,
    autor_id        INTEGER NOT NULL REFERENCES korisnik(korisnik_id),
    datum_kreiranja TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2.7 Stavka (checklist)

CREATE TABLE stavka (
    stavka_id       SERIAL PRIMARY KEY,
    tekst           VARCHAR(500) NOT NULL,
    zavrsena        BOOLEAN NOT NULL DEFAULT FALSE,
    zadatak_id      INTEGER NOT NULL REFERENCES zadatak(zadatak_id) ON DELETE CASCADE,
    datum_kreiranja TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2.8 Povijest stanja zadatka

CREATE TABLE povijest_stanja (
    povijest_stanja_id  SERIAL PRIMARY KEY,
    zadatak_id          INTEGER NOT NULL REFERENCES zadatak(zadatak_id) ON DELETE CASCADE,
    staro_stanje_id     INTEGER REFERENCES stanje_zadatka(stanje_zadatka_id),
    novo_stanje_id      INTEGER NOT NULL REFERENCES stanje_zadatka(stanje_zadatka_id),
    promijenio_id       INTEGER NOT NULL REFERENCES korisnik(korisnik_id),
    komentar            TEXT,
    datum_promjene      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. INDEKSI
-- ============================================================

CREATE INDEX idx_korisnik_uloga          ON korisnik(uloga_id);
CREATE INDEX idx_projekt_menadzer        ON projekt(menadzer_id);
CREATE INDEX idx_projekt_status          ON projekt(status_projekta_id);
CREATE INDEX idx_clan_projekta_korisnik  ON clan_projekta(korisnik_id);
CREATE INDEX idx_zadatak_projekt         ON zadatak(projekt_id);
CREATE INDEX idx_zadatak_dodijeljeni     ON zadatak(dodijeljeni_id);
CREATE INDEX idx_zadatak_stanje          ON zadatak(stanje_zadatka_id);
CREATE INDEX idx_zadatak_prioritet       ON zadatak(prioritet_id);
CREATE INDEX idx_zadatak_kreator         ON zadatak(kreator_id);
CREATE INDEX idx_komentar_zadatak        ON komentar(zadatak_id);
CREATE INDEX idx_komentar_autor          ON komentar(autor_id);
CREATE INDEX idx_stavka_zadatak          ON stavka(zadatak_id);
CREATE INDEX idx_povijest_zadatak        ON povijest_stanja(zadatak_id);
CREATE INDEX idx_povijest_promijenio     ON povijest_stanja(promijenio_id);

-- ============================================================
-- 4. PUNJENJE ŠIFRARNIKA
-- ============================================================

INSERT INTO uloga (naziv) VALUES
    ('Direktor'),
    ('Menadžer'),
    ('Zaposleni');

INSERT INTO status_projekta (naziv) VALUES
    ('Aktivan'),
    ('Završen'),
    ('Obustavljan');

INSERT INTO prioritet (naziv, redoslijed) VALUES
    ('Nizak',    1),
    ('Srednji',  2),
    ('Visok',    3),
    ('Kritičan', 4);

INSERT INTO stanje_zadatka (naziv, redoslijed) VALUES
    ('U pripremi', 1),
    ('U postupku',  2),
    ('Na provjeri', 3),
    ('Zatvoren',    4);
