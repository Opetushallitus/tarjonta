CREATE TABLE "oppiaineet" (
  "id" bigint NOT NULL,
  "version" bigint NOT NULL,
  "kieli_koodi" character varying(255) NOT NULL,
  "oppiaine" character varying(255) NOT NULL
);

ALTER TABLE "oppiaineet"
ADD CONSTRAINT "oppiaineet_id" PRIMARY KEY ("id");

CREATE TABLE "koulutusmoduuli_toteutus_oppiaineet" (
  "koulutusmoduuli_toteutus_id" bigint NOT NULL,
  "oppiaine_id" bigint NOT NULL
);

ALTER TABLE "koulutusmoduuli_toteutus_oppiaineet"
ADD CONSTRAINT "koulutusmoduuli_toteutus_oppiaineet_koulutusmoduuli_toteutus_id_oppiaine_id" PRIMARY KEY ("koulutusmoduuli_toteutus_id", "oppiaine_id");

ALTER TABLE "koulutusmoduuli_toteutus_oppiaineet"
ADD FOREIGN KEY ("koulutusmoduuli_toteutus_id") REFERENCES "koulutusmoduuli_toteutus" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE "koulutusmoduuli_toteutus_oppiaineet"
ADD FOREIGN KEY ("oppiaine_id") REFERENCES "oppiaineet" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE "oppiaineet"
ADD CONSTRAINT "oppiaineet_oppiaine_kieli_koodi" UNIQUE ("oppiaine", "kieli_koodi");
