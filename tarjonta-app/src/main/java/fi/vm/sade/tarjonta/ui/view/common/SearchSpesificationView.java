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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.NestedMethodProperty;
import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;
import fi.vm.sade.tarjonta.ui.view.common.css.CssHorizontalLayout;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.ui.OphHorizontalLayout;
import fi.vm.sade.vaadin.util.UiUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

import java.util.HashMap;

/**
 * This is the search controller and spesification component used to search Haku
 * and Tarjonta.
 *
 * @author mlyly
 */
@Configurable
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class SearchSpesificationView extends OphHorizontalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(SearchSpesificationView.class);
    private static final String I18N_PROMPT = ".prompt";
    private static final String I18N_VUOSI = "vuosi";
    private static final String I18N_KAUSI = "kausi";
    private static final long serialVersionUID = 425330075453507227L;
    private final transient I18NHelper i18nHelper = new I18NHelper(this);
    private TextField tfSearch;
    private Button btnHae;
    private ComboBox cbVuosi;
    private ComboBox cbTilat;
    private KoodistoComponent kcKausi;
    private Button btnTyhjenna;
    private boolean attached = false;

    private HashMap<String, String> tilaMap;
    /* Model for search spesifications */
    private KoulutusSearchSpesificationViewModel model = new KoulutusSearchSpesificationViewModel();
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    public SearchSpesificationView() {
        super(true, UiMarginEnum.BOTTOM);
    }

    public SearchSpesificationView(KoulutusSearchSpesificationViewModel model) {
        super(true, UiMarginEnum.BOTTOM);
        this.model = model;
    }

    @Override
    public void attach() {
        super.attach();
        if (attached) {
            return;
        }

        attached = true;
        buildLayout();
    }

    protected void buildLayout() {
        setSizeFull();
        //without the height parameter result area would be hidden.
        //
        // Create fields
        //
        // HorizontalLayout searchTextLayout = new HorizontalLayout();
        tfSearch = new TextField("");
        tfSearch.addStyleName(Oph.TEXTFIELD_SEARCH);
        tfSearch.setNullRepresentation("");
        tfSearch.setPropertyDataSource(new NestedMethodProperty(model, "searchStr"));
        tfSearch.setWidth(240, UNITS_PIXELS);

        tfSearch.setImmediate(true);
        tfSearch.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent valueChangeEvent) {
                doSearch();
            }
        });

        // searchTextLayout.addComponent(tfSearch);
        //
        // Hook enter to do the search
        //
        tfSearch.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                doSearch();
            }
        });

        addComponent(tfSearch);

        CssHorizontalLayout searchSpecLayout = new CssHorizontalLayout();
        VerticalLayout tilatLayout = new VerticalLayout();
        tilatLayout.setSizeUndefined();
        Label tilatLabel = UiUtil.label(null, T("koulutuksenTilat")); 
        tilatLayout.addComponent(tilatLabel);
        cbTilat = UiUtil.comboBox(null, null, getKoulutuksenTilat());
        cbTilat.setSizeUndefined();
        tilatLayout.addComponent(cbTilat);
        searchSpecLayout.addComponent(tilatLayout, CssHorizontalLayout.StyleEnum.PADDING_RIGHT_10PX);

        VerticalLayout vuosiLayout = new VerticalLayout();
        Label vuosiLabel = UiUtil.label(null, T(I18N_VUOSI));
        vuosiLayout.addComponent(vuosiLabel);
        cbVuosi = UiUtil.comboBox(null, null, new String[]{T(I18N_VUOSI + I18N_PROMPT), "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025"});
        cbVuosi.setNullSelectionAllowed(true);
        cbVuosi.setNullSelectionItemId(T(I18N_VUOSI + I18N_PROMPT));
        cbVuosi.setSizeUndefined();
        vuosiLayout.addComponent(cbVuosi);
        vuosiLayout.setWidth("165px");
        searchSpecLayout.addComponent(vuosiLayout, CssHorizontalLayout.StyleEnum.PADDING_RIGHT_10PX);

        VerticalLayout kausiLayout = new VerticalLayout();
        Label kausiLabel = UiUtil.label(null, T(I18N_KAUSI));
        kausiLayout.addComponent(kausiLabel);
        kcKausi = uiBuilder.koodistoComboBox(this, KoodistoURI.KOODISTO_ALKAMISKAUSI_URI, null, null, T(I18N_KAUSI + I18N_PROMPT));
        kcKausi.setSizeUndefined();
        kcKausi.getField().setNullSelectionAllowed(false);
        kausiLayout.addComponent(kcKausi);
        kausiLayout.setWidth("165px");
        searchSpecLayout.addComponent(kausiLayout, CssHorizontalLayout.StyleEnum.PADDING_RIGHT_10PX);

        HorizontalLayout buttons = new HorizontalLayout();

        btnTyhjenna = UiBuilder.buttonSmallSecodary(null, T("tyhjenna"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                tfSearch.setValue("");
                cbTilat.select(T("kaikkiTilat"));
                cbVuosi.select(cbVuosi.getNullSelectionItemId());
                kcKausi.setValue(null);
            }
        });
        CssHorizontalLayout cssPadding10PxHae = new CssHorizontalLayout();
        cssPadding10PxHae.addComponent(btnTyhjenna, CssHorizontalLayout.StyleEnum.PADDING_RIGHT_10PX);
        buttons.addComponent(cssPadding10PxHae);

        btnHae = UiBuilder.buttonSmallPrimary(null, T("hae"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                doSearch();
            }
        });

        buttons.addComponent(btnHae);
        searchSpecLayout.addComponent(buttons);
        searchSpecLayout.setWidth("100%");

        addComponent(searchSpecLayout);
        setExpandRatio(searchSpecLayout, 1f);
    }

    private String T(String key) {
        return i18nHelper.getMessage(key);
    }

    private String[] getKoulutuksenTilat() {
        String[] tilat = new String[TarjontaTila.values().length + 1];
        tilat[0] = T("kaikkiTilat");
        tilaMap = new HashMap<String, String>();
        int counter = 1;
        for (TarjontaTila tila : TarjontaTila.values()) {
            String localizedTila = T(tila.value());
            tilaMap.put(localizedTila, tila.value());
            tilat[counter] = localizedTila;
            counter++;
        }

        return tilat;
    }

    /**
     * Search has been triggered.
     */
    private void doSearch() {
        LOG.info("doSearch()");
        model.setKoulutuksenAlkamiskausi(kcKausi.getValue() != null ? (String) kcKausi.getValue() : null);
        model.setKoulutuksenAlkamisvuosi(cbVuosi.getValue() != null
                && !cbVuosi.getNullSelectionItemId().equals(cbVuosi.getValue())
                ? Integer.parseInt((String) cbVuosi.getValue()) : -1);

        String valittuTila = (String) cbTilat.getValue();
        if (valittuTila != null && !valittuTila.equalsIgnoreCase(T("koulutuksenTilat"))) {
            model.setKoulutuksenTila(tilaMap.get(valittuTila));
        }

        fireEvent(new SearchSpesificationView.SearchEvent(model));
    }

    /**
     * This event is sent when search is triggered.
     */
    public class SearchEvent extends Component.Event {

        private static final long serialVersionUID = 6351667953499686108L;
        private KoulutusSearchSpesificationViewModel _searchModel;

        public SearchEvent(KoulutusSearchSpesificationViewModel model) {
            super(SearchSpesificationView.this);
            _searchModel = model;
        }

        public KoulutusSearchSpesificationViewModel getModel() {
            return _searchModel;
        }
    }
}
