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
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohdePerustiedotViewImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohteenLiitteetTabImpl;
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
    private TarjontaPresenter _presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    private TabSheet tabs;
    private TabSheet.Tab perustiedotTab;
    private TabSheet.Tab liitteetTab;
    private HakukohteenLiitteetTabImpl liitteet;
    
    public EditHakukohdeView() {
        super();
        setHeight(-1, UNITS_PIXELS);
        
    }
    
    public void enableLiitteetTab() {
        if (liitteetTab != null) {
            liitteetTab.setEnabled(true);
        }
    }
    
    public void loadLiiteTableWithData() {
        if (liitteet != null) {
            liitteet.reloadTableData();
        }
    }
    
    public void closeHakukohdeLiiteEditWindow() {
        if (liitteet != null) {
            liitteet.closeEditWindow();
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
        if (_presenter.getModel().getHakukohde() != null && _presenter.getModel().getHakukohde().getOid() != null) {
            hakukohdeOid = _presenter.getModel().getHakukohde().getOid();
        }
        
        tabs = UiBuilder.tabSheet(this);
        HakukohdePerustiedotViewImpl perustiedot = new HakukohdePerustiedotViewImpl(hakukohdeOid);
        
        liitteet = new HakukohteenLiitteetTabImpl();
        
        System.out.println("!!!!!!!!!!!!!! " + hakukohdeOid != null + " " + hakukohdeOid);
        
        perustiedotTab = tabs.addTab(perustiedot, T("tabNimi"));
        liitteetTab = tabs.addTab(liitteet, T("liitteetTab"));
        liitteetTab.setEnabled(hakukohdeOid != null);
        liitteet.reloadTableData();
    }
}
