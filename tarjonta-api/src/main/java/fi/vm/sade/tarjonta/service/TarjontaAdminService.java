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

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSearchDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSummaryDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;
import org.apache.cxf.annotations.WSDLDocumentation;

/**
 *
 * @author Jukka Raanamo
 * @author Marko Lyly
 */
@WebService(name="tarjontaAdminService")
@WSDLDocumentation("Web servicet koulutustarjonnan käsittelyyn.")
public interface TarjontaAdminService {

    /**
     * Creates a new TutkintoOhjelma. At this point, nothing is stored to persistent
     * storage yet. Call save for that.
     *
     * @param typpi
     * @param organisaatioOID
     * @return
     */
    @WebMethod
    TutkintoOhjelmaDTO createTutkintoOhjelma(String organisaatioOID);

    /**
     * Saves given data to persistent storage.
     *
     * @param koulutusModuuli
     * @return
     */
    @WebMethod
    KoulutusmoduuliDTO save(KoulutusmoduuliDTO koulutusModuuli);

    /**
     * Find given Koulutusmoduuli by it's OID.
     *
     * @param koulutusModuuliOID
     * @return
     */
    @WebMethod
    KoulutusmoduuliDTO find(String koulutusModuuliOID);

    /**
     * Find Koulutusmoduuli's with given search spesification.
     *
     * @param searchSpesification
     * @return
     */
    @WebMethod
    List<KoulutusmoduuliDTO> find(KoulutusmoduuliSearchDTO searchSpesification);
    
    
    /**
     * 
     * @param moduuliOID
     * @return
     * @throws NoSuchOIDException  
     */
    @WebMethod
    List<KoulutusmoduuliSummaryDTO> getParentModuulis(String moduuliOID) throws NoSuchOIDException;


    @WebMethod
    List<KoulutusmoduuliSummaryDTO> getChildModuulis(String moduuliOID) throws NoSuchOIDException;
    
}

