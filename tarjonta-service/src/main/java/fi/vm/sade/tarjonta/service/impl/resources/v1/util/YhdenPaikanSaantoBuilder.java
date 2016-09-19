package fi.vm.sade.tarjonta.service.impl.resources.v1.util;

import fi.vm.sade.tarjonta.model.BaseKoulutusmoduuli;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO.YhdenPaikanSaanto;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class YhdenPaikanSaantoBuilder {

    private static final String JATKOTUTKINTOHAKU_URI = "haunkohdejoukontarkenne_3#";
    private static final List<String> TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO = Collections.singletonList(JATKOTUTKINTOHAKU_URI);
    private static final List<TarjontaTila> IGNORE_KOULUTUS_STATES = Arrays.asList(TarjontaTila.LUONNOS, TarjontaTila.KOPIOITU);
    public static final String KAUSI_KEVAT = "kausi_k";

    public static YhdenPaikanSaanto from(Haku haku) {
        if (!haku.isKorkeakouluHaku()) {
            return new YhdenPaikanSaanto(false, "Ei korkeakouluhaku");
        }
        boolean hasAlkamisVuosiAndUri = haku.getKoulutuksenAlkamisVuosi() != null && haku.getKoulutuksenAlkamiskausiUri() != null;
        if (hasAlkamisVuosiAndUri) {
            boolean isBeforeSyksy2016 = haku.getKoulutuksenAlkamisVuosi() < 2016 ||
                    (haku.getKoulutuksenAlkamisVuosi() == 2016 && haku.getKoulutuksenAlkamiskausiUri().startsWith(KAUSI_KEVAT));
            if (isBeforeSyksy2016) {
                return new YhdenPaikanSaanto(false, "Haun koulutuksen alkamiskausi on ennen syksyä 2016");
            }
        } else {
            return new YhdenPaikanSaanto(false, "Haulla ei ole koulutuksen alkamisvuotta tai alkamiskautta");
        }
        if (!haunKohdejoukontarkenneYPSYhteensopiva(haku)) {
            return new YhdenPaikanSaanto(false, String.format("Haulla on kohdejoukon tarkenne, joka ei ole joukossa %s",
                    TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO));
        }
        if (StringUtils.isBlank(haku.getKohdejoukonTarkenne())) {
            return new YhdenPaikanSaanto(true, "Korkeakouluhaku ilman kohdejoukon tarkennetta");
        } else {
            return new YhdenPaikanSaanto(true, String.format("Haun kohdejoukon tarkenne on '%s'",
                    haku.getKohdejoukonTarkenne()));
        }
    }

    public static YhdenPaikanSaanto from(Hakukohde hakukohde) {
        Haku haku = hakukohde.getHaku();
        YhdenPaikanSaanto ypsBasedOnHaku = from(haku);
        if (ypsBasedOnHaku.isVoimassa()) {
            return ypsBasedOnHaku;
        }
        if (!(haku.isKorkeakouluHaku() && haku.isJatkuva() && haunKohdejoukontarkenneYPSYhteensopiva(haku))) {
            return new YhdenPaikanSaanto(false, String.format(
                    "%s ja hakukohde ei kuulu jatkuvaan korkeakouluhakuun, jonka kohdejoukon tarkenne kuuluu joukkoon %s tai sitä ei ole",
                    ypsBasedOnHaku.getSyy(),
                    TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO
            ));
        }
        List<KoulutusmoduuliToteutus> koulutukset = new ArrayList<>();
        for (KoulutusmoduuliToteutus koulutus : hakukohde.getKoulutusmoduuliToteutuses()) {
            if (!IGNORE_KOULUTUS_STATES.contains(koulutus.getTila()) &&
                    koulutus.getAlkamisVuosi() != null &&
                    koulutus.getAlkamiskausiUri() != null) {
                koulutukset.add(koulutus);
            }
        }
        if (koulutukset.isEmpty()) {
            return new YhdenPaikanSaanto(false, String.format(
                    "%s ja hakukohteella ei ole oikean tilaista koulutusta",
                    ypsBasedOnHaku.getSyy()
            ));
        }
        if (!uniqueKoulutuksenAlkamiskausi(koulutukset)) {
            List<String> koulutusOids = new ArrayList<>();
            for (KoulutusmoduuliToteutus koulutus : koulutukset) {
                koulutusOids.add(koulutus.getOid());
            }
            throw new IllegalStateException(String.format(
                    "Hakukohteen %s koulutusten %s koulutusten alkamiskaudet eivät ole yhtenevät.",
                    hakukohde.getOid(),
                    koulutusOids
            ));
        }
        KoulutusmoduuliToteutus koulutus = koulutukset.get(0);
        boolean ennenSyksya2016 = koulutus.getAlkamisVuosi() < 2016 ||
                (koulutus.getAlkamisVuosi() == 2016 && koulutus.getAlkamiskausiUri().startsWith(KAUSI_KEVAT));
        if (ennenSyksya2016) {
            return new YhdenPaikanSaanto(false, String.format(
                    "%s ja hakukohteen koulutuksen alkamiskausi on ennen syksyä 2016",
                    ypsBasedOnHaku.getSyy()
            ));
        }

        return new YhdenPaikanSaanto(true, "Jatkuvan haun hakukohteen alkamiskausi ja vuosi on jälkeen kevään 2016");
    }

    private static boolean haunKohdejoukontarkenneYPSYhteensopiva(Haku haku) {
        if (StringUtils.isBlank(haku.getKohdejoukonTarkenne())) {
            return true;
        }
        for (String kohdejoukonTarkenne: TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO) {
            if (haku.getKohdejoukonTarkenne().startsWith(kohdejoukonTarkenne)) {
                return true;
            }
        }
        return false;
    }

    private static boolean uniqueKoulutuksenAlkamiskausi(List<KoulutusmoduuliToteutus> koulutukset) {
        int alkamisvuosi = koulutukset.get(0).getAlkamisVuosi();
        String alkamiskausiUri = koulutukset.get(0).getAlkamiskausiUri();
        for (KoulutusmoduuliToteutus koulutus : koulutukset) {
            if (alkamisvuosi != koulutus.getAlkamisVuosi() || !alkamiskausiUri.equals(koulutus.getAlkamiskausiUri())) {
                return false;
            }
        }
        return true;
    }
}
