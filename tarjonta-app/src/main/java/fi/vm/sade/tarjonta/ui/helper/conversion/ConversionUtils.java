/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jukka Raanamo
 */
public class ConversionUtils {

    public static List<KielikaannosViewModel> convertTekstiToVM(MonikielinenTekstiTyyppi tekstiTyyppi) {

        if (tekstiTyyppi == null) {
            return Collections.EMPTY_LIST;
        }

        List<KielikaannosViewModel> vastaus = new ArrayList<KielikaannosViewModel>();
        for (MonikielinenTekstiTyyppi.Teksti teksti : tekstiTyyppi.getTeksti()) {
            KielikaannosViewModel kvm = new KielikaannosViewModel(teksti.getKieliKoodi(), teksti.getValue());
            vastaus.add(kvm);
        }

        return vastaus;
    }

    public static MonikielinenTekstiTyyppi convertKielikaannosToMonikielinenTeksti(List<KielikaannosViewModel> kielikaannokset) {
        MonikielinenTekstiTyyppi monikielinenTekstiTyyppi = new MonikielinenTekstiTyyppi();

        for (KielikaannosViewModel kielikaannosViewModel:kielikaannokset) {
            MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();

            teksti.setKieliKoodi(kielikaannosViewModel.getKielikoodi());
            teksti.setValue(kielikaannosViewModel.getNimi());

            monikielinenTekstiTyyppi.getTeksti().add(teksti);
        }

        return monikielinenTekstiTyyppi;
    }

}

