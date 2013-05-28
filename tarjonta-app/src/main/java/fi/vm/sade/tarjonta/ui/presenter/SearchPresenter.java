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
import fi.vm.sade.tarjonta.ui.view.koulutus.SimpleAutocompleteTextField;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class SearchPresenter implements SimpleAutocompleteTextField.IAutocompleteSearch {

    private static transient final Logger LOG = LoggerFactory.getLogger(SearchPresenter.class);
    private String text;

    public SearchPresenter() {
        text = "";
    }

    @Override
    public List<String> searchAutocompleteText(String searchword) {
        LOG.error("Searchword : {}", searchword);

        List<String> texts = Lists.<String>newArrayList();
        if (searchword == null) {
            return texts;
        }

        texts.add("jotain 1");
        texts.add("Uuno on numero 1");
        texts.add("hackathon");
        texts.add("Marilla oli lammas");

        List<String> lOut = Lists.<String>newArrayList();

        for (String s : texts) {
            if (s.contains(searchword)) {
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
