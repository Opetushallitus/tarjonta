ALTER TABLE hakukohde ADD COLUMN valintaperustekuvaus_koodi_uri character varying(255);
ALTER TABLE hakukohde ADD COLUMN sora_kuvaus_koodi_uri character varying(255);
ALTER TABLE hakukohde ADD COLUMN valintaperustekuvaus_kategoria character varying(32);
ALTER TABLE hakukohde ADD COLUMN sora_kuvaus_kategoria character varying(32);

--ROLLBACK
--ALTER TABLE hakukohde DROP COLUMN valintaperustekuvaus_koodi_uri;
--ALTER TABLE hakukohde DROP COLUMN valintaperustekuvaus_kategoria;
--ALTER TABLE hakukohde DROP COLUMN sora_kuvaus_koodi_uri;
--ALTER TABLE hakukohde DROP COLUMN sora_kuvaus_kategoria;
--DELETE FROM schema_version WHERE version_rank=11;