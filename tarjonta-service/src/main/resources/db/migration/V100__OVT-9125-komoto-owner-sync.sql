/**
owner-tauluun oli jäänyt virheellinen tarjoaja-tieto, koska tarjonnassa olleen puutteen takia
tietoa ei päivitetty owner-tauluun esim. kun organisaatio siirrettiin toiselle organisaatiolle.
Tämän seurauksena koulutus indeksoiutui esim. Solriin molempiin organisaatioihin (komoto taulun
tarjoajalle ja lisäksi myös owner-taulusta löytyvälle tarjoajalle (joka oli vanhentunut tieto).

Tämä kysely korjaa tiedon oikeaksi niille koulutuksille, joille väärä tieto oli jäänyt (~9 koulutusta).

Tarjontaan tehtiin nyt myös korjaus, joka:
  1. Luo tarjoajatiedot owner-tauluun myös muille kun kk-koulutuksille
  2. Pitää owner-taulun tiedot synkassa esim. siirron yhteydessä
 */
update koulutusmoduuli_toteutus_owner as o
set owneroid = t.tarjoaja
from koulutusmoduuli_toteutus as t
where
	t.id = o.koulutusmoduuli_toteutus_id
	and o.ownertype = 'TARJOAJA'
	and t.toteutustyyppi != 'KORKEAKOULUTUS'
	and t.tarjoaja != o.owneroid;


/**
Tämän kyseisen bugin takia ei owner-tauluun ole lisätty rivejä kun uusia koulutuksia on luotu. Tämä korjaa asian.
 */
INSERT INTO koulutusmoduuli_toteutus_owner (id, version, koulutusmoduuli_toteutus_id, owneroid, ownertype)
SELECT nextval('hibernate_sequence'), 0, t.id AS koulutusmoduuli_toteutus_id, t.tarjoaja AS owneroid, 'TARJOAJA'
FROM koulutusmoduuli_toteutus as t
LEFT JOIN koulutusmoduuli_toteutus_owner as o on (o.koulutusmoduuli_toteutus_id = t.id AND o.ownertype = 'TARJOAJA')
WHERE o.id IS NULL