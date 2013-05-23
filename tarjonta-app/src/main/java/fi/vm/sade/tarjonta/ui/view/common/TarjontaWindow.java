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

import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiBaseUtil;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 *
 * @author Jani Wilén
 */
public abstract class TarjontaWindow extends Window implements Window.CloseListener {

    public static final String WINDOW_TITLE_PROPERTY = "dialogTitle";
    private static final long serialVersionUID = -4980885940929735113L;
    /**
     * Build the layout only if not only done before.
     */
    protected boolean _initialized = false;
    private VerticalLayout wrapper;
    private VerticalLayout view;
    private ErrorMessage errorMessage;
    private String height;
    private String width;

    public TarjontaWindow(String width, String height) {
        super();
        this.width = width;
        this.height = height;

        init(true);
    }
    private transient I18NHelper _i18n;

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

    protected void init(boolean modal) {
        setSizeUndefined();
        center();
        setModal(true);
        setScrollable(true);

        HorizontalLayout errorArea = buildErrorLayout();

        view = UiUtil.verticalLayout(true, UiMarginEnum.NONE);
        //Width and height must be defined or scrollbars are not shown on window.
        UiBaseUtil.handleWidth(view, width); //set default width
        UiBaseUtil.handleHeight(view, height);  //set default height

        wrapper = UiUtil.verticalLayout(true, UiMarginEnum.TOP);
        wrapper.setSizeUndefined();
        wrapper.addComponent(errorArea);
        wrapper.addComponent(view);

        this.setContent(wrapper);

    }

    @Override
    public void attach() {
        super.attach();

        // Initialize only once
        if (!_initialized) {
            buildLayout(view);
            _initialized = true;
        }
    }

    public abstract void buildLayout(VerticalLayout layout);

    private HorizontalLayout buildErrorLayout() {
        HorizontalLayout topErrorArea = UiUtil.horizontalLayout();
        HorizontalLayout padding = UiUtil.horizontalLayout();
        padding.setWidth(30, UNITS_PERCENTAGE);
        errorMessage = new ErrorMessage();
        errorMessage.setSizeUndefined();

        topErrorArea.addComponent(padding);
        topErrorArea.addComponent(errorMessage);

        return topErrorArea;
    }

    public ErrorMessage getErrorView() {
        return errorMessage;
    }
}
