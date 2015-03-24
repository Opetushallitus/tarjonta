ALTER TABLE "koulutusmoduuli_toteutus"
ADD "hinta_string" character varying(255) NULL;
COMMENT ON COLUMN "koulutusmoduuli_toteutus"."hinta" IS
'vanha kenttä, jolloin oletettiin hinnan sisältävän vain numeroita';
COMMENT ON COLUMN "koulutusmoduuli_toteutus"."hinta_string" IS
'uusi kenttä, koska pitää pystyä syöttämään myös muuta kuin numeroita';

ALTER TABLE "koulutusmoduuli_toteutus"
ALTER "maksullisuus" TYPE boolean USING CAST(maksullisuus as boolean);

UPDATE koulutusmoduuli_toteutus set maksullisuus = false where maksullisuus is null;

ALTER TABLE "koulutusmoduuli_toteutus"
ALTER "maksullisuus" SET NOT NULL;

UPDATE koulutusmoduuli_toteutus SET "hinta_string" = "hinta";