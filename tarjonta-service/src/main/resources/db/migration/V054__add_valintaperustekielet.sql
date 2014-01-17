 create table hakukohde_sora_kielet (
        hakukohde_id int8 not null,
        soraKuvausKielet varchar(255)
    );


  alter table hakukohde_sora_kielet
        add constraint FK7459832B1822B1EB
        foreign key (hakukohde_id)
        references hakukohde;

  create table hakukohde_valintaperuste_kielet (
        hakukohde_id int8 not null,
        valintaPerusteKuvausKielet varchar(255)
    );


      alter table hakukohde_valintaperuste_kielet
        add constraint FKB768515D1822B1EB
        foreign key (hakukohde_id)
        references hakukohde;