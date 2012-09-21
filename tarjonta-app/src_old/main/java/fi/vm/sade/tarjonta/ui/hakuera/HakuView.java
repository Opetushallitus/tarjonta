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
package fi.vm.sade.tarjonta.ui.hakuera;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;

/**
 * The container for displaying the Haku listing and Hakulomake.
 * 
 * @author markus
 *
 */
public class HakuView extends HorizontalLayout {
    
    private HakueraEditForm hakuForm = new HakueraEditForm();
    private HakueraList hakueraList = new HakueraList();
    private VerticalLayout leftPanel; 
    private Button createButton; 
    
    public HakuView() {
        initComponents();
    }
    
    /**
     * Initialization of components and adding them to the layout.
     */
    private void initComponents() {
        leftPanel =  new VerticalLayout();
        
        //Create new hakuera button. initializes hakuForm and refreshes hakueraList.
        createButton = new Button(I18N.getMessage("HakuView.luoUusi"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                hakuForm.bind(new HakueraDTO());
                hakueraList.reload();
            }
        });
        createButton.setDebugId("luoUusiHakuera");
        leftPanel.addComponent(createButton);
        leftPanel.addComponent(hakueraList);
        addComponent(leftPanel);
        hakuForm.bind(new HakueraDTO());
        addComponent(hakuForm);
        hakueraList.getTable().addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                if (hakueraList.getTable().getValue() != null) {
                    hakuForm.populate((HakueraList.HakueraSimple) hakueraList.getTable().getValue());
                }
            }
        });
    }

    public HakueraEditForm getHakuForm() {
        return hakuForm;
    }

    public HakueraList getHakueraList() {
        return hakueraList;
    }
}
