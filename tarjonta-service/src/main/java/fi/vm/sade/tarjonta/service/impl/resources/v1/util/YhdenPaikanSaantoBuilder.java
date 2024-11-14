package fi.vm.sade.tarjonta.service.impl.resources.v1.util;

import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO.YhdenPaikanSaanto;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YhdenPaikanSaantoBuilder {

  private static final String JATKOTUTKINTOHAKU_URI = "haunkohdejoukontarkenne_3#";
  private static final List<String> TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO =
      Collections.singletonList(JATKOTUTKINTOHAKU_URI);
  private static final String KAUSI_KEVAT = "kausi_k";

  @Autowired private TarjontaKoodistoHelper tarjontaKoodistoHelper;

  public static YhdenPaikanSaanto from(Haku haku) {
    if (!haku.isKorkeakouluHaku()) {
      return new YhdenPaikanSaanto(false, "Ei korkeakouluhaku");
    }
    if (!KoulutusmoduuliTyyppi.TUTKINTO.equals(haku.getKoulutusmoduuliTyyppi())) {
      return new YhdenPaikanSaanto(false, "Haun koulutukset eivät ole tutkintoon johtavaa");
    }
    boolean hasAlkamisVuosiAndUri =
        haku.getKoulutuksenAlkamisVuosi() != null && haku.getKoulutuksenAlkamiskausiUri() != null;
    if (hasAlkamisVuosiAndUri) {
      boolean isBeforeSyksy2016 =
          haku.getKoulutuksenAlkamisVuosi() < 2016
              || (haku.getKoulutuksenAlkamisVuosi() == 2016
                  && haku.getKoulutuksenAlkamiskausiUri().startsWith(KAUSI_KEVAT));
      if (isBeforeSyksy2016) {
        return new YhdenPaikanSaanto(false, "Haun koulutuksen alkamiskausi on ennen syksyä 2016");
      }
    } else {
      return new YhdenPaikanSaanto(
          false, "Haulla ei ole koulutuksen alkamisvuotta tai alkamiskautta");
    }
    if (!haunKohdejoukontarkenneYPSYhteensopiva(haku)) {
      return new YhdenPaikanSaanto(
          false,
          String.format(
              "Haulla on kohdejoukon tarkenne, joka ei ole joukossa %s",
              TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO));
    }
    if (StringUtils.isBlank(haku.getKohdejoukonTarkenne())) {
      return new YhdenPaikanSaanto(true, "Korkeakouluhaku ilman kohdejoukon tarkennetta");
    } else {
      return new YhdenPaikanSaanto(
          true, String.format("Haun kohdejoukon tarkenne on '%s'", haku.getKohdejoukonTarkenne()));
    }
  }

  public YhdenPaikanSaanto from(Hakukohde hakukohde) {
    Haku haku = hakukohde.getHaku();
    YhdenPaikanSaanto ypsBasedOnHaku = from(haku);
    if (ypsBasedOnHaku.isVoimassa()) {
      return ypsBasedOnHaku;
    }
    if (!(haku.isKorkeakouluHaku()
        && haku.isJatkuva()
        && haunKohdejoukontarkenneYPSYhteensopiva(haku))) {
      return new YhdenPaikanSaanto(
          false,
          String.format(
              "%s ja hakukohde ei kuulu jatkuvaan korkeakouluhakuun, jonka kohdejoukon tarkenne kuuluu joukkoon %s tai sitä ei ole",
              ypsBasedOnHaku.getSyy(), TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO));
    }
    if (!tutkintoonJohtavaHakukohde(hakukohde)) {
      return new YhdenPaikanSaanto(
          false,
          String.format(
              "%s ja hakukohteen koulutus ei ole tutkintoon johtavaa", ypsBasedOnHaku.getSyy()));
    }
    int alkamisvuosi = hakukohde.getUniqueAlkamisVuosi();
    String alkamiskausi = hakukohde.getUniqueAlkamiskausiUri();
    boolean ennenSyksya2016 =
        alkamisvuosi < 2016 || (alkamisvuosi == 2016 && alkamiskausi.startsWith(KAUSI_KEVAT));
    if (ennenSyksya2016) {
      return new YhdenPaikanSaanto(
          false,
          String.format(
              "%s ja hakukohteen koulutuksen alkamiskausi on ennen syksyä 2016",
              ypsBasedOnHaku.getSyy()));
    }

    return new YhdenPaikanSaanto(
        true, "Jatkuvan haun hakukohteen alkamiskausi ja vuosi on jälkeen kevään 2016");
  }

  private boolean tutkintoonJohtavaHakukohde(Hakukohde hakukohde) {
    for (KoulutusmoduuliToteutus koulutus : hakukohde.getKoulutusmoduuliToteutuses()) {
      if (koulutus.getTila() != TarjontaTila.POISTETTU && koulutusJohtaaTutkintoon(koulutus)) {
        return true;
      }
    }
    return false;
  }

  public boolean koulutusJohtaaTutkintoon(KoulutusmoduuliToteutus koulutus)
      throws IllegalStateException {
    String koulutusUri = koulutus.getKoulutusUri();
    if (koulutusUri == null || koulutus.getKoulutusmoduuli() != null) {
      koulutusUri = koulutus.getKoulutusmoduuli().getKoulutusUri();
    }
    if (koulutusUri == null) {
      return false;
    }
    String tutkintoonjohtavuus =
        tarjontaKoodistoHelper.getUniqueKoodistoRelation(
            koulutusUri.split("#")[0],
            KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI,
            SuhteenTyyppiType.SISALTYY,
            false);
    if (tutkintoonjohtavuus == null) {
      throw new IllegalStateException(
          String.format(
              "Koulutus koodi %s ei ole relaatiossa koodiston %s kanssa",
              koulutus.getKoulutusUri(), KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI));
    }
    return tutkintoonjohtavuus.startsWith(KoodistoURI.KOODI_ON_TUTKINTO_URI);
  }

  private static boolean haunKohdejoukontarkenneYPSYhteensopiva(Haku haku) {
    if (StringUtils.isBlank(haku.getKohdejoukonTarkenne())) {
      return true;
    }
    for (String kohdejoukonTarkenne : TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO) {
      if (haku.getKohdejoukonTarkenne().startsWith(kohdejoukonTarkenne)) {
        return true;
      }
    }
    return false;
  }
}
