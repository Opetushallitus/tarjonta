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
import fi.vm.sade.tarjonta.ui.helper.conversion.Koulutus2asteConverter;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusKuvailevatTiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusPerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusView;

import java.util.Collection;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
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
import fi.vm.sade.tarjonta.service.types.TarkistaKoulutusKopiointiTyyppi;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusLukioConverter;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
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
    private EditLukioKoulutusPerustiedotView editLukioKoulutusPerustiedotView;
    private EditLukioKoulutusKuvailevatTiedotView editLukioKoulutusKuvailevatTiedotView;
    private EditLukioKoulutusKuvailevatTiedotView kuvailevatTiedotView;
    private EditLukioKoulutusView editLukioKoulutusView;

    public TarjontaLukioPresenter() {
    }

    public void loadLukiolajis() {
        LOG.debug("in loadLukiolajis");

        getPerustiedotModel().getLukiolinjas().clear();

        LukiolinjaModel l = new LukiolinjaModel();
        l.setKielikoodi("fi");
        l.setKoodistoUri("uri");
        l.setKoodistoVersio(1);
        l.setKuvaus("LukiolajiModel");
        l.setNimi("LukiolajiModel");
        l.getKielikaannos().add(new KielikaannosViewModel("fi", "laji " + new Date()));
        getPerustiedotModel().getLukiolinjas().add(l);
    }

    public void loadKoulutuskoodis() {
        HaeKaikkiKoulutusmoduulitKyselyTyyppi kysely = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutustyyppi(KoulutusasteTyyppi.LUKIOKOULUTUS);
        HaeKaikkiKoulutusmoduulitVastausTyyppi allKomos = tarjontaPublicService.haeKaikkiKoulutusmoduulit(kysely);
        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = allKomos.getKoulutusmoduuliTulos();

        Set<String> uris = new HashSet<String>();
        List<KoulutusmoduuliKoosteTyyppi> komos = new ArrayList<KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            komos.add(tulos.getKoulutusmoduuli());
            uris.add(tulos.getKoulutusmoduuli().getKoulutuskoodiUri());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("KOMOs found {}", komos.size());
        }

        getPerustiedotModel().setKomos(komos);

        //koodisto service search result remapped to UI model objects.
        List<KoulutuskoodiModel> listaaKoulutuskoodit = kolutusKoodistoConverter.listaaKoulutukset(uris, I18N.getLocale());

        Collections.sort(listaaKoulutuskoodit, new BeanComparator("nimi"));
        getPerustiedotModel().getKoulutuskoodis().addAll(getPresenter().filterBasedOnOppilaitosTyyppi(listaaKoulutuskoodit));
    }

    public void saveKoulutus(SaveButtonState tila) throws ExceptionMessage {
        LOG.info("in saveKoulutus : {}", tila);
        LOG.info("model : {}", getPerustiedotModel().toString());
        LOG.info("yhteyshenkilo : {}", getPerustiedotModel().getYhteyshenkilo());
        LOG.info("kuvailevat tiedot model: {}", getKuvailevatTiedotModel());
        this.editLukioKoulutusView.enableKuvailevatTiedotTab();
        this.kuvailevatTiedotView.getLisatiedotForm().reBuildTabsheet();


        KoulutusToisenAsteenPerustiedotViewModel koulutusModel = getTarjontaModel().getKoulutusPerustiedotModel();

        if (koulutusModel.isLoaded()) {
            //update KOMOTO
            PaivitaKoulutusTyyppi paivita = lukioKoulutusConverter.createPaivitaLukioKoulutusTyyppi(getTarjontaModel(), null);
            paivita.setTila(tila.toTarjontaTila(koulutusModel.getTila()));
            tarjontaAdminService.paivitaKoulutus(paivita);
        } else {
            for (TarjontaModel.OrganisaatioOidNamePair pair : getTarjontaModel().getOrganisaatios()) {
                getTarjontaModel().setOrganisaatioName(pair.getName());
                getTarjontaModel().setOrganisaatioOid(pair.getOid());
                persistKoulutus(koulutusModel, tila);
            }
        }
//
//        this.editKoulutusView.enableLisatiedotTab();
//        this.lisatiedotView.getEditKoulutusLisatiedotForm().reBuildTabsheet();

    }

    private void persistKoulutus(KoulutusToisenAsteenPerustiedotViewModel koulutusModel, SaveButtonState tila) throws ExceptionMessage {
        //persist new KOMO and KOMOTO
        koulutusModel.setOrganisaatioOid(getTarjontaModel().getOrganisaatioOid());
        koulutusModel.setOrganisaatioName(getTarjontaModel().getOrganisaatioName());

        LisaaKoulutusTyyppi lisaa = lukioKoulutusConverter.createLisaaLukioKoulutusTyyppi(getTarjontaModel(), getPerustiedotModel().getOrganisaatioOid());
        lisaa.setTila(tila.toTarjontaTila(koulutusModel.getTila()));

        checkKoulutusmoduuli();
        if (checkExistingKomoto(lisaa)) {
            tarjontaAdminService.lisaaKoulutus(lisaa);
            koulutusModel.setDocumentStatus(DocumentStatus.SAVED);
            koulutusModel.setOid(lisaa.getOid());
        } else {

            LOG.debug("Unable to add koulutus because of the duplicate");
            throw new ExceptionMessage("EditKoulutusPerustiedotYhteystietoView.koulutusExistsMessage");
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

    /*
     * TODO: fix this to support lukio.
     */
    private boolean checkExistingKomoto(LisaaKoulutusTyyppi lisaaTyyppi) {
//        TarkistaKoulutusKopiointiTyyppi kysely = new TarkistaKoulutusKopiointiTyyppi();
//        kysely.setKoulutusAlkamisPvm(lisaaTyyppi.getKoulutuksenAlkamisPaiva());
//        kysely.setKoulutusLuokitusKoodi(lisaaTyyppi.getKoulutusKoodi().getUri());
//        kysely.setLukiolinjaKoodi(lisaaTyyppi.getLukiolinjaKoodi().getUri());
//        kysely.setPohjakoulutus(lisaaTyyppi.getPohjakoulutusvaatimus().getUri());
// 
//        kysely.setTarjoajaOid(lisaaTyyppi.getTarjoaja());
//
//        return tarjontaAdminService.tarkistaKoulutuksenKopiointi(kysely);
        return true;

    }

    public void getReloadKoulutusListData() {
        getPresenter().getRootView().getListKoulutusView().reload();
    }

    public void showEditLukioKoulutusPerustiedotView(final String koulutusOid, final KoulutusActiveTab tab) {
        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
        if (koulutusOid != null) {
            readKoulutusToModel(koulutusOid);
        } else {
            if (getTarjontaModel().getOrganisaatioOid() == null) {
                throw new RuntimeException("Application error - missing organisation OID.");
            }
            getPerustiedotModel().clearModel(DocumentStatus.NEW);
            getPerustiedotModel().setOrganisaatioOidTree(getPresenter().fetchOrganisaatioTree(getTarjontaModel().getOrganisaatioOid()));
        }
        showEditLukioKoulutusPerustiedotView(koulutusOid);
    }

    private void readKoulutusToModel(String koulutusOid) {
        Preconditions.checkNotNull(koulutusOid, "koulutusOid cannot be null");
        LueKoulutusVastausTyyppi koulutus = getPresenter().getKoulutusByOid(koulutusOid);
        //perustiedot TODO

        //kuvailevattiedot
         getTarjontaModel().setKoulutusLukioKuvailevatTiedot(KoulutusLukioConverter.createKoulutusLukioKuvailevatTiedotViewModel(koulutus, DocumentStatus.LOADED));
    }

    /**
     * Open edit koulutus view.
     *
     * @param koulutusOid
     * @param tab
     */
    private void showEditLukioKoulutusPerustiedotView(final String koulutusOid) {
        editLukioKoulutusPerustiedotView = new EditLukioKoulutusPerustiedotView(koulutusOid);
        getPresenter().getRootView().changeView(editLukioKoulutusPerustiedotView);
    }

    public void showEditLukioKoulutusKuvailevatTiedotView(final String koulutusOid, final KoulutusActiveTab tab) {
        if (getTarjontaModel().getOrganisaatioOid() == null) {
            throw new RuntimeException("Application error - missing organisation OID.");
        }
        getKuvailevatTiedotModel().clearModel(DocumentStatus.NEW);
        getPerustiedotModel().setOrganisaatioOidTree(getPresenter().fetchOrganisaatioTree(getTarjontaModel().getOrganisaatioOid()));
        showEditLukioKoulutusPerustiedotView(koulutusOid);
    }

    private void showEditLukioKoulutusKuvailevatTiedotView(final String koulutusOid) {
        editLukioKoulutusKuvailevatTiedotView = new EditLukioKoulutusKuvailevatTiedotView(koulutusOid);
        getPresenter().getRootView().changeView(editLukioKoulutusKuvailevatTiedotView);
    }

    public void setKuvailevatTiedotView(EditLukioKoulutusKuvailevatTiedotView kuvailevatTiedotView) {
        this.kuvailevatTiedotView = kuvailevatTiedotView;
    }

    public void setEditKoulutusView(EditLukioKoulutusView editLukioKoulutusView) {
        this.editLukioKoulutusView = editLukioKoulutusView;

    }

    public void showLukioKoulutusEditView(Collection<OrganisaatioPerustietoType> orgs) {
        getTarjontaModel().setOrganisaatios(getPresenter().convertPerustietoToNameOidPair(orgs));
        showLukioKoulutustEditView(null, KoulutusActiveTab.PERUSTIEDOT);
    }

    public void showLukioKoulutustEditView(final String koulutusOid, final KoulutusActiveTab tab) {
        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
        if (koulutusOid != null) {
            readKoulutusToModel(koulutusOid);
        } else {
            Preconditions.checkNotNull(getTarjontaModel().getOrganisaatioOid(), "Application error - missing organisation OID.");
            getTarjontaModel().getKoulutusLukioPerustiedot().clearModel(DocumentStatus.NEW);
            getTarjontaModel().getKoulutusLukioKuvailevatTiedot().clearModel(DocumentStatus.NEW);
        }
        showEditLukioKoulutusView(koulutusOid, tab);
    }

    private void showEditLukioKoulutusView(final String koulutusOid, final KoulutusActiveTab tab) {
        setEditKoulutusView(new EditLukioKoulutusView(koulutusOid, tab));
        getPresenter().getRootView().changeView(editLukioKoulutusView);
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
}
