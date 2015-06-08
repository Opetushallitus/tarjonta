ALTER TABLE "koulutusmoduuli_toteutus"
ADD "haun_kopioinnin_tunniste" character varying(255) NULL;

ALTER TABLE "hakukohde"
ADD "haun_kopioinnin_tunniste" character varying(255) NULL;

UPDATE "koulutusmoduuli_toteutus" SET "haun_kopioinnin_tunniste" = "ulkoinentunniste";

UPDATE "hakukohde" SET "haun_kopioinnin_tunniste" = "ulkoinentunniste";