CREATE INDEX teksti_kaannos_teksti_id_idx ON teksti_kaannos USING btree (teksti_id);

ALTER TABLE koulutusmoduuli ADD CONSTRAINT koulutusmoduuli_oid_key UNIQUE (oid);
ALTER TABLE koulutusmoduuli_toteutus ADD CONSTRAINT koulutusmoduuli_toteutus_oid_key UNIQUE (oid);
ALTER TABLE hakukohde ADD CONSTRAINT hakukohde_oid_key UNIQUE (oid);

-- ROLLBACK
-- DROP INDEX teksti_kaannos_teksti_id_idx;
-- ALTER TABLE koulutusmoduuli DROP CONSTRAINT koulutusmoduuli_oid_key;
-- ALTER TABLE koulutusmoduuli_toteutus DROP CONSTRAINT koulutusmoduuli_toteutus_oid_key;
-- ALTER TABLE hakukohde DROP CONSTRAINT hakukohde_oid_key;