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
package fi.vm.sade.tarjonta.ui.presenter;

import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.service.types.*;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.enums.SelectedOrgModel;
import fi.vm.sade.tarjonta.ui.helper.OidCreationException;
import fi.vm.sade.tarjonta.ui.helper.RegexModelFilter;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.helper.conversion.KorkeakouluConverter;
import static fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter.INVALID_DATA;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KoulutuskoodiRowModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluKuvailevatTiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluPerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.LisaaNimiDialog;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.ShowKorkeakouluSummaryView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.ValitseKoulutusDialog;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.ValitseTutkintoohjelmaDialog;
import java.util.List;
import java.util.Locale;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.ValitseKoulutusModel;

/**
 *
 * @author Jani Wilén
 */
@Component
public class TarjontaKorkeakouluPresenter {

    public class FakeTarjontaAdminService implements TarjontaAdminService {

        public FakeTarjontaAdminService() {
        }

        @Override
        public HakukohdeTyyppi poistaHakukohde(HakukohdeTyyppi hakukohdePoisto) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<ValintakoeTyyppi> paivitaValintakokeitaHakukohteelle(String hakukohdeOid, List<ValintakoeTyyppi> hakukohteenValintakokeet) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public HakukohdeTyyppi lisaaHakukohde(HakukohdeTyyppi hakukohde) throws GenericFault {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void lisaaTaiPoistaKoulutuksiaHakukohteelle(LisaaKoulutusHakukohteelleTyyppi parameters) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public LisaaKoulutusVastausTyyppi lisaaKoulutus(LisaaKoulutusTyyppi koulutus) throws GenericFault {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void poistaValintakoe(String valintakoeTunniste) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public KoulutusmoduuliKoosteTyyppi paivitaKoulutusmoduuli(KoulutusmoduuliKoosteTyyppi koulutusmoduuli) throws GenericFault {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void poistaKoulutus(String koulutusOid) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }


        @Override
        public MonikielinenMetadataTyyppi tallennaMetadata(String avain, String kategoria, String kieli, String arvo) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<MonikielinenMetadataTyyppi> haeMetadata(String avain, String kategoria) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public HakukohdeTyyppi paivitaHakukohde(HakukohdeTyyppi hakukohdePaivitys) throws GenericFault {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PaivitaTilaVastausTyyppi paivitaTilat(PaivitaTilaTyyppi parameters) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<ValintakoeTyyppi> tallennaValintakokeitaHakukohteelle(String hakukohdeOid, List<ValintakoeTyyppi> hakukohteenValintakokeet) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public KoulutusmoduuliKoosteTyyppi lisaaKoulutusmoduuli(KoulutusmoduuliKoosteTyyppi koulutusmoduuli) throws GenericFault {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public PaivitaKoulutusVastausTyyppi paivitaKoulutus(PaivitaKoulutusTyyppi koulutus) throws GenericFault {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void poistaHakukohdeLiite(String hakukohdeLiiteTunniste) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean testaaTilasiirtyma(GeneerinenTilaTyyppi parameters) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean tarkistaKoulutuksenKopiointi(TarkistaKoulutusKopiointiTyyppi parameters) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void tallennaLiitteitaHakukohteelle(String hakukohdeOid, List<HakukohdeLiiteTyyppi> hakukohteenLiitteen) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void initSample(String parameters) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void initKomo(String parameters) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        @SOAPBinding(parameterStyle = ParameterStyle.BARE)
        @WebResult(name = "haeOidVastaus", targetNamespace = "http://service.tarjonta.sade.vm.fi/types", partName = "parameters")
        @WebMethod
        public String haeOid(
                @WebParam(partName = "parameters", name = "haeOid", targetNamespace = "http://service.tarjonta.sade.vm.fi/types") String parameters) {
            // TODO Auto-generated method stub
            return null;
        }
    }
    private static transient final Logger LOG = LoggerFactory.getLogger(TarjontaKorkeakouluPresenter.class);
    private static transient final String URI_LANG_FI = "kieli_fi";
    private TarjontaAdminService tarjontaAdminService = new FakeTarjontaAdminService();
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;
    @Autowired(required = true)
    private KorkeakouluConverter korkeakouluConverter;
    @Autowired(required = true)
    private KoulutusKoodistoConverter koulutusKoodistoConverter;
    @Autowired(required = true)
    private UiBuilder uiBuilder;
    @Autowired(required = true)
    private TarjontaUIHelper tarjontaUiHelper;
    private TarjontaPresenter presenter; //initialized in Spring xml configuration file.
    private EditKorkeakouluPerustiedotView perustiedotView;
    private EditKorkeakouluKuvailevatTiedotView kuvailevatTiedotView;
    private ValitseKoulutusDialog valitseKoulutusDialog;
    private ValitseTutkintoohjelmaDialog valitseTutkintoohjelmaDialog;
    private EditKorkeakouluView editKoulutusView;
    private RegexModelFilter<KoulutuskoodiRowModel> filter;
    @Autowired(required = true)
    private SearchPresenter searchPresenter;
    private Locale locale = I18N.getLocale();
    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    public TarjontaKorkeakouluPresenter() {
    }

    /**
     * Insert and update lukiokoulutus.
     *
     * @param tila
     * @throws ExceptionMessage
     * @return komo oid
     */
    public void saveKoulutus(SaveButtonState tila) throws OidCreationException {

        LOG.debug("in saveKoulutus, tila : {}", tila);
        /**
         * TODOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO!!!!
         */
        ///this.getEditKoulutusView().enableKuvailevatTiedotTab();
        ///this.getKuvailevatTiedotView().getLisatiedotForm().reBuildTabsheet();
        KorkeakouluPerustiedotViewModel perustiedot = getPerustiedotModel();

        /**
         * TODOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO!!!!
         */
        KoulutusasteTyyppi tyyppi = KoulutusasteTyyppi.AMMATTIKORKEAKOULUTUS;
        LOG.debug("In is loaded : {}", perustiedot.isLoaded());

        if (perustiedot.isLoaded()) {//update KOMOTO
            PaivitaKoulutusTyyppi paivita = korkeakouluConverter.createPaivitaKoulutusTyyppi(getTarjontaModel(), perustiedot.getKomotoOid(), tyyppi, tila);
            tarjontaAdminService.paivitaKoulutus(paivita);
            perustiedot.setKoulutusmoduuliOid(paivita.getKomoOid());
        } else { //insert new KOMOTO
            for (OrganisationOidNamePair pair : getTarjontaModel().getTarjoajaModel().getOrganisationOidNamePairs()) {
                LisaaKoulutusTyyppi lisaa = korkeakouluConverter.createLisaaKoulutusTyyppi(getTarjontaModel(), tyyppi, pair, tila);

                LisaaKoulutusVastausTyyppi lisaaKoulutus = tarjontaAdminService.lisaaKoulutus(lisaa);
                Preconditions.checkNotNull(lisaaKoulutus.getVersion(), INVALID_DATA + "Version ID for optimistic locking control cannot be null.");
                Preconditions.checkNotNull(lisaaKoulutus.getKomoOid(), INVALID_DATA + "KOMO OID cannot ne null.");
                perustiedot.setKomotoOid(lisaa.getOid());
                perustiedot.setVersion(lisaaKoulutus.getVersion());
                perustiedot.setKoulutusmoduuliOid(lisaaKoulutus.getKomoOid());
            }
        }
    }

    public void getReloadKoulutusListData() {
        getPresenter().getRootView().getListKoulutusView().reload();
    }

    public void showEditKoulutusView(final KoulutusActiveTab tab) {
        showEditKoulutusView(tab);
    }

    /**
     * Open lukiokoulutus edit view. When KOMOTO OID is provided to the method,
     * lukiokoulutus data is preloaded to the edit view form.
     *
     * @param komotoOid
     * @param tab
     */
    public void showEditKoulutusView(final String komotoOid, final KoulutusActiveTab tab) {
        // If koulutus OID is provided, the koulutus is read from database
        // before opening the KoulutusEditView.

        if (komotoOid != null) {
            presenter.readOrgTreeToTarjoajaByModel(SelectedOrgModel.TARJOAJA);
        } else {
            presenter.readOrgTreeToTarjoajaByModel(SelectedOrgModel.NAVIGATION);
        }

        loadKomoto(komotoOid);

        setEditKoulutusView(new EditKorkeakouluView(komotoOid, tab));
        getPresenter().getRootView().changeView(getEditKoulutusView());
    }

    public void showCopyKoulutusView(final String komotoOid, final KoulutusActiveTab tab, Collection<OrganisaatioPerustietoType> orgs) {
        // If koulutus OID is provided, the koulutus is read from database
        // before opening the KoulutusEditView.

        loadKomoto(komotoOid);
        if (orgs != null && orgs.size() > 0) {
            presenter.getModel().getTarjoajaModel().getOrganisationOidNamePairs().clear();
            for (OrganisaatioPerustietoType org : orgs) {
                OrganisationOidNamePair oidNamePair = new OrganisationOidNamePair();
                oidNamePair.setOrganisation(org.getOid(), org.getNimiFi());
                presenter.getModel().getTarjoajaModel().getOrganisationOidNamePairs().add(oidNamePair);
            }
        }
        setEditKoulutusView(new EditKorkeakouluView(komotoOid, tab));
        getPerustiedotModel().setKomotoOid(null);
        getPerustiedotModel().setTila(TarjontaTila.LUONNOS);
        getPresenter().getRootView().changeView(getEditKoulutusView());
    }

    /**
     *
     * Open lukiokoulutus summary page with current komoto OID.
     *
     * @param komotoOid
     */
    public void showSummaryKoulutusView() {
        final String komotoOid = getPerustiedotModel().getKomotoOid();
        if (komotoOid != null) {
            showSummaryKoulutusView(komotoOid);
        } else {
            LOG.error("Page navigation error - No KOMOTO OID selected, return to main page.");
            presenter.showMainDefaultView();
        }
    }

    public void showSummaryKoulutusView(final String komotoOid) {
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID object cannot be null.");
        loadKomoto(komotoOid);
        getPresenter().getRootView().changeView(new ShowKorkeakouluSummaryView(getPerustiedotModel().getTutkintoohjelma().getNimi(), null));
    }

    /**
     * Reset UI models and set loaded values to the models.
     *
     * @param komotoOid, allows null value.
     */
    private void loadKomoto(final String komotoOid) {
        LOG.debug("In loadKomoto : {}", komotoOid);

        if (komotoOid != null) {
            getPerustiedotModel().clearModel();
            getKuvailevatTiedotModel().clearModel();
            LueKoulutusVastausTyyppi koulutus = getPresenter().getKoulutusByOid(komotoOid);
            korkeakouluConverter.loadLueKoulutusVastausTyyppiToModel(getPresenter().getModel(), koulutus, locale);
        } else {
            Preconditions.checkNotNull(getTarjontaModel().getTarjoajaModel().getSelectedOrganisationOid(), "Missing organisation OID.");
            korkeakouluConverter.updateKoulutuskoodiModel(getPerustiedotModel(), getValitseKoulutusModel(), locale);
        }

        //set the user koodi language uri to the base model.
        getPerustiedotModel().setUserKoodiLangUri(korkeakouluConverter.getUserLangUri());
    }

    private TarjontaModel getTarjontaModel() {
        return getPresenter().getModel();
    }

    /**
     * @return the perustiedotModel
     */
    public KorkeakouluPerustiedotViewModel getPerustiedotModel() {
        return getTarjontaModel().getKorkeakouluPerustiedot();
    }

    /**
     * @return the ValitseKoulutusModel
     */
    public ValitseKoulutusModel getValitseKoulutusModel() {
        return getTarjontaModel().getValitseKoulutusModel();
    }

    /**
     * @return the kuvailevatTiedotModel
     */
    public KorkeakouluKuvailevatTiedotViewModel getKuvailevatTiedotModel() {
        return getTarjontaModel().getKorkeakouluKuvailevatTiedot();
    }

    /**
     * @return the presenter
     */
    public TarjontaPresenter getPresenter() {
        return presenter;
    }

    /**
     * @param presenter the presenter to set
     */
    public void setPresenter(TarjontaPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * @return the perustiedotView
     */
    public EditKorkeakouluPerustiedotView getPerustiedotView() {
        return perustiedotView;
    }

    /**
     * @param perustiedotView the perustiedotView to set
     */
    public void setPerustiedotView(EditKorkeakouluPerustiedotView perustiedotView) {
        this.perustiedotView = perustiedotView;
    }

    /**
     * @return the kuvailevatTiedotView
     */
    public EditKorkeakouluKuvailevatTiedotView getKuvailevatTiedotView() {
        return kuvailevatTiedotView;
    }

    /**
     * @param kuvailevatTiedotView the kuvailevatTiedotView to set
     */
    public void setKuvailevatTiedotView(EditKorkeakouluKuvailevatTiedotView kuvailevatTiedotView) {
        this.kuvailevatTiedotView = kuvailevatTiedotView;
    }

    /**
     * @return the editKoulutusView
     */
    public EditKorkeakouluView getEditKoulutusView() {
        return editKoulutusView;
    }

    /**
     * @param editKoulutusView the editKoulutusView to set
     */
    public void setEditKoulutusView(EditKorkeakouluView editKoulutusView) {
        this.editKoulutusView = editKoulutusView;
    }

    public void showKorkeakouluKoulutusEditView() {
        showEditKoulutusView(null, KoulutusActiveTab.PERUSTIEDOT);
    }

    /**
     * Filter koodisto tutkintokoodi data by given parameters. Koulutusala by
     * koodisto relation + search word by regex.
     *
     * @return
     */
    public List<KoulutuskoodiRowModel> filterKoulutuskoodis() {
        final String searchWord = getValitseKoulutusModel().getSearchWord();
        final String koulutusalaUri = getValitseKoulutusModel().getKoulutusala();

        List<KoulutuskoodiRowModel> models = null;

        if (koulutusalaUri != null && !koulutusalaUri.isEmpty()) {
            LOG.debug("Filter all items by koulutusala Uri : {}", koulutusalaUri);
            models = koulutusKoodistoConverter.listaaKoulutuksesByKoulutusala(koulutusalaUri, locale);
        } else if (searchWord == null || searchWord.isEmpty() || searchWord.length() < 2) {
            LOG.debug("No filters.");
            return koulutusKoodistoConverter.listaaKoulutuksesByKoulutusala(null, locale);
        } else {
            LOG.debug("Filter all items by koulustusala : '{}' and search word '{}'.", koulutusalaUri, searchWord);
            models = koulutusKoodistoConverter.listaaKoulutuksesByKoulutusala(null, locale);
        }

        if (filter == null) {
            filter = new RegexModelFilter<KoulutuskoodiRowModel>();
        }
        final List<KoulutuskoodiRowModel> result = filter.filterByParams(models, searchWord);
        LOG.debug("Result size : {} ", result.size());

        return result;
    }

    /**
     * Direct KOMO name text update request to database. Search and update by
     * KOMO OID.
     *
     * @param komoOid, not null value.
     */
    public void updateSelectedKOMOName(final String komoOid) {
        Preconditions.checkNotNull(komoOid, "KOMO OID cannot be null.");
        LueKoulutusmoduuliKyselyTyyppi kysely = new LueKoulutusmoduuliKyselyTyyppi();
        kysely.setOid(komoOid);

        LueKoulutusmoduuliVastausTyyppi lueKoulutusmoduuli = tarjontaPublicService.lueKoulutusmoduuli(kysely);
        Preconditions.checkNotNull(lueKoulutusmoduuli, "Update failed, KOMO not found by OID " + komoOid);


        KoulutusmoduuliKoosteTyyppi koulutusmoduuli = lueKoulutusmoduuli.getKoulutusmoduuli();
        //persist changed data to db, after the operatio we need to reload komos to form. 
        tarjontaAdminService.paivitaKoulutusmoduuli(koulutusmoduuli);
    }

    /**
     * @return the ValitseKoulutusDialog
     */
    public void showValitseKoulutusDialog() {
        getPerustiedotModel().clearModel();
        getKuvailevatTiedotModel().clearModel();
        getPerustiedotModel().setUserKoodiLangUri(korkeakouluConverter.getUserLangUri());
        this.valitseKoulutusDialog = new ValitseKoulutusDialog(presenter, uiBuilder);
        this.valitseKoulutusDialog.windowOpen();
    }

    /**
     * @return the ValitseTutkintoohjelmaDialog
     */
    public void showValitseTutkintoohjelmaDialog() {
        this.valitseTutkintoohjelmaDialog = new ValitseTutkintoohjelmaDialog(presenter, tarjontaKoodistoHelper, uiBuilder);
        this.valitseTutkintoohjelmaDialog.windowOpen();
    }

    public void showLisaaKieliDialog() {
        LisaaNimiDialog dialog = new LisaaNimiDialog(presenter, tarjontaUiHelper, uiBuilder);
        dialog.windowOpen();
    }

    /**
     * @return the searchPresenter
     */
    public SearchPresenter getSearchPresenter() {
        return searchPresenter;
    }

    /**
     * @param searchPresenter the searchPresenter to set
     */
    public void setSearchPresenter(SearchPresenter searchPresenter) {
        this.searchPresenter = searchPresenter;
    }
}
