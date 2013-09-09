package fi.vm.sade.tarjonta.ui.view.raportointi;/*
 *
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


import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;

import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.RaportointiRestClientHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.service.UserContext;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Tuomas Katva
 * Date: 4.9.2013
 */
@Configurable(preConstruction = true)
public class AloituspaikatRaporttiDialog extends CustomComponent {

    private final transient I18NHelper i18nHelper = new I18NHelper(this);
    private static final Logger LOG = LoggerFactory.getLogger(AloituspaikatRaporttiDialog.class);
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    @Autowired(required = true)
    private RaportointiRestClientHelper raportointiRestHelper;
    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;
    @Autowired
    private UserContext userContext;

    private VerticalLayout rootLayout;
    private GridLayout searchSpecLayout;

    private ComboBox cbVuosi;
    private KoodistoComponent kcKausi;
    private ComboBox cbKoulutusToimijat;
    private ComboBox cbOppilaitos;
    private ComboBox cbToimipiste;
    private KoodistoComponent kcPohjakoulutus;
    private OptionGroup tulostusOption;
    private Button cancelBtn;
    private Button printBtn;

    private DialogCloseListener closeListener;

    protected ErrorMessage errorView;


    private static final String I18N_KAUSI = "kausi";
    private static final String I18N_POHJAKOULU = "pohjakouluprompt";
    private static final String CAPTION_PROPERTY_ID = "localizedName";



    public AloituspaikatRaporttiDialog(DialogCloseListener listener) {
        this.closeListener = listener;
        buildLayout();
    }

    public interface DialogCloseListener {

        void windowCloseEvent(String result);

    }

    protected void buildLayout() {

        rootLayout = new VerticalLayout();

        errorView = new ErrorMessage();

        rootLayout.addComponent(errorView);

        searchSpecLayout = createSearchSpecLayout();

        rootLayout.addComponent(searchSpecLayout);

        rootLayout.addComponent(buildButtonLayout());

        setCompositionRoot(rootLayout);



    }

    private GridLayout createSearchSpecLayout() {

        GridLayout grid = new GridLayout(4,1);

        VerticalLayout headerLayout = new VerticalLayout();
        Label hakutekijatLbl = new Label("<b>"+T("hakutekijat")+"</b>");
        hakutekijatLbl.setContentMode(Label.CONTENT_XHTML);
        headerLayout.addComponent(hakutekijatLbl);
        headerLayout.setMargin(true,true,false,true);


        grid.addComponent(headerLayout,0,0);
        grid.newLine();
        grid.newLine();

        Label vuosiKausiLbl = UiBuilder.label(null,T("vuosiKausi"));
        grid.addComponent(new Label());
        grid.addComponent(vuosiKausiLbl);

        HorizontalLayout vuosiKausiLayout = new HorizontalLayout();
        vuosiKausiLayout.setMargin(true,false,false,false);
        cbVuosi = UiUtil.comboBox(null, null, new String[]{"2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025"});
        cbVuosi.setWidth("100px");
        VerticalLayout vuosiLayout = new VerticalLayout();
        vuosiLayout.addComponent(cbVuosi);
        vuosiLayout.setMargin(false, true, false, true);
        kcKausi = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_ALKAMISKAUSI_URI, null, null, T(I18N_KAUSI));
        kcKausi.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object o) {
              if (o instanceof KoodiType) {
                  String kausiLyhytNimi = null;
                 for (KoodiMetadataType meta :((KoodiType) o).getMetadata()) {
                    if (meta.getKieli().equals(KieliType.FI)) {
                       kausiLyhytNimi = meta.getLyhytNimi();
                    }
                 }
                  return kausiLyhytNimi;
              } else {
                  return o;
              }
            }
        });
        kcKausi.getField().setWidth("100px");
        vuosiKausiLayout.addComponent(vuosiLayout);
        vuosiKausiLayout.addComponent(kcKausi);
        grid.addComponent(vuosiKausiLayout);
        grid.newLine();

        Label koulutuksenJarjestaja = UiBuilder.label(null,T("koulutuksenJarjestaja"));
        grid.addComponent(new Label());
        grid.addComponent(koulutuksenJarjestaja);
        HorizontalLayout cbKoulutusToimijatLayout = new HorizontalLayout();
        cbKoulutusToimijat = buildKoulutustoimijaCombo();
        cbKoulutusToimijatLayout.addComponent(cbKoulutusToimijat);
        cbKoulutusToimijatLayout.setMargin(true,false,false,true);
        grid.addComponent(cbKoulutusToimijatLayout);
        grid.newLine();

        Label oppilaitosLbl = UiBuilder.label(null,T("oppilaitos"));
        grid.addComponent(new Label());
        grid.addComponent(oppilaitosLbl);
        HorizontalLayout oppilaitosComboLayout = new HorizontalLayout();
        cbOppilaitos = buildOppilaitosCombo(null);
        cbOppilaitos.setEnabled(false);
        oppilaitosComboLayout.addComponent(cbOppilaitos);
        oppilaitosComboLayout.setMargin(true,false,false,true);
        grid.addComponent(oppilaitosComboLayout);
        grid.newLine();

        Label toimipisteLbl = UiBuilder.label(null,T("toimipiste"));
        grid.addComponent(new Label());
        grid.addComponent(toimipisteLbl);
        HorizontalLayout toimipisteCbLayout = new HorizontalLayout();
        cbToimipiste = buildToimipisteCombo();
        cbToimipiste.setEnabled(false);
        toimipisteCbLayout.addComponent(cbToimipiste);
        toimipisteCbLayout.setMargin(true,false,false,true);
        grid.addComponent(toimipisteCbLayout);
        grid.newLine();

        Label pohjakoulutusLabel = UiBuilder.label(null,T("pohjakoulutus"));
        grid.addComponent(new Label());
        grid.addComponent(pohjakoulutusLabel);
        HorizontalLayout pohjakoulutusLayout = new HorizontalLayout();
        kcPohjakoulutus = uiBuilder.koodistoComboBox(null,KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI,null,null,T(I18N_POHJAKOULU));
        kcPohjakoulutus.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object o) {
                if (o instanceof KoodiType) {
                      String pkLyhytNimi = null;
                    for (KoodiMetadataType meta :((KoodiType) o).getMetadata()) {
                        if (meta.getKieli().equals(KieliType.FI)) {
                            pkLyhytNimi = meta.getLyhytNimi();
                        }
                    }
                    return pkLyhytNimi;
                } else{
                    return o;
                }
            }
        });
        pohjakoulutusLayout.addComponent(kcPohjakoulutus);
        pohjakoulutusLayout.setMargin(true,false,false,true);
        grid.addComponent(pohjakoulutusLayout);
        grid.newLine();

        VerticalLayout tulostusMuotoHdrLayout = new VerticalLayout();
        tulostusMuotoHdrLayout.setMargin(true,true,false,true);
        Label tulostusMuotoLbl = new Label("<b>"+T("tulostusmuoto")+"</b>");
        tulostusMuotoLbl.setContentMode(Label.CONTENT_XHTML);
        tulostusMuotoHdrLayout.addComponent(tulostusMuotoLbl);
        grid.addComponent(tulostusMuotoHdrLayout);
        grid.addComponent(new Label());
        HorizontalLayout tulostusmuotoLayout = new HorizontalLayout();
        tulostusOption = buildTulostusOptionGroup();
        tulostusmuotoLayout.addComponent(tulostusOption);
        tulostusmuotoLayout.setSpacing(true);
        tulostusmuotoLayout.setMargin(true,false,false,true);
        grid.addComponent(tulostusmuotoLayout);

        grid.setColumnExpandRatio(0,0.5f);
        grid.setColumnExpandRatio(1,0.5f);
        grid.setColumnExpandRatio(2,0.8f);
        grid.setColumnExpandRatio(3,0.5f);
        return grid;
    }

    private String T(String key) {
        return i18nHelper.getMessage(key);
    }

    private ComboBox buildKoulutustoimijaCombo() {
        ComboBox koulutustoimijaCombo = new ComboBox();

        OrganisaatioSearchCriteria criteria = new OrganisaatioSearchCriteria();

        criteria.setOrganisaatioTyyppi(OrganisaatioTyyppi.KOULUTUSTOIMIJA.value());

        koulutustoimijaCombo.setContainerDataSource(createOrganisaatioContainer(organisaatioSearchService.searchBasicOrganisaatiosExact(criteria)));
        koulutustoimijaCombo.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);
        koulutustoimijaCombo.setImmediate(true);
        koulutustoimijaCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                OrganisaatioPerustietoWrapper selectedOrg = (OrganisaatioPerustietoWrapper)valueChangeEvent.getProperty().getValue();

               createOppilaitosComboDatasource(selectedOrg.getOrganisaatioPerustieto().getOid());
            }
        });
        koulutustoimijaCombo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        return koulutustoimijaCombo;
    }

    private OptionGroup buildTulostusOptionGroup() {
        String[] tulostukset = new String[]{RaportointiRestClientHelper.EXCEL_TYPE,RaportointiRestClientHelper.PDF_TYPE};
        List<String> tulostuksetList = Arrays.asList(tulostukset);
        OptionGroup tulostusOption = new OptionGroup("",tulostuksetList);
        tulostusOption.setNullSelectionAllowed(false);
        tulostusOption.select(RaportointiRestClientHelper.PDF_TYPE);
        tulostusOption.addStyleName("horizontal");
        return tulostusOption;

    }


    private void createOppilaitosComboDatasource(String koulutustoimijaOid) {
         if (cbOppilaitos != null) {

             cbOppilaitos.setContainerDataSource(createOrganisaatioContainer(organisaatioSearchService.searchBasicOrganisaatiosExact(createOrganisaatioSearchCriteria(koulutustoimijaOid, OrganisaatioTyyppi.OPPILAITOS))));
             cbOppilaitos.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);
             cbOppilaitos.setEnabled(true);
         }
    }

    private OrganisaatioSearchCriteria createOrganisaatioSearchCriteria(String oid,OrganisaatioTyyppi tyyppi) {
        OrganisaatioSearchCriteria criteria = new OrganisaatioSearchCriteria();

        criteria.setOrganisaatioTyyppi(tyyppi.value());
        criteria.getOidResctrictionList().add(oid);

        return criteria;
    }

    private void createToimipisteComboDatasource(String oppilaitosOid) {
        if (cbToimipiste != null) {

            cbToimipiste.setContainerDataSource(createOrganisaatioContainer(organisaatioSearchService.searchBasicOrganisaatiosExact(createOrganisaatioSearchCriteria(oppilaitosOid, OrganisaatioTyyppi.OPETUSPISTE))));
            cbToimipiste.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);
            cbToimipiste.setEnabled(true);

        }
    }

    private HorizontalLayout buildButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();

        cancelBtn = UiUtil.button(null,T("suljeBtn"));
        cancelBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                closeListener.windowCloseEvent(null);
            }
        });
        buttonLayout.addComponent(cancelBtn);

        printBtn = UiUtil.button(null,T("tulostaBtn"));
        printBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                if (!validateFields())  {
                    return;
                }

                String url = createUrl();




                closeListener.windowCloseEvent(url);
            }
        });

        buttonLayout.addComponent(printBtn);
        buttonLayout.setSizeFull();
        buttonLayout.setComponentAlignment(cancelBtn,Alignment.MIDDLE_LEFT);
        buttonLayout.setComponentAlignment(printBtn,Alignment.MIDDLE_RIGHT);
        buttonLayout.setMargin(true,true,true,true);
        return buttonLayout;
    }

    private boolean validateFields() {
        boolean fieldsValid = true;

        if (cbKoulutusToimijat.getValue() == null || ((OrganisaatioPerustietoWrapper)cbKoulutusToimijat.getValue()).getLocalizedName() == null ) {
            errorView.addError(T("validation.koulutustoimija.missing"));
            fieldsValid = false;
        }

        if (cbVuosi.getValue() == null || ((String)cbVuosi.getValue()).trim().length() < 1 ) {
            errorView.addError(T("validation.vuosi.missing"));
            fieldsValid = false;
        }



        return fieldsValid;
    }

    private String createUrl() {
        String url = null;

        String koulutusToimija = ((OrganisaatioPerustietoWrapper)cbKoulutusToimijat.getValue()).getLocalizedName();
        String oppilaitos = cbOppilaitos.getValue() != null ? ((OrganisaatioPerustietoWrapper)cbOppilaitos.getValue()).getLocalizedName() : null;
        String toimipiste = cbToimipiste.getValue() != null ? ((OrganisaatioPerustietoWrapper)cbToimipiste.getValue()).getLocalizedName() : null;
        String vuosi = (String)cbVuosi.getValue();
        String kausiKoodi = (String)kcKausi.getValue();
        String pohjakoulutusKoodi = (String)kcPohjakoulutus.getValue();
        String tulostusTyyppi = (String)tulostusOption.getValue();

        url = raportointiRestHelper.createAloitusPaikatRaporttiUrl(koulutusToimija,oppilaitos,toimipiste,kausiKoodi,vuosi,pohjakoulutusKoodi,tulostusTyyppi, I18N.getLocale().getLanguage().toUpperCase());

        return url;
    }

    private ComboBox buildToimipisteCombo() {
        ComboBox toimipisteCombo = new ComboBox();

        toimipisteCombo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        return toimipisteCombo;
    }

    private ComboBox buildOppilaitosCombo(String koulutustoimijaOid) {

       ComboBox oppilaitosCombo = new ComboBox();
       oppilaitosCombo.setImmediate(true);
        if (koulutustoimijaOid != null) {



            oppilaitosCombo.setContainerDataSource(createOrganisaatioContainer(organisaatioSearchService.searchBasicOrganisaatiosExact(createOrganisaatioSearchCriteria(koulutustoimijaOid, OrganisaatioTyyppi.OPPILAITOS))));
            oppilaitosCombo.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);

        }

        oppilaitosCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                OrganisaatioPerustietoWrapper selectedOrg = (OrganisaatioPerustietoWrapper)valueChangeEvent.getProperty().getValue();
                createToimipisteComboDatasource(selectedOrg.getOrganisaatioPerustieto().getOid());
                cbToimipiste.setEnabled(true);
            }
        });
        oppilaitosCombo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        return oppilaitosCombo;
    }

    private BeanItemContainer<OrganisaatioPerustietoWrapper> createOrganisaatioContainer(List<OrganisaatioPerustieto> organisaatiot) {
        List<OrganisaatioPerustietoWrapper> organisaatioPerustietoWrapperList = new ArrayList<OrganisaatioPerustietoWrapper>();
        for (OrganisaatioPerustieto org: organisaatiot) {
            OrganisaatioPerustietoWrapper orgWrapper = new OrganisaatioPerustietoWrapper(org);
            organisaatioPerustietoWrapperList.add(orgWrapper);
        }

        BeanItemContainer<OrganisaatioPerustietoWrapper> organisaatioDatasource = new BeanItemContainer<OrganisaatioPerustietoWrapper>(OrganisaatioPerustietoWrapper.class,organisaatioPerustietoWrapperList);
        return organisaatioDatasource;
    }
}
