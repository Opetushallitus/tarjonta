
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

package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import fi.vm.sade.tarjonta.ui.view.common.TwinColSelectKoodisto;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.util.UiUtil;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
/**
 *
 * @author Tuomas Katva
 */
@Configurable
public class LanguageTabSheet extends TabSheet implements Property.ValueChangeListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(LanguageTabSheet.class);
    private static final ThemeResource TAB_ICON_PLUS = new ThemeResource(UiConstant.RESOURCE_URL_OPH_IMG + "icon-add-black.png");
    private Map<String, TabSheet.Tab> selectedLanguages = new HashMap<String, TabSheet.Tab>();
    private TwinColSelectKoodisto twinColSelect;
    private List<KielikaannosViewModel> languageValues = null;
    

    public LanguageTabSheet(String koodistoUri) {
        initialize(koodistoUri, null, null);
    }

    public LanguageTabSheet(String koodistoUri, PropertysetItem psi, String expression) {
        initialize(koodistoUri, psi, expression);
    }
    
    public LanguageTabSheet(String koodistoUri, List<KielikaannosViewModel> values) {
        initialize(koodistoUri, values);
    }

    private void initialize(String koodistoUri, PropertysetItem psi, String expression) {
        twinColSelect = new TwinColSelectKoodisto(koodistoUri);
        twinColSelect.addListener(this);
        
        addTab(twinColSelect, "", TAB_ICON_PLUS);

        if (psi != null && expression != null) {
            twinColSelect.dataSource(psi, expression);
        }
    }
    @PostConstruct
    private void initializeTabs() {
        if (languageValues != null) {
            setInitialValues(languageValues);
        }
        languageValues = null;
        twinColSelect.addListener(this);
    }
    
    private void initialize(String koodistoUri, List<KielikaannosViewModel> values) {
        twinColSelect = new TwinColSelectKoodisto(koodistoUri);
        addTab(twinColSelect, "", TAB_ICON_PLUS);
        languageValues = values;
        
     
    }
    
    public List<KielikaannosViewModel> getKieliKaannokset() {
        languageValues = new ArrayList<KielikaannosViewModel>();
        for (String key : selectedLanguages.keySet()) {
            Tab selectedTab = selectedLanguages.get(key);
            Component component = selectedTab.getComponent();
            if (component instanceof TextField) {
                TextField txtField = (TextField)component;
                KielikaannosViewModel kieli = new KielikaannosViewModel(key, txtField.getValue().toString());
                languageValues.add(kieli);
            } else {
                LOG.warn("Tab component not a TextField");
            }
            
        }
        
        return languageValues;
    }
    
    private void setInitialValues(List<KielikaannosViewModel> values) {
        if (values != null) {
            Set<String> kielet = new HashSet<String>();
            for (KielikaannosViewModel kieliKaannos : values) {
                kielet.add(kieliKaannos.getKielikoodi());
                addKieliKaannosTab(kieliKaannos);
            }
             twinColSelect.setValue(kielet);
        }
    }
    
    private void addKieliKaannosTab(KielikaannosViewModel kaannos) {
        addTextFieldTab(kaannos.getKielikoodi(), kaannos.getNimi());
        
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        LOG.debug("ValueChangeEvent : " + event);


        Object value = event.getProperty().getValue();
        if (value instanceof Collection) {
            for (String lang : twinColSelect.getLanguages()) {
                Collection<String> selected = (Collection<String>) value;
                if (!selected.contains(lang) && selectedLanguages.containsKey(lang)) {
                    LOG.debug("Remove " + lang);
                    removeTab(selectedLanguages.remove(lang));
                } else if (selected.contains(lang) && !selectedLanguages.containsKey(lang)) {
                    LOG.debug("Add " + lang);
                    addTextFieldTab(lang);
                }

            }
        } else {
            LOG.error("An unknown event object : " + event);
        }
    }
    
    private void addTextFieldTab(String uri, String teksti) {
        TextField textField = UiUtil.textField(null);
        textField.setValue(teksti);
        textField.setHeight("100px");
        textField.setWidth(UiConstant.PCT100);
        String caption = twinColSelect.getCaptionFor(uri);
        selectedLanguages.put(uri, addTab(textField, caption));
    }
            

    private void addTextFieldTab(String uri) {
        TextField textField = UiUtil.textField(null);
        textField.setHeight("100px");
        textField.setWidth(UiConstant.PCT100);
        String caption = twinColSelect.getCaptionFor(uri);
        
        selectedLanguages.put(uri, addTab(textField,caption));
    }

}
