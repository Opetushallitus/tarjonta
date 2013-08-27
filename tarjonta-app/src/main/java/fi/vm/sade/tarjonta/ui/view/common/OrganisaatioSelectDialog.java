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
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
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
    
    private static final long serialVersionUID = 1L;

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
    protected KoulutusasteTyyppi koulutusTyyppi;
    private boolean limitToOne = false;
    
    protected HashMap<String,OrganisaatioPerustieto> selectedOrgs = new HashMap<String,OrganisaatioPerustieto>();
    
    public OrganisaatioSelectDialog(String width,String height) {
        super();
        init(width, height);
    }

    public OrganisaatioSelectDialog(String width,String height,boolean limitToOne) {
        this(width, height);
        this.limitToOne = limitToOne;
    }

    public OrganisaatioSelectDialog(String width,String height, KoulutusasteTyyppi tyyppi) {
        koulutusTyyppi = tyyppi;
        init(width, height);
    }
    
    protected abstract Collection<String> getOrganisaatioOids();
    
    protected abstract VerticalLayout buildTopLayout();
    
    protected abstract void setButtonListeners();
    
    private void init(String width, String height) {
        _i18n = new I18NHelper(this);
        setWidth(width);
        setHeight(height);
        setContent(buildMainLayout());
        addElementsToTree(getOrganisaatioOids());
        setModal(true);
        setButtonListeners();
    }

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

    protected void setTableCaption(String tableCaption) {
        if (organisaatioChildTree != null) {
            organisaatioChildTree.setCaption(tableCaption);
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
        List<OrganisaatioPerustieto> organisaatios = presenter.fetchChildOrganisaatios(orgOids);
        if (organisaatioChildTree != null) {
        for (final OrganisaatioPerustieto curOrg:organisaatios) {

            Button buttonOrganisaatio = UiUtil.buttonLink(null, OrganisaatioDisplayHelper.getAvailableNameBasic(curOrg), new Button.ClickListener() {
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

    private void createHierarchy(List<OrganisaatioPerustieto> organisaatios) {
        HashMap<String, String> childParent = new HashMap<String, String>();
        HashMap<String, OrganisaatioPerustieto> oidOrg = new HashMap<String, OrganisaatioPerustieto>();
        HashSet<String> doesNotHaveChildren = new HashSet<String>();
        for (OrganisaatioPerustieto curOrg : organisaatios) {
            childParent.put(curOrg.getOid(), curOrg.getParentOid());
            oidOrg.put(curOrg.getOid(), curOrg);
            doesNotHaveChildren.add(curOrg.getOid());
        }

        for (OrganisaatioPerustieto curOrg : organisaatios) {
            final OrganisaatioPerustieto parent = oidOrg.get(curOrg.getParentOid());
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
    
     public void addOrganisaatioToRight(OrganisaatioPerustieto org) {
        if (!selectedOrgs.containsKey(org.getOid())) {
        if (limitToOne && selectedOrgs.size() > 0) {
            return;
        }
        SelectableItem<OrganisaatioPerustieto> link = null ;
        if (org.getNimiFi() != null && org.getNimiFi().trim().length() > 0) {
          link  = new SelectableItem<OrganisaatioPerustieto>(org,"nimiFi");
        } else if (org.getNimiSv() != null && org.getNimiSv().trim().length() > 0) {
            link  = new SelectableItem<OrganisaatioPerustieto>(org,"nimiSv");
        } else if (org.getNimiEn() != null && org.getNimiEn().trim().length() > 0) {
            link  = new SelectableItem<OrganisaatioPerustieto>(org,"nimiEn");
        }
        selectedOrgs.put(org.getOid(),org);
        link.setMargin(false);
        link.addListener(new SelectableItemListener() {
            @Override
            public void itemSelected(Object item) {
                if (item instanceof SelectableItem) {
                    SelectableItem<OrganisaatioPerustieto> link = (SelectableItem<OrganisaatioPerustieto>)item;
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

    public boolean isLimitToOne() {
        return limitToOne;
    }

    public void setLimitToOne(boolean limitToOne) {
        this.limitToOne = limitToOne;
    }
}
