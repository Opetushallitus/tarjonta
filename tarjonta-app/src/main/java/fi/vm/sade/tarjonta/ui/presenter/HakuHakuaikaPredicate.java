package fi.vm.sade.tarjonta.ui.presenter;

import com.google.common.base.Predicate;

import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;

public class HakuHakuaikaPredicate implements Predicate<HakuViewModel> {

    private final String hakutyyppiLisahakuUrl;
    private final String hakutapaErillishakuUrl;
    private final TarjontaPermissionServiceImpl permissionService;
    private final HakuaikaViewModel currentHakuaika;

    public HakuHakuaikaPredicate(final HakuaikaViewModel currentHakuaika,
            final String hakutyyppiLisahakuUrl,
            final String hakutapaErillishakuUrl,
            final TarjontaPermissionServiceImpl permissionService) {
        this.hakutyyppiLisahakuUrl = hakutyyppiLisahakuUrl;
        this.hakutapaErillishakuUrl = hakutapaErillishakuUrl;
        this.permissionService = permissionService;
        this.currentHakuaika = currentHakuaika;

    }

    @Override
    public boolean apply(HakuViewModel input) {
        boolean acceptable = false;

        HakuaikaPredicate hakuaikaPredicate = new HakuaikaPredicate(input,
                currentHakuaika, hakutyyppiLisahakuUrl, hakutapaErillishakuUrl,
                permissionService);

        for (HakuaikaViewModel hakuaika : input.getSisaisetHakuajat()) {
            if (hakuaikaPredicate.apply(hakuaika)) {
                acceptable = true;
            }
        }
        
        System.out.println(this + "\n\n\n\nfiltering result:" + input.getNimi() + ": " + acceptable);

        return acceptable;
    }

}
