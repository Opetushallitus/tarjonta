DROP INDEX IF EXISTS "ryhmaliitos_hakukohde_id";
CREATE INDEX "ryhmaliitos_hakukohde_id" ON "ryhmaliitos" ("hakukohde_id");

DROP INDEX IF EXISTS "valintakoe_hakukohde_id";
CREATE INDEX "valintakoe_hakukohde_id" ON "valintakoe" ("hakukohde_id");
