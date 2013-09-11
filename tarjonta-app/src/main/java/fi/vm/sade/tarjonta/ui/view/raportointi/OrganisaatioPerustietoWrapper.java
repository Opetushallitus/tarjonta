package fi.vm.sade.tarjonta.ui.view.raportointi;/*
 *
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

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
/**
 * @author: Tuomas Katva
 * Date: 5.9.2013
 */
public class OrganisaatioPerustietoWrapper {

    private OrganisaatioPerustieto organisaatioPerustieto;

    private String localizedName;

    private String organisaatioOid;

    public OrganisaatioPerustietoWrapper(OrganisaatioPerustieto perustieto) {
        this.organisaatioPerustieto = perustieto;
        this.organisaatioOid = perustieto.getOid();
        this.localizedName = OrganisaatioDisplayHelper.getAvailableNameBasic(perustieto);
    }


    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public OrganisaatioPerustieto getOrganisaatioPerustieto() {
        return organisaatioPerustieto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganisaatioPerustietoWrapper that = (OrganisaatioPerustietoWrapper) o;

        if (!organisaatioOid.equals(that.organisaatioOid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return organisaatioOid.hashCode();
    }
}
