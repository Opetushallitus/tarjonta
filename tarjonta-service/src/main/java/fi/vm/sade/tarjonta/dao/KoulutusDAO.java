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
import fi.vm.sade.tarjonta.model.Koulutus;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import java.util.List;

/**
 */
public interface KoulutusDAO extends JpaDAO<Koulutus, Long> {

    /**
     * If this was here only for unit testing, remove it and access entity manager from test.
     * @return
     */
    public List<KoulutusSisaltyvyys> findAllSisaltyvyys();

    /**
     * 
     * @param tila
     * @param startIndex
     * @param pageSize
     * @return
     */
    public List<Koulutus> find(String tila, int startIndex, int pageSize);

    /**
     * 
     * @param <T>
     * @param type
     * @param oid
     * @return
     */
    public <T> List<T> findAllVersions(Class<T> type, String oid);

    /**
     * Returns a list of Koulutus -objects of type <code>type</code>.
     * 
     * @param <T> type of 
     * @param type
     * @return
     */
    public <T extends Koulutus> List<T> findAll(Class<T> type);
    
    
    /**
     * Returns a list of Koulutusmoduulis that are direct children of given <code>oid</code>
     * 
     * @param <T>
     * @param type
     * @param oid 
     * @return
     */
    public <T extends Koulutus> List<T> findAllChildren(Class<T> type, String oid);
    
    
    
    /**
     * Typed version of read to save from casting.
     * 
     * @param <T>
     * @param type
     * @param id
     * @return
     */
    public <T extends Koulutus> T findByOid(Class<T> type, String id);

}

