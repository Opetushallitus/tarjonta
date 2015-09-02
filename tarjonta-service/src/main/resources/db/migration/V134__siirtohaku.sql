ALTER TABLE "haku"
ADD "kohdejoukon_tarkenne" character varying(255) NULL;

CREATE TABLE "hakukohde_opinto_oikeus" (
  "hakukohde_id" bigint NOT NULL,
  "opinto_oikeus_uri" character varying(255) NOT NULL
);

ALTER TABLE "hakukohde_opinto_oikeus"
ADD CONSTRAINT "hakukohde_opinto_oikeus_hakukohde_id_opinto_oikeus_uri" PRIMARY KEY ("hakukohde_id", "opinto_oikeus_uri");

ALTER TABLE "hakukohde_opinto_oikeus"
ADD FOREIGN KEY ("hakukohde_id") REFERENCES "hakukohde" ("id") ON DELETE CASCADE