package fi.vm.sade.tarjonta.service.impl.resources.v1.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO.YhdenPaikanSaanto;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.*;

public class YhdenPaikanSaantoBuilder {

    private static final String JATKOTUTKINTOHAKU_URI = "haunkohdejoukontarkenne_3#";
    private static final List<String> TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO = Collections.singletonList(JATKOTUTKINTOHAKU_URI);

    public static YhdenPaikanSaanto from(Haku haku) {
        if (!haku.isKorkeakouluHaku()) {
            return new YhdenPaikanSaanto(false, "Ei korkeakouluhaku");
        }
        boolean hasAlkamisVuosiAndUri = haku.getKoulutuksenAlkamisVuosi() != null && haku.getKoulutuksenAlkamiskausiUri() != null;
        if (hasAlkamisVuosiAndUri) {
            boolean isBeforeSyksy2016 = haku.getKoulutuksenAlkamisVuosi() < 2016 ||
                    (haku.getKoulutuksenAlkamisVuosi() == 2016 && haku.getKoulutuksenAlkamiskausiUri().startsWith("kausi_k"));
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
        Collection<KoulutusmoduuliToteutus> validKomotos = filterOnlyValidKomotos(hakukohde.getKoulutusmoduuliToteutuses());
        if (validKomotos.isEmpty()) {
            return new YhdenPaikanSaanto(false, String.format(
                    "%s ja hakukohteella ei ole oikean tilaista koulutusmoduulia",
                    ypsBasedOnHaku.getSyy()
            ));
        }
        if (validKomotos.size() > 1) {
            return new YhdenPaikanSaanto(false, String.format(
                    "%s ja hakukohteella on liian monta oikean tilaista koulutusmoduulia",
                    ypsBasedOnHaku.getSyy()
            ));
        }
        KoulutusmoduuliToteutus validKomoto = validKomotos.iterator().next();
        boolean ennenSyksya2016 = validKomoto.getAlkamisVuosi() < 2016 ||
                (validKomoto.getAlkamisVuosi() == 2016 && validKomoto.getAlkamiskausiUri().startsWith("kausi_k"));
        if (ennenSyksya2016) {
            return new YhdenPaikanSaanto(false, String.format(
                    "%s ja hakukohteen alkamiskausi ja vuosi on ennen syksyä 2016",
                    ypsBasedOnHaku.getSyy()
            ));
        }

        return new YhdenPaikanSaanto(true, "Jatkuvan haun hakukohteen alkamiskausi ja vuosi on jälkeen kevään 2016");
    }

    private static Collection<KoulutusmoduuliToteutus> filterOnlyValidKomotos(Set<KoulutusmoduuliToteutus> komotos) {
        return Collections2.filter(komotos, new Predicate<KoulutusmoduuliToteutus>() {
            private final List<TarjontaTila> INVALID = Arrays.asList(TarjontaTila.LUONNOS, TarjontaTila.KOPIOITU);

            @Override
            public boolean apply(@Nullable KoulutusmoduuliToteutus input) {
                if (input == null) {
                    return false;
                }
                return !INVALID.contains(input.getTila()) && input.getAlkamisVuosi() != null && input.getAlkamiskausiUri() != null;
            }
        });
    }

    private static boolean haunKohdejoukontarkenneYPSYhteensopiva(Haku haku) {
        return StringUtils.isBlank(haku.getKohdejoukonTarkenne()) ||
                TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO.stream()
                        .anyMatch(tarkenne -> haku.getKohdejoukonTarkenne().startsWith(tarkenne));
    }
}
