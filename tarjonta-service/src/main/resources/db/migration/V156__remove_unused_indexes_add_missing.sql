DROP INDEX IF EXISTS "koulutusmoduuli_toteutus_tarjoajatiedot_id_index";
CREATE INDEX "koulutusmoduuli_toteutus_tarjoajatiedot_id_index"
  ON "koulutusmoduuli_toteutus_tarjoajatiedot_tarjoaja_oid" ("koulutusmoduuli_toteutus_tarjoajatiedot_id");

DROP INDEX IF EXISTS "koulutusmoduuli_toteutus_koulutusmoduuli_id";
CREATE INDEX "koulutusmoduuli_toteutus_koulutusmoduuli_id"
  ON "koulutusmoduuli_toteutus" ("koulutusmoduuli_id");

DROP INDEX IF EXISTS "valintakoe_ajankohta_valintakoe_id";
CREATE INDEX "valintakoe_ajankohta_valintakoe_id"
  ON "valintakoe_ajankohta" ("valintakoe_id");

/* Näitä ei ole postgresin analyysin mukaan käytetty koskaan. */
DROP INDEX IF EXISTS "haku_hakutapa";
DROP INDEX IF EXISTS "haku_hakutyyppi";
DROP INDEX IF EXISTS "haku_kohdejoukko";
DROP INDEX IF EXISTS "haku_koulutuksen_alkamiskausi";
DROP INDEX IF EXISTS "haku_koulutuksen_alkamisvuosi";
DROP INDEX IF EXISTS "haku_tila";
DROP INDEX IF EXISTS "hakuaika_alkamispvm";
DROP INDEX IF EXISTS "koulutus_permissions_alku_pvm";
DROP INDEX IF EXISTS "koulutus_permissions_koodi_uri";
DROP INDEX IF EXISTS "koulutus_permissions_koodisto";
DROP INDEX IF EXISTS "koulutus_permissions_loppu_pvm";
DROP INDEX IF EXISTS "koulutusmoduuli_koulutuksen_tunniste_oid";
DROP INDEX IF EXISTS "koulutusmoduuli_toteutus_toteutustyyppi";
DROP INDEX IF EXISTS "localisation_key_idx";
DROP INDEX IF EXISTS "localisation_language_idx";
DROP INDEX IF EXISTS "monikielinen_metadata_avain_idx";
DROP INDEX IF EXISTS "monikielinen_metadata_kategoria_idx";
DROP INDEX IF EXISTS "monikielinen_metadata_kieli_idx";
DROP INDEX IF EXISTS "valintaperuste_sora_kuvaus_tila";

