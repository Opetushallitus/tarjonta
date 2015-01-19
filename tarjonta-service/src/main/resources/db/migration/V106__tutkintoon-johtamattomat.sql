alter table koulutusmoduuli_toteutus add column tarjoajan_koulutus_id bigint NULL;

alter table koulutusmoduuli_toteutus
  add constraint FKTARJOAJAN_KOULUTUS
  foreign key (tarjoajan_koulutus_id)
  references koulutusmoduuli_toteutus (id);