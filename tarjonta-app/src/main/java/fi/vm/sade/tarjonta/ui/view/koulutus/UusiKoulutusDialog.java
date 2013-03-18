package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatioSelectDialog;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Configurable;

/*
 *
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
/**
 *
 * @author Tuomas Katva
 */
@Configurable(preConstruction = true)
public class UusiKoulutusDialog extends OrganisaatioSelectDialog {

    List<String> organisaatioOids;
    private ComboBox koulutusAsteCombo;
    private ComboBox koulutusValintaCombo;
    
    
    public UusiKoulutusDialog(String width, String height, List<String> orgnasaatioOids) {
        super(width,height);
        setCaption(_i18n.getMessage("dialog.title"));
        this.organisaatioOids = orgnasaatioOids;
        
    }

    @Override
    protected Collection<String> getOrganisaatioOids() {
        return organisaatioOids;
    }

    @Override
    protected void setButtonListeners() {
        peruutaBtn.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                getParent().getWindow().removeWindow(UusiKoulutusDialog.this);
            }
        });
    }
    
    
     
    @Override
    protected VerticalLayout buildTopLayout() {
        VerticalLayout topLayout = new VerticalLayout();
        errorView = new ErrorMessage();
        topLayout.addComponent(errorView);
        topLayout.addComponent(createLabelLayout());
        topLayout.addComponent(createComboLayout());
        
        return topLayout;
    }
    
    private AbstractLayout createComboLayout() {
        HorizontalLayout comboLayout = new HorizontalLayout();
        
        koulutusValintaCombo = buildKoulutusValintaCombo();
        koulutusAsteCombo = buildKoulutusAsteCombobox();
                
        comboLayout.addComponent(koulutusValintaCombo);
        comboLayout.addComponent(koulutusAsteCombo);
        
        return comboLayout;
    }
    
    private ComboBox buildKoulutusValintaCombo() {
        ComboBox koulutusValintaTmp = new ComboBox();
        
        koulutusValintaTmp.addItem("Koulutus");
        
        return koulutusValintaTmp;
    } 
    
    private ComboBox buildKoulutusAsteCombobox() {
        ComboBox koulutusCombo = UiUtil.comboBox(null, null, null);
        String[] koulutusAsteValues = new String[KoulutusasteTyyppi.values().length];
        for(KoulutusasteTyyppi aste : KoulutusasteTyyppi.values()) {
          koulutusCombo.addItem(aste.value());
          koulutusCombo.setItemCaption(aste.value(), _i18n.getMessage(aste.value()));
        }
        return koulutusCombo; 
    }
    
    private AbstractLayout createLabelLayout() {
        GridLayout labelLayout = new GridLayout(2, 1);
        labelLayout.setColumnExpandRatio(0, 10);
        labelLayout.setColumnExpandRatio(1, 0.1f);
        labelLayout.setMargin(true,true,true,true);
        
        Label ohjeteksti = new Label(_i18n.getMessage("dialog.ohjeTeksti"));
        labelLayout.addComponent(ohjeteksti, 0, 0);
        Button ohjeBtn = UiUtil.buttonSmallInfo(null);
        labelLayout.addComponent(ohjeBtn,1,0);
        
        
        return labelLayout;
    }
    
 
  
}
