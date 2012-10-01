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
package fi.vm.sade.tarjonta.ui.view.haku;

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

import fi.vm.sade.tarjonta.ui.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.view.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;


/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = false)
public class ListHakuViewImpl extends VerticalLayout implements ListHakuView {
    
    public static final String[] ORDER_BY = new String[]{"Organisaatiorakenteen mukainen järjestys", "Koulutuksen tilan mukainen järjestys", "Aakkosjärjestys", "Koulutuslajin mukaan"};
    
    private static final Logger LOG = LoggerFactory.getLogger(ListHakuViewImpl.class);
    private Button btnLuoUusiHaku;
    private Button btnPoista;
    private ComboBox cbJarjestys;
    private CategoryTreeView categoryTree;
    private CheckBox valKaikki;
    
    private I18NHelper i18n = new I18NHelper(this);
    
    @Autowired(required = true)
    private HakuPresenter presenter;

    public ListHakuViewImpl() {
        setWidth(UiConstant.PCT100);
        HorizontalLayout buildMiddleResultLayout = buildMiddleResultLayout();
        addComponent(buildMiddleResultLayout);

        CssLayout wrapper = UiUtil.cssLayout(UiMarginEnum.BOTTOM);
        valKaikki = new CheckBox(i18n.getMessage("ValitseKaikki"));
        valKaikki.setImmediate(true);
        valKaikki.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (valKaikki.booleanValue()) {
                    changeHakuSelections();
                }
            }
        });
        wrapper.addComponent(valKaikki);
        
        addComponent(wrapper);

        categoryTree = new CategoryTreeView();
        addComponent(categoryTree);
        setHeight(Sizeable.SIZE_UNDEFINED, 0);

        setExpandRatio(wrapper, 0.07f);
        setExpandRatio(categoryTree, 0.93f);
        setMargin(true);
        
    }
    
    @PostConstruct
    public void setDataSource() {
        presenter.setHakuList(this);
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getTreeDataSource()));
    }
    
    private Container createDataSource(Map<String, List<HakuViewModel>> map) {
        Set<Map.Entry<String, List<HakuViewModel>>> set = map.entrySet();

        HierarchicalContainer hc = new HierarchicalContainer();
        HakuResultRow rowStyleDef = new HakuResultRow();
        hc.addContainerProperty(presenter.COLUMN_A, HakuResultRow.class, rowStyleDef.format("", false));

        for (Map.Entry<String, List<HakuViewModel>> e : set) {
            LOG.info("getTreeDataSource()" + e.getKey());
            HakuResultRow rowStyle = new HakuResultRow();
           
            Object rootItem = hc.addItem();

            hc.getContainerProperty(rootItem, presenter.COLUMN_A).setValue(rowStyle.format(e.getKey(), false));

            for (HakuViewModel curHaku : e.getValue()) {
                HakuResultRow rowStyleInner = new HakuResultRow(curHaku);
                hc.addItem(curHaku);
                hc.setParent(curHaku, rootItem);
                hc.getContainerProperty(curHaku, presenter.COLUMN_A).setValue(rowStyleInner.format(curHaku.getNimiFi(), true));
                hc.setChildrenAllowed(curHaku, false);
                
                rowStyleInner.addListener(new Listener() {

                    @Override
                    public void componentEvent(Event event) {
                        if (event instanceof HakuResultRow.HakuRowMenuEvent) {
                            fireEvent(event);    
                        }
                    }
                });
            }
        }
        return hc;
    }
    
    private void changeHakuSelections() {  
        presenter.getSelectedhaut().clear();
        HierarchicalContainer hc = (HierarchicalContainer)(this.categoryTree.getContainerDataSource());
        for (Object item : hc.getItemIds()) {
            HakuResultRow curRow = (HakuResultRow)(categoryTree.getContainerProperty(item, presenter.COLUMN_A).getValue());
            curRow.getIsSelected().setValue(true);
        }
    }

    private HorizontalLayout buildMiddleResultLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);



        btnLuoUusiHaku = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("LuoUusiHaku"));
        btnLuoUusiHaku.addStyleName(Oph.BUTTON_SMALL);

        btnLuoUusiHaku.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigateToHakuEditForm();
                
            }
        });

        btnPoista = UiUtil.button(layout, i18n.getMessage("Poista"));
        btnPoista.addStyleName(Oph.BUTTON_SMALL);
        //btnPoista.setEnabled(false);
        btnPoista.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.removeSelectedHaut();
                
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
        this.btnLuoUusiHaku.addListener(btnKopioiUudelleKaudelle);
    }

    @Override
    public void reload() {
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getTreeDataSource()));
        if (this.valKaikki.booleanValue()) {
            changeHakuSelections();
        }
    }
    
    private void navigateToHakuEditForm() {
        fireEvent(new NewHakuEvent(this));
    }
    
    /**
     * Event to signal that the user wants to create a new Haku.
    */
    public class NewHakuEvent extends Component.Event {

        public NewHakuEvent(Component source) {
            super(source);
            
        }
        
    }

}
