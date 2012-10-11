
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
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
/**
 *
 * @author Tuomas Katva
 */
public class PerustiedotView extends CustomComponent {
    
    private static final Logger LOG = LoggerFactory.getLogger(PerustiedotView.class);
    @Value("${koodisto-uris.kieli:http://kieli}")
    private String _koodistoUriKieli;
    
    //MainLayout element
    VerticalLayout mainLayout;
    GridLayout itemContainer;
    
    //Fields
    ComboBox hakukohteenNimiCombo;
    TextField tunnisteKoodiText;
    
    ComboBox hakuCombo;
    TextField aloitusPaikatText;
    KoodistoComponent hakuKelpoisuusVaatimuksetCombo;
    LanguageTabSheet valintaPerusteidenKuvausTabs;
    LanguageTabSheet lisatiedotTabs;
    
    //Info buttons
    Button upRightInfoButton;
    Button downRightInfoButton;
    
    public PerustiedotView() {
        super();
        buildMainLayout();
    }
    
    private void buildMainLayout() {
        mainLayout = new VerticalLayout();
        //Add top info button layout
        mainLayout.addComponent(buildInfoButtonLayout());        
        
        //Build main item container
        mainLayout.addComponent(buildGrid());
        
        //Add bottom addtional info text areas and info button
        mainLayout.addComponent(buildBottomAreaLanguageTab());
        setCompositionRoot(mainLayout);
    }
    
    private GridLayout buildGrid() {
        itemContainer = new GridLayout(2, 1);
        itemContainer.setWidth(UiConstant.PCT100);
        itemContainer.setSpacing(true);
        itemContainer.setMargin(false, true, true, true);
        
        addItemToGrid("PerustiedotView.hakukohteenNimi", buildHakukode());
        addItemToGrid("PerustiedotView.hakuValinta", buildHaku());
        //TODO, lisää pistemäärä informaatio.
        
        addItemToGrid("PerustiedotView.aloitusPaikat", buildAloitusPaikat());
        addItemToGrid("PerustiedotView.hakukelpoisuusVaatimukset", buildHakukelpoisuusVaatimukset());
        addItemToGrid("PerustiedotView.valintaperusteidenKuvaus", buildValintaPerusteet());
        
        itemContainer.setColumnExpandRatio(0, 0f);
        itemContainer.setColumnExpandRatio(1, 1f);
        
        return itemContainer;
    }
    
    private void addItemToGrid(String captionKey, AbstractComponent component) {
        
        if (itemContainer != null) {
            itemContainer.addComponent(UiUtil.label(null, T(captionKey)));
            itemContainer.addComponent(component);
            itemContainer.newLine();
        }
        
    }
    
    private KoodistoComponent buildHakukelpoisuusVaatimukset() {
        hakuKelpoisuusVaatimuksetCombo = WidgetFactory.create(_koodistoUriKieli);
        
        
        return hakuKelpoisuusVaatimuksetCombo;
    }
    
    private TextField buildAloitusPaikat() {
        aloitusPaikatText = UiUtil.textField(null);
        
        return aloitusPaikatText;
    }
    
    private ComboBox buildHaku() {
        
        hakukohteenNimiCombo = UiUtil.comboBox(null,null , null );
        
        return hakukohteenNimiCombo;
    }
    
    private LanguageTabSheet buildValintaPerusteet() {
        valintaPerusteidenKuvausTabs = buildLanguageTab();
        
        
        return valintaPerusteidenKuvausTabs;
    }
    
    private HorizontalLayout buildHakukode() {
        
        //TODO: Tunnistekoodit koodistosta, mistä tulee hakukohteen nimi
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        hakukohteenNimiCombo = UiUtil.comboBox(hl, null, new String[]{"Tunnistekoodi1", "Tunnistekoodi2", "Tunnistekoodi3"});
        tunnisteKoodiText = UiUtil.textField(hl, "",  T("tunnistekoodi"), true);
        
        

        hl.setExpandRatio(tunnisteKoodiText, 5l);
        hl.setComponentAlignment(tunnisteKoodiText, Alignment.TOP_LEFT);
        return hl;
    }
    
    private HorizontalLayout buildInfoButtonLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT_LEFT);
        upRightInfoButton = UiUtil.buttonSmallInfo(layout);
        layout.setComponentAlignment(upRightInfoButton, Alignment.TOP_RIGHT);
        return layout;
    }
    
    private VerticalLayout buildBottomAreaLanguageTab() {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label label = UiUtil.label(hl, T("PerustiedotView.lisatiedot"), LabelStyleEnum.H2);
        downRightInfoButton = UiUtil.buttonSmallInfo(hl);
        hl.setExpandRatio(label, 1l);
        hl.setExpandRatio(downRightInfoButton, 3l);
        hl.setComponentAlignment(label, Alignment.TOP_LEFT);
        hl.setComponentAlignment(downRightInfoButton, Alignment.TOP_RIGHT);
        vl.addComponent(hl);
        lisatiedotTabs = buildLanguageTab();
        vl.addComponent(lisatiedotTabs);
        return vl;
    }
    
    private LanguageTabSheet buildLanguageTab() {
        return new LanguageTabSheet(getKoodistoUriKieli());
    }
     
     private String getKoodistoUriKieli() {
         return _koodistoUriKieli;
     }
     
     private String T(String key) {
         return I18N.getMessage(key);
     }

}
