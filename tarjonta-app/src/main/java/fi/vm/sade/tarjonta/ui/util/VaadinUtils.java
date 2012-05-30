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
package fi.vm.sade.tarjonta.ui.util;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

/**
 *
 * @author Jukka Raanamo
 */
public class VaadinUtils {
    
    /**
     * Returns new TextField showing null's as empty string.
     * 
     * @return
     */
    public static TextField newTextField() {
        
        TextField f = new TextField();
        f.setNullRepresentation("");
        return f;
        
    }
    
    
    public static HorizontalLayout newTwoColumnHorizontalLayout(Component left, Component right) {

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(left);
        layout.addComponent(right);
        return layout;

    }
}

