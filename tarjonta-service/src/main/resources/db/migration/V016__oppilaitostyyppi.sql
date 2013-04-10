ALTER TABLE koulutusmoduuli ADD COLUMN oppilaitostyyppi character varying(500);

--ROLLBACK
--ALTER TABLE koulutusmoduuli DROP COLUMN oppilaitostyyppi;