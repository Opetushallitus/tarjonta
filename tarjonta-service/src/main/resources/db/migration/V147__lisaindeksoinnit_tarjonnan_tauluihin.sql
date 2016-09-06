DROP INDEX IF EXISTS "yhteystiedot_hakukohde_id";
CREATE INDEX "yhteystiedot_hakukohde_id" ON "yhteystiedot" ("hakukohde_id");

DROP INDEX IF EXISTS "koulutusmoduuli_toteutus_owner_toteutus_id";
CREATE INDEX "koulutusmoduuli_toteutus_owner_toteutus_id" ON "koulutusmoduuli_toteutus_owner" ("koulutusmoduuli_toteutus_id");

DROP INDEX IF EXISTS "koulutusmoduuli_toteutus_viimpaivityspvm";
CREATE INDEX "koulutusmoduuli_toteutus_viimpaivityspvm" ON "koulutusmoduuli_toteutus" ("viimpaivityspvm");

DROP INDEX IF EXISTS "koulutusmoduuli_toteutus_viimindeksointipvm";
CREATE INDEX "koulutusmoduuli_toteutus_viimindeksointipvm" ON "koulutusmoduuli_toteutus" ("viimindeksointipvm");

DROP INDEX IF EXISTS "koulutusmoduuli_toteutus_toteutustyyppi";
CREATE INDEX "koulutusmoduuli_toteutus_toteutustyyppi" ON "koulutusmoduuli_toteutus" ("toteutustyyppi");

DROP INDEX IF EXISTS "koulutusmoduuli_toteutus_children_id";
CREATE INDEX "koulutusmoduuli_toteutus_children_id" ON "koulutusmoduuli_toteutus" ("koulutusmoduuli_toteutus_children_id");

DROP INDEX IF EXISTS "hakukohde_hakukelpoisuusvaatimus_hakukohde_id";
CREATE INDEX "hakukohde_hakukelpoisuusvaatimus_hakukohde_id" ON "hakukohde_hakukelpoisuusvaatimus" ("hakukohde_id");