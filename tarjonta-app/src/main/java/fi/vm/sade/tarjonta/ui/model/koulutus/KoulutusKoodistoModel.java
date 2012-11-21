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
package fi.vm.sade.tarjonta.ui.model.koulutus;

import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Base model for KOMO/KOMOTO UI Comboboxes.
 *
 * @author Jani Wilén
 */
public abstract class KoulutusKoodistoModel extends BaseUIViewModel {

    protected String nimi;
    protected String kuvaus;
    protected String kielikoodi;
    protected String koodi;
    protected String koodistoUri;
    protected int koodistoVersio;
    protected String koodistoUriVersio;

    public KoulutusKoodistoModel() {
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
     * @return the koodi
     */
    public String getKoodi() {
        return koodi;
    }

    /**
     * @param koodi the koodi to set
     */
    public void setKoodi(String koodi) {
        this.koodi = koodi;
    }

    /**
     * @return the koodistoUri
     */
    public String getKoodistoUri() {
        return koodistoUri;
    }

    /**
     * @param koodistoUri the koodistoUri to set
     */
    public void setKoodistoUri(String koodistoUri) {
        this.koodistoUri = koodistoUri;
    }

    /**
     * @return the koodistoVersio
     */
    public int getKoodistoVersio() {
        return koodistoVersio;
    }

    /**
     * @param koodistoVersio the koodistoVersio to set
     */
    public void setKoodistoVersio(int koodistoVersio) {
        this.koodistoVersio = koodistoVersio;
    }

    /**
     * @return the koodistoUriVersio
     */
    public String getKoodistoUriVersio() {
        return koodistoUriVersio;
    }

    /**
     * @param koodistoUriVersio the koodistoUriVersio to set
     */
    public void setKoodistoUriVersio(String koodistoUriVersio) {
        this.koodistoUriVersio = koodistoUriVersio;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        KoulutusKoodistoModel other = (KoulutusKoodistoModel) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(nimi, other.nimi);
        builder.append(kielikoodi, other.kielikoodi);
        builder.append(koodi, other.koodi);
        builder.append(koodistoUri, other.koodistoUri);
        builder.append(koodistoVersio, other.koodistoVersio);
        builder.append(koodistoUriVersio, other.koodistoUriVersio);
        builder.append(kuvaus, other.kuvaus);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(nimi).
                append(kielikoodi).
                append(koodi).
                append(koodistoUri).
                append(koodistoVersio).
                append(koodistoUriVersio).
                append(kuvaus).toHashCode();
    }

    /**
     * @return the kuvaus
     */
    public String getKuvaus() {
        return kuvaus;
    }

    /**
     * @param kuvaus the kuvaus to set
     */
    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }
}
