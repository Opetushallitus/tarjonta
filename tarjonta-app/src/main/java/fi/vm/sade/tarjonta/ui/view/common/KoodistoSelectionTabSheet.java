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
import com.vaadin.data.Property;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.vaadin.constants.UiConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple tabsheet with "+" tab containing koodisto twincol select so that
 * addition caused some action.
 *
 * @author mlyly
 */
public class KoodistoSelectionTabSheet extends TabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(KoodistoSelectionTabSheet.class);
    private static final String DEFAULT_LANGUAGE = "default.tab";
    private static final ThemeResource TAB_ICON_PLUS = new ThemeResource(UiConstant.RESOURCE_URL_OPH_IMG + "icon-add-black.png");
    private static final long serialVersionUID = 2357490409207157859L;
    private String _koodistoUri;
    private VerticalLayout _rootSelectionTabLayout = new VerticalLayout();
    private KoodistoComponent _kcSelection;
    // Map of tabs, key is the koodisto koodi uri
    private Map<String, Tab> _tabs = new HashMap<String, Tab>();
    private Set<String> removedTabs = new HashSet<String>(); //keep count of removed languages
    private transient UiBuilder uiBuilder;
    private transient TarjontaUIHelper uiHelper;

    /**
     * Createt tabsheet with given koodisto used for tabs "keys".
     *
     * @param koodistoUri
     */
    public KoodistoSelectionTabSheet(final String koodistoUri, final TarjontaUIHelper uiHelper, final UiBuilder uiBuilder) {
        super();
        Preconditions.checkNotNull(koodistoUri, "koodistoUri cannot be null");
        Preconditions.checkNotNull(uiHelper, "uiHelper cannot be null");
        Preconditions.checkNotNull(uiBuilder, "uiBuilder cannot be null");
        this.uiHelper = uiHelper;
        this.uiBuilder = uiBuilder;
        _koodistoUri = koodistoUri;
        _kcSelection = createKoodistoComponent();
        _kcSelection.setImmediate(true);

        buildSelectionTabAndAddMonitoring();
    }

    public Map<String, Tab> getTabs() {
        return _tabs;
    }

    public Tab getTab(String koodiUri) {
        return _tabs.get(koodiUri);
    }

    /**
     * Get tab koodi uri by a tab instance.
     *
     * @param tab
     * @return
     */
    public String getKoodiUri(Tab tab) {
        for (Entry<String, Tab> e : _tabs.entrySet()) {
            if (e.equals(tab)) {
                return e.getKey();
            }
        }

        return null;
    }

    public Tab addTab(String koodiUri, Component c, String caption) {
        Tab tab = super.addTab(c, caption);
        _tabs.put(koodiUri, tab);
        removedTabs.remove(koodiUri); //item unremoved (if any)
        return tab;
    }

    public Tab addTab(final String koodiUri, final Component c) {
        Preconditions.checkNotNull(koodiUri, "koodiUri cannot be null");
        Preconditions.checkNotNull(c, "Component cannot be null");
        Tab tab = super.addTab(c, uiHelper.getKoodiNimi(koodiUri));
        _tabs.put(koodiUri, tab);
        removedTabs.remove(koodiUri); //item unremoved (if any)
        return tab;
    }

    public Tab removeTab(String koodiUri) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("remove koodiUri : {}", koodiUri);
        }
        Tab tab = _tabs.remove(koodiUri);

        if (tab != null) {
            super.removeTab(tab);
            removedTabs.add(koodiUri); //add new removed item
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
     * Create selection tab with the selection component and add listener to
     * monitor changes.
     */
    private void buildSelectionTabAndAddMonitoring() {

        // Manage tab additions and deletions
        _kcSelection.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

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

        addLanguageMenuTab();
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
        return uiBuilder.koodistoTwinColSelect(_rootSelectionTabLayout, _koodistoUri, null, null);
    }

    /**
     * Add language menu tab.
     */
    protected void addLanguageMenuTab() {
        addTab(_rootSelectionTabLayout, "", TAB_ICON_PLUS);
    }

    /**
     * @return the removed item koodi uris
     */
    public Set<String> getRemoved() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getRemoved : {}", removedTabs);
        }

        return removedTabs;
    }

    /**
     * Set active pre-defined a language tab.
     */
    public void setSelectedTab() {
        final String finnishLangKoodiUri = getDefaultLanguageKoodiUri();
        TabSheet.Tab tab = getTab(finnishLangKoodiUri);
        if (tab != null) {
            setSelectedTab(tab);
        }
    }

    /**
     * Set active a language tab by Koodisto service koodi uri.
     */
    public void setSelectedTab(String koodiUri) {
        if (koodiUri == null) {
            setSelectedTab();
        } else {
            TabSheet.Tab tab = getTab(koodiUri);
            if (tab != null) {
                setSelectedTab(tab);
            }
        }
    }

    /**
     * Add default language.
     *
     * @param field
     */
    public void addDefaultLanguage(AbstractComponent field) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("default language added.");
        }

        final String soomiKieli = getDefaultLanguageKoodiUri();
        Set<String> kielet = new HashSet<String>();
        kielet.add(soomiKieli);
        addTab(soomiKieli, field, uiHelper.getKoodiNimi(soomiKieli));
        getKcSelection().setValue(kielet);
    }

    protected String getDefaultLanguageKoodiUri() {
        return I18N.getMessage(DEFAULT_LANGUAGE);
    }
}
