--
-- KJOH-734 - 04.03.2014 / mlyly
--
-- Adding "maxHakukohdes" to Haku. Needed in KoulutusInformation UI to know how many
-- hakukohdes can be added...
--
ALTER TABLE haku ADD COLUMN max_hakukohdes int4;
ALTER TABLE haku ALTER COLUMN max_hakukohdes SET DEFAULT 0;

UPDATE haku SET max_hakukohdes = 0;
