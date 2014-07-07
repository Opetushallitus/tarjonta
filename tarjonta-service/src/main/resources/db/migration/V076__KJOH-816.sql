create table massakopiointi (
    id int8 not null unique,
    version int8 not null,
    haku_oid varchar(255) not null,
    oid varchar(255) not null,
    content_type varchar(255) not null,
    json text not null,
    meta text not null,
    tila varchar(32) not null,
    updated timestamp,
    created timestamp,  
    primary key (haku_oid, oid )
);