
create table koulutusmoduuli_toteutus_owners (
    koulutusmoduuli_toteutus_id int8 not null,
    ownerOid varchar(255),
    ownerType varchar(255)
);

alter table koulutusmoduuli_toteutus_owners
    add constraint FKE812D75E2566EBFA
    foreign key (koulutusmoduuli_toteutus_id)
    references koulutusmoduuli_toteutus;
