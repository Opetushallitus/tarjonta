package fi.vm.sade.tarjonta.ui.presenter;

import com.google.common.base.Predicate;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;

/**
 * Suodattaa pois poistetut haut.
 */
public class HakuTilaPredicate implements Predicate<HakuViewModel> {

    @Override
    public boolean apply(HakuViewModel input) {
        return !input.getHaunTila().equals(TarjontaTila.POISTETTU);
    }

}
