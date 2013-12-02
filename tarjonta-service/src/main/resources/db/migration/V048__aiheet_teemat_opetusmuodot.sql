create table koulutusmoduuli_toteutus_opetusaika (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
    );

    alter table koulutusmoduuli_toteutus_opetusaika
        add constraint FKD3FC0DAA2566EBFA
        foreign key (koulutusmoduuli_toteutus_id)
        references koulutusmoduuli_toteutus;


  create table koulutusmoduuli_toteutus_opetuspaikka (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
    );


 alter table koulutusmoduuli_toteutus_opetuspaikka
        add constraint FKDE568A952566EBFA
        foreign key (koulutusmoduuli_toteutus_id)
        references koulutusmoduuli_toteutus;


 create table koulutusmoduuli_toteutus_aihe (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
    );


 alter table koulutusmoduuli_toteutus_aihe
        add constraint FK5575D9E32566EBFA
        foreign key (koulutusmoduuli_toteutus_id)
        references koulutusmoduuli_toteutus;