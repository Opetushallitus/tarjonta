ALTER TABLE "koulutusmoduuli_toteutus"
ADD "unique_external_id" character varying(255) NULL;

ALTER TABLE "koulutusmoduuli_toteutus"
ADD CONSTRAINT "koulutusmoduuli_toteutus_unique_external_id" UNIQUE ("unique_external_id");