package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.koulutus.KoulutusKopiointiDialog;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author: Tuomas Katva
 * 
 * This is abstract class for organisaatio selection dialog.
 * All implementing subclasses must implement buildTopLayout-method,
 * which builds dialogs top layout which might contain instruction texts etc.
 * 
 * Implementing subclasses must implement getOrganisaatioOids-method which returns list containing all parent organisaatio oids,
 * this list is used to populate organisaatio selection treetable.
 * 
 * Finally implementing subclasses must implement setButtonListener-method which is used to add listeners to jatka and peruuta button
 * 
 */
@Configurable(preConstruction =  true)
public abstract class OrganisaatioSelectDialog extends Window {
    
    @Autowired(required = true)
    protected transient UiBuilder uiBuilder;
    
    @Autowired(required = true)
    protected TarjontaPresenter presenter;
    
    protected transient I18NHelper _i18n;
    private TreeTable organisaatioChildTree;
    private SelectableItemContainer vlRight;
    protected ErrorMessage errorView;
    private static final String CHILD_TREE_PROPERTY = "childOrganisaatioButton";
    protected Button peruutaBtn; 
    protected Button jatkaBtn;
    
    protected HashMap<String,OrganisaatioPerustietoType> selectedOrgs = new HashMap<String,OrganisaatioPerustietoType>();
    
    public OrganisaatioSelectDialog(String width,String height) {
        super();
        _i18n = new I18NHelper(this);
        setWidth(width);
        setHeight(height);
        setContent(buildMainLayout());
        addElementsToTree(getOrganisaatioOids());
        setModal(true);
        setButtonListeners();
    }
    
    protected abstract Collection<String> getOrganisaatioOids();
    
    protected abstract VerticalLayout buildTopLayout();
    
    protected abstract void setButtonListeners();

    private VerticalLayout buildMainLayout() {
       VerticalLayout mainLayout = new VerticalLayout();

        mainLayout.setSizeFull();
       mainLayout.addComponent(buildTopLayout());
       mainLayout.addComponent(buildBottomLayout());

       return mainLayout; 
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
       peruutaBtn = UiUtil.button(null,_i18n.getMessage("peruutaBtn"), null);

        jatkaBtn = UiUtil.button(null,_i18n.getMessage("jatkaBtn"), null);

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
    
    public void addErrorMessage(String message) {
        if (errorView != null) {
            errorView.addError(message);
        }
    }
    
    protected TreeTable buildOrganisaatioTree() {
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
    
}
