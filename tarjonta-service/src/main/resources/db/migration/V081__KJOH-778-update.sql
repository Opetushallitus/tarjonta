-- Lisää unique constraint
ALTER TABLE "koulutusmoduuli_toteutus_owners"
ADD CONSTRAINT "koulutusmoduuli_toteutus_owners_koulutusmoduuli_toteutus_id_owneroid_ownertype" PRIMARY KEY ("koulutusmoduuli_toteutus_id", "owneroid", "ownertype");

-- Kopioi tarjoaja-tieto "koulutusmoduuli_toteutus" taulusta uuteen "koulutusmoduuli_toteutus_owners" mappaus tauluun
INSERT INTO "koulutusmoduuli_toteutus_owners" ("koulutusmoduuli_toteutus_id", "owneroid", "ownertype")
SELECT "id" AS "koulutusmoduuli_toteutus_id", "tarjoaja" AS "owneroid", 'TARJOAJA' FROM "koulutusmoduuli_toteutus";
