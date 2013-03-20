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
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolajiModel;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusPerustiedotView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class TarjontaLukioPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaLukioPresenter.class);
    private TarjontaModel model;
    private KoulutusLukioPerustiedotViewModel perustiedotModel;
    private KoulutusLukioKuvailevatTiedotViewModel kuvailevatTiedotModel;
    private TarjontaPresenter presenter;
    private EditLukioKoulutusPerustiedotView editLukioKoulutusPerustiedotView;

    public TarjontaLukioPresenter(TarjontaPresenter presenter) {
        this.model = presenter.getModel();
        this.presenter = presenter;

        perustiedotModel = this.model.getKoulutusLukioPerustiedot();
        kuvailevatTiedotModel = model.getKoulutusLukioKuvailevatTiedot();
    }

    public void loadLukiolajis() {
        LOG.debug("in loadLukiolajis");

        perustiedotModel.getLukiolajis().clear();

        LukiolajiModel l = new LukiolajiModel();
        l.setKielikoodi("fi");
        l.setKoodistoUri("uri");
        l.setKoodistoVersio(1);
        l.setKuvaus("LukiolajiModel");
        l.setNimi("LukiolajiModel");
        l.getKielikaannos().add(new KielikaannosViewModel("fi", "laji " + new Date()));
        perustiedotModel.getLukiolajis().add(l);
    }

    public void loadKoulutuskoodis() {
        LOG.debug("in loadKoulutuskoodis");

        //TODO : load komos
        KoodiModel c = new KoodiModel();
        c.setKielikoodi("fi");
        c.setKoodistoUri("uri");
        c.setKoodistoVersio(1);
        c.setKuvaus("kuvaus");
        c.setNimi("generic code");

        MonikielinenTekstiModel mtm = new MonikielinenTekstiModel();
        mtm.getKielikaannos().add(new KielikaannosViewModel("fi", "multilanguage"));

        KoulutuskoodiModel m = new KoulutuskoodiModel();
        m.setKielikoodi("fi");
        m.setKoodistoUri("uri");
        m.setKoodistoVersio(1);
        m.setKuvaus("KoulutuskoodiModel");
        m.setNimi("KoulutuskoodiModel");

        m.getKielikaannos().add(new KielikaannosViewModel("fi", "testi"));
        m.setKoulutusaste(c);
        m.setKoulutusala(c);
        m.setOpintojenLaajuusyksikko(c);
        m.setOpintojenLaajuus(c);
        m.setOpintoala(c);
        m.setKoulutuksenRakenne(mtm);
        m.setTavoitteet(mtm);
        m.setJatkoopintomahdollisuudet(mtm);

        //load komos to ui models
        perustiedotModel.getKoulutuskoodis().add(m);
    }

    public void saveKoulutus(SaveButtonState tila) throws ExceptionMessage {
        LOG.info("in saveKoulutus : {}", tila);
        LOG.info("model : {}", perustiedotModel.toString());
        LOG.info("yhteyshenkilo : {}", perustiedotModel.getYhteyshenkilo());
        LOG.info("kuvailevat tiedot model: {}", kuvailevatTiedotModel);
    }

    public void getReloadKoulutusListData() {
        presenter.getRootView().getListKoulutusView().reload();
    }

    public void showEditLukioKoulutusPerustiedotView(final String koulutusOid, final KoulutusActiveTab tab) {
        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
//        if (koulutusOid != null) {
//            readKoulutusToModel(koulutusOid);
//        } else {
        if (getModel().getOrganisaatioOid() == null) {
            throw new RuntimeException("Application error - missing organisation OID.");
        }
        perustiedotModel.clearModel(DocumentStatus.NEW);
        perustiedotModel.setOrganisaatioOidTree(presenter.fetchOrganisaatioTree(getModel().getOrganisaatioOid()));
//        }
        showEditLukioKoulutusPerustiedotView(koulutusOid);
    }

    /**
     * Open edit koulutus view.
     *
     * @param koulutusOid
     * @param tab
     */
    private void showEditLukioKoulutusPerustiedotView(final String koulutusOid) {
        editLukioKoulutusPerustiedotView = new EditLukioKoulutusPerustiedotView(koulutusOid);
        presenter.getRootView().changeView(editLukioKoulutusPerustiedotView);
    }

    public void setModel(TarjontaModel model) {
        this.model = model;
    }

    public TarjontaModel getModel() {
        return this.model;
    }
    
    
}
