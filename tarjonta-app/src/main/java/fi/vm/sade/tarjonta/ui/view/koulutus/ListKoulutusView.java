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
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
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
    
    @Autowired(required=true)
    private TarjontaUIHelper _tarjontaUIHelper;

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
        luoKoulutusB.setEnabled(presenter.getModel().getOrganisaatioOid() != null);
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

            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(presenter.getOrganisaatioNimiByOid(e.getKey()), false));

            for (KoulutusTulos curKoulutus : e.getValue()) {
                KoulutusResultRow rowStyleInner = new KoulutusResultRow(curKoulutus);
                hc.addItem(curKoulutus);
                hc.setParent(curKoulutus, rootItem);
                hc.getContainerProperty(curKoulutus, COLUMN_A).setValue(rowStyleInner.format(buildKoulutusCaption(curKoulutus), true));
                hc.setChildrenAllowed(curKoulutus, false);
            }
        }
        return hc;
    }
    
    private String buildKoulutusCaption(KoulutusTulos curKoulutus) {
    	String caption = getKoodiNimi(curKoulutus.getKoulutus().getKoulutuskoodi());
    	if (curKoulutus.getKoulutus().getKoulutusohjelmakoodi() != null) {
    		caption +=  ", " + getKoodiNimi(curKoulutus.getKoulutus().getKoulutusohjelmakoodi());
    	}
    	caption += ", " + curKoulutus.getKoulutus().getAjankohta();
		caption += ", " + getTilaStr(curKoulutus.getKoulutus().getTila());		
		return caption;
    }
    
    private String getTilaStr(String tilaUri) {
    	String[] parts = tilaUri.split("\\/"); 
    	return i18n.getMessage(parts[parts.length-1]);
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
     * Creation of the button bar part above the Koulutus-listing.
     * @return
     */
    private HorizontalLayout buildMiddleResultLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);


        //Creating the edit button
        muokkaaB = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("Muokkaa"));
        muokkaaB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
            	getWindow().showNotification("Toiminnallisuutta ei ole toteutettu");
            }
        });
        
        //Enabloidaan sitten kun toiminnallisuus on toteutettu
        muokkaaB.setEnabled(false);

        //Creating the remove button
        poistaB = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("Poista"));
        poistaB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.removeSelectedKoulutukset();
            }
        });
        poistaB.setEnabled(false);
        
        //Creating the create hakukohde button
        luoHakukohdeB = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("LuoHakukohde"));
        luoHakukohdeB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.showHakukohdeEditView(presenter.getSelectedKoulutusOids(), null);
            }
        });
        
        //Creating the create koulutus button
        luoKoulutusB = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("LuoKoulutus"));
        luoKoulutusB.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
            	presenter.showKoulutusPerustiedotEditView(null);
            }
        });
        

        //Creating the sorting options combobox
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

	public void toggleCreateKoulutusB(boolean b) {
		luoKoulutusB.setEnabled(b);
	}
	
    /**
     * Returns the name of the hakukohde based on koodisto uri given.
     * @param hakukohdeUri the koodisto uri given.
     * @return
     */
    private String getKoodiNimi(String hakukohdeUri) {
    	String nimi = _tarjontaUIHelper.getKoodiNimi(hakukohdeUri, I18N.getLocale());
    	if ("".equals(nimi)) {
    		nimi = hakukohdeUri;
    	}
    	return nimi; 
    }
    

}
