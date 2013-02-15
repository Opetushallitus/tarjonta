/*
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
package fi.vm.sade.tarjonta.ui.model;

import fi.vm.sade.tarjonta.ui.model.valinta.ValintaperusteModel;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Tuomas Katva
 */
public class KielikaannosViewModel extends BaseUIViewModel {

    private String kielikoodi;
    private String nimi;

    public KielikaannosViewModel() {
    }

    public KielikaannosViewModel(String kielikoodiParam, String nimiParam) {
        this.kielikoodi = kielikoodiParam;
        this.nimi = nimiParam;
    }

    /**
     * @return the kielikoodi
     */
    public String getKielikoodi() {
        return kielikoodi;
    }

    /**
     * @param kielikoodi the kielikoodi to set
     */
    public void setKielikoodi(String kielikoodi) {
        this.kielikoodi = kielikoodi;
    }

    /**
     * @return the nimi
     */
    public String getNimi() {
        return nimi;
    }

    /**
     * @param nimi the nimi to set
     */
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        KielikaannosViewModel other = (KielikaannosViewModel) obj;

        EqualsBuilder builder = new EqualsBuilder();
        builder.append(kielikoodi, other.kielikoodi).
                append(nimi, other.nimi);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(kielikoodi)
                .append(nimi).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(kielikoodi).append(nimi).toString();
    }
}
