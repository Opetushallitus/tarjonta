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
package fi.vm.sade.tarjonta.ui.model.valinta;

import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public class ValintaperusteModel extends BaseUIViewModel {
    /*
     * Form input fields
     */

    private String selectedUri;
    private List<KielikaannosViewModel> kuvaus;
    /*
     * Other data objects
     */
    private boolean loaded;

    public ValintaperusteModel() {
        reset(null);
    }

    public ValintaperusteModel(String uri) {
        reset(uri);
    }

    /**
     * @return the selectedUri
     */
    public String getSelectedUri() {
        return selectedUri;
    }

    /**
     * @param selectedUri the selectedUri to set
     */
    public void setSelectedUri(String selectedUri) {
        this.selectedUri = selectedUri;
    }

    /**
     * @return the kuvaus
     */
    public List<KielikaannosViewModel> getKuvaus() {
        return kuvaus;
    }

    /**
     * @param kuvaus the kuvaus to set
     */
    public void setKuvaus(List<KielikaannosViewModel> kuvaus) {
        this.kuvaus = kuvaus;
    }

    /**
     * @return the loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @param loaded the loaded to set
     */
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ValintaperusteModel other = (ValintaperusteModel) obj;

        EqualsBuilder builder = new EqualsBuilder();
        builder.append(kuvaus, other.kuvaus);
        builder.append(selectedUri, other.selectedUri);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(selectedUri)
                .append(kuvaus).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(selectedUri).append(kuvaus).append(loaded).toString();
    }

    public void reset(String uri) {
        setSelectedUri(uri);
        setLoaded(false);
        setKuvaus(new ArrayList<KielikaannosViewModel>());
    }
}
