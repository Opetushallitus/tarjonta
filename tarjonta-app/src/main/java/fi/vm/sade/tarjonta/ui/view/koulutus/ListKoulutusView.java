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

import java.util.*;


import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.view.hakukohde.CreationDialog;
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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.ui.enums.RequiredRole;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.vaadin.Oph;
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
    /**
     * Presenter object for the Hakukohde listing.
     */
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private Window createHakukohdeDialog;
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

    private CreationDialog<KoulutusOidNameViewModel> createDialog;
    private Button btnPoista;
    private Button btnMuokkaa;
    private Button btnSiirraJaKopioi;
    private I18NHelper i18n = new I18NHelper(this);
    private boolean isAttached = false;

    public ListKoulutusView() {
        setWidth(100, UNITS_PERCENTAGE);
        setHeight(-1, UNITS_PIXELS);
        setMargin(true);

        if (presenter == null) {
            presenter = new TarjontaPresenter();
        }
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
        HorizontalLayout buildMiddleResultLayout = buildMiddleResultLayout();
        addComponent(buildMiddleResultLayout);

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

        //Adding the actual Hakukohde-listing component.
        categoryTree = new CategoryTreeView();
        addComponent(categoryTree);
        setExpandRatio(categoryTree, 1f);

        /**
         * Sets the datasource for the hierarchical listing of Koulutus objects.
         */
        luoKoulutusB.setEnabled(presenter.getModel().getOrganisaatioOid() != null);
        luoHakukohdeB.setEnabled(!presenter.getModel().getSelectedKoulutukset().isEmpty());
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

        for (Map.Entry<String, List<KoulutusTulos>> e : set) {
            LOG.debug("getTreeDataSource()" + e.getKey());
            KoulutusResultRow rowStyle = new KoulutusResultRow();

            Object rootItem = hc.addItem();

            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(buildOrganisaatioCaption(e), false));

            for (KoulutusTulos curKoulutus : e.getValue()) {
                KoulutusResultRow rowStyleInner = new KoulutusResultRow(curKoulutus, getKoulutusNimi(curKoulutus));
                hc.addItem(curKoulutus);
                hc.setParent(curKoulutus, rootItem);
                hc.getContainerProperty(curKoulutus, COLUMN_A).setValue(rowStyleInner.format(buildKoulutusCaption(curKoulutus), true));
                hc.setChildrenAllowed(curKoulutus, false);
            }
        }
        return hc;
    }

    private String buildOrganisaatioCaption(Map.Entry<String, List<KoulutusTulos>> e) {
        return e.getKey() + " (" + e.getValue().size() + ")";
    }

    private String buildKoulutusCaption(KoulutusTulos curKoulutus) {
        String caption = getKoulutusNimi(curKoulutus);
        caption += ", " + getTilaStr(curKoulutus.getKoulutus().getTila().name());
        return caption;
    }

    private String getKoulutusNimi(KoulutusTulos curKoulutus) {
        String nimi = getKoodiNimi(curKoulutus.getKoulutus().getKoulutuskoodi());
        if (curKoulutus.getKoulutus().getKoulutusohjelmakoodi() != null) {
            nimi += ", " + getKoodiNimi(curKoulutus.getKoulutus().getKoulutusohjelmakoodi());
        }
        nimi += ", " + curKoulutus.getKoulutus().getAjankohta();
        return nimi;
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
    }

    /**
     * Creation of the button bar part above the Koulutus-listing.
     *
     * @return
     */
    private HorizontalLayout buildMiddleResultLayout() {
        LOG.debug("buildMiddleResultLayout()");
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);
        layout.setSizeFull();

        //Creating the create hakukohde button
        btnMuokkaa = UiBuilder.buttonSmallPrimary(layout, i18n.getMessage("muokkaa"), RequiredRole.UPDATE, presenter.getPermission());
        btnMuokkaa.setEnabled(false);
        btnPoista = UiBuilder.buttonSmallPrimary(layout, i18n.getMessage("poista"), RequiredRole.CRUD, presenter.getPermission());
        btnPoista.setEnabled(false);
        btnSiirraJaKopioi = UiBuilder.buttonSmallPrimary(layout, i18n.getMessage("siirraTaiKopioi"), RequiredRole.CRUD, presenter.getPermission());
        btnSiirraJaKopioi.setEnabled(false);

        luoHakukohdeB = UiBuilder.buttonSmallPrimary(layout, i18n.getMessage("LuoHakukohde"), RequiredRole.CRUD, presenter.getPermission());
        luoHakukohdeB.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                List<String> selectedKoulutusOids = presenter.getSelectedKoulutusOids();
                if (!selectedKoulutusOids.isEmpty()) {
                    showCreateHakukohdeDialog(selectedKoulutusOids);
                }
            }
        });

        //Creating the create koulutus button
        luoKoulutusB = UiBuilder.buttonSmallPrimary(layout, i18n.getMessage("LuoKoulutus"), RequiredRole.CRUD, presenter.getPermission());
        luoKoulutusB.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.showKoulutusPerustiedotEditView(null);
            }
        });

        //Creating the sorting options combobox
        cbJarjestys = UiUtil.comboBox(layout, null, ORDER_BY);
        cbJarjestys.setWidth("300px");
        layout.setExpandRatio(luoKoulutusB, 1f);
        layout.setComponentAlignment(luoKoulutusB, Alignment.TOP_RIGHT);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);

        Button btnInfo = new Button();
        btnInfo.addStyleName(Oph.BUTTON_INFO);
        layout.addComponent(btnInfo);

        return layout;
    }

    private void showCreateHakukohdeDialog(List<String> oids) {
        createDialog = presenter.createHakukohdeCreationDialogWithKomotoOids(oids);
        createButtonListeners();
        createDialog.setWidth("700px");
        createHakukohdeDialog = new Window();
        createHakukohdeDialog.setContent(createDialog);
        createHakukohdeDialog.setModal(true);
        createHakukohdeDialog.center();
        createHakukohdeDialog.setCaption(I18N.getMessage("HakukohdeCreationDialog.windowTitle"));
        getWindow().addWindow(createHakukohdeDialog);
    }

    private void createButtonListeners() {

            createDialog.getPeruutaBtn().addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    if (createHakukohdeDialog != null) {
                        getWindow().removeWindow(createHakukohdeDialog);
                    }
                }
            });


            createDialog.getJatkaBtn().addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    Object values = createDialog.getOptionGroup().getValue();
                    Collection<KoulutusOidNameViewModel> selectedKoulutukses = null;
                    if (values instanceof Collection) {
                        selectedKoulutukses = (Collection<KoulutusOidNameViewModel>) values;
                    }

                    getWindow().removeWindow(createHakukohdeDialog);
                    presenter.showHakukohdeEditView(koulutusNameViewModelToOidList(selectedKoulutukses), null);

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
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getKoulutusDataSource()));
    }

    public void toggleCreateKoulutusB(boolean b) {
        if (presenter.getPermission().userCanReadAndUpdate()) {
            luoKoulutusB.setEnabled(b);
        }
    }

    public void toggleCreateHakukohdeB(boolean b) {
        if (presenter.getPermission().userCanReadAndUpdate()) {
            this.luoHakukohdeB.setEnabled(b);
        }
    }

    /**
     * Returns the name of the hakukohde based on koodisto uri given.
     *
     * @param hakukohdeUri the koodisto uri given.
     * @return
     */
    private String getKoodiNimi(String hakukohdeUri) {
        String nimi = presenter.getUiHelper().getKoodiNimi(hakukohdeUri, I18N.getLocale());
        if ("".equals(nimi)) {
            nimi = hakukohdeUri;
        }
        return nimi;
    }
}
