    create table painotettavaoppiaine (
        id int8 not null unique,
        version int8 not null,
        oppiaine varchar(255),
        painokerroin int4,
        primary key (id)
    );
	
	create table hakukohde_painotettavaoppiaine (
        hakukohde_id int8 not null,
        painotettavatOppiaineet_id int8 not null,
        primary key (hakukohde_id, painotettavatOppiaineet_id),
        unique (painotettavatOppiaineet_id)
    );
	
	    alter table hakukohde_painotettavaoppiaine 
        add constraint FKDF3EF5D8147B34EA 
        foreign key (painotettavatOppiaineet_id) 
        references painotettavaoppiaine;
	
	
	ALTER TABLE hakukohde ADD COLUMN alinHyvaksyttavaKeskiarvo float8;
	
	ALTER TABLE valintakoe ADD COLUMN lisanaytot_monikielinenteksti_id int8;
	
	 alter table valintakoe 
        add constraint FKF7AE1AE7BE3A608 
        foreign key (lisanaytot_monikielinenteksti_id) 
        references monikielinen_teksti;
	
	create table pisteraja (
        id int8 not null unique,
        version int8 not null,
        alinHyvaksyttyPistemaara int4,
        alinPistemaara int4,
        valinnanPisterajaTyyppi varchar(255),
        ylinPistemaara int4,
        primary key (id)
    );
	
	 create table valintakoe_pisteraja (
        valintakoe_id int8 not null,
        pisterajat_id int8 not null,
        primary key (valintakoe_id, pisterajat_id),
        unique (pisterajat_id)
    );
	

    alter table valintakoe_pisteraja 
        add constraint FK63376A60BFB79B19 
        foreign key (pisterajat_id) 
        references pisteraja;

    alter table valintakoe_pisteraja 
        add constraint FK63376A60C5B6C9A9 
        foreign key (valintakoe_id) 
        references valintakoe;