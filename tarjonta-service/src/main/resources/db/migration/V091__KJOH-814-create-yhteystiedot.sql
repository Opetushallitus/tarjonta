create table yhteystiedot (
    id int8 not null unique,
    version int8 not null,
    osoiterivi1 varchar(255),
    osoiterivi2 varchar(255),
    postinumero varchar(255),
    postitoimipaikka varchar(255),
    hakukohde_id int8,
    primary key (id)
);

alter table yhteystiedot 
    add constraint FK31A724C7EF154D1 
    foreign key (hakukohde_id) 
    references hakukohde;
