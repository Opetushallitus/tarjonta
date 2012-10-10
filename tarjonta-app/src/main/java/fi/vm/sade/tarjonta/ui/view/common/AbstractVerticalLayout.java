
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

import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.constants.UiConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import fi.vm.sade.generic.common.I18N;
/**
 *
 * @author Tuomas Katva
 */
@Configurable(preConstruction = true)
public class AbstractVerticalLayout {

    protected static Logger LOG;
     @Value("${koodisto-uris.kieli:http://kieli}")
    private String _koodistoUriKieli;
     
     public AbstractVerticalLayout(Class clazz) {
         LOG = LoggerFactory.getLogger(clazz);
     }
     
     public enum Type {

        PCT_100, AUTOSIZE
    };
     
    protected String T(String key) {
        return I18N.getMessage(key);
    } 
    
     public String getKoodistoUriKieli() {
        return _koodistoUriKieli;
    }
}
