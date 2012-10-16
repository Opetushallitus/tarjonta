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
import fi.vm.sade.tarjonta.model.BaseKoulutusmoduuli;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutusmoduuliTyyppi;

/**
 * Business logic for manipulating classes inherited from LearningOpportunityObject.
 *
 */
public interface KoulutusBusinessService {

    /**
     * Create new Koulutusmoduuli as a child of Koulutusmoduuli matching given <code>parentOid</code>.
     *
     * @param parentOid oid of the parent LOO the new Kulutusmoduuli will be child of
     * @param optional flag indicating optionality from parent to child
     * @param koulutus data for the new Koulutusmoduuli
     * @return
     */
    public Koulutusmoduuli create(Koulutusmoduuli koulutus, String parentOid, boolean optional);

    /**
     * Creates a new top level Koulutusmoduuli.
     *
     * @param moduuli
     * @return
     */
    public Koulutusmoduuli create(Koulutusmoduuli moduuli);

    /**
     *
     * @param moduuli
     * @return
     */
    public Koulutusmoduuli update(Koulutusmoduuli moduuli);


    
    /**
     *
     * @param toteutus
     * @return
     */
    public KoulutusmoduuliToteutus update(KoulutusmoduuliToteutus toteutus);

    /**
     * Creates new KoulutusmoduuliToteutus from passed data.
     *
     * @param koulutus KoulutusmoduuliToteutus to create
     * @param koulutusmoduuliOid reference to Koulutusmoduuli that this KoulutusmoduuliToteutus specifies.
     * @return
     */
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, String koulutusmoduuliOid);

    /**
     * Creates new KoulutusmoduuliToteutus from passed data. Before storing, reference to given Koulutusmoduuli is assigned.
     * If given Koulutusmoduuli is also new, it is also created.
     *
     * @param toteutus
     * @param moduuli
     * @return
     */
    public KoulutusmoduuliToteutus create(KoulutusmoduuliToteutus toteutus, Koulutusmoduuli moduuli);

    /**
     * Returns LearningOpportunityObject with given oid if any.
     *
     * @param oid
     * @return
     */
    public BaseKoulutusmoduuli findByOid(String oid);

    /**
     * Deletes LearningOpportunityObject if a) it is Koulutusmoduuli and it has no KoulutusmoduuliToteutus and its
     * state is "non published" or b) it is KoulutusmoduuliToteutus and it is not reference by any Hakukohde and it it's
     * state is "non published".
     *
     * @param oid
     */
    public void deleteKoulutusmoduuliByOid(String oid);

    /**
     * Palauttaa {@link KoulutusmoduuliTyyppi#TUTKINTO_OHJELMA} -tyyppisen Koulutusmoduulin jonka koulutusLuokitus sekä koulutusOhjelma
     * vastaavat annettuja arvoja.
     *
     * @param tutkintoUri
     * @param koulutusUri
     * @return
     */
    public Koulutusmoduuli findTutkintoOhjelma(String koulutusLuokitusUri, String koulutusOhjelmaUri);

    /**
     * Once search criteria tyyppi is declared in WSDL expose this method. The implementation is in place in DAO.
     *
     * @param oid
     * @param criteria
     * @return
     */
    //public List<? extends LearningOpportunityObject> search(KoulutusSearchTyyppi criteria);
}

