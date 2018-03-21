-- OK-180 Reformin minimihäntä


-- Poistetaan näyttötutkinnon järjestäjä vuoden 2018 koulutuksilta
update koulutusmoduuli_toteutus set jarjesteja = null
where alkamisvuosi = 2018 and jarjesteja is not null;


-- Poistetaan sisältyvä koulutus vuoden 2018 koulutuksilta
update koulutusmoduuli_toteutus set koulutusmoduuli_toteutus_children_id = null
where alkamisvuosi = 2018 and koulutusmoduuli_toteutus_children_id is not null;


-- Poistetaan aikuiskoulutus-koulutuslaji vuoden 2018 julkaistuilta koulutuksilta
DELETE FROM koulutusmoduuli_toteutus_koulutuslaji
WHERE koulutusmoduuli_toteutus_id in
      (SELECT kola.koulutusmoduuli_toteutus_id FROM koulutusmoduuli_toteutus_koulutuslaji as kola
        join koulutusmoduuli_toteutus as komoto on kola.koulutusmoduuli_toteutus_id = komoto.id
        where komoto.alkamisvuosi = 2018
              and kola.koodi_uri like 'koulutuslaji_a#%'
              and komoto.tila = 'JULKAISTU');
