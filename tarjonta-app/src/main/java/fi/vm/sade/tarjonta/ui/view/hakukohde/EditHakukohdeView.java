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

import com.vaadin.ui.TabSheet;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohdePerustiedotViewImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohdeValintakoeTabImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohteenLiitteetTabImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.ValintakoeViewImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Tuomas Katva
 */
@Configurable
public class EditHakukohdeView extends AbstractVerticalLayout {
    
    private static final long serialVersionUID = 8806220426371090907L;
    @Autowired
    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    private TabSheet tabs;
    private TabSheet.Tab perustiedotTab;
    private TabSheet.Tab liitteetTab;
    private TabSheet.Tab valintakokeetTab;
    private HakukohteenLiitteetTabImpl liitteet;
    private HakukohdeValintakoeTabImpl valintakokeet;
    private HakukohdePerustiedotViewImpl perustiedot;
    
    public EditHakukohdeView() {
        super();
        setHeight(-1, UNITS_PIXELS);
    }

    public void enableValintakokeetTab() {
        if (valintakokeetTab != null) {
            valintakokeetTab.setEnabled(true);
        }
    }
    
    public void enableLiitteetTab() {
        if (liitteetTab != null) {
            liitteetTab.setEnabled(true);
        }
    }
    
    public void loadLiiteTableWithData() {
        if (liitteet != null) {
            liitteet.loadTableWithData();
        }
    }

    public void loadValintakokees() {
        if (valintakokeet != null) {
            valintakokeet.getFormView().loadTableData();
        }
    }

    public void closeValintakoeEditWindow() {
        if (valintakokeet != null) {
            valintakokeet.getFormView().closeValintakoeEditWindow();
        }
    }
    
    public void closeHakukohdeLiiteEditWindow() {
        if (liitteet != null) {
            liitteet.closeEditWindow();
        }
    }

    public void showHakukohdeValintakoeEditView(String valintakoeId) {
        if (valintakokeet != null) {
            valintakokeet.getFormView().showValintakoeEditWithId(valintakoeId);
        }
    }
    
    public void showHakukohdeEditWindow(String liiteId) {
        if (liitteet != null) {
            liitteet.showHakukohdeEditWindow(liiteId);
        }
    }


    
    @Override
    protected void buildLayout() {
        String hakukohdeOid = null;
        if (presenter.getModel().getHakukohde() != null && presenter.getModel().getHakukohde().getOid() != null) {
            hakukohdeOid = presenter.getModel().getHakukohde().getOid();
        }
        

        tabs = UiBuilder.tabSheet(this);
        perustiedot = new HakukohdePerustiedotViewImpl(hakukohdeOid);


        
        liitteet = new HakukohteenLiitteetTabImpl();
        valintakokeet = new HakukohdeValintakoeTabImpl(hakukohdeOid);
        perustiedotTab = tabs.addTab(perustiedot, T("tabNimi"));
        valintakokeetTab = tabs.addTab(valintakokeet,T("valintakoeTab"));
        liitteetTab = tabs.addTab(liitteet, T("liitteetTab"));
        liitteetTab.setEnabled(hakukohdeOid != null);
        valintakokeetTab.setEnabled(hakukohdeOid != null);

    }

    public void setValintakokeetTabSelected() {
        if (tabs != null && valintakokeetTab != null) {

            tabs.setSelectedTab(valintakokeetTab);
        }
    }

    public void setLiitteetTabSelected() {
        if (tabs != null && liitteetTab != null) {
            tabs.setSelectedTab(liitteetTab);
        }
    }
}
