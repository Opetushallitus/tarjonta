package fi.vm.sade.tarjonta.shared.types;

import static fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum.*;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ToteutustyyppiEnumTest {

  @Test
  public void containsCorrectEntriesForSecondarySchoolTypes() throws Exception {
    Set<ToteutustyyppiEnum> secondarySchoolTypes =
        EnumSet.of(
            AMMATILLINEN_PERUSTUTKINTO,
            AMMATILLINEN_PERUSTUTKINTO_ALK_2018,
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
        assertTrue(
            entry
                + " was expected to be marked as secondary school (toisen asteen koulutus) but isn't",
            entry.isToisenAsteenKoulutus());
      } else {
        assertFalse(
            entry
                + " was expected to NOT be marked as secondary school (toisen asteen koulutus) but is",
            entry.isToisenAsteenKoulutus());
      }
    }
  }
}
