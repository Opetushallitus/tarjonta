package fi.vm.sade.tarjonta.ui.presenter;

import com.google.common.base.Predicate;

import fi.vm.sade.tarjonta.shared.ParameterServices;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;

/**
 * Filter away hakus that can not be attached to hakukohde based on parameters.
 * OPH user can do whatever.
 */
public class HakuParameterPredicate implements Predicate<HakuViewModel> {

    private final ParameterServices parameterServices;
    private final TarjontaPermissionServiceImpl permissionService;
    private final String currentHakuOid;

    public HakuParameterPredicate(final String currentHakuOid, final ParameterServices parameterServices,
            final TarjontaPermissionServiceImpl permissionService) {
        this.parameterServices = parameterServices;
        this.permissionService = permissionService;
        this.currentHakuOid=currentHakuOid;
    }

    public boolean apply(HakuViewModel input) {
        if(currentHakuOid==input.getHakuOid()) { //näytä myös nykyinen haku vaikka parametrit estää
            return true;
        }
        return parameterServices.parameterCanAddHakukohdeToHaku(input
                .getHakuOid()) || permissionService.userIsOphCrud();
    };

}
