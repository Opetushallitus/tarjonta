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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

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
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeViewImpl;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 *
 * @author Markus
 */
@Configurable(preConstruction = false)
public class ListKoulutusView extends VerticalLayout {
	

	public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListKoulutusView.jarjestys.Organisaatio")};

	public static final String COLUMN_A = "Kategoriat";

    private static final Logger LOG = LoggerFactory.getLogger(ListHakukohdeViewImpl.class);
    /**
     * Button for editing koulutus objects.
     */
    private Button muokkaaB;

    /**
     * Button for removing selected koulutus objects.
     */
    private Button poistaB;
    
    /**
    * Button for creating a hakukohde object.
    */
   private Button luoHakukohdeB;

   /**
    * Button for creating a koulutus object.
    */
   private Button luoKoulutusB;

    /**
     * Component for selecting desired sorting/grouping criteria for listed Hakukohde objects.
     */
    private ComboBox cbJarjestys;

    /**
     * TreeTable component to display the Hakukohde objects in a grouped/hierarchical manner.
     */
    private CategoryTreeView categoryTree;

    /**
     * Checkbox for selecting all the Hakukohde objects in the list.
     */
    private CheckBox valKaikki;

    private I18NHelper i18n = new I18NHelper(this);

    /**
     * Presenter object for the Hakukohde listing.
     */
    @Autowired(required = true)
    private TarjontaPresenter presenter;

    public ListKoulutusView() {
    	//Initialization of the view layout
        setWidth(UiConstant.PCT100);
        //Creation of the button bar above the Hakukohde hierarchical/grouped list.
        HorizontalLayout buildMiddleResultLayout = buildMiddleResultLayout();
        addComponent(buildMiddleResultLayout);

        //Adding the select all checkbox.
        CssLayout wrapper = UiUtil.cssLayout(UiMarginEnum.BOTTOM);
        valKaikki = new CheckBox(i18n.getMessage("ValitseKaikki"));
        valKaikki.setImmediate(true);
        valKaikki.addListener(new Property.ValueChangeListener() {
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

        setExpandRatio(wrapper, 0.07f);
        setExpandRatio(categoryTree, 0.93f);
        setMargin(true);

    }

    /**
     * Sets the datasource for the hierarchical listing of Koulutus objects.
     */
    @PostConstruct
    public void setDataSource() {
        presenter.setKoulutusListView(this);
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getKoulutusDataSource()));
    }

    /**
     * Creates the vaadin HierarchicalContainer datasource for the Koulutus listing
     * based on data provided by the presenter.
     * @param map the data map provided by the presenter.
     * @return the hierarchical container for Koulutus listing.
     */
    private Container createDataSource(Map<String, List<KoulutusTulos>> map) {
        Set<Map.Entry<String, List<KoulutusTulos>>> set = map.entrySet();

        HierarchicalContainer hc = new HierarchicalContainer();
        KoulutusResultRow rowStyleDef = new KoulutusResultRow();
        hc.addContainerProperty(COLUMN_A, KoulutusResultRow.class, rowStyleDef.format("", false));

        for (Map.Entry<String, List<KoulutusTulos>> e : set) {
            LOG.info("getTreeDataSource()" + e.getKey());
            KoulutusResultRow rowStyle = new KoulutusResultRow();

            Object rootItem = hc.addItem();

            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(e.getKey(), false));

            for (KoulutusTulos curKoulutus : e.getValue()) {
                KoulutusResultRow rowStyleInner = new KoulutusResultRow(curKoulutus);
                hc.addItem(curKoulutus);
                hc.setParent(curKoulutus, rootItem);
                hc.getContainerProperty(curKoulutus, COLUMN_A).setValue(rowStyleInner.format(curKoulutus.getKoulutus().getNimi(), true));
                hc.setChildrenAllowed(curKoulutus, false);

                rowStyleInner.addListener(new Listener() {

                    @Override
                    public void componentEvent(Event event) {
                        if (event instanceof KoulutusResultRow.KoulutusRowMenuEvent) {
                            fireEvent(event);
                        }
                    }
                });
            }
        }
        return hc;
    }

    /**
     * Selects or unselects all the objects in the Hakukohde listing.
     * @param selected
     */
    private void toggleHakuSelections(boolean selected) {
        presenter.getSelectedhakukohteet().clear();
        HierarchicalContainer hc = (HierarchicalContainer)(this.categoryTree.getContainerDataSource());
        for (Object item : hc.getItemIds()) {
            KoulutusResultRow curRow = (KoulutusResultRow)(categoryTree.getContainerProperty(item, COLUMN_A).getValue());
            curRow.getIsSelected().setValue(selected);
        }
    }

    /**
     * Creation of the button bar part above the Hakukohde-listing.
     * @return
     */
    private HorizontalLayout buildMiddleResultLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);



        muokkaaB = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("LisaaHakuun"));
        muokkaaB.addStyleName(Oph.BUTTON_SMALL);

        muokkaaB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigateToKoulutusEditForm();
            }
        });

        poistaB = UiUtil.button(layout, i18n.getMessage("Poista"));
        poistaB.addStyleName(Oph.BUTTON_SMALL);
        //btnPoista.setEnabled(false);
        poistaB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.removeSelectedKoulutukset();
            }
        });
        
        luoHakukohdeB = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("LuoHakukohde"));
        luoHakukohdeB.addStyleName(Oph.BUTTON_SMALL);
        luoHakukohdeB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigateToHakukohdeEditForm();
            }
        });
        
        luoKoulutusB = UiUtil.button(layout, i18n.getMessage("LuoKoulutus"));
        luoKoulutusB.addStyleName(Oph.BUTTON_SMALL);
        //btnPoista.setEnabled(false);
        luoKoulutusB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
            	navigateToKoulutusEditForm();
            }
        });

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
     * Reloads the data to the Hakukohde list.
     */
    public void reload() {
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getKoulutusDataSource()));
    }

    /**
     * fires event to signal navigation to Koulutus edit form.
     */
    private void navigateToKoulutusEditForm() {
        fireEvent(new NewKoulutusEvent(this));
    }
    
    /**
     * fires event to signal mavigation tu Hakukohde edit form.
     */
	private void navigateToHakukohdeEditForm() {
		fireEvent(new NewHakukohdeEvent(this));
	}

    /**
     * Event to signal that the user wants to create a new Koulutus.
    */
    public class NewKoulutusEvent extends Component.Event {

        public NewKoulutusEvent(Component source) {
            super(source);

        }

    }
    
    /**
     * Event to signal that the user wants to create a new Hakukohde.
    */
    public class NewHakukohdeEvent extends Component.Event {

        public NewHakukohdeEvent(Component source) {
            super(source);

        }

    }
    

}
