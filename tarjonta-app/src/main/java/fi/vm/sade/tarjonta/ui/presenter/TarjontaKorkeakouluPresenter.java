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
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;

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
import fi.vm.sade.tarjonta.ui.helper.conversion.KorkeakouluConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusLukioConverter;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KoulutuskoodiRowModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluKuvailevatTiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluPerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.EditKorkeakouluView;
import fi.vm.sade.tarjonta.ui.view.koulutus.kk.ShowKorkeakouluSummaryView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    private KoulutusKoodistoConverter kolutusKoodistoConverter;
    private TarjontaPresenter presenter; //initialized in Spring xml configuration file.
    private EditKorkeakouluPerustiedotView perustiedotView;
    private EditKorkeakouluKuvailevatTiedotView kuvailevatTiedotView;
    private EditKorkeakouluView editKoulutusView;
    private RegexModelFilter<KoulutuskoodiRowModel> filter;

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
        this.getEditKoulutusView().enableKuvailevatTiedotTab();
        this.getKuvailevatTiedotView().getLisatiedotForm().reBuildTabsheet();
        KorkeakouluPerustiedotViewModel perustiedot = getPerustiedotModel();

        if (perustiedot.isLoaded()) {//update KOMOTO
            PaivitaKoulutusTyyppi paivita = korkeakouluConverter.createPaivitaLukioKoulutusTyyppi(getTarjontaModel(), perustiedot.getKomotoOid(), tila);
            tarjontaAdminService.paivitaKoulutus(paivita);
        } else { //insert new KOMOTO
            for (OrganisationOidNamePair pair : getTarjontaModel().getTarjoajaModel().getOrganisationOidNamePairs()) {
                LisaaKoulutusTyyppi lisaa = korkeakouluConverter.createLisaaLukioKoulutusTyyppi(getTarjontaModel(), pair, tila);
                checkKoulutusmoduuli();
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
        HaeKoulutusmoduulitKyselyTyyppi kysely =
                KoulutusLukioConverter.mapToHaeKoulutusmoduulitKyselyTyyppi(
                KoulutusasteTyyppi.LUKIOKOULUTUS,
                getPerustiedotModel().getKoulutuskoodiModel(),
                getPerustiedotModel().getTutkintoohjelma());

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
        if (komotoOid != null) {
            LueKoulutusVastausTyyppi koulutus = getPresenter().getKoulutusByOid(komotoOid);
            korkeakouluConverter.loadLueKoulutusVastausTyyppiToModel(getPresenter().getModel(), koulutus, I18N.getLocale());
        } else {
            Preconditions.checkNotNull(getTarjontaModel().getTarjoajaModel().getSelectedOrganisationOid(), "Missing organisation OID.");
            getPerustiedotModel().clearModel();
            getTarjontaModel().setKoulutusLukioKuvailevatTiedot(new KoulutusLukioKuvailevatTiedotViewModel());
        }
    }

    public void loadSelectedKomoData() {
        KorkeakouluPerustiedotViewModel perustiedotModel = getPerustiedotModel();
        final KoulutuskoodiModel koulutuskoodi = perustiedotModel.getKoulutuskoodiModel();
        KoulutusohjelmaModel tutkintoohjelma = perustiedotModel.getTutkintoohjelma();

        if (koulutuskoodi != null && koulutuskoodi.getKoodi() != null && tutkintoohjelma != null && tutkintoohjelma.getKoodi() != null) {
            perustiedotModel.getTutkintoohjelmas().clear();
            KoulutusmoduuliKoosteTyyppi tyyppi = perustiedotModel.getQuickKomo(
                    koulutuskoodi.getKoodistoUriVersio(),
                    tutkintoohjelma.getKoodistoUriVersio());

            if (tyyppi == null) {
                LOG.error("No tutkinto & koulutusohjelma result was null. Search by '" + koulutuskoodi.getKoodistoUriVersio() + "'" + " and '" + koulutuskoodi.getKoodistoUriVersio() + "'");
            }

            // kolutusKoodistoConverter.listaaLukioSisalto(koulutuskoodi, lukiolinja, tyyppi, I18N.getLocale());

            //TODO: do we need the setters on bottom?
            perustiedotModel.setKoulutusaste(koulutuskoodi.getKoulutusaste());
            perustiedotModel.setKoulutusala(koulutuskoodi.getKoulutusala());
            //perustiedotModel.setKoulutuslaji(null);//TODO!!!!!!!!!!!
        }
    }

    /**
     * Load and convert Koodisto service data to human readable format. The data
     * is used in form combobox component.
     */
    public void loadTutkintoohjelmas() {
        LOG.debug("in loadTutkintoohjelmas");
        KorkeakouluPerustiedotViewModel perustiedot = getPerustiedotModel();
        KoulutuskoodiModel koulutuskoodiModel = getPerustiedotModel().getKoulutuskoodiModel();

        //Select 'tutkinto-ohjelma' from pre-filtered koodisto data.

        if (koulutuskoodiModel != null && koulutuskoodiModel.getKoodi() != null) {
            getPerustiedotModel().getTutkintoohjelmas().clear();
            LOG.debug("Tutkintoohjelmas list size : {}.", perustiedot.getKoulutuskoodiModel().getKoodistoUriVersio());
            List<KoulutusmoduuliKoosteTyyppi> tyyppis = perustiedot.getQuickKomosByKoulutuskoodiUri(perustiedot.getKoulutuskoodiModel().getKoodistoUriVersio());

//            final List<LukiolinjaModel> lukiolinjas = kolutusKoodistoConverter.listaaLukiolinjas(tyyppis, I18N.getLocale());
//            LOG.debug("Tutkintoohjelmas list size : {}.", lukiolinjas);
//            Collections.sort(lukiolinjas, new BeanComparator("nimi"));
//            perustiedot.getTutkintoohjelmas().addAll(lukiolinjas);
            //NO PARENT KOMOTO DATA FOR LUKIO
            //loadParentKomotoData(koulutuskoodiModel.getKoodistoUriVersio());
        } else {
            LOG.debug("No tutkintoohjelma selected.");
        }
    }

    /**
     * Load and convert Koodisto service data to human readable format. The data
     * is used in form combobox component.
     */
    public void loadKoulutuskoodis() {
        LOG.debug("in loadKoulutuskoodis");
        HaeKaikkiKoulutusmoduulitKyselyTyyppi kysely = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutustyyppi(KoulutusasteTyyppi.AMMATTIKORKEAKOULUTUS);

        //TODO: fix this
        //kysely.getOppilaitostyyppiUris().addAll(presenter.getOppilaitostyyppiUris());
        HaeKaikkiKoulutusmoduulitVastausTyyppi allKomoParents = tarjontaPublicService.haeKaikkiKoulutusmoduulit(kysely);
        List<KoulutusmoduuliTulos> resultKomos = allKomoParents.getKoulutusmoduuliTulos();

        Set<String> uris = new HashSet<String>();
        List<KoulutusmoduuliKoosteTyyppi> komos = new ArrayList<KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliTulos komoParents : resultKomos) {
            komos.add(komoParents.getKoulutusmoduuli());
            uris.add(komoParents.getKoulutusmoduuli().getKoulutuskoodiUri());
        }
        LOG.debug("KOMOs found {}", komos.size());
        KorkeakouluPerustiedotViewModel perusModel = getPerustiedotModel();
        perusModel.setKomos(komos);
        perusModel.createCacheKomos(); //cache komos to map object

        //koodisto service search result remapped to UI model objects.
        List<KoulutuskoodiModel> listaaKoulutuskoodit = kolutusKoodistoConverter.listaaKoulutukses(uris, I18N.getLocale());
        Collections.sort(listaaKoulutuskoodit, new BeanComparator("nimi"));

        perusModel.getKoulutuskoodis().clear();
        perusModel.getKoulutuskoodis().addAll(listaaKoulutuskoodit);
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

    public void updateKoulutuskoodiToModel(final KoulutuskoodiRowModel rowModel) {
        Preconditions.checkNotNull(rowModel, "KoulutuskoodiRowModel cannot be null.");
        List<KoulutuskoodiModel> listaaKoulutukses = kolutusKoodistoConverter.listaaKoulutukses(rowModel, I18N.getLocale());

        if (listaaKoulutukses.isEmpty()) {
            Preconditions.checkNotNull(rowModel, "No KoulutuskoodiModel object found.");
        }
        presenter.getModel().getKorkeakouluPerustiedot().setKoulutuskoodiModel(listaaKoulutukses.get(0));
    }

    public void showKorkeakouluKoulutusEditView(Collection<OrganisaatioPerustietoType> orgs) {
        Preconditions.checkNotNull(orgs, "Collection of OrganisaatioPerustietoTypes cannot be null.");
        showEditKoulutusView(null, KoulutusActiveTab.PERUSTIEDOT);
    }

    /**
     * Retrieves the koulutus objects for ListKoulutusView.
     *
     * @return the koulutus objects
     */
    public List<KoulutuskoodiRowModel> getKoulutusDataSource() {
        ArrayList<KoulutuskoodiRowModel> results = Lists.<KoulutuskoodiRowModel>newArrayList();

        String num = "";

        for (int i = 0; i < 20; i++) {
            KoulutuskoodiRowModel r = new KoulutuskoodiRowModel();
            r.setKoodi(num += i);
            r.setNimi("nimi asd adad ada adsadsasda da da asd asdd");
            results.add(r);
        }

        return results;
    }

    public List<KoulutuskoodiRowModel> filterKoulutuskoodis() {
        final String searchWord = getPerustiedotModel().getValitseKoulutus().getSearchWord();

        if (searchWord == null || searchWord.isEmpty() || searchWord.length() < 2) {
            LOG.debug("Search all items. param : '{}'", searchWord);
            return getKoulutusDataSource();
        }

        if (filter == null) {
            filter = new RegexModelFilter<KoulutuskoodiRowModel>();

        }
        final List<KoulutuskoodiRowModel> result = filter.filterByParams(getKoulutusDataSource(), searchWord);
        LOG.debug("Result size  : {}, param : '{}' ", result.size(), searchWord);

        return result;
    }
}
