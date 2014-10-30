/*
Tarjonta-service bugin takia (katso commit 391250f61ed9e92adf4960814b3fea4066a313a7)
tiettyjen koulutustyyppien kopiointi asetti väärän koulutustyypin kopioidulle koulutukselle.
Alla olevat queryt korjaavat tilanteen.
*/

update koulutusmoduuli_toteutus
set toteutustyyppi = 'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA'
where id IN(
    select koulutusmoduuli_toteutus_children_id as id
    from koulutusmoduuli as m
    join koulutusmoduuli_toteutus as t on t.koulutusmoduuli_id = m.id
    where m.koulutustyyppi = 'AMMATILLINEN_PERUSKOULUTUS'
    and t.toteutustyyppi = 'ERIKOISAMMATTITUTKINTO'
    and t.tila != 'POISTETTU'
);

update koulutusmoduuli_toteutus
set toteutustyyppi = 'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA'
where id IN(
    select t.id
    from koulutusmoduuli as m
    join koulutusmoduuli_toteutus as t on t.koulutusmoduuli_id = m.id
    where m.koulutustyyppi = 'AMMATILLINEN_PERUSKOULUTUS'
    and t.toteutustyyppi = 'ERIKOISAMMATTITUTKINTO'
    and t.tila != 'POISTETTU'
);

/**/

update koulutusmoduuli_toteutus
set toteutustyyppi = 'ERIKOISAMMATTITUTKINTO'
where id IN(
    select t.id
    from koulutusmoduuli as m
    join koulutusmoduuli_toteutus as t on t.koulutusmoduuli_id = m.id
    where m.koulutustyyppi = 'ERIKOISAMMATTITUTKINTO'
    and t.toteutustyyppi = 'AMMATTITUTKINTO'
    and t.tila != 'POISTETTU'
);

/**/

update koulutusmoduuli_toteutus
/* Pitäisi varmaankin olla AMMATTITUTKINTO_VALMISTAVA, mutta tällä hetkellä jos luo ammattitutkintoa
valmistavan koulutuksen tyypiksi tulee ERIKOISAMMATTITUTKINTO_VALMISTAVA. Tämä on todennäköisesti bugi,
joka pitää mahdollisesti korjata tulevaisuudessa (tietääkseni tämä on tällä hetkellä lähinnä semanttinen
ongelma eikä näy ulospäin) */
set toteutustyyppi = 'ERIKOISAMMATTITUTKINTO_VALMISTAVA'
where id IN(
    select koulutusmoduuli_toteutus_children_id as id
    from koulutusmoduuli as m
    join koulutusmoduuli_toteutus as t on t.koulutusmoduuli_id = m.id
    where m.koulutustyyppi = 'AMMATTITUTKINTO'
    and t.toteutustyyppi = 'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA'
    and t.tila != 'POISTETTU'
);

update koulutusmoduuli_toteutus
set toteutustyyppi = 'AMMATTITUTKINTO'
where id IN(
    select t.id
    from koulutusmoduuli as m
    join koulutusmoduuli_toteutus as t on t.koulutusmoduuli_id = m.id
    where m.koulutustyyppi = 'AMMATTITUTKINTO'
    and t.toteutustyyppi = 'AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA'
    and t.tila != 'POISTETTU'
);
