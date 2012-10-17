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
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.poc.ui.TarjontaPresenter;
import fi.vm.sade.vaadin.ui.OphAbstractNavigationLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public abstract class AbstractVerticalNavigationLayout extends OphAbstractNavigationLayout<VerticalLayout> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractVerticalNavigationLayout.class);
    private I18NHelper _i18n;
    @Autowired(required = true)
    private TarjontaPresenter _presenter;

    public AbstractVerticalNavigationLayout(Class clazz) {
        super(VerticalLayout.class);
        _i18n = new I18NHelper(clazz);
    }

    protected String T(String key) {
        return _i18n.getMessage(key);
    }

    /**
     * @return the application presenter instance.
     */
    protected TarjontaPresenter getPresenter() {
        return _presenter;
    }

    /**
     * @return the I18N instance.
     */
    protected I18NHelper getI18n() {
        return _i18n;
    }
}
