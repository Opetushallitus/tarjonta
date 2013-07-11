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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
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
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
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
    public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListKoulutusView.jarjestys.Organisaatio")};
    public static final String COLUMN_A = "Kategoriat";
    public static final String COLUMN_TUTKINTONIMIKE = "Tutkintonimike";
    public static final String COLUMN_PVM = "Ajankohta";
    public static final String COLUMN_KOULUTUSLAJI = "Koulutuslaji";
    public static final String COLUMN_TILA = "Tila";
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
        LOG.debug("attach : ListKoulutusView()");
        isAttached = true;

        //Initialization of the view layout

        //Creation of the button bar above the Hakukohde hierarchical/grouped list.
        buildMiddleResultLayout();

        //Adding the select all checkbox.
        addValitsekaikki();



        /**
         * Sets the datasource for the hierarchical listing of Koulutus objects.
         */
        luoKoulutusB.setEnabled(presenter.getNavigationOrganisation().isOrganisationSelected());
        luoHakukohdeB.setEnabled(!presenter.getModel().getSelectedKoulutukset().isEmpty());
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
        categoryTree.addContainerProperty(COLUMN_A, KoulutusResultRow.class, new KoulutusResultRow());
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
                //update size of the visible parent and children rows 
                setPageLength(categoryTree.getItemIds().size());
            }
        });
        categoryTree.setSizeFull();
        addComponent(categoryTree);
        setExpandRatio(categoryTree, 1f);
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

    private String getKoulutusNimi(KoulutusTulos koulutus, Map<KoulutusTulos, String> cache) {
        String ret = cache.get(koulutus);
        if (ret == null) {
            ret = uiHelper.getKoulutusNimi(koulutus);
            cache.put(koulutus, ret);
        }
        return ret;
    }

    /**
     * Creates the vaadin HierarchicalContainer datasource for the Koulutus
     * listing based on data provided by the presenter.
     *
     * @param map the data map provided by the presenter.
     * @return the hierarchical container for Koulutus listing.
     */
    private Container createDataSource(Map<String, List<KoulutusTulos>> map) {

        Set<Map.Entry<String, List<KoulutusTulos>>> set = map.entrySet();

        HierarchicalContainer hc = new HierarchicalContainer();
        KoulutusResultRow rowStyleDef = new KoulutusResultRow();

        hc.addContainerProperty(COLUMN_A, KoulutusResultRow.class, rowStyleDef.format("", false));
        hc.addContainerProperty(COLUMN_PVM, String.class, "");
        hc.addContainerProperty(COLUMN_KOULUTUSLAJI, String.class, "");
        hc.addContainerProperty(COLUMN_TILA, String.class, "");


        // väliaikainen kakku nimille jottei haeta koodistosta moneen kertaan järjestyksen yhteydessä
        final Map<KoulutusTulos, String> nimet = new HashMap<KoulutusTulos, String>();


        for (Map.Entry<String, List<KoulutusTulos>> e : set) {
            //LOG.debug("getTreeDataSource()" + e.getKey());
            KoulutusResultRow rowStyle = new KoulutusResultRow();

            Collections.sort(e.getValue(), new Comparator<KoulutusTulos>() {
                @Override
                public int compare(KoulutusTulos a, KoulutusTulos b) {
                    return getKoulutusNimi(a, nimet).compareTo(getKoulutusNimi(b, nimet));
                }
            });

            Object rootItem = hc.addItem();

            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(buildOrganisaatioCaption(e), false));

            for (KoulutusTulos curKoulutus : e.getValue()) {
                KoulutusResultRow rowStyleInner = new KoulutusResultRow(curKoulutus, getKoulutusNimi(curKoulutus, nimet));

                hc.addItem(curKoulutus);
                hc.setParent(curKoulutus, rootItem);
                hc.getContainerProperty(curKoulutus, COLUMN_A).setValue(rowStyleInner.format(uiHelper.getKoulutusNimi(curKoulutus), true));
                hc.getContainerProperty(curKoulutus, COLUMN_PVM).setValue(uiHelper.getAjankohtaStr(curKoulutus));
                hc.getContainerProperty(curKoulutus, COLUMN_KOULUTUSLAJI).setValue(uiHelper.getKoulutuslaji(curKoulutus));
                hc.getContainerProperty(curKoulutus, COLUMN_TILA).setValue(getTilaStr(curKoulutus.getKoulutus().getTila().name()));
                hc.setChildrenAllowed(curKoulutus, false);
            }
        }
        return hc;
    }

    private String getKoulutusTutkintoNimike(KoulutusTulos curKoulutus) {
        if (curKoulutus.getKoulutus().getTarjoaja() != null) {
            return uiHelper.getKoodiNimi(curKoulutus.getKoulutus().getTutkintonimike());
        }
        return "";
    }

    private String buildOrganisaatioCaption(Map.Entry<String, List<KoulutusTulos>> e) {
        return e.getKey() + " (" + e.getValue().size() + ")";
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
                List<KoulutusTulos> valitutKoulutukset = presenter.getSelectedKoulutukset();

                if (valitutKoulutukset != null && valitutKoulutukset.size() > 0) {
                    if (valitutKoulutukset.size() > 1) {
                        getWindow().showNotification(i18n.getMessage("yksiKopioitavaKoulutus"));
                    } else {
                        presenter.getModel().setSelectedKoulutusOid(valitutKoulutukset.get(0).getKoulutus().getKomotoOid());
                        KoulutusKopiointiDialog kopiointiDialog = new KoulutusKopiointiDialog("600px", "550px", valitutKoulutukset.get(0).getKoulutus().getKoulutustyyppi());

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
                        //presenter.getTarjoaja().setSelectedOrganisationOid(presenter.getModel().getSelectedKoulutukset().get(0).getKoulutus().getTarjoaja().getTarjoajaOid());
                        presenter.showHakukohdeEditView(null, null, presenter.getSelectedKoulutusOidNameViewModels(), null);
                        presenter.getTarjoaja().setSelectedResultRowOrganisationOid(
                                presenter.getModel().getSelectedKoulutukset().get(0).getKoulutus().getTarjoaja().getTarjoajaOid());
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
        cbJarjestys = UiUtil.comboBox(layout, null, ORDER_BY);
        cbJarjestys.setWidth("300px");
        layout.setExpandRatio(luoKoulutusB, 1f);
        layout.setComponentAlignment(luoKoulutusB, Alignment.TOP_RIGHT);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);
        addComponent(layout);
    }

    /**
     * Showing the confirmation dialog for removing multiple haku objects.
     *
     */
    private void showRemoveDialog() {
        MultipleKoulutusRemovalDialog removeDialog = new MultipleKoulutusRemovalDialog(T("removeQ"), T("removeYes"), T("removeNo"), presenter);
        koulutusDialog = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(koulutusDialog);
    }

    private IntTuple checkForLukioKoulutus() {
        int lukioKoulutusCounter = 0;
        int koulutusCounter = 0;
        for (KoulutusTulos koulutus : presenter.getSelectedKoulutukset()) {
            koulutusCounter++;
            if (koulutus.getKoulutus().getKoulutustyyppi() != null && koulutus.getKoulutus().getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
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
        createDialog.setWidth("800px");
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
        clearAllDataItems();
        //this.btnPoista.setEnabled(false);
        this.btnSiirraJaKopioi.setEnabled(false);
        this.luoHakukohdeB.setEnabled(false);
        Map<String, List<KoulutusTulos>> koulutusDataSource = presenter.getKoulutusDataSource();
        categoryTree.setContainerDataSource(createDataSource(koulutusDataSource));
        setPageLength(categoryTree.getItemIds().size());
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

    public void showHakukohteetForKoulutus(List<HakukohdeTulos> hakukohdeTulos,
            KoulutusTulos koulutus) {

        ShowHakukohteetDialog hakukohteetDialog = new ShowHakukohteetDialog(hakukohdeTulos, koulutus, presenter);
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

    private List<KoulutusTulos> getCheckedKoulutukset() {

        List<KoulutusTulos> checkedKoulutukset = new ArrayList<KoulutusTulos>();
        if (categoryTree == null || categoryTree.getContainerDataSource() == null) {
            return checkedKoulutukset;
        }
        for (KoulutusTulos curKoulutus : presenter.getModel().getKoulutukset()) {
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
}
