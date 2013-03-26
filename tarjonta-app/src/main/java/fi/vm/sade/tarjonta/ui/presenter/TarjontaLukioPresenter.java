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
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusKuvailevatTiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusPerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusView;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusLukioConverter;
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
public class TarjontaLukioPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaLukioPresenter.class);
    @Autowired(required = true)
    protected OIDService oidService;
    @Autowired(required = true)
    private TarjontaAdminService tarjontaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;
    @Autowired(required = true)
    private KoulutusLukioConverter lukioKoulutusConverter;
    @Autowired(required = true)
    private KoulutusKoodistoConverter kolutusKoodistoConverter;
    private TarjontaPresenter presenter;
    public EditLukioKoulutusPerustiedotView perustiedotView;
    private EditLukioKoulutusKuvailevatTiedotView kuvailevatTiedotView;
    private EditLukioKoulutusView editLukioKoulutusView;

    public TarjontaLukioPresenter() {
    }

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
        } else {
            LOG.debug("No lukiolinja selected.");
        }
    }

    public void loadKoulutuskoodis() {
        LOG.debug("in loadKoulutuskoodis");
        HaeKaikkiKoulutusmoduulitKyselyTyyppi kysely = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        HaeKaikkiKoulutusmoduulitVastausTyyppi allKomoParents = tarjontaPublicService.haeKaikkiKoulutusmoduulit(kysely);
        List<KoulutusmoduuliTulos> resultKomos = allKomoParents.getKoulutusmoduuliTulos();

        Set<String> uris = new HashSet<String>();
        List<KoulutusmoduuliKoosteTyyppi> komos = new ArrayList<KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliTulos komoParents : resultKomos) {
            komos.add(komoParents.getKoulutusmoduuli());
            uris.add(komoParents.getKoulutusmoduuli().getKoulutuskoodiUri());
        }

        KoulutusLukioPerustiedotViewModel perusModel = getPerustiedotModel();
        perusModel.setKomos(komos);
        perusModel.createCacheKomos(); //cache komos to map object
        perusModel.getKoulutuskoodis().clear();

        //koodisto service search result remapped to UI model objects.
        List<KoulutuskoodiModel> listaaKoulutuskoodit = kolutusKoodistoConverter.listaaKoulutukses(uris, I18N.getLocale());
        final String komotoOid = perusModel.getKomotoOid() != null ? perusModel.getKomotoOid() : null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("KOMOs found {}", komos.size());
            LOG.debug("listaaKoulutuskoodit found : {}", listaaKoulutuskoodit.size());
            LOG.debug("filterBasedOnOppilaitosTyyppi found : {}", getPresenter().filterBasedOnOppilaitosTyyppi(listaaKoulutuskoodit, komotoOid).size());
        }

        Collections.sort(listaaKoulutuskoodit, new BeanComparator("nimi"));
        getPerustiedotModel().getKoulutuskoodis().addAll(getPresenter().filterBasedOnOppilaitosTyyppi(listaaKoulutuskoodit, komotoOid));
    }

    public void saveKoulutus(SaveButtonState tila) throws ExceptionMessage {
        LOG.debug("in saveKoulutus, tila : {}", tila);
        this.editLukioKoulutusView.enableKuvailevatTiedotTab();
        this.kuvailevatTiedotView.getLisatiedotForm().reBuildTabsheet();

        KoulutusLukioPerustiedotViewModel perustiedot = getPerustiedotModel();

        if (perustiedot.isLoaded()) {
            LOG.debug("update");
            //update KOMOTO
            PaivitaKoulutusTyyppi paivita = lukioKoulutusConverter.createPaivitaLukioKoulutusTyyppi(getTarjontaModel(), perustiedot.getKomotoOid());
            paivita.setTila(tila.toTarjontaTila(perustiedot.getTila()));
            tarjontaAdminService.paivitaKoulutus(paivita);
        } else {
            LOG.debug("insert to organisations : {}", getTarjontaModel().getOrganisaatios());
            for (TarjontaModel.OrganisaatioOidNamePair pair : getTarjontaModel().getOrganisaatios()) {
                getTarjontaModel().setOrganisaatioName(pair.getName());
                getTarjontaModel().setOrganisaatioOid(pair.getOid());
                perustiedot.setOrganisaatioOid(pair.getOid());
                perustiedot.setOrganisaatioName(pair.getName());

                LisaaKoulutusTyyppi lisaa = lukioKoulutusConverter.createLisaaLukioKoulutusTyyppi(getTarjontaModel(), getPerustiedotModel().getOrganisaatioOid(), tila);
                LOG.debug("getKuvailevatTiedotModel() :  {}", getKuvailevatTiedotModel());

                checkKoulutusmoduuli();
                if (checkExistingKomoto(lisaa)) {
                    tarjontaAdminService.lisaaKoulutus(lisaa);
                    perustiedot.setDocumentStatus(DocumentStatus.SAVED);
                    perustiedot.setKomotoOid(lisaa.getOid());
                } else {
                    LOG.debug("Unable to add koulutus because of the duplicate");
                    throw new ExceptionMessage("EditKoulutusPerustiedotYhteystietoView.koulutusExistsMessage");
                }
            }
        }
    }

    public void checkKoulutusmoduuli() {

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

    public void showEditKoulutusView(final String komotoOid, final KoulutusActiveTab tab) {
        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView

        if (komotoOid != null) {
            readKoulutusToModel(komotoOid);
        } else {
            if (getTarjontaModel().getOrganisaatioOid() == null) {
                throw new RuntimeException("Application error - missing organisation OID.");
            }
            getPerustiedotModel().clearModel(DocumentStatus.NEW);
            getTarjontaModel().setKoulutusLukioKuvailevatTiedot(new KoulutusLukioKuvailevatTiedotViewModel(DocumentStatus.NEW));
        }
        getPerustiedotModel().setOrganisaatioOidTree(getPresenter().fetchOrganisaatioTree(getTarjontaModel().getOrganisaatioOid()));
        setEditKoulutusView(new EditLukioKoulutusView(komotoOid, tab));
        getPresenter().getRootView().changeView(editLukioKoulutusView);
    }

    private void readKoulutusToModel(final String koulutusOid) {
        Preconditions.checkNotNull(koulutusOid, "koulutusOid cannot be null");
        LueKoulutusVastausTyyppi koulutus = getPresenter().getKoulutusByOid(koulutusOid);
        lukioKoulutusConverter.loadLueKoulutusVastausTyyppiToModel(getPresenter().getModel(), koulutus, I18N.getLocale());
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

            //TODO: do we need the setters on bottom?
            perustiedotModel.setKoulutusaste(koulutuskoodi.getKoulutusaste());
            perustiedotModel.setKoulutusala(koulutuskoodi.getKoulutusala());
            perustiedotModel.setKoulutuslaji(null);//TODO!!!!!!!!!!!
        }
    }

    public void setKuvailevatTiedotView(EditLukioKoulutusKuvailevatTiedotView kuvailevatTiedotView) {
        this.kuvailevatTiedotView = kuvailevatTiedotView;
    }

    public void setEditKoulutusView(EditLukioKoulutusView editLukioKoulutusView) {
        this.editLukioKoulutusView = editLukioKoulutusView;
    }

    public void showLukioKoulutusEditView(Collection<OrganisaatioPerustietoType> orgs) {
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
     * TODO: fix this to support lukio.
     */
    private boolean checkExistingKomoto(LisaaKoulutusTyyppi lisaaTyyppi) {
        LOG.warn("checkExistingKomoto method is still disabled!");
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
