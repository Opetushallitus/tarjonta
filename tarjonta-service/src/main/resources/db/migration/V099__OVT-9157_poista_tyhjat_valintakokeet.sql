/*
Poistaa tyhjät valintakokeet, näitä on tallennettu kantaan muinoin.
Lukiohin ei kuitenkaan kosketa, koska niillä on pisterajatiedot, jotka
sisältyvät tyhjiin valintakokeisiin.
*/

delete from valintakoe
using hakukohde, koulutus_hakukohde, koulutusmoduuli_toteutus
where
hakukohde.id = valintakoe.hakukohde_id and
koulutus_hakukohde.hakukohde_id = hakukohde.id and
koulutus_hakukohde.koulutus_id = koulutusmoduuli_toteutus.id and
valintakoe.valintakoe_nimi is null and valintakoe.tyyppiuri is null and
koulutusmoduuli_toteutus.toteutustyyppi != 'LUKIOKOULUTUS';