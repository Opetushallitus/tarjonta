update hakukohde
set viimindeksointipvm = null
from koulutus_hakukohde, koulutusmoduuli_toteutus
where
koulutus_hakukohde.hakukohde_id = hakukohde.id
and koulutus_hakukohde.koulutus_id = koulutusmoduuli_toteutus.id
and koulutusmoduuli_toteutus.toteutustyyppi = 'KORKEAKOULUTUS';
