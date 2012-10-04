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
package fi.vm.sade.tarjonta.ui.view.common;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.tarjonta.ui.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.ui.OphAbstractCollapsibleLeft;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Component for searching and selecting organisaatios. 
 * 
 * @author markus
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = false)
public class OrganisaatiohakuView extends
        OphAbstractCollapsibleLeft<VerticalLayout> {
    
    private static final Logger LOG = LoggerFactory.getLogger(OrganisaatiohakuView.class);
    
    public static final String COLUMN_KEY = "COLUMN";
    
    private static I18NHelper i18n = new I18NHelper(OrganisaatiohakuView.class);
    private static final int PANEL_WIDTH = 250;
    private TextField search;
    private ComboBox organisaatioTyyppi;
    private KoodistoComponent oppilaitosTyyppi;
    private CheckBox lakkautetut;
    private CheckBox suunnitellut;
    private Button searchB;
    private Tree tree;
    private HierarchicalContainer hc;
    
    @Autowired
    private OrganisaatioService organisaatioService;
    private List<OrganisaatioDTO> organisaatios;
    private OrganisaatioSearchCriteriaDTO criteria;
    List<String> rootOrganisaatioOids;
    
    @Value("${koodisto-uris.oppilaitostyyppi:Oppilaitostyyppi}")
    private String oppilaitostyyppiUri;
    
    public OrganisaatiohakuView() {
        super(VerticalLayout.class);
        criteria = new OrganisaatioSearchCriteriaDTO();
    }
    
    public OrganisaatiohakuView(List<String> rootOrgOids) {
        super(VerticalLayout.class);
        this.rootOrganisaatioOids = rootOrgOids;
        criteria = new OrganisaatioSearchCriteriaDTO();
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        layout.setHeight(-1, UNITS_PERCENTAGE);
        layout.setWidth(-1, UNITS_PIXELS);
        Panel panelTop = buildPanel(buildPanelLayout());

        search = UiUtil.textFieldSmallSearch(panelTop);
        
        organisaatioTyyppi = UiUtil.comboBox(panelTop, null, new String[]{OrganisaatioTyyppi.KOULUTUSTOIMIJA.value(), 
                                                                            OrganisaatioTyyppi.OPPILAITOS.value(), 
                                                                            OrganisaatioTyyppi.OPETUSPISTE.value(), 
                                                                            OrganisaatioTyyppi.OPPISOPIMUSTOIMIPISTE.value(), 
                                                                            OrganisaatioTyyppi.MUU_ORGANISAATIO.value()});
        setOrgTyyppiItemCaptions();
        organisaatioTyyppi.setSizeUndefined();
        
        
        oppilaitosTyyppi =  UiBuilder.koodistoComboBox(null, i18n.getMessage("koodisto-uris.oppilaitostyyppi"), null, null, i18n.getMessage("oppilaitostyyppi.prompt"));
        oppilaitosTyyppi.setWidth("210px");
        
        panelTop.addComponent(oppilaitosTyyppi);
        
        
        lakkautetut = UiUtil.checkbox(panelTop, i18n.getMessage("naytaMyosLakkautetut"));
        suunnitellut = UiUtil.checkbox(panelTop, i18n.getMessage("naytaMyosSuunnitellut"));
        searchB = UiUtil.buttonSmallPlus(panelTop, i18n.getMessage("hae"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                searchOrganisaatios();         
            }
        });
        
        Panel panelBottom = buildPanel(buildTreePanelLayout());
        panelBottom.addStyleName(Oph.CONTAINER_SECONDARY);

        layout.addComponent(panelTop);
        layout.addComponent(panelBottom);
    }
    


    private Panel buildPanel(AbstractLayout layout) {
        Panel panel = new Panel(layout);
        panel.setWidth(PANEL_WIDTH, Sizeable.UNITS_PIXELS);
        panel.setHeight(-1, Sizeable.UNITS_PIXELS);
        panel.addStyleName(Oph.CONTAINER_SECONDARY);
        panel.setScrollable(true);
        return panel;
    }

    private AbstractLayout buildTreePanelLayout() {
        
        VerticalLayout hl = buildPanelLayout();

        tree = new Tree();
        tree.setSizeUndefined();
        
        //TODO: REAL DATA
        
        tree.setItemCaptionPropertyId(COLUMN_KEY);
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);

        tree.addListener(new ItemClickEvent.ItemClickListener() {
            
            @Override
            public void itemClick(ItemClickEvent event) {
               organisaatioSelected((OrganisaatioDTO)(event.getItemId()));
            }

        });
        
        hl.addComponent(tree);
        return hl;
    }
    
    private VerticalLayout buildPanelLayout() {
        VerticalLayout hl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        hl.setSizeUndefined();
        return hl;
    }
    
    @PostConstruct
    private void initializeData() {
        bind();
        this.organisaatios = (rootOrganisaatioOids != null) 
                ? this.organisaatioService.listOrganisaatioByParentOids(rootOrganisaatioOids) 
                        : this.organisaatioService.searchOrganisaatios(new OrganisaatioSearchCriteriaDTO());
        
         tree.setContainerDataSource(createDatasource());          
    }
    
    private void searchOrganisaatios() {
        organisaatios = organisaatioService.searchOrganisaatios(criteria);
        tree.setContainerDataSource(createDatasource());
    }
    
    private HierarchicalContainer createDatasource() {
        tree.removeAllItems();
        hc = new HierarchicalContainer();
        hc.addContainerProperty(COLUMN_KEY, String.class, "");
        for (OrganisaatioDTO curOrg : organisaatios) {
            LOG.debug("Organisaatio: " + curOrg);
            hc.addItem(curOrg);
            hc.getContainerProperty(curOrg, COLUMN_KEY).setValue(curOrg.getNimiFi());
        }
        createHierarchy();
        return hc;
    }
    
    private void createHierarchy() {
        for (OrganisaatioDTO curOrg : organisaatios) {
            if (!hasParentInCurrentResults(curOrg)) {
                setChildrenTo(curOrg);
            }
        }
    }

    private boolean hasParentInCurrentResults(OrganisaatioDTO org) {
        if (org.getParentOid() == null) {
            return false;
        }
        for (OrganisaatioDTO curOrg : organisaatios) {
            if (org.getParentOid().equals(curOrg.getParentOid())) {
                return true;
            }
        }
        return false;
    }

    private void setChildrenTo(OrganisaatioDTO parentOrg) {
        boolean wasChildren = false;
        for (OrganisaatioDTO curOrg : organisaatios) {
            if (parentOrg.getOid().equals(curOrg.getParentOid())) {
                hc.setParent(curOrg, parentOrg);
                setChildrenTo(curOrg);
                wasChildren = true;
            }
        }
        if (!wasChildren) {
            hc.setChildrenAllowed(parentOrg, false);
        }
    }
    
    private void bind() {
        search.setPropertyDataSource(new NestedMethodProperty(criteria, "searchStr"));
        organisaatioTyyppi.setPropertyDataSource(new NestedMethodProperty(criteria, "organisaatioTyyppi"));
        oppilaitosTyyppi.setPropertyDataSource(new NestedMethodProperty(criteria, "oppilaitosTyyppi"));
        lakkautetut.setPropertyDataSource(new NestedMethodProperty(criteria, "lakkautetut"));
        suunnitellut.setPropertyDataSource(new NestedMethodProperty(criteria, "suunnitellut"));
    }
    
    private void organisaatioSelected(OrganisaatioDTO item) {
        LOG.info("Event fired: " + item.getOid());
        fireEvent(new OrganisaatioSelectedEvent(this, item.getOid()));
    }
    
    private void setOrgTyyppiItemCaptions() {
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.KOULUTUSTOIMIJA.value(), i18n.getMessage(OrganisaatioTyyppi.KOULUTUSTOIMIJA.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.MUU_ORGANISAATIO.value(), i18n.getMessage(OrganisaatioTyyppi.MUU_ORGANISAATIO.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.OPETUSPISTE.value(), i18n.getMessage(OrganisaatioTyyppi.OPETUSPISTE.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.OPPILAITOS.value(), i18n.getMessage(OrganisaatioTyyppi.OPPILAITOS.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.OPPISOPIMUSTOIMIPISTE.value(), i18n.getMessage(OrganisaatioTyyppi.OPPISOPIMUSTOIMIPISTE.name()));
    }
    
    /**
     * Event to signal that the user wants to create a new Haku.
    */
    public class OrganisaatioSelectedEvent extends Component.Event {

        private String organisaatioOid;
        
        public OrganisaatioSelectedEvent(Component source) {
            super(source);
            
        }
        
        public OrganisaatioSelectedEvent(Component source, String organisaatioOid) {
            super(source);
            this.organisaatioOid = organisaatioOid;
        }

        public String getOrganisaatioOid() {
            return organisaatioOid;
        }

        public void setOrganisaatioOid(String organisaatioOid) {
            this.organisaatioOid = organisaatioOid;
        }
        
    }
}
