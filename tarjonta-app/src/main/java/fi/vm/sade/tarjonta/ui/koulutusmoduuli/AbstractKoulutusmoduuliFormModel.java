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
package fi.vm.sade.tarjonta.ui.koulutusmoduuli;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliPerustiedotDTO;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class that wraps KoulutusmoduuliDTO to provide convenient methods for BeanItem data binding.
 *
 * @param <T> type of KoulutusmoduuliDTO to wrap
 *
 * @author Jukka Raanamo
 */
@SuppressWarnings("serial")
public class AbstractKoulutusmoduuliFormModel<T extends KoulutusmoduuliDTO> implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(AbstractKoulutusmoduuliFormModel.class);

    protected T koulutusmoduuli;

    public AbstractKoulutusmoduuliFormModel() {
    }

    public AbstractKoulutusmoduuliFormModel(T dto) {
        this.koulutusmoduuli = dto;
    }

    public void setKoulutusmoduuli(T dto) {
        this.koulutusmoduuli = dto;
    }

    public T getKoulutusmoduuli() {
        return koulutusmoduuli;
    }
    
    
    public KoulutusmoduuliPerustiedotDTO getPerustiedot() {
        return koulutusmoduuli.getPerustiedot();
    }
    
    public void setPerustiedot(KoulutusmoduuliPerustiedotDTO perustiedot) {
        // get is enough for setting its properties
    }

    public void setOrganisaatioOid(String organisaatioOid) {
        koulutusmoduuli.setOrganisaatioOid(organisaatioOid);
    }

    public String getOrganisaatioOid() {
        return koulutusmoduuli.getOrganisaatioOid();
    }

}

