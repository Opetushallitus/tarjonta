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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;
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
    private static final String I18N_KAUDEN_TARKENNE = "kaudenTarkenne";
    private static final String I18N_VUOSI = "vuosi";
    private static final String I18N_KAUSI = "kausi";
    // private static final String I18N_HAKUTYYPPI = "hakutyyppi";
    // private static final String I18N_KOHDEJOUKKO = "kohdejoukko";
    private static final long serialVersionUID = 425330075453507227L;
    private transient I18NHelper i18nHelper = new I18NHelper(this);
    private TextField tfSearch;
    private Button btnHae;
    //private ComboBox cbKaudenTarkenne;
    private ComboBox cbVuosi;
    private KoodistoComponent kcKausi;
    // private KoodistoComponent kcHakutyyppi;
    // private KoodistoComponent kcKohdejoukko;
    private Button btnTyhjenna;
    private boolean attached = false;
    /* Model for search spesifications */
    private KoulutusSearchSpesificationViewModel model = new KoulutusSearchSpesificationViewModel();
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    public SearchSpesificationView() {
        super(true, UiMarginEnum.RIGHT_BOTTOM_LEFT);
    }

    public SearchSpesificationView(KoulutusSearchSpesificationViewModel model) {
        super(true, UiMarginEnum.RIGHT_BOTTOM_LEFT);
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
        tfSearch = new TextField("");
        tfSearch.addStyleName(Oph.TEXTFIELD_SEARCH);
        tfSearch.setNullRepresentation("");
        tfSearch.setPropertyDataSource(new NestedMethodProperty(model, "searchStr"));
        tfSearch.setWidth(300, UNITS_PIXELS);
        addComponent(tfSearch);

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

        btnHae = UiBuilder.buttonSmallSecodary(this, T("hae"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                doSearch();
            }
        });

        //TODO: no application logic, only for Christmas demo
//        cbKaudenTarkenne = UiUtil.comboBox(this, T(I18N_KAUDEN_TARKENNE), new String[]{"Koulutuksen alkamiskausi"});
//        cbKaudenTarkenne.setSizeUndefined();
//        cbKaudenTarkenne.setWidth("200px");

        //TODO: no application logic, only for Christmas demo
        cbVuosi = UiUtil.comboBox(null, T(I18N_VUOSI), new String[]{T(I18N_VUOSI + I18N_PROMPT), "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022"});
        cbVuosi.setNullSelectionAllowed(true);
        cbVuosi.setNullSelectionItemId(T(I18N_VUOSI + I18N_PROMPT));
        //cbVuosi.setInputPrompt(T(I18N_VUOSI + I18N_PROMPT));
        addComponent(cbVuosi);
        cbVuosi.setSizeUndefined();
        cbVuosi.setWidth("200px");

        //TODO: no application logic, only for Christmas demo
        kcKausi = uiBuilder.koodistoComboBox(this, KoodistoURI.KOODISTO_ALKAMISKAUSI_URI, null, null, T(I18N_KAUSI + I18N_PROMPT));
        kcKausi.setCaption(T(I18N_KAUSI));
        kcKausi.setSizeUndefined();
        kcKausi.setWidth("200px");
        kcKausi.getField().setNullSelectionAllowed(false);

//        //TODO: no application logic, only for Christmas demo
//        kcHakutyyppi = uiBuilder.koodistoComboBox(this, KoodistoURI.KOODISTO_HAKUTYYPPI_URI, null, null, T(I18N_HAKUTYYPPI + I18N_PROMPT));
//        kcHakutyyppi.setCaption(T(I18N_HAKUTYYPPI));
//        kcHakutyyppi.setReadOnly(true);
//        kcHakutyyppi.setSizeUndefined();
//
//        //TODO: no application logic, only for Christmas demo
//        kcKohdejoukko = uiBuilder.koodistoComboBox(this, KoodistoURI.KOODISTO_HAUN_KOHDEJOUKKO_URI, null, null, T(I18N_KOHDEJOUKKO + I18N_PROMPT));
//        kcKohdejoukko.setCaption(T(I18N_KOHDEJOUKKO));
//        kcKohdejoukko.setReadOnly(true);
//        kcKohdejoukko.setSizeUndefined();

        btnTyhjenna = UiBuilder.buttonSmallSecodary(this, T("tyhjenna"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                tfSearch.setValue("");
                cbVuosi.select(cbVuosi.getNullSelectionItemId());
                kcKausi.setValue(null);
            }
        });


        this.setComponentAlignment(tfSearch, Alignment.BOTTOM_LEFT);
        this.setComponentAlignment(btnHae, Alignment.BOTTOM_LEFT);
        // this.setComponentAlignment(cbKaudenTarkenne, Alignment.BOTTOM_RIGHT);
        this.setComponentAlignment(cbVuosi, Alignment.BOTTOM_RIGHT);
        this.setComponentAlignment(kcKausi, Alignment.BOTTOM_RIGHT);
        this.setComponentAlignment(btnTyhjenna, Alignment.BOTTOM_RIGHT);
        this.setExpandRatio(cbVuosi, 1f);
    }

    private String T(String key) {
        return i18nHelper.getMessage(key);
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
        fireEvent(new SearchEvent(model));
    }

    /**
     * This event is sent when search is triggered.
     */
    public class SearchEvent extends Event {

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
