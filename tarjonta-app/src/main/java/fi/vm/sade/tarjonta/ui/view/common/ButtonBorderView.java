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
import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vertical button image layout.
 *
 * @author Jani Wilén
 */
public class ButtonBorderView<T extends AbstractComponentContainer> extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ButtonBorderView.class);
    private static final long serialVersionUID = -1245183033181006282L;
    private static final int BUTTON_PANEL_WIDTH = 34;
    private static final int BUTTON_PANEL_HEIGHT = 766;
    private Panel buttonPanel;
    private boolean isAttached = false;
    private Button viewToggleButton;

    public ButtonBorderView() {
        setWidth(-1, UNITS_PIXELS);
        viewToggleButton = UiUtil.button(null, "");
        viewToggleButton.setStyleName("vertical-collapse");
    }

    @Override
    public void attach() {
        super.attach();

        if (isAttached) {
            return;
        }
        isAttached = true;
        buildButtonLayout();
    }

    private void buildButtonLayout() {
        final VerticalLayout innerPanelLayout = UiUtil.verticalLayout(false, UiMarginEnum.NONE);
        innerPanelLayout.setSizeFull();
        buttonPanel = UiUtil.panel(null, null, innerPanelLayout);
        buttonPanel.setWidth(BUTTON_PANEL_WIDTH, UNITS_PIXELS);
        buttonPanel.setHeight(BUTTON_PANEL_HEIGHT, UNITS_PIXELS);
        buttonPanel.setScrollable(false);

        viewToggleButton.setSizeUndefined();
        innerPanelLayout.addComponent(viewToggleButton);
        super.addComponent(buttonPanel);
        super.setComponentAlignment(buttonPanel, Alignment.TOP_LEFT);
    }

    public void setButtonListener(Button.ClickListener listener) {
        Preconditions.checkNotNull(listener, "Show / hide organisation layout toggle listener cannot be null.");

        viewToggleButton.addListener(listener);
    }
}
