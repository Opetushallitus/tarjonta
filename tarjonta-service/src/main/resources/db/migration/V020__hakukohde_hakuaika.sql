alter table hakukohde add column hakuaika_id int8;

alter table hakukohde 
    add constraint FK8218B8C2EFA7089 
    foreign key (hakuaika_id) 
    references hakuaika;