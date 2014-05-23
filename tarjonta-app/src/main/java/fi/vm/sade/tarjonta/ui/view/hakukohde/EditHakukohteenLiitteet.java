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

package fi.vm.sade.tarjonta.ui.view.hakukohde;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohteenLiitteetTabImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Jani Wilén
 */
public class EditHakukohteenLiitteet extends AbstractEditLayoutView<HakukohdeViewModel, HakukohteenLiitteetTabImpl>{

    @Autowired(required = true)
    private TarjontaPresenter presenter;

    public EditHakukohteenLiitteet(String oid) {
        super(oid, SisaltoTyyppi.HAKUKOHDE);
        setMargin(true);
        setHeight(-1, UNITS_PIXELS);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        super.buildLayout(layout); //init base navigation here
        HakukohteenLiitteetTabImpl formView = new HakukohteenLiitteetTabImpl(presenter);
        buildFormLayout( presenter, layout, presenter.getModel().getHakukohde(), formView);
    }

    @Override
    protected void eventBack(Button.ClickEvent event) {
        presenter.showMainDefaultView();
        presenter.getHakukohdeListView().reload();
    }

    @Override
    public void actionNext(Button.ClickEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isformDataLoaded() {
        return isLoaded();
    }

    @Override
    public String actionSave(SaveButtonState tila, Button.ClickEvent event) throws Exception {
        presenter.saveHakuKohde(tila);
        return getHakukohdeOid();
    }

    private String getHakukohdeOid() {
        return presenter.getModel().getHakukohde() != null ? presenter.getModel().getHakukohde().getOid() : null;
    }

    private boolean isLoaded() {
        return getHakukohdeOid() != null ? true : false;
    }
}