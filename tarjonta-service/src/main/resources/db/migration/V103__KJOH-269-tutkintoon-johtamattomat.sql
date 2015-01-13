
alter table koulutusmoduuli_toteutus add column opinnontyyppi_uri varchar(255) NULL;
alter table koulutusmoduuli_toteutus add column koulutuksenloppumispvm timestamp;
alter table koulutusmoduuli_toteutus add column opintokokonaisuus_id bigint NULL;
alter table koulutusmoduuli_toteutus add column oppiaine varchar(255) NULL;
alter table koulutusmoduuli_toteutus add column opettaja varchar(255) NULL;
alter table koulutusmoduuli_toteutus add column yhteyshenkilo_nimi varchar(255) NULL;
alter table koulutusmoduuli_toteutus add column yhteyshenkilo_puhelin varchar(255) NULL;
alter table koulutusmoduuli_toteutus add column yhteyshenkilo_titteli varchar(255) NULL;
alter table koulutusmoduuli_toteutus add column yhteyshenkilo_email varchar(255) NULL;

alter table koulutusmoduuli_toteutus 
  add constraint FKOPINTOKOKONAISUUS
  foreign key (opintokokonaisuus_id)
  references koulutusmoduuli_toteutus (id);


create table koulutusmoduuli_toteutus_koulutusryhma (
  koulutusmoduuli_toteutus_id bigint not null, 
  koulutusryhma_oid varchar(255)
);

alter table koulutusmoduuli_toteutus_koulutusryhma 
  add constraint FK6659635F2566EBFA 
  foreign key (koulutusmoduuli_toteutus_id) 
  references koulutusmoduuli_toteutus;


alter table hakukohde add column hakumenettely_teksti_id bigint;
alter table hakukohde add column peruutusehdot_teksti_id bigint;

alter table hakukohde 
  add constraint FK8218B8C29167513A 
  foreign key (hakumenettely_teksti_id)
  references monikielinen_teksti (id);

alter table hakukohde 
  add constraint FK8218B8C26951CE64
  foreign key (peruutusehdot_teksti_id)
  references monikielinen_teksti (id);
