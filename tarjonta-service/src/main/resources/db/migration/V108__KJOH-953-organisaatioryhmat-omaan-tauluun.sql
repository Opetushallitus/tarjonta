insert into ryhmaliitos (id, version, hakukohde_id, ryhma_oid)
select nextval('hibernate_sequence'), 0, id, regexp_split_to_table(organisaatioryhmaoids, ',') as ryhma_oid
from hakukohde
where organisaatioRyhmaOids is not null;