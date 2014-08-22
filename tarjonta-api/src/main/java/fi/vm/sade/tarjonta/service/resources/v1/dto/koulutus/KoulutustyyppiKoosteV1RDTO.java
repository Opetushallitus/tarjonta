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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import com.wordnik.swagger.annotations.ApiModel;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jani Wilén
 */
@ApiModel(value = "KOulutustyyppien näyttämiseen liittyvä rajapintaolio")
public class KoulutustyyppiKoosteV1RDTO extends KoodiV1RDTO {

    private static final long serialVersionUID = 1L;
    private Set<String> koulutustyyppiUris;
    private boolean modules;

    /**
     * @return the koulutustyyppiUris
     */
    public Set<String> getKoulutustyyppiUris() {
        if (koulutustyyppiUris == null) {
            koulutustyyppiUris = new HashSet<String>();
        }

        return koulutustyyppiUris;
    }

    /**
     * @param koulutustyyppiUris the koulutustyyppiUris to set
     */
    public void setKoulutustyyppiUris(Set<String> koulutustyyppiUris) {
        this.koulutustyyppiUris = koulutustyyppiUris;
    }

    /**
     * @return the modules
     */
    public boolean isModules() {
        return modules;
    }

    /**
     * @param modules the modules to set
     */
    public void setModules(boolean modules) {
        this.modules = modules;
    }
}
