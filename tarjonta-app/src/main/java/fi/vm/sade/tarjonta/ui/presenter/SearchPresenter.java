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

import com.google.common.collect.Lists;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HakusanaTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConveter;
import fi.vm.sade.tarjonta.ui.helper.conversion.UiModelBuilder;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KKAutocompleteModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import fi.vm.sade.tarjonta.ui.view.koulutus.SimpleAutocompleteTextField;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
    private String text;
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;
    private UiModelBuilder<TutkintoohjelmaModel> uiModelBuilder;

    public SearchPresenter() {
        text = "";
        uiModelBuilder = new UiModelBuilder<TutkintoohjelmaModel>(TutkintoohjelmaModel.class);
    }

    @Override
    public List<SimpleAutocompleteTextField.IAutocompleteModel> searchAutocompleteText(final String searchWord) {
        LOG.error("Search word : {}", searchWord);
        //tarjontaPublicService.hae
        List<SimpleAutocompleteTextField.IAutocompleteModel> lOut = Lists.<SimpleAutocompleteTextField.IAutocompleteModel>newArrayList();

        if (searchWord == null || searchWord.isEmpty() || searchWord.length() < 2) {
            return lOut;
        }

        HaeKoulutusmoduulitKyselyTyyppi haeKoulutusmoduulitKyselyTyyppi = new HaeKoulutusmoduulitKyselyTyyppi();
        HakusanaTyyppi hakusanaTyyppi = new HakusanaTyyppi();
        hakusanaTyyppi.setHakusana(searchWord);
        haeKoulutusmoduulitKyselyTyyppi.setHakusana(hakusanaTyyppi);
        HaeKoulutusmoduulitVastausTyyppi haeKoulutusmoduulit = tarjontaPublicService.haeKoulutusmoduulit(haeKoulutusmoduulitKyselyTyyppi);

        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = haeKoulutusmoduulit.getKoulutusmoduuliTulos();
        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            KoulutusmoduuliKoosteTyyppi m = tulos.getKoulutusmoduuli();
            TutkintoohjelmaModel t = uiModelBuilder.build(m.getKoulutusmoduulinNimi(), I18N.getLocale());
            t.setKomoOid(m.getOid());
            t.setKomoParentOid(m.getParentOid());
            lOut.add(new KKAutocompleteModel(t));
        }

        LOG.error("Output count {}", lOut.size());

        return lOut;
    }

    @Override
    public void clearAutocompleteTextField() {
        text = "";
    }
}
