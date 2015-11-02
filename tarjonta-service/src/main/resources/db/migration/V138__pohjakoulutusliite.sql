CREATE TABLE "hakukohde_pohjakoulutusliite" (
  "hakukohde_id" bigint NOT NULL,
  "koodi_uri" character varying(255) NOT NULL
);

ALTER TABLE "hakukohde_pohjakoulutusliite"
ADD FOREIGN KEY ("hakukohde_id") REFERENCES "hakukohde" ("id");

ALTER TABLE "hakukohde_pohjakoulutusliite"
ADD CONSTRAINT "hakukohde_pohjakoulutusliite_hakukohde_id_koodi_uri" PRIMARY KEY ("hakukohde_id", "koodi_uri");

ALTER TABLE "hakukohde"
ADD "jos_yo_ei_muita_liitepyyntoja" boolean NOT NULL DEFAULT 'false';