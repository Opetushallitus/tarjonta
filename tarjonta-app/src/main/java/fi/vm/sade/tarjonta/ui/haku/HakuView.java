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
package fi.vm.sade.tarjonta.ui.haku;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * The container for displaying the Haku listing and Hakulomake.
 * 
 * @author markus
 *
 */
public class HakuView extends HorizontalLayout {
    
    private HakuEditForm hakuForm;
    Label hakuListing;
    
    public HakuView() {
        hakuListing = new Label("TODO: hakulistaus!!!");
        addComponent(hakuListing);
        hakuForm = new HakuEditForm();
        addComponent(hakuForm);
    }
}
