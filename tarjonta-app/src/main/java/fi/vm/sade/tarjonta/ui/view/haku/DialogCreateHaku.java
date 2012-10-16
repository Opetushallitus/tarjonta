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
package fi.vm.sade.tarjonta.ui.view.haku;

import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.vaadin.ui.OphAbstractDialogWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable
public class DialogCreateHaku extends OphAbstractDialogWindow {

    private static final Logger LOG = LoggerFactory.getLogger(DialogCreateHaku.class);

    private I18NHelper _i18nHelper = new I18NHelper(DialogCreateHaku.class);

    public DialogCreateHaku() {
        super("windowLabel", "topic", "message");
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String T(String key) {
        return _i18nHelper.getMessage(key);
    }

}
