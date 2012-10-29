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
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.ui.OphHorizontalLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;
import org.vaadin.addon.formbinder.ViewBoundForm;

/**
 * This is the search controller and spesification component used to search Haku and Tarjonta.
 *
 * @author mlyly
 */
@Configurable
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class SearchSpesificationView extends OphHorizontalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(SearchSpesificationView.class);
    private Button _btnTyhjenna;
    private Button _btnHae;

    //    TODO these were removed at the last spesification change... They'll be back.
    //
    //    @PropertyId("haunKohdejoukko")
    //    private KoodistoComponent _cbHaunKohdejoukko;
    //    @PropertyId("hakutyyppi")
    //    private KoodistoComponent _cbHakutyyppi;
    //    @PropertyId("hakutapa")
    //    private KoodistoComponent _cbHakutapa;

    
    private KoodistoComponent _cbKoulutuksenAlkamiskausi;
    
    private KoodistoComponent _cbHakukausi;
   
    private TextField _tfSearch;
    private I18NHelper _i18nHelper = new I18NHelper(this);

    /* Model for search spesifications */
    private KoulutusSearchSpesificationViewModel _model = new KoulutusSearchSpesificationViewModel();

    /* View bound form for search specs. This for is bound to presenter.getSearchSpec model. */
    private Form _form;
    
    public SearchSpesificationView() {
    	 super(true, UiMarginEnum.RIGHT_BOTTOM_LEFT);
    	 setSizeUndefined();
    	 buildLayout();
    }
    
    public SearchSpesificationView(KoulutusSearchSpesificationViewModel model) {
    	super(true, UiMarginEnum.RIGHT_BOTTOM_LEFT);
    	setSizeUndefined();
    	_model = model;
    	buildLayout();
    }

    protected void buildLayout() {
        //
        // Create fields
        //
    	_tfSearch = new TextField("");
        _tfSearch.addStyleName(Oph.TEXTFIELD_SEARCH);
        _tfSearch.setNullRepresentation("");
        _tfSearch.setPropertyDataSource(new NestedMethodProperty(_model, "searchStr"));
        addComponent(_tfSearch);
        
        //
        // Hook enter to do the search
        //
        _tfSearch.setImmediate(true);
        _tfSearch.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                doSearch();
            }
        });
        
        _btnHae = UiBuilder.buttonSmallPrimary(this, T("hae"));
        _btnHae.addStyleName(Oph.BUTTON_SMALL);
        _btnHae.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                doSearch();
            }
        });
        
    	
        _cbHakukausi = UiBuilder.koodistoComboBox(this, KoodistoURIHelper.KOODISTO_HAKUKAUSI_URI, null, null, T("hakukausi.prompt"));

        _cbKoulutuksenAlkamiskausi = UiBuilder.koodistoComboBox(this, KoodistoURIHelper.KOODISTO_KOULUTUKSEN_ALKAMISKAUSI_URI, null, null, T("koulutuksenalkamiskausi.prompt"));
        
        _btnTyhjenna = UiBuilder.buttonSmallPrimary(this, T("tyhjenna"));
        
        _btnTyhjenna.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                _tfSearch.setValue("");
            }
        });

        
        this.setComponentAlignment(_btnHae, Alignment.BOTTOM_LEFT);
        this.setComponentAlignment( _cbHakukausi, Alignment.BOTTOM_RIGHT);
        this.setComponentAlignment( _cbKoulutuksenAlkamiskausi, Alignment.BOTTOM_RIGHT);

        this.setComponentAlignment(_btnTyhjenna, Alignment.BOTTOM_RIGHT);
        this.setExpandRatio(_cbHakukausi, 1f); 

        
    }

    private String T(String key) {
		return _i18nHelper.getMessage(key);
	}

	/**
     * Search has been triggered.
     */
    private void doSearch() {
        LOG.info("doSearch()");
        fireEvent(new SearchEvent(_model));
    }

    /**
     * This event is sent when search is triggered.
     */
    public class SearchEvent extends Event {

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
