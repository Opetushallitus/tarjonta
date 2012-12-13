
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

package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.data.Property;
import com.vaadin.ui.CssLayout;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.mock.KoodiServiceMock;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
/**
 *
 * @author Tuomas Katva
 */
@Configurable
public class TwinColSelectKoodisto extends CssLayout  {

    private static final Logger LOG = LoggerFactory.getLogger(TwinColSelectKoodisto.class);
    private KoodistoComponent kc;

    private Set<String> languages = new HashSet<String>();

    @Autowired
    private KoodiService koodiService;
    
       @Autowired(required = true)
    private UiBuilder uiBuilder;

    public TwinColSelectKoodisto() {

        initKoodisto();

    }

    public void addListener(Property.ValueChangeListener listener) {
        kc.addListener(listener);
    }

   public void removeListener(Property.ValueChangeListener listener) {
       kc.removeListener(listener);
   }

    private void initKoodisto() {
        kc = uiBuilder.koodistoTwinColSelectUri(null, KoodistoURIHelper.KOODISTO_KIELI_URI);

//        kc.setImmediate(true);

//
        // DISPLAYED text
        kc.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType kdto = (KoodiType) dto;
                    String arvo = tryToGetLocalisedValue(kdto);

                    return arvo;
                } else {
                    LOG.warn("An unknown DTO : " + dto);
                    return "!KoodiDTO?: " + dto;
                }
            }
        });
//
//
//        // BOUND value
        kc.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType kdto = (KoodiType) dto;
                    return kdto.getKoodiUri();
                } else {
                    LOG.warn("An unknown DTO : " + dto);
                    return "" + dto;
                }
            }
        });
        addComponent(kc);
    }

    private String tryToGetLocalisedValue(KoodiType kdto) {
        if (I18N.getLocale() != null) {
                    String kieliArvo = null;

                    for (KoodiMetadataType kmt : kdto.getMetadata()) {
                        if (kmt.getKieli().value().equalsIgnoreCase(I18N.getLocale().getLanguage())) {
                            kieliArvo = kmt.getNimi();

                        }
                    }

                    if (kieliArvo != null ) {
                        languages.add(kdto.getKoodiUri());

                        return kieliArvo;
                    } else {
                        languages.add(kdto.getKoodiArvo());
                    }

                    } else {
                   languages.add(kdto.getKoodiArvo());
                    }
                    return kdto.getKoodiArvo();
    }

    public void setValue(Set<String> uris) {
        kc.setValue(Collections.unmodifiableSet(uris));
    }
    //Try to get localised name for uri
    public String getCaptionFor(String uri) {

        // List<KoodiType> koodit = kc.getKoodiService().searchKoodis(KoodiServiceSearchCriteriaBuilder.latestAcceptedKoodiByUri(uri));
        List<KoodiType> koodit = koodiService.searchKoodis(KoodiServiceSearchCriteriaBuilder.latestAcceptedKoodiByUri(uri));
        if (koodit != null && koodit.size() > 0) {
            return tryToGetLocalisedValue(koodit.get(0));
        }  else {
            return null;
        }
    }

    /**
     * @return the languages
     */
    public Set<String> getLanguages() {
        return languages;
    }

}
