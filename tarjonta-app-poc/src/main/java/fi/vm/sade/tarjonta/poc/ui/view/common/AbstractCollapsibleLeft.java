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

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collapsible layout with a vertical image button.
 *
 * @author Jani Wilén
 */
public abstract class AbstractCollapsibleLeft<T extends AbstractComponentContainer> extends HorizontalLayout {
    //Open:               // Close: 
    //---------------->   //<----
    //_________________   //____
    //|           |   |   //|   |
    //| Your      | b |   //| b |  
    //| layout    | u |   //| u |
    //| container | t |   //| t |
    //|           | t |   //| t |
    //|           | o |   //| o |
    //|           | n |   //| n |
    //|___________|___|   //|___|
    //                    //
    //---------------->   //<----

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCollapsibleLeft.class);
    private T container;

    public AbstractCollapsibleLeft(Class<T> containertClass) {
        initialize(containertClass);
        setWidth(100, UNITS_PERCENTAGE);
    }

    private void initialize(Class<T> containerClass) {
        if (containerClass == null) {
            throw new RuntimeException("An invalid constructor argument - the generic container class instance cannot be null.");
        }
        try {
            this.container = containerClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Abstract class cannot initialize container class.", ex);
        }

        buildLayout(container);
        super.addComponent(container);
        container.setSizeUndefined();

        buildButtonLayout();
    }

    /**
     * Add components to left panel container.
     *
     * @param container
     */
    protected abstract void buildLayout(T container);

    /**
     * Add components to left panel container.
     *
     * @param component
     */
    protected void addLayoutComponent(Component component) {
        container.addComponent(component);
    }

    /**
     * Get a component container.
     *
     * @return AbstractComponentContainer
     */
    protected T getLayout() {
        return container;
    }

    private void buildButtonLayout() {
        final VerticalLayout innerPanelLayout = UiUtil.verticalLayout(false, UiMarginEnum.NONE);
        final Panel buttonPanel = UiUtil.panel(null, null, innerPanelLayout);
        buttonPanel.setWidth(32, UNITS_PIXELS);

        final Button viewToggleButton = UiUtil.button(innerPanelLayout, "", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (container.isVisible()) {
                    container.setVisible(false);
                    buttonPanel.setHeight(getWindow().getHeight(), UNITS_PIXELS);
                } else {
                    container.setVisible(true);
                    buttonPanel.setHeight(getWindow().getHeight(), UNITS_PIXELS);
                }
            }
        });

        viewToggleButton.setStyleName("vertical-collapse");
        viewToggleButton.setSizeUndefined();
        super.addComponent(buttonPanel);
    }
}
