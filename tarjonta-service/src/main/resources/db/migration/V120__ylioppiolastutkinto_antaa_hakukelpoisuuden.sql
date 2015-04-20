ALTER TABLE "haku"
ADD "ylioppilastutkinto_antaa_hakukelpoisuuden" boolean NOT NULL DEFAULT 'false';

ALTER TABLE "hakukohde"
ADD "ylioppilastutkinto_antaa_hakukelpoisuuden" boolean NULL;

CREATE INDEX "hakukohde_ylioppilastutkinto_antaa_hakukelpoisuuden" ON "hakukohde" ("ylioppilastutkinto_antaa_hakukelpoisuuden");