DROP TABLE valintakoe_osoite;

DROP TABLE valintakoe_ajankohta;

DROP TABLE valintakoe;

create table valintakoe (
        id int8 not null unique,
        version int8 not null,
        tyyppiUri varchar(255),
        kuvaus_monikielinenteksti_id int8,
        hakukohde_id int8,
        primary key (id)
    );

    create table valintakoe_ajankohta (
        id int8 not null unique,
        version int8 not null,
        osoiterivi1 varchar(255),
        osoiterivi2 varchar(255),
        postinumero varchar(255),
        postitoimipaikka varchar(255),
        alkamisaika timestamp,
        lisatietoja varchar(255),
        paattymisaika timestamp,
        valintakoe_id int8,
        primary key (id)
    );
