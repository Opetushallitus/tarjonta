package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

public class KoulutusasteResolver {

    public static boolean isToisenAsteenKoulutus(ToteutustyyppiEnum toteutustyyppi) {
        return ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO.equals(toteutustyyppi) ||
                ToteutustyyppiEnum.LUKIOKOULUTUS.equals(toteutustyyppi) ||
                ToteutustyyppiEnum.PERUSOPETUKSEN_LISAOPETUS.equals(toteutustyyppi) ||
                ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS.equals(toteutustyyppi) ||
                ToteutustyyppiEnum.MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS.equals(toteutustyyppi) ||
                ToteutustyyppiEnum.MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS.equals(toteutustyyppi) ||
                ToteutustyyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS.equals(toteutustyyppi) ||
                ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS.equals(toteutustyyppi) ||
                ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA.equals(toteutustyyppi);
    }
}
