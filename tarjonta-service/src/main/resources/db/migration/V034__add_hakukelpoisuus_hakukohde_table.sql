 create table hakukohde_hakukelpoisuusvaatimus (
        hakukohde_id int8 not null,
        hakukelpoisuusVaatimukset varchar(255)
    );

alter table hakukohde_hakukelpoisuusvaatimus
        add constraint FK122B9C981822B1EB
        foreign key (hakukohde_id)
        references hakukohde;

ALTER TABLE hakukohde DROP COLUMN hakukelpoisuusvaatimus;