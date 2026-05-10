-- ============================================================
-- SUP — Inicijalno punjenje podataka (seeding)
--
-- Pretpostavlja da je sup_baza.sql već izvršena.
-- ============================================================

-- 1. Korisnici
-- Lozinke su bcrypt hashevi testne lozinke 'Test1234!'

INSERT INTO korisnik (korisnicko_ime, lozinka_hash, ime, prezime, email, uloga_id) VALUES
    ('ihorvat',    '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashDirektor',    'Ivan',  'Horvat',     'ivan.horvat@techsolutions.hr',    1),
    ('akovacevic', '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashMenadzer1',   'Ana',   'Kovačević',  'ana.kovacevic@techsolutions.hr',  2),
    ('mbabic',     '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashMenadzer2',   'Marko', 'Babić',      'marko.babic@techsolutions.hr',    2),
    ('pnovak',     '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni1',  'Petra', 'Novak',      'petra.novak@techsolutions.hr',    3),
    ('ljuric',     '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni2',  'Luka',  'Jurić',      'luka.juric@techsolutions.hr',     3),
    ('mtomic',     '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni3',  'Maja',  'Tomić',      'maja.tomic@techsolutions.hr',     3);

-- 2. Projekti

INSERT INTO projekt (naziv, opis, datum_pocetka, datum_zavrsetka, status_projekta_id, menadzer_id) VALUES
    ('Web trgovina',
     'Razvoj web trgovine za klijenta XY d.o.o. Uključuje frontend, backend i integraciju s platnim sustavom.',
     '2026-01-15', '2026-06-30', 1, 2),
    ('Mobilna aplikacija',
     'Razvoj mobilne aplikacije za internu uporabu. iOS i Android verzija s push notifikacijama.',
     '2026-03-01', '2026-09-30', 1, 3);

-- 3. Članovi projekata
-- Menadžer je uvijek član svog projekta.
-- Luka je na oba projekta (demonstrira M:N vezu).

INSERT INTO clan_projekta (projekt_id, korisnik_id) VALUES
    (1, 2),  -- Ana (menadžer) → Web trgovina
    (1, 4),  -- Petra → Web trgovina
    (1, 5),  -- Luka → Web trgovina
    (2, 3),  -- Marko (menadžer) → Mobilna aplikacija
    (2, 5),  -- Luka → Mobilna aplikacija
    (2, 6);  -- Maja → Mobilna aplikacija

-- 4. Zadaci

INSERT INTO zadatak (naziv, opis, prioritet_id, stanje_zadatka_id, projekt_id, dodijeljeni_id, kreator_id, rok) VALUES
    -- Web trgovina
    ('Dizajn baze podataka',
     'Kreirati ER dijagram i DDL skriptu za bazu podataka web trgovine.',
     3, 4, 1, 4, 2, '2026-02-15'),

    ('Implementacija REST API-ja',
     'Razviti backend API za upravljanje proizvodima, košaricom i narudžbama.',
     3, 2, 1, 5, 2, '2026-04-15'),

    ('Izrada wireframea',
     'Dizajnirati wireframe za glavne stranice: početna, katalog, košarica, checkout.',
     2, 3, 1, 4, 2, '2026-03-01'),

    -- Mobilna aplikacija
    ('Postavljanje CI/CD pipeline-a',
     'Konfigurirati GitHub Actions za automatski build i deploy na testni server.',
     2, 1, 2, 6, 3, '2026-04-15'),

    ('Razvoj korisničkog sučelja',
     'Implementirati ekrane za prijavu, dashboard i listu obavijesti.',
     4, 2, 2, 5, 3, '2026-05-30'),

    ('Pisanje tehničke dokumentacije',
     'Dokumentirati API endpointe, arhitekturu i upute za deployment.',
     1, 1, 2, NULL, 3, '2026-08-30');

-- 5. Komentari

INSERT INTO komentar (tekst, zadatak_id, autor_id) VALUES
    ('Baza je gotova, ER dijagram je u prilogu. Molim provjeru.',
     1, 4),

    ('Sve izgleda u redu, odobravam. Odličan posao!',
     1, 2),

    ('Imam pitanje oko autentikacije — koristimo li JWT ili session cookies?',
     2, 5),

    ('Koristimo JWT. Pogledaj primjer u dokumentaciji projekta.',
     2, 2),

    ('Wireframe je spreman za review. Fokusirala sam se na mobile-first pristup.',
     3, 4);

-- 6. Stavke (checklist)

INSERT INTO stavka (tekst, zavrsena, zadatak_id) VALUES
    ('Kreirati ER dijagram',              TRUE,  1),
    ('Napisati DDL skriptu',              TRUE,  1),
    ('Dodati testne podatke',             TRUE,  1),
    ('Endpoint za proizvode (CRUD)',      TRUE,  2),
    ('Endpoint za košaricu',              FALSE, 2),
    ('Endpoint za narudžbe',              FALSE, 2),
    ('Autentikacija i autorizacija',      FALSE, 2),
    ('Wireframe — početna stranica',      TRUE,  3),
    ('Wireframe — katalog proizvoda',     TRUE,  3),
    ('Wireframe — košarica i checkout',   FALSE, 3);

-- 7. Povijest stanja zadataka
-- Zadatak 1 (Dizajn baze) prošao je cijeli ciklus: U pripremi → U postupku → Na provjeri → Zatvoren

INSERT INTO povijest_stanja (zadatak_id, staro_stanje_id, novo_stanje_id, promijenio_id, komentar, datum_promjene) VALUES
    (1, NULL, 1, 2, 'Zadatak kreiran.',                                    '2026-01-16 09:00:00'),
    (1, 1,    2, 4, 'Preuzimam zadatak.',                                  '2026-01-17 08:30:00'),
    (1, 2,    3, 4, 'Baza je gotova, šaljem na provjeru.',                 '2026-02-10 16:00:00'),
    (1, 3,    4, 2, 'Odobreno. Sve je u skladu sa specifikacijom.',        '2026-02-12 10:00:00'),

    -- Zadatak 2 (API): U pripremi → U postupku
    (2, NULL, 1, 2, 'Zadatak kreiran.',                                    '2026-02-01 09:00:00'),
    (2, 1,    2, 5, 'Počinjem s radom na API-ju.',                         '2026-02-05 08:00:00'),

    -- Zadatak 3 (Wireframe): U pripremi → U postupku → Na provjeri (s jednim vraćanjem na doradu)
    (3, NULL, 1, 2, 'Zadatak kreiran.',                                    '2026-02-01 09:30:00'),
    (3, 1,    2, 4, 'Preuzimam wireframe zadatak.',                        '2026-02-03 10:00:00'),
    (3, 2,    3, 4, 'Wireframe spreman za provjeru.',                      '2026-02-20 14:00:00'),
    (3, 3,    2, 2, 'Nedostaje checkout stranica, molim doradu.',          '2026-02-22 11:00:00'),
    (3, 2,    3, 4, 'Dodana checkout stranica. Ponovo šaljem na provjeru.','2026-02-25 15:00:00'),

    -- Zadatak 5 (UI mobilna): U pripremi → U postupku
    (5, NULL, 1, 3, 'Zadatak kreiran.',                                    '2026-03-05 09:00:00'),
    (5, 1,    2, 5, 'Počinjem s razvojem UI-ja.',                          '2026-03-10 08:00:00');
