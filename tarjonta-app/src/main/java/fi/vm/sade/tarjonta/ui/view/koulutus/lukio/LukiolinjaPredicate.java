package fi.vm.sade.tarjonta.ui.view.koulutus.lukio;

import com.google.common.base.Predicate;

import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;

/**
 * Suodata pois lukiolinjat joita ei haluta näyttää vaadin kälissä 
 */
public class LukiolinjaPredicate implements Predicate<LukiolinjaModel>{
    @Override
    public boolean apply(LukiolinjaModel input) {
        return !"lukiolinjat_0086".equals(input.getKoodistoUri());
    }

}
