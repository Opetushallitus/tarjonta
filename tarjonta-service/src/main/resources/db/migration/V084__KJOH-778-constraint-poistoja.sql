alter table "koulutusmoduuli_toteutus_owner"
drop constraint if exists "koulutusmoduuli_toteutus_owner_koulutusmoduuli_toteutus_id_owneroid_ownertype";

alter table "koulutusmoduuli_toteutus_owner"
add constraint "koulutusmoduuli_toteutus_owners_pk" PRIMARY KEY ("id");
