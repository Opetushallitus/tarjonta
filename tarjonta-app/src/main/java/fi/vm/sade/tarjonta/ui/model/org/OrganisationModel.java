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
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;

/**
 *
 * @author Jani Wilén
 */
public class OrganisationModel extends BaseUIViewModel {

    private static final long serialVersionUID = -7107381962666068440L;
    protected String organisationOid;
    protected String organisationName;

    public OrganisationModel() {
    }

    public OrganisationModel(String organisationOid, String organisationName) {
        this.organisationOid = organisationOid;
        this.organisationName = organisationName;
    }

    /**
     * @return the organisationOid
     */
    public String getOrganisationOid() {
        return organisationOid;
    }

    /**
     * @param organisationOid the organisationOid to set
     */
    public void setOrganisationOid(String organisaatioOid) {
        this.organisationOid = organisaatioOid;
    }

    /**
     * @return the organisationName
     */
    public String getOrganisationName() {
        return organisationName;
    }

    /**
     * @param organisationNimi the organisationName to set
     */
    public void setOrganisationName(String organisaatioNimi) {
        this.organisationName = organisaatioNimi;
    }

    public void setOrganisation(final String organisaatioOid, final String organisaatioNimi) {
        setOrganisationOid(organisaatioOid);
        setOrganisationName(organisaatioNimi);
    }

    public void copyToModel(OrganisationModel obj) {
        Preconditions.checkNotNull(obj, "OrganisationModel object cannot be null!");
        Preconditions.checkNotNull(obj.getOrganisationOid(), "OrganisationModel object OID cannot be null!");
        setOrganisation(obj.getOrganisationOid(), obj.getOrganisationName());
    }

    public void dtoToModel(OrganisaatioPerustieto dto) {
        Preconditions.checkNotNull(dto, "OrganisaatioDTO object cannot be null!");
        Preconditions.checkNotNull(dto.getOid(), "OrganisaatioDTO object OID cannot be null!");

        setOrganisation(dto.getOid(), OrganisaatioDisplayHelper.getClosestBasic(I18N.getLocale(), dto));
    }

    public boolean isOrganisationSelected() {
        return this.getOrganisationOid() != null;
    }
}
