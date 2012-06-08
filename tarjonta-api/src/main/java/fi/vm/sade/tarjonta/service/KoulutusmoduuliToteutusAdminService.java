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
package fi.vm.sade.tarjonta.service;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import javax.jws.WebMethod;
import javax.jws.WebService;
import org.apache.cxf.annotations.WSDLDocumentation;

/**
 *
 * @author Jukka Raanamo
 * @author Marko Lyly
 */
@WebService(name="koulutusmoduuliToteutusAdminService")
@WSDLDocumentation("Palvelut koulutustarjonnan toteutuksien käsittelyyn.")
public interface KoulutusmoduuliToteutusAdminService {

    
    /**
     * Saves given data to persistent storage.
     *
     * @param koulutusModuuli
     * @return
     */
    @WebMethod
    KoulutusmoduuliToteutusDTO save(KoulutusmoduuliToteutusDTO toteutus);
    
}

