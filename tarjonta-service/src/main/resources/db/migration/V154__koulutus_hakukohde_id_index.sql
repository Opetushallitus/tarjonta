DROP INDEX IF EXISTS "koulutus_hakukohde_hakukohde_id";
CREATE INDEX "koulutus_hakukohde_hakukohde_id" ON "koulutus_hakukohde" ("hakukohde_id");

DROP INDEX IF EXISTS "koulutus_hakukohde_koulutus_id";
CREATE INDEX "koulutus_hakukohde_koulutus_id" ON "koulutus_hakukohde" ("koulutus_id");
