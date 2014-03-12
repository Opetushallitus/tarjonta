    create table nodeclass (
        id int8 not null unique,
        version int8 not null,
        classcode varchar(255),
        description varchar(255),
        nodevalue varchar(255) not null,
        primary key (id),
        unique (nodevalue)
    );

    create table oid (
        oid_value int8 not null,
        checkDigit int4 not null,
        node varchar(255) not null,
        primary key (oid_value)
    );

    create table oid_base_data (
        id int8 not null unique,
        version int8 not null,
        key varchar(255),
        value varchar(255),
        primary key (id),
        unique (key)
    );
    
    insert into oid_base_data (id,version,key,value) values (0,0,'rootNode','1.2.246.562')