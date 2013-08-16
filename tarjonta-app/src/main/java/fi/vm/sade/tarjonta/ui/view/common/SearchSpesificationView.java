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
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
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
    private static final CssHorizontalLayout.StyleEnum[] COMPONENT_STYLE = {
        //a style for CssLayout components
        CssHorizontalLayout.StyleEnum.PADDING_RIGHT_5PX
    };

    public SearchSpesificationView() {
        super(true, UiMarginEnum.BOTTOM);
    }

    public SearchSpesificationView(KoulutusSearchSpesificationViewModel model) {
        super(true, UiMarginEnum.BOTTOM);
        this.model = model;
    }

    @Override
    public void attach() {
        if (btnHae != null) {
            btnHae.removeClickShortcut();
        }
        super.attach();
        if (attached) {
            return;
        }

        attached = true;
        buildLayout();
    }

    protected void buildLayout() {
        setSizeFull();

        CssHorizontalLayout searchSpecLayout = new CssHorizontalLayout();
        searchSpecLayout.addStyleName(CssHorizontalLayout.StyleEnum.TEXT_ALIGN_RIGHT.getStyleName());

        //without the height parameter result area would be hidden.
        //
        // Create fields
        // Hook enter to do the search
        CssLayout texFieldLayout = new CssLayout();
        tfSearch = new TextField();
        tfSearch.addStyleName(Oph.TEXTFIELD_SEARCH);
        tfSearch.setNullRepresentation("");
        tfSearch.setPropertyDataSource(new NestedMethodProperty(model, "searchStr"));
        tfSearch.setWidth(230, UNITS_PIXELS);
        tfSearch.setImmediate(false);

        texFieldLayout.addComponent(new Label("&nbsp;", Label.CONTENT_XHTML));
        texFieldLayout.addComponent(tfSearch);
        searchSpecLayout.addComponent(texFieldLayout, CssHorizontalLayout.StyleEnum.FLOAT_LEFT);
        VerticalLayout tilatLayout = new VerticalLayout();
        tilatLayout.setSizeUndefined();
        Label tilatLabel = UiUtil.label(null, T("koulutuksenTilat"));
        tilatLayout.addComponent(tilatLabel);
        cbTilat = UiUtil.comboBox(null, null, getKoulutuksenTilat());
        cbTilat.setWidth("80px");
        tilatLayout.addComponent(cbTilat);
        searchSpecLayout.addComponent(tilatLayout, COMPONENT_STYLE);

        VerticalLayout vuosiLayout = new VerticalLayout();
        vuosiLayout.setSizeUndefined();
        Label vuosiLabel = UiUtil.label(null, T(I18N_VUOSI));
        vuosiLayout.addComponent(vuosiLabel);
        cbVuosi = UiUtil.comboBox(null, null, new String[]{T(I18N_VUOSI + I18N_PROMPT), "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025"});
        cbVuosi.setNullSelectionAllowed(true);
        cbVuosi.setNullSelectionItemId(T(I18N_VUOSI + I18N_PROMPT));
        cbVuosi.setSizeUndefined();
        cbVuosi.setWidth("140px");
        vuosiLayout.addComponent(cbVuosi);

        searchSpecLayout.addComponent(vuosiLayout, COMPONENT_STYLE);

        VerticalLayout kausiLayout = new VerticalLayout();
        kausiLayout.setSizeUndefined();
        Label kausiLabel = UiUtil.label(null, T(I18N_KAUSI));
        kausiLayout.addComponent(kausiLabel);
        kcKausi = uiBuilder.koodistoComboBox(this, KoodistoURI.KOODISTO_ALKAMISKAUSI_URI, null, null, T(I18N_KAUSI + I18N_PROMPT));
        kcKausi.setSizeUndefined();
        kcKausi.getField().setNullSelectionAllowed(false);
        kcKausi.getField().setWidth("140px");
        kausiLayout.addComponent(kcKausi);

        searchSpecLayout.addComponent(kausiLayout, COMPONENT_STYLE);

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
        CssHorizontalLayout cssPadding5PxHae = new CssHorizontalLayout();
        cssPadding5PxHae.addComponent(btnTyhjenna, COMPONENT_STYLE);
        buttons.addComponent(cssPadding5PxHae);

        btnHae = UiBuilder.buttonSmallPrimary(null, T("hae"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                doSearch();
            }
        });

        tfSearch.addListener(new FocusListener() {
            private static final long serialVersionUID = -5924587297708382318L;

            @Override
            public void focus(FocusEvent event) {
                btnHae.setClickShortcut(KeyCode.ENTER);
            }
        });
        tfSearch.addListener(new BlurListener() {
            private static final long serialVersionUID = 7429378966840902444L;

            @Override
            public void blur(BlurEvent event) {
                //Remove textfield enter-click listener, if any.
                btnHae.removeClickShortcut();
            }
        });

        buttons.addComponent(btnHae);
        searchSpecLayout.addComponent(buttons);

        searchSpecLayout.setWidth("100%");
        addComponent(searchSpecLayout);
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
