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
package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliPerustiedotDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Jukka Raanamo
 */
public class TarjontaServiceMock implements TarjontaAdminService {

    private Map<Long, KoulutusmoduuliDTO> koulutusModuuliMap = new HashMap<Long, KoulutusmoduuliDTO>();
    
    private AtomicLong idCounter = new AtomicLong(0);

    @Override
    public TutkintoOhjelmaDTO createTutkintoOhjelma(String organisaatioUri) {

        TutkintoOhjelmaDTO ohjelma = new TutkintoOhjelmaDTO();
        KoulutusmoduuliPerustiedotDTO perustiedot = new KoulutusmoduuliPerustiedotDTO();
        perustiedot.setOrganisaatioOid(organisaatioUri);
        ohjelma.setPerustiedot(perustiedot);
        return ohjelma;

    }

    @Override
    public KoulutusmoduuliDTO save(KoulutusmoduuliDTO koulutusModuuli) {

        if (koulutusModuuli.getId() == null) {
            koulutusModuuli.setId(idCounter.incrementAndGet());
        }
        koulutusModuuliMap.put(koulutusModuuli.getId(), koulutusModuuli);
        return koulutusModuuli;
        
    }

}

