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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.helper.ButtonSynchronizer;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.tarjonta.ui.view.hakukohde.CreationDialog;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 *
 * @author Markus
 */
@Configurable(preConstruction = true)
public class ListKoulutusView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ListKoulutusView.class);
    private static final long serialVersionUID = 2571418094927644189L;
    //public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListKoulutusView.jarjestys.Organisaatio")};
    public static final String COLUMN_A = "Kategoriat";
    public static final String COLUMN_TUTKINTONIMIKE = "Tutkintonimike";
    public static final String COLUMN_PVM = "Ajankohta";
    public static final String COLUMN_KOULUTUSLAJI = "Koulutuslaji";
    public static final String COLUMN_TILA = "Tila";
    private static final int MAX_PARENT_ROWS = 100;
    private ErrorMessage errorView;
    /**
     * Presenter object for the Hakukohde listing.
     */
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private Window createHakukohdeDialog;
    private final ButtonSynchronizer synchronizer = new ButtonSynchronizer();
    /**
     * Button for creating a hakukohde object.
     */
    private Button luoHakukohdeB;
    /**
     * Button for creating a koulutus object.
     */
    private Button luoKoulutusB;
    /**
     * Component for selecting desired sorting/grouping criteria for listed
     * Hakukohde objects.
     */
    private ComboBox cbJarjestys;
    /**
     * TreeTable component to display the Hakukohde objects in a
     * grouped/hierarchical manner.
     */
    private CategoryTreeView categoryTree;
    /**
     * Checkbox for selecting all the Hakukohde objects in the list.
     */
    private CheckBox valKaikki;
    private TarjontaDialogWindow koulutusDialog;
    private CreationDialog<KoulutusOidNameViewModel> createDialog;
    //private Button btnPoista;
    //private Button btnMuokkaa;
    private Button btnSiirraJaKopioi;
    private transient I18NHelper i18n = new I18NHelper(this);
    private boolean isAttached = false;
    @Autowired(required = true)
    private transient TarjontaUIHelper uiHelper;
    private Set<Map.Entry<String, List<KoulutusPerustieto>>> resultSet;

    public ListKoulutusView() {
        setSizeFull();
        setMargin(true);

    }

    @Override
    public void attach() {
        super.attach();


        if (isAttached) {
            LOG.debug("already attached : ListKoulutusView()");
            return;
        }
        
        presenter.registerEventListener(this);

        LOG.debug("attach : ListKoulutusView()");
        isAttached = true;



        //Initialization of the view layout

        //Creation of the button bar above the Hakukohde hierarchical/grouped list.
        buildMiddleResultLayout();

        errorView = new ErrorMessage();
        addComponent(errorView);
        //Adding the select all checkbox.
        addValitsekaikki();



        /**
         * Sets the datasource for the hierarchical listing of Koulutus objects.
         */
        luoKoulutusB.setEnabled(presenter.getNavigationOrganisation().isOrganisationSelected());
        luoHakukohdeB.setEnabled(!presenter.getModel().getSelectedKoulutukset().isEmpty());
    }

    
    @Override
    public void finalize() {
        presenter.unregisterEventListener(this);
    }

    /*
     * Adding the actual tutkinto-listing component.
     */
    private void addAndRebuildTutkintoResultList() {
        /*
         * A problem with Vaadin TreeTable:
         * 
         * SearchResultsView must be initialized every time data rows are changed.
         * 
         * If there is a better way to make TreeTable to not show result rows 
         * with right scrollbar, then the code on bottom can be modified so it only 
         * attached once. 
         */
        if (categoryTree != null) {
            this.removeComponent(categoryTree);
            categoryTree = null;
        }
        //Adding the actual Hakukohde-listing component.
        categoryTree = new CategoryTreeView();
        categoryTree.addContainerProperty(COLUMN_A, KoulutusResultRow.class, new KoulutusResultRow(uiHelper));
        categoryTree.addContainerProperty(COLUMN_TUTKINTONIMIKE, String.class, "");
        categoryTree.addContainerProperty(COLUMN_PVM, String.class, "");
        categoryTree.addContainerProperty(COLUMN_KOULUTUSLAJI, String.class, "");
        categoryTree.addContainerProperty(COLUMN_TILA, String.class, "");

        categoryTree.setColumnExpandRatio(COLUMN_A, 2.2f);
        categoryTree.setColumnExpandRatio(COLUMN_PVM, 0.3f);
        categoryTree.setColumnExpandRatio(COLUMN_KOULUTUSLAJI, 0.5f);
        categoryTree.setColumnExpandRatio(COLUMN_TILA, 0.3f);

        categoryTree.addListener(new Tree.ExpandListener() {
            private static final long serialVersionUID = 7555216006146778964L;

            @Override
            public void nodeExpand(Tree.ExpandEvent event) {
                /*
                 * LAZY CHILD DATA LOADING
                 */

                if (resultSet == null || event == null) {
                    LOG.error("An unknown problem in nodeExpand.");
                    return;
                }

                Item item = categoryTree.getItem(event.getItemId());
                KoulutusResultRow row = (KoulutusResultRow) item.getItemProperty(COLUMN_A).getValue();

                for (KoulutusPerustieto curKoulutus : row.getChildren()) {
                    addKoulutusRow(event.getItemId(), curKoulutus);
                }
                setPageLength(categoryTree.getItemIds().size());
            }

        });

        categoryTree.setSizeFull();
    }

    private void addKoulutusRow(Object parentId,
            KoulutusPerustieto curKoulutus) {
        final String nimi = uiHelper.getKoulutusNimi(curKoulutus);
        KoulutusResultRow rowStyleInner = new KoulutusResultRow(uiHelper, curKoulutus, nimi);
        categoryTree.addItem(curKoulutus);
        categoryTree.setParent(curKoulutus, parentId);
        categoryTree.getContainerProperty(curKoulutus, COLUMN_A).setValue(rowStyleInner.format(nimi, true));
        setKoulutusRowProperties(curKoulutus);
        categoryTree.setChildrenAllowed(curKoulutus, false);
    }

    private void setKoulutusRowProperties(KoulutusPerustieto curKoulutus) {
        categoryTree.getContainerProperty(curKoulutus, COLUMN_PVM).setValue(uiHelper.getAjankohtaStr(curKoulutus));
        categoryTree.getContainerProperty(curKoulutus, COLUMN_KOULUTUSLAJI).setValue(uiHelper.getKoulutuslaji(curKoulutus));
        categoryTree.getContainerProperty(curKoulutus, COLUMN_TILA).setValue(getTilaStr(curKoulutus.getTila().name()));
    }
    
    private void addValitsekaikki() {
        //Adding the select all checkbox.
        CssLayout wrapper = UiUtil.cssLayout(UiMarginEnum.BOTTOM);
        valKaikki = new CheckBox(i18n.getMessage("ValitseKaikki"));
        valKaikki.setImmediate(true);
        valKaikki.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                toggleHakuSelections(valKaikki.booleanValue());

            }
        });
        wrapper.addComponent(valKaikki);
        addComponent(wrapper);
    }

    /**
     * Creates the vaadin HierarchicalContainer datasource for the Koulutus
     * listing based on data provided by the presenter.
     *
     * @param map the data map provided by the presenter.
     * @return the hierarchical container for Koulutus listing.
     */
    private Container createDataSource(Map<String, List<KoulutusPerustieto>> map) {

        resultSet = map.entrySet();
        HierarchicalContainer hc = new HierarchicalContainer();
        KoulutusResultRow rowStyleDef = new KoulutusResultRow(uiHelper);

        hc.addContainerProperty(COLUMN_A, KoulutusResultRow.class, rowStyleDef.format("", false));
        hc.addContainerProperty(COLUMN_PVM, String.class, "");
        hc.addContainerProperty(COLUMN_KOULUTUSLAJI, String.class, "");
        hc.addContainerProperty(COLUMN_TILA, String.class, "");

        int index = 0;
        for (Map.Entry<String, List<KoulutusPerustieto>> e : resultSet) {
            if (index > MAX_PARENT_ROWS) {
                //A quick hack, it would be great, if data was limited in back-end service.
                errorView.addError(I18N.getMessage("liianMontaHakutulosta"));
                break;
            }

            //LOG.debug("getTreeDataSource()" + e.getKey());
            KoulutusResultRow rowStyle = new KoulutusResultRow(uiHelper);
            rowStyle.setRowKey(e.getKey());
            rowStyle.setChildren(e.getValue());
            Object rootItem = hc.addItem();
            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(buildOrganisaatioCaption(e), false));
            index++;
        }
        return hc;
    }

    private String buildOrganisaatioCaption(Map.Entry<String, List<KoulutusPerustieto>> e) {
        return e.getKey().substring(0, e.getKey().lastIndexOf(",")) + " (" + e.getValue().size() + ")";
    }

    private String getTilaStr(String tilaUri) {
        String[] parts = tilaUri.split("\\/");
        return i18n.getMessage(parts[parts.length - 1]);
    }

    /**
     * Selects or unselects all the objects in the Hakukohde listing.
     *
     * @param selected
     */
    private void toggleHakuSelections(boolean selected) {
        presenter.getSelectedhakukohteet().clear();
        HierarchicalContainer hc = (HierarchicalContainer) (this.categoryTree.getContainerDataSource());
        for (Object item : hc.getItemIds()) {
            KoulutusResultRow curRow = (KoulutusResultRow) (categoryTree.getContainerProperty(item, COLUMN_A).getValue());
            curRow.getIsSelected().setValue(selected);
        }

        setPageLength(hc.getItemIds().size());
    }

    /**
     * Creation of the button bar part above the Koulutus-listing.
     *
     * @return
     */
    private void buildMiddleResultLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);
        btnSiirraJaKopioi = UiBuilder.buttonSmallSecodary(layout, i18n.getMessage("siirraTaiKopioi"));
        btnSiirraJaKopioi.setEnabled(false);

        synchronizer.synchronize(btnSiirraJaKopioi, new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent clickEvent) {
                List<KoulutusPerustieto> valitutKoulutukset = presenter.getSelectedKoulutukset();

                if (valitutKoulutukset != null && valitutKoulutukset.size() > 0) {
                    if (valitutKoulutukset.size() > 1) {
                        getWindow().showNotification(i18n.getMessage("yksiKopioitavaKoulutus"));
                    } else {
                        presenter.getModel().setSelectedKoulutusOid(valitutKoulutukset.get(0).getKomotoOid());
                        KoulutusKopiointiDialog kopiointiDialog = new KoulutusKopiointiDialog("600px", "550px", valitutKoulutukset.get(0).getKoulutusasteTyyppi());

                        getWindow().addWindow(synchronizer.synchronize(kopiointiDialog));

                    }

                }
            }
        });

        luoHakukohdeB = UiBuilder.buttonSmallSecodary(layout, i18n.getMessage("LuoHakukohde"));
        synchronizer.synchronize(luoHakukohdeB, new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                IntTuple tuple = checkForLukioKoulutus();
                if (tuple.getValOne() < 1) {
                    List<String> selectedKoulutusOids = presenter.getSelectedKoulutusOids();
                    if (!selectedKoulutusOids.isEmpty()) {

                        showCreateHakukohdeDialog(selectedKoulutusOids);
                    }
                } else {
                    if (tuple.getValOne() > 1 || tuple.getValTwo() > tuple.getValOne()) {
                        final Window dialog = new Window();
                        NoKoulutusDialog koulutusDialog = new NoKoulutusDialog("lukioHakukohdeTooMany", new Button.ClickListener() {
                            private static final long serialVersionUID = 5019806363620874205L;

                            @Override
                            public void buttonClick(ClickEvent clickEvent) {
                                getWindow().removeWindow(dialog);
                            }
                        });
                        dialog.setContent(koulutusDialog);
                        dialog.setWidth("500px");
                        dialog.setHeight("200px");
                        dialog.setModal(true);
                        dialog.center();
                        getWindow().addWindow(synchronizer.synchronize(dialog));
                    } else {
                        //presenter.getTarjoaja().setSelectedOrganisationOid(presenter.getModel().getSelectedKoulutukset().get(0).getTarjoaja().getTarjoajaOid());
                        presenter.showHakukohdeEditView(null, null, presenter.getSelectedKoulutusOidNameViewModels(), null);
                        presenter.getTarjoaja().setSelectedResultRowOrganisationOid(
                                presenter.getModel().getSelectedKoulutukset().get(0).getTarjoaja().getOid());
                    }
                }
            }
        });

        //Creating the create koulutus button
        luoKoulutusB = UiBuilder.buttonSmallSecodary(layout, i18n.getMessage("LuoKoulutus"));
        synchronizer.synchronize(luoKoulutusB, new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (presenter.availableKoulutus()) {
                    UusiKoulutusDialog uusiKoulutusDialog = new UusiKoulutusDialog("800px", "476px");
                    getWindow().addWindow(synchronizer.synchronize(uusiKoulutusDialog));
                } else {
                    showNoKoulutusDialog("viesti");
                }
            }
        });

        //Creating the sorting options combobox
        cbJarjestys = UiUtil.comboBox(layout, null, new String[]{I18N.getMessage("ListKoulutusView.jarjestys.Organisaatio")});//ORDER_BY);
        cbJarjestys.setWidth("300px");
        layout.setExpandRatio(luoKoulutusB, 1f);
        layout.setComponentAlignment(luoKoulutusB, Alignment.TOP_RIGHT);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);
        addComponent(layout);
    }

    private IntTuple checkForLukioKoulutus() {
        int lukioKoulutusCounter = 0;
        int koulutusCounter = 0;
        for (KoulutusPerustieto koulutus : presenter.getSelectedKoulutukset()) {
            koulutusCounter++;
            if (koulutus.getKoulutusasteTyyppi() != null && koulutus.getKoulutusasteTyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
                lukioKoulutusCounter++;
            }
        }
        IntTuple tuple = new IntTuple();
        tuple.setValOne(lukioKoulutusCounter);
        tuple.setValTwo(koulutusCounter);
        return tuple;
    }

    private void showNoKoulutusDialog(String msg) {

        NoKoulutusDialog noKoulutusView = new NoKoulutusDialog(msg, new Button.ClickListener() {
            private static final long serialVersionUID = -5998239901946190160L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeKoulutusDialog();
            }
        });
        koulutusDialog = new TarjontaDialogWindow(noKoulutusView, i18n.getMessage("noKoulutusLabel"));
        koulutusDialog.setWidth("300px");
        koulutusDialog.setHeight("200px");
        getWindow().addWindow(koulutusDialog);
    }

    public void closeKoulutusDialog() {
        if (koulutusDialog != null) {
            getWindow().removeWindow(koulutusDialog);
        }
    }

    private void showCreateHakukohdeDialog(List<String> oids) {
        createDialog = presenter.createHakukohdeCreationDialogWithKomotoOids(oids);
        createButtonListeners();
        createDialog.setWidth("600px");
        createHakukohdeDialog = new Window();
        createHakukohdeDialog.setContent(createDialog);
        createHakukohdeDialog.setModal(true);
        createHakukohdeDialog.center();
        createHakukohdeDialog.setCaption(I18N.getMessage("HakukohdeCreationDialog.windowTitle"));
        getWindow().addWindow(createHakukohdeDialog);
    }

    private void createButtonListeners() {

        createDialog.getPeruutaBtn().addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (createHakukohdeDialog != null) {
                    getWindow().removeWindow(createHakukohdeDialog);
                }
            }
        });


        createDialog.getJatkaBtn().addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createDialog.removeErrorMessages();
                Object values = createDialog.getOptionGroup().getValue();
                Collection<KoulutusOidNameViewModel> selectedKoulutukses = null;
                if (values instanceof Collection) {
                    selectedKoulutukses = (Collection<KoulutusOidNameViewModel>) values;
                }


                if (selectedKoulutukses != null && selectedKoulutukses.size() > 0) {
                    List<String> validationErrors = presenter.validateKoulutusOidNameViewModel(selectedKoulutukses);
                    if (validationErrors != null && validationErrors.size() > 0) {
                        for (String validationError : validationErrors) {
                            createDialog.addErrorMessage(validationError);
                        }
                    } else {
                        getWindow().removeWindow(createHakukohdeDialog);
                        List<KoulutusOidNameViewModel> selectedKoulutusList = new ArrayList<KoulutusOidNameViewModel>(selectedKoulutukses);
                        presenter.setModelSelectedKoulutusOidAndNames(selectedKoulutusList);
                        presenter.showHakukohdeEditView(koulutusNameViewModelToOidList(selectedKoulutukses), null, null, null);

                    }

                }


            }
        });

    }

    private List<String> koulutusNameViewModelToOidList(Collection<KoulutusOidNameViewModel> models) {
        List<String> oids = new ArrayList<String>();
        for (KoulutusOidNameViewModel model : models) {
            oids.add(model.getKoulutusOid());
        }
        return oids;
    }

    public void closeHakukohdeCreationDialog() {
        if (createHakukohdeDialog != null) {
            getWindow().removeWindow(createHakukohdeDialog);
        }
    }

    /**
     * Reloads the data to the Hakukohde list.
     */
    public void reload() {
        errorView.resetErrors();
        clearAllDataItems();
        this.btnSiirraJaKopioi.setEnabled(false);
        this.luoHakukohdeB.setEnabled(false);
        Map<String, List<KoulutusPerustieto>> koulutusDataSource = presenter.getKoulutusDataSource();
        categoryTree.setContainerDataSource(createDataSource(koulutusDataSource));
        setPageLength(categoryTree.getItemIds().size());
        attachTree();
    }

    private void attachTree() {
        //prepare data source unattached and then set it to the treetable 
        addComponent(categoryTree);
        setExpandRatio(categoryTree, 1f);
        refreshLayout();
    }

    public void toggleCreateKoulutusB(String organisaatioOid, boolean b) {
        luoKoulutusB.setEnabled(b && presenter.getPermission().userCanCreateKoulutus(OrganisaatioContext.getContext(organisaatioOid)));
    }

    public void toggleCreateHakukohdeB(String organisaatioOid, boolean b) {
        boolean enabled = b && presenter.getPermission().userCanCreateHakukohde(OrganisaatioContext.getContext(organisaatioOid));
        this.luoHakukohdeB.setEnabled(enabled);
        this.btnSiirraJaKopioi.setEnabled(enabled);
    }

    /**
     * Clear all data items from a tree component.
     */
    public void clearAllDataItems() {
        addAndRebuildTutkintoResultList(); //will remove all items
        // categoryTree.removeAllItems();
    }

    public void setPageLength(int pageLength) {
        categoryTree.setPageLength(pageLength + 1);
    }

    private static class IntTuple {

        private int valOne;
        private int valTwo;

        public int getValOne() {
            return valOne;
        }

        public void setValOne(int valOne) {
            this.valOne = valOne;
        }

        public int getValTwo() {
            return valTwo;
        }

        public void setValTwo(int valTwo) {
            this.valTwo = valTwo;
        }
    }

    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }

    public void showHakukohteetForKoulutus(List<HakukohdePerustieto> hakukohteet,
            KoulutusPerustieto koulutus) {

        ShowHakukohteetDialog hakukohteetDialog = new ShowHakukohteetDialog(hakukohteet, koulutus, presenter);
        koulutusDialog = new TarjontaDialogWindow(hakukohteetDialog, T("hakukohteetDialog"));
        getWindow().addWindow(koulutusDialog);
    }

    /**
     * Refresh layout view.
     */
    public void refreshLayout() {
        if (categoryTree != null) {
            setWidth("100%");
            categoryTree.setWidth("100%");
        }
    }

    public void synchronizeKoulutusSelections() {
        presenter.getSelectedKoulutukset().clear();
        presenter.getSelectedKoulutukset().addAll(getCheckedKoulutukset());
    }

    private List<KoulutusPerustieto> getCheckedKoulutukset() {

        List<KoulutusPerustieto> checkedKoulutukset = new ArrayList<KoulutusPerustieto>();
        if (categoryTree == null || categoryTree.getContainerDataSource() == null) {
            return checkedKoulutukset;
        }
        for (KoulutusPerustieto curKoulutus : presenter.getModel().getKoulutukset()) {
            if (categoryTree.getContainerDataSource().getContainerProperty(curKoulutus, COLUMN_A) == null
                    || categoryTree.getContainerDataSource().getContainerProperty(curKoulutus, COLUMN_A).getValue() == null) {
                continue;
            }
            KoulutusResultRow curRow = (KoulutusResultRow) (categoryTree.getContainerDataSource().getContainerProperty(curKoulutus, COLUMN_A).getValue());
            if (curRow.getIsSelected().booleanValue()) {
                checkedKoulutukset.add(curKoulutus);
            }
        }
        return checkedKoulutukset;
    }
    
    
    private final Predicate<Object> filter(final Class type) {
        return new Predicate<Object>() {
            public boolean apply(Object o) {
                return o.getClass() == type;
            }
        };
    }
    
    /**
     * Event listeneri joka saa viestejä KoulutusPerustietopuun muutostarpeista, katso
     * {@link TarjontaPresenter#sendEvent(Object)}
     */
    @Subscribe 
    public void receiveKoulutusContainerEvent(KoulutusContainerEvent e) {

        LOG.debug("Received container event");

        final String eventKoulutusOid = e.oid;
    
        switch (e.type) {
        case DELETE:
            LOG.debug("delete event");
            
            for(Object itemid: Iterables.filter(categoryTree.getItemIds(), filter(KoulutusPerustieto.class))){
                    final KoulutusPerustieto currentKoulutus = (KoulutusPerustieto)itemid;
                    if(currentKoulutus.getKomotoOid().equals(eventKoulutusOid)) {
                        categoryTree.removeItem(currentKoulutus);
                    }
            }
            break;

        case UPDATE:
            LOG.debug("update event");
            for(Object itemid: Iterables.filter(categoryTree.getItemIds(), filter(KoulutusPerustieto.class))){
                    KoulutusPerustieto currentKoulutus = (KoulutusPerustieto)itemid;
                    if(currentKoulutus.getKomotoOid().equals(eventKoulutusOid)) {
                        //hae tuore koulutus
                        final KoulutusPerustieto freshKoulutus = presenter.findKoulutusByKoulutusOid(eventKoulutusOid).getKoulutukset().get(0);
                        copyData(currentKoulutus, freshKoulutus);
                        final KoulutusResultRow curRow = (KoulutusResultRow) (categoryTree.getContainerProperty(itemid, COLUMN_A).getValue());
                        setKoulutusRowProperties(currentKoulutus);
                        curRow.reinitMenubar();
                    }
            }
            break;

        case CREATE:
            LOG.debug("create event");

            final KoulutusPerustieto freshKoulutus = presenter.findKoulutusByKoulutusOid(eventKoulutusOid).getKoulutukset().get(0);
            Object parent = findParent(freshKoulutus.getTarjoaja().getOid());
            if(parent==null) {
                //need to add new org to tree, falling back to reload for now!
                reload();
            } else {
                addKoulutusRow(parent, freshKoulutus);
                //TODO increase counter?
            }
            
            
            break;
            
        default:
            LOG.warn("event not processed:" + e);
            break;
        }
    }

    /**
     * Etsi organisaatio
     * @param tarjoajaOid
     * @return
     */
    private Object findParent(String tarjoajaOid) {
        for(Object itemId: Iterables.filter(categoryTree.getItemIds(), filter(Integer.class))){
            final KoulutusResultRow curRow = (KoulutusResultRow) (categoryTree.getContainerProperty(itemId, COLUMN_A).getValue());
            if (curRow.getChildren() != null) {
                if (tarjoajaOid.equals(curRow.getChildren().get(0)
                        .getTarjoaja().getOid())) {
                    return itemId;
                }
            }

        }
        return null;
    }

    private void copyData(Object to,
            final Object from) {
        try {
            BeanUtils.copyProperties(to,  from);
        } catch (Throwable t) {
            LOG.warn("Could not copy properties from " + from.getClass() + " to " + to.getClass(), t);
        }
    }

}
