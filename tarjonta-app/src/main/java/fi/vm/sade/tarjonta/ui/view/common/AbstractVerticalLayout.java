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

import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;

/**
 * Build layout lazy way.
 *
 * @author Jani Wilén
 * @author mlyly
 */
public abstract class AbstractVerticalLayout extends VerticalLayout {
    private static final long serialVersionUID = -7968833366864022882L;

    private transient I18NHelper _i18n;

    /**
     * Build the layout only if not only done before.
     */
    protected boolean _initialized = false;

    protected String T(String key) {
       String retVal = getI18n().getMessage(key);
        return retVal;
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

    @Override
    public void attach() {
        super.attach();

        // Initialize only once
        if (!_initialized) {
            buildLayout();
            _initialized = true;
        }
    }

    /**
     * Implement this is subclasses.
     */
    protected abstract void buildLayout();

}

