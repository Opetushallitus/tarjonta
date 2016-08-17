package fi.vm.sade.tarjonta.service.impl.resources.v1.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
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
        if(hasAlkamisVuosiAndUri) {
            boolean isBeforeSyksy2016 = haku.getKoulutuksenAlkamisVuosi() < 2016 ||
                    (haku.getKoulutuksenAlkamisVuosi() == 2016 && haku.getKoulutuksenAlkamiskausiUri().startsWith("kausi_k"));
            if (isBeforeSyksy2016) {
                return new YhdenPaikanSaanto(false, "Koulutuksen alkamiskausi ennen syksyä 2016");
            }
        } else {
            return new YhdenPaikanSaanto(false, "Koulutuksella ei ole alkamisvuotta ja alkamiskautta");
        }
        String haunKohdeJoukonTarkenne = haku.getKohdejoukonTarkenne();
        if (StringUtils.isBlank(haunKohdeJoukonTarkenne)) {
            return new YhdenPaikanSaanto(true, "Korkeakouluhaku ilman kohdejoukon tarkennetta");
        }
        for (String tarkenne : TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO) {
            if (haunKohdeJoukonTarkenne.startsWith(tarkenne)) {
                return new YhdenPaikanSaanto(true, String.format("Kohdejoukon tarkenne on '%s'", haunKohdeJoukonTarkenne));
            }
        }
        return new YhdenPaikanSaanto(false, String.format("Kohdejoukon tarkenne on '%s', sääntö on voimassa tarkenteille %s",
                haunKohdeJoukonTarkenne, TARKENTEET_JOILLE_YHDEN_PAIKAN_SAANTO));
    }

    public static YhdenPaikanSaanto from(Hakukohde hakukohde) {
        Haku haku = hakukohde.getHaku();
        YhdenPaikanSaanto yhdenPaikanSaantoBasedOnHaku = from(haku);
        if(yhdenPaikanSaantoBasedOnHaku.isVoimassa()) {
            return yhdenPaikanSaantoBasedOnHaku;
        }
        if(haku.isKorkeakouluHaku()) {
            if(haku.isJatkuva()) {
                Collection<KoulutusmoduuliToteutus> validKomotos = filterOnlyValidKomotos(hakukohde.getKoulutusmoduuliToteutuses());
                if(validKomotos.isEmpty()) {
                    return new YhdenPaikanSaanto(false, String.format("%s ja hakukohteella ei ole oikean tilaista koulutusmoduulia",
                            yhdenPaikanSaantoBasedOnHaku.getSyy()));
                } else if(validKomotos.size() > 1) {
                    return new YhdenPaikanSaanto(false, String.format("%s ja hakukohteella on liian monta oikean tilaista koulutusmoduulia",
                            yhdenPaikanSaantoBasedOnHaku.getSyy()));
                } else {
                    KoulutusmoduuliToteutus validKomoto = validKomotos.iterator().next();
                    boolean ennenSyksya2016 = validKomoto.getAlkamisVuosi() < 2016 ||
                            (validKomoto.getAlkamisVuosi() == 2016 && validKomoto.getAlkamiskausiUri().startsWith("kausi_k"));
                    if(ennenSyksya2016) {
                        return new YhdenPaikanSaanto(false, String.format("%s ja hakukohteen alkamiskausi ja vuosi on ennen syksyä 2016",
                                yhdenPaikanSaantoBasedOnHaku.getSyy()));
                    } else {
                        return new YhdenPaikanSaanto(true, "Jatkuvan haun hakukohteen alkamiskausi ja vuosi on jälkeen kevään 2016");
                    }
                }
            } else {
                return new YhdenPaikanSaanto(false, String.format("%s ja kyseessä ei ole jatkuva haku",
                        yhdenPaikanSaantoBasedOnHaku.getSyy()));
            }
        }
        return yhdenPaikanSaantoBasedOnHaku;
    }

    private static Collection<KoulutusmoduuliToteutus> filterOnlyValidKomotos(Set<KoulutusmoduuliToteutus> komotos) {
        return Collections2.filter(komotos, new Predicate<KoulutusmoduuliToteutus>() {
            private final List<TarjontaTila> INVALID = Arrays.asList(TarjontaTila.LUONNOS, TarjontaTila.KOPIOITU);
            @Override
            public boolean apply(@Nullable KoulutusmoduuliToteutus input) {
                if(input == null) {
                    return false;
                }
                return !INVALID.contains(input.getTila()) && input.getAlkamisVuosi() != null && input.getAlkamiskausiUri() != null;
            }
        });
    }

}
