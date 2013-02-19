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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.ui.enums.RequiredRole;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Component for listing hakukohde objects.
 *
 * @author Markus
 */
@Configurable(preConstruction = false)
public class ListHakukohdeViewImpl extends VerticalLayout implements ListHakukohdeView {

    public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListHakukohdeViewImpl.jarjestys.Organisaatio")};
    public static final String COLUMN_A = "Kategoriat";
    public static final String COLUMN_PVM = "Ajankohta";
    public static final String COLUMN_TILA = "Tila";
    
    private static final Logger LOG = LoggerFactory.getLogger(ListHakukohdeViewImpl.class);
    private static final long serialVersionUID = 60562140590088029L;
    /**
     * Button for adding selected Hakukohde objects to a Haku.
     */
    private Button lisaaHakuunB;
    /**
     * Button for removing selected Hakukohde objects.
     */
    private Button poistaB;
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
    private transient I18NHelper i18n = new I18NHelper(this);
    /**
     * Presenter object for the Hakukohde listing.
     */
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private TarjontaUIHelper _tarjontaUIHelper;
    private boolean attached = false;

    public ListHakukohdeViewImpl() {
        //Initialization of the view layout
        setWidth(UiConstant.PCT100);

    }

    @Override
    public void attach() {
        super.attach();

        if (attached) {
            return;
        }
        attached = true;

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
        setHeight(Sizeable.SIZE_UNDEFINED, 0);
        
        categoryTree.addContainerProperty(COLUMN_A, HakukohdeResultRow.class, new HakukohdeResultRow());
        categoryTree.addContainerProperty(COLUMN_PVM, String.class, "");
        categoryTree.addContainerProperty(COLUMN_TILA, String.class, "");
        
        categoryTree.setColumnExpandRatio(COLUMN_A,  1.0f);
        categoryTree.setColumnExpandRatio(COLUMN_PVM,  0.5f);
        categoryTree.setColumnExpandRatio(COLUMN_TILA,  0.5f);
        

        setExpandRatio(wrapper, 0.07f);
        setExpandRatio(categoryTree, 0.93f);
        setMargin(true);

        presenter.setHakukohdeListView(this);
    }

    /**
     * Sets the datasource for the hierarchical listing of Hakukohde objects.
     */
    public void setDataSource() {
        presenter.setHakukohdeListView(this);
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getHakukohdeDataSource()));
    }

    /**
     * Creates the vaadin HierarchicalContainer datasource for the Hakukohde
     * listing based on data provided by the presenter.
     *
     * @param map the data map provided by the presenter.
     * @return the hierarchical container for Hakukokhde listing.
     */
    private Container createDataSource(Map<String, List<HakukohdeTulos>> map) {
        Set<Map.Entry<String, List<HakukohdeTulos>>> set = map.entrySet();

        HierarchicalContainer hc = new HierarchicalContainer();
        hc.addContainerProperty(COLUMN_A, HakukohdeResultRow.class, new HakukohdeResultRow());
        hc.addContainerProperty(COLUMN_PVM, String.class, "");
        hc.addContainerProperty(COLUMN_TILA, String.class, "");

        for (Map.Entry<String, List<HakukohdeTulos>> e : set) {
            LOG.debug("getTreeDataSource()" + e.getKey());
            HakukohdeResultRow rowStyle = new HakukohdeResultRow();

            Object rootItem = hc.addItem();

            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(buildOrganisaatioCaption(e), false));

            for (HakukohdeTulos curHakukohde : e.getValue()) {
                HakukohdeResultRow rowStyleInner = new HakukohdeResultRow(curHakukohde, getHakukohdeNimi(curHakukohde));
                hc.addItem(curHakukohde);
                hc.setParent(curHakukohde, rootItem);
                hc.getContainerProperty(curHakukohde, COLUMN_A).setValue(rowStyleInner.format(getHakukohdeNimi(curHakukohde), true));
                hc.getContainerProperty(curHakukohde, COLUMN_PVM).setValue(getAjankohta(curHakukohde));
                hc.getContainerProperty(curHakukohde, COLUMN_TILA).setValue(getTilaStr(curHakukohde));
                
                hc.setChildrenAllowed(curHakukohde, false);
            }
        }
        return hc;
    }

    private String buildOrganisaatioCaption(Map.Entry<String, List<HakukohdeTulos>> e) {
        return e.getKey() + " (" + e.getValue().size() + ")";
    }
    
    private String getAjankohta(HakukohdeTulos curHakukohde) {
        return getKoodiNimi(curHakukohde.getHaku().getHakukausiUri()) + " " + curHakukohde.getHaku().getHakuvuosi();
    }
    
    private String getTilaStr(HakukohdeTulos curHakukohde) {
        return i18n.getMessage(curHakukohde.getHakukohde().getTila().name());
    }
    
    private String getHakukohdeNimi(HakukohdeTulos curHakukohde) {
        return getKoodiNimi(curHakukohde.getHakukohde().getNimi());
    }

    /**
     * Returns the name of the hakukohde based on koodisto uri given.
     *
     * @param koodiUri the koodisto uri given.
     * @return
     */
    private String getKoodiNimi(String koodiUri) {
        String nimi = _tarjontaUIHelper.getKoodiNimi(koodiUri, I18N.getLocale());
        if ("".equals(nimi)) {
            nimi = koodiUri;
        }
        return nimi;
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
    }

    /**
     * Creation of the button bar part above the Hakukohde-listing.
     *
     * @return
     */
    private HorizontalLayout buildMiddleResultLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);
        /*
         lisaaHakuunB = UiBuilder.buttonSmallPrimary(layout, i18n.getMessage("LisaaHakuun"), RequiredRole.CRUD, presenter.getPermission());
         lisaaHakuunB.addListener(new Button.ClickListener() {
         @Override
         public void buttonClick(Button.ClickEvent event) {
         getWindow().showNotification("Toiminnallisuutta ei ole toteutettu");

         }
         });
         */
        poistaB = UiBuilder.buttonSmallPrimary(layout, i18n.getMessage("Poista"), RequiredRole.CRUD, presenter.getPermission());
        poistaB.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = 5833582377090856884L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.removeSelectedHakukohteet();
            }
        });
        poistaB.setEnabled(false);

        cbJarjestys = UiUtil.comboBox(layout, null, ORDER_BY);
        cbJarjestys.setWidth("300px");
        layout.setExpandRatio(cbJarjestys, 1f);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);

        Button btnInfo = new Button();
        btnInfo.addStyleName(Oph.BUTTON_INFO);
        layout.addComponent(btnInfo);

        return layout;
    }

    /**
     * @param btnListenerMuokkaa( the btnLuoUusiKoulutus to set
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
    @Override
    public void reload() {
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getHakukohdeDataSource()));
    }

    @Override
    public void appendKoulutuksetToList(HakukohdeViewModel hakukohde) {
        HierarchicalContainer hc = (HierarchicalContainer) (this.categoryTree.getContainerDataSource());
        for (Object item : hc.getItemIds()) {
            if (!(categoryTree.getContainerProperty(item, COLUMN_A).getValue() instanceof HakukohdeResultRow)) {
                continue;
            }
            HakukohdeResultRow curRow = (HakukohdeResultRow) (categoryTree.getContainerProperty(item, COLUMN_A).getValue());
            if (curRow.getHakukohde().getHakukohde() != null && curRow.getHakukohde().getHakukohde().getOid().equals(hakukohde.getOid())) {
                addKoulutuksetToTree(item, hakukohde, hc);
                return;
            }
        }
    }

    private void addKoulutuksetToTree(Object item, HakukohdeViewModel hakukohde, HierarchicalContainer hc) {
        hc.setChildrenAllowed(item, true);
        for (String komotoOid : hakukohde.getKomotoOids()) {
            HakukohdeResultRow rowStyle = new HakukohdeResultRow();
            hc.addItem(komotoOid);
            hc.setParent(komotoOid, item);
            LueKoulutusVastausTyyppi koulutus = presenter.getKoulutusByOid(komotoOid);
            String koulutusNimi = "";
            if (koulutus != null) {
                koulutusNimi = getKoodiNimi(koulutus.getKoulutusKoodi().getUri()) + ", "
                        + getKoodiNimi(koulutus.getKoulutusohjelmaKoodi().getUri());
            }
            hc.getContainerProperty(komotoOid, COLUMN_A).setValue(rowStyle.format(koulutusNimi, false));
            hc.setChildrenAllowed(komotoOid, false);
        }
        this.categoryTree.setCollapsed(item, false);
    }
}
