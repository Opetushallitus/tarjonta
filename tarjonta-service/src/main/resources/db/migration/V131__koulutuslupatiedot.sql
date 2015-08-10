CREATE TABLE "koulutus_permissions" (
  "id" bigint NOT NULL,
  "org_oid" character varying(255) NOT NULL,
  "koodisto" character varying(255) NOT NULL,
  "koodi_uri" character varying(255) NOT NULL,
  "alku_pvm" timestamp NULL,
  "loppu_pvm" timestamp NULL
);

ALTER TABLE "koulutus_permissions"
ADD CONSTRAINT "koulutus_permissions_id" PRIMARY KEY ("id");
CREATE INDEX "koulutus_permissions_koodisto" ON "koulutus_permissions" ("koodisto");
CREATE INDEX "koulutus_permissions_koodi_uri" ON "koulutus_permissions" ("koodi_uri");
CREATE INDEX "koulutus_permissions_org_oid" ON "koulutus_permissions" ("org_oid");
CREATE INDEX "koulutus_permissions_alku_pvm" ON "koulutus_permissions" ("alku_pvm");
CREATE INDEX "koulutus_permissions_loppu_pvm" ON "koulutus_permissions" ("loppu_pvm");