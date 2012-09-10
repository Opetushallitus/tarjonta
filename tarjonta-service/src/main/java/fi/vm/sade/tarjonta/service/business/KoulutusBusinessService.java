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
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import java.util.List;

/**
 * Common business logic for manipulating classes inherited from Koulutus. Instances of Koulutusmoduuli and KoulutusmoduuliToteutus have been mostly merged on
 * UI level and the specification team also now speaks about Koulutus instead of the two in separate.
 *
 */
public interface KoulutusBusinessService {

    /**
     * Creates new Koulutusmoduuli into database. Newly created Koulutusmoduuli will have no implementations (LOIs). Note that OID must be already assigned.
     *
     * @param koulutusmoduuli
     * @return
     */
    public Koulutusmoduuli create(Koulutusmoduuli koulutus);

    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus koulutus);

    /**
     * Stores KoulutusmoduuliToteutus into database, before storing, reference to given Koulutusmoduuli is assigned. If given Koulutusmoduuli is also new, it is
     * first persisted.
     *
     * @param toteutus
     * @param moduuli
     * @return
     */
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, Koulutusmoduuli moduuli);

    /**
     * Returns a list of Koulutusmoduuli -objects that represent the same learning opportunity specification in it's available versions.
     *
     * @throws todo: do we
     * @param oid
     * @return
     */
    public List<Koulutusmoduuli> findAllKoulutusmoduuliVersions(String oid);

    public List<KoulutusmoduuliToteutus> findAllKoulutusmoduuliToteutusVersions(String oid);

    /**
     * Returns current (latest) Koulutusmoduuli for given oid.
     *
     * @param oid
     * @return
     */
    public Koulutusmoduuli findKoulutusmoduuliByOid(String oid);

}

