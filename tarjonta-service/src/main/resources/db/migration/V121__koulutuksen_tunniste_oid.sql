ALTER TABLE "koulutusmoduuli"
ADD "koulutuksen_tunniste_oid" character varying(255) NULL;

UPDATE "koulutusmoduuli" SET "koulutuksen_tunniste_oid" = "oid";

ALTER TABLE "koulutusmoduuli"
ALTER "koulutuksen_tunniste_oid" SET NOT NULL;

CREATE INDEX "koulutusmoduuli_koulutuksen_tunniste_oid" ON "koulutusmoduuli" ("koulutuksen_tunniste_oid");