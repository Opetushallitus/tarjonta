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
package fi.vm.sade.tarjonta.ui.view.common;

import com.google.common.base.Preconditions;
import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Alignment;
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
public abstract class OphAbstractCollapsibleLeft<T extends AbstractComponentContainer> extends HorizontalLayout {
    //Open button click:  // Close: 
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

    private static final Logger LOG = LoggerFactory.getLogger(OphAbstractCollapsibleLeft.class);
    private static final long serialVersionUID = -1245183033181006282L;
    private static final int BUTTON_PANEL_WIDTH = 32;
    private static final int BUTTON_PANEL_HEIGHT = 800;
    private T container;
    private Panel buttonPanel;
    private boolean isAttached = false;

    public OphAbstractCollapsibleLeft(Class<T> containertClass) {
        initialize(containertClass);
        setWidth(100, UNITS_PERCENTAGE);

        //Add collapsible panel.
        super.addComponent(container);

        //Add button layout to base layout.
        buildButtonLayout();
    }

    @Override
    public void attach() {
        super.attach();

        if (isAttached) {
            /*
             * We need to manually set width of the button panel container back to max height. 
             * Without this the page that uses this class in a view component, 
             * in some cases its height will be reset to -1 and an user will only see a white page.
             * 
             * The problem occurs at least when the user has decided to hide organisation 
             * navigation panel. 
             */
            buttonPanel.setVisible(true);
            buttonPanel.setWidth(BUTTON_PANEL_WIDTH, UNITS_PIXELS);

            float height = getWindow().getHeight();
            if (height < BUTTON_PANEL_HEIGHT) {
                //just to be sure that the browser window height will never 
                //be less than 800px
                height = BUTTON_PANEL_HEIGHT;
            }

            buttonPanel.setHeight(height, UNITS_PIXELS);
            return;
        }

        isAttached = true;

        LOG.debug("attach : OphAbstractCollapsibleLeft()");
        buildLayout(container);

    }

    private void initialize(Class<T> containerClass) {
        LOG.debug("in initialize");
        Preconditions.checkNotNull(containerClass, "An invalid constructor argument - the generic container class instance cannot be null.");

        try {
            this.container = containerClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Abstract class cannot initialize container class.", ex);
        }
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
        innerPanelLayout.setSizeFull();
        innerPanelLayout.setWidth(-1, UNITS_PIXELS);
        buttonPanel = UiUtil.panel(null, null, innerPanelLayout);
        buttonPanel.setWidth(BUTTON_PANEL_WIDTH, UNITS_PIXELS);

        final Button viewToggleButton = UiUtil.button(innerPanelLayout, "", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (container.isVisible()) {
                    container.setVisible(false);
                    // buttonPanel.setHeight(getWindow().getHeight(), UNITS_PIXELS);
                } else {
                    container.setVisible(true);
                    // buttonPanel.setHeight(getWindow().getHeight(), UNITS_PIXELS);
                }
            }
        });

        viewToggleButton.setStyleName("vertical-collapse");
        viewToggleButton.setSizeUndefined();
        super.addComponent(buttonPanel);
        super.setComponentAlignment(buttonPanel, Alignment.TOP_LEFT);
    }

    public Panel getButtonPanel() {
        return buttonPanel;
    }
}
