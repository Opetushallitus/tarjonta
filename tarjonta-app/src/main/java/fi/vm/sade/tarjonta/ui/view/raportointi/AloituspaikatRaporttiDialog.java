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
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.organisaatio.service.search.SearchCriteria;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.RaportointiRestClientHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

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
    private TarjontaUIHelper tarjontaUIHelper;
    @Autowired
    private TarjontaPresenter presenter;

    @Value("${root.organisaatio.oid}") String rootOrgOid;


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

    private String selectedVuosi = null;
    private String selectedKausi = null;


    private static final String I18N_KAUSI = "kausi";
    private static final String I18N_POHJAKOULU = "pohjakouluprompt";
    private static final String CAPTION_PROPERTY_ID = "localizedName";



    public AloituspaikatRaporttiDialog(DialogCloseListener listener) {
        this.closeListener = listener;
        buildLayout();
    }

    public AloituspaikatRaporttiDialog(DialogCloseListener listener, String vuosi, String kausi, OrganisaatioPerustieto perustieto) {
        this.closeListener = listener;
        this.selectedVuosi = vuosi;
        this.selectedKausi = kausi;
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

    @Override
    public void attach() {
        super.attach();    //To change body of overridden methods use File | Settings | File Templates.

        String selectedOrgOid = this.presenter.getNavigationOrganisation() != null ? this.presenter.getNavigationOrganisation().getOrganisationOid() : null;

        if (selectedOrgOid != null) {
            Set<String> searchOrgOids = new HashSet<String>();
            searchOrgOids.add(selectedOrgOid);
            List<OrganisaatioPerustieto> orgs = organisaatioSearchService.findByOidSet(searchOrgOids);
            if (orgs != null && orgs.size()>0) {
                OrganisaatioPerustieto org = orgs.get(0);
                setSelectedOrganisaatio(org);
            }

        }

        if (selectedKausi != null) {
          List<KoodiType> koodis = tarjontaUIHelper.getKoodis(selectedKausi);
            if (koodis != null && koodis.get(0) != null) {
                kcKausi.setValue(getKausiLyhytNimi(koodis.get(0).getMetadata()));
            }
        }
    }

    private OrganisaatioPerustieto findOrganisaatioByOid(String oid) {
        OrganisaatioPerustieto foundOrg = null;

        HashSet<String> oids = new HashSet<String>();
        oids.add(oid);
        List<OrganisaatioPerustieto> orgs = organisaatioSearchService.findByOidSet(oids);
        foundOrg = orgs.get(0);

        return foundOrg;
    }

    private void setSelectedOrganisaatio(OrganisaatioPerustieto organisaatioPerustieto) {
          if (organisaatioPerustieto.getOrganisaatiotyypit().contains(OrganisaatioTyyppi.KOULUTUSTOIMIJA)) {

              OrganisaatioPerustietoWrapper org = new OrganisaatioPerustietoWrapper(organisaatioPerustieto);
              cbKoulutusToimijat.select(org);

          } else if (organisaatioPerustieto.getOrganisaatiotyypit().contains(OrganisaatioTyyppi.OPPILAITOS)) {
             String parentOrg = getKoulutusParent(organisaatioPerustieto, 1);
             OrganisaatioPerustietoWrapper selectedOrg = new OrganisaatioPerustietoWrapper(organisaatioPerustieto);

              OrganisaatioPerustietoWrapper parent = new OrganisaatioPerustietoWrapper(findOrganisaatioByOid(parentOrg));

              cbKoulutusToimijat.select(parent);


              cbOppilaitos.select(selectedOrg);
          }  else if (organisaatioPerustieto.getOrganisaatiotyypit().contains(OrganisaatioTyyppi.TOIMIPISTE)) {
              String oppilaitos = getKoulutusParent(organisaatioPerustieto, 2);
              String koulutustoimija = getKoulutusParent(organisaatioPerustieto, 1);

              cbKoulutusToimijat.select(new OrganisaatioPerustietoWrapper(findOrganisaatioByOid(koulutustoimija)));
              cbOppilaitos.select(new OrganisaatioPerustietoWrapper(findOrganisaatioByOid(oppilaitos)));
              cbToimipiste.select(new OrganisaatioPerustietoWrapper(organisaatioPerustieto));

          }
    }



    private String getKoulutusParent(OrganisaatioPerustieto perustieto, int nthParent) {
        String parentOid = null;
        StringTokenizer st = new StringTokenizer(perustieto.getParentOidPath(),"/");
        int tokens = st.countTokens();
        int counter = 0;
        int nthToken = tokens - nthParent;
        while (st.hasMoreTokens())  {
            counter ++;
            String temp = st.nextToken();
            if (counter == nthToken) {
                parentOid = temp;
            }

        }




        return parentOid;
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
        if (selectedVuosi != null) {
            cbVuosi.select(selectedVuosi);
        } else {

            cbVuosi.select(new Integer(Calendar.getInstance().get(Calendar.YEAR)).toString());
        }
        VerticalLayout vuosiLayout = new VerticalLayout();
        vuosiLayout.addComponent(cbVuosi);
        vuosiLayout.setMargin(false, true, false, true);

        kcKausi = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_ALKAMISKAUSI_URI, null, null, T(I18N_KAUSI));
        kcKausi.setImmediate(true);


        kcKausi.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object o) {
              if (o instanceof KoodiType) {

                  return getKausiLyhytNimi(((KoodiType)o).getMetadata());
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

    private String getKausiLyhytNimi(List<KoodiMetadataType> metas) {
        String kausiLyhytNimi = null;
        for (KoodiMetadataType meta :metas) {
            if (meta.getKieli().equals(KieliType.FI)) {
                kausiLyhytNimi = meta.getLyhytNimi();
            }
        }
       return kausiLyhytNimi;
    }

    private String T(String key) {
        return i18nHelper.getMessage(key);
    }

    private ComboBox buildKoulutustoimijaCombo() {
        ComboBox koulutustoimijaCombo = new ComboBox();

        SearchCriteria criteria = new SearchCriteria();
        criteria.setAktiiviset(true);
        criteria.setOrganisaatioTyyppi(OrganisaatioTyyppi.KOULUTUSTOIMIJA.value());

        koulutustoimijaCombo.setContainerDataSource(createOrganisaatioContainer(organisaatioSearchService.searchHierarchy(criteria)));
        koulutustoimijaCombo.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);
        koulutustoimijaCombo.setImmediate(true);
        koulutustoimijaCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                OrganisaatioPerustietoWrapper selectedOrg = (OrganisaatioPerustietoWrapper)valueChangeEvent.getProperty().getValue();

               createOppilaitosComboDatasource(selectedOrg.getOrganisaatioPerustieto() != null ? selectedOrg.getOrganisaatioPerustieto().getOid() : selectedOrg.getOrganisaatioOid());
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

             cbOppilaitos.setContainerDataSource(createOrganisaatioContainer(organisaatioSearchService.searchHierarchy(createOrganisaatioSearchCriteria(koulutustoimijaOid, OrganisaatioTyyppi.OPPILAITOS))));
             cbOppilaitos.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);
             cbOppilaitos.setEnabled(true);
         }
    }

    private SearchCriteria createOrganisaatioSearchCriteria(String oid,OrganisaatioTyyppi tyyppi) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setAktiiviset(true);

        criteria.setOrganisaatioTyyppi(tyyppi.value());
        criteria.getOidRestrictionList().add(oid);

        return criteria;
    }

    private void createToimipisteComboDatasource(String oppilaitosOid) {
        if (cbToimipiste != null) {

            cbToimipiste.setContainerDataSource(createOrganisaatioContainer(organisaatioSearchService.searchExact(createOrganisaatioSearchCriteria(oppilaitosOid, OrganisaatioTyyppi.TOIMIPISTE))));
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



            oppilaitosCombo.setContainerDataSource(createOrganisaatioContainer(organisaatioSearchService.searchExact(createOrganisaatioSearchCriteria(koulutustoimijaOid, OrganisaatioTyyppi.OPPILAITOS))));
            oppilaitosCombo.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);

        }

        oppilaitosCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                OrganisaatioPerustietoWrapper selectedOrg = (OrganisaatioPerustietoWrapper)valueChangeEvent.getProperty().getValue();
                if (selectedOrg == null) {
                    selectedOrg = (OrganisaatioPerustietoWrapper)cbKoulutusToimijat.getValue();
                }
                createToimipisteComboDatasource(selectedOrg.getOrganisaatioPerustieto() != null ? selectedOrg.getOrganisaatioPerustieto().getOid() : selectedOrg.getOrganisaatioOid());
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
