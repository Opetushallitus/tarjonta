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
package fi.vm.sade.tarjonta.poc.ui.view.hakukohde;

import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.vaadin.constants.StyleEnum;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Jani Wilén
 */
@Configurable(preConstruction = true)
public class CreateHakukohdeView extends AbstractVerticalNavigationLayout {
    
    public CreateHakukohdeView() {
        super(CreateHakukohdeView.class);
        
        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getPresenter().showMainKoulutusView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);
        
        addNavigationButton(T("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
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
                getPresenter().showShowHakukohdeView();               
            }
        });

//        addComponent(hl);
//        ViewBoundForm viewBound = new ViewBoundForm(new HorizontalTopNavigation());
//        viewBound.setCaption("Another example that uses annotations instead of naming convention");
//        // wrap pojo in a bean item and bind it to the form
//        viewBound.setItemDataSource(new BeanItem<MyExamplePojo>(secondExamplePojo));
    }
    
    @Override
    protected void buildLayout(VerticalLayout layout) {
        layout.setMargin(false, false, true, false);
        TabSheet tabHaut = new TabSheet();
        
        TabPerustiedotView tabPerustiedot = new TabPerustiedotView();
        tabHaut.addTab(tabPerustiedot, T("tabPerustiedot"), null);
        
        TabValintakokeenTiedotView tabValintakokeenTiedot = new TabValintakokeenTiedotView();
        tabHaut.addTab(tabValintakokeenTiedot, T("tabValintakokeidenTiedot"), null);
       
        TabLiitteidenTiedotView tabLiitteidenTiedot = new TabLiitteidenTiedotView();
        tabHaut.addTab(tabLiitteidenTiedot, T("tabLiitteidenTiedot"), null);
        
        layout.addComponent(tabHaut);
    }

}
