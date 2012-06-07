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
package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.NoSuchOIDException;

/**
 *
 * @author Jukka Raanamo
 */
public interface KoulutusmoduuliToteutusDAO extends JpaDAO<KoulutusmoduuliToteutus, Long> {

    
    /**
     * 
     * @param oid
     * @return
     * @throws NoSuchOIDException todo: this comes from a bad package for this DAO
     */
    public KoulutusmoduuliToteutus findByOid(String oid)
        throws NoSuchOIDException;

}
