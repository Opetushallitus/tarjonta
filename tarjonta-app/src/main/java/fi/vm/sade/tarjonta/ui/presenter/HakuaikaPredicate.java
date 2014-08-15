package fi.vm.sade.tarjonta.ui.presenter;

import java.util.Date;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;

public class HakuaikaPredicate implements Predicate<HakuaikaViewModel> {

    private final TarjontaPermissionServiceImpl permissionService;
    private final HakuaikaViewModel currentHakuaika;
    private boolean isLisahakuOrErillishaku;

    public HakuaikaPredicate(final HakuViewModel hakuViewModel,
            final HakuaikaViewModel currentHakuaika,
            final String hakutyyppiLisahakuUrl,
            final String hakutapaErillishakuUrl,
            final TarjontaPermissionServiceImpl permissionService) {
        Preconditions.checkNotNull(hakutapaErillishakuUrl,
                "hakutapaUrl cannot be null");
        Preconditions.checkNotNull(hakutyyppiLisahakuUrl,
                "lisahakuUrl cannot be null");

        this.permissionService = permissionService;
        this.currentHakuaika = currentHakuaika;
        this.isLisahakuOrErillishaku = isErillishakuOrLisahaku(hakuViewModel,
                hakutyyppiLisahakuUrl, hakutapaErillishakuUrl);
    }

    @Override
    public boolean apply(HakuaikaViewModel input) {
        return accepts(input, isLisahakuOrErillishaku);
    }

    private final boolean isErillishakuOrLisahaku(HakuViewModel hm,
            String hakutyyppiLisahakuUrl, String hakutapaErillishakuUrl) {
        return hakutyyppiLisahakuUrl.equals(hm.getHakutyyppi())
                || hakutapaErillishakuUrl.equals(hm.getHakutapa());
    }

    /*
     * Checks if the hakuaika is acceptable for hakukohde
     */
    private boolean accepts(HakuaikaViewModel ham,
            boolean isLisahakuOrErillishaku) {

        // Oph has her own rules
        if (permissionService.userIsOphCrud()) {
            return true;
        }

        // nykyinen hakuaika aina ok
        if (ham.equals(currentHakuaika)) {
            return true;
        }

        // If it is lisahaku it is acceptable if hakuaika has not ended yet.
        if (isLisahakuOrErillishaku) {
            return !ham.getPaattymisPvm().before(new Date());
        }

        // Hakuaika is ok if it has not started yet.
        return !ham.getAlkamisPvm().before(new Date());

    }

}
