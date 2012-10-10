
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
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TwinColSelect;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Tuomas Katva
 */
public class TwinColSelectKoodisto extends CssLayout  {

    private static final Logger LOG = LoggerFactory.getLogger(TwinColSelectKoodisto.class);
    private KoodistoComponent kc;
    private TwinColSelect tcs;
    private Set<String> languages = new HashSet<String>();

    public TwinColSelectKoodisto(String koodistoUri) {
        tcs = UiUtil.twinColSelect();
        initKoodisto(koodistoUri);

    }

    public void addListener(Property.ValueChangeListener listener) {
        tcs.addListener(listener);
    }

    public void dataSource(PropertysetItem psi, String expression) {
        // Selected data bound there
        if (psi != null && expression != null) {
            kc.setPropertyDataSource(psi.getItemProperty(expression));
        }
    }

    private void initKoodisto(String koodistoUri) {
        kc = WidgetFactory.create(koodistoUri);

        // Wire koodisto to selectable component
        kc.setField(tcs);

        // DISPLAYED text
        kc.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType kdto = (KoodiType) dto;
                    languages.add(kdto.getKoodiArvo());
                    return kdto.getKoodiArvo();
                } else {
                    LOG.warn("An unknown DTO : " + dto);
                    return "!KoodiDTO?: " + dto;
                }
            }
        });

        // BOUND value
        kc.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType kdto = (KoodiType) dto;
                    return kdto.getKoodiArvo();
                } else {
                    LOG.warn("An unknown DTO : " + dto);
                    return "" + dto;
                }
            }
        });
        addComponent(kc);
    }

    /**
     * @return the languages
     */
    public Set<String> getLanguages() {
        return languages;
    }
    
}
