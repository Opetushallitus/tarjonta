package fi.vm.sade.tarjonta.ui.view.koulutus;/*
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

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.UserContext;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatioSelectDialog;
import fi.vm.sade.tarjonta.ui.view.common.SelectableItem;
import fi.vm.sade.tarjonta.ui.view.common.SelectableItemContainer;
import fi.vm.sade.tarjonta.ui.view.common.SelectableItemListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import fi.vm.sade.vaadin.util.UiUtil;

import java.util.*;

/**
 * @author: Tuomas Katva
 * Date: 4.3.2013
 */
@Configurable(preConstruction =  true)
public class KoulutusKopiointiDialog extends OrganisaatioSelectDialog {

    
    @Autowired(required = true)
    private UserContext userContext;

    
    
    private OptionGroup optionGroup;
    
    public KoulutusKopiointiDialog(String width,String height) {
        super(width,height);
        
        setCaption(_i18n.getMessage("dialog.title"));
        
    }

    @Override
    protected Collection<String> getOrganisaatioOids() {
       return userContext.getUserOrganisations();
    }

    @Override
    protected VerticalLayout buildTopLayout() {
        VerticalLayout topLayout = new VerticalLayout();
        errorView = new ErrorMessage();
        topLayout.addComponent(errorView);
        VerticalLayout labelLayout = new VerticalLayout();
        Label ohjeTekstiLbl = new Label(_i18n.getMessage("dialog.ohjeteksti"));
        labelLayout.addComponent(ohjeTekstiLbl);
        labelLayout.setMargin(true,true,true,true);
        topLayout.addComponent(labelLayout);



        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(true,true,true,true);
        Label optionGroupLbl = new Label(_i18n.getMessage("optionGroup.caption"));
        horizontalLayout.addComponent(optionGroupLbl);
        optionGroup = new OptionGroup();
        optionGroup.addItem(_i18n.getMessage("optionGroup.kopioidaan"));
        optionGroup.addItem(_i18n.getMessage("optionGroup.siirretaan"));

        horizontalLayout.addComponent(optionGroup);
        topLayout.addComponent(horizontalLayout);

        Label orgTreeTableLabel = new Label(_i18n.getMessage("organisaatioTree.label"));
        VerticalLayout orgLabelLayout = new VerticalLayout();
        orgLabelLayout.setMargin(false, false, false, true);
        orgLabelLayout.addComponent(orgTreeTableLabel);
        topLayout.addComponent(orgLabelLayout);
        topLayout.setSizeFull();
        return topLayout;
    }
    
    @Override
    protected void setButtonListeners() {
        jatkaBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                errorView.resetErrors();
                if (optionGroup.getValue() !=  null) {
                String value =  (String)optionGroup.getValue();
                if (value.equalsIgnoreCase(_i18n.getMessage("optionGroup.kopioidaan"))) {
                if (selectedOrgs.values() != null && selectedOrgs.values().size() > 0) {
                    if (presenter.checkOrganisaatiosKoulutukses(selectedOrgs.values())) {
                    presenter.copyKoulutusToOrganizations(selectedOrgs.values());
                    getParent().removeWindow(KoulutusKopiointiDialog.this);
                    } else {
                      addErrorMessage(_i18n.getMessage("koulutusOrgMismatch"));
                    }
                } else {
                    addErrorMessage(_i18n.getMessage("valitseOrganisaatioMessage"));
                }
                } else {
                    addErrorMessage("Vain kopiointi toteutettu");
                }
                } else {
                    addErrorMessage(_i18n.getMessage("valitseToiminto"));
                }
            }
        });
        
        peruutaBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
               getParent().removeWindow(KoulutusKopiointiDialog.this);
            }
        });
    }

    
}
