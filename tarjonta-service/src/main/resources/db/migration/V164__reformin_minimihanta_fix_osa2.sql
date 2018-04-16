-- BUG-1713 Reformin minimihäntä bugfix


-- Aikuiskoulutus-koulutuslajia ei olisi pitänyt poistaa muilta koulutustyypeiltä
-- kuin ammattitutkinto, erikoisammattitutkinto, ammatillinen perustutkinto.
-- Lisätään se takaisin niille toteutustyypeille, joilla ei nyt ole koulutuslajia,
-- se on pakollinen tieto, ja aikuiskoulutus on yksi sallituista koulutuslajeista.

-- Kuvatunlaiset koulutustyypit ja niitä vastaavat toteutustyypit ovat:
-- Perusopetuksen lisäopetus (6)                                                    PERUSOPETUKSEN_LISAOPETUS
-- Ammatilliseen peruskoulutukseen ohjaava ja valmistava koulutus (7)               AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS
-- Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus (8)         MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS
-- Maahanmuuttajien ja vieraskielisten lukiokoulutukseen valmistava koulutus (9)    MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS
-- Vapaan sivistystyön koulutus (10)                                                VAPAAN_SIVISTYSTYON_KOULUTUS

-- Näiden DTO-tyyppi on ValmistavaKoulutusV1RDTO, jolle koulutuslajis ei ole nullable, eli on pakollinen tieto.
-- myös VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS on tällä DTO:lla, mutta se ei voi olla aikuiskoulutus.


insert into koulutusmoduuli_toteutus_koulutuslaji(koulutusmoduuli_toteutus_id, koodi_uri)
values(
  unnest((SELECT ARRAY(SELECT komoto.id FROM koulutusmoduuli_toteutus as komoto
  where komoto.alkamisvuosi = 2018
        and komoto.toteutustyyppi in ('PERUSOPETUKSEN_LISAOPETUS',
                                      'AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS',
                                      'MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                                      'MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS',
                                      'VAPAAN_SIVISTYSTYON_KOULUTUS')
        and komoto.koulutustyyppi_uri is not null
        and komoto.id not in (select kola.koulutusmoduuli_toteutus_id from koulutusmoduuli_toteutus_koulutuslaji as kola)
  ))),
  'koulutuslaji_a#1'
);
