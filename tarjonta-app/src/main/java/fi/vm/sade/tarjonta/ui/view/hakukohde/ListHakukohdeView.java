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
package fi.vm.sade.tarjonta.ui.view.hakukohde;

import java.util.List;
import java.util.Map;
import java.util.Set;


import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Component for listing hakukohde objects.
 *
 * @author Markus
 */
@Configurable(preConstruction = false)
public class ListHakukohdeView extends VerticalLayout {

    private static final int MAX_PARENT_ROWS = 100;
    public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListHakukohdeViewImpl.jarjestys.Organisaatio")};
    public static final String COLUMN_A = "Kategoriat";
    public static final String COLUMN_PVM = "Ajankohta";
    public static final String COLUMN_KOULUTUSLAJI = "Koulutuslaji";
    public static final String COLUMN_HAKUTAPA = "Hakutapa";
    public static final String COLUMN_ALOITUSPAIKAT = "Aloituspaikat";
    public static final String COLUMN_TILA = "Tila";
    private static final Logger LOG = LoggerFactory.getLogger(ListHakukohdeView.class);
    private static final long serialVersionUID = 60562140590088029L;
    /**
     * Button for adding selected Hakukohde objects to a Haku.
     */
    private Button lisaaHakuunB;
    /**
     * Button for removing selected Hakukohde objects.
     */
    //private Button poistaB;
    /**
     * Component for selecting desired sorting/grouping criteria for listed
     * Hakukohde objects.
     */
    private ComboBox cbJarjestys;
    /**
     * TreeTable component to display the Hakukohde objects in a
     * grouped/hierarchical manner.
     */
    private TreeTable categoryTree;
    /**
     * Checkbox for selecting all the Hakukohde objects in the list.
     */
    private CheckBox valKaikki;
    private TarjontaDialogWindow hakukohdeDialog;
    private transient I18NHelper i18n = new I18NHelper(this);
    /**
     * Presenter object for the Hakukohde listing.
     */
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private boolean attached = false;
    private Set<Map.Entry<String, List<HakukohdePerustieto>>> resultSet;
    private ErrorMessage errorView;

    public ListHakukohdeView() {
        //Initialization of the view layout
        setSizeFull();
        setMargin(true);
    }

    @Override
    public void attach() {
        super.attach();
        presenter.registerEventListener(this);

        if (attached) {
            return;
        }
        attached = true;

        //Creation of the button bar above the Hakukohde hierarchical/grouped list.
        addMiddleResultLayout();

        errorView = new ErrorMessage();
        addComponent(errorView);
        //Adding the select all checkbox.
        addSelectAllButton();

        presenter.setHakukohdeListView(this);
    }
    
    @Override
    public void detach() {
        presenter.unregisterEventListener(this);
    }

    /*
     *  Adding the actual hakukohde-listing component.
     */
    private void addAndRebuildTutkintoResultList() {
        if (categoryTree == null) {
            categoryTree = new CategoryTreeView();
            categoryTree.addContainerProperty(COLUMN_A, HakukohdeResultRow.class, new HakukohdeResultRow());
            categoryTree.addContainerProperty(COLUMN_PVM, String.class, "");
            categoryTree.addContainerProperty(COLUMN_HAKUTAPA, String.class, "");
            categoryTree.addContainerProperty(COLUMN_ALOITUSPAIKAT, String.class, "");
            categoryTree.addContainerProperty(COLUMN_KOULUTUSLAJI, String.class, "");
            categoryTree.addContainerProperty(COLUMN_TILA, String.class, "");

            categoryTree.setColumnExpandRatio(COLUMN_A, 1.9f);
            categoryTree.setColumnExpandRatio(COLUMN_PVM, 0.3f);
            categoryTree.setColumnExpandRatio(COLUMN_HAKUTAPA, 0.4f);
            categoryTree.setColumnExpandRatio(COLUMN_ALOITUSPAIKAT, 0.1f);
            categoryTree.setColumnExpandRatio(COLUMN_KOULUTUSLAJI, 0.3f);
            categoryTree.setColumnExpandRatio(COLUMN_TILA, 0.3f);
            categoryTree.setSizeFull();
            addComponent(categoryTree);
            setExpandRatio(categoryTree, 1f);
        } else {
            //remove items
            categoryTree.getContainerDataSource().removeAllItems();
            //remove listeners
            for(Object o: categoryTree.getListeners(Tree.ExpandListener.class)){
                categoryTree.removeListener((Tree.ExpandListener)o);
            }
            
        }

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
                
                if (item==null || item.getItemProperty(COLUMN_A)==null) {
                	return;
                }
                
                HakukohdeResultRow row = (HakukohdeResultRow) item.getItemProperty(COLUMN_A).getValue();
                categoryTree.getParent(event);
                for (HakukohdePerustieto curHakukohde : row.getChildren()) {
                    HakukohdeResultRow rowStyleInner = new HakukohdeResultRow(curHakukohde, getHakukohdeNimi(curHakukohde));
                    categoryTree.addItem(curHakukohde);
                    categoryTree.setParent(curHakukohde, event.getItemId());
                    categoryTree.getContainerProperty(curHakukohde, COLUMN_A).setValue(rowStyleInner.format(getHakukohdeNimi(curHakukohde), true));
                    setHakukohdeRowProperties(curHakukohde, rowStyleInner);
                    categoryTree.setChildrenAllowed(curHakukohde, false);
                }
                setPageLength(categoryTree.getItemIds().size());
            }
        });

    }

    private void addSelectAllButton() {
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
     * Creates the vaadin HierarchicalContainer datasource for the Hakukohde
     * listing based on data provided by the presenter.
     *
     * @param map the data map provided by the presenter.
     * @return the hierarchical container for Hakukokhde listing.
     */
    private Container createDataSource(Map<String, List<HakukohdePerustieto>> map) {
        resultSet = map.entrySet();

        HierarchicalContainer hc = new HierarchicalContainer();
        hc.addContainerProperty(COLUMN_A, HakukohdeResultRow.class, new HakukohdeResultRow());
        hc.addContainerProperty(COLUMN_PVM, String.class, "");
        hc.addContainerProperty(COLUMN_HAKUTAPA, String.class, "");
        hc.addContainerProperty(COLUMN_ALOITUSPAIKAT, String.class, "");
        hc.addContainerProperty(COLUMN_KOULUTUSLAJI, String.class, "");
        hc.addContainerProperty(COLUMN_TILA, String.class, "");

        int index = 0;
        for (Map.Entry<String, List<HakukohdePerustieto>> e : resultSet) {
            if (index > MAX_PARENT_ROWS) {
                //A quick hack, it would be great, if data was limited in back-end service.
                errorView.addError(I18N.getMessage("liianMontaHakutulosta"));
                break;
            }
            //LOG.debug("getTreeDataSource()" + e.getKey());

            HakukohdeResultRow rowStyle = new HakukohdeResultRow();
            rowStyle.setRowKey(e.getKey());
            rowStyle.setChildren(e.getValue());
            Object rootItem = hc.addItem();
            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(buildOrganisaatioCaption(e), false));
            index++;
        }
        return hc;
    }

    private String getHakutapa(HakukohdePerustieto curHakukohde) {
        return TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(),  curHakukohde.getHakutapaNimi());
    }

    private String buildOrganisaatioCaption(Map.Entry<String, List<HakukohdePerustieto>> e) {
        return e.getKey() + " (" + e.getValue().size() + ")";
    }

    private String getAjankohta(HakukohdePerustieto curHakukohde) {
        return curHakukohde.getKoulutuksenAlkamiskausiUri() + " " + curHakukohde.getKoulutuksenAlkamisvuosi();
    }

    private String getTilaStr(HakukohdePerustieto curHakukohde) {
        return i18n.getMessage(curHakukohde.getTila().name());
    }

    private String getHakukohdeNimi(HakukohdePerustieto curHakukohde) {
        return TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), curHakukohde.getNimi());
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
            HakukohdeResultRow curRow = (HakukohdeResultRow) (categoryTree.getContainerProperty(item, COLUMN_A).getValue());
            curRow.getIsSelected().setValue(selected);
        }
        setPageLength(hc.getItemIds().size());
    }

    /**
     * Creation of the button bar part above the Hakukohde-listing.
     *
     * @return
     */
    private void addMiddleResultLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);
        cbJarjestys = UiUtil.comboBox(layout, null, ORDER_BY);
        cbJarjestys.setWidth("300px");
        layout.setExpandRatio(cbJarjestys, 1f);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);
        addComponent(layout);
    }

    public void closeRemoveDialog() {
        if (hakukohdeDialog != null) {
            getWindow().removeWindow(hakukohdeDialog);
        }
    }

    /**
     * @param btnKopioiUudelleKaudelle
     */
    public void setBtnListenerMuokkaa(Button.ClickListener btnKopioiUudelleKaudelle) {
        this.lisaaHakuunB.addListener(btnKopioiUudelleKaudelle);
    }

    public void showErrorMessage(String msg) {
        getWindow().showNotification(msg, Notification.TYPE_ERROR_MESSAGE);
    }

    /**
     * Reloads the data to the Hakukohde list.
     */
    public void reload() {
        errorView.resetErrors();
        clearAllDataItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getHakukohdeDataSource()));
        setPageLength(categoryTree.getItemIds().size());
    }

    public void showKoulutuksetForHakukohde(List<KoulutusPerustieto> koulutukset, HakukohdePerustieto hakukohde) {
        ShowKoulutuksetDialog koulutusDialog = new ShowKoulutuksetDialog(koulutukset, hakukohde, presenter);
        hakukohdeDialog = new TarjontaDialogWindow(koulutusDialog, T("koulutusDialog"));
        getWindow().addWindow(hakukohdeDialog);

    }

    public void clearAllDataItems() {
        addAndRebuildTutkintoResultList();
    }

    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }

    public void closeDialog() {
        if (hakukohdeDialog != null) {
            getWindow().removeWindow(hakukohdeDialog);
            hakukohdeDialog = null;
        }
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

    public void setPageLength(int pageLength) {
        categoryTree.setPageLength(pageLength + 1);
    }
    
    /**
     * Event listeneri joka saa viestejä hakutulospuun muutostarpeista, katso
     * {@link TarjontaPresenter#sendEvent(Object)}
     */
    @Subscribe 
    public void receiveHakukohdeContainerEvent(HakukohdeContainerEvent e) {
        
        final String eventHakukohdeOid = e.oid;
    
        switch (e.type) {
        case REMOVE:
            for(Object itemid: categoryTree.getItemIds()){
                if (itemid.getClass() == HakukohdePerustieto.class) {
                    HakukohdePerustieto currentHakukohde = (HakukohdePerustieto)itemid;
                    if(currentHakukohde.getOid().equals(eventHakukohdeOid)) {
                        categoryTree.removeItem(currentHakukohde);
                    }
            
                }
            }
            break;

        case UPDATE:
            
            for(Object itemid: categoryTree.getItemIds()){
                if (itemid.getClass() == HakukohdePerustieto.class) {
                    HakukohdePerustieto currentHakukohde = (HakukohdePerustieto)itemid;
                    if(currentHakukohde.getOid().equals(eventHakukohdeOid)) {
                        //hae tuore hakukohde
                        final HakukohdePerustieto freshHakukohde = presenter.findHakukohdeByHakukohdeOid(eventHakukohdeOid).getHakukohteet().get(0); 
                        copyData(currentHakukohde, freshHakukohde);
                        final HakukohdeResultRow curRow = (HakukohdeResultRow) (categoryTree.getContainerProperty(itemid, COLUMN_A).getValue());
                        setHakukohdeRowProperties(currentHakukohde, curRow);
                        curRow.reinitMenubar();
                    }
                }
            }
            
            break;
            
        default:
            LOG.warn("event not processed:" + e);
            break;
        }
    }

    private void copyData(Object to,
            final Object from) {
        try {
            BeanUtils.copyProperties(to,  from);
        } catch (Throwable t) {
            LOG.warn("Could not copy properties from " + from.getClass() + " to " + to.getClass(), t);
        }
    }

    private void setHakukohdeRowProperties(final HakukohdePerustieto curHakukohde,
            final HakukohdeResultRow rowStyleInner) {
        categoryTree.getContainerProperty(curHakukohde, COLUMN_PVM).setValue(getAjankohta(curHakukohde));
        categoryTree.getContainerProperty(curHakukohde, COLUMN_HAKUTAPA).setValue(getHakutapa(curHakukohde));
        categoryTree.getContainerProperty(curHakukohde, COLUMN_ALOITUSPAIKAT).setValue(curHakukohde.getAloituspaikat());
        categoryTree.getContainerProperty(curHakukohde, COLUMN_KOULUTUSLAJI).setValue(TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(),  curHakukohde.getKoulutuslaji().getNimi()));
        categoryTree.getContainerProperty(curHakukohde, COLUMN_TILA).setValue(getTilaStr(curHakukohde));
    }
}
