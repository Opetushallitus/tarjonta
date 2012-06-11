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
import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.service.types.dto.HakueraDTO;
import fi.vm.sade.tarjonta.ui.AbstractSadeApplication;
import fi.vm.sade.tarjonta.ui.hakuera.event.HakueraSavedEvent;
import fi.vm.sade.tarjonta.ui.hakuera.event.HakueraSavedEvent.HakueraSavedEventListener;

/**
 * The container for displaying the Haku listing and Hakulomake.
 * 
 * @author markus
 *
 */
public class HakuView extends HorizontalLayout {
    
    private HakueraEditForm hakuForm = new HakueraEditForm();
    private HakueraList hakueraList = new HakueraList();
    
    public HakuView() {
        addComponent(hakueraList);
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
}
