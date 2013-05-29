alter table yhteyshenkilo add column tyyppi character varying(32);
UPDATE yhteyshenkilo SET tyyppi='Yhteyshenkilo';

-- ROLLBACK
--ALTER TABLE yhteyshenkilo DROP COLUMN tyyppi;