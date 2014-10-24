alter table monikielinen_teksti add column hakukohde_id int8 default null;

insert into monikielinen_teksti(id, version, hakukohde_id)
select nextval('hibernate_sequence'), 0, hakukohde.id
from hakukohde
left join koulutus_hakukohde on koulutus_hakukohde.hakukohde_id = hakukohde.id
left join koulutusmoduuli_toteutus on koulutusmoduuli_toteutus.id = koulutus_hakukohde.koulutus_id
where
koulutusmoduuli_toteutus.toteutustyyppi = 'KORKEAKOULUTUS'
group by hakukohde.id;

insert into teksti_kaannos(id, version, arvo, kieli_koodi, teksti_id)
select nextval('hibernate_sequence'), 0, aloituspaikat_lkm, 'kieli_fi', monikielinen_teksti.id
from hakukohde, monikielinen_teksti
where hakukohde.id = monikielinen_teksti.hakukohde_id;

update hakukohde
set aloituspaikat_teksti_id = monikielinen_teksti.id
from monikielinen_teksti
where monikielinen_teksti.hakukohde_id = hakukohde.id;

alter table monikielinen_teksti drop column hakukohde_id;