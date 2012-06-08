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
package fi.vm.sade.tarjonta.service.business;

import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.NoSuchOIDException;

/**
 * Service to be used internally by public services to centralize Koulutusmoduuli operations.
 * 
 * @author Jukka Raanamo
 */
public interface KoulutusmoduuliBusinessService {

    /**
     * Insert or update given Koulutusmoduuli
     *
     * @param moduuli
     * @return
     */
    Koulutusmoduuli save(Koulutusmoduuli moduuli);

    /**
     * Looks up Koulutusmoduuli by its public OID
     *
     * @param koulutusmoduuliOID
     * @return
     * @throws NoSuchOIDException if no match is found 
     */
    Koulutusmoduuli findByOid(String koulutusmoduuliOID) throws NoSuchOIDException;

}

