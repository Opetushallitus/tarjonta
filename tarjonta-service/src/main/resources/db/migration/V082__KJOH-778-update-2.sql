drop table koulutusmoduuli_toteutus_owners;

create table koulutusmoduuli_toteutus_owner (
    id int8 not null unique,
    version int8 not null,
    koulutusmoduuli_toteutus_id int8 not null,
    ownerOid varchar(255),
    ownerType varchar(255)
);

alter table koulutusmoduuli_toteutus_owner
    add constraint FKE812D75E2566EBFA
    foreign key (koulutusmoduuli_toteutus_id)
    references koulutusmoduuli_toteutus;


-- Lisää unique constraint
ALTER TABLE "koulutusmoduuli_toteutus_owner"
ADD CONSTRAINT "koulutusmoduuli_toteutus_owner_koulutusmoduuli_toteutus_id_owneroid_ownertype" UNIQUE ("koulutusmoduuli_toteutus_id", "owneroid", "ownertype");

-- Kopioi tarjoaja-tieto "koulutusmoduuli_toteutus" taulusta uuteen "koulutusmoduuli_toteutus_owner" mappaus tauluun
INSERT INTO "koulutusmoduuli_toteutus_owner" (id, "koulutusmoduuli_toteutus_id", "owneroid", "ownertype", "version")
SELECT nextval('hibernate_sequence'), "id" AS "koulutusmoduuli_toteutus_id", "tarjoaja" AS "owneroid", 'TARJOAJA', 0 FROM "koulutusmoduuli_toteutus";
