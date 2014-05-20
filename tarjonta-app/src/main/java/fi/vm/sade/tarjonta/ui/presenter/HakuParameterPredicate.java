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

    public HakuParameterPredicate(final ParameterServices parameterServices,
            final TarjontaPermissionServiceImpl permissionService) {
        this.parameterServices = parameterServices;
        this.permissionService = permissionService;
    }

    public boolean apply(HakuViewModel input) {
        return parameterServices.parameterCanAddHakukohdeToHaku(input
                .getHakuOid()) || permissionService.userIsOphCrud();
    };

}
