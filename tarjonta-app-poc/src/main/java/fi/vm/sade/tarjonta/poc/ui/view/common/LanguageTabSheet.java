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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class LanguageTabSheet extends TabSheet implements Property.ValueChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(LanguageTabSheet.class);
    private static final ThemeResource TAB_ICON_PLUS = new ThemeResource(UiConstant.RESOURCE_URL_OPH_IMG + "icon-add-black.png");
    private Map<String, Tab> selectedLanguages = new HashMap<String, Tab>();
    private TwinColSelectKoodisto twinColSelect;

    public LanguageTabSheet(String koodistoUri) {
        initialize(koodistoUri, null, null);
    }

    public LanguageTabSheet(String koodistoUri, PropertysetItem psi, String expression) {
        initialize(koodistoUri, psi, expression);
    }

    private void initialize(String koodistoUri, PropertysetItem psi, String expression) {
        twinColSelect = new TwinColSelectKoodisto(koodistoUri);
        twinColSelect.addListener(this);

        addTab(twinColSelect, "", TAB_ICON_PLUS);

        if (psi != null && expression != null) {
            twinColSelect.dataSource(psi, expression);
        }
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        //DEBUGSAWAY:LOG.debug("ValueChangeEvent : " + event);


        Object value = event.getProperty().getValue();
        if (value instanceof Collection) {
            for (String lang : twinColSelect.getLanguages()) {
                Collection<String> selected = (Collection<String>) value;
                if (!selected.contains(lang) && selectedLanguages.containsKey(lang)) {
                    //DEBUGSAWAY:LOG.debug("Remove " + lang);
                    removeTab(selectedLanguages.remove(lang));
                } else if (selected.contains(lang) && !selectedLanguages.containsKey(lang)) {
                    //DEBUGSAWAY:LOG.debug("Add " + lang);
                    addTextFieldTab(lang);
                }

            }
        } else {
            LOG.error("An unknown event object : " + event);
        }
    }

    private void addTextFieldTab(String caption) {
        TextField textField = UiUtil.textField(null);
        textField.setHeight("100px");
        textField.setWidth(UiConstant.PCT100);

        selectedLanguages.put(caption, addTab(textField, caption));
    }
}
