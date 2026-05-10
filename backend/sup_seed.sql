-- ============================================================
-- SUP — Inicijalno punjenje podataka (seeding)
--
-- Pretpostavlja da Hibernate (ddl-auto=update) već kreira shemu
-- pri pokretanju Spring Boot aplikacije.
--
-- Seed je idempotentan: TRUNCATE ... RESTART IDENTITY CASCADE
-- briše sve i resetira sekvencere, pa ID-evi uvijek kreću od 1.
-- ============================================================

TRUNCATE TABLE
    povijest_stanja,
    stavka,
    komentar,
    zadatak,
    clan_projekta,
    projekt,
    korisnik,
    stanje_zadatka,
    prioritet,
    status_projekta,
    uloga
RESTART IDENTITY CASCADE;

-- 0. Šifrarnici (lookup tablice)

INSERT INTO uloga (naziv) VALUES
    ('Direktor'),
    ('Menadžer'),
    ('Zaposleni');

INSERT INTO status_projekta (naziv) VALUES
('U pripremi'),
    ('Aktivan'),
    ('Završen'),
    ('Obustavljan');

INSERT INTO prioritet (naziv, redoslijed) VALUES
    ('Nizak',    1),
    ('Srednji',  2),
    ('Visok',    3),
    ('Kritičan', 4);

INSERT INTO stanje_zadatka (naziv, redoslijed) VALUES
    ('U pripremi',  1),
    ('U postupku',  2),
    ('Na provjeri', 3),
    ('Zatvoren',    4);

-- 1. Korisnici
-- Lozinke su bcrypt hashevi testne lozinke 'Test1234!'
-- Marin Vuković je deaktiviran (aktivan = FALSE) — bivši zaposlenik.

INSERT INTO korisnik (korisnicko_ime, lozinka_hash, ime, prezime, email, uloga_id, aktivan) VALUES
    ('ihorvat',     '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashDirektor',    'Ivan',     'Horvat',    'ivan.horvat@techsolutions.hr',       1, TRUE),
    ('akovacevic',  '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashMenadzer1',   'Ana',      'Kovačević', 'ana.kovacevic@techsolutions.hr',     2, TRUE),
    ('mbabic',      '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashMenadzer2',   'Marko',    'Babić',     'marko.babic@techsolutions.hr',       2, TRUE),
    ('pnovak',      '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni1',  'Petra',    'Novak',     'petra.novak@techsolutions.hr',       3, TRUE),
    ('ljuric',      '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni2',  'Luka',     'Jurić',     'luka.juric@techsolutions.hr',        3, TRUE),
    ('mtomic',      '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni3',  'Maja',     'Tomić',     'maja.tomic@techsolutions.hr',        3, TRUE),
    ('dpetrovic',   '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashMenadzer3',   'Dario',    'Petrović',  'dario.petrovic@techsolutions.hr',    2, TRUE),
    ('ssimic',      '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashMenadzer4',   'Sara',     'Šimić',     'sara.simic@techsolutions.hr',        2, TRUE),
    ('tknezevic',   '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni4',  'Tomislav', 'Knežević',  'tomislav.knezevic@techsolutions.hr', 3, TRUE),
    ('ivisnjic',    '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni5',  'Iva',      'Višnjić',   'iva.visnjic@techsolutions.hr',       3, TRUE),
    ('nblazevic',   '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni6',  'Nikolina', 'Blažević',  'nikolina.blazevic@techsolutions.hr', 3, TRUE),
    ('fpavlic',     '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni7',  'Filip',    'Pavlić',    'filip.pavlic@techsolutions.hr',      3, TRUE),
    ('mvukovic',    '$2a$10$xJwL5v5Zq1Z8z5z5z5z5zOexamplehashZaposleni8',  'Marin',    'Vuković',   'marin.vukovic@techsolutions.hr',     3, FALSE);

-- 2. Projekti
-- Status: 1=Aktivan, 2=Završen, 3=Obustavljan

INSERT INTO projekt (naziv, opis, datum_pocetka, datum_zavrsetka, status_projekta_id, menadzer_id) VALUES
    ('Web trgovina',
     'Razvoj web trgovine za klijenta XY d.o.o. Uključuje frontend, backend i integraciju s platnim sustavom.',
     '2026-01-15', '2026-06-30', 1, 2),

    ('Mobilna aplikacija',
     'Razvoj mobilne aplikacije za internu uporabu. iOS i Android verzija s push notifikacijama.',
     '2026-03-01', '2026-09-30', 1, 3),

    ('CRM sustav',
     'Razvoj internog CRM sustava za upravljanje klijentima i prodajnim procesima.',
     '2026-02-01', '2026-12-31', 1, 7),

    ('ERP migracija',
     'Migracija postojećeg ERP sustava na novu platformu. Uključuje analizu, mapping i edukaciju krajnjih korisnika.',
     '2026-04-01', '2026-11-30', 1, 8),

    ('Redizajn web stranice',
     'Redizajn korporativne web stranice s naglaskom na pristupačnost i mobile-first dizajn.',
     '2025-09-01', '2026-01-15', 2, 2),

    ('Integracija s API-jem banke',
     'Integracija sustava plaćanja s API-jem partnerske banke. Trenutno obustavljen zbog izmjene ugovora.',
     '2025-11-01', '2026-04-30', 3, 3);

-- 3. Članovi projekata
-- Menadžer je uvijek član svog projekta. Neki članovi sudjeluju u više projekata.

INSERT INTO clan_projekta (projekt_id, korisnik_id) VALUES
    -- Projekt 1: Web trgovina
    (1, 2),  -- Ana (menadžer)
    (1, 4),  -- Petra
    (1, 5),  -- Luka

    -- Projekt 2: Mobilna aplikacija
    (2, 3),  -- Marko (menadžer)
    (2, 5),  -- Luka (na više projekata)
    (2, 6),  -- Maja

    -- Projekt 3: CRM sustav
    (3, 7),  -- Dario (menadžer)
    (3, 9),  -- Tomislav
    (3, 10), -- Iva

    -- Projekt 4: ERP migracija
    (4, 8),  -- Sara (menadžer)
    (4, 11), -- Nikolina
    (4, 12), -- Filip
    (4, 6),  -- Maja (na više projekata)

    -- Projekt 5: Redizajn (završen)
    (5, 2),  -- Ana (menadžer)
    (5, 4),  -- Petra (na više projekata)
    (5, 10), -- Iva (na više projekata)

    -- Projekt 6: Integracija s bankom (obustavljan)
    (6, 3),  -- Marko (menadžer)
    (6, 9),  -- Tomislav (na više projekata)
    (6, 12); -- Filip (na više projekata)

-- 4. Zadaci
-- prioritet_id: 1=Nizak, 2=Srednji, 3=Visok, 4=Kritičan
-- stanje_zadatka_id: 1=U pripremi, 2=U postupku, 3=Na provjeri, 4=Zatvoren

INSERT INTO zadatak (naziv, opis, prioritet_id, stanje_zadatka_id, projekt_id, dodijeljeni_id, kreator_id, rok) VALUES
    -- Projekt 1: Web trgovina
    ('Dizajn baze podataka',
     'Kreirati ER dijagram i DDL skriptu za bazu podataka web trgovine.',
     3, 4, 1, 4, 2, '2026-02-15'),

    ('Implementacija REST API-ja',
     'Razviti backend API za upravljanje proizvodima, košaricom i narudžbama.',
     3, 2, 1, 5, 2, '2026-04-15'),

    ('Izrada wireframea',
     'Dizajnirati wireframe za glavne stranice: početna, katalog, košarica, checkout.',
     2, 3, 1, 4, 2, '2026-03-01'),

    ('Integracija sa Stripe sustavom',
     'Implementirati naplatu putem Stripe-a, uključujući webhookove za potvrdu transakcija.',
     4, 1, 1, 5, 2, '2026-05-15'),

    ('Testiranje korisničkih scenarija',
     'Napisati i izvršiti end-to-end testove za ključne scenarije: registracija, kupnja, povrat.',
     2, 1, 1, 4, 2, '2026-06-15'),

    -- Projekt 2: Mobilna aplikacija
    ('Postavljanje CI/CD pipeline-a',
     'Konfigurirati GitHub Actions za automatski build i deploy na testni server.',
     2, 1, 2, 6, 3, '2026-04-15'),

    ('Razvoj korisničkog sučelja',
     'Implementirati ekrane za prijavu, dashboard i listu obavijesti.',
     4, 2, 2, 5, 3, '2026-05-30'),

    ('Pisanje tehničke dokumentacije',
     'Dokumentirati API endpointe, arhitekturu i upute za deployment.',
     1, 1, 2, NULL, 3, '2026-08-30'),

    ('Implementacija push notifikacija',
     'Integrirati Firebase Cloud Messaging za iOS i Android.',
     3, 2, 2, 5, 3, '2026-07-15'),

    ('Optimizacija performansi',
     'Profilirati aplikaciju i smanjiti vrijeme učitavanja na ispod 2 sekunde.',
     2, 1, 2, 6, 3, '2026-09-01'),

    -- Projekt 3: CRM sustav
    ('Analiza zahtjeva klijenata',
     'Provesti intervjue s ključnim korisnicima i dokumentirati funkcionalne zahtjeve.',
     3, 4, 3, 9, 7, '2026-02-28'),

    ('Modul za upravljanje kontaktima',
     'Implementirati CRUD operacije za kontakte i tvrtke, s mogućnošću grupiranja po segmentu.',
     4, 2, 3, 10, 7, '2026-05-30'),

    ('Modul za prodaju',
     'Razviti modul za praćenje prodajnog lijevka i kvota po prodavaču.',
     3, 1, 3, 9, 7, '2026-08-30'),

    ('Integracija s e-mail sustavom',
     'Povezati CRM s Microsoft 365 i Gmail-om za automatsku sinkronizaciju komunikacije.',
     2, 1, 3, 10, 7, '2026-09-30'),

    ('Reporting modul',
     'Implementirati dashboard s ključnim KPI-jevima i izvozom u PDF/Excel.',
     1, 1, 3, NULL, 7, '2026-11-30'),

    -- Projekt 4: ERP migracija
    ('Inventar postojećeg sustava',
     'Popisati sve module starog ERP sustava i identificirati podatke za migraciju.',
     3, 4, 4, 11, 8, '2026-04-30'),

    ('Mapping starih podataka',
     'Definirati mapping između stare i nove sheme baze podataka.',
     4, 2, 4, 12, 8, '2026-06-15'),

    ('Skripte za migraciju',
     'Razviti i testirati ETL skripte za prijenos podataka.',
     4, 2, 4, 12, 8, '2026-08-15'),

    ('Validacija migriranih podataka',
     'Provesti automatske i ručne provjere integriteta nakon migracije.',
     3, 1, 4, 6, 8, '2026-09-30'),

    ('Edukacija krajnjih korisnika',
     'Pripremiti i održati radionice za korisnike novog sustava.',
     2, 1, 4, 11, 8, '2026-11-15'),

    -- Projekt 5: Redizajn web stranice (završen)
    ('Analiza trenutne stranice',
     'Heuristička evaluacija postojeće stranice i prikupljanje povratnih informacija od korisnika.',
     2, 4, 5, 10, 2, '2025-09-30'),

    ('Novi dizajn',
     'Izraditi nove mockupe i prototipe u Figmi.',
     3, 4, 5, 4, 2, '2025-11-15'),

    ('Implementacija nove verzije',
     'Razviti novu verziju stranice u Next.js-u, s prilagodbom za mobilne uređaje.',
     3, 4, 5, 10, 2, '2025-12-31'),

    ('Migracija sadržaja',
     'Prebaciti sve postojeće članke i medije na novu platformu.',
     2, 4, 5, 4, 2, '2026-01-10'),

    -- Projekt 6: Integracija s bankom (obustavljan)
    ('Specifikacija API-ja',
     'Pripremiti tehničku specifikaciju i dogovoriti je s bankom.',
     3, 4, 6, 9, 3, '2025-12-15'),

    ('Implementacija autentikacije',
     'Implementirati OAuth2 + mTLS prema specifikaciji banke. Pauzirano do dogovora.',
     4, 1, 6, 12, 3, '2026-03-15'),

    ('Testiranje sigurnosti',
     'Penetracijski testovi i analiza ranjivosti integracije.',
     4, 1, 6, NULL, 3, '2026-04-15');

-- 5. Komentari

INSERT INTO komentar (tekst, zadatak_id, autor_id) VALUES
    -- Zadatak 1: Dizajn baze
    ('Baza je gotova, ER dijagram je u prilogu. Molim provjeru.',                       1, 4),
    ('Sve izgleda u redu, odobravam. Odličan posao!',                                   1, 2),

    -- Zadatak 2: REST API
    ('Imam pitanje oko autentikacije — koristimo li JWT ili session cookies?',          2, 5),
    ('Koristimo JWT. Pogledaj primjer u dokumentaciji projekta.',                       2, 2),
    ('Hvala, krećem s implementacijom.',                                                2, 5),

    -- Zadatak 3: Wireframe
    ('Wireframe je spreman za review. Fokusirala sam se na mobile-first pristup.',      3, 4),
    ('Nedostaje checkout stranica, molim doradu.',                                      3, 2),
    ('Dodano. Ponovo na review.',                                                       3, 4),

    -- Zadatak 4: Stripe
    ('Otvorio sam test račun na Stripe-u, krećem s integracijom.',                      4, 5),

    -- Zadatak 7: UI mobilne
    ('Prvi dizajn dashboard-a je uploadan na Figmu.',                                   7, 5),
    ('Sviđa mi se smjer, idemo s tim.',                                                 7, 3),

    -- Zadatak 9: Push notifikacije
    ('Trebamo li podržati i web push notifikacije?',                                    9, 5),
    ('Za sada samo native, web ide u sljedećoj fazi.',                                  9, 3),

    -- Zadatak 11: Analiza CRM
    ('Završeni svi intervjui, dokument zahtjeva je u Confluence-u.',                   11, 9),
    ('Hvala, organizirat ćemo review s klijentom.',                                    11, 7),

    -- Zadatak 12: Modul kontakti
    ('Implementiran osnovni CRUD, radim na grupiranju.',                               12, 10),
    ('Možeš li pripremiti demo za petak?',                                             12, 7),
    ('Mogu, javit ću se s detaljima.',                                                 12, 10),

    -- Zadatak 16: Inventar ERP
    ('Inventar je gotov, otkrila sam 3 dodatna modula koja nisu bila u dokumentaciji.', 16, 11),
    ('Dodaj ih u scope, javit ću klijentu.',                                           16, 8),

    -- Zadatak 17: Mapping
    ('Mapping je 70% gotov. Imam nekoliko pitanja oko legacy polja.',                  17, 12),
    ('Pošalji popis pitanja, prošli ćemo ih u utorak.',                                17, 8),

    -- Zadatak 18: ETL skripte
    ('Prvi dry-run je prošao bez grešaka.',                                            18, 12),
    ('Odlično! Pripremi i load test s punim volumenom.',                               18, 8),

    -- Zadatak 21: Analiza (završen projekt)
    ('Analiza dovršena, glavni problemi su pristupačnost i sporo učitavanje.',         21, 10),

    -- Zadatak 22: Novi dizajn
    ('Prototip je odobren od strane uprave.',                                          22, 4),
    ('Krećemo s implementacijom.',                                                     22, 2),

    -- Zadatak 25: Specifikacija (obustavljan)
    ('Specifikacija je usuglašena s bankom. Čekamo potpis ugovora.',                   25, 9),
    ('Banka traži dodatne klauzule, projekt na čekanju.',                              25, 3),

    -- Zadatak 26: Auth banke
    ('Pauzirao sam rad dok ne dobijemo nove specifikacije.',                           26, 12);

-- 6. Stavke (checklist)

INSERT INTO stavka (tekst, zavrsena, zadatak_id) VALUES
    -- Zadatak 1: Dizajn baze
    ('Kreirati ER dijagram',                  TRUE,  1),
    ('Napisati DDL skriptu',                  TRUE,  1),
    ('Dodati testne podatke',                 TRUE,  1),

    -- Zadatak 2: REST API
    ('Endpoint za proizvode (CRUD)',          TRUE,  2),
    ('Endpoint za košaricu',                  FALSE, 2),
    ('Endpoint za narudžbe',                  FALSE, 2),
    ('Autentikacija i autorizacija',          FALSE, 2),

    -- Zadatak 3: Wireframe
    ('Wireframe — početna stranica',          TRUE,  3),
    ('Wireframe — katalog proizvoda',         TRUE,  3),
    ('Wireframe — košarica i checkout',       TRUE,  3),

    -- Zadatak 4: Stripe
    ('Postaviti Stripe test account',         TRUE,  4),
    ('Implementirati Payment Intent',         FALSE, 4),
    ('Webhook za potvrdu transakcije',        FALSE, 4),
    ('Test refund flow',                      FALSE, 4),

    -- Zadatak 7: UI mobilne
    ('Ekran za prijavu',                      TRUE,  7),
    ('Dashboard',                             TRUE,  7),
    ('Lista obavijesti',                      FALSE, 7),
    ('Postavke korisnika',                    FALSE, 7),

    -- Zadatak 9: Push notifikacije
    ('Postavka Firebase projekta',            TRUE,  9),
    ('iOS integracija',                       TRUE,  9),
    ('Android integracija',                   FALSE, 9),
    ('Backend slanje notifikacija',           FALSE, 9),

    -- Zadatak 12: Modul kontakti
    ('Model i baza za kontakte',              TRUE,  12),
    ('Model i baza za tvrtke',                TRUE,  12),
    ('CRUD endpointi',                        TRUE,  12),
    ('Grupiranje po segmentu',                FALSE, 12),
    ('Import iz CSV-a',                       FALSE, 12),

    -- Zadatak 17: Mapping
    ('Mapping korisnika',                     TRUE,  17),
    ('Mapping računa',                        TRUE,  17),
    ('Mapping artikala',                      FALSE, 17),
    ('Mapping povijesnih transakcija',        FALSE, 17),

    -- Zadatak 18: ETL skripte
    ('Skripta za prijenos korisnika',         TRUE,  18),
    ('Skripta za prijenos artikala',          FALSE, 18),
    ('Skripta za prijenos transakcija',       FALSE, 18),
    ('Logiranje grešaka i retry',             FALSE, 18),

    -- Zadatak 21: Analiza (završen)
    ('Heuristička evaluacija',                TRUE,  21),
    ('Analiza Google Analytics-a',            TRUE,  21),
    ('Intervjui s 10 korisnika',              TRUE,  21),
    ('Sastavljanje izvještaja',               TRUE,  21);

-- 7. Povijest stanja zadataka
-- Detaljan ciklus za reprezentativne zadatke iz različitih projekata.

INSERT INTO povijest_stanja (zadatak_id, staro_stanje_id, novo_stanje_id, promijenio_id, komentar, datum_promjene) VALUES
    -- Zadatak 1: pun ciklus do Zatvoren
    (1, NULL, 1, 2,  'Zadatak kreiran.',                                     '2026-01-16 09:00:00'),
    (1, 1,    2, 4,  'Preuzimam zadatak.',                                   '2026-01-17 08:30:00'),
    (1, 2,    3, 4,  'Baza je gotova, šaljem na provjeru.',                  '2026-02-10 16:00:00'),
    (1, 3,    4, 2,  'Odobreno.',                                            '2026-02-12 10:00:00'),

    -- Zadatak 2: U pripremi → U postupku
    (2, NULL, 1, 2,  'Zadatak kreiran.',                                     '2026-02-01 09:00:00'),
    (2, 1,    2, 5,  'Počinjem s radom na API-ju.',                          '2026-02-05 08:00:00'),

    -- Zadatak 3: pun ciklus s vraćanjem na doradu
    (3, NULL, 1, 2,  'Zadatak kreiran.',                                     '2026-02-01 09:30:00'),
    (3, 1,    2, 4,  'Preuzimam wireframe zadatak.',                         '2026-02-03 10:00:00'),
    (3, 2,    3, 4,  'Wireframe spreman za provjeru.',                       '2026-02-20 14:00:00'),
    (3, 3,    2, 2,  'Nedostaje checkout, doraditi.',                        '2026-02-22 11:00:00'),
    (3, 2,    3, 4,  'Dodana checkout stranica.',                            '2026-02-25 15:00:00'),

    -- Zadatak 7: U pripremi → U postupku
    (7, NULL, 1, 3,  'Zadatak kreiran.',                                     '2026-03-05 09:00:00'),
    (7, 1,    2, 5,  'Počinjem s razvojem UI-ja.',                           '2026-03-10 08:00:00'),

    -- Zadatak 9: U pripremi → U postupku
    (9, NULL, 1, 3,  'Zadatak kreiran.',                                     '2026-04-01 09:00:00'),
    (9, 1,    2, 5,  'Krećem s Firebase integracijom.',                      '2026-04-05 09:30:00'),

    -- Zadatak 11: pun ciklus do Zatvoren
    (11, NULL, 1, 7, 'Zadatak kreiran.',                                     '2026-02-02 09:00:00'),
    (11, 1,    2, 9, 'Krećem s intervjuima.',                                '2026-02-05 10:00:00'),
    (11, 2,    3, 9, 'Dokument zahtjeva spreman.',                           '2026-02-25 16:00:00'),
    (11, 3,    4, 7, 'Odobreno od klijenta.',                                '2026-02-28 14:00:00'),

    -- Zadatak 12: U pripremi → U postupku
    (12, NULL, 1, 7, 'Zadatak kreiran.',                                     '2026-03-01 09:00:00'),
    (12, 1,    2, 10,'Preuzimam.',                                           '2026-03-05 08:30:00'),

    -- Zadatak 16: pun ciklus do Zatvoren
    (16, NULL, 1, 8, 'Zadatak kreiran.',                                     '2026-04-02 09:00:00'),
    (16, 1,    2, 11,'Krećem s inventarom.',                                 '2026-04-05 10:00:00'),
    (16, 2,    3, 11,'Inventar gotov.',                                      '2026-04-25 15:00:00'),
    (16, 3,    4, 8, 'Odobreno.',                                            '2026-04-28 09:00:00'),

    -- Zadatak 17: U pripremi → U postupku
    (17, NULL, 1, 8, 'Zadatak kreiran.',                                     '2026-04-15 09:00:00'),
    (17, 1,    2, 12,'Krećem s mapping-om.',                                 '2026-04-20 09:30:00'),

    -- Zadatak 18: U pripremi → U postupku
    (18, NULL, 1, 8, 'Zadatak kreiran.',                                     '2026-04-20 09:00:00'),
    (18, 1,    2, 12,'Krećem s ETL skriptama.',                              '2026-04-25 10:00:00'),

    -- Zadatak 21: pun ciklus do Zatvoren (završen projekt)
    (21, NULL, 1, 2, 'Zadatak kreiran.',                                     '2025-09-02 09:00:00'),
    (21, 1,    2, 10,'Krećem s analizom.',                                   '2025-09-05 09:00:00'),
    (21, 2,    3, 10,'Analiza gotova.',                                      '2025-09-25 16:00:00'),
    (21, 3,    4, 2, 'Prihvaćeno.',                                          '2025-09-28 10:00:00'),

    -- Zadatak 22: pun ciklus do Zatvoren
    (22, NULL, 1, 2, 'Zadatak kreiran.',                                     '2025-09-29 09:00:00'),
    (22, 1,    2, 4, 'Krećem s dizajnom.',                                   '2025-10-01 09:00:00'),
    (22, 2,    3, 4, 'Dizajn spreman.',                                      '2025-11-10 16:00:00'),
    (22, 3,    4, 2, 'Odobreno od uprave.',                                  '2025-11-14 10:00:00'),

    -- Zadatak 23: pun ciklus do Zatvoren
    (23, NULL, 1, 2, 'Zadatak kreiran.',                                     '2025-11-15 09:00:00'),
    (23, 1,    2, 10,'Krećem s implementacijom.',                            '2025-11-17 09:00:00'),
    (23, 2,    3, 10,'Implementacija završena.',                             '2025-12-22 16:00:00'),
    (23, 3,    4, 2, 'Odobreno.',                                            '2025-12-28 10:00:00'),

    -- Zadatak 24: pun ciklus do Zatvoren
    (24, NULL, 1, 2, 'Zadatak kreiran.',                                     '2025-12-29 09:00:00'),
    (24, 1,    2, 4, 'Krećem s migracijom sadržaja.',                        '2025-12-30 09:00:00'),
    (24, 2,    4, 2, 'Migracija gotova, projekt zatvoren.',                  '2026-01-12 14:00:00'),

    -- Zadatak 25: pun ciklus do Zatvoren (obustavljan projekt)
    (25, NULL, 1, 3, 'Zadatak kreiran.',                                     '2025-11-05 09:00:00'),
    (25, 1,    2, 9, 'Pišem specifikaciju.',                                 '2025-11-08 10:00:00'),
    (25, 2,    3, 9, 'Specifikacija gotova.',                                '2025-12-10 15:00:00'),
    (25, 3,    4, 3, 'Specifikacija odobrena, ali ugovor nije potpisan.',    '2025-12-12 11:00:00');
