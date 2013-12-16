   create table valintaperuste_sora_kuvaus (
        id int8 not null unique,
        version int8 not null,
        organisaatio_tyyppi varchar(255),
        tyyppi int4,
        monikielinen_nimi_id int8,
        primary key (id)
    );

      create table valintaperuste_sora_kuvaus_monikielinen_metadata (
        valintaperuste_sora_kuvaus_id int8 not null,
        tekstis_id int8 not null,
        unique (tekstis_id)
    );


      alter table valintaperuste_sora_kuvaus
        add constraint FKBCB5B8C17C7E6101
        foreign key (monikielinen_nimi_id)
        references monikielinen_teksti;

          alter table valintaperuste_sora_kuvaus_monikielinen_metadata
        add constraint FK1EDD0B2098A3D2E3
        foreign key (tekstis_id)
        references monikielinen_metadata;

    alter table valintaperuste_sora_kuvaus_monikielinen_metadata
        add constraint FK1EDD0B20D1BDF6DF
        foreign key (valintaperuste_sora_kuvaus_id)
        references valintaperuste_sora_kuvaus;