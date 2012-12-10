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

import com.vaadin.data.Property;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.vaadin.constants.UiConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple tabsheet with "+" tab containing koodisto twincol select so that addition caused some action.
 *
 * @author mlyly
 */
public class KoodistoSelectionTabSheet extends TabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(KoodistoSelectionTabSheet.class);

    private static final ThemeResource TAB_ICON_PLUS = new ThemeResource(UiConstant.RESOURCE_URL_OPH_IMG + "icon-add-black.png");

    private String _koodistoUri;

    private VerticalLayout _rootSelectionTabLayout = new VerticalLayout();
    private KoodistoComponent _kcSelection;

    // Map of tabs, key is the koodisto koodi uri
    private Map<String, Tab> _tabs = new HashMap<String, Tab>();

    /**
     * Createt tabsheet with given koodisto used for tabs "keys".
     *
     * @param koodistoUri
     */
    public KoodistoSelectionTabSheet(String koodistoUri) {
        super();
        _koodistoUri = koodistoUri;
        _kcSelection = createKoodistoComponent();
        buildSelectionTabAndAddMonitoring();
    }

    public Map<String, Tab> getTabs() {
        return _tabs;
    }

    public Tab getTab(String koodiUri) {
        return _tabs.get(koodiUri);
    }

    public Tab addTab(String koodiUri, Component c, String caption) {
        Tab tab = super.addTab(c, caption);
        _tabs.put(koodiUri, tab);
        return tab;
    }

    public Tab removeTab(String koodiUri) {
        Tab tab = _tabs.remove(koodiUri);

        if (tab != null) {
            super.removeTab(tab);
        }
        return tab;
    }

    /**
     * Use to get the internal KoodistoComponent.
     *
     * @return
     */
    public KoodistoComponent getKcSelection() {
        return _kcSelection;
    }

    /**
     * Create selection tab with the selection component and add listener to monitor changes.
     */
    private void buildSelectionTabAndAddMonitoring() {

        // Manage tab additions and deletions
        _kcSelection.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LOG.info("new values: {}", event.getProperty().getValue());

                // New values
                Set<String> values = (Set<String>) event.getProperty().getValue();

                List<String> tabsToBeRemoved = new ArrayList<String>();

                // Check for tab removals
                for (String uri : _tabs.keySet()) {
                    if (!values.contains(uri) && uri != null) {
                        tabsToBeRemoved.add(uri);
                    }
                }

                // Check for additions
                for (String uri : values) {
                    if (!_tabs.containsKey(uri) && uri != null) {
                        doAddTab(uri);
                    }
                }

                // Remove needed tabs
                for (String uri : tabsToBeRemoved) {
                    removeTab(uri);
                }
            }
        });

        addTab(_rootSelectionTabLayout, "", TAB_ICON_PLUS);
    }

    /**
     * Override this to actually add the new tab. Only dummy tab added here.
     *
     * Call "addTab(uri, component, caption)" to do the adding.
     *
     * @param uri
     */
    public void doAddTab(String uri) {
        this.addTab(uri, new Label(uri), uri);
    }

    /**
     * By default TwinColSelect is created, override to create something else.
     *
     * @return
     */
    public KoodistoComponent createKoodistoComponent() {
        // Create koodisto component
        return UiBuilder.koodistoTwinColSelect(_rootSelectionTabLayout, _koodistoUri, null, null);
    }
}
