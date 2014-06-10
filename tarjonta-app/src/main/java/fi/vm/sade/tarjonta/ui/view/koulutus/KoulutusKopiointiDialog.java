package fi.vm.sade.tarjonta.ui.view.koulutus;/*
*
* Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
*
* This program is free software: Licensed under the EUPL, Version 1.1 or - as
* soon as they will be approved by the European Commission - subsequent versions
* of the EUPL (the "Licence");
*
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* European Union Public Licence for more details.
*/

import com.vaadin.ui.*;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.service.UserContext;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatioSelectDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.*;

/**
* @author: Tuomas Katva
* Date: 4.3.2013
*/
@Configurable(preConstruction = true)
public class KoulutusKopiointiDialog extends OrganisaatioSelectDialog {
    private static final long serialVersionUID = 5989896711603394196L;


    @Autowired(required = true)
    private UserContext userContext;



    private KoodistoComponent kcPohjakoulutusvaatimus;

    private OptionGroup optionGroup;

    public KoulutusKopiointiDialog(String width,String height, KoulutusasteTyyppi tyyppi) {
        super(width,height,tyyppi);
        koulutusTyyppi = tyyppi;

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

        if (koulutusTyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            HorizontalLayout pkVaatimusLayout = new HorizontalLayout();

            Label caption = new Label(_i18n.getMessage("valitsePohjakoulutus"));
            pkVaatimusLayout.addComponent(caption);
            kcPohjakoulutusvaatimus = buildKoodistoCombobox(KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI);

            pkVaatimusLayout.addComponent(kcPohjakoulutusvaatimus);

            pkVaatimusLayout.setMargin(true,true,true,true);
            topLayout.addComponent(pkVaatimusLayout);

        }

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
                if (optionGroup.getValue() != null) {
                    String value = (String) optionGroup.getValue();
                    if (value.equalsIgnoreCase(_i18n
                            .getMessage("optionGroup.kopioidaan"))) {
                        if (selectedOrgs.values() != null
                                && selectedOrgs.values().size() > 0) {

                            // if
                            // (presenter.checkOrganisaatiosKoulutukses(selectedOrgs.values()))
                            // {
                            switch (koulutusTyyppi) {

                            case AMMATILLINEN_PERUSKOULUTUS:
                                String pkVaatimus = (String) kcPohjakoulutusvaatimus
                                        .getValue();
                                if (pkVaatimus != null) {
                                    presenter.copyKoulutusToOrganizations(
                                            selectedOrgs.values(), pkVaatimus);
                                } else {
                                    addErrorMessage(_i18n
                                            .getMessage("valitsePohjakoulutus"));
                                    return;
                                }
                                break;
                            case LUKIOKOULUTUS:
                                presenter
                                        .copyLukioKoulutusToOrganization(selectedOrgs
                                                .values());
                                break;

                            default:
                                presenter.copyKoulutusToOrganizations(
                                        selectedOrgs.values(), null);

                                break;
                            }

                            getParent().removeWindow(
                                    KoulutusKopiointiDialog.this);
                            /*
                             * } else { addErrorMessage(_i18n.getMessage(
                             * "koulutusOrgMismatch")); }
                             */
                        } else {
                            addErrorMessage(_i18n
                                    .getMessage("valitseOrganisaatioMessage"));
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

    private KoodistoComponent buildKoodistoCombobox(String koodistoUri) {
        return uiBuilder.koodistoComboBox(null, koodistoUri, null);//KoodistoURI.KOODISTO_TARJONTA_KOULUTUSASTE,null);
    }
}