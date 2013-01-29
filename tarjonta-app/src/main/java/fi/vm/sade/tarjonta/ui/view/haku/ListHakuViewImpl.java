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

import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.view.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.tarjonta.ui.view.haku.HakuResultRow.HakuRowMenuEvent;
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

    public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListaHakuViewImpl.jarjestys.Hakutapa")};

    private static final Logger LOG = LoggerFactory.getLogger(ListHakuViewImpl.class);
    private Button btnLuoUusiHaku;
    private Button btnPoista;
    private ComboBox cbJarjestys;
    private CategoryTreeView categoryTree;
    private CheckBox valKaikki;
    private Window removeDialogWindow;

    private transient I18NHelper i18n = new I18NHelper(this);

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

                    changeHakuSelections(valKaikki.booleanValue());

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
    
    @Override
    public void setDataSource(Map<String, List<HakuViewModel>> haut) {
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(haut));
        requestRepaint();
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

            hc.getContainerProperty(rootItem, presenter.COLUMN_A).setValue(rowStyle.format(e.getKey() + " (" + e.getValue().size() + ")", false));

            for (HakuViewModel curHaku : e.getValue()) {
                HakuResultRow rowStyleInner = new HakuResultRow(curHaku, getListHakuName(curHaku));//TarjontaUIHelper.getClosestHakuName(I18N.getLocale(), curHaku));
                hc.addItem(curHaku);
                hc.setParent(curHaku, rootItem);
                hc.getContainerProperty(curHaku, presenter.COLUMN_A).setValue(rowStyleInner.format(getListHakuName(curHaku), true));
                hc.setChildrenAllowed(curHaku, false);

                rowStyleInner.addListener(new Listener() {

                    private static final long serialVersionUID = -3059346525675411902L;

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
    
    private String getListHakuName(HakuViewModel curHaku) {
        return TarjontaUIHelper.getClosestHakuName(I18N.getLocale(), curHaku) + ", " + T(curHaku.getHaunTila());
    }

    private void changeHakuSelections(boolean selected) {
        presenter.getSelectedhaut().clear();
        HierarchicalContainer hc = (HierarchicalContainer)(this.categoryTree.getContainerDataSource());
        for (Object item : hc.getItemIds()) {
            HakuResultRow curRow = (HakuResultRow)(categoryTree.getContainerProperty(item, presenter.COLUMN_A).getValue());
            curRow.getIsSelected().setValue(selected);
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

        btnPoista = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("Poista"));
        btnPoista.addStyleName(Oph.BUTTON_SMALL);
        //btnPoista.setEnabled(false);
        btnPoista.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                //presenter.removeSelectedHaut();
                removeSelectedHaut();

            }
        });
        btnPoista.setEnabled(false);


        cbJarjestys = UiUtil.comboBox(layout, null, ORDER_BY);
        cbJarjestys.setWidth("300px");
        layout.setExpandRatio(cbJarjestys, 1f);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);

        Button btnInfo = new Button();
        btnInfo.addStyleName(Oph.BUTTON_INFO);
        layout.addComponent(btnInfo);

        return layout;
    }

    @Override
    public void showErrorMessage(String msg) {
        getWindow().showNotification(msg, Window.Notification.TYPE_ERROR_MESSAGE);
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
        presenter.getSelectedhaut().clear();
        btnPoista.setEnabled(false);
    }

    private void navigateToHakuEditForm() {
        fireEvent(new NewHakuEvent(this));
    }
    
    private void removeSelectedHaut() {
        
        //for (HakuViewModel curHaku : presenter.getSelectedhaut()) {
        if (!presenter.getSelectedhaut().isEmpty()) {
            try {        
                //tarjontaAdminService.poistaHaku(curHaku.getHakuDto());
                showRemoveDialog(presenter.getSelectedhaut().get(0));
            } catch (Exception e) {
                showErrorMessage("ODOTTAMATON VIRHE TAPAHTUI : " + e.getMessage());
            }
        } else {
            reload();
        }
    }
    
    /**
     * Showing the confirmation dialog for removing multiple haku objects.
     * @param haku
     */
    private void showRemoveDialog(final HakuViewModel haku) {
        MultipleHakuRemovalDialog removeDialog = new  MultipleHakuRemovalDialog(T("removeQ"), presenter.getSelectedhaut(), T("removeYes"), T("removeNo"));
        removeDialogWindow = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(removeDialogWindow);
    }

    
    /**
     * Closing of the confirmation for removing multiple haku objects.
     */
    public void closeHakuRemovalDialog() {
        if (removeDialogWindow != null) {
            getWindow().removeWindow(removeDialogWindow);
        } else {
            showErrorMessage("removeDialogWindow was null");
        }
    }
    


    @Override
    public void showNotification(String title, String content, int type) {
        getWindow().showNotification(title, content, type, true);   
    }

    @Override
    public void toggleRemoveButton(boolean enable) {
        this.btnPoista.setEnabled(enable);
    }
    
    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
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
