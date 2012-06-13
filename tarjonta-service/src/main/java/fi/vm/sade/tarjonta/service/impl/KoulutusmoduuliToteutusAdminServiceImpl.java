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
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.dto.*;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliToteutusAdminService;
import fi.vm.sade.tarjonta.service.business.KoulutusmoduuliBusinessService;
import fi.vm.sade.tarjonta.service.business.KoulutusmoduuliToteutusBusinessService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Jukka Raanamo
 * @author Marko Lyly
 */
public class KoulutusmoduuliToteutusAdminServiceImpl implements KoulutusmoduuliToteutusAdminService {

    @Autowired
    private KoulutusmoduuliToteutusBusinessService toteutusService;

    @Autowired
    private KoulutusmoduuliBusinessService moduuliService;

    @Autowired
    private SadeConversionService conversionService;

    @Override
    public KoulutusmoduuliToteutusDTO save(KoulutusmoduuliToteutusDTO dto) {

        KoulutusmoduuliToteutus model = conversionService.convert(dto, KoulutusmoduuliToteutus.class);

        final String koulutusmoduuliOID = dto.getToteutettavaKoulutusmoduuliOID();
        model.setKoulutusmoduuli(moduuliService.findByOid(koulutusmoduuliOID));

        model = toteutusService.save(model);
        return conversionService.convert(model, KoulutusmoduuliToteutusDTO.class);

    }

    @Override
    public List<KoulutusmoduuliToteutusDTO> findWithTila(KoulutusmoduuliToteutusSearchDTO criteria) {
        List<KoulutusmoduuliToteutus> jpaResults = toteutusService.findWithTila(criteria);
        return conversionService.convertAll(jpaResults,KoulutusmoduuliToteutusDTO.class);
        
    }
  
    
    
}

