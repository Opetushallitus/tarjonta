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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.ui.AbstractComponent;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.common.KoodistoSelectionTabSheet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public abstract class LisatiedotTabSheet extends KoodistoSelectionTabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(LisatiedotTabSheet.class);
    private static final long serialVersionUID = 8350473574707759159L;
    private TarjontaModel tarjontaModel;
    private transient I18NHelper _i18n;

    public LisatiedotTabSheet(TarjontaModel tarjontaModel, TarjontaUIHelper uiHelper, UiBuilder uiBuilder) {
        super(KoodistoURI.KOODISTO_KIELI_URI, uiHelper, uiBuilder);
        this.tarjontaModel = tarjontaModel;

        // Initialize with all preselected languages
        initializeTabsheet(false);
    }

    @Override
    public void doAddTab(String uri) {
        addTab(uri, createLanguageEditor(uri));
    }

    protected abstract void initializeTabsheet(boolean allowDefault);

    protected void setInitialValues(final Set<String> values) {
        if (values != null) {
            for (String kieliKaannos : values) {
                addTab(kieliKaannos, createLanguageEditor(kieliKaannos));
            }
            getKcSelection().setValue(values);
        }
    }

    public void reload() {
        removeAllComponents();
        addLanguageMenuTab();
        initializeTabsheet(true);
    }

    /**
     * Create rich text editors for content editing.
     *
     * @param uri
     * @return
     */
    protected abstract AbstractComponent createLanguageEditor(String uri);

    /**
     * Get base UI model.
     *
     * @return
     */
    protected TarjontaModel getModel() {
        return this.tarjontaModel;
    }

    // Generic translatio helpers
    protected String T(String key) {
        return getI18n().getMessage(key);
    }

    protected String T(String key, Object... args) {
        return getI18n().getMessage(key, args);
    }

    private I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(getClass().getSimpleName() + ".");
        }
        return _i18n;
    }
}
