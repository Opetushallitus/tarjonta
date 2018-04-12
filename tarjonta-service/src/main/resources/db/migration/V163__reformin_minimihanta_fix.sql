-- BUG-1713 Reformin minimihäntä bugfix


-- Aikuiskoulutus-koulutuslajia ei olisi pitänyt poistaa muilta koulutustyypeiltä
-- kuin ammattitutkinto, erikoisammattitutkinto, ammatillinen perustutkinto.
-- Lisätään se takaisin niille toteutustyypeille, joille se on yksiselitteinen (koulutuslaji
-- on vaadittu ja voi olla vain aikuiskoulutus), ja joilla sitä ei nyt ole.

-- Kuvatunlaiset koulutustyypit ja niitä vastaavat toteutustyypit ovat:
-- Aikuisten lukiokoulutus (14)                                                       LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA
-- Aikuisten perusopetus (17)                                                         AIKUISTEN_PERUSOPETUS
-- Ammatilliseen peruskoulutukseen valmentava koulutus (VALMA) (18)                   AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA
-- Ammatilliseen peruskoulutukseen valmentava koulutus (VALMA) erityisopetuksena (19) AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA

insert into koulutusmoduuli_toteutus_koulutuslaji(koulutusmoduuli_toteutus_id, koodi_uri)
    with komoto_ids_array as (SELECT ARRAY(SELECT komoto.id FROM koulutusmoduuli_toteutus as komoto
          where komoto.alkamisvuosi = 2018
              and komoto.toteutustyyppi in ('LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA',
                                            'AIKUISTEN_PERUSOPETUS',
                                            'AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA')
              and komoto.koulutustyyppi_uri is not null
              and komoto.id not in (select kola.koulutusmoduuli_toteutus_id from koulutusmoduuli_toteutus_koulutuslaji as kola)
    ))
    values(unnest(komoto_ids_array), 'koulutuslaji_a#1');