DROP INDEX IF EXISTS "koulutusmoduuli_toteutus_siblings";
CREATE INDEX CONCURRENTLY "koulutusmoduuli_toteutus_siblings"
  ON "koulutusmoduuli_toteutus" ("tarjoaja",
                                 "toteutustyyppi",
                                 "alkamisvuosi",
                                 "oid",
                                 "tila")
  WHERE toteutustyyppi IN (
    'AMMATILLINEN_PERUSTUTKINTO',
    'AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA'
  );

