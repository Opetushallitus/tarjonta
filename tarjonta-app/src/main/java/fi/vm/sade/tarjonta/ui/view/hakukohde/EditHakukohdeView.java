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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotViewImpl;
import fi.vm.sade.vaadin.constants.StyleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
/**
 *
 * @author Tuomas Katva
 */
@Configurable
public class EditHakukohdeView extends AbstractVerticalNavigationLayout {

    @Autowired
    private TarjontaPresenter _presenter;
    private TabSheet tabs;
    boolean attached = false;
    
    public EditHakukohdeView() {
        super(EditHakukohdeView.class);
        setHeight(-1, UNITS_PIXELS);
    }
    
     @Override
    public void attach() {
        if (attached) {
            return;
        }

        attached = true;
        super.attach();

        tabs = new TabSheet();
        tabs.setHeight(-1, UNITS_PIXELS);
        addComponent(tabs);
        
        tabs.addTab(new PerustiedotViewImpl(_presenter),T("PerustiedotView.tabNimi"));
        createButtons();
     }
    
    @Override
    protected void buildLayout(VerticalLayout t) {
        
    }

    private void createButtons() {
        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
//                getPresenter().showMainKoulutusView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);
        
        addNavigationButton(T("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                _presenter.saveHakuKohde();
            }
        });
        
        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        addNavigationButton(T("jatka"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
//                getPresenter().showShowHakukohdeView();               
            }
        });
    }
    
}