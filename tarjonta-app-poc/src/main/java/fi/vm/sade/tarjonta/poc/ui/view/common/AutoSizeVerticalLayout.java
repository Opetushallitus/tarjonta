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
package fi.vm.sade.tarjonta.poc.ui.view.common;

import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.constants.UiConstant;

/**
 *
 * @author Jani Wilén
 */
public class AutoSizeVerticalLayout extends VerticalLayout {

   public enum Type {

        PCT_100, AUTOSIZE
    };

    public AutoSizeVerticalLayout() {
       //setSizeUndefined();
    }

    public AutoSizeVerticalLayout(Type width, Type height) {
        //setSizeFull();
//        if (width != null) {
//            switch (width) {
//                case AUTOSIZE:
//                    setWidth(-1, UNITS_PIXELS);
//                    break;
//                case PCT_100:
//                    setWidth(UiConstant.PCT100);
//                    break;
//            }
//        }
//
//        if (height != null) {
//            switch (height) {
//                case AUTOSIZE:
//                    setHeight(-1, UNITS_PIXELS);
//                    break;
//                case PCT_100:
//                    setHeight(UiConstant.PCT100);
//                    break;
//            }
//        }
    }
}
