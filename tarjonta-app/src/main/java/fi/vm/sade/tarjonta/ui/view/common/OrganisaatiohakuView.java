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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import fi.vm.sade.generic.common.I18NHelper;

import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
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
public class OrganisaatiohakuView extends OphAbstractCollapsibleLeft<VerticalLayout> {

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
    private Button tyhjennaB;
    private Tree tree;
    private HierarchicalContainer hc;
    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private TarjontaPresenter presenter;

    private List<OrganisaatioDTO> organisaatios;
    private OrganisaatioSearchCriteriaDTO criteria;
    List<String> rootOrganisaatioOids;

    public OrganisaatiohakuView() {
        super(VerticalLayout.class);
        criteria = new OrganisaatioSearchCriteriaDTO();
    }

    public OrganisaatiohakuView(List<String> rootOrgOids) {
        super(VerticalLayout.class);
        this.rootOrganisaatioOids = rootOrgOids;
        criteria = new OrganisaatioSearchCriteriaDTO();
        if (rootOrganisaatioOids != null) {
            criteria.getOidResctrictionList().addAll(rootOrganisaatioOids);
        }
    }

    @Override
    public void attach() {
        super.attach();
        initializeData();
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        layout.setHeight(-1, UNITS_PERCENTAGE);
        layout.setWidth(-1, UNITS_PIXELS);
        Panel panelTop = buildPanel(buildPanelLayout());

        search = UiUtil.textFieldSmallSearch(panelTop);

        // Bind enter to do the search
        search.setImmediate(true);
        search.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                searchOrganisaatios();
            }
        });

        organisaatioTyyppi = UiUtil.comboBox(panelTop, null, new String[]{OrganisaatioTyyppi.KOULUTUSTOIMIJA.value(),
                    OrganisaatioTyyppi.OPPILAITOS.value(),
                    OrganisaatioTyyppi.OPETUSPISTE.value(),
                    OrganisaatioTyyppi.OPPISOPIMUSTOIMIPISTE.value(),
                    OrganisaatioTyyppi.MUU_ORGANISAATIO.value()});
        setOrgTyyppiItemCaptions();
        organisaatioTyyppi.setSizeUndefined();

        // TODO missä tämä koodisto on? Eikös orgnanisaation puolella se ole olemassa?
        oppilaitosTyyppi = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_OPPILAITOSTYYPPI_URI, null, null, i18n.getMessage("oppilaitostyyppi.prompt"));
        oppilaitosTyyppi.getField().setNullSelectionAllowed(true);
        oppilaitosTyyppi.setWidth("210px");
        // oppilaitosTyyppi.setCaptionFormatter(TarjontaUIHelper.getKoodiTypeAsLocalizedNameCaptionFormatter());

        panelTop.addComponent(oppilaitosTyyppi);


        lakkautetut = UiUtil.checkbox(panelTop, i18n.getMessage("naytaMyosLakkautetut"));
        suunnitellut = UiUtil.checkbox(panelTop, i18n.getMessage("naytaMyosSuunnitellut"));
        HorizontalLayout buttonsL = UiUtil.horizontalLayout();
        searchB = UiUtil.buttonSmallPrimary(buttonsL, i18n.getMessage("hae"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                searchOrganisaatios();
            }
        });

        tyhjennaB = UiUtil.buttonSmallPrimary(buttonsL, i18n.getMessage("tyhjenna"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                criteria = new OrganisaatioSearchCriteriaDTO();
                if (rootOrganisaatioOids != null) {
                    criteria.getOidResctrictionList().addAll(rootOrganisaatioOids);
                }
                initializeData();
            }
        });

        panelTop.addComponent(buttonsL);
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

        tree.setItemCaptionPropertyId(COLUMN_KEY);
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);

        tree.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                organisaatioSelected((OrganisaatioDTO) (event.getItemId()));
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

    /**
     * Initializes organisaatio tree.
     */
    private void initializeData() {
        bind();
        //If an oid list is provided lists the child tree of each organisaatio
        //Otherwise searches with empty search criteria.
        this.organisaatios = (rootOrganisaatioOids != null)
                ? this.organisaatioService.listOrganisaatioByParentOids(rootOrganisaatioOids)
                : this.organisaatioService.searchOrganisaatios(new OrganisaatioSearchCriteriaDTO());

        tree.setContainerDataSource(createDatasource());
    }

    /**
     * Searches the organisaatios according to criteria, and updates
     * the data in the tree.
     */
    private void searchOrganisaatios() {
        organisaatios = organisaatioService.searchOrganisaatios(criteria);
        tree.setContainerDataSource(createDatasource());
    }

    /**
     * Creates the data source for the organisaatio tree.
     * @return
     */
    private HierarchicalContainer createDatasource() {
        tree.removeAllItems();
        hc = new HierarchicalContainer();
        hc.addContainerProperty(COLUMN_KEY, String.class, "");
        //Setting the items to the tree.
        for (OrganisaatioDTO curOrg : organisaatios) {
            //DEBUGSAWAY:LOG.debug("Organisaatio: " + curOrg);
            hc.addItem(curOrg);
            hc.getContainerProperty(curOrg, COLUMN_KEY).setValue(curOrg.getNimiFi());
        }
        //Creating the hierarchical structure of the organisaatio tree.
        createHierarchy();
        return hc;
    }

    /**
     * Creates the parent child hieararchy to the organisaatio tree.
     */
    private void createHierarchy() {
        for (OrganisaatioDTO curOrg : organisaatios) {
        	//If the current organisaatio is root, it's child tree is created.
            if (!hasParentInCurrentResults(curOrg)) {
                setChildrenTo(curOrg);
            }
        }
    }

    /**
     * Is the current organisaatio a root organisaatio
     * @param org
     * @return
     */
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

    /**
     * Setting the children of the organisaatio given as parameter.
     * @param parentOrg
     */
    private void setChildrenTo(OrganisaatioDTO parentOrg) {
        boolean wasChildren = false;
        for (OrganisaatioDTO curOrg : organisaatios) {
        	//if the curOrg is the child of parentOrg the parent-child relation is
        	//created in the container
            if (parentOrg.getOid().equals(curOrg.getParentOid())) {
                hc.setParent(curOrg, parentOrg);
                setChildrenTo(curOrg);
                wasChildren = true;
            }
        }
        //If the parentOrg has no children we set childreAllowed to false.
        if (!wasChildren) {
            hc.setChildrenAllowed(parentOrg, false);
        }
    }

    /**
     * Binds the search criteria according to criteria data.
     */
    private void bind() {
        search.setPropertyDataSource(new NestedMethodProperty(criteria, "searchStr"));
        organisaatioTyyppi.setPropertyDataSource(new NestedMethodProperty(criteria, "organisaatioTyyppi"));
        oppilaitosTyyppi.setPropertyDataSource(new NestedMethodProperty(criteria, "oppilaitosTyyppi"));
        lakkautetut.setPropertyDataSource(new NestedMethodProperty(criteria, "lakkautetut"));
        suunnitellut.setPropertyDataSource(new NestedMethodProperty(criteria, "suunnitellut"));
    }

    /**
     * Sets the selected organisaatio oid and name in TarjontaModel and fires an OrganisaatioSelectedEvent.
     * The selected organisaatio information is used when koulutus is created.
     * @param item the organisaatio selected.
     */
    private void organisaatioSelected(OrganisaatioDTO item) {
        LOG.info("Event fired: " + item.getOid());
        presenter.selectOrganisaatio(item.getOid(), item.getNimiFi());
    }

    /**
     * Gets the item captions of organisaatiotyyppi selections in search criteria combobox.
     */
    private void setOrgTyyppiItemCaptions() {
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.KOULUTUSTOIMIJA.value(), i18n.getMessage(OrganisaatioTyyppi.KOULUTUSTOIMIJA.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.MUU_ORGANISAATIO.value(), i18n.getMessage(OrganisaatioTyyppi.MUU_ORGANISAATIO.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.OPETUSPISTE.value(), i18n.getMessage(OrganisaatioTyyppi.OPETUSPISTE.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.OPPILAITOS.value(), i18n.getMessage(OrganisaatioTyyppi.OPPILAITOS.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.OPPISOPIMUSTOIMIPISTE.value(), i18n.getMessage(OrganisaatioTyyppi.OPPISOPIMUSTOIMIPISTE.name()));
    }
}
