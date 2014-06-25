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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HakusanaTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.LueKoulutusmoduuliKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusmoduuliVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.helper.conversion.UiModelBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KKAutocompleteModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import fi.vm.sade.tarjonta.ui.view.koulutus.SimpleAutocompleteTextField;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class SearchPresenter implements SimpleAutocompleteTextField.IAutocompleteSearch {

    private static transient final Logger LOG = LoggerFactory.getLogger(SearchPresenter.class);
    private String text = "";
    @Autowired(required = true)
    private TarjontaKoodistoHelper helper;
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;
    private UiModelBuilder<TutkintoohjelmaModel> uiModelBuilder;

    public SearchPresenter() {
    }

    @Override
    public List<SimpleAutocompleteTextField.IAutocompleteModel> searchAutocompleteText(final String searchWord) {
        LOG.error("Search word : {}", searchWord);
        //tarjontaPublicService.hae
        List<SimpleAutocompleteTextField.IAutocompleteModel> lOut = Lists.<SimpleAutocompleteTextField.IAutocompleteModel>newArrayList();

        if (searchWord == null || searchWord.isEmpty() || searchWord.length() < 2) {
            return lOut;
        }

        HaeKoulutusmoduulitKyselyTyyppi tyyppi = new HaeKoulutusmoduulitKyselyTyyppi();
        HakusanaTyyppi hakusanaTyyppi = new HakusanaTyyppi();
        hakusanaTyyppi.setHakusana(searchWord);
        tyyppi.setHakusana(hakusanaTyyppi);
        HaeKoulutusmoduulitVastausTyyppi haeKoulutusmoduulit = tarjontaPublicService.haeKoulutusmoduulit(tyyppi);

        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = haeKoulutusmoduulit.getKoulutusmoduuliTulos();
        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            lOut.add(model(tulos.getKoulutusmoduuli()));
        }

        LOG.error("Output count {}", lOut.size());

        return lOut;
    }

    @Override
    public void clearAutocompleteTextField() {
        text = "";
    }

    @Override
    public KKAutocompleteModel loadSelected(String oid) {
        LueKoulutusmoduuliKyselyTyyppi kysely = new LueKoulutusmoduuliKyselyTyyppi();
        kysely.setOid(oid);
        LueKoulutusmoduuliVastausTyyppi lueKoulutusmoduuli = tarjontaPublicService.lueKoulutusmoduuli(kysely);

        return model(lueKoulutusmoduuli.getKoulutusmoduuli());
    }

    private KKAutocompleteModel model(KoulutusmoduuliKoosteTyyppi m) {
        TutkintoohjelmaModel t = getUiModelBuilder().build(m.getNimi(), I18N.getLocale());
        return new KKAutocompleteModel(t);
    }

    private UiModelBuilder<TutkintoohjelmaModel> getUiModelBuilder() {
        if (uiModelBuilder == null) {
            uiModelBuilder = new UiModelBuilder<TutkintoohjelmaModel>(TutkintoohjelmaModel.class, helper);
        }

        return uiModelBuilder;
    }

    private TutkintoohjelmaModel modelTutkintoohjelmaModel(KoulutusmoduuliKoosteTyyppi m) {
        MonikielinenTekstiTyyppi tyyppi = m.getNimi();
        if (m.getKoulutusmoduulinNimi() != null && !m.getKoulutusmoduulinNimi().getTeksti().isEmpty()) {
            tyyppi = m.getKoulutusmoduulinNimi();
        }

        return getUiModelBuilder().build(tyyppi, I18N.getLocale());
    }

    public List<TutkintoohjelmaModel> searchKorkeakouluTutkintoohjelmas() {
        List<TutkintoohjelmaModel> tutkintoohjelmas = Lists.<TutkintoohjelmaModel>newArrayList();

        HaeKoulutusmoduulitKyselyTyyppi tyyppi = new HaeKoulutusmoduulitKyselyTyyppi();
        tyyppi.setKoulutustyyppi(KoulutusasteTyyppi.KORKEAKOULUTUS); //TODO : add koulutusaste
        HaeKoulutusmoduulitVastausTyyppi haeKoulutusmoduulit = tarjontaPublicService.haeKoulutusmoduulit(tyyppi);

        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = haeKoulutusmoduulit.getKoulutusmoduuliTulos();
        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            TutkintoohjelmaModel m = modelTutkintoohjelmaModel(tulos.getKoulutusmoduuli());
            Preconditions.checkNotNull(tulos.getKoulutusmoduuli().getOid(), "KOMO OID cannot be null.");
            tutkintoohjelmas.add(m);
        }
        LOG.error("Output count {}", tutkintoohjelmas.size());
        return tutkintoohjelmas;
    }
}
