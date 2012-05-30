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
package fi.vm.sade.tarjonta.ui.service;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jukka Raanamo
 */
public class TarjontaUiServiceImpl implements TarjontaUiService {

    @Autowired
    private TarjontaAdminService adminService;

    @Override
    public TutkintoOhjelmaDTO createTutkintoOhjelma() {
        return adminService.createTutkintoOhjelma(null);
    }

    @Override
    public KoulutusmoduuliDTO save(KoulutusmoduuliDTO koulutusmoduuli) {
        return adminService.save(koulutusmoduuli);
    }
    
    

}

