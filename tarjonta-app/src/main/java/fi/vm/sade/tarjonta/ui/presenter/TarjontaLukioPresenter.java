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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.enums.SelectedOrgModel;
import fi.vm.sade.tarjonta.ui.helper.OidCreationException;
import fi.vm.sade.tarjonta.ui.helper.OidHelper;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusLukioConverter;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusKuvailevatTiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusPerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.ShowKoulutusSummaryView;

/**
 *
 * @author Jani Wilén
 */
@Component
public class TarjontaLukioPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaLukioPresenter.class);
    @Autowired(required = true)
    protected OidHelper oidHelper;
    @Autowired(required = true)
    private TarjontaAdminService tarjontaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;
    @Autowired(required = true)
    private KoulutusLukioConverter lukioKoulutusConverter;
    @Autowired(required = true)
    private KoulutusKoodistoConverter kolutusKoodistoConverter;

    private TarjontaPresenter presenter; //initialized in Spring xml configuration file.
    public EditLukioKoulutusPerustiedotView perustiedotView;
    private EditLukioKoulutusKuvailevatTiedotView kuvailevatTiedotView;
    private EditLukioKoulutusView editLukioKoulutusView;
    private ShowKoulutusSummaryView summaryView;

    public TarjontaLukioPresenter() {
    }

    /**
     * Insert and update lukiokoulutus.
     *
     * @param tila
     * @throws ExceptionMessage
     */
    public void saveKoulutus(SaveButtonState tila, KoulutusActiveTab activeTab) throws OidCreationException {
        LOG.debug("in saveKoulutus, tila : {}", tila);
        this.editLukioKoulutusView.enableKuvailevatTiedotTab();
        this.kuvailevatTiedotView.getLisatiedotForm().reBuildTabsheet();
        
        String koulutusOid = null;

        KoulutusLukioPerustiedotViewModel perustiedot = getPerustiedotModel();

        if (perustiedot.isLoaded()) {//update KOMOTO
            PaivitaKoulutusTyyppi paivita = lukioKoulutusConverter.createPaivitaLukioKoulutusTyyppi(getTarjontaModel(), perustiedot.getKomotoOid(), tila);
            try {
                paivita.setViimeisinPaivittajaOid(presenter.getUserOid());
            } catch (Exception ex) {

            }

            tarjontaAdminService.paivitaKoulutus(paivita);
            koulutusOid = paivita.getOid();
        } else { //insert new KOMOTO
            for (OrganisationOidNamePair pair : getTarjontaModel().getTarjoajaModel().getOrganisationOidNamePairs()) {
                LisaaKoulutusTyyppi lisaa = lukioKoulutusConverter.createLisaaLukioKoulutusTyyppi(getTarjontaModel(), pair, tila);
                try {
                   lisaa.setViimeisinPaivittajaOid(presenter.getUserOid());
                } catch (Exception exp ) {

                }
                checkKoulutusmoduuli();
                if (checkExistingKomoto(lisaa)) {
                    tarjontaAdminService.lisaaKoulutus(lisaa);
                    koulutusOid = lisaa.getOid();
                    perustiedot.setKomotoOid(lisaa.getOid());
                } else {
                    LOG.debug("Unable to add koulutus because of the duplicate");
                    throw new OidCreationException("EditKoulutusPerustiedotYhteystietoView.koulutusExistsMessage");
                }
            }
        }
        Preconditions.checkNotNull(koulutusOid);
        showEditKoulutusView(koulutusOid, activeTab);
        
    }

    /**
     * Tries to find KoulutusModuuli ("KOMO") for given koulutus (tutkinto ==
     * KoulutudKoodiUri AND koulutusohjelma == KoulutusOhjelmakoodiUri)
     */
    private void checkKoulutusmoduuli() {
        HaeKoulutusmoduulitKyselyTyyppi kysely =
                KoulutusLukioConverter.mapToHaeKoulutusmoduulitKyselyTyyppi(
                KoulutusasteTyyppi.LUKIOKOULUTUS,
                getPerustiedotModel().getKoulutuskoodiModel(),
                getPerustiedotModel().getLukiolinja());

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

        setEditKoulutusView(new EditLukioKoulutusView(komotoOid, tab));
        getPresenter().getRootView().changeView(editLukioKoulutusView);
    }

    public void showCopyKoulutusView(final String komotoOid, final KoulutusActiveTab tab, Collection<OrganisaatioPerustieto> orgs) {
        // If koulutus OID is provided, the koulutus is read from database
        // before opening the KoulutusEditView.

        loadKomoto(komotoOid);
        if (orgs != null && orgs.size() > 0) {
            presenter.getModel().getTarjoajaModel().getOrganisationOidNamePairs().clear();
            for (OrganisaatioPerustieto org : orgs) {
                OrganisationOidNamePair oidNamePair = new OrganisationOidNamePair();
                oidNamePair.setOrganisation(org.getOid(), OrganisaatioDisplayHelper.getClosestBasic(I18N.getLocale(), org));
                presenter.getModel().getTarjoajaModel().getOrganisationOidNamePairs().add(oidNamePair);
            }
        }
        setEditKoulutusView(new EditLukioKoulutusView(komotoOid, tab));
        getPerustiedotModel().setKomotoOid(null);
        getPerustiedotModel().setTila(TarjontaTila.LUONNOS);

        getPresenter().getRootView().changeView(editLukioKoulutusView);
    }

    /**
     *
     * Open lukiokoulutus summary page with current komoto OID.
     *

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
        summaryView = new ShowKoulutusSummaryView(getPerustiedotModel().getLukiolinja().getNimi(), null);
        getPresenter().getRootView().changeView(summaryView);
    }

    public void showRemoveHakukohdeFromLukioKoulutusDialog(String hakukohdeOid, String hakukohdeNimi) {
       summaryView.showHakukohdeRemovalDialog(hakukohdeOid,hakukohdeNimi);
    }


    private void loadKomoto(final String komotoOid) {
        if (komotoOid != null) {
            LueKoulutusVastausTyyppi koulutus = getPresenter().getKoulutusByOid(komotoOid);
            Preconditions.checkNotNull(koulutus);
            HakukohteetVastaus vastaus = this.presenter.getHakukohteetForKoulutus(komotoOid);//.getHakukohdeTulos();
            List<HakukohdePerustieto> hakukohteet = vastaus != null ? vastaus.getHakukohteet() : new ArrayList<HakukohdePerustieto>();
            
            lukioKoulutusConverter.loadLueKoulutusVastausTyyppiToModel(getPresenter().getModel(), koulutus, I18N.getLocale(), hakukohteet);
        } else {
            Preconditions.checkNotNull(getTarjontaModel().getTarjoajaModel().getSelectedOrganisationOid(), "Missing organisation OID.");   
            getPerustiedotModel().clearModel();
            getTarjontaModel().setKoulutusLukioKuvailevatTiedot(new KoulutusLukioKuvailevatTiedotViewModel());
        }
    }

    public void loadSelectedKomoData() {
        KoulutusLukioPerustiedotViewModel perustiedotModel = getPerustiedotModel();
        final KoulutuskoodiModel koulutuskoodi = perustiedotModel.getKoulutuskoodiModel();
        LukiolinjaModel lukiolinja = perustiedotModel.getLukiolinja();

        if (koulutuskoodi != null && koulutuskoodi.getKoodi() != null && lukiolinja != null && lukiolinja.getKoodi() != null) {
            perustiedotModel.getLukiolinjas().clear();
            KoulutusmoduuliKoosteTyyppi tyyppi = perustiedotModel.getQuickKomo(
                    koulutuskoodi.getKoodistoUriVersio(),
                    lukiolinja.getKoodistoUriVersio());

            if (tyyppi == null) {
                LOG.error("No tutkinto & koulutusohjelma result was null. Search by '" + koulutuskoodi.getKoodistoUriVersio() + "'" + " and '" + koulutuskoodi.getKoodistoUriVersio() + "'");
            }

            kolutusKoodistoConverter.listaaLukioSisalto(koulutuskoodi, lukiolinja, tyyppi, I18N.getLocale());
        }
    }

    /**
     * Load and convert Koodisto service data to human readable format. The data
     * is used in form combobox component.
     */
    public void loadLukiolinjas() {
        LOG.debug("in loadLukiolinjas");
        KoulutusLukioPerustiedotViewModel perustiedot = getPerustiedotModel();
        KoulutuskoodiModel koulutuskoodiModel = getPerustiedotModel().getKoulutuskoodiModel();

        //Select 'lukiolinja' from pre-filtered koodisto data.

        if (koulutuskoodiModel != null && koulutuskoodiModel.getKoodi() != null) {
            getPerustiedotModel().getLukiolinjas().clear();
            LOG.debug("Lukiolinjas list size : {}.", perustiedot.getKoulutuskoodiModel().getKoodistoUriVersio());
            List<KoulutusmoduuliKoosteTyyppi> tyyppis = perustiedot.getQuickKomosByKoulutuskoodiUri(perustiedot.getKoulutuskoodiModel().getKoodistoUriVersio());

            final List<LukiolinjaModel> lukiolinjas = kolutusKoodistoConverter.listaaLukiolinjas(tyyppis, I18N.getLocale());
            LOG.debug("Lukiolinjas list size : {}.", lukiolinjas);
            Collections.sort(lukiolinjas, new BeanComparator("nimi"));
            perustiedot.getLukiolinjas().addAll(lukiolinjas);
            //NO PARENT KOMOTO DATA FOR LUKIO
            //loadParentKomotoData(koulutuskoodiModel.getKoodistoUriVersio());
        } else {
            LOG.debug("No lukiolinja selected.");
        }
    }

    /**
     * Loading start date for created komoto from an existing relative komoto
     *
     * @param koulutuskoodi
     */
    /*private void loadParentKomotoData(String koulutuskoodi) {
       LOG.debug(koulutuskoodi);
        
        KoulutusTulos komoto = presenter.findKomotoByKoulutuskoodiPohjakoulutus(koulutuskoodi, null);
        if (komoto != null) {
            LueKoulutusKyselyTyyppi lueKysely = new LueKoulutusKyselyTyyppi();
            lueKysely.setOid(komoto.getKoulutus().getKomotoOid());
            LueKoulutusVastausTyyppi lueVastaus = this.tarjontaPublicService.lueKoulutus(lueKysely);
            //ALKAMISPAIVA NO LONGER IN PARENT
            //Date koulutuksenAlkuPvm = lueVastaus.getKoulutuksenAlkamisPaiva() != null ? lueVastaus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null;
            //this.getPerustiedotModel().setKoulutuksenAlkamisPvm(koulutuksenAlkuPvm);
        }
    }*/

    /**
     * Load and convert Koodisto service data to human readable format. The data
     * is used in form combobox component.
     */
    public void loadKoulutuskoodis() {
        LOG.debug("in loadKoulutuskoodis");
        HaeKaikkiKoulutusmoduulitKyselyTyyppi kysely = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);

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

        KoulutusLukioPerustiedotViewModel perusModel = getPerustiedotModel();
        perusModel.setKomos(komos);
        perusModel.createCacheKomos(); //cache komos to map object

        //koodisto service search result remapped to UI model objects.
        List<KoulutuskoodiModel> listaaKoulutuskoodit = kolutusKoodistoConverter.listaaKoulutukses(uris, I18N.getLocale());
        Collections.sort(listaaKoulutuskoodit, new BeanComparator("nimi"));

        perusModel.getKoulutuskoodis().clear();
        perusModel.getKoulutuskoodis().addAll(listaaKoulutuskoodit);
    }

    public void setKuvailevatTiedotView(EditLukioKoulutusKuvailevatTiedotView kuvailevatTiedotView) {
        this.kuvailevatTiedotView = kuvailevatTiedotView;
    }

    public void setEditKoulutusView(EditLukioKoulutusView editLukioKoulutusView) {
        this.editLukioKoulutusView = editLukioKoulutusView;
    }

    public void showLukioKoulutusEditView(Collection<OrganisaatioPerustieto> orgs) {
        showEditKoulutusView(null, KoulutusActiveTab.PERUSTIEDOT);
    }

    private TarjontaModel getTarjontaModel() {
        return getPresenter().getModel();
    }

    /**
     * @return the perustiedotModel
     */
    public KoulutusLukioPerustiedotViewModel getPerustiedotModel() {
        return getTarjontaModel().getKoulutusLukioPerustiedot();
    }

    /**
     * @return the kuvailevatTiedotModel
     */
    public KoulutusLukioKuvailevatTiedotViewModel getKuvailevatTiedotModel() {
        return getTarjontaModel().getKoulutusLukioKuvailevatTiedot();
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
    public EditLukioKoulutusPerustiedotView getPerustiedotView() {
        return perustiedotView;
    }

    /**
     * @param perustiedotView the perustiedotView to set
     */
    public void setPerustiedotView(EditLukioKoulutusPerustiedotView perustiedotView) {
        this.perustiedotView = perustiedotView;
    }

    /*
     * TODO checkExistingKomoto() fix this to support lukio. Now returns true always.
     */
    private boolean checkExistingKomoto(LisaaKoulutusTyyppi lisaaTyyppi) {
        LOG.error("checkExistingKomoto method is still disabled!");
//        TarkistaKoulutusKopiointiTyyppi kysely = new TarkistaKoulutusKopiointiTyyppi();
//        kysely.setKoulutusAlkamisPvm(lisaaTyyppi.getKoulutuksenAlkamisPaiva());
//        kysely.setKoulutusLuokitusKoodi(lisaaTyyppi.getKoulutusKoodi().getUri());
//        kysely.setLukiolinjaKoodi(lisaaTyyppi.getLukiolinjaKoodi().getUri());
//        kysely.setPohjakoulutus(lisaaTyyppi.getPohjakoulutusvaatimus().getUri());
//        kysely.setTarjoajaOid(lisaaTyyppi.getTarjoaja());
//        return tarjontaAdminService.tarkistaKoulutuksenKopiointi(kysely);
        return true;
    }
}
