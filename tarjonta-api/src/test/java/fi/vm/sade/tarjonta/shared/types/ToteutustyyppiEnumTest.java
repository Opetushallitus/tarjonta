package fi.vm.sade.tarjonta.shared.types;

import org.junit.Test;

import java.util.EnumSet;
import java.util.Set;

import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.LUKIOKOULUTUS;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.PERUSOPETUKSEN_LISAOPETUS;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS;
import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.VAPAAN_SIVISTYSTYON_KOULUTUS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ToteutustyyppiEnumTest {

    @Test
    public void containsCorrectEntriesForSecondarySchoolTypes() throws Exception {
        Set<ToteutustyyppiEnum> secondarySchoolTypes = EnumSet.of(
                AMMATILLINEN_PERUSTUTKINTO,
                LUKIOKOULUTUS,
                PERUSOPETUKSEN_LISAOPETUS,
                AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS,
                MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS,
                MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS,
                VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS,
                VAPAAN_SIVISTYSTYON_KOULUTUS,
                AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA);

        for (ToteutustyyppiEnum entry : ToteutustyyppiEnum.values()) {
            if (secondarySchoolTypes.contains(entry)) {
                assertTrue(entry + " was expected to be marked as secondary school (toisen asteen koulutus) but isn't", entry.isToisenAsteenKoulutus());
            } else {
                assertFalse(entry + " was expected to NOT be marked as secondary school (toisen asteen koulutus) but is", entry.isToisenAsteenKoulutus());
            }
        }
    }
}