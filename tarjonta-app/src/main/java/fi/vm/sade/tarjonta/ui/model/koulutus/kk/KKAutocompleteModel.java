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

import fi.vm.sade.tarjonta.ui.model.AutocompleteModel;

/**
 *
 * @author Jani Wilén
 */
public class KKAutocompleteModel extends AutocompleteModel {

    private TutkintoohjelmaModel tutkintoohjelma;

    public KKAutocompleteModel(String text, TutkintoohjelmaModel tutkintoohjelma) {
        super();
        setText(text);
        setTutkintoohjelma(tutkintoohjelma);
    }

    public KKAutocompleteModel(TutkintoohjelmaModel tutkintoohjelma) {
        super();
        setText(tutkintoohjelma.getNimi());
        setTutkintoohjelma(tutkintoohjelma);
    }

    /**
     * @return the koulutusohjelma
     */
    public TutkintoohjelmaModel getTutkintoohjelma() {
        return tutkintoohjelma;
    }

    /**
     * @param koulutusohjelma the koulutusohjelma to set
     */
    public void setTutkintoohjelma(TutkintoohjelmaModel tutkintoohjelma) {
        this.tutkintoohjelma = tutkintoohjelma;
    }
}
