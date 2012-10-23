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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;
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
public class SearchSpesificationView extends AbstractHorizontalLayout {

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

    @PropertyId("koulutuksenAlkamiskausi")
    private KoodistoComponent _cbKoulutuksenAlkamiskausi;
    @PropertyId("hakukausi")
    private KoodistoComponent _cbHakukausi;
    @PropertyId("searchSpec")
    private TextField _tfSearch;
    private I18NHelper _i18nHelper = new I18NHelper(this);

    /* Model for search spesifications */
    private KoulutusSearchSpesificationViewModel _model = new KoulutusSearchSpesificationViewModel();

    /* View bound form for search specs. This for is bound to presenter.getSearchSpec model. */
    private Form _form;

    @Override
    protected void buildLayout() {
        //
        // Create fields
        //
        _cbHakukausi = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_HAKUKAUSI_URI, null, null, T("hakukausi.prompt"));
//        _cbHakutapa = UiBuilder.koodistoComboBox(null,KoodistoURIHelper.KOODISTO_HAKUTAPA_URI, null, null, T("hakutapa.prompt"));
//        _cbHakutyyppi = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_HAKUTYYPPI_URI, null, null, T("hakutyyppi.prompt"));
//        _cbHaunKohdejoukko = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_HAUN_KOHDEJOUKKO_URI, null, null, T("haunkohdejoukko.prompt"));
        _cbKoulutuksenAlkamiskausi = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_KOULUTUKSEN_ALKAMISKAUSI_URI, null, null, T("koulutuksenalkamiskausi.prompt"));
        _tfSearch = UiBuilder.textField(null, "", T("hakuehto.prompt"), false);
        _btnHae = UiBuilder.buttonSmallPrimary(null, T("hae"));
        _btnTyhjenna = UiBuilder.buttonSmallPrimary(null, T("tyhjenna"));

        _btnHae.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                doSearch();
            }
        });

        addComponent(_tfSearch);
        addComponent(_btnHae);

        addComponent(_cbHakukausi);
//        addComponent(_cbHakutapa);
//        addComponent(_cbHakutyyppi);
//        addComponent(_cbHaunKohdejoukko);
        addComponent(_cbKoulutuksenAlkamiskausi);

        addComponent(_btnTyhjenna);

        // Bind fields above to search spesifications
        BeanItem<KoulutusSearchSpesificationViewModel> beanItem = new BeanItem<KoulutusSearchSpesificationViewModel>(_model);
        _form = new ViewBoundForm();
        _form.setItemDataSource(beanItem);

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
    }

    /**
     * Search has been triggered.
     */
    private void doSearch() {
        LOG.info("doSearch()");
        _form.commit();
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
