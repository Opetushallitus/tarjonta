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
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Jani Wilén
 */
public class TarjoajaModel extends BaseUIViewModel {
    
    private static final int SINGLE_ORGANISATION = 0;
    private static final long serialVersionUID = -7107381962666068440L;
    /*
     * A list of selected organisations. 
     * When user create new koulutus, the list should have one or more 
     * organisations. Null or empty list is an error case.
     */
    private List<OrganisationOidNamePair> organisationOidNamePairs;
    /*
     * the organisaatio oids of the organisaatio tree of the tarjoaja organisaatio of this koulutus.
     * Is used when fetching potential yhteyshenkilos for the current koulutus.
     */
    public List<String> organisaatioOidTree;
    /*
     *  OID is set when an result item was selected.
     */
    private String selectedResultRowOrganisationOid;
    
    public TarjoajaModel() {
    }

    /**
     * @return the organisaatioOidNamePairs
     */
    public List<OrganisationOidNamePair> getOrganisationOidNamePairs() {
        if (organisationOidNamePairs == null) {
            organisationOidNamePairs = new ArrayList<OrganisationOidNamePair>();
        }
        
        return organisationOidNamePairs;
    }
    
    private List<OrganisationOidNamePair> checkSingleOrganisation() {
        final List<OrganisationOidNamePair> pair = getOrganisationOidNamePairs();
        
        Preconditions.checkNotNull(pair, "Organisation list object cannot be null.");
        Preconditions.checkArgument(!pair.isEmpty(), "No organisation was selected.");
        Preconditions.checkArgument(!isMultiSelect(), "Too many organisations was selected. Organisation count : " + pair.size());
        return pair;
    }

    /**
     * Get the selected organisation String OID. Use the method only when user
     * has select a single organisation. Throws an exception if an invalid data.
     *
     * @return List of organisations.
     */
    public String getSelectedOrganisationOid() {
        return getSelectedOrganisation().getOrganisationOid();
    }

    /**
     * Get the selected organisation object. Use the method only when user has
     * select a single organisation. Throws an exception if an invalid data.
     *
     * @return List of organisations.
     */
    public OrganisationOidNamePair getSelectedOrganisation() {
        return checkSingleOrganisation().get(SINGLE_ORGANISATION);
    }

    /*
     * Returns true when user has selected multiple organisations.
     */
    public boolean isMultiSelect() {
        Collection<OrganisationOidNamePair> pairs = getOrganisationOidNamePairs();
        
        return pairs.size() > 1 ? true : false;
    }

    /**
     * @param organisaatioOidNamePairs the organisaatioOidNamePairs to set
     */
    public void setOrganisationOidNamePairs(List<OrganisationOidNamePair> organisaatioOidNamePairs) {
        this.organisationOidNamePairs = organisaatioOidNamePairs;
    }
    
    private void addOrganisation(OrganisationOidNamePair pair) {
        getOrganisationOidNamePairs().add(pair);
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

    /*
     * Remove all previously added organisations from a list of organisations and adds new organisations to the list.
     */
    public void addSelectedOrganisations(Collection<OrganisaatioPerustietoType> orgs) {
        getOrganisationOidNamePairs().clear();
        List<OrganisaatioPerustietoType> tempOrgs = new ArrayList<OrganisaatioPerustietoType>(orgs);
        addOrganisation(new OrganisationOidNamePair(tempOrgs.get(0).getOid(), tempOrgs.get(0).getNimiFi()));
    }

    /*
     * Remove all previously added organisations from a list of organisations and set new organisation to the list.
     */
    public void setSelectedOrganisation(OrganisationModel naviOrg) {
        getOrganisationOidNamePairs().clear();
        OrganisationOidNamePair pair = new OrganisationOidNamePair();
        pair.copyToModel(naviOrg);
        addOrganisation(pair);
    }

    /**
     * @return the selectedResultRowOrganisationOid
     */
    public String getSelectedResultRowOrganisationOid() {
        return selectedResultRowOrganisationOid;
    }

    /**
     * @param selectedResultRowOrganisationOid the
     * selectedResultRowOrganisationOid to set
     */
    public void setSelectedResultRowOrganisationOid(String selectedResultRowOrganisationOid) {
        //set organisation as selected organisation
        setSelectedOrganisationOid(selectedResultRowOrganisationOid);
        
        //store the value for other use.
        this.selectedResultRowOrganisationOid = selectedResultRowOrganisationOid;
    }

    /**
     * Returns selected row organisation OID.
     */
    public String getSingleSelectRowResultOrganisationOid() {
        if (getSelectedResultRowOrganisationOid() != null && getOrganisationOidNamePairs().size() > 1) {
            /*
             * Used when user copy koulutus from organisation A to organisation B,C,D,E etc.  
             * Also ignore all selected organisations on dialog.
             */
            return getSelectedResultRowOrganisationOid();
        } else {
            /*
             * Create new koulutus by an organisation selected on dialog.
             */
            return getSelectedOrganisation().getOrganisationOid();
        }
    }
    
    public void setSelectedOrganisationOid(String tarjoajaOid) {
        getOrganisationOidNamePairs().clear();
        OrganisationOidNamePair pair = new OrganisationOidNamePair();
        pair.setOrganisationOid(tarjoajaOid);
        addOrganisation(pair);
        
    }
}
