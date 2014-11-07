/* KJOH-926 Lisää puuttuvat foreign keyt */
alter table hakukohde_painotettavaoppiaine add constraint FKDF3EF5D81822B1EB foreign key (hakukohde_id) references hakukohde;
alter table kielivalikoima_kieli add constraint FKF2B0D53250DC4DC9 foreign key (kielivalikoima_id) references kielivalikoima;
alter table valintakoe add constraint FKF7AE1AEEFD02C7D foreign key (kuvaus_monikielinenteksti_id) references monikielinen_teksti;
alter table valintakoe add constraint FKF7AE1AE1822B1EB foreign key (hakukohde_id) references hakukohde;
alter table valintakoe_ajankohta add constraint FK58FC234AC5B6C9A9 foreign key (valintakoe_id) references valintakoe;
