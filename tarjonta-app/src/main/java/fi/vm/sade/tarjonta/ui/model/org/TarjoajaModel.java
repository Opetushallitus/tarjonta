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
package fi.vm.sade.tarjonta.ui.model.org;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Jani Wilén
 */
public class TarjoajaModel extends OrganisationModel {

    private static final long serialVersionUID = -7107381962666068440L;
    private Collection<OrganisationOidNamePair> organisaatioOidNamePairs;
    /*
     * the organisaatio oids of the organisaatio tree of the tarjoaja organisaatio of this koulutus.
     * Is used when fetching potential yhteyshenkilos for the current koulutus.
     */
    public List<String> organisaatioOidTree;
    
    public TarjoajaModel() {
    }

    public TarjoajaModel(String organisaatioOid, String organisaatioNimi) {
        this.organisationOid = organisaatioOid;
        this.organisationName = organisaatioNimi;
    }

    /**
     * @return the organisaatioOidNamePairs
     */
    public Collection<OrganisationOidNamePair> getOrganisaatioOidNamePairs() {
        if (organisaatioOidNamePairs == null) {
            organisaatioOidNamePairs = new ArrayList<OrganisationOidNamePair>();
        }

        return organisaatioOidNamePairs;
    }

    /**
     * @return the organisaatioOid
     */
    @Override
    public String getOrganisationOid() {
        Preconditions.checkNotNull(organisationOid, "Organisation OID  cannot be null.");

        return organisationOid;
    }

    /**
     * @param organisaatioOidNamePairs the organisaatioOidNamePairs to set
     */
    public void setOrganisaatioOidNamePairs(Collection<OrganisationOidNamePair> organisaatioOidNamePairs) {
        this.organisaatioOidNamePairs = organisaatioOidNamePairs;
    }

    public void addOneOrganisaatioNameOidPair(OrganisationOidNamePair pair) {
        getOrganisaatioOidNamePairs().clear();
        getOrganisaatioOidNamePairs().add(pair);
    }

    /**
     * @return the organisaatioOidTree
     */
    public List<String> getOrganisaatioOidTree() {
        return organisaatioOidTree;
    }

    /**
     * @param organisaatioOidTree the organisaatioOidTree to set
     */
    public void setOrganisaatioOidTree(List<String> organisaatioOidTree) {
        this.organisaatioOidTree = organisaatioOidTree;
    }
}
