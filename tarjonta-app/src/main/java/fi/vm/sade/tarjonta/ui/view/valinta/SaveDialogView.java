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
package fi.vm.sade.tarjonta.ui.view.valinta;

import fi.vm.sade.vaadin.util.UiUtil;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.vaadin.constants.UiConstant;

import fi.vm.sade.vaadin.ui.OphAbstractDialogWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
public class SaveDialogView extends OphAbstractDialogWindow {

    private static final Logger LOG = LoggerFactory.getLogger(SaveDialogView.class);
    private static final String INFO_TEXT = "informaatio";
    private static final String TITLE = "SaveDialogView.otsikko";
    private transient I18NHelper _i18n;

   
    public SaveDialogView() {
        super(null, I18N.getMessage(TITLE), null);
        setWidth("400px");
        setHeight("300px");
    }

    @Override
    public void buildLayout(VerticalLayout layout) {

        /* YOUR LAYOUT BETWEEN TOPIC AND BUTTONS */
        HorizontalLayout middleLayout = UiUtil.horizontalLayout();
        Panel newTextPanel = UiUtil.textPanel( T(INFO_TEXT), null, UiConstant.DEFAULT_RELATIVE_SIZE, middleLayout);
        newTextPanel.setHeight(UiConstant.DEFAULT_RELATIVE_SIZE);
        layout.addComponent(middleLayout);
    }

    /**
     * In case the window is closed otherwise.
     */
    @Override
    public void windowClose(CloseEvent e) {
        removeDialogButtons();
    }
    
     protected String T(String key) {
        return getI18n().getMessage(key);
    }

    protected String T(String key, Object... args) {
        return getI18n().getMessage(key, args);
    }

    protected I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }

}
