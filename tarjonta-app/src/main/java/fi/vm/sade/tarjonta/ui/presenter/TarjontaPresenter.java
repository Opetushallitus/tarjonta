/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.ui.presenter;

import java.text.SimpleDateFormat;
import java.util.*;

import fi.vm.sade.authentication.service.types.dto.HenkiloFatType;
import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vaadin.ui.Window;

import fi.vm.sade.authentication.service.UserService;
import fi.vm.sade.authentication.service.types.HenkiloPagingObjectType;
import fi.vm.sade.authentication.service.types.HenkiloSearchObjectType;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OsoiteDTO;
import fi.vm.sade.organisaatio.api.model.types.OsoiteTyyppi;
import fi.vm.sade.organisaatio.api.model.types.YhteystietoDTO;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.organisaatio.service.search.SearchCriteria;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenLiitteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenLiitteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusHakukohteelleTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKoulutuksineenKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKoulutuksineenVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenLiiteTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenLiiteTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenValintakoeTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenValintakoeTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenMetadataTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.NimettyMonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.TarkistaKoulutusKopiointiTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.ParameterServices;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.types.KomotoTeksti;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.enums.Koulutustyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.enums.SelectedOrgModel;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.OidCreationException;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.conversion.ConversionUtils;
import fi.vm.sade.tarjonta.ui.helper.conversion.HakukohdeLiiteTyyppiToViewModelConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.HakukohdeLiiteViewModelToDtoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.HakukohdeViewModelToDTOConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.Koulutus2asteConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusSearchSpecificationViewModelToDTOConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.ValintakoeConverter;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.org.NavigationModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.model.org.TarjoajaModel;
import fi.vm.sade.tarjonta.ui.service.PublishingService;
import fi.vm.sade.tarjonta.ui.service.UserContext;
import fi.vm.sade.tarjonta.ui.view.SearchResultsView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.CreationDialog;
import fi.vm.sade.tarjonta.ui.view.hakukohde.EditHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ShowHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.KoulutusContainerEvent;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusView;
import fi.vm.sade.tarjonta.ui.view.koulutus.aste2.EditKoulutusLisatiedotToinenAsteView;
import fi.vm.sade.tarjonta.ui.view.koulutus.aste2.EditKoulutusView;

/**
 * This class is used to control the "tarjonta" UI.
 * 
* @author jwilen
 * @author tkatva
 * @author mholi
 * @author mlyly
 * @author Timo Santasalo / Teknokala Ky
 */
public class TarjontaPresenter extends CommonPresenter<TarjontaModel> {

    private static Logger LOG = LoggerFactory.getLogger(TarjontaPresenter.class);
    private static final boolean KOODISTO_URIS_FROM_KOODISTO = true;
    private static final boolean LOAD_URIS_FROM_DB = false;

    private static final String LIITE_DATE_PATTERNS = "dd.MM.yyyy HH:mm";
    private static final String NAME_OPH = "OPH";
    @Autowired(required = true)
    private UserService userService;
    @Autowired(required = true)
    private UserContext userContext;
    // Services used
    @Autowired(required = true)
    private HakukohdeViewModelToDTOConverter hakukohdeToDTOConverter;
    @Autowired(required = true)
    private Koulutus2asteConverter koulutusToDTOConverter;
    @Autowired(required = true)
    private KoulutusKoodistoConverter kolutusKoodistoConverter;
    @Autowired(required = true)
    private transient TarjontaUIHelper uiHelper;
    private transient I18NHelper i18n = new I18NHelper(this);
    // Views this presenter can control
    private TarjontaModel _model;
    private TarjontaRootView _rootView;
    private ListHakukohdeView _hakukohdeListView;
    private PerustiedotView hakuKohdePerustiedotView;
    private ShowHakukohdeView hakukohdeView;
    private ShowKoulutusView showKoulutusView;
    private EditKoulutusView editKoulutusView;
    private SearchResultsView searchResultsView;
    private EditHakukohdeView editHakukohdeView;
    private EditKoulutusLisatiedotToinenAsteView lisatiedotView;
    @Autowired(required = true)
    private TarjontaLukioPresenter lukioPresenter;
    @Autowired(required = true)
    private TarjontaKorkeakouluPresenter korkeakouluPresenter;
    @Autowired
    private TarjontaSearchService tarjontaSearchService;
    @Autowired(required = true)
    protected PublishingService publishingService;
    @Autowired
    private ParameterServices parameterServices;

    public static final String VALINTAKOE_TAB_SELECT = "valintakokeet";
    public static final String LIITTEET_TAB_SELECT = "liitteet";

    public void saveHakuKohde(SaveButtonState tila) {
        HakukohdeViewModel hakukohde = getModel().getHakukohde();
        hakukohde.setTila(tila.toTarjontaTila(getModel().getHakukohde().getTila()));
        hakukohde.setHakukohdeKoodistoNimi(resolveHakukohdeKoodistonimiFields());

        saveHakuKohdePerustiedot();
        editHakukohdeView.enableLiitteetTab();
        editHakukohdeView.enableValintakokeetTab();
    }

    private void saveHakuKohdePerustiedot() {
        LOG.info("Form saved");
        //checkHakuLiitetoimitusPvm();
        String userOid = userContext.getUserOid();

        // OVT-4911
        getModel().setHakukohde(editHakukohdeView.getModel());
        HakukohdeTyyppi fresh;
        if (getModel().getHakukohde().getOid() == null) {

            HakukohdeTyyppi hakukohdeTyyppi = hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde());
            hakukohdeTyyppi.setViimeisinPaivittajaOid(userOid);
            getModel().getHakukohde().setOid(hakukohdeTyyppi.getOid());

            if (!hakukohdeTyyppi.getHakukohteenKoulutusaste().equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS)) {
                KoodiUriAndVersioType uriType = TarjontaUIHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(getModel().getHakukohde().getHakukohdeNimi());
                List<KoodiType> listKoodiByRelation = koodiService.listKoodiByRelation(uriType, true, SuhteenTyyppiType.SISALTYY);

                for (KoodiType koodi : listKoodiByRelation) {
                    final String koodistoUri = koodi.getKoodisto().getKoodistoUri();
                    if (KoodistoURI.KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI.equals(koodistoUri)) {
                        hakukohdeTyyppi.setValintaperustekuvausKoodiUri(TarjontaUIHelper.createVersionUri(koodi.getKoodiUri(), koodi.getVersio()));
                    }

                    if (KoodistoURI.KOODISTO_SORA_KUVAUSRYHMA_URI.equals(koodistoUri)) {
                        hakukohdeTyyppi.setSoraKuvausKoodiUri(TarjontaUIHelper.createVersionUri(koodi.getKoodiUri(), koodi.getVersio()));
                    }
                }
            }
            fresh = tarjontaAdminService.lisaaHakukohde(hakukohdeTyyppi);
        } else {
            updateHakukohdeKoulutusasteTyyppi(getModel().getHakukohde());
            fresh = tarjontaAdminService.paivitaHakukohde(hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde()));
        }
        refreshHakukohdeUIModel(fresh);
    }

    public HenkiloFatType getFatHenkiloWithOid(String oid) {

        return userService.findByOid(oid);

    }

    // Figure out the type
    private void updateHakukohdeKoulutusasteTyyppi(HakukohdeViewModel hakukohde) {
        Preconditions.checkNotNull(hakukohde);

        if (hakukohde.getKoulutusasteTyyppi() == null && hakukohde.getKomotoOids().size() > 0) {
            LueKoulutusVastausTyyppi koulutus = getKoulutusByOid(hakukohde.getKomotoOids().get(0));
            hakukohde.setKoulutusasteTyyppi(koulutus.getKoulutusmoduuli().getKoulutustyyppi());
        }

        Preconditions.checkNotNull(hakukohde.getKoulutusasteTyyppi(), "Can not figure out the type!");
    }

    public void saveHakukohdeLiite() {
        ArrayList<HakukohdeLiiteTyyppi> liitteet = new ArrayList<HakukohdeLiiteTyyppi>();
        HakukohdeLiiteViewModelToDtoConverter converter = new HakukohdeLiiteViewModelToDtoConverter();

        HakukohdeLiiteTyyppi hakukohdeLiite = converter.convertHakukohdeViewModelToHakukohdeLiiteTyyppi(getModel().getSelectedLiite());
        hakukohdeLiite.setLiitteenTyyppiKoodistoNimi(uiHelper.getKoodiNimi(hakukohdeLiite.getLiitteenTyyppi()));
        liitteet.add(hakukohdeLiite);

        for (HakukohdeLiiteViewModel hakuLiite : loadHakukohdeLiitteet(true)) {
            HakukohdeLiiteTyyppi liite = converter.convertHakukohdeViewModelToHakukohdeLiiteTyyppi(hakuLiite);
            liitteet.add(liite);
        }

        tarjontaAdminService.tallennaLiitteitaHakukohteelle(getModel().getHakukohde().getOid(), liitteet);
        getModel().setSelectedLiite(null);
        refreshHakukohdeUIModel(getModel().getHakukohde().getOid());
    }

    public void removeLiiteFromHakukohde(HakukohdeLiiteViewModel liite) {
        tarjontaAdminService.poistaHakukohdeLiite(liite.getHakukohdeLiiteId());
        editHakukohdeView.loadLiiteTableWithData();
        refreshHakukohdeUIModel(getModel().getHakukohde().getOid());
    }

    public void removeValintakoeFromHakukohde(ValintakoeViewModel valintakoe) {
        tarjontaAdminService.poistaValintakoe(valintakoe.getValintakoeTunniste());
        editHakukohdeView.loadValintakokees();
        refreshHakukohdeUIModel(getModel().getHakukohde().getOid());
    }

    public OsoiteDTO resolveSelectedOrganisaatioOsoite(OsoiteTyyppi tyyppi) {
        return resolveSelectedOrganisaatioOsoite(getSelectOrganisaatioModel(), tyyppi);
    }

    public OsoiteDTO resolveSelectedOrganisaatioOsoite(OrganisaatioDTO parent, OsoiteTyyppi tyyppi) {
        if (parent == null) {
            return null;
        }

        for (YhteystietoDTO yhteystietoDTO : parent.getYhteystiedot()) {
            if (yhteystietoDTO instanceof OsoiteDTO) {
                OsoiteDTO osoite = (OsoiteDTO) yhteystietoDTO;
                if (osoite.getOsoiteTyyppi().equals(OsoiteTyyppi.POSTI)) {
                    return osoite;
                }
            }
        }

        return parent.getParentOid() == null
                ? null
                : resolveSelectedOrganisaatioOsoite(organisaatioService.findByOid(parent.getParentOid()), tyyppi);
    }

    public OrganisaatioDTO getSelectOrganisaatioModel() {
        String orgOid = getTarjoaja().getSelectedOrganisationOid();
        OrganisaatioDTO organisaatioDTO = organisaatioService.findByOid(orgOid);
        return organisaatioDTO;
    }

    public void saveHakukohdeValintakoe(List<KielikaannosViewModel> kuvaukset) {
        Preconditions.checkNotNull(getModel().getHakukohde().getOid(), "Hakukohde OID cannot be null.");

        if (!kuvaukset.isEmpty()) {
            getModel().getSelectedValintaKoe().setSanallisetKuvaukset(kuvaukset);
        }
        if (!getModel().getSelectedValintaKoe().isEmpty()) {
            addOrReplaceSelectedValintakoe();
        }
        List<ValintakoeTyyppi> valintakokeet = new ArrayList<ValintakoeTyyppi>();
        for (ValintakoeViewModel valintakoeViewModel : getModel().getHakukohde().getValintaKokees()) {
            valintakokeet.add(ValintakoeConverter.mapKieliKaannosToValintakoeTyyppi(valintakoeViewModel));
        }

        tarjontaAdminService.tallennaValintakokeitaHakukohteelle(getModel().getHakukohde().getOid(), valintakokeet);

        getModel().getSelectedValintaKoe().clearModel();
        refreshHakukohdeUIModel(getModel().getHakukohde().getOid());

        editHakukohdeView.loadValintakokees();
        editHakukohdeView.refreshValintaKokeetLastUpdatedBy();
        editHakukohdeView.closeValintakoeEditWindow();
    }

    private void addOrReplaceSelectedValintakoe() {
        List<ValintakoeViewModel> updatedValintakokees = new ArrayList<ValintakoeViewModel>();
        for (ValintakoeViewModel curValintakoe : getModel().getHakukohde().getValintaKokees()) {
            if (!curValintakoe.getValintakoeTunniste().equals(getModel().getSelectedValintaKoe().getValintakoeTunniste())) {
                updatedValintakokees.add(curValintakoe);
            }
        }
        getModel().getHakukohde().getValintaKokees().clear();
        getModel().getHakukohde().getValintaKokees().addAll(updatedValintakokees);
        getModel().getHakukohde().getValintaKokees().add(getModel().getSelectedValintaKoe());
    }

    public void closeCancelHakukohteenEditView() {
        editHakukohdeView.closeHakukohdeLiiteEditWindow();
    }

    public void setHakukohteenOletusOsoiteToEmpty() {
        if (getModel().getHakukohde() != null) {
            getModel().getHakukohde().setPostinumero(null);
            getModel().getHakukohde().setPostitoimipaikka("");
            getModel().getHakukohde().setOsoiteRivi1("");
            getModel().getHakukohde().setOsoiteRivi2("");

        }
    }

    /**
     * Asettaa/tutkii liitteen toimitusosoitteen tilan (oletus vai muu).
     */
    public void setCustomLiiteOsoiteSelected(boolean enabled) {
        if (!enabled) {
            getSelectedHakuliite().setOsoiteRivi1(getModel().getHakukohde().getOsoiteRivi1());
            getSelectedHakuliite().setOsoiteRivi2(getModel().getHakukohde().getOsoiteRivi2());
            getSelectedHakuliite().setPostinumero(getModel().getHakukohde().getPostinumero());
            getSelectedHakuliite().setPostitoimiPaikka(getModel().getHakukohde().getPostitoimipaikka());
        }
    }

    public boolean isCustomLiiteOsoiteSelected() {
        return !Objects.equal(getSelectedHakuliite().getOsoiteRivi1(), getModel().getHakukohde().getOsoiteRivi1())
                || !Objects.equal(getSelectedHakuliite().getOsoiteRivi2(), getModel().getHakukohde().getOsoiteRivi2())
                || !Objects.equal(getSelectedHakuliite().getPostinumero(), getModel().getHakukohde().getPostinumero())
                || !Objects.equal(getSelectedHakuliite().getPostitoimiPaikka(), getModel().getHakukohde().getPostitoimipaikka());
    }

    public void saveHakukohteenEditView() {
        saveHakukohdeLiite();
        editHakukohdeView.loadLiiteTableWithData();
        editHakukohdeView.closeHakukohdeLiiteEditWindow();
    }

    private StringTuple getHakukohdeKoulutusAlkamisKausiVuosi(HakukohdeViewModel hakukohdeViewModel) {
        //Try to get hakukohtees koulutus alkamiskausi and vuosi from service, first koulutus will suffice because
        //all hakukohde koulutukses should have same alkamiskausi and vuosi.
        if (hakukohdeViewModel.getKoulukses() != null && hakukohdeViewModel.getKoulukses().size() > 0) {
            KoulutuksetKysely kyselyTyyppi = new KoulutuksetKysely();
            kyselyTyyppi.getKoulutusOids().add(hakukohdeViewModel.getKoulukses().get(0).getKoulutusOid());
            KoulutuksetVastaus vastausTyyppi = tarjontaSearchService.haeKoulutukset(kyselyTyyppi);
            return new StringTuple(vastausTyyppi.getKoulutukset().get(0).getKoulutuksenAlkamiskausi().getUri(),
                    vastausTyyppi.getKoulutukset().get(0).getKoulutuksenAlkamisVuosi().toString());
        } else if (hakukohdeViewModel.getKomotoOids() != null && hakukohdeViewModel.getKomotoOids().size() > 0) {
            KoulutuksetKysely kyselyTyyppi = new KoulutuksetKysely();
            kyselyTyyppi.getKoulutusOids().add(hakukohdeViewModel.getKomotoOids().get(0));
            KoulutuksetVastaus vastausTyyppi = tarjontaSearchService.haeKoulutukset(kyselyTyyppi);
            return new StringTuple(vastausTyyppi.getKoulutukset().get(0).getKoulutuksenAlkamiskausi().getUri(),
                    vastausTyyppi.getKoulutukset().get(0).getKoulutuksenAlkamisVuosi().toString());
        } else {
            return new StringTuple(null, null);
        }
    }

    public void initHakukohdeForm(PerustiedotView hakuKohdePerustiedotView) {
        this.hakuKohdePerustiedotView = hakuKohdePerustiedotView;
        if (getModel().getHakukohde() != null && getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite() != null && getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite().trim().length() > 0) {
            getModel().getHakukohde().setSahkoinenToimitusSallittu(true);
        } else {
            getModel().getHakukohde().setSahkoinenToimitusSallittu(false);
        }

        ListaaHakuTyyppi hakuKyselyTyyppi = new ListaaHakuTyyppi();

        StringTuple tuple = getHakukohdeKoulutusAlkamisKausiVuosi(getModel().getHakukohde());
        hakuKyselyTyyppi.setKoulutuksenAlkamisKausi(tuple.getStrOne());
        hakuKyselyTyyppi.setKoulutuksenAlkamisVuosi(new Integer(tuple.getStrTwo()));
        ListHakuVastausTyyppi haut = tarjontaPublicService.listHaku(hakuKyselyTyyppi);

        this.hakuKohdePerustiedotView.initForm();

        prepareHakuSelections(haut);

    }

    public String getUserOid() {
        return userContext.getUserOid();
    }

    /*
     * Prepares the haku combobox selector in hakukohde form.
     */
    private void prepareHakuSelections(ListHakuVastausTyyppi haut) {

        HakuViewModel hakuView = null;
        if (getModel().getHakukohde() != null && getModel().getHakukohde().getHakuViewModel() != null) {
            hakuView = getModel().getHakukohde().getHakuViewModel();
        }

        List<HakuViewModel> foundHaut = findMatchingHakusForHakukohde(haut);

        
        //XXX HJVO-55 suodata pois haut joihin ei saa koskea (permissiot!) 
        
        Collections.sort(foundHaut, new Comparator<HakuViewModel>() {
            @Override
            public int compare(HakuViewModel a, HakuViewModel b) {
                int ret = a.getNimi().compareTo(b.getNimi());
                return ret != 0 ? ret : a.getHakuOid().compareTo(b.getHakuOid());
            }
        });

        this.hakuKohdePerustiedotView.addItemsToHakuCombobox(foundHaut);

        if (hakuView != null) {
            getModel().getHakukohde().setHakuViewModel(hakuView);
            ListaaHakuTyyppi hakuKysely = new ListaaHakuTyyppi();
            hakuKysely.setHakuOid(getModel().getHakukohde().getHakuViewModel().getHakuOid());
            ListHakuVastausTyyppi hakuVastaus = tarjontaPublicService.listHaku(hakuKysely);
            HakuViewModel hakuModel = new HakuViewModel(hakuVastaus.getResponse().get(0));
            hakuModel.getHakuOid();
            hakuModel.getMlNimiFi();
            getModel().getHakukohde().setHakuViewModel(hakuModel);
            this.hakuKohdePerustiedotView.setSelectedHaku(hakuView);
        }
    }

    /*
     * Finds the haku instances matching the koulutus for which the hakukohde is created.
     */
    private List<HakuViewModel> findMatchingHakusForHakukohde(ListHakuVastausTyyppi haut) {

        KoulutusasteTyyppi koulTyyppi = getModel().getSelectedKoulutukset().get(0).getKoulutustyyppi();

        List<HakuViewModel> foundHaut = new ArrayList<HakuViewModel>();

        String pkVaatimus = null;
        if (getModel().getSelectedKoulutukset() != null
                && getModel().getSelectedKoulutukset().get(0) != null
                && getModel().getSelectedKoulutukset().get(0).getPohjakoulutusvaatimus() != null) {
            pkVaatimus = getModel().getSelectedKoulutukset().get(0).getPohjakoulutusvaatimus().getUri();
        }

        boolean isKoulutusErityisopetus = (pkVaatimus != null)
                && pkVaatimus.contains(KoodistoURI.KOODI_YKSILOLLISTETTY_PERUSOPETUS_URI)
                && koulTyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        boolean isKoulutusValmentavaOpetus = koulTyyppi.equals(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS);
        boolean isValmistavaKoulutus = koulTyyppi.equals(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS)
                || koulTyyppi.equals(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS)
                || koulTyyppi.equals(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS)
                || koulTyyppi.equals(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS);
        boolean isVapaaSivistystyo = koulTyyppi.equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS);

        for (HakuTyyppi foundHaku : haut.getResponse()) {
            if (isKoulutusErityisopetus
                    && foundHaku.getHakutapaUri().equals(KoodistoURI.KOODI_ERILLISHAKU_URI)
                    && foundHaku.getKohdejoukkoUri().equals(KoodistoURI.KOODI_KOHDEJOUKKO_ERITYISOPETUS_URI)) {
                foundHaut.add(new HakuViewModel(foundHaku));
            } else if (!isKoulutusErityisopetus
                    && getModel().getSelectedKoulutukset().get(0).getKoulutustyyppi().equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)
                    && foundHaku.getHakutapaUri().equals(KoodistoURI.KOODI_YHTEISHAKU_URI)
                    && foundHaku.getKohdejoukkoUri().equals(KoodistoURI.KOODI_KOHDEJOUKKO_AMMATILLINEN_LUKIO_URI)) {
                foundHaut.add(new HakuViewModel(foundHaku));
            } else if (isKoulutusValmentavaOpetus
                    && foundHaku.getHakutapaUri().equals(KoodistoURI.KOODI_ERILLISHAKU_URI)
                    && foundHaku.getKohdejoukkoUri().equals(KoodistoURI.KOODI_KOHDEJOUKKO_VALMENTAVA_URI)) {
                foundHaut.add(new HakuViewModel(foundHaku));
            } else if (isValmistavaKoulutus
                    && foundHaku.getKohdejoukkoUri().equals(KoodistoURI.KOODI_KOHDEJOUKKO_VALMISTAVA_URI)) {
                foundHaut.add(new HakuViewModel(foundHaku));
            } else if (isVapaaSivistystyo
                    && foundHaku.getKohdejoukkoUri().equals(KoodistoURI.KOODI_KOHDEJOUKKO_VAPAASIVISTYS_URI)) {
                foundHaut.add(new HakuViewModel(foundHaku));
            } else if (getModel().getSelectedKoulutukset().get(0).getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)
                    && foundHaku.getHakutapaUri().equals(KoodistoURI.KOODI_YHTEISHAKU_URI)
                    && foundHaku.getKohdejoukkoUri().equals(KoodistoURI.KOODI_KOHDEJOUKKO_AMMATILLINEN_LUKIO_URI)) {
                foundHaut.add(new HakuViewModel(foundHaku));
            }
        }
        return Lists.newArrayList(Iterables.filter(foundHaut, new HakuParameterPredicate(parameterServices, tarjontaPermissionService)));
    }

    /**
     * Show main default view
     */
    @Override
    public void showMainDefaultView() {
        LOG.info("showMainDefaultView()");
        getRootView().showMainView();
    }

    public void reloadAndShowMainDefaultView() {
        reloadMainView();
        getRootView().showMainView();
    }

    public void closeValintakoeEditWindow() {
        editHakukohdeView.closeValintakoeEditWindow();
    }

    //Tuomas Katva : two following methods break the presenter pattern consider moving everything except service call to view
    public CreationDialog<KoulutusOidNameViewModel> createHakukohdeCreationDialogWithKomotoOids(List<String> komotoOids) {
        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.getKoulutusOids().addAll(komotoOids);
        //KoulutuksetVastaus vastaus = tarjontaPublicService.haeKoulutukset(kysely);

        List<KoulutusOidNameViewModel> koulutusModel = convertKoulutusToNameOidViewModel(getSelectedKoulutukset());//vastaus.getKoulutukset());

        CreationDialog<KoulutusOidNameViewModel> dialog = new CreationDialog<KoulutusOidNameViewModel>(koulutusModel, KoulutusOidNameViewModel.class, "HakukohdeCreationDialog.title", null, true);
        List<String> validationMessages = validateKoulutukses(getSelectedKoulutukset());//vastaus.getKoulutukset());
        if (validationMessages != null && validationMessages.size() > 0) {
            for (String validationMessage : validationMessages) {
                dialog.addErrorMessage(validationMessage);
            }
        }

        return dialog;
    }

    public List<String> validateKoulutusOidNameViewModel(Collection<KoulutusOidNameViewModel> koulutukses) {
        List<String> selectedOids = new ArrayList<String>();
        for (KoulutusOidNameViewModel koulutusOidNameViewModel : koulutukses) {
            selectedOids.add(koulutusOidNameViewModel.getKoulutusOid());
        }

        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.getKoulutusOids().addAll(selectedOids);
        KoulutuksetVastaus vastaus = tarjontaSearchService.haeKoulutukset(kysely);
        return validateKoulutukses(vastaus.getKoulutukset());
    }

    private List<String> validateKoulutukses(List<KoulutusPerustieto> koulutukses) {

        List<String> returnVal = new ArrayList<String>();
        List<String> koulutusKoodis = new ArrayList<String>();
        List<String> pohjakoulutukses = new ArrayList<String>();
        Set<String> tarjoajaOids = new HashSet<String>();

        Set<String> koulutusAlkamiskaudes = new HashSet<String>();
        Set<Integer> koulutusAlkamisVuodes = new HashSet<Integer>();
        for (KoulutusPerustieto koulutus : koulutukses) {
            koulutusAlkamiskaudes.add(koulutus.getKoulutuksenAlkamiskausi().getUri());
            koulutusAlkamisVuodes.add(koulutus.getKoulutuksenAlkamisVuosi());
            koulutusKoodis.add(koulutus.getKoulutuskoodi().getUri());
            pohjakoulutukses.add(koulutus.getPohjakoulutusvaatimus().getUri());
            tarjoajaOids.add(koulutus.getTarjoaja().getOid());
        }
        if (!doesEqual(koulutusKoodis.toArray(new String[koulutusKoodis.size()]))) {
            returnVal.add(I18N.getMessage("HakukohdeCreationDialog.wrongKoulutuskoodi"));
        }
        if (!doesEqual(pohjakoulutukses.toArray(new String[pohjakoulutukses.size()]))) {
            returnVal.add(I18N.getMessage("HakukohdeCreationDialog.wrongPohjakoulutus"));
        }

        if (!doesEqual(pohjakoulutukses.toArray(new String[pohjakoulutukses.size()]))) {
            returnVal.add(I18N.getMessage("HakukohdeCreationDialog.wrongPohjakoulutus"));
        }

        if (tarjoajaOids.size() > 1) {
            returnVal.add(I18N.getMessage("HakukohdeCreationDialog.tarjoajaDoesNotMatch"));
        }

        if (koulutusAlkamiskaudes.size() > 1 || koulutusAlkamisVuodes.size() > 1) {
            returnVal.add(I18N.getMessage("HakukohdeCreationDialog.koulutusAlkaminenDoesNotMatch"));
        }

        return returnVal;
    }

    private boolean doesEqual(String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            if (!strs[0].equals(strs[i])) {
                return false;
            }
        }
        return true;
    }

    public CreationDialog<KoulutusOidNameViewModel> createHakukohdeCreationDialogWithSelectedTarjoaja() {
        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.getTarjoajaOids().add(getNavigationOrganisation().getOrganisationOid());
        KoulutuksetVastaus vastaus = tarjontaSearchService.haeKoulutukset(kysely);
        LueKoulutusKyselyTyyppi lueK = new LueKoulutusKyselyTyyppi();
        lueK.setOid(getModel().getHakukohde().getKomotoOids().get(0));
        LueKoulutusVastausTyyppi koulutus = this.tarjontaPublicService.lueKoulutus(lueK);
        List<KoulutusPerustieto> validKoulutukses = getValidKoulutuksesForHakukohde(vastaus, koulutus.getKoulutusKoodi(), koulutus.getPohjakoulutusvaatimus(), koulutus.getTarjoaja());
        List<KoulutusOidNameViewModel> filtedredKoulutukses = removeSelectedKoulutukses(convertKoulutusToNameOidViewModel(validKoulutukses));
        CreationDialog<KoulutusOidNameViewModel> dialog = new CreationDialog<KoulutusOidNameViewModel>(filtedredKoulutukses, KoulutusOidNameViewModel.class, "ShowHakukohdeViewImpl.liitaUusiKoulutusDialogSecondaryTitle", "HakukohdeCreationDialog.valitutKoulutuksetOptionGroup", false);
        return dialog;

    }

    private List<KoulutusPerustieto> getValidKoulutuksesForHakukohde(
            KoulutuksetVastaus vastaus, KoodistoKoodiTyyppi koulutuskoodi, KoodistoKoodiTyyppi pohjakoulutuskoodi, String tarjoajaOid) {
        ListaaHakuTyyppi kysely = new ListaaHakuTyyppi();
        kysely.setHakuOid(getModel().getHakukohde().getHakuViewModel().getHakuOid());
        ListHakuVastausTyyppi hakuVastaus = this.tarjontaPublicService.listHaku(kysely);
        if (hakuVastaus.getResponse().isEmpty()) {
            return new ArrayList<KoulutusPerustieto>();
        }
        HakuTyyppi hakuT = hakuVastaus.getResponse().get(0);
        List<KoulutusPerustieto> validKoulutukses = new ArrayList<KoulutusPerustieto>();
        for (KoulutusPerustieto curKoulutus : vastaus.getKoulutukset()) {
            if (curKoulutus.getTarjoaja().getOid().equals(tarjoajaOid) && curKoulutus.getKoulutuskoodi().getUri().equals(koulutuskoodi.getUri())
                    && curKoulutus.getPohjakoulutusvaatimus().getUri().contains(pohjakoulutuskoodi.getUri())
                    && curKoulutus.getKoulutuksenAlkamiskausi().getUri().equals(hakuT.getKoulutuksenAlkamisKausiUri())
                    && curKoulutus.getKoulutuksenAlkamisVuosi().equals(hakuT.getKoulutuksenAlkamisVuosi())) {
                validKoulutukses.add(curKoulutus);
            }
        }

        return validKoulutukses;
    }

    private List<KoulutusOidNameViewModel> removeSelectedKoulutukses(List<KoulutusOidNameViewModel> koulutukses) {
        List<KoulutusOidNameViewModel> filteredKoulutukses = new ArrayList<KoulutusOidNameViewModel>();
        for (KoulutusOidNameViewModel koulutus : koulutukses) {
            boolean koulutusFound = false;
            for (String oid : getModel().getHakukohde().getKomotoOids()) {
                if (koulutus.getKoulutusOid().trim().equalsIgnoreCase(oid)) {
                    koulutusFound = true;
                }
            }
            if (!koulutusFound) {
                filteredKoulutukses.add(koulutus);
            }
        }
        return filteredKoulutukses;
    }

    private List<KoulutusOidNameViewModel> convertKoulutusToNameOidViewModel(List<KoulutusPerustieto> tulokset) {
        List<KoulutusOidNameViewModel> result = new ArrayList<KoulutusOidNameViewModel>();

        for (KoulutusPerustieto tulos : tulokset) {

            KoulutusOidNameViewModel nimiOid = new KoulutusOidNameViewModel();

            nimiOid.setKoulutusOid(tulos.getKomotoOid());
            String nimi = "";

            if (tulos.getNimi() != null) {
                nimi = TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), tulos.getNimi());
            }

            nimiOid.setKoulutusNimi(nimi);
            nimiOid.setKoulutustyyppi(tulos.getKoulutustyyppi());
            nimiOid.setKoulutustyyppi(tulos.getKoulutustyyppi());
            result.add(nimiOid);

        }

        return result;
    }

    public void removeHakukohdeFromKoulutus(String hakukohdeOid) {
        try {
            LisaaKoulutusHakukohteelleTyyppi req = new LisaaKoulutusHakukohteelleTyyppi();
            req.setLisaa(false);
            req.setHakukohdeOid(hakukohdeOid);
            req.getKoulutusOids().add(getModel().getKoulutusPerustiedotModel().getOid());
            tarjontaAdminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(req);
            HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
            hakukohde.setOid(hakukohdeOid);
        } catch (Exception exp) {
            if (exp.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException")) {
                showKoulutusView.addErrorMsg("hakukohdeRemovalErrorMsg");
            }
        }
        //tarjontaAdminService.poistaHakukohde(hakukohde);
        showShowKoulutusView(getModel().getKoulutusPerustiedotModel().getOid());
    }

    public void removeLukioHakukohdeFromKoulutus(String hakukohdeOid) {
        try {
            LisaaKoulutusHakukohteelleTyyppi req = new LisaaKoulutusHakukohteelleTyyppi();
            req.setLisaa(false);
            req.setHakukohdeOid(hakukohdeOid);
            req.getKoulutusOids().add(getModel().getKoulutusLukioPerustiedot().getKomotoOid());
            tarjontaAdminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(req);
            HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
            hakukohde.setOid(hakukohdeOid);
        } catch (Exception exp) {
            if (exp.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException")) {
                showKoulutusView.addErrorMsg("hakukohdeRemovalErrorMsg");
            }
        }
        //tarjontaAdminService.poistaHakukohde(hakukohde);
        getLukioPresenter().showSummaryKoulutusView(getModel().getKoulutusLukioPerustiedot().getKomotoOid());
        //showShowKoulutusView(getModel().getKoulutusPerustiedotModel().getOid());
    }

    public void removeKoulutusFromHakukohde(KoulutusOidNameViewModel koulutus) {
        int hakukohdeKoulutusCount = getModel().getHakukohde().getKoulukses().size();
        List<String> poistettavatKoulutukses = new ArrayList<String>();
        poistettavatKoulutukses.add(koulutus.getKoulutusOid());
        LisaaKoulutusHakukohteelleTyyppi req = new LisaaKoulutusHakukohteelleTyyppi();
        req.setHakukohdeOid(getModel().getHakukohde().getOid());
        req.getKoulutusOids().addAll(poistettavatKoulutukses);
        req.setLisaa(false);
        try {
            tarjontaAdminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(req);
            if (hakukohdeKoulutusCount > 1) {
                showHakukohdeViewImpl(getModel().getHakukohde().getOid());
            } else {
                reloadAndShowMainDefaultView();
            }
        } catch (Exception exp) {
            if (exp.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException")) {
                hakukohdeView.showErrorMsg("hakukohdeRemovalErrorMsg");
            }
        }
        /* HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();
         hakukohdeTyyppi.setOid(getModel().getHakukohde().getOid());
         tarjontaAdminService.poistaHakukohde(hakukohdeTyyppi);*/
        //If removing last koulutus from hakukohde then hakukohde is not valid
        //anymore, show main view instead

    }

    public void addKoulutuksesToHakukohde(Collection<KoulutusOidNameViewModel> koulutukses) {

        List<String> koulutusOids = new ArrayList<String>();
        for (KoulutusOidNameViewModel koulutus : koulutukses) {
            koulutusOids.add(koulutus.getKoulutusOid());
        }
        koulutusOids.addAll(getModel().getHakukohde().getKomotoOids());
        LisaaKoulutusHakukohteelleTyyppi req = new LisaaKoulutusHakukohteelleTyyppi();
        req.setHakukohdeOid(getModel().getHakukohde().getOid());
        req.getKoulutusOids().addAll(koulutusOids);
        req.setLisaa(true);
        tarjontaAdminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(req);

        showHakukohdeViewImpl(getModel().getHakukohde().getOid());
    }

    /*
     * Show hakukohde overview view
     */
    public void showHakukohdeViewImpl(final String hakukohdeOid) {
        if (hakukohdeOid != null) {
            LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
            kysely.setOid(hakukohdeOid);
            LueHakukohdeVastausTyyppi vastaus = tarjontaPublicService.lueHakukohde(kysely);
            if (vastaus.getHakukohde() != null) {
                //create name string
                hakukohdeToDTOConverter.convertDTOToHakukohdeViewMode(getModel().getHakukohde(), vastaus.getHakukohde());
                HakukohdeViewModel hakukohde = getModel().getHakukohde();
                hakukohde.setHakukohdeKoodistoNimi(resolveHakukohdeKoodistonimiFields());
                hakukohde.setKoulukses(getHakukohdeKoulutukses(getModel().getHakukohde()));

                hakukohdeView = new ShowHakukohdeView(hakukohde.getHakukohdeKoodistoNimi(), null, null);
                getRootView().changeView(hakukohdeView);
            }
        }
    }

    public List<OrganisaatioPerustieto> fetchChildOrganisaatios(List<String> organisaatioOids) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setAktiiviset(true);


        criteria.getOidRestrictionList().addAll(organisaatioOids);
        criteria.setSuunnitellut(true);

        return organisaatioSearchService.searchBasicOrganisaatios(criteria);
    }

    private List<KoulutusOidNameViewModel> getHakukohdeKoulutukses(HakukohdeViewModel hakukohdeViewModel) {
        List<KoulutusOidNameViewModel> koulutukses = new ArrayList<KoulutusOidNameViewModel>();

        LueHakukohdeKoulutuksineenKyselyTyyppi kysely = new LueHakukohdeKoulutuksineenKyselyTyyppi();
        kysely.setHakukohdeOid(hakukohdeViewModel.getOid());
        LueHakukohdeKoulutuksineenVastausTyyppi vastaus = tarjontaPublicService.lueHakukohdeKoulutuksineen(kysely);
        if (vastaus.getHakukohde() != null && vastaus.getHakukohde().getHakukohdeKoulutukses() != null) {

            List<KoulutusKoosteTyyppi> koulutusKoostes = vastaus.getHakukohde().getHakukohdeKoulutukses();
            for (KoulutusKoosteTyyppi koulutusKooste : koulutusKoostes) {
                KoulutusOidNameViewModel koulutus = new KoulutusOidNameViewModel();
                koulutus.setKoulutusNimi(buildKoulutusCaption(koulutusKooste));
                koulutus.setKoulutusOid(koulutusKooste.getKomotoOid());
                koulutus.setKoulutustyyppi(koulutusKooste.getKoulutustyyppi());
                koulutukses.add(koulutus);
            }
        }

        return koulutukses;
    }

    private String buildKoulutusCaption(KoulutusKoosteTyyppi curKoulutus) {
        String caption = getKoulutusNimi(curKoulutus);
        caption += ", " + this.tilaToLangStr(curKoulutus.getTila());
        return caption;
    }

    public void setAllSelectedOrganisaatios(Collection<OrganisaatioPerustieto> orgs) {
        getTarjoaja().addSelectedOrganisations(orgs);
    }

    public void showKoulutusEditView(Collection<OrganisaatioPerustieto> orgs, String pohjakoulutusvaatimusUri, KoodiType type) {
        getTarjoaja().setSelectedResultRowOrganisationOid(null); //clear tarjoaja model
        getTarjoaja().addSelectedOrganisations(orgs); //add orgs to rajoaja model

        getModel().getKoulutusPerustiedotModel().clearModel(DocumentStatus.NEW);
        this.getModel().getKoulutusPerustiedotModel().setPohjakoulutusvaatimus(pohjakoulutusvaatimusUri);
        KoodiModel koulutuksenTyyppi = new KoodiModel();
        koulutuksenTyyppi.setKoodistoUri(type.getKoodiUri());//setKoodi(type.getKoodiUri());
        koulutuksenTyyppi.setKoodistoUriVersio(type.getKoodiUri());
        koulutuksenTyyppi.setKoodi(type.getKoodiUri());
        this.getModel().getKoulutusPerustiedotModel().setKoulutuksenTyyppi(koulutuksenTyyppi);
        getModel().setKoulutusLisatiedotModel(new KoulutusLisatiedotModel());
        readOrgTreeToTarjoajaByModel(SelectedOrgModel.TARJOAJA);
        showEditKoulutusView(null, KoulutusActiveTab.PERUSTIEDOT);
    }

    public void showNewKoulutusEditView(final KoulutusActiveTab tab) {
    }

    public void copyKoulutusToOrganizations(Collection<OrganisaatioPerustieto> targetOrgs, String pohjakoulutusVaatimus) {
        getTarjoaja().addSelectedOrganisations(targetOrgs);
        showCopyKoulutusPerustiedotEditView(getModel().getSelectedKoulutusOid(), targetOrgs, pohjakoulutusVaatimus);
        getModel().getSelectedKoulutukset().clear();
    }

    public void copyLukioKoulutusToOrganization(Collection<OrganisaatioPerustieto> orgs) {
        lukioPresenter.showCopyKoulutusView(getModel().getSelectedKoulutusOid(), KoulutusActiveTab.PERUSTIEDOT, orgs);

        getModel().getSelectedKoulutukset().clear();
    }

    private String getKoulutusNimi(KoulutusKoosteTyyppi curKoulutus) {
        String nimi = getKoodiNimi(curKoulutus.getKoulutuskoodi());
        if (curKoulutus.getKoulutusohjelmakoodi() != null) {
            nimi += ", " + getKoodiNimi(curKoulutus.getKoulutusohjelmakoodi());
        }
        nimi += ", " + curKoulutus.getAjankohta();
        return nimi;
    }

    public String getKoodiNimi(String hakukohdeUri) {
        String nimi = this.getUiHelper().getKoodiNimi(hakukohdeUri, I18N.getLocale());
        if ("".equals(nimi)) {
            nimi = hakukohdeUri;
        }
        return nimi;
    }

    public void showShowKoulutusView(String koulutusOid) {
        readKoulutusToModel(koulutusOid, LOAD_URIS_FROM_DB);

        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        String title;
        final KoulutusasteType koulutusaste = model.getSelectedKoulutusasteType();
        switch (koulutusaste) {
            case TOINEN_ASTE_AMMATILLINEN_KOULUTUS:
                title = model.getKoulutuskoodiModel().getNimi()
                        + ", "
                        + model.getKoulutusohjelmaModel().getNimi();
                break;
            case TUNTEMATON:
                title = model.getKoulutuskoodiModel().getNimi()
                        + ", "
                        + model.getKoulutusohjelmaModel().getNimi();
                break;
            case PERUSOPETUKSEN_LISAOPETUS:
                title = model.getKoulutuskoodiModel().getNimi()
                        + ", "
                        + model.getKoulutusohjelmaModel().getNimi();
                break;
            case TOINEN_ASTE_LUKIO:
                title = model.getKoulutuskoodiModel().getNimi();
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(koulutusaste));
        }

        showKoulutusView = new ShowKoulutusView(title, null);
        getRootView().changeView(showKoulutusView);
    }

    /**
     * Show koulutus overview view.
     */
    public void showShowKoulutusView() {
        showShowKoulutusView(getModel().getKoulutusPerustiedotModel().getOid());
    }

    public void setKomotoOids(List<String> komotoOids) {
        getModel().getHakukohde().setKomotoOids(komotoOids);
    }

    public void showCopyKoulutusPerustiedotEditView(final String koulutusOid, Collection<OrganisaatioPerustieto> orgs, String pohjakoulutusVaatimus) {

        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
        if (koulutusOid != null) {
            copyKoulutusToModel(koulutusOid);

            if (orgs != null && orgs.size() > 0) {

                getModel().getTarjoajaModel().getOrganisationOidNamePairs().clear();
                for (OrganisaatioPerustieto org : orgs) {
                    OrganisationOidNamePair oidNamePair = new OrganisationOidNamePair(org.getOid(), OrganisaatioDisplayHelper.getClosestBasic(I18N.getLocale(), org));
                    getModel().getTarjoajaModel().getOrganisationOidNamePairs().add(oidNamePair);
                }
            }
            getModel().getKoulutusPerustiedotModel().setOid("-1");
            getModel().getKoulutusPerustiedotModel().setTila(TarjontaTila.LUONNOS);
            getModel().getKoulutusPerustiedotModel()
                    .setPohjakoulutusvaatimus(pohjakoulutusVaatimus);
            showEditKoulutusView(koulutusOid, KoulutusActiveTab.PERUSTIEDOT);

        }
    }

    public void showLukioCopyKoulutusPerustiedotView(final String koulutusOid) {
        if (koulutusOid != null) {
        }
    }

    public void showKoulutustEditView(final String koulutusOid, final KoulutusActiveTab tab) {
        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
        if (koulutusOid != null) {
            LOG.info("readeing koulutus:" + koulutusOid);
            readKoulutusToModel(koulutusOid, KOODISTO_URIS_FROM_KOODISTO);
        } else {
            Preconditions.checkNotNull(getTarjoaja().getSelectedOrganisationOid(), "Missing organisation OID.");
            getTarjoaja().setSelectedResultRowOrganisationOid(null);
            getModel().getKoulutusPerustiedotModel().clearModel(DocumentStatus.NEW);
            getModel().setKoulutusLisatiedotModel(new KoulutusLisatiedotModel());
        }
        readOrgTreeToTarjoajaByModel(SelectedOrgModel.TARJOAJA);
        showEditKoulutusView(koulutusOid, tab);
    }

    public void showLisaaRinnakkainenToteutusEditView(final String koulutusOid, String pohjakoulutusVaatimus) {
        if (koulutusOid != null) {
            readKoulutusToModel(koulutusOid, KOODISTO_URIS_FROM_KOODISTO);
            readOrgTreeToTarjoajaByModel(SelectedOrgModel.TARJOAJA);

            getModel().getKoulutusPerustiedotModel().getKoulutuksenHakukohteet().clear();
            getModel().getKoulutusPerustiedotModel().setOpetuskieli(null);
            getModel().getKoulutusPerustiedotModel().setOid("-1");
            getModel().getKoulutusPerustiedotModel().setSuunniteltuKesto(null);
            getModel().getKoulutusPerustiedotModel().setPohjakoulutusvaatimus(pohjakoulutusVaatimus);
            //Valiakainen pakotettu valinta
            getModel().getKoulutusPerustiedotModel().setKoulutuslaji(KoodistoURI.KOODI_KOULUTUSLAJI_NUORTEN_KOULUTUS_URI);
            getModel().getKoulutusPerustiedotModel().setOpetusmuoto(null);
            getModel().getKoulutusPerustiedotModel().setTila(null);
            getModel().setKoulutusLisatiedotModel(new KoulutusLisatiedotModel());

            showEditKoulutusView(getModel().getKoulutusPerustiedotModel().getOid(), KoulutusActiveTab.PERUSTIEDOT);

        }
    }

    public void readOrgTreeToTarjoajaByModel(final SelectedOrgModel modelType) {
        Preconditions.checkNotNull(modelType, "SelectedOrgModel enum cannot be null.");

        switch (modelType) {
            case TARJOAJA:
                readOrgTree(getTarjoaja().getSelectedOrganisationOid());
                break;

            case NAVIGATION:
                readOrgTree(getNavigationOrganisation().getOrganisationOid());
                break;
        }
    }

    /*
     * Retrieves the oids of organisaatios that belong to the organisaatio tree of the organisaatio the oid of which is
     * given as a parameter to this method.
     * The retrieved oid list is used when querying for potential yhteyshenkilos of a koulutus object.
     */
    private void readOrgTree(final String organisaatioOid) {
        Preconditions.checkNotNull(organisaatioOid, "Organisation OID cannot be null.");

        LOG.info("getting org oid tree by oid : {}", organisaatioOid);
        SearchCriteria dto = new SearchCriteria();
        dto.setAktiiviset(true);

        dto.getOidRestrictionList().add(organisaatioOid);
        try {
            List<OrganisaatioPerustieto> orgs = organisaatioSearchService.searchBasicOrganisaatios(dto);
            List<String> organisaatioOidTree = new ArrayList<String>();
            for (OrganisaatioPerustieto perus : orgs) {
                if (perus != null) {
                    organisaatioOidTree.add(perus.getOid());
                }
            }
            getTarjoaja().setOrganisaatioOidTree(organisaatioOidTree);

        } catch (Exception ex) {
            LOG.error("Problem fetching organisaatio oid tree: {}", ex.getMessage());
        }
        LOG.info("getting org oid tree, done.");
    }

    private void copyKoulutusToModel(final String koulutusOid) {
        LueKoulutusVastausTyyppi lueKoulutus = this.getKoulutusByOid(koulutusOid);
        try {
            KoulutusToisenAsteenPerustiedotViewModel koulutus;

            koulutus = koulutusToDTOConverter.createKoulutusPerustiedotViewModel(getModel(), lueKoulutus, I18N.getLocale(), KOODISTO_URIS_FROM_KOODISTO);
            readOrgTreeToTarjoajaByModel(SelectedOrgModel.TARJOAJA);
            getModel().setKoulutusPerustiedotModel(koulutus);
            getModel().setKoulutusLisatiedotModel(koulutusToDTOConverter.createKoulutusLisatiedotViewModel(lueKoulutus));

            //Empty previous Koodisto data from the comboboxes.
            koulutus.getKoulutusohjelmat().clear();
            koulutus.getKoulutuskoodit().clear();
            koulutus.getKoulutuksenHakukohteet().clear();

            //Add selected data to the comboboxes.
            if (koulutus.getKoulutusohjelmaModel() != null && koulutus.getKoulutusohjelmaModel().getKoodistoUri() != null) {

                getModel().getKoulutusPerustiedotModel().getKoulutusohjelmat().add(koulutus.getKoulutusohjelmaModel());
            }
            getModel().getKoulutusPerustiedotModel().setKoulutuslaji(koulutus.getKoulutuslaji());
            getModel().getKoulutusPerustiedotModel().setPohjakoulutusvaatimus(koulutus.getPohjakoulutusvaatimus());

            getModel().getKoulutusPerustiedotModel().setSuunniteltuKesto(koulutus.getSuunniteltuKesto());
            getModel().getKoulutusPerustiedotModel().setSuunniteltuKestoTyyppi(koulutus.getSuunniteltuKestoTyyppi());
            koulutus.getKoulutuskoodit().add(koulutus.getKoulutuskoodiModel());
        } catch (OidCreationException ex) {
            LOG.error("Service call failed.", ex);
            showMainDefaultView();
        }
    }

    private void readKoulutusToModel(final String koulutusOid, final boolean searchLatestKoodistoUris) {

        LueKoulutusVastausTyyppi rawKoulutus = this.getKoulutusByOid(koulutusOid);
        try {
            KoulutusToisenAsteenPerustiedotViewModel koulutus;

            koulutus = koulutusToDTOConverter.createKoulutusPerustiedotViewModel(getModel(), rawKoulutus, I18N.getLocale(), searchLatestKoodistoUris);

            KoodiModel tyyppiModel = new KoodiModel();
            if (rawKoulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS)) {
                tyyppiModel.setKoodi(Koulutustyyppi.TOINEN_ASTE_VALMENTAVA_KOULUTUS.getKoulutustyyppiUri());
            } else if (rawKoulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
                tyyppiModel.setKoodi(Koulutustyyppi.TOINEN_ASTE_AMMATILLINEN_KOULUTUS.getKoulutustyyppiUri());
            } else if (rawKoulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
                tyyppiModel.setKoodi(Koulutustyyppi.TOINEN_ASTE_LUKIO.getKoulutustyyppiUri());
            } else if (rawKoulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS)) {
                tyyppiModel.setKoodi(Koulutustyyppi.AMMATILLISEEN_OHJAAVA_KOULUTUS.getKoulutustyyppiUri());
            } else if (rawKoulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS)) {
                tyyppiModel.setKoodi(Koulutustyyppi.PERUSOPETUKSEN_LISAOPETUS.getKoulutustyyppiUri());
            } else if (rawKoulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS)) {
                tyyppiModel.setKoodi(Koulutustyyppi.MAMU_AMMATILLISEEN_OHJAAVA_KOULUTUS.getKoulutustyyppiUri());
            } else if (rawKoulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS)) {
                tyyppiModel.setKoodi(Koulutustyyppi.MAMU_LUKIOON_OHJAAVA_KOULUTUS.getKoulutustyyppiUri());
            } else if (rawKoulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS)) {
                tyyppiModel.setKoodi(Koulutustyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS.getKoulutustyyppiUri());
            }
            koulutus.setKoulutuksenTyyppi(tyyppiModel);

            getModel().setKoulutusPerustiedotModel(koulutus);
            getModel().setKoulutusLisatiedotModel(koulutusToDTOConverter.createKoulutusLisatiedotViewModel(rawKoulutus));

            //Empty previous Koodisto data from the comboboxes.
            koulutus.getKoulutusohjelmat().clear();
            koulutus.getKoulutuskoodit().clear();
            if (rawKoulutus.getHakukohteet() != null) {
                koulutus.getKoulutuksenHakukohteet().clear();
                HakukohteetVastaus hakukVastaus = this.getHakukohteetForKoulutus(koulutusOid);
                for (HakukohdePerustieto hakukohde : hakukVastaus.getHakukohteet()) {//rawKoulutus.getHakukohteet()) {
                    SimpleHakukohdeViewModel hakukohdeViewModel = new SimpleHakukohdeViewModel();
                    hakukohdeViewModel.setHakukohdeNimiKoodi(hakukohde.getKoodistoNimi());// getKoodistoNimi());
                    hakukohdeViewModel.setHakukohdeNimi(TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), hakukohde.getNimi()));
                    hakukohdeViewModel.setHakukohdeOid(hakukohde.getOid());
                    hakukohdeViewModel.setHakukohdeTila(hakukohde.getTila().value());
                    hakukohdeViewModel.setHakuStarted(hakukohde.getHakuAlkamisPvm());
                    koulutus.getKoulutuksenHakukohteet().add(hakukohdeViewModel);
                }
            }

            //Add selected data to the comboboxes.
            if (koulutus.getKoulutusohjelmaModel() != null && koulutus.getKoulutusohjelmaModel().getKoodistoUri() != null) {
                koulutus.getKoulutusohjelmat().add(koulutus.getKoulutusohjelmaModel());
            }
            koulutus.getKoulutuskoodit().add(koulutus.getKoulutuskoodiModel());
        } catch (OidCreationException ex) {
            LOG.error("Service call failed.", ex);
            showMainDefaultView();
        }
    }

    public List<ValintakoeViewModel> loadHakukohdeValintaKokees() {
        HakukohdeViewModel hakukohde = getModel().getHakukohde();
        hakukohde.getValintaKokees().clear(); //clear model

        if (hakukohde.getOid() != null) {
            HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi kysely = new HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi();
            kysely.setHakukohteenTunniste(hakukohde.getOid());
            HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi vastaus = tarjontaPublicService.haeHakukohteenValintakokeetHakukohteenTunnisteella(kysely);
            LOG.debug("haeHakukohteenValintakokeetHakukohteenTunnisteella size {}", vastaus != null ? vastaus.getHakukohteenValintaKokeet().size() : null);
            if (vastaus != null && vastaus.getHakukohteenValintaKokeet() != null) {
                for (ValintakoeTyyppi valintakoeTyyppi : vastaus.getHakukohteenValintaKokeet()) {
                    ValintakoeViewModel valintakoeViewModel = ValintakoeConverter.mapDtoToValintakoeViewModel(valintakoeTyyppi);
                    hakukohde.getValintaKokees().add(valintakoeViewModel); //add data to model
                }
            }
        }

        return hakukohde.getValintaKokees();
    }
    private String cachedLiitteetOid = null;
    private List<HakukohdeLiiteViewModel> cachedLiitteet = null;
    private KoulutusSearchSpecificationViewModelToDTOConverter koulutusSearchSpecToDTOConverter = new KoulutusSearchSpecificationViewModelToDTOConverter();

    /**
     * @param forceReload Jos tosi, liitteitä et oteta kakusta.
     */
    public synchronized List<HakukohdeLiiteViewModel> loadHakukohdeLiitteet(boolean forceReload) {
        if (getModel().getHakukohde() == null || getModel().getHakukohde().getOid() == null) {
            return Collections.emptyList();
        }
        if (forceReload) {
            cachedLiitteet = null;
            cachedLiitteetOid = null;
        } else if (cachedLiitteetOid != null
                && cachedLiitteet != null
                && cachedLiitteetOid.equals(getModel().getHakukohde().getOid())) {
            return cachedLiitteet;
        }

        cachedLiitteetOid = getModel().getHakukohde().getOid();
        cachedLiitteet = new ArrayList<HakukohdeLiiteViewModel>();

        HaeHakukohteenLiitteetKyselyTyyppi kysely = new HaeHakukohteenLiitteetKyselyTyyppi();
        kysely.setHakukohdeOid(getModel().getHakukohde().getOid());
        HaeHakukohteenLiitteetVastausTyyppi vastaus = tarjontaPublicService.lueHakukohteenLiitteet(kysely);

        for (HakukohdeLiiteTyyppi liiteTyyppi : vastaus.getHakukohteenLiitteet()) {
            HakukohdeLiiteViewModel hakukohdeLiiteViewModel = HakukohdeLiiteTyyppiToViewModelConverter.convert(liiteTyyppi);

            cachedLiitteet.add(addTableFields(hakukohdeLiiteViewModel));
        }

        return cachedLiitteet;
    }

    private HakukohdeLiiteViewModel addTableFields(HakukohdeLiiteViewModel hakukohdeLiiteViewModel) {

        for (KielikaannosViewModel teksti : hakukohdeLiiteViewModel.getLiitteenSanallinenKuvaus()) {
            if (teksti.getKielikoodi().trim().equalsIgnoreCase(I18N.getLocale().getLanguage().trim())) {
                hakukohdeLiiteViewModel.setLocalizedKuvaus(teksti.getNimi());
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat(LIITE_DATE_PATTERNS);
        if (hakukohdeLiiteViewModel.getToimitettavaMennessa() != null) {
            hakukohdeLiiteViewModel.setToimitusPvmTablePresentation(sdf.format(hakukohdeLiiteViewModel.getToimitettavaMennessa()));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(hakukohdeLiiteViewModel.getOsoiteRivi1());
        sb.append("\n");
        sb.append(hakukohdeLiiteViewModel.getPostinumero());
        sb.append(" ");
        sb.append(hakukohdeLiiteViewModel.getPostitoimiPaikka());
        hakukohdeLiiteViewModel.setToimitusOsoiteConcat(sb.toString());
        return hakukohdeLiiteViewModel;
    }

    public void loadHakukohdeLiiteWithId(String liiteId) {
        LueHakukohteenLiiteTunnisteellaKyselyTyyppi kysely = new LueHakukohteenLiiteTunnisteellaKyselyTyyppi();
        kysely.setHakukohteenLiiteTunniste(liiteId);
        LueHakukohteenLiiteTunnisteellaVastausTyyppi vastaus = tarjontaPublicService.lueHakukohteenLiiteTunnisteella(kysely);
        getModel().setSelectedLiite(HakukohdeLiiteTyyppiToViewModelConverter.convert(vastaus.getHakukohteenLiite()));
    }

    public void showHakukohdeLiiteEditWindow(String liiteId) {
        editHakukohdeView.showHakukohdeEditWindow(liiteId);
    }

    public void showHakukohdeValintakoeEditView(String valintakoeId) {
        editHakukohdeView.showHakukohdeValintakoeEditView(valintakoeId);
    }

    public HakukohdeLiiteViewModel getSelectedHakuliite() {
        return getModel().getSelectedLiite();
    }

    public void loadValintakoeWithId(String id) {
        LueHakukohteenValintakoeTunnisteellaKyselyTyyppi kysely = new LueHakukohteenValintakoeTunnisteellaKyselyTyyppi();
        kysely.setHakukohteenValintakoeTunniste(id);
        LueHakukohteenValintakoeTunnisteellaVastausTyyppi vastaus = tarjontaPublicService.lueHakukohteenValintakoeTunnisteella(kysely);
        getModel().setSelectedValintaKoe(ValintakoeConverter.mapDtoToValintakoeViewModel(vastaus.getHakukohdeValintakoe()));
    }

    public ValintakoeViewModel getSelectedValintakoe() {
        if (getModel().getSelectedValintaKoe() == null) {
            getModel().setSelectedValintaKoe(new ValintakoeViewModel());
        }

        return getModel().getSelectedValintaKoe();
    }

    public void removeValintakoeAikaSelection(ValintakoeAikaViewModel valintakoeAikaViewModel) {
        List<ValintakoeAikaViewModel> ajat = new ArrayList<ValintakoeAikaViewModel>();
        if (getModel().getSelectedValintaKoe().getValintakoeAjat() != null) {
            for (ValintakoeAikaViewModel aika : getModel().getSelectedValintaKoe().getValintakoeAjat()) {
                if (!aika.equals(valintakoeAikaViewModel)) {
                    ajat.add(aika);
                }
            }
            getModel().getSelectedValintaKoe().setValintakoeAjat(ajat);
        }
    }

    public Window getValintakoeTab() {
        if (editHakukohdeView != null) {
            return editHakukohdeView.getValintakoeTab().getWindow();
        } else {
            return null;
        }
    }

    public Window getLiitteetTabImpl() {
        if (editHakukohdeView != null) {
            return editHakukohdeView.getLiitteetTab().getWindow();
        } else {
            return null;
        }
    }

    public void setModelSelectedKoulutusOidAndNames(List<KoulutusOidNameViewModel> koulutusOidAndNames) {

        getModel().setHakukohdeTitleKoulutukses(koulutusOidAndNames);
    }

    private void reloadSelectedKoulutuksesModel(List<String> koulutusOids) {
        KoulutuksetKysely kysely = new KoulutuksetKysely();
        if (koulutusOids != null) {
            kysely.getKoulutusOids().addAll(koulutusOids);
            KoulutuksetVastaus vastaus = tarjontaSearchService.haeKoulutukset(kysely);
            if (vastaus.getKoulutukset() != null && !vastaus.getKoulutukset().isEmpty()) {
                getModel().getSelectedKoulutukset().clear();
                getModel().getSelectedKoulutukset().addAll(vastaus.getKoulutukset());
            }
        }
    }

    /**
     * Show hakukohde edit view.
     *     
* @param koulutusOids
     * @param hakukohdeOid
     */
    public void showHakukohdeEditView(List<String> koulutusOids, String hakukohdeOid, List<KoulutusOidNameViewModel> koulutusOidNameViewModels, String selectedTab) {
        LOG.info("showHakukohdeEditView()");
        getModel().getHakukohde().clearModel();

        if (koulutusOids != null) {
            reloadSelectedKoulutuksesModel(koulutusOids);
        }
        //After the data has been initialized the form is created
        editHakukohdeView = new EditHakukohdeView(hakukohdeOid);
        if (hakukohdeOid == null) {

            if (koulutusOidNameViewModels != null) {
                addKomotoOidsToModel(koulutusOidNameViewModels);
                getModel().getHakukohde().getKoulukses().addAll(koulutusOidNameViewModels);
            }

            if (getModel().getSelectedKoulutukset() != null && !getModel().getSelectedKoulutukset().isEmpty()) {
                KoulutusPerustieto firstKoulutus = getModel().getSelectedKoulutukset().get(0);

                //set tarjoaja
                getTarjoaja().setSelectedResultRowOrganisationOid(firstKoulutus.getTarjoaja().getOid());

                //set koulutusastetyyppi
                getModel().getHakukohde().setKoulutusasteTyyppi(firstKoulutus.getKoulutustyyppi());
            } else if (koulutusOids != null && !koulutusOids.isEmpty()) {
                getTarjoaja().setSelectedResultRowOrganisationOid(getNavigationOrganisation().getOrganisationOid());
            }
        } else {
            editHakukohdeView.loadLiiteTableWithData();
        }

        //If a list of koulutusOids is provided they are set in the model
        //These koulutus objects will be published in the created hakukohde
        if (koulutusOids != null) {
            setKomotoOids(koulutusOids);
        }
        //if a hakukohdeOid is provided the hakukohde is read from the database
        if (hakukohdeOid != null) {
            refreshHakukohdeUIModel(hakukohdeOid);
            setKomotoOids(getModel().getHakukohde().getKomotoOids());
            reloadSelectedKoulutuksesModel(getModel().getHakukohde().getKomotoOids());
        }

        getRootView().changeView(editHakukohdeView);
        //If selected tab is given set it to selected
        if (selectedTab != null && selectedTab.trim().equalsIgnoreCase(TarjontaPresenter.VALINTAKOE_TAB_SELECT)) {
            editHakukohdeView.setValintakokeetTabSelected();
        } else if (selectedTab != null && selectedTab.trim().equalsIgnoreCase(TarjontaPresenter.LIITTEET_TAB_SELECT)) {
            editHakukohdeView.setLiitteetTabSelected();

        }
    }

    /**
     * Lue hakukohde tietovarastosta ja päivitä ui model.
     */
    public void refreshHakukohdeUIModel(String hakukohdeOid) {
        Preconditions.checkNotNull(hakukohdeOid, "Hakukohde OID cannot be null.");
        LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
        kysely.setOid(hakukohdeOid);
        HakukohdeTyyppi hakukohde = tarjontaPublicService
                .lueHakukohde(kysely).getHakukohde();
        refreshHakukohdeUIModel(hakukohde);
    }

    /**
     * Päivitä ui model.
     */
    private void refreshHakukohdeUIModel(HakukohdeTyyppi hakukohdeTyyppi) {
        cachedLiitteet = null;
        cachedLiitteetOid = null;
        HakukohdeViewModel hakukohdeVM = getModel().getHakukohde();

        hakukohdeToDTOConverter.convertDTOToHakukohdeViewMode(hakukohdeVM, hakukohdeTyyppi);
        setKomotoOids(hakukohdeVM.getKomotoOids());

        if (hakukohdeVM.getKoulukses() == null || hakukohdeVM.getKoulukses().isEmpty()) {
            hakukohdeVM.setKoulukses(getHakukohdeKoulutukses(hakukohdeVM));
        }

        switch (hakukohdeTyyppi.getHakukohteenKoulutusaste()) {
            case LUKIOKOULUTUS:
                if (hakuKohdePerustiedotView != null) {
                    //update form data binding for oppiaine UI models
                    hakuKohdePerustiedotView.refreshOppiaineet();
                }
                break;
        }
    }

    private void addKomotoOidsToModel(List<KoulutusOidNameViewModel> koulutukses) {
        for (KoulutusOidNameViewModel koulutus : koulutukses) {
            getModel().getHakukohde().getKomotoOids().add(koulutus.getKoulutusOid());
        }
    }

    /**
     * TODO rename.
     *
     *
     * @return
     */
    public String resolveHakukohdeKoodistonimiFields() {
        final HakukohdeViewModel model = getModel().getHakukohde();
        if (model.getKoulutusasteTyyppi().equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS)) {
            return model.getEditedHakukohdeNimi();
        }
        //return uiHelper.getHakukohdeHakukentta(model.getHakuViewModel().getHakuOid(), I18N.getLocale(), model.getHakukohdeNimi()) + ", " + tilaToLangStr(model.getTila());
        return uiHelper.getHakukohdeHakukentta(model.getKomotoOids().get(0), I18N.getLocale(), model.getHakukohdeNimi()) + ", " + tilaToLangStr(model.getTila());
    }

    public ListHakukohdeView getHakukohdeListView() {
        return _hakukohdeListView;
    }

    public void setHakukohdeListView(ListHakukohdeView hakukohdeListView) {
        this._hakukohdeListView = hakukohdeListView;
    }

    private void restrictHakukohdeKyselyByKoulutusaste(HakukohteetKysely kysely) {

        //Restrict korkeakoulu hakukohdes because they cannot be shown on Vaadin-UI
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.LUKIOKOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS);

    }

    public Map<String, List<HakukohdePerustieto>> getHakukohdeDataSource() {
        List<HakukohdePerustieto> hakukohdetulos = Lists.newArrayList();
        Map<String, List<HakukohdePerustieto>> map = new HashMap<String, List<HakukohdePerustieto>>();
        try {
            // Fetching komotos matching currently specified criteria (currently
            // selected organisaatio and written text in search box)
            HakukohteetKysely kysely = koulutusSearchSpecToDTOConverter
                    .convertViewModelToHakukohdeDTO(getModel().getSearchSpec());
            restrictHakukohdeKyselyByKoulutusaste(kysely);
            hakukohdetulos.addAll(tarjontaSearchService.haeHakukohteet(kysely).getHakukohteet());
        } catch (Exception ex) {
            LOG.error("Error in finding hakukokohteet ", ex);
            throw new RuntimeException(ex);
        }
        this.searchResultsView.setResultSizeForHakukohdeTab(hakukohdetulos.size());
        for (HakukohdePerustieto curHk : hakukohdetulos) {
            String hkKey = TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), curHk.getTarjoajaNimi()) + "," + curHk.getTarjoajaOid();
            if (!map.containsKey(hkKey)) {
                List<HakukohdePerustieto> hakukohteetM = Lists.newArrayList();
                hakukohteetM.add(curHk);
                map.put(hkKey, hakukohteetM);
            } else {
                map.get(hkKey).add(curHk);
            }
        }
        TreeMap<String, List<HakukohdePerustieto>> sortedMap = new TreeMap<String, List<HakukohdePerustieto>>(map);

        return sortedMap;
    }

    /**
     * Gets the currently selected hakukohde objects.
     *     
* @return
     */
    public List<HakukohdePerustieto> getSelectedhakukohteet() {
        return getModel().getSelectedhakukohteet();
    }

    public List<KoulutusOidNameViewModel> getHakukohdeKoulutukses(String hakukohdeOid) {
        KoulutuksetKysely kyselyTyyppi = new KoulutuksetKysely();
        kyselyTyyppi.getHakukohdeOids().add(hakukohdeOid);
        KoulutuksetVastaus vastaus = tarjontaSearchService.haeKoulutukset(kyselyTyyppi);
        if (vastaus.getKoulutukset() != null) {
            return convertKoulutusToNameOidViewModel(vastaus.getKoulutukset());
        } else {
            return null;
        }
    }

    public void loadHakukohdeHakuPvm() {
        ListaaHakuTyyppi haku = new ListaaHakuTyyppi();
        haku.setHakuOid(getModel().getHakukohde().getHakuViewModel().getHakuOid());
        ListHakuVastausTyyppi vastaus = tarjontaPublicService.listHaku(haku);
        if (vastaus != null && vastaus.getResponse() != null) {
            HakuTyyppi hakuTyyppi = vastaus.getResponse().get(0);
            SisaisetHakuAjat hakuaika = hakuTyyppi.getSisaisetHakuajat().get(0);
            getModel().getHakukohde().getHakuViewModel().setAlkamisPvm(hakuaika.getSisaisenHaunAlkamisPvm());
            getModel().getHakukohde().getHakuViewModel().setPaattymisPvm(hakuaika.getSisaisenHaunPaattymisPvm());
        }
    }
    
    /** 
     * Saako nykyinen käyttäjä muokata hakukohdetta, tarkistaa permissiot ja parametrit
     * 
     * @return
     */
    public boolean isHakukohdeEditableForCurrentUser() {

        //for oph crud user, always editable
        if (getPermission().userIsOphCrud()) {
            return true;
        }

        //TODO tarvitaanko tätä?
        loadHakukohdeHakuPvm();
        
        boolean hasPermission=true;

        //hae koulutukset jotta tiedetään tarjoaja(t)
        for(String komotoOid: getModel().getHakukohde().getKomotoOids()){
            KoulutuksetVastaus kv = findKoulutusByKoulutusOid(komotoOid);
            for(KoulutusPerustieto koulutus: kv.getKoulutukset()){
                String tarjoajaOid = koulutus.getTarjoaja().getOid();
                //pitää olla oikeuis kaikkiin!
                hasPermission = hasPermission && getPermission().userCanUpdateHakukohde(OrganisaatioContext.getContext(tarjoajaOid));
            }
        }

        final String hakuOid = getModel().getHakukohde().getHakuViewModel().getHakuOid();
        
        final boolean parameterAllows = parameterServices.parameterCanEditHakukohde(hakuOid);
        
        return hasPermission && parameterAllows;
    }

    /** 
     * Saako nykyinen käyttäjä muokata hakukohdetta, tarkistaa permissiot ja parametrit
     * 
     * @return
     */
    public boolean isHakukohdeEditableForCurrentUser(final String hakukohdeOid) {

        final LueHakukohdeVastausTyyppi hakukohde = tarjontaPublicService.lueHakukohde(new LueHakukohdeKyselyTyyppi(hakukohdeOid));
        
        final String hakuOid = hakukohde.getHakukohde().getHakukohteenHakuOid();

        //for oph crud user, always editable
        if (getPermission().userIsOphCrud()) {
            return true;
        }
        
        boolean hasPermission=true;

        //hae koulutukset jotta tiedetään tarjoaja(t)
        for(KoulutusKoosteTyyppi koulutusKoosteTyyppi: hakukohde.getHakukohde().getHakukohdeKoulutukses()){
            KoulutuksetVastaus kv = findKoulutusByKoulutusOid(koulutusKoosteTyyppi.getKomotoOid());
            for(KoulutusPerustieto koulutus: kv.getKoulutukset()){
                String tarjoajaOid = koulutus.getTarjoaja().getOid();
                //pitää olla oikeuis kaikkiin!
                hasPermission = hasPermission && getPermission().userCanUpdateHakukohde(OrganisaatioContext.getContext(tarjoajaOid));
            }
        }

        
        final boolean parameterAllows = parameterServices.parameterCanEditHakukohde(hakuOid);
        
        return hasPermission && parameterAllows;
    }

    
    
    public void removeSelectedHakukohde() {
        getModel().getSelectedhakukohteet().clear();
        removeHakukohde(getModel().getHakukohde().getOid());
        getRootView().showMainView();
    }

    /**
     * Removes the selected hakukohde objects from the database.
     */
    public void removeSelectedHakukohteet() {

        int removalLaskuri = 0;
        String errorNotes = "";
        for (HakukohdePerustieto curHakukohde : getModel().getSelectedhakukohteet()) {
            String hakukohdeNimi = TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), curHakukohde.getNimi());
            try {
                final OrganisaatioContext context = OrganisaatioContext.getContext(curHakukohde.getTarjoajaOid());
                TarjontaTila tila = curHakukohde.getTila();

                if ((tila.equals(TarjontaTila.VALMIS) || tila.equals(TarjontaTila.LUONNOS))
                        && getPermission().userCanDeleteHakukohde(context)) {
                    HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
                    hakukohde.setOid(curHakukohde.getOid());
                    tarjontaAdminService.poistaHakukohde(hakukohde);
                    ++removalLaskuri;
                } else {
                    errorNotes += I18N.getMessage("notification.error.hakukohde.notRemovable", hakukohdeNimi) + "<br/>";
                }
            } catch (Throwable e) {

                if (e.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException")) {
                    errorNotes += I18N.getMessage("notification.error.hakukohde.used.multiple", hakukohdeNimi) + "<br/>";
                } else {
                    LOG.error(e.getMessage());
                }
            }
        }

        String notificationMessage = "<br />" + I18N.getMessage("notification.deleted.hakukohteet", removalLaskuri) + "<br />" + errorNotes;
        getModel().getSelectedhakukohteet().clear();

        //TODO korvaa reload
        getHakukohdeListView().reload();

        getRootView().getSearchResultsView().getHakukohdeList().getWindow().showNotification(I18N.getMessage("notification.deleted.hakukohteet.title"),
                notificationMessage,
                Window.Notification.TYPE_HUMANIZED_MESSAGE);
        getRootView().getSearchResultsView().getHakukohdeList().closeRemoveDialog();
    }

    public void removeHakukohde(String oid) {
        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        hakukohde.setOid(oid);
        try {
            tarjontaAdminService.poistaHakukohde(hakukohde);
            showNotification(UserNotification.DELETE_SUCCESS);
        } catch (Exception exp) {
            if (exp.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException")) {
                getHakukohdeListView().showErrorMessage(I18N.getMessage("notification.error.hakukohde.used"));
            } else {
                showNotification(UserNotification.SAVE_FAILED);
            }
        }
    }

    public void showRemoveKoulutusFromHakukohdeDialog(KoulutusOidNameViewModel koulutusOidNameViewModel) {

        hakukohdeView.showKoulutusRemovalDialog(koulutusOidNameViewModel);
    }

    /**
     * Gets the currently selected koulutus objects.
     *     
* @return
     */
    public List<KoulutusPerustieto> getSelectedKoulutukset() {
        return getModel().getSelectedKoulutukset();
    }

    /**
     * Removes the selected koulutus objects from the database.
     */
    public void removeSelectedKoulutukset() {

        int removalLaskuri = 0;
        String errorNotes = "";
        for (KoulutusPerustieto curKoulutus : getModel().getSelectedKoulutukset()) {
            String koulutusNimiUri = curKoulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)
                    ? curKoulutus.getKoulutuskoodi().getUri()
                    : curKoulutus.getKoulutusohjelmakoodi().getUri();
            try {
                final OrganisaatioContext context = OrganisaatioContext.getContext(curKoulutus.getTarjoaja().getOid());
                TarjontaTila tila = curKoulutus.getTila();

                if ((tila.equals(TarjontaTila.VALMIS) || tila.equals(TarjontaTila.LUONNOS))
                        && getPermission().userCanDeleteKoulutus(context)) {
                    tarjontaAdminService.poistaKoulutus(curKoulutus.getKoulutusmoduuliToteutus());
                    sendEvent(KoulutusContainerEvent.delete(curKoulutus.getKomotoOid()));
                    ++removalLaskuri;
                } else {
                    errorNotes += I18N.getMessage("notification.error.koulutus.notRemovable", uiHelper.getKoodiNimi(koulutusNimiUri)) + "<br/>";
                }
            } catch (Throwable e) {

                if (e.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException")) {
                    errorNotes += I18N.getMessage("notification.error.koulutus.used.multiple", uiHelper.getKoodiNimi(koulutusNimiUri)) + "<br/>";
                } else {
                    LOG.error(e.getMessage());
                }
            }
        }

        String notificationMessage = "<br />" + I18N.getMessage("notification.deleted.koulutukset", removalLaskuri) + "<br />" + errorNotes;
        getModel().getSelectedKoulutukset().clear();

        getRootView().getListKoulutusView().getWindow().showNotification(I18N.getMessage("notification.deleted.koulutukset.title"),
                notificationMessage,
                Window.Notification.TYPE_HUMANIZED_MESSAGE);
        getRootView().getListKoulutusView().closeKoulutusDialog();
    }

    /**
     * Saves koulutus/tukinto, other synonyms: LOI, KOMOTO.
     *     
* @param tila (save state)
     * @throws ExceptionMessage
     */
    public void saveKoulutus(SaveButtonState tila, KoulutusActiveTab activeTab) throws OidCreationException {
        KoulutusToisenAsteenPerustiedotViewModel koulutusModel = getModel().getKoulutusPerustiedotModel();

        String oid = null;
        if (koulutusModel.getOid() != null && koulutusModel.getOid().equalsIgnoreCase("-1")) {
            koulutusModel.setOid(null);
        }
        koulutusModel.setViimeisinPaivittajaOid(userContext.getUserOid());
        if (koulutusModel.isLoaded()) {
            //update KOMOTO
            OrganisationOidNamePair selectedOrganisation = getModel().getTarjoajaModel().getSelectedOrganisation();
            PaivitaKoulutusTyyppi paivita = koulutusToDTOConverter.createPaivitaKoulutusTyyppi(getModel(), selectedOrganisation, koulutusModel.getOid());
            paivita.setTila(tila.toTarjontaTila(koulutusModel.getTila()));

            koulutusToDTOConverter.validateSaveData(paivita, koulutusModel);
            tarjontaAdminService.paivitaKoulutus(paivita);
            oid = paivita.getOid();
            sendEvent(KoulutusContainerEvent.update(oid));
        } else {
            for (OrganisationOidNamePair pair : getTarjoaja().getOrganisationOidNamePairs()) {
                oid = persistKoulutus(koulutusModel, pair, tila);
                sendEvent(KoulutusContainerEvent.create(oid));
            }
        }

        //reload koulutus (optimistic locking)
        showKoulutustEditView(oid, activeTab);

// this.editKoulutusView.enableLisatiedotTab();
// this.lisatiedotView.getEditKoulutusLisatiedotForm().reBuildTabsheet();
    }

    private String persistKoulutus(KoulutusToisenAsteenPerustiedotViewModel koulutusModel, OrganisationOidNamePair pair, SaveButtonState tila) throws OidCreationException {
        //persist new KOMO and KOMOTO
        final KoodiModel koulutuksenTyyppi = koulutusModel.getKoulutuksenTyyppi();

        LisaaKoulutusTyyppi lisaa = koulutusToDTOConverter.createLisaaKoulutusTyyppi(getModel(), pair);
        lisaa.setTila(tila.toTarjontaTila(koulutusModel.getTila()));
        koulutusToDTOConverter.validateSaveData(lisaa, koulutusModel);
        checkKoulutusmoduuli();
        //OVT-6477 valmentava ja kuntouttava saa olla useita
        //OVT-6676 vapaan sivistystyön koulutuksia saa olla useita
        if (lisaa.getKoulutustyyppi() == KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS || lisaa.getKoulutustyyppi() == KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS || checkExistingKomoto(lisaa)) {

            tarjontaAdminService.lisaaKoulutus(lisaa);
            koulutusModel.setDocumentStatus(DocumentStatus.SAVED);
            koulutusModel.setOid(lisaa.getOid());

            koulutusModel.setKoulutuksenTyyppi(koulutuksenTyyppi);
            getModel().getKoulutusPerustiedotModel().setKoulutuksenTyyppi(koulutuksenTyyppi);
            return lisaa.getOid();
        } else {

            LOG.debug("Unable to add koulutus because of the duplicate");
            throw new OidCreationException("EditKoulutusPerustiedotYhteystietoView.koulutusExistsMessage");
        }
    }

    private boolean checkExistingKomoto(LisaaKoulutusTyyppi lisaaTyyppi) {
        TarkistaKoulutusKopiointiTyyppi kysely = new TarkistaKoulutusKopiointiTyyppi();
        kysely.setKoulutusAlkamisPvm(lisaaTyyppi.getKoulutuksenAlkamisPaiva());
        kysely.setKoulutusLuokitusKoodi(lisaaTyyppi.getKoulutusKoodi().getUri());
        kysely.setKoulutusohjelmaKoodi(lisaaTyyppi.getKoulutusohjelmaKoodi().getUri());
        kysely.setPohjakoulutus(lisaaTyyppi.getPohjakoulutusvaatimus().getUri());
        kysely.getOpetuskielis().addAll(getUrisFromKoodistoTyyppis(lisaaTyyppi.getOpetuskieli()));
        kysely.getKoulutuslajis().addAll(getUrisFromKoodistoTyyppis(lisaaTyyppi.getKoulutuslaji()));
        kysely.setTarjoajaOid(lisaaTyyppi.getTarjoaja());

        return tarjontaAdminService.tarkistaKoulutuksenKopiointi(kysely);

    }

    private List<String> getUrisFromKoodistoTyyppis(List<KoodistoKoodiTyyppi> koodistoKoodis) {
        List<String> uris = new ArrayList<String>();

        for (KoodistoKoodiTyyppi koodi : koodistoKoodis) {
            uris.add(koodi.getUri());
        }

        return uris;
    }

    /**
     * Get UI model.
     *     
* @return
     */
    public TarjontaModel getModel() {
        if (_model == null) {
            _model = new TarjontaModel();
        }
        return _model;
    }

    public void setRootView(TarjontaRootView rootView) {
        _rootView = rootView;
    }

    public TarjontaRootView getRootView() {
        return _rootView;
    }

    /**
     * @return application identifier.
     */
    public String getIdentifier() {
        return getModel().getIdentifier();
    }

    public void getReloadKoulutusListData() {
        getRootView().getListKoulutusView().reload();
    }

    /**
     *
     * Reload koulutus and hakukohde views when an user hasn't selected root
     * organization OID. If the root organization is selected, then all data
     * items are cleared from the views.
     *     
*/
    public void reloadMainView() {
        reloadMainView(false);
    }

    /**
     *
     * Reload koulutus and hakukohde views when an user hasn't selected root
     * organization OID. If the root organization is selected, then all data
     * items are cleared from the views. There is also an option to force reload
     * to the views.
     *     
* @param forceReload
     */
    public void reloadMainView(final boolean forceReload) {
        //Main view will be reloaded only when an user has selected other than the root organisation

        if (forceReload || (getNavigationOrganisation().getOrganisationOid() != null && !getModel().isSelectedRootOrganisaatio())) {
            LOG.debug("not root, main view reloaded {} {}", getNavigationOrganisation().getOrganisationOid(), getModel().isSelectedRootOrganisaatio());
            getReloadKoulutusListData();
            getHakukohdeListView().reload();
        } else {
            getRootView().getListKoulutusView().clearAllDataItems();
            getHakukohdeListView().clearAllDataItems();
            this.searchResultsView.setResultSizeForKoulutusTab(0);
            this.searchResultsView.setResultSizeForHakukohdeTab(0);
        }
        getModel().getSelectedKoulutukset().clear();
        getModel().getSelectedhakukohteet().clear();
        this.searchResultsView.getKoulutusList().toggleCreateHakukohdeB(null, false);
    }

    private void restrictKyselyWithKoulutusTyypit(KoulutuksetKysely kysely) {

        //Restrict korkeakoulutus from these search results
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.LUKIOKOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS);
        kysely.getKoulutusasteTyypit().add(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS);

    }

    /**
     * Retrieves the koulutus objects for ListKoulutusView.
     *     
* @return the koulutus objects
     */
    public Map<String, List<KoulutusPerustieto>> getKoulutusDataSource() {
        Map<String, List<KoulutusPerustieto>> map = new HashMap<String, List<KoulutusPerustieto>>();
        try {
            // Fetching komotos matching currently specified criteria (currently
            // selected organisaatio and written text in search box)
            KoulutuksetKysely kysely = koulutusSearchSpecToDTOConverter
                    .convertViewModelToKoulutusDTO(getModel().getSearchSpec());
            restrictKyselyWithKoulutusTyypit(kysely);
            getModel().setKoulutukset(tarjontaSearchService.haeKoulutukset(kysely).getKoulutukset());

        } catch (Exception ex) {
            LOG.error("Error in finding koulutukset", ex);
            getModel().setKoulutukset(new ArrayList<KoulutusPerustieto>());
            throw new RuntimeException(ex);
        }

        this.searchResultsView.setResultSizeForKoulutusTab(getModel().getKoulutukset().size());
        // Creating the datasource model
        for (KoulutusPerustieto curKoulutus : getModel().getKoulutukset()) {
            String koulutusKey = TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), curKoulutus.getTarjoaja().getNimi()) + "," + curKoulutus.getTarjoaja().getOid();
            if (!map.containsKey(koulutusKey)) {
                //LOG.info("Adding a new key to the map: " + koulutusKey);
                List<KoulutusPerustieto> koulutuksetM = new ArrayList<KoulutusPerustieto>();
                koulutuksetM.add(curKoulutus);
                map.put(koulutusKey, koulutuksetM);
            } else {
                map.get(koulutusKey).add(curKoulutus);
            }
        }

        TreeMap<String, List<KoulutusPerustieto>> sortedMap = new TreeMap<String, List<KoulutusPerustieto>>(map);

        return sortedMap;
    }

    /**
     * Gets the oids of the selectd koulutuses.
     *     
* @return the oids
     */
    public List<String> getSelectedKoulutusOids() {
        List<String> kOids = new ArrayList<String>();

        if (getModel().getSelectedKoulutukset() != null || !getModel().getSelectedKoulutukset().isEmpty()) {
            for (KoulutusPerustieto curKoul : getModel().getSelectedKoulutukset()) {
                if (curKoul != null && curKoul != null) {
                    kOids.add(curKoul.getKoulutusmoduuliToteutus());
                }
            }
        }
        return kOids;
    }

    public List<KoulutusOidNameViewModel> getSelectedKoulutusOidNameViewModels() {
        List<KoulutusOidNameViewModel> koulutukses = new ArrayList<KoulutusOidNameViewModel>();

        if (getModel().getSelectedKoulutukset() != null || !getModel().getSelectedKoulutukset().isEmpty()) {
            koulutukses = convertKoulutusToNameOidViewModel(getModel().getSelectedKoulutukset());
        }

        return koulutukses;
    }

    public void showRemoveHakukohdeFromKoulutusDialog(String hakukohdeOid, String hakukohdeNimi) {
        showKoulutusView.showHakukohdeRemovalDialog(hakukohdeOid, hakukohdeNimi);
    }

    /**
     * Removal of a komoto object.
     *     
* @param koulutusOid
     */
    public boolean removeKoulutus(String koulutusOid) {
        boolean removeSuccess = false;
        try {
            tarjontaAdminService.poistaKoulutus(koulutusOid);
            showNotification(UserNotification.DELETE_SUCCESS);
            removeSuccess = true;
        } catch (Exception ex) {
            if (ex.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException")) {
                showNotification(UserNotification.KOULUTUS_REMOVAL_FAILED);
            } else {
                showNotification(UserNotification.SAVE_FAILED);
            }
        }
        return removeSuccess;
    }

    /**
     * Shows the koulutus objects for a hakukohde in the ListHakukohdeView.
     *     
* @param hakukohde
     */
    public void showKoulutuksetForHakukohde(HakukohdePerustieto hakukohde) {

        KoulutuksetKysely kysely = new KoulutuksetKysely();
        //kysely.getHakukohdeOids().add(hakukohde);
        kysely.getHakukohdeOids().add(hakukohde.getOid());

        KoulutuksetVastaus vastaus = tarjontaSearchService.haeKoulutukset(kysely);

        this._hakukohdeListView.showKoulutuksetForHakukohde(vastaus.getKoulutukset(), hakukohde);//appendKoulutuksetToList(hakukohde);
    }

    private void addOrganisaatioNameValuePair(String oid, String name) {
        getTarjoaja().getOrganisationOidNamePairs().clear();
        getTarjoaja().setSelectedOrganisation(new OrganisationOidNamePair(oid, name));
    }

    /**
     * Selects the organisaatio in tarjonta, by setting the organisaatio name in
     * breadcrumb and setting the organisaatioOid and organisaatioNimi in
     * tarjonta model. Enables the create koulutus button in koulutus list view.
     *     
* @param organisaatioOid - the organisaatio oid to select
     * @param organisaatioName - the organisaatio name to select
     */
    public void selectOrganisaatio(String organisaatioOid, String organisaatioName, boolean active) {
        NavigationModel navigaatioModel = getNavigationOrganisation();
        navigaatioModel.setOrganisation(organisaatioOid, organisaatioName);

        addOrganisaatioNameValuePair(organisaatioOid, organisaatioName);
        getRootView().getBreadcrumbsView().setOrganisaatio(organisaatioName);

        // Descendant organisation oids to limit the search
        getModel().getSearchSpec().getOrganisaatioOids().clear();
        //getModel().getSearchSpec().getOrganisaatioOids().addAll(findAllChilrenOidsByParentOid(organisaatioOid));
        getModel().getSearchSpec().getOrganisaatioOids().add(organisaatioOid);

        //Clearing the selected hakukohde and koulutus objects
        getModel().getSelectedhakukohteet().clear();
        getModel().getSelectedKoulutukset().clear();

        // Updating koulutuslista to show only komotos with tarjoaja matching
        // the selected org or one of its descendants
        reloadMainView(false);
        getRootView().getListKoulutusView().toggleCreateKoulutusB(organisaatioOid, active);
    }

    public void unSelectOrganisaatio() {
        //TODO: there is no real breadcrumb, so the parent is always root level (OPH)...
        getNavigationOrganisation().setOrganisation(getModel().getRootOrganisaatioOid(), NAME_OPH);
        getRootView().getBreadcrumbsView().setOrganisaatio(NAME_OPH);

        getRootView().getOrganisaatiohakuView().clearTreeSelection();

        //Clearing the selected hakukohde and koulutus objects
        getModel().getSelectedhakukohteet().clear();
        getModel().getSelectedKoulutukset().clear();

        getModel().getSearchSpec().setOrganisaatioOids(new ArrayList<String>());

        reloadMainView();
        getRootView().getListKoulutusView().toggleCreateKoulutusB(getModel().getRootOrganisaatioOid(), false);
        getRootView().getListKoulutusView().toggleCreateHakukohdeB(getModel().getRootOrganisaatioOid(), false);
    }

    /**
     * Gets koulutus by its oid.
     *     
* @param komotoOid - the koulutus oid for which the name is returned
     * @return the koulutus
     */
    public LueKoulutusVastausTyyppi getKoulutusByOid(String komotoOid) {
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");
        LueKoulutusKyselyTyyppi kysely = new LueKoulutusKyselyTyyppi();
        kysely.setOid(komotoOid);
        LOG.info("getKoulutusByOId");
        LueKoulutusVastausTyyppi vastaus = tarjontaPublicService.lueKoulutus(kysely);
        LOG.info("getKoulutusByOId, done.");

        return vastaus;
    }

    /**
     *
     *
     */
    public void checkKoulutusmoduuli() {
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();

        HaeKoulutusmoduulitKyselyTyyppi kysely
                = Koulutus2asteConverter.mapToHaeKoulutusmoduulitKyselyTyyppi(
                        KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS,
                        model.getKoulutuskoodiModel(),
                        model.getKoulutusohjelmaModel());

        HaeKoulutusmoduulitVastausTyyppi vastaus = tarjontaPublicService.haeKoulutusmoduulit(kysely);

        if (vastaus.getKoulutusmoduuliTulos().isEmpty()) {
            //No KOMO, insert new KOMO
            LOG.error("Tarjonta do not have requested komo! "
                    + "tutkinto : '" + kysely.getKoulutuskoodiUri()
                    + "', koulutusohjelma : '" + kysely.getKoulutusohjelmakoodiUri() + "'");
        } else {
            //KOMO found
            model.setKoulutusmoduuliOid(vastaus.getKoulutusmoduuliTulos().get(0).getKoulutusmoduuli().getOid());
        }
    }

    /*
     * More detailed information of selected 'koulutusluokitus'.
     */
    @SuppressWarnings("unchecked")
    public void loadKoulutuskoodit() {
        HaeKaikkiKoulutusmoduulitKyselyTyyppi kysely = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
        KoodiModel koulutuksenTyyppi = getModel().getKoulutusPerustiedotModel().getKoulutuksenTyyppi();

        if (koulutuksenTyyppi == null
                || (koulutuksenTyyppi.getKoodi().equals(Koulutustyyppi.TOINEN_ASTE_AMMATILLINEN_KOULUTUS.getKoulutustyyppiUri())
                || koulutuksenTyyppi.getKoodi().equals(Koulutustyyppi.TOINEN_ASTE_AMMATILLINEN_ERITYISKOULUTUS.getKoulutustyyppiUri()))) {
            kysely.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        } else if (koulutuksenTyyppi != null
                && koulutuksenTyyppi.getKoodi().equals(Koulutustyyppi.TOINEN_ASTE_VALMENTAVA_KOULUTUS.getKoulutustyyppiUri())) {
            kysely.setKoulutustyyppi(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS);
        } else if (koulutuksenTyyppi != null
                && koulutuksenTyyppi.getKoodi().equals(Koulutustyyppi.AMMATILLISEEN_OHJAAVA_KOULUTUS.getKoulutustyyppiUri())) {
            kysely.setKoulutustyyppi(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS);
        } else if (koulutuksenTyyppi != null
                && koulutuksenTyyppi.getKoodi().equals(Koulutustyyppi.MAMU_AMMATILLISEEN_OHJAAVA_KOULUTUS.getKoulutustyyppiUri())) {
            kysely.setKoulutustyyppi(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS);
        } else if (koulutuksenTyyppi != null
                && koulutuksenTyyppi.getKoodi().equals(Koulutustyyppi.MAMU_LUKIOON_OHJAAVA_KOULUTUS.getKoulutustyyppiUri())) {
            kysely.setKoulutustyyppi(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS);
        } else if (koulutuksenTyyppi != null
                && koulutuksenTyyppi.getKoodi().equals(Koulutustyyppi.PERUSOPETUKSEN_LISAOPETUS.getKoulutustyyppiUri())) {
            kysely.setKoulutustyyppi(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS);
        } else if (koulutuksenTyyppi != null
                && koulutuksenTyyppi.getKoodi().equals(Koulutustyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS.getKoulutustyyppiUri())) {
            kysely.setKoulutustyyppi(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS);
        }

        //TODO: fix this
        //kysely.getOppilaitostyyppiUris().addAll(getOppilaitostyyppiUris());
        HaeKaikkiKoulutusmoduulitVastausTyyppi haeKaikkiKoulutusmoduulit = tarjontaPublicService.haeKaikkiKoulutusmoduulit(kysely);
        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = haeKaikkiKoulutusmoduulit.getKoulutusmoduuliTulos();

        Set<String> uris = new HashSet<String>();
        List<KoulutusmoduuliKoosteTyyppi> komos = new ArrayList<KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            komos.add(tulos.getKoulutusmoduuli());
            uris.add(tulos.getKoulutusmoduuli().getKoulutuskoodiUri());
        }
        LOG.debug("KOMOs found {}", komos.size());

        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        model.setKomos(komos);
        model.createCacheKomos(); //cache komos to map object

        //koodisto service search result remapped to UI model objects.
        List<KoulutuskoodiModel> listaaKoulutuskoodit = new ArrayList<KoulutuskoodiModel>();
        if (!uris.isEmpty()) {
            listaaKoulutuskoodit = kolutusKoodistoConverter.listaaKoulutukses(uris, I18N.getLocale());
            Collections.sort(listaaKoulutuskoodit, new BeanComparator("nimi"));
        }

        model.getKoulutuskoodit().clear();
        model.getKoulutuskoodit().addAll(listaaKoulutuskoodit);
    }

    /**
     * Returns the list of (koodisto) oppilaitostyyppi uri's matching the
     * provided organisation
     */
    public List<String> getOppilaitostyyppiUris(String orgOid) {
        final String organisaatioOid = orgOid;
        OrganisaatioPerustieto selectedOrg = organisaatioSearchService.findByOidSet(Sets.newHashSet(organisaatioOid)).get(0);

        if (selectedOrg == null) {
            throw new RuntimeException("No organisation found by OID " + organisaatioOid + ".");
        }

        List<OrganisaatioTyyppi> tyypit = selectedOrg.getOrganisaatiotyypit();
        List<String> olTyyppiUris = new ArrayList<String>();
        //If the types of the organisaatio contains oppilaitos, its oppilaitostyyppi is appended to the list of oppilaitostyyppiuris
        if (tyypit.contains(OrganisaatioTyyppi.OPPILAITOS)) {
            olTyyppiUris.add(selectedOrg.getOppilaitostyyppi());
        }
        //If the types of the organisaatio contain koulutustoimija the oppilaitostyyppis of its children are appended to the
        //list of oppilaitostyyppiuris
        if (tyypit.contains(OrganisaatioTyyppi.KOULUTUSTOIMIJA)) {
            olTyyppiUris.addAll(getChildOrgOlTyyppis(selectedOrg));

            //If the types of the organisaatio contain opetuspiste the oppilaitostyyppi of its parent organisaatio is appended to the list of
            //oppilaitostyyppiuris
        } else if (tyypit.contains(OrganisaatioTyyppi.TOIMIPISTE)
                && selectedOrg.getParentOid() != null) {
            addParentOlTyyppi(selectedOrg, olTyyppiUris);
        }

        LOG.debug("TyyppiUris : {}", olTyyppiUris);
        LOG.debug("olTyyppiUris size: {}", olTyyppiUris.size());
        return olTyyppiUris;
    }

    /**
     * Returns the list of (koodisto) oppilaitostyyppi uri's matching the
     * currently selected organisaatio.
     */
    public List<String> getOppilaitostyyppiUris() {
        final String organisaatioOid = this.getNavigationOrganisation().getOrganisationOid();
        return getOppilaitostyyppiUris(organisaatioOid);
    }

    /*
     * Adds the oppilaitostyypi of the parent of the organisaatio given as first parameter
     * to the list of oppilaitostyyppis given as second parameters.
     */
    private void addParentOlTyyppi(OrganisaatioPerustieto selectedOrg, List<String> olTyyppiUris) {
        String olTyyppi = getOrganisaatioOlTyyppi(selectedOrg.getParentOid());
        if (olTyyppi != null) {
            olTyyppiUris.add(olTyyppi);
        }
    }

    /*
     * Gets the oppilaitostyyppi of the organisaatio the oid of which is given as parameters.
     */
    private String getOrganisaatioOlTyyppi(String oid) {
        final List<OrganisaatioPerustieto> perusList = this.organisaatioSearchService.findByOidSet(Sets.newHashSet(oid));
        final OrganisaatioPerustieto perustieto = perusList.get(0);
        //OrganisaatioDTO organisaatio = this.getOrganisaatioService().findByOid(oid);
        if (perustieto.getOrganisaatiotyypit().contains(OrganisaatioTyyppi.OPPILAITOS)) {
            return perustieto.getOppilaitostyyppi();
        } else if (perustieto.getOrganisaatiotyypit().contains(OrganisaatioTyyppi.TOIMIPISTE)) {
            return getOrganisaatioOlTyyppi(perustieto.getParentOid());
        }
        return null;
    }

    /*
     * Gets the list of oppilaitostyyppi uris that match the children of the organisaatio given as parameter.
     */
    private List<String> getChildOrgOlTyyppis(OrganisaatioPerustieto selectedOrg) {
        List<String> childOlTyyppis = new ArrayList<String>();
        SearchCriteria criteria = new SearchCriteria();
        criteria.setAktiiviset(true);

        criteria.getOidRestrictionList().add(selectedOrg.getOid());
        List<OrganisaatioPerustieto> childOrgs = organisaatioSearchService.searchBasicOrganisaatios(criteria);
        if (childOrgs != null) {
            for (OrganisaatioPerustieto curChild : childOrgs) {
                if (curChild.getOrganisaatiotyypit().contains(OrganisaatioTyyppi.OPPILAITOS)
                        && !childOlTyyppis.contains(curChild.getOppilaitostyyppi())) {
                    childOlTyyppis.add(curChild.getOppilaitostyyppi());
                }
            }
        }
        return childOlTyyppis;
    }

    @SuppressWarnings("unchecked")
    public void loadKoulutusohjelmat() {

        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        //Select 'koulutusohjelma' from pre-filtered koodisto data.
        if (model.getKoulutuskoodiModel() != null && model.getKoulutuskoodiModel().getKoodi() != null) {
            model.getKoulutusohjelmat().clear();
            final String koulutuskoodiUri = model.getKoulutuskoodiModel().getKoodistoUri();

            LOG.debug("Find koulutusohjelma by koulutuskoodi uri : '{}'", koulutuskoodiUri);
            List<KoulutusmoduuliKoosteTyyppi> tyyppis = model.getQuickKomosByKoulutuskoodiUri(koulutuskoodiUri);
            List<KoulutusohjelmaModel> listaaKoulutusohjelmat = kolutusKoodistoConverter.listaaKoulutusohjelmas(tyyppis, I18N.getLocale());

            Collections.sort(listaaKoulutusohjelmat, new BeanComparator("nimi"));
            model.getKoulutusohjelmat().addAll(listaaKoulutusohjelmat);

            //Loading data from the parent tutkinto komo (startDate and koulutusohjelmanValinta).
            loadKoulutusohjelmaLisatiedotData(model.getKoulutuskoodiModel().getKoodistoUri(), model.getPohjakoulutusvaatimus());
        }
    }

    //Prefills the tutkinto komoto (koulutuksenAlkamisPvm, koulutusohjelmanValinta) fields if a tutkinto komoto exists
    private void loadKoulutusohjelmaLisatiedotData(final String koulutuskoodi, String pohjakoulutusvaatimus) {
        LOG.debug("loadtutkintoData, koulutuskoodi: {}, tarjoaja: {}", koulutuskoodi, getTarjoaja());
        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.setKoulutusKoodi(koulutuskoodi);

        /*
         * When use has selected many organisations(example koulutus copy),
         * an organisation OID is taken from the selected result row item, if
         * use has selected only one organisation on dialog, then the OID is
         * taken from the selected organisation.
         */
        kysely.getTarjoajaOids().add(getTarjoaja().getSingleSelectRowResultOrganisationOid());
        KoulutuksetVastaus vastaus = tarjontaSearchService.haeKoulutukset(kysely);

        if (vastaus.getKoulutukset() != null && !vastaus.getKoulutukset().isEmpty()) {
            for (KoulutusPerustieto curTulos : vastaus.getKoulutukset()) {

                if (pohjakoulutusMatches(pohjakoulutusvaatimus, curTulos)
                        && tarjoajaMatches(getTarjoaja().getSingleSelectRowResultOrganisationOid(), curTulos)) {
                    LueKoulutusKyselyTyyppi lueKysely = new LueKoulutusKyselyTyyppi();
                    lueKysely.setOid(curTulos.getKomotoOid());
                    LueKoulutusVastausTyyppi lueVastaus = tarjontaPublicService.lueKoulutus(lueKysely);
                    //KOULUTUKSEN ALKUPVM NO LONGER IN PARENT
                    //Date koulutuksenAlkuPvm = lueVastaus.getKoulutuksenAlkamisPaiva() != null ? lueVastaus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null;

                    //getModel().getKoulutusPerustiedotModel().setKoulutuksenAlkamisPvm(koulutuksenAlkuPvm);
                    getModel().setKoulutusLisatiedotModel(new KoulutusLisatiedotModel());

                    NimettyMonikielinenTekstiTyyppi kovt = ConversionUtils.getTeksti(lueVastaus.getTekstit(), KomotoTeksti.KOULUTUSOHJELMAN_VALINTA);
                    if (kovt != null) {
                        for (MonikielinenTekstiTyyppi.Teksti mkt : kovt.getTeksti()) {
                            getModel().getKoulutusLisatiedotModel().getLisatiedot(mkt.getKieliKoodi()).setKoulutusohjelmanValinta(mkt.getValue());
                        }
                    }
                    LOG.debug("going to reload tabsheet");
                }
            }
            if (lisatiedotView != null) {
                this.lisatiedotView.getEditKoulutusLisatiedotForm().reBuildTabsheet();
            }
        }
    }

    private boolean tarjoajaMatches(String tarjoajaOid, KoulutusPerustieto KoulutusPerustieto) {
        return (tarjoajaOid != null) && tarjoajaOid.equals(KoulutusPerustieto.getTarjoaja().getOid());
    }

    private boolean pohjakoulutusMatches(String pohjakoulutusvaatimus, KoulutusPerustieto KoulutusPerustieto) {
        return (pohjakoulutusvaatimus == null
                && KoulutusPerustieto.getPohjakoulutusvaatimus() == null)
                || (pohjakoulutusvaatimus != null
                && pohjakoulutusvaatimus.equals(KoulutusPerustieto.getPohjakoulutusvaatimus().getUri()));
    }

    public KoulutusPerustieto findKomotoByKoulutuskoodiPohjakoulutus(String koulutuskoodi, String pohjakoulutusvaatimus) {
        KoulutuksetKysely kysely = new KoulutuksetKysely();
        kysely.setKoulutusKoodi(koulutuskoodi);

        /*
         * When use has selected many organisations(example koulutus copy),
         * an organisation OID is taken from the selected result row item, if
         * use has selected only one organisation on dialog, then the OID is
         * taken from the selected organisation.
         */
        kysely.getTarjoajaOids().add(getTarjoaja().getSingleSelectRowResultOrganisationOid());
        KoulutuksetVastaus vastaus = tarjontaSearchService.haeKoulutukset(kysely);

        if (vastaus.getKoulutukset() != null && !vastaus.getKoulutukset().isEmpty()) {

            for (KoulutusPerustieto curTulos : vastaus.getKoulutukset()) {
                if (pohjakoulutusMatches(pohjakoulutusvaatimus, curTulos)
                        && tarjoajaMatches(getTarjoaja().getSingleSelectRowResultOrganisationOid(), curTulos)) {
                    return curTulos;
                }
            }
        }
        return null;
    }

    public void loadSelectedKomoData() {
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        final KoulutuskoodiModel koulutuskoodi = model.getKoulutuskoodiModel();
        final KoulutusohjelmaModel ohjelma = model.getKoulutusohjelmaModel();

        if (koulutuskoodi != null && koulutuskoodi.getKoodi() != null && ohjelma != null && ohjelma.getKoodi() != null) {
            model.getKoulutusohjelmat().clear();
            KoulutusmoduuliKoosteTyyppi tyyppi = model.getQuickKomo(
                    koulutuskoodi.getKoodistoUri(),
                    ohjelma.getKoodistoUri());

            if (tyyppi == null) {
                LOG.error("No tutkinto & koulutusohjelma, result was null. Search by '{}'" + " and '{}'", koulutuskoodi.getKoodistoUriVersio(), ohjelma.getKoodistoUriVersio());
            }

            koulutusToDTOConverter.updateKoulutuskoodiAndKoulutusohjelmaAndRelationsFromKoodisto(model, tyyppi, I18N.getLocale());
        }
    }

    public void showKoulutusPreview() {
        if (getRootView() != null) {
            getRootView().showNotification("NOT IMPLEMNTED");
        }
    }

    /**
     * @return the uiHelper
     */
    public TarjontaUIHelper getUiHelper() {
        return uiHelper;
    }

    /**
     * @param uiHelper the uiHelper to set
     */
    public void setUiHelper(TarjontaUIHelper uiHelper) {
        this.uiHelper = uiHelper;
    }

    /**
     * Enables or disables hakukohde button based on whether there are selected
     * koulutus objects in the list.
     */
    public void toggleCreateHakukohde() {
        String organisaatioOid = null;
        if (_model.getSelectedKoulutukset().size() > 0) {
            organisaatioOid = _model.getSelectedKoulutukset().get(0).getTarjoaja().getOid();
        }

        if (organisaatioOid == null) {
            organisaatioOid = getNavigationOrganisation().getOrganisationOid();
        }

        this.getRootView().getListKoulutusView().toggleCreateHakukohdeB(organisaatioOid, !this._model.getSelectedKoulutukset().isEmpty());
    }

    public void setSearchResultsView(SearchResultsView searchResultsView) {
        this.searchResultsView = searchResultsView;
    }

    /**
     * Search yhteyshenkilo for a koulutus using user service based on on name
     * of the user and the organisation of the koulutus.
     *     
* @param value - the name or part of the name of the user to search for
     */
    public List<HenkiloType> searchYhteyshenkilo(String value) {
        List<String> organisaatioOids = getTarjoaja().getOrganisaatioOidTree();

        //If given string is null or empty returning an empty list, i.e. not doing an empty search.
        Preconditions.checkNotNull(organisaatioOids, "A list of organisaatio OIDs cannot be null.");

        if (value == null || value.isEmpty()) {
            return new ArrayList<HenkiloType>();
        }
        //Doing the search to UserService
        HenkiloSearchObjectType searchType = new HenkiloSearchObjectType();

        //searchType.setConnective(SearchConnectiveType.AND);
        String[] nimetSplit = value.split(" ");
        if (nimetSplit.length > 1) {
            searchType.setSukunimi(nimetSplit[nimetSplit.length - 1]);
            searchType.setEtunimet(value.substring(0, value.lastIndexOf(' ')));
        } else {
            searchType.setEtunimet(value);
        }
        searchType.getOrganisaatioOids().addAll(organisaatioOids);
        HenkiloPagingObjectType paging = new HenkiloPagingObjectType();
        List<HenkiloType> henkilos = new ArrayList<HenkiloType>();
        try {

            henkilos = this.userService.listHenkilos(searchType, paging);
        } catch (Exception ex) {
            LOG.error("Problem fetching henkilos: {}", ex.getMessage());
        }

        //Returning the list of found henkilos.
        return henkilos;
    }

    @Override
    public boolean isSaveButtonEnabled(String oid, SisaltoTyyppi sisalto, TarjontaTila... requiredState) {
        return publishingService.isStateStepAllowed(oid, sisalto, requiredState);
    }

    public void setLisatiedotView(
            EditKoulutusLisatiedotToinenAsteView lisatiedotView) {
        this.lisatiedotView = lisatiedotView;
    }

    /**
     * Returns true if there are koulutuskoodis that are related to the
     * oppilaitostyyppis of the currently selected organisaatio.
     *     
* @return
     */
    public boolean availableKoulutus() {
        List<String> oppilaitostyyppiUris = getOppilaitostyyppiUris();

        if (this.getPermission().underConstruction() && uiHelper.isOrganisationKorkeakoulu(oppilaitostyyppiUris)) {
            //No KOMO data check needed.
            return true;
        } else {
            //KOMO required.
            HaeKaikkiKoulutusmoduulitKyselyTyyppi kysely = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
            kysely.getOppilaitostyyppiUris().addAll(oppilaitostyyppiUris);
            return !this.tarjontaPublicService.haeKaikkiKoulutusmoduulit(kysely).getKoulutusmoduuliTulos().isEmpty();
        }
    }

    /**
     * FIXME shouldn't this check that the (given) koulutus is allowed for ALL
     * of the given organisations?
     *     
* Only used from KoulutusKopiointiDialog, safe to modify. Maybe rename to
     * "checkKoulutusCanBeAddedToOrganisations(String koulutusKoodiUri,
     * Collection<> orgs)" ?
     *     
* @param orgs
     * @return
     */
    public boolean checkOrganisaatiosKoulutukses(Collection<OrganisaatioPerustieto> orgs) {
        for (OrganisaatioPerustieto org : orgs) {
            List<String> oppilaitosTyyppis = new ArrayList<String>(getOppilaitosTyyppiUrisForOrg(org));
            boolean isEmpty = this.uiHelper.getOlRelatedKoulutuskoodit(oppilaitosTyyppis).isEmpty();
            if (isEmpty) {
                return false;
            }
        }
        return true;
    }

    /**
     * Used only from UusiKoulutusDialog.
     *     
* Make sure selected Organisaatios have at least one common organisation
     * type.
     *     
* @param orgs
     * @return true if there is at leas one common organisaatio type in selected
     * organisation types
     */
    public boolean checkOrganisaatioOppilaitosTyyppimatches(Collection<OrganisaatioPerustieto> orgs) {

        // Load the list containing the SET of all OppilaitosTyyppi uris.
        List<Set<String>> listOfOppilaitostyyppisLists = new ArrayList<Set<String>>();
        for (OrganisaatioPerustieto org : orgs) {
            listOfOppilaitostyyppisLists.add(getOppilaitosTyyppiUrisForOrg(org));
        }

        // Initialize intersection with the first set in the list (if any)
        Set<String> intersectionSet = new HashSet<String>();
        if (!listOfOppilaitostyyppisLists.isEmpty()) {
            intersectionSet.addAll(listOfOppilaitostyyppisLists.get(0));
        }

        // Make intersection with all the sets
        for (Set<String> set : listOfOppilaitostyyppisLists) {
            intersectionSet.retainAll(set);
        }

        // If we have any common elements in the set we conlucde that there is a match and reation can proceed.
        return !intersectionSet.isEmpty();
    }

    /**
     * Returns a set of OppilaitosTyyppi's for a given organisation. If org is
     * of type "OPPILAITOS" then we use that orgs type AND (If org is of type
     * "KOULUTUSTOIMIJA" then we use the types for the child organisations. OR
     * If org is of type "OPETUSPISTE" then we use the parents OppilaitosTyyppi
     * uris also)
     *     
* @param org
     * @return
     */
    private Set<String> getOppilaitosTyyppiUrisForOrg(OrganisaatioPerustieto org) {
        Set<String> oppilaitosTyyppis = new HashSet<String>();

        //OrganisaatioDTO selectedOrg = this.getOrganisaatioService().findByOid(org.getOid());
//        if (selectedOrg == null) {
//            throw new RuntimeException("No organisation found by OID " + this.getNavigationOrganisation().getOrganisationOid() + ".");
//        }
        List<OrganisaatioTyyppi> tyypit = org.getOrganisaatiotyypit();

        //If the types of the organisaatio contains oppilaitos, its oppilaitostyyppi is appended to the list of oppilaitostyyppiuris
        if (tyypit.contains(OrganisaatioTyyppi.OPPILAITOS)) {
            oppilaitosTyyppis.add(org.getOppilaitostyyppi());
        }
        //If the types of the organisaatio contain koulutustoimija the oppilaitostyyppis of its children are appended to the
        //list of oppilaitostyyppiuris
        if (tyypit.contains(OrganisaatioTyyppi.KOULUTUSTOIMIJA)) {
            oppilaitosTyyppis.addAll(getChildOrgOlTyyppis(org));

            //If the types of the organisaatio contain opetuspiste the oppilaitostyyppi of its parent organisaatio is appended to the list of
            //oppilaitostyyppiuris
        } else if (tyypit.contains(OrganisaatioTyyppi.TOIMIPISTE)
                && org.getParentOid() != null) {
            List<String> olTyyppis = new ArrayList<String>(oppilaitosTyyppis);
            addParentOlTyyppi(org, olTyyppis);
            oppilaitosTyyppis.addAll(olTyyppis);
        }
        LOG.debug("olTyyppiUris size: {}", oppilaitosTyyppis.size());

        return oppilaitosTyyppis;
    }

    /**
     * Open edit koulutus view.
     *     
* @param koulutusOid
     * @param tab
     */
    private void showEditKoulutusView(final String koulutusOid, final KoulutusActiveTab tab) {
        editKoulutusView = new EditKoulutusView(koulutusOid, tab);
        getRootView().changeView(editKoulutusView);
        LOG.info("showing new form");
    }

    private String tilaToLangStr(TarjontaTila tila) {
        return i18n.getMessage(tila.value());
    }

    /**
     * @return the lukioPresenter
     */
    public TarjontaLukioPresenter getLukioPresenter() {
        return lukioPresenter;
    }

    /**
     * @param lukioPresenter the lukioPresenter to set
     */
    public void setLukioPresenter(TarjontaLukioPresenter lukioPresenter) {
        this.lukioPresenter = lukioPresenter;
    }

    /**
     * Get koulutustarjoaja.
     *     
* @return
     */
    public TarjoajaModel getTarjoaja() {
        return getModel().getTarjoajaModel();
    }

    /**
     * Get organisation data for navigation component.
     *     
* @return
     */
    public NavigationModel getNavigationOrganisation() {
        return getModel().getNavigationModel();
    }

    public void closeKoulutusRemovalDialog() {
        getRootView().getListKoulutusView().closeKoulutusDialog();
    }

    public void closeHakukohdeRemovalDialog() {
        getRootView().getSearchResultsView().getHakukohdeList().closeRemoveDialog();
    }

    public void showHakukohteetForKoulutus(KoulutusPerustieto koulutus) {

        HakukohteetVastaus vastaus = getHakukohteetForKoulutus(koulutus.getKomotoOid());
        this.getRootView().getListKoulutusView().showHakukohteetForKoulutus(vastaus.getHakukohteet(), koulutus);
    }

    public HakukohteetVastaus getHakukohteetForKoulutus(String komotoOid) {
        HakukohteetKysely kysely = new HakukohteetKysely();
        kysely.getKoulutusOids().add(komotoOid);
        kysely.setKoulutuksenAlkamisvuosi(-1);

        HakukohteetVastaus vastaus = tarjontaSearchService.haeHakukohteet(kysely);
        return vastaus;
    }

    /**
     * @return the korkeakouluPresenter
     */
    public TarjontaKorkeakouluPresenter getKorkeakouluPresenter() {
        return korkeakouluPresenter;
    }

    /**
     * @param korkeakouluPresenter the korkeakouluPresenter to set
     */
    public void setKorkeakouluPresenter(TarjontaKorkeakouluPresenter korkeakouluPresenter) {
        this.korkeakouluPresenter = korkeakouluPresenter;
    }

    public static class StringTuple {

        private String strOne;
        private String strTwo;

        public StringTuple(String stringOne, String stringTwo) {
            this.strOne = stringOne;
            this.strTwo = stringTwo;
        }

        public String getStrOne() {
            return strOne;
        }

        public String getStrTwo() {
            return strTwo;
        }
    }

    /**
     * Should be called "isKoulutusMutable" or something similar
     *
     * @param komotoOid
     * @return
     */
    public boolean isHakuStartedForKoulutus(String komotoOid) {
        boolean hakuStarted = false;
        HakukohteetVastaus hakukVastaus = getHakukohteetForKoulutus(komotoOid);
        for (HakukohdePerustieto curHakuk : hakukVastaus.getHakukohteet()) {
            Date hakuAlku = curHakuk.getHakuAlkamisPvm();
            Date today = new Date();
            if (today.after(hakuAlku)) {
                hakuStarted = true;
            }

            //jos hakutyyppi tai erillishaku 
            if (KoodistoURI.KOODI_LISAHAKU_URI.equals(curHakuk
                    .getHakutyyppiUri())
                    || KoodistoURI.KOODI_ERILLISHAKU_URI.equals(curHakuk
                            .getHakutapaKoodi().getUri())) {
                hakuStarted = false;
            }

        }

        return hakuStarted;
    }

    public boolean isKoulutusNivelvaihe() {
        KoulutusasteTyyppi koulTyyppi = getModel().getSelectedKoulutukset().get(0).getKoulutustyyppi();
        return koulTyyppi.equals(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS)
                || koulTyyppi.equals(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS)
                || koulTyyppi.equals(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS)
                || koulTyyppi.equals(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS)
                || koulTyyppi.equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS);
    }

    public HakukohteetVastaus findHakukohdeByHakukohdeOid(final String oid) {
        return tarjontaSearchService.haeHakukohteet(HakukohteetKysely.byHakukohdeOid(oid));
    }

    public KoulutuksetVastaus findKoulutusByKoulutusOid(final String oid) {
        return tarjontaSearchService.haeKoulutukset(KoulutuksetKysely.byKoulutusOid(oid));
    }

    public List<MonikielinenMetadataTyyppi> haeMetadata(String avain, String kategoria) {
        return tarjontaAdminService.haeMetadata(avain, kategoria);
    }

    public void setTarjontaSearchService(TarjontaSearchService tarjontaSearchService) {
        this.tarjontaSearchService = tarjontaSearchService;
    }

    @Override
    PublishingService getPublishingService() {
        return publishingService;
    }

}
