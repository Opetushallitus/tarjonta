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
package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliSisaltyvyys;
import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSearchDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSummaryDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.service.NoSuchOIDException;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.business.KoulutusmoduuliBusinessService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Jukka Raanamo
 * @author Marko Lyly
 */
public class TarjontaAdminServiceImpl implements TarjontaAdminService {

    @Autowired
    private KoulutusmoduuliBusinessService businessService;

    @Autowired
    private SadeConversionService conversionService;

    @Override
    public TutkintoOhjelmaDTO createTutkintoOhjelma(String organisaatioUri) {

        TutkintoOhjelma newModule = new TutkintoOhjelma();
        return conversionService.convert(newModule, TutkintoOhjelmaDTO.class);

    }

    @Override
    public KoulutusmoduuliDTO save(KoulutusmoduuliDTO dto) {
        Koulutusmoduuli model = conversionService.convert(dto, Koulutusmoduuli.class);
        model = businessService.save(model);
        return conversionService.convert(model, TutkintoOhjelmaDTO.class);
    }

    @Override
    public KoulutusmoduuliDTO find(String koulutusModuuliOID) {
        Koulutusmoduuli model = businessService.findByOid(koulutusModuuliOID);
        return conversionService.convert(model, KoulutusmoduuliDTO.class);
    }

    @Override
    public List<KoulutusmoduuliDTO> find(KoulutusmoduuliSearchDTO searchSpesification) {
        throw new UnsupportedOperationException("NOT IMPEMENTED YET");
    }

    @Override
    public List<KoulutusmoduuliSummaryDTO> getParentModuulis(String moduuliOID) {
        
        // todo: we can get parents directly too without going through relations
        
        Koulutusmoduuli moduuli = loadKoulutusmoduuli(moduuliOID);
        
        Set<KoulutusmoduuliSisaltyvyys> parents = moduuli.getParents();
        List<KoulutusmoduuliSummaryDTO> summaries = new ArrayList<KoulutusmoduuliSummaryDTO>(parents.size());
        
        for (KoulutusmoduuliSisaltyvyys s : parents) {
            summaries.add(conversionService.convert(s, KoulutusmoduuliSummaryDTO.class));
        }
        
        return summaries;
        
    }

    @Override
    public List<KoulutusmoduuliSummaryDTO> getChildModuulis(String moduuliOID) throws NoSuchOIDException {
        
        Koulutusmoduuli moduuli = loadKoulutusmoduuli(moduuliOID);
        Set<KoulutusmoduuliSisaltyvyys> children = moduuli.getChildren();
        List<KoulutusmoduuliSummaryDTO> summaries = new ArrayList<KoulutusmoduuliSummaryDTO>(children.size());
        
        for (KoulutusmoduuliSisaltyvyys s : children) {
            summaries.add(conversionService.convert(s, KoulutusmoduuliSummaryDTO.class));
        }
        
        return summaries;
        
    }
    
    
    /**
     * Returns a non null Koulutusmoduuli or throws exception if one does not exist for given oid.
     *
     * @param oid
     * @return
     * @throws NoSuchOIDException
     */
    private Koulutusmoduuli loadKoulutusmoduuli(String oid) throws NoSuchOIDException {
        
        Koulutusmoduuli k = businessService.findByOid(oid);
        if (k == null) {
            throw new NoSuchOIDException("No such oid: " + oid, null);
        }
        return k;
        
    }
    
    

}

