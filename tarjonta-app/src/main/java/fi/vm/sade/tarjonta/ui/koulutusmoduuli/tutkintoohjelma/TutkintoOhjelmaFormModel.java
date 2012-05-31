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
package fi.vm.sade.tarjonta.ui.koulutusmoduuli.tutkintoohjelma;

import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.AbstractKoulutusmoduuliFormModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jukka Raanamo
 */
public class TutkintoOhjelmaFormModel extends AbstractKoulutusmoduuliFormModel<TutkintoOhjelmaDTO> {

    private static final long serialVersionUID = 492232378548775918L;
    
    private static final Logger log = LoggerFactory.getLogger(TutkintoOhjelmaFormModel.class);

    public TutkintoOhjelmaFormModel(TutkintoOhjelmaDTO dto) {
        super(dto);
    }

    public TutkintoOhjelmaDTO getTutkintoOhjelma() {
        return koulutusmoduuli;
    }

    public void setTutkintoOhjelma(TutkintoOhjelmaDTO dto) {
        this.koulutusmoduuli = dto;
    }


}

