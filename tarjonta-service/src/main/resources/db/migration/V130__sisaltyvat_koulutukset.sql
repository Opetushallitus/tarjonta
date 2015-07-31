CREATE TABLE "koulutusmoduuli_toteutus_sisaltyvat_koulutuskoodit" (
  "koulutusmoduuli_toteutus_id" bigint NOT NULL,
  "koodi_uri" character varying(255) NOT NULL
);

ALTER TABLE "koulutusmoduuli_toteutus_sisaltyvat_koulutuskoodit"
ADD CONSTRAINT "koulutusmoduuli_toteutus_sisaltyvat_koulutuskoodit_koulutusmoduuli_toteutus_id_koodi_uri" PRIMARY KEY ("koulutusmoduuli_toteutus_id", "koodi_uri");

ALTER TABLE "koulutusmoduuli_toteutus_sisaltyvat_koulutuskoodit"
ADD FOREIGN KEY ("koulutusmoduuli_toteutus_id") REFERENCES "koulutusmoduuli_toteutus" ("id");

/* Kopioi vanha sisältää kandi koulutuksen */
INSERT INTO koulutusmoduuli_toteutus_sisaltyvat_koulutuskoodit (koulutusmoduuli_toteutus_id, koodi_uri) (
  select k.id, COALESCE(k.kandi_koulutus_uri, m.kandi_koulutus_uri)
  from koulutusmoduuli m
  join koulutusmoduuli_toteutus k on k.koulutusmoduuli_id = m.id
  where COALESCE(k.kandi_koulutus_uri, m.kandi_koulutus_uri) is not null
);

/* Poista vanha kandi koulutus */
ALTER TABLE "koulutusmoduuli_toteutus"
DROP "kandi_koulutus_uri";

ALTER TABLE "koulutusmoduuli"
DROP "kandi_koulutus_uri";