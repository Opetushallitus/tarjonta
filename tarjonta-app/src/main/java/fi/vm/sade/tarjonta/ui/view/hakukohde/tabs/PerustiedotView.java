
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

package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.LanguageTabSheet;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import com.vaadin.ui.CustomComponent;
import fi.vm.sade.generic.common.I18N;
import org.springframework.beans.factory.annotation.Value;
/**
 *
 * @author Tuomas Katva
 */
public class PerustiedotView extends CustomComponent {
    
    private static final Logger LOG = LoggerFactory.getLogger(PerustiedotView.class);
    @Value("${koodisto-uris.kieli:http://kieli}")
    private String _koodistoUriKieli;
    
    //Layout elements
    HorizontalLayout hl;
    
    //Fields
    
    
    
    public PerustiedotView() {
        super();
    }
    
    private void buildHakukode() {
        
        //TODO: Tunnistekoodit koodistosta, mistä tulee hakukohteen nimi
        hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        ComboBox comboBox = UiUtil.comboBox(hl, null, new String[]{"Tunnistekoodi1", "Tunnistekoodi2", "Tunnistekoodi3"});
        TextField textField = UiUtil.textField(hl, "",  T("tunnistekoodi"), true);
        

        hl.setExpandRatio(textField, 5l);
        hl.setComponentAlignment(textField, Alignment.TOP_LEFT);
        
    }
    
    protected void buildLayout(VerticalLayout t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
     private TabSheet buildLanguageTab() {
        return new LanguageTabSheet(getKoodistoUriKieli());
    }
     
     private String getKoodistoUriKieli() {
         return _koodistoUriKieli;
     }
     
     private String T(String key) {
         return I18N.getMessage(key);
     }

}
