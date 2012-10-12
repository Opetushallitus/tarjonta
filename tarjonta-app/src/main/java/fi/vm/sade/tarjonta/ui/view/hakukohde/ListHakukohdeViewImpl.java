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
import fi.vm.sade.tarjonta.ui.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.haku.ListHakuViewImpl;
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
    
    private static final Logger LOG = LoggerFactory.getLogger(ListHakukohdeViewImpl.class);
    /**
     * Button for adding selected Hakukohde objects to a Haku.
     */
    private Button lisaaHakuunB;
    
    /**
     * Button for removing selected Hakukohde objects.
     */
    private Button poistaB;
    
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

    public ListHakukohdeViewImpl() {
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
     * Sets the datasource for the hierarchical listing of Hakukohde objects.
     */
    @PostConstruct
    public void setDataSource() {
        presenter.setHakukohdeListView(this);
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getHakukohdeDataSource()));
    }
    
    /**
     * Creates the vaadin HierarchicalContainer datasource for the Hakukohde listing
     * based on data provided by the presenter.
     * @param map the data map provided by the presenter.
     * @return the hierarchical container for Hakukokhde listing.
     */
    private Container createDataSource(Map<String, List<HakukohdeViewModel>> map) {
        Set<Map.Entry<String, List<HakukohdeViewModel>>> set = map.entrySet();

        HierarchicalContainer hc = new HierarchicalContainer();
        HakukohdeResultRow rowStyleDef = new HakukohdeResultRow();
        hc.addContainerProperty(COLUMN_A, HakukohdeResultRow.class, rowStyleDef.format("", false));

        for (Map.Entry<String, List<HakukohdeViewModel>> e : set) {
            LOG.info("getTreeDataSource()" + e.getKey());
            HakukohdeResultRow rowStyle = new HakukohdeResultRow();
           
            Object rootItem = hc.addItem();

            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(e.getKey(), false));

            for (HakukohdeViewModel curHakukohde : e.getValue()) {
                HakukohdeResultRow rowStyleInner = new HakukohdeResultRow(curHakukohde);
                hc.addItem(curHakukohde);
                hc.setParent(curHakukohde, rootItem);
                hc.getContainerProperty(curHakukohde, COLUMN_A).setValue(rowStyleInner.format(curHakukohde.getHakukohdeNimi(), true));
                hc.setChildrenAllowed(curHakukohde, false);
                
                rowStyleInner.addListener(new Listener() {

                    @Override
                    public void componentEvent(Event event) {
                        if (event instanceof HakukohdeResultRow.HakukohdeRowMenuEvent) {
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
            HakukohdeResultRow curRow = (HakukohdeResultRow)(categoryTree.getContainerProperty(item, COLUMN_A).getValue());
            curRow.getIsSelected().setValue(selected);
        }
    }

    /**
     * Creation of the button bar part above the Hakukohde-listing.
     * @return
     */
    private HorizontalLayout buildMiddleResultLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);



        lisaaHakuunB = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("LisaaHakuun"));
        lisaaHakuunB.addStyleName(Oph.BUTTON_SMALL);

        lisaaHakuunB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigateToHakukohdeEditForm();
                
            }
        });

        poistaB = UiUtil.button(layout, i18n.getMessage("Poista"));
        poistaB.addStyleName(Oph.BUTTON_SMALL);
        //btnPoista.setEnabled(false);
        poistaB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.removeSelectedHakukohteet();
                
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
     * @param btnListenerMuokkaa( the btnLuoUusiKoulutus to set
     */
    public void setBtnListenerMuokkaa(Button.ClickListener btnKopioiUudelleKaudelle) {
        this.lisaaHakuunB.addListener(btnKopioiUudelleKaudelle);
    }

    /**
     * Reloads the data to the Hakukohde list.
     */
    @Override
    public void reload() {
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getHakukohdeDataSource()));
    }
    
    /**
     * fires event to signal navigation to Hakukohde edit form.
     */
    private void navigateToHakukohdeEditForm() {
        fireEvent(new NewHakukohdeEvent(this));
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
