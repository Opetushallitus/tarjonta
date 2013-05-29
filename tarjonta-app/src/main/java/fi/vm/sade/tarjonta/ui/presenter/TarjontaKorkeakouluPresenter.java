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

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.service.types.*;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.ui.enums.SelectedOrgModel;
import fi.vm.sade.tarjonta.ui.helper.RegexModelFilter;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.helper.conversion.KorkeakouluConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusLukioConverter;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KoulutuskoodiRowModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluKuvailevatTiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluPerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.ShowKorkeakouluSummaryView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.ValitseKoulutusDialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.beanutils.BeanComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class TarjontaKorkeakouluPresenter {

    private static transient final Logger LOG = LoggerFactory.getLogger(TarjontaKorkeakouluPresenter.class);
    @Autowired(required = true)
    protected OIDService oidService;
    @Autowired(required = true)
    private TarjontaAdminService tarjontaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;
    @Autowired(required = true)
    private KorkeakouluConverter korkeakouluConverter;
    @Autowired(required = true)
    private KoulutusKoodistoConverter koulutusKoodistoConverter;
    @Autowired(required = true)
    private UiBuilder uiBuilder;
    private TarjontaPresenter presenter; //initialized in Spring xml configuration file.
    private EditKorkeakouluPerustiedotView perustiedotView;
    private EditKorkeakouluKuvailevatTiedotView kuvailevatTiedotView;
    private ValitseKoulutusDialog valitseKoulutusDialog;
    private EditKorkeakouluView editKoulutusView;
    private RegexModelFilter<KoulutuskoodiRowModel> filter;
    @Autowired(required = true)
    private SearchPresenter searchPresenter;
    private Locale locale = I18N.getLocale();

    public TarjontaKorkeakouluPresenter() {
    }

    /**
     * Insert and update lukiokoulutus.
     *
     * @param tila
     * @throws ExceptionMessage
     */
    public void saveKoulutus(SaveButtonState tila) throws ExceptionMessage {
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

        if (perustiedot.isLoaded()) {//update KOMOTO
            PaivitaKoulutusTyyppi paivita = korkeakouluConverter.createPaivitaKoulutusTyyppi(getTarjontaModel(), perustiedot.getKomotoOid(), tyyppi, tila);
            tarjontaAdminService.paivitaKoulutus(paivita);
        } else { //insert new KOMOTO
            for (OrganisationOidNamePair pair : getTarjontaModel().getTarjoajaModel().getOrganisationOidNamePairs()) {
                LisaaKoulutusTyyppi lisaa = korkeakouluConverter.createLisaaKoulutusTyyppi(getTarjontaModel(), tyyppi, pair, tila);
                tarjontaAdminService.lisaaKoulutus(lisaa);
                perustiedot.setKomotoOid(lisaa.getOid());
            }
        }
    }

    /**
     * Tries to find KoulutusModuuli ("KOMO") for given koulutus (tutkinto ==
     * KoulutudKoodiUri AND koulutusohjelma == KoulutusOhjelmsKoodiUri)
     */
    private void checkKoulutusmoduuli() {
        HaeKoulutusmoduulitKyselyTyyppi kysely = new HaeKoulutusmoduulitKyselyTyyppi();



        kysely.setKoulutuskoodiUri(getPerustiedotModel().getKoulutuskoodiModel().getKoodistoUriVersio());
        kysely.setKoulutusohjelmakoodiUri(getPerustiedotModel().getTutkintoohjelma().getKoodistoUriVersio());
        HaeKoulutusmoduulitVastausTyyppi vastaus = this.tarjontaPublicService.haeKoulutusmoduulit(kysely);

        if (vastaus.getKoulutusmoduuliTulos().isEmpty()) {
            //No KOMO, insert new KOMO
            LOG.error("Tarjonta do not have requested komo! "
                    + "tutkinto : '" + kysely.getKoulutuskoodiUri()
                    + "', koulutusohjelma : '" + kysely.getKoulutusohjelmakoodiUri() + "'");
        } else {
            //KOMO found
            getPerustiedotModel().setKoulutusmoduuliOid(vastaus.getKoulutusmoduuliTulos().get(0).getKoulutusmoduuli().getOid());
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

    private void loadKomoto(final String komotoOid) {
        getPerustiedotModel().clearModel();
        getKuvailevatTiedotModel().clearModel();

        if (komotoOid != null) {
            LueKoulutusVastausTyyppi koulutus = getPresenter().getKoulutusByOid(komotoOid);
            korkeakouluConverter.loadLueKoulutusVastausTyyppiToModel(getPresenter().getModel(), koulutus, locale);
        } else {
            Preconditions.checkNotNull(getTarjontaModel().getTarjoajaModel().getSelectedOrganisationOid(), "Missing organisation OID.");
            korkeakouluConverter.updateKoulutuskoodiModel(getPerustiedotModel(), locale);
        }
    }

    /**
     * Load and convert Koodisto service data to human readable format. The data
     * is used in form combobox component.
     */
    public void loadTutkintoohjelmas(final String komoUri, KoulutusasteTyyppi aste) {
        Preconditions.checkNotNull(komoUri, "KOMO URI cannot be null.");
        HaeKaikkiKoulutusmoduulitKyselyTyyppi tyyppi = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
        tyyppi.setKoulutuskoodiUri(komoUri);

        if (aste != null) {
            tyyppi.setKoulutustyyppi(aste);
        }

        HaeKaikkiKoulutusmoduulitVastausTyyppi haeKaikkiKoulutusmoduulit = tarjontaPublicService.haeKaikkiKoulutusmoduulit(tyyppi);
        List<KoulutusmoduuliTulos> modules = haeKaikkiKoulutusmoduulit.getKoulutusmoduuliTulos();

        List<KoulutusmoduuliKoosteTyyppi> komoTyyppis = Lists.<KoulutusmoduuliKoosteTyyppi>newArrayList();
        for (KoulutusmoduuliTulos t : modules) {
            komoTyyppis.add(t.getKoulutusmoduuli());
        }

        List<TutkintoohjelmaModel> koulutusohjelmas = KorkeakouluConverter.convertToTutkintoohjelmaModels(komoTyyppis, locale);
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

    public List<KoulutuskoodiRowModel> filterKoulutuskoodis() {
        final String searchWord = getPerustiedotModel().getValitseKoulutus().getSearchWord();
        final String koulutusalaUri = getPerustiedotModel().getValitseKoulutus().getKoulutusala();

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
     * @return the valitseKoulutusDialog
     */
    public void showValitseKoulutusDialog() {
        if (this.valitseKoulutusDialog == null) {
            this.valitseKoulutusDialog = new ValitseKoulutusDialog(presenter, uiBuilder);
        }

        this.valitseKoulutusDialog.windowOpen();
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
