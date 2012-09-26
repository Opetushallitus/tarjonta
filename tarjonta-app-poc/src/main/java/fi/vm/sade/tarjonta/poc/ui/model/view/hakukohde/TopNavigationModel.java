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

package fi.vm.sade.tarjonta.poc.ui.model.view.hakukohde;

import com.vaadin.ui.Button;

/**
 *
 * @author Jani Wilén
 */
public class TopNavigationModel {

    private Button btnBack;

    private Button btnSaveDraft;

    private Button btnSave;

    private Button btnNext;

    /**
     * @return the btnBack
     */
    public Button getBtnBack() {
        return btnBack;
    }

    /**
     * @param btnBack the btnBack to set
     */
    public void setBtnBack(Button btnBack) {
        this.btnBack = btnBack;
    }

    /**
     * @return the btnSaveDraft
     */
    public Button getBtnSaveDraft() {
        return btnSaveDraft;
    }

    /**
     * @param btnSaveDraft the btnSaveDraft to set
     */
    public void setBtnSaveDraft(Button btnSaveDraft) {
        this.btnSaveDraft = btnSaveDraft;
    }

    /**
     * @return the btnSave
     */
    public Button getBtnSave() {
        return btnSave;
    }

    /**
     * @param btnSave the btnSave to set
     */
    public void setBtnSave(Button btnSave) {
        this.btnSave = btnSave;
    }

    /**
     * @return the btnNext
     */
    public Button getBtnNext() {
        return btnNext;
    }

    /**
     * @param btnNext the btnNext to set
     */
    public void setBtnNext(Button btnNext) {
        this.btnNext = btnNext;
    }
    
}
