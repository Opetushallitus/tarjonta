ALTER TABLE "hakukohde"
ADD "unique_external_id" character varying(255) NULL;

ALTER TABLE "hakukohde"
ADD CONSTRAINT "hakukohde_unique_external_id" UNIQUE ("unique_external_id");