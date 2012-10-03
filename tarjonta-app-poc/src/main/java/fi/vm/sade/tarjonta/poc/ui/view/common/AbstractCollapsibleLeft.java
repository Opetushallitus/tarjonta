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
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Collapsible layout with a vertical image button.
 *
 * @author Jani Wilén
 */
public abstract class AbstractCollapsibleLeft<T extends AbstractComponentContainer> extends HorizontalLayout {
    //Open:            // Close: 
    //------------>    //<----
    //______________   //____
    //|        |   |   //|   |
    //| Your   | b |   //| b |  
    //| layout | u |   //| u |
    //|        | t |   //| t |
    //|        | t |   //| t |
    //|        | o |   //| o |
    //|        | n |   //| n |
    //|________|___|   //|___|
    //                 //
    //------------->   //<----

    private Panel buttonPanel;
    private T container;
    private Button viewToggleButton;
    private boolean mode;

    public AbstractCollapsibleLeft(Class<T> containertClass) {
        initialize(containertClass);
    }

    public AbstractCollapsibleLeft(Class<T> containertClass, boolean visible) {
        mode = visible;
        initialize(containertClass);
    }

    private void initialize(Class<T> containerClass) {
        setHeight(-1, UNITS_PIXELS);
        setWidth(-1, UNITS_PIXELS);

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
        buildButtonLayout();
    }

    protected abstract void buildLayout(T container);

    @Override
    public void addComponent(Component c) {
        container.addComponent(c);
    }

    private void buildButtonLayout() {
        VerticalLayout innerPanelLayout = UiUtil.verticalLayout(false, UiMarginEnum.NONE);
        buttonPanel = UiUtil.panel(UiConstant.DEFAULT_REALTIVE_SIZE, UiConstant.PCT100, innerPanelLayout);
        viewToggleButton = UiUtil.button(innerPanelLayout, "", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (container.isVisible()) {
                    container.setVisible(mode);
                } else {
                    container.setVisible(!mode);
                }
                
                container.setSizeFull();
            }
        });

        viewToggleButton.setStyleName("vertical-collapse");
        viewToggleButton.setHeight(-1, UNITS_PIXELS);
        viewToggleButton.setWidth(-1, UNITS_PIXELS);
        super.addComponent(buttonPanel);
    }

    /**
     * @return the collapsible button instance
     */
    protected Button getCollapsibleButton() {
        return viewToggleButton;
    }
}
