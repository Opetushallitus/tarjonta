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
import fi.vm.sade.generic.ui.component.OphTokenField;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.UserContext;
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
public class KoulutusKopiointiDialog extends Window {

    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    @Autowired(required = true)
    private UserContext userContext;

    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private transient I18NHelper _i18n;
    private OptionGroup optionGroup;
    private TreeTable organisaatioChildTree;
    private SelectableItemContainer vlRight;
    protected ErrorMessage errorView;
    private HashMap<String,OrganisaatioPerustietoType> selectedOrgs = new HashMap<String,OrganisaatioPerustietoType>();
    private static final String CHILD_TREE_PROPERTY = "childOrganisaatioButton";

    public KoulutusKopiointiDialog(String width,String height) {
        super();
        _i18n = new I18NHelper(this);
        setWidth(width);
        setHeight(height);
        setContent(buildMainLayout());
        addElementsToTree(getUserOrgnanisaatioOids());
        setModal(true);
        setCaption(_i18n.getMessage("dialog.title"));

    }

    private Collection<String> getUserOrgnanisaatioOids() {
       return userContext.getUserOrganisations();
    }

    private VerticalLayout buildMainLayout() {
       VerticalLayout mainLayout = new VerticalLayout();

        mainLayout.setSizeFull();
       mainLayout.addComponent(buildTopLayout());
       mainLayout.addComponent(buildBottomLayout());

       return mainLayout;
    }

    public void addErrorMessage(String message) {
        if (errorView != null) {
            errorView.addError(message);
        }
    }

    private VerticalLayout buildTopLayout() {
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

    private HorizontalLayout buildBottomLayout() {
        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSizeFull();
        bottomLayout.setMargin(false,false,true,false);


       VerticalLayout vlLeft = new VerticalLayout();
        vlLeft.setWidth("100%");
        vlLeft.setHeight("100%");
        vlLeft.addComponent(buildOrganisaatioTree());

       GridLayout gridRight = new GridLayout(1,2);
       /* vlParentRight.setWidth("100%");
        vlParentRight.setHeight("100%");*/
        Panel vlRightPanel = new Panel();
    /*    vlRightPanel.setWidth("100%");
        vlRightPanel.setHeight("100%");*/
        vlRight = new SelectableItemContainer("100%","100%");
        vlRight.setMargin(false);
        vlRightPanel.addComponent(vlRight);
        Button peruutaBtn = UiUtil.button(null,_i18n.getMessage("peruutaBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
               getParent().removeWindow(KoulutusKopiointiDialog.this);
            }
        });

        Button jatkaBtn = UiUtil.button(null,_i18n.getMessage("jatkaBtn"), new Button.ClickListener() {
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

        HorizontalLayout buttonHl = new HorizontalLayout();

        buttonHl.setMargin(false,true,false,true);
        buttonHl.setWidth("100%");
        buttonHl.addComponent(peruutaBtn);
        buttonHl.addComponent(jatkaBtn);
        buttonHl.setComponentAlignment(peruutaBtn,Alignment.BOTTOM_LEFT);
        buttonHl.setComponentAlignment(jatkaBtn,Alignment.BOTTOM_RIGHT);

        gridRight.addComponent(vlRightPanel);
        gridRight.addComponent(buttonHl);

        gridRight.setRowExpandRatio(0,10);
        gridRight.setRowExpandRatio(1,0.1f);





        bottomLayout.addComponent(vlLeft);
        bottomLayout.addComponent(gridRight);
        gridRight.setSizeFull();
        vlRightPanel.setSizeFull();
        vlRight.setSizeFull();


        bottomLayout.setSizeFull();
        return bottomLayout;
    }

    public void addOrganisaatioToRight(OrganisaatioPerustietoType org) {
        if (!selectedOrgs.containsKey(org.getOid())) {
        SelectableItem<OrganisaatioPerustietoType> link = new SelectableItem<OrganisaatioPerustietoType>(org,"nimiFi");
        selectedOrgs.put(org.getOid(),org);
        link.setMargin(false);
        link.addListener(new SelectableItemListener() {
            @Override
            public void itemSelected(Object item) {
                if (item instanceof SelectableItem) {
                    SelectableItem<OrganisaatioPerustietoType> link = (SelectableItem<OrganisaatioPerustietoType>)item;
                    selectedOrgs.remove(link.getItem().getOid());
                    vlRight.removeComponentFromGrid(link);
                }
            }
        });
        link.setSizeFull();
        vlRight.addComponent(link);
        vlRight.requestRepaintAll();
        }
    }

    private TreeTable buildOrganisaatioTree() {
        organisaatioChildTree = new TreeTable();
        organisaatioChildTree.setColumnHeaderMode(TreeTable.COLUMN_HEADER_MODE_HIDDEN);
        organisaatioChildTree.addContainerProperty(CHILD_TREE_PROPERTY, Button.class, null);
        organisaatioChildTree.setWidth("100%");
        organisaatioChildTree.setHeight("100%");
        return organisaatioChildTree;
    }

    private void addElementsToTree(Collection<String> organisaatioOids) {
        List<String> orgOids = new ArrayList<String>(organisaatioOids);
        List<OrganisaatioPerustietoType> organisaatios = presenter.fetchChildOrganisaatios(orgOids);
        if (organisaatioChildTree != null) {
        for (final OrganisaatioPerustietoType curOrg:organisaatios) {

            Button buttonOrganisaatio = UiUtil.buttonLink(null, getAvailableNameBasic(curOrg), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                     addOrganisaatioToRight(curOrg);
                }
            });



            organisaatioChildTree.addItem(curOrg);
            Property prop = organisaatioChildTree.getContainerProperty(curOrg, CHILD_TREE_PROPERTY);
            if (prop != null) {
                prop.setValue(buttonOrganisaatio);
            }
        }
        createHierarchy(organisaatios);
        organisaatioChildTree.requestRepaint();
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

    public TarjontaPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(TarjontaPresenter presenter) {
        this.presenter = presenter;
    }
}
