create table koulutusmoduuli_toteutus_tarjoajatiedot (
    id int8 not null unique,
    version int8 not null,
    primary key (id)
);

create table hakukohde_koulutusmoduuli_toteutus_tarjoajatiedot (
    id int8 not null unique,
    version int8 not null,
    hakukohde_id int8 not null,
    koulutusmoduuli_toteutus_tarjoajatiedot_id int8 not null,
    koulutusmoduuli_toteutus_oid varchar(255) not null,
    constraint hakukohde_koulutusmoduuli_toteutus_tarjoajatiedot_pkey primary key (hakukohde_id, koulutusmoduuli_toteutus_oid),
    constraint fk59d7f1112566ebfa foreign key (hakukohde_id) references hakukohde (id),
    constraint fk59d7f241d81ff18c foreign key (koulutusmoduuli_toteutus_tarjoajatiedot_id) references koulutusmoduuli_toteutus_tarjoajatiedot (id)
);

create table koulutusmoduuli_toteutus_tarjoajatiedot_tarjoaja_oid (
  koulutusmoduuli_toteutus_tarjoajatiedot_id int8 not null,
  tarjoaja_oid varchar(255)
);

alter table koulutusmoduuli_toteutus_tarjoajatiedot_tarjoaja_oid
    add constraint koulutusmoduuli_toteutus_tarjoajatiedot_tarjoaja_oid_koulutusmoduuli_toteutus_tarjoajatiedot
    foreign key (koulutusmoduuli_toteutus_tarjoajatiedot_id)
    references koulutusmoduuli_toteutus_tarjoajatiedot;
