create table ryhmaliitos (
    id int8 not null unique,
    version int8 not null,
    hakukohde_id int8 not null,
    ryhma_oid varchar(255) not null,
    prioriteetti int4,
    primary key (id)
);

alter table ryhmaliitos
    add constraint ryhmaliitos_hakukohde
    foreign key (hakukohde_id) 
    references hakukohde;
