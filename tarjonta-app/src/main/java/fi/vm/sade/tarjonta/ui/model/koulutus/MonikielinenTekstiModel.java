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
package fi.vm.sade.tarjonta.ui.model.koulutus;

import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import java.util.Set;

/**
 *
 * @author Jani Wilén
 */
public class MonikielinenTekstiModel extends KoulutusKoodistoModel {
    private static final long serialVersionUID = -3004063117090257469L;

    private Set<KielikaannosViewModel> kielikaannos;

    /**
     * @return the kielikaannos
     */
    public Set<KielikaannosViewModel> getKielikaannos() {
        return kielikaannos;
    }

    /**
     * @param kielikaannos the kielikaannos to set
     */
    public void setKielikaannos(Set<KielikaannosViewModel> kielikaannos) {
        this.kielikaannos = kielikaannos;
    }
}
