/**
Lisää indeksejä haku-tauluun, jotta kyselyt nopeutuvat.
 */
DROP INDEX IF EXISTS "haku_hakukausi_vuosi";
CREATE INDEX "haku_hakukausi_vuosi" ON "haku" ("hakukausi_vuosi");

DROP INDEX IF EXISTS "haku_hakutapa";
CREATE INDEX "haku_hakutapa" ON "haku" ("hakutapa");

DROP INDEX IF EXISTS "haku_hakutyyppi";
CREATE INDEX "haku_hakutyyppi" ON "haku" ("hakutyyppi");

DROP INDEX IF EXISTS "haku_kohdejoukko";
CREATE INDEX "haku_kohdejoukko" ON "haku" ("kohdejoukko");

DROP INDEX IF EXISTS "haku_koulutuksen_alkamisvuosi";
CREATE INDEX "haku_koulutuksen_alkamisvuosi" ON "haku" ("koulutuksen_alkamisvuosi");

DROP INDEX IF EXISTS "haku_koulutuksen_alkamiskausi";
CREATE INDEX "haku_koulutuksen_alkamiskausi" ON "haku" ("koulutuksen_alkamiskausi");

DROP INDEX IF EXISTS "haku_tila";
CREATE INDEX "haku_tila" ON "haku" ("tila");
