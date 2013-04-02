package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.ui.*;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatioSelectDialog;
import fi.vm.sade.vaadin.util.UiUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final long serialVersionUID = 6240999779746262735L;
    
    private Logger logger = LoggerFactory.getLogger(UusiKoulutusDialog.class);
    private List<String> organisaatioOids;
    private ComboBox koulutusAsteCombo;
    private ComboBox koulutusValintaCombo;
    
    public UusiKoulutusDialog(String width, String height) {
        super(width, height);
        setCaption(_i18n.getMessage("dialog.title"));
        

    }
    
    @Override
    protected Collection<String> getOrganisaatioOids() {
        if (organisaatioOids == null) {
            organisaatioOids = new ArrayList<String>();
        }
        organisaatioOids.add(presenter.getNavigationOrganisation().getOrganisationOid());
        return organisaatioOids;
    }
    
    @Override
    protected void setButtonListeners() {
        peruutaBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getParent().getWindow().removeWindow(UusiKoulutusDialog.this);
            }
        });
        
        jatkaBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                 if (selectedOrgs == null || selectedOrgs.size() < 1) {
                     errorView.addError(_i18n.getMessage("valitseVahintaanYksiOrganisaatio"));
                     return;
                 }
                if (presenter.checkOrganisaatioOppilaitosTyyppimatches(selectedOrgs.values())) {
                    presenter.setAllSelectedOrganisaatios(selectedOrgs.values());
                    
                    if (koulutusAsteCombo.getValue() instanceof String && ((String) koulutusAsteCombo.getValue()).equals(KoulutusasteTyyppi.LUKIOKOULUTUS.value())) {
                        presenter.getLukioPresenter().showLukioKoulutusEditView(selectedOrgs.values());
                        logger.info("lukiokoulutus()");
                        getParent().removeWindow(UusiKoulutusDialog.this);
                    } else if (koulutusAsteCombo.getValue() instanceof String && ((String) koulutusAsteCombo.getValue()).equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value())) {
                        presenter.showKoulutusEditView(selectedOrgs.values());
                        logger.info("ammatillinen peruskoulutus()");
                        getParent().removeWindow(UusiKoulutusDialog.this);
                    } else {
                        showNotification("Ei toteutettu");
                    }
                } else {
                    addErrorMessage(_i18n.getMessage("oppilaitosTyyppiDoesNotMatch"));
                }
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
        topLayout.setSizeFull();
        return topLayout;
    }
    
    private AbstractLayout createComboLayout() {
        HorizontalLayout comboLayout = new HorizontalLayout();

        Label valitseKoulutusLbl = new Label(_i18n.getMessage("valitseKoulutusLbl"));
        comboLayout.addComponent(valitseKoulutusLbl);
        comboLayout.setComponentAlignment(valitseKoulutusLbl,Alignment.MIDDLE_LEFT);
        koulutusValintaCombo = buildKoulutusValintaCombo();
        koulutusAsteCombo = buildKoulutusAsteCombobox();

        
        
        comboLayout.addComponent(koulutusValintaCombo);

        comboLayout.setComponentAlignment(koulutusValintaCombo, Alignment.MIDDLE_CENTER);
        comboLayout.addComponent(koulutusAsteCombo);
        comboLayout.setComponentAlignment(koulutusAsteCombo, Alignment.MIDDLE_RIGHT);
        
        comboLayout.setMargin(false, true, true, true);

        comboLayout.setSizeFull();
        return comboLayout;
    }
    
    private ComboBox buildKoulutusValintaCombo() {
        ComboBox koulutusValintaTmp = new ComboBox();
        
        koulutusValintaTmp.addItem("Koulutus");
        koulutusValintaTmp.select("Koulutus");
        koulutusValintaTmp.setNullSelectionAllowed(false);
        
        return koulutusValintaTmp;
    }
    
    private ComboBox buildKoulutusAsteCombobox() {
        ComboBox koulutusCombo = UiUtil.comboBox(null, null, null);
        for (KoulutusasteTyyppi aste : KoulutusasteTyyppi.values()) {
            koulutusCombo.addItem(aste.value());
            koulutusCombo.setItemCaption(aste.value(), _i18n.getMessage(aste.value()));
        }
        return koulutusCombo;
    }
    
    private AbstractLayout createLabelLayout() {
        GridLayout labelLayout = new GridLayout(2, 1);
        labelLayout.setColumnExpandRatio(0, 10);
        labelLayout.setColumnExpandRatio(1, 0.1f);
        labelLayout.setMargin(false, true, false, true);

        //HorizontalLayout labelLayout = new HorizontalLayout();
        Label ohjeteksti = new Label(_i18n.getMessage("dialog.ohjeTeksti"));
        //labelLayout.addComponent(ohjeteksti);
        labelLayout.addComponent(ohjeteksti, 0, 0);
        Button ohjeBtn = UiUtil.buttonSmallInfo(null);
        labelLayout.addComponent(ohjeBtn, 1, 0);
        labelLayout.setSizeFull();
        
        return labelLayout;
    }
}
