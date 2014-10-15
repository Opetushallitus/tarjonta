alter table hakuaika add column nimi_teksti_id int8;
alter table monikielinen_teksti add column hakuaika_id int8 default null;

insert into monikielinen_teksti(id, version, hakuaika_id)
select nextval('hibernate_sequence'), 0, id
from hakuaika;

insert into teksti_kaannos(id, version, arvo, kieli_koodi, teksti_id)
select nextval('hibernate_sequence'), 0, sisaisenhakuajannimi, 'kieli_fi', monikielinen_teksti.id
from hakuaika, monikielinen_teksti
where hakuaika.id = monikielinen_teksti.hakuaika_id;

update teksti_kaannos
set arvo = ''
from monikielinen_teksti
where monikielinen_teksti.hakuaika_id is not null
and teksti_kaannos.arvo is null
and teksti_kaannos.teksti_id = monikielinen_teksti.id;

update hakuaika
set nimi_teksti_id = monikielinen_teksti.id
from monikielinen_teksti
where monikielinen_teksti.hakuaika_id = hakuaika.id;

alter table hakuaika
add constraint nimi_monikielinen_teksti_constraint
foreign key (nimi_teksti_id)
references monikielinen_teksti;

alter table monikielinen_teksti drop column hakuaika_id;
alter table hakuaika drop column sisaisenhakuajannimi;