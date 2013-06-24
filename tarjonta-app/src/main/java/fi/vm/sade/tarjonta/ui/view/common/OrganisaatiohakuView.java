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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.UserContext;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Component for searching and selecting organisaatios.
 *
 * @author markus
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = false)
public class OrganisaatiohakuView extends VerticalLayout {

    private transient static final Logger LOG = LoggerFactory.getLogger(OrganisaatiohakuView.class);
    public static final String COLUMN_KEY = "COLUMN";
    private transient static I18NHelper i18n = new I18NHelper(OrganisaatiohakuView.class);
    private static final int PANEL_WIDTH = 250;
    private static final long serialVersionUID = -7700929639425223853L;
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
    private UserContext userContext;
    @Autowired
    private OrganisaatioService organisaatioService;
    @Autowired
    private TarjontaPresenter presenter;
    private List<OrganisaatioPerustietoType> organisaatios;
    private OrganisaatioSearchCriteriaDTO criteria;
    List<String> rootOrganisaatioOids;
    private boolean isAttached = false;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    private CaptionFormatter koodiNimiFormatter = new CaptionFormatter<KoodiType>() {
        @Override
        public String formatCaption(KoodiType dto) {
            if (dto == null) {
                return "";
            }

            return TarjontaUIHelper.getKoodiMetadataForLanguage(dto, I18N.getLocale()).getNimi();
        }
    };

    public OrganisaatiohakuView() {

        criteria = new OrganisaatioSearchCriteriaDTO();
        criteria.setFirstResult(1); //HACK, see OVT-4770, to be removed in master
        try {
            criteria.setMaxResults(Integer.parseInt(T("maxResults")));
        } catch (Exception ex) {
            LOG.warn("max results not set: " + ex.getMessage());
        }
    }

    public OrganisaatiohakuView(List<String> rootOrgOids) {
        this();
        this.rootOrganisaatioOids = rootOrgOids;
        if (rootOrganisaatioOids != null) {
            criteria.getOidResctrictionList().addAll(rootOrganisaatioOids);
        }
    }

    @Override
    public void attach() {
        super.attach();
        if (isAttached) {
            return;
        }

        buildLayout(this);
        //autoSearch();

        isAttached = true;
        //initializeData();
        bind();
    }

    public void autoSearch() {
        Preconditions.checkNotNull(userContext);
        if (userContext.isDoAutoSearch()) {
            this.rootOrganisaatioOids = Lists.newArrayList(userContext.getUserOrganisations());

            criteria.getOidResctrictionList().clear();
            criteria.getOidResctrictionList().addAll(rootOrganisaatioOids);
            LOG.info("Autosearching orgs, restrictions: " + criteria.getOidResctrictionList());
            searchOrganisaatios();

            //auto select
            final String ooid = userContext.getUserOrganisations().size() == 1 ? userContext.getFirstOrganisaatio() : null;
            if (ooid != null) {
                for (OrganisaatioPerustietoType organisaatio : organisaatios) {
                    if (ooid.equals(organisaatio.getOid())) {
                        LOG.debug("Comparing {} against {}.", ooid, organisaatio.getOid());
                        organisaatioSelected(organisaatio);
                        break;
                    }
                }
            }
        }
    }

    protected void buildLayout(VerticalLayout layout) {
        layout.setHeight(-1, UNITS_PIXELS);
        layout.setWidth(-1, UNITS_PIXELS);
        Panel panelTop = buildPanel(buildPanelLayout());

        search = UiUtil.textFieldSmallSearch(panelTop);
        search.setInputPrompt(T("search.prompt"));

        // Bind enter to do the search
        search.setImmediate(true);
        search.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (search.getValue() != null && !((String) (search.getValue())).isEmpty()) {
                    searchOrganisaatios();
                }
            }
        });

        organisaatioTyyppi = UiUtil.comboBox(panelTop, null,
                new String[]{
            OrganisaatioTyyppi.KOULUTUSTOIMIJA.value(),
            OrganisaatioTyyppi.OPPILAITOS.value(),
            OrganisaatioTyyppi.OPETUSPISTE.value(),
            OrganisaatioTyyppi.OPPISOPIMUSTOIMIPISTE.value(),
            OrganisaatioTyyppi.MUU_ORGANISAATIO.value()
        });
        setOrgTyyppiItemCaptions();
        organisaatioTyyppi.setSizeUndefined();
        organisaatioTyyppi.setInputPrompt(T("organisaatioTyyppi.prompt"));

        // TODO missä tämä koodisto on? Eikös orgnanisaation puolella se ole olemassa?
        oppilaitosTyyppi = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_OPPILAITOSTYYPPI_URI, null, null, T("oppilaitostyyppi.prompt"), true);
        oppilaitosTyyppi.getField().setNullSelectionAllowed(true);
        oppilaitosTyyppi.setSizeUndefined();
        oppilaitosTyyppi.getField().setSizeUndefined();
        oppilaitosTyyppi.setCaptionFormatter(this.koodiNimiFormatter);

        panelTop.addComponent(oppilaitosTyyppi);


        lakkautetut = UiUtil.checkbox(panelTop, T("naytaMyosLakkautetut"));
        suunnitellut = UiUtil.checkbox(panelTop, T("naytaMyosSuunnitellut"));
        HorizontalLayout buttonsL = UiUtil.horizontalLayout();
        searchB = UiUtil.buttonSmallSecodary(buttonsL, T("hae"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                searchOrganisaatios();
            }
        });

        tyhjennaB = UiUtil.buttonSmallSecodary(buttonsL, T("tyhjenna"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                criteria = new OrganisaatioSearchCriteriaDTO();
                if (rootOrganisaatioOids != null) {
                    criteria.getOidResctrictionList().addAll(rootOrganisaatioOids);
                }
                try {
                    criteria.setMaxResults(Integer.parseInt(T("maxResults")));
                } catch (Exception ex) {
                    LOG.warn("Max results not set: " + ex.getMessage());
                }
                bind();
                //initializeData();
            }
        });

        panelTop.addComponent(buttonsL);
        Panel panelBottom = buildPanel(buildTreePanelLayout());
        panelBottom.setHeight(550, UNITS_PIXELS);
        panelBottom.addStyleName(Oph.CONTAINER_SECONDARY);

        layout.addComponent(panelTop);
        layout.addComponent(panelBottom);
    }

    private Panel buildPanel(AbstractLayout layout) {
        Panel panel = new Panel(layout);
        panel.setWidth("100%");
        panel.setHeight(-1, Sizeable.UNITS_PIXELS);
        panel.addStyleName(Oph.CONTAINER_SECONDARY);
        panel.setScrollable(true);
        return panel;
    }

    private AbstractLayout buildTreePanelLayout() {
        VerticalLayout hl = buildPanelLayout();

        tree = new Tree();
        tree.setNullSelectionAllowed(false); //
        tree.setSizeUndefined();

        tree.setItemCaptionPropertyId(COLUMN_KEY);
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {

            private static final long serialVersionUID = 618972158328470017L;

            @Override
            public String generateDescription(Component source, Object itemId,
                    Object propertyId) {
                if (itemId instanceof OrganisaatioPerustietoType) {
                    OrganisaatioPerustietoType tooltipOrg = (OrganisaatioPerustietoType) itemId;
                    return getClosestNimi(I18N.getLocale(), tooltipOrg);
                }
                
                return null;
            }
        });
        
        tree.addListener(new ItemClickEvent.ItemClickListener() {
            private static final long serialVersionUID = -2318797984292753676L;

            @Override
            public void itemClick(ItemClickEvent event) {
                if ((event != null && event.getItemId() != null)) {
                    final OrganisaatioPerustietoType opt = (OrganisaatioPerustietoType) event.getItemId();
                    final String newOrganisaatioOid = opt.getOid();
                    final String previousOrganisaatioOid = presenter.getNavigationOrganisation().getOrganisationOid();

                    //don't allow an user to deselect a row item in a tree component.
                    if (newOrganisaatioOid != null && (previousOrganisaatioOid == null || !newOrganisaatioOid.equals(previousOrganisaatioOid))) {
                        organisaatioSelected(opt);
                    }
                }
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
     * Searches the organisaatios according to criteria, and updates the data in
     * the tree.
     */
    private void searchOrganisaatios() {
        long time = System.currentTimeMillis();
        LOG.debug("Doing organisaatio search");
        try {
            criteria.getOidResctrictionList().clear();
            if (userContext.isUseRestriction()) {
                LOG.debug("Using restriction:" + userContext.getUserOrganisations());
                criteria.getOidResctrictionList().addAll(userContext.getUserOrganisations());
            }
            organisaatios = organisaatioService.searchBasicOrganisaatios(criteria);
        } catch (Exception ex) {
            if (ex.getMessage().contains("organisaatioSearch.tooManyResults")) {
                this.getWindow().showNotification(T("tooManyOrganisaatioResults"), Notification.TYPE_WARNING_MESSAGE);
            }
            this.organisaatios = new ArrayList<OrganisaatioPerustietoType>();
        }
        LOG.debug("org search done. took {}ms.", System.currentTimeMillis() - time);
        tree.setContainerDataSource(createDatasource());
    }

    /**
     * Creates the data source for the organisaatio tree.
     *
     * @return
     */
    private HierarchicalContainer createDatasource() {
        tree.removeAllItems();
        //tree.setItem
        hc = new HierarchicalContainer();
        hc.addContainerProperty(COLUMN_KEY, String.class, "");
        //Setting the items to the tree.
        Ordering<OrganisaatioPerustietoType> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<OrganisaatioPerustietoType, Comparable>() {
            public Comparable apply(OrganisaatioPerustietoType input) {
                return getClosestNimi(I18N.getLocale(), input);

            }
        ;
        });
        //sort
        organisaatios = ordering.immutableSortedCopy(organisaatios);


        for (OrganisaatioPerustietoType curOrg : organisaatios) {
            hc.addItem(curOrg);
            hc.getContainerProperty(curOrg, COLUMN_KEY).setValue(getClosestNimi(I18N.getLocale(), curOrg));//curOrg.getNimiFi());
        }
        //Creating the hierarchical structure of the organisaatio tree.
        createHierarchy();
        return hc;
    }

    /**
     * Creates the parent child hieararchy to the organisaatio tree.
     */
    private void createHierarchy() {
        for (OrganisaatioPerustietoType curOrg : organisaatios) {
            //If the current organisaatio is root, it's child tree is created.
            if (!hasParentInCurrentResults(curOrg)) {
                setChildrenTo(curOrg);
            }
        }
    }

    private void openTree(OrganisaatioPerustietoType organisaatio, OrganisaatioPerustietoType parentOrg) {
        if (criteria.getSearchStr() != null && !criteria.getSearchStr().isEmpty()
                && getClosestNimi(I18N.getLocale(), organisaatio).toLowerCase().contains(criteria.getSearchStr().toLowerCase())) {
            expandPath(parentOrg); //this.setCollapsed(parentOrg, false);
        }
    }

    private void expandPath(OrganisaatioPerustietoType org) {
        tree.expandItem(org);
        OrganisaatioPerustietoType parent = (OrganisaatioPerustietoType) (hc.getParent(org));
        if (parent != null) {
            expandPath(parent);
        }
    }

    /**
     * Is the current organisaatio a root organisaatio
     *
     * @param org
     * @return
     */
    private boolean hasParentInCurrentResults(OrganisaatioPerustietoType org) {
        if (org.getParentOid() == null || org.getParentOid().equals(presenter.getModel().getRootOrganisaatioOid())) {
            return false;
        }
        for (OrganisaatioPerustietoType curOrg : organisaatios) {
            if (org.getParentOid().equals(curOrg.getOid())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Setting the children of the organisaatio given as parameter.
     *
     * @param parentOrg
     */
    private void setChildrenTo(OrganisaatioPerustietoType parentOrg) {
        boolean wasChildren = false;
        for (OrganisaatioPerustietoType curOrg : organisaatios) {
            //if the curOrg is the child of parentOrg the parent-child relation is
            //created in the container
            if (parentOrg.getOid().equals(curOrg.getParentOid())) {
                hc.setParent(curOrg, parentOrg);
                setChildrenTo(curOrg);
                wasChildren = true;
                openTree(curOrg, parentOrg);
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
     * Sets the selected organisaatio oid and name in TarjontaModel and fires an
     * OrganisaatioSelectedEvent. The selected organisaatio information is used
     * when koulutus is created.
     *
     * @param item the organisaatio selected.
     */
    private void organisaatioSelected(final OrganisaatioPerustietoType item) {
        LOG.info("Event fired: " + item.getOid());
        if (!item.getOid().equals(presenter.getNavigationOrganisation().getOrganisationOid())) {
            presenter.selectOrganisaatio(item.getOid(), getOrganisaatioNimi(item),
            		item.getLakkautusPvm()==null || item.getLakkautusPvm().after(new Date()));
        } else {
            presenter.unSelectOrganisaatio();
        }
    }

    private String getOrganisaatioNimi(final OrganisaatioPerustietoType item) {
        if (item.getNimiFi() != null) {
            return item.getNimiFi();
        } else if (item.getNimiSv() != null ) {
            return item.getNimiSv();

        }   else if (item.getNimiEn() != null) {
            return item.getNimiEn();
        }
        return null;
    }

    /**
     * Gets the item captions of organisaatiotyyppi selections in search
     * criteria combobox.
     */
    private void setOrgTyyppiItemCaptions() {
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.KOULUTUSTOIMIJA.value(), T(OrganisaatioTyyppi.KOULUTUSTOIMIJA.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.MUU_ORGANISAATIO.value(), T(OrganisaatioTyyppi.MUU_ORGANISAATIO.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.OPETUSPISTE.value(), T(OrganisaatioTyyppi.OPETUSPISTE.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.OPPILAITOS.value(), T(OrganisaatioTyyppi.OPPILAITOS.name()));
        organisaatioTyyppi.setItemCaption(OrganisaatioTyyppi.OPPISOPIMUSTOIMIPISTE.value(), T(OrganisaatioTyyppi.OPPISOPIMUSTOIMIPISTE.name()));
    }

    private String getClosestNimi(Locale locale, OrganisaatioPerustietoType org) {
        String lang = (locale != null) ? locale.getLanguage().toLowerCase() : "";
        if (lang.equals("fi") && org.getNimiFi() != null) {

            return org.getNimiFi();
        }
        if (lang.equals("sv") && org.getNimiSv() != null) {

            return org.getNimiSv();
        }
        if (lang.equals("en") && org.getNimiEn() != null) {
            return org.getNimiEn();
        }
        return getAvailableNimi(org);
    }

    private String getAvailableNimi(OrganisaatioPerustietoType org) {
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

    public void clearTreeSelection() {

        this.tree.setValue(null);
    }

    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }
}
