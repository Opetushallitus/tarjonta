ALTER TABLE koulutusmoduuli ADD COLUMN koulutustyyppi character varying(32);
ALTER TABLE koulutusmoduuli ADD COLUMN lukiolinja character varying(255);

UPDATE koulutusmoduuli SET koulutustyyppi='AMMATILLINEN_PERUSKOULUTUS';

--ROLLBACK
--ALTER TABLE koulutusmoduuli DROP COLUMN koulutustyyppi;
--ALTER TABLE koulutusmoduuli DROP COLUMN lukiolinja;