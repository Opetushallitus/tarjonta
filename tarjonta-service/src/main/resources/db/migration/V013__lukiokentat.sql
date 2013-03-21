create table kielivalikoima (
        id int8 not null unique,
        version int8 not null,
        key varchar(255),
        primary key (id)
);

create table kielivalikoima_kieli (
        kielivalikoima_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (kielivalikoima_id, koodi_uri)
);

create table koulutusmoduuli_toteutus_kielivalikoima (
        koulutusmoduuli_toteutus_id int8 not null,
        tarjotutKielet_id int8 not null,
        primary key (koulutusmoduuli_toteutus_id, tarjotutKielet_id),
        unique (tarjotutKielet_id)
);

create table koulutusmoduuli_toteutus_lukiodiplomi (
        koulutusmoduuli_toteutus_id int8 not null,
        koodi_uri varchar(255) not null,
        primary key (koulutusmoduuli_toteutus_id, koodi_uri)
);

alter table koulutusmoduuli_toteutus_kielivalikoima 
        add constraint FK67BD36AB2566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;

alter table koulutusmoduuli_toteutus_kielivalikoima 
        add constraint FK67BD36ABFF05FFD9 
        foreign key (tarjotutKielet_id) 
        references kielivalikoima;
        
alter table koulutusmoduuli_toteutus_lukiodiplomi 
        add constraint FK769F54802566EBFA 
        foreign key (koulutusmoduuli_toteutus_id) 
        references koulutusmoduuli_toteutus;
