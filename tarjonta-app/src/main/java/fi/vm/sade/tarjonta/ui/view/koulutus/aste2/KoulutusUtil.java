package fi.vm.sade.tarjonta.ui.view.koulutus.aste2;

import java.util.Set;

import com.google.common.collect.Sets;

import fi.vm.sade.tarjonta.ui.enums.Koulutustyyppi;

public class KoulutusUtil {

    private static final Set<String> valmentavatTyypit = Sets
            .newHashSet(Koulutustyyppi.PERUSOPETUKSEN_LISAOPETUS
                    .getKoulutustyyppiUri(),
                    Koulutustyyppi.AMMATILLISEEN_OHJAAVA_KOULUTUS
                            .getKoulutustyyppiUri(),
                    Koulutustyyppi.MAMU_AMMATILLISEEN_OHJAAVA_KOULUTUS
                            .getKoulutustyyppiUri(),
                    Koulutustyyppi.MAMU_LUKIOON_OHJAAVA_KOULUTUS
                            .getKoulutustyyppiUri(),
                    Koulutustyyppi.TOINEN_ASTE_VALMENTAVA_KOULUTUS
                            .getKoulutustyyppiUri(),
                    Koulutustyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS
                            .getKoulutustyyppiUri());

    /**
     * Return true jos koulutus on pervako
     * 
     * @param uri
     * @return
     */
    public static boolean isPervako(String uri) {
        return uri != null && valmentavatTyypit.contains(uri);
    }

    /**
     * Return true jos koulutus on valmentava ja kuntouttava
     * 
     * @param uri
     * @return
     */
    public static boolean isValmentavaJaKuntouttava(String uri) {
        return uri != null && Koulutustyyppi.TOINEN_ASTE_VALMENTAVA_KOULUTUS.getKoulutustyyppiUri().equals(uri);
    }
}
