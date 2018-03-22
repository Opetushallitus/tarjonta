-- OK-180 Reformin minimihäntä (osa 2)


-- Poistetaan aikuiskoulutus-koulutuslaji vuoden 2018 koulutuksilta (myös muilta kuin julkaistuilta)
DELETE FROM koulutusmoduuli_toteutus_koulutuslaji
WHERE koulutusmoduuli_toteutus_id in
      (SELECT kola.koulutusmoduuli_toteutus_id FROM koulutusmoduuli_toteutus_koulutuslaji as kola
        join koulutusmoduuli_toteutus as komoto on kola.koulutusmoduuli_toteutus_id = komoto.id
        where komoto.alkamisvuosi = 2018
              and kola.koodi_uri like 'koulutuslaji_a#%');
