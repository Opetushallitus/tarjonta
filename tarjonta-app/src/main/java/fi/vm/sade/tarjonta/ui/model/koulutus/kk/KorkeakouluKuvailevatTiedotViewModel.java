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
package fi.vm.sade.tarjonta.ui.model.koulutus.kk;

import java.util.Map;

import com.google.common.collect.Maps;

import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import java.util.Collection;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class KorkeakouluKuvailevatTiedotViewModel extends BaseUIViewModel {

    private static final long serialVersionUID = 1L;
    // tekstikentät
    private Collection<String> ammattinimikkeet;
    private Map<String, KorkeakouluLisatietoModel> tekstit = Maps.<String, KorkeakouluLisatietoModel>newHashMap();

    public Map<String, KorkeakouluLisatietoModel> getTekstikentat() {
        return tekstit;
    }

    public void setLisatiedot(Map<String, KorkeakouluLisatietoModel> lisatiedot) {
        this.tekstit = lisatiedot;
    }

    public KorkeakouluKuvailevatTiedotViewModel() {
        super();
    }

    /**
     * Reset model state
     */
    public void clearModel() {

        tekstit.clear();
    }

    public KorkeakouluLisatietoModel getLisatiedot(String kieliKoodi) {
        if (!tekstit.containsKey(kieliKoodi)) {
            tekstit.put(kieliKoodi, new KorkeakouluLisatietoModel());
        }
        return tekstit.get(kieliKoodi);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        KorkeakouluKuvailevatTiedotViewModel other = (KorkeakouluKuvailevatTiedotViewModel) obj;

        EqualsBuilder builder = new EqualsBuilder();

        builder.append(ammattinimikkeet, other.ammattinimikkeet);
        builder.append(tekstit, other.tekstit);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(tekstit).append(ammattinimikkeet)
                .toHashCode();
    }

    /**
     * @return the ammattinimikkeet
     */
    public Collection<String> getAmmattinimikkeet() {
        return ammattinimikkeet;
    }

    /**
     * @param ammattinimikkeet the ammattinimikkeet to set
     */
    public void setAmmattinimikkeet(Collection<String> ammattinimikkeet) {
        this.ammattinimikkeet = ammattinimikkeet;
    }
}
