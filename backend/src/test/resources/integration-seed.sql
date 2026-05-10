-- Idempotentni seed za integration testove.
-- @Sql skripta se commita izvan testne transakcije, pa moramo prvo počistiti
-- ostatke prethodnog @Sql commita prije ponovnog umetanja.
-- Lookup ID-evi su fiksni jer ih servisi koriste kao konstante
-- (npr. TaskService.INITIAL_STATUS_ID = 1, ProjectService traži statuse "Završen" / "Zatvoren").

DELETE FROM povijest_stanja;
DELETE FROM komentar;
DELETE FROM stavka;
DELETE FROM clan_projekta;
DELETE FROM zadatak;
DELETE FROM projekt;
DELETE FROM korisnik;
DELETE FROM uloga;
DELETE FROM status_projekta;
DELETE FROM prioritet;
DELETE FROM stanje_zadatka;

ALTER TABLE uloga ALTER COLUMN uloga_id RESTART WITH 100;
ALTER TABLE status_projekta ALTER COLUMN status_projekta_id RESTART WITH 100;
ALTER TABLE prioritet ALTER COLUMN prioritet_id RESTART WITH 100;
ALTER TABLE stanje_zadatka ALTER COLUMN stanje_zadatka_id RESTART WITH 100;
ALTER TABLE korisnik ALTER COLUMN korisnik_id RESTART WITH 1;
ALTER TABLE projekt ALTER COLUMN projekt_id RESTART WITH 1;
ALTER TABLE zadatak ALTER COLUMN zadatak_id RESTART WITH 1;
ALTER TABLE clan_projekta ALTER COLUMN clan_projekta_id RESTART WITH 1;

INSERT INTO uloga (uloga_id, naziv) VALUES
  (1, 'Direktor'),
  (2, 'Menadžer'),
  (3, 'Zaposleni');

INSERT INTO status_projekta (status_projekta_id, naziv) VALUES
  (1, 'Aktivan'),
  (2, 'Završen'),
  (3, 'Otkazan');

INSERT INTO prioritet (prioritet_id, naziv, redoslijed) VALUES
  (1, 'Nizak', 1),
  (2, 'Normalan', 2),
  (3, 'Visok', 3);

INSERT INTO stanje_zadatka (stanje_zadatka_id, naziv, redoslijed) VALUES
  (1, 'Otvoren', 1),
  (2, 'U tijeku', 2),
  (3, 'Zatvoren', 3);
