ALTER TABLE hakukohde DROP COLUMN valintaperustekuvaus_kategoria;
ALTER TABLE hakukohde DROP COLUMN sora_kuvaus_kategoria;

--ROLLBACK
--ALTER TABLE hakukohde ADD COLUMN valintaperustekuvaus_kategoria character varying(32);
--ALTER TABLE hakukohde ADD COLUMN sora_kuvaus_kategoria character varying(32);
--DELETE FROM schema_version WHERE version_rank=11;