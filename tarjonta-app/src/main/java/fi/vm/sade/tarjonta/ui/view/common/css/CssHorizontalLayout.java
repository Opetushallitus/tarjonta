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
package fi.vm.sade.tarjonta.ui.view.common.css;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

/**
 *
 * @author Jani Wilén
 */
public class CssHorizontalLayout extends CssLayout {

    private final static int NO_PARAMETER = -1;

    public enum StyleEnum {

        LEFT("component-left"),
        PADDING_RIGHT_5PX("component-spacing-right-extra-1"),
        PADDING_RIGHT_10PX("component-spacing-right-1"),
        PADDING_RIGHT_20PX("component-spacing-right-2"),
        PADDING_RIGHT_30PX("component-spacing-right-3"),
        PADDING_LEFT_5PX("component-spacing-left-extra-1"),
        PADDING_LEFT_10PX("component-spacing-left-1"),
        PADDING_LEFT_20PX("component-spacing-left-2"),
        PADDING_LEFT_30PX("component-spacing-left-3");
        private final String padding;

        StyleEnum(String padding) {
            this.padding = padding;
        }

        public String getStyleName() {
            return padding;
        }
    }

    public CssHorizontalLayout() {
        setDebug(false);
        this.setSizeUndefined();
    }

    public CssLayout addComponent(Component component, StyleEnum[] styles) {
        CssLayout subContainer = new CssLayout();
        subContainer.setSizeUndefined();
        subContainer.addStyleName("wrap-container");
        subContainer.addComponent(component);

        if (styles != null && styles.length > 0) {
            for (StyleEnum s : styles) {
                subContainer.addStyleName(s.getStyleName());
            }
        }

        super.addComponent(subContainer);

        return this;
    }

    @Override
    public void addComponent(Component component) {
        this.addComponent(component, (StyleEnum) null);
    }

    public CssLayout addComponent(Component component, StyleEnum style) {
        return this.addComponent(component, style != null ? new StyleEnum[]{style} : null);
    }

    public void setDebug(boolean on) {
        if (on) {
            this.setStyleName("wrap-debug");
        } else {
            this.setStyleName("wrap");
        }
    }
}
