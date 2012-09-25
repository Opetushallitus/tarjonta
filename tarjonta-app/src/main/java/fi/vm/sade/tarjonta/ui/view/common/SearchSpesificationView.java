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

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;
import fi.vm.sade.tarjonta.ui.view.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;
import org.vaadin.addon.formbinder.ViewBoundForm;

/**
 *
 * @author mlyly
 */
@Configurable
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class SearchSpesificationView extends HorizontalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(SearchSpesificationView.class);
    private Button _btnTyhjenna;
    private Button _btnHae;
    @PropertyId("hanKohdejoukko")
    private KoodistoComponent _cbHaunKohdejoukko;
    @PropertyId("hakutyyppi")
    private KoodistoComponent _cbHakutyyppi;
    @PropertyId("hakutapa")
    private KoodistoComponent _cbHakutapa;
    @PropertyId("koulutuksenAlkamiskausi")
    private KoodistoComponent _cbKoulutuksenAlkamiskausi;
    @PropertyId("hakukausi")
    private KoodistoComponent _cbHakukausi;
    @PropertyId("searchSpec")
    private TextField _tfSearch;
    @Autowired
    private HakuPresenter _presenter;
    @Value("${koodisto-uris.hakukausi:http://hakukausi}")
    private String _koodistoUriHakukausi;
    @Value("${koodisto-uris.hakutapa:http://hakutapa}")
    private String _koodistoUriHakutapa;
    @Value("${koodisto-uris.hakutyyppi:http://hakutyyppi}")
    private String _koodistoUriHakutyyppi;
    @Value("${koodisto-uris.haunKohdejoukko:http://kohdejoukko}")
    private String _koodistoUriHaunKohdejoukko;
    @Value("${koodisto-uris.koulutuksenAlkamiskausi:http://alkamiskausi")
    private String _koodistoUriKoulutuksenAlkamiskausi;
    private I18NHelper _i18nHelper = new I18NHelper(this);
    
    private Form form;

    public SearchSpesificationView() {
        super();
    }

    @Override
    public void attach() {
        super.attach();

        _cbHakukausi = UiBuilder.koodistoComboBox(null,_koodistoUriHakukausi, null, null, T("hakukausi.prompt"));
        _cbHakutapa = UiBuilder.koodistoComboBox(null,_koodistoUriHakutapa, null, null, T("hakutapa.prompt"));
        _cbHakutyyppi = UiBuilder.koodistoComboBox(null,_koodistoUriHakutyyppi, null, null, T("hakutyyppi.prompt"));
        _cbHaunKohdejoukko = UiBuilder.koodistoComboBox(null,_koodistoUriHaunKohdejoukko, null, null, T("haunkohdejoukko.prompt"));
        _cbKoulutuksenAlkamiskausi = UiBuilder.koodistoComboBox(null,_koodistoUriKoulutuksenAlkamiskausi, null, null, T("koulutuksenalkamiskausi.prompt"));
        _tfSearch = UiBuilder.textField(null, "", T("hakuehto.prompt"), false);
        _btnHae = UiBuilder.buttonSmallPrimary(null, T("hae"));
        _btnTyhjenna = UiBuilder.buttonSmallPrimary(null, T("tyhjenna"));

        _btnHae.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                form.commit();
                _presenter.doSearch();
            }
        });

        addComponent(_tfSearch);
        addComponent(_btnHae);

        addComponent(_cbHakukausi);
        addComponent(_cbHakutapa);
        addComponent(_cbHakutyyppi);
        addComponent(_cbHaunKohdejoukko);
        addComponent(_cbKoulutuksenAlkamiskausi);

        addComponent(_btnTyhjenna);
        
        BeanItem<KoulutusSearchSpesificationViewModel> beanItem = new BeanItem<KoulutusSearchSpesificationViewModel>(_presenter.getSearchSpec());
        form = new ViewBoundForm();
        form.setItemDataSource(beanItem);
    }

    private String T(String key) {
        return _i18nHelper.getMessage(key);
    }
}
