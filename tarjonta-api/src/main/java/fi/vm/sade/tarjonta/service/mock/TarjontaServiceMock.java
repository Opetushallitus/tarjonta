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
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSearchDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Jukka Raanamo
 * @author Marko Lyly
 */
public class TarjontaServiceMock implements TarjontaAdminService {
    
    private static final Logger log = LoggerFactory.getLogger(TarjontaServiceMock.class);

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

        log.debug("saving " + koulutusModuuli);
        
        if (koulutusModuuli.getId() == null) {
            koulutusModuuli.setId(idCounter.incrementAndGet());
        }
        koulutusModuuliMap.put(koulutusModuuli.getId(), koulutusModuuli);
        return koulutusModuuli;

    }

    @Override
    public KoulutusmoduuliDTO find(String koulutusModuuliOID) {

        KoulutusmoduuliDTO result = null;

        for (KoulutusmoduuliDTO koulutusmoduuliDTO : koulutusModuuliMap.values()) {
            if (koulutusmoduuliDTO.getOid().equals(koulutusModuuliOID)) {
                result = koulutusmoduuliDTO;
                break;
            }
        }

        return result;
    }

    @Override
    public List<KoulutusmoduuliDTO> find(KoulutusmoduuliSearchDTO searchSpesification) {
        if (searchSpesification == null) {
            throw new IllegalArgumentException("Search spesification required.");
        }

        List<KoulutusmoduuliDTO> result = new ArrayList<KoulutusmoduuliDTO>();

        for (KoulutusmoduuliDTO koulutusmoduuliDTO : koulutusModuuliMap.values()) {
            if (match(koulutusmoduuliDTO, searchSpesification)) {
                result.add(koulutusmoduuliDTO);
            }
        }

        return result;
    }

    /**
     * Filter/match koulutusmoduuli with given search spesification.
     *
     * @param koulutusmoduuliDTO
     * @param searchSpesification
     * @return true if matches
     */
    private boolean match(KoulutusmoduuliDTO koulutusmoduuliDTO, KoulutusmoduuliSearchDTO searchSpesification) {
        if (searchSpesification.getNimi() != null) {
            // TODO match when koulutusohjelma has some content :)
            return true;
        }

        // no match by default
        return false;
    }

}

