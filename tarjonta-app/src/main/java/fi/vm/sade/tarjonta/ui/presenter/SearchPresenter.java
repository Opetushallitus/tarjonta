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
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
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
    private String text;
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;

    public SearchPresenter() {
        text = "";
    }

    @Override
    public List<SimpleAutocompleteTextField.IAutocompleteModel> searchAutocompleteText(String searchword) {
        LOG.error("Searchword : {}", searchword);

        //tarjontaPublicService.hae
        
        List<SimpleAutocompleteTextField.IAutocompleteModel> texts = Lists.<SimpleAutocompleteTextField.IAutocompleteModel>newArrayList();
        if (searchword == null) {
            return texts;
        }
        TutkintoohjelmaModel t1 = new TutkintoohjelmaModel();
        t1.setNimi("jotain 1");

        TutkintoohjelmaModel t2 = new TutkintoohjelmaModel();
        t2.setNimi("Uuno on numero 1");

        TutkintoohjelmaModel t3 = new TutkintoohjelmaModel();
        t3.setNimi("hackathon");

        TutkintoohjelmaModel t4 = new TutkintoohjelmaModel();
        t4.setNimi("Marilla oli lammas");

        texts.add(new KKAutocompleteModel(t1.getNimi(), t1));
        texts.add(new KKAutocompleteModel(t2.getNimi(), t2));
        texts.add(new KKAutocompleteModel(t3.getNimi(), t3));
        texts.add(new KKAutocompleteModel(t4.getNimi(), t4));

        List<SimpleAutocompleteTextField.IAutocompleteModel> lOut = Lists.<SimpleAutocompleteTextField.IAutocompleteModel>newArrayList();

        for (SimpleAutocompleteTextField.IAutocompleteModel s : texts) {
            if (s.getText().contains(searchword)) {
                lOut.add(s);
            }
        }

        LOG.error("Output {}", lOut);

        return lOut;
    }

    @Override
    public void clearAutocompleteTextField() {
        text = "";
    }
}
