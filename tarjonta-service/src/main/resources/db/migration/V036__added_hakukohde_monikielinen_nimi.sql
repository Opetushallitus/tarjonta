ALTER TABLE hakukohde ADD COLUMN hakukohde_monikielinen_nimi_id int8;

alter table hakukohde 
        add constraint FK8218B8C275A3305E 
        foreign key (hakukohde_monikielinen_nimi_id) 
        references monikielinen_teksti;


ALTER TABLE hakukohdeliite ADD COLUMN kieli varchar(255);

ALTER TABLE valintakoe ADD COLUMN kieli varchar(255);