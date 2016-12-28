create table koulutusmoduuli_toteutus_koulutuksenlaajuus (
      koulutusmoduuli_toteutus_id int8 not null,
      koodi_uri varchar(255) not null,
      primary key (koulutusmoduuli_toteutus_id, koodi_uri)
);