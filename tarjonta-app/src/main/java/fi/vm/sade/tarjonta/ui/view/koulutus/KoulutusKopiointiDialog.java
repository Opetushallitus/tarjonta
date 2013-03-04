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
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author: Tuomas Katva
 * Date: 4.3.2013
 */
@Configurable
public class KoulutusKopiointiDialog extends Window {

    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    private transient I18NHelper _i18n;
    private OptionGroup optionGroup;
    private TreeTable organisaatioChildTree;
    private static final String CHILD_TREE_PROPERTY = "childOrganisaatioButton";

    public KoulutusKopiointiDialog() {
        super();
        _i18n = new I18NHelper(this);
        setContent(buildMainLayout());
        setModal(true);
        setCaption(_i18n.getMessage("dialog.title"));
        buildMainLayout();
    }

    private VerticalLayout buildMainLayout() {
       VerticalLayout mainLayout = new VerticalLayout();
       mainLayout.addComponent(buildTopLayout());
       mainLayout.addComponent(buildBottomLayout());
       return mainLayout;
    }

    private VerticalLayout buildTopLayout() {
        VerticalLayout topLayout = new VerticalLayout();

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
        orgLabelLayout.setMargin(false,false,false,true);
        orgLabelLayout.addComponent(orgTreeTableLabel);
        topLayout.addComponent(orgLabelLayout);

        return topLayout;
    }

    private HorizontalLayout buildBottomLayout() {
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setMargin(false,false,true,false);
        Label label = new Label("Tahan tulee organisaatiolistaus");

        bottomLayout.addComponent(label);

        VerticalLayout verticalLayout = new VerticalLayout();
        Label anotherLabel = new Label("Tahan tulee valitut organisaatiot");
        verticalLayout.addComponent(anotherLabel);

        return bottomLayout;
    }

    private TreeTable buildOrganisaatioTree(List<OrganisaatioPerustietoType> organisaatios) {
        organisaatioChildTree = new TreeTable();
        organisaatioChildTree.setColumnHeaderMode(TreeTable.COLUMN_HEADER_MODE_HIDDEN);
        organisaatioChildTree.addContainerProperty(CHILD_TREE_PROPERTY, Button.class, null);
        organisaatioChildTree.setWidth("100%");

        return organisaatioChildTree;
    }

    public void addElementsToTree(List<OrganisaatioPerustietoType> organisaatios) {

        if (organisaatioChildTree != null) {
        for (final OrganisaatioPerustietoType curOrg:organisaatios) {

            Button buttonOrganisaatio = UiUtil.buttonLink(null, getAvailableNameBasic(curOrg), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {

                }
            });

            organisaatioChildTree.addItem(curOrg);
            Property prop = organisaatioChildTree.getContainerProperty(curOrg, CHILD_TREE_PROPERTY);
            if (prop != null) {
                prop.setValue(buttonOrganisaatio);
            }
        }
        createHierarchy(organisaatios);
        }
    }

    private void createHierarchy(List<OrganisaatioPerustietoType> organisaatios) {
        HashMap<String, String> childParent = new HashMap<String, String>();
        HashMap<String, OrganisaatioPerustietoType> oidOrg = new HashMap<String, OrganisaatioPerustietoType>();
        HashSet<String> doesNotHaveChildren = new HashSet<String>();
        for (OrganisaatioPerustietoType curOrg : organisaatios) {
            childParent.put(curOrg.getOid(), curOrg.getParentOid());
            oidOrg.put(curOrg.getOid(), curOrg);
            doesNotHaveChildren.add(curOrg.getOid());
        }

        for (OrganisaatioPerustietoType curOrg : organisaatios) {
            final OrganisaatioPerustietoType parent = oidOrg.get(curOrg.getParentOid());
            if (parent!=null) {
                // has parent!
                organisaatioChildTree.setParent(curOrg, parent);
                organisaatioChildTree.setChildrenAllowed(parent, true);
                doesNotHaveChildren.remove(parent.getOid());
            }
        }

        for(String oid: doesNotHaveChildren) {
            organisaatioChildTree.setChildrenAllowed(oidOrg.get(oid), false);
        }
    }

    private static String getAvailableNameBasic(OrganisaatioPerustietoType org) {
        if (org.getNimiFi() != null) {

            return org.getNimiFi();
        }
        if (org.getNimiSv() != null) {

            return org.getNimiSv();
        }
        if (org.getNimiEn() != null) {

            return org.getNimiEn();
        }
        return "";
    }

    protected I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }

    public UiBuilder getUiBuilder() {
        return uiBuilder;
    }

    public void setUiBuilder(UiBuilder uiBuilder) {
        this.uiBuilder = uiBuilder;
    }
}
