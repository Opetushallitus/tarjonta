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

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
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

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus.KoulutusTulos;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.presenter.HakuPresenter;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;


/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = false)
public class ListHakuView extends VerticalLayout {

    private static final long serialVersionUID = 6264485392051745482L;

    public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListaHakuViewImpl.jarjestys.Hakutapa")};
    
    private static final String COLUMN_A = "Kategoria";
    private static final String COLUMN_PVM = "Ajankohta";
    private static final String COLUMN_HAKUTAPA = "Hakutapa";
    private static final String COLUMN_HAKUTYYPPI = "Hakutyyppi";
    private static final String COLUMN_TILA = "Tila";
    
    

    private static final Logger LOG = LoggerFactory.getLogger(ListHakuView.class);
    private Button btnLuoUusiHaku;
    private Button btnPoista;
    private ComboBox cbJarjestys;
    private CategoryTreeView categoryTree;
    private CheckBox valKaikki;
    private Window removeDialogWindow;

    private transient I18NHelper i18n = new I18NHelper(this);

    @Autowired(required = true)
    private HakuPresenter presenter;
    
    @Autowired(required = true)
    private TarjontaUIHelper _tarjontaUIHelper;

    public ListHakuView() {
        setWidth(UiConstant.PCT100);
        HorizontalLayout buildMiddleResultLayout = buildMiddleResultLayout();
        addComponent(buildMiddleResultLayout);

        CssLayout wrapper = UiUtil.cssLayout(UiMarginEnum.BOTTOM);
        valKaikki = new CheckBox(i18n.getMessage("ValitseKaikki"));
        valKaikki.setImmediate(true);
        valKaikki.addListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = 4379174913230877685L;

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
        
        categoryTree.addContainerProperty(COLUMN_A, HakuResultRow.class, new HakuResultRow());
        categoryTree.addContainerProperty(COLUMN_PVM, String.class, "");
        categoryTree.addContainerProperty(COLUMN_HAKUTAPA, String.class, "");
        categoryTree.addContainerProperty(COLUMN_HAKUTYYPPI, String.class,"");
        categoryTree.addContainerProperty(COLUMN_TILA, String.class, "");
        
        categoryTree.setColumnExpandRatio(COLUMN_A, 2.0f);
        categoryTree.setColumnExpandRatio(COLUMN_PVM, 0.5f);
        categoryTree.setColumnExpandRatio(COLUMN_HAKUTAPA, 0.5f);
        categoryTree.setColumnExpandRatio(COLUMN_HAKUTYYPPI, 0.5f);
        categoryTree.setColumnExpandRatio(COLUMN_TILA, 0.5f);
        

        setExpandRatio(wrapper, 0.07f);
        setExpandRatio(categoryTree, 0.93f);
        setMargin(true);

    }

    @PostConstruct
    public void setDataSource() {
        //permissions
        btnLuoUusiHaku.setVisible(presenter.getPermission().userCanCreateHaku());

        
        presenter.setHakuList(this);
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(presenter.getTreeDataSource()));
    }
    
    public void setDataSource(Map<String, List<HakuViewModel>> haut) {
        categoryTree.removeAllItems();
        categoryTree.setContainerDataSource(createDataSource(haut));
        requestRepaint();
    }
   
    private Container createDataSource(Map<String, List<HakuViewModel>> map) {
        Set<Map.Entry<String, List<HakuViewModel>>> set = map.entrySet();

        HierarchicalContainer hc = new HierarchicalContainer();
        hc.addContainerProperty(COLUMN_A, HakuResultRow.class, new HakuResultRow());
        hc.addContainerProperty(COLUMN_PVM, String.class, "");
        hc.addContainerProperty(COLUMN_HAKUTAPA, String.class, "");
        hc.addContainerProperty(COLUMN_HAKUTYYPPI, String.class,"");
        hc.addContainerProperty(COLUMN_TILA, String.class, "");

        for (Map.Entry<String, List<HakuViewModel>> e : set) {
            LOG.info("getTreeDataSource()" + e.getKey());
            HakuResultRow rowStyle = new HakuResultRow();

            Object rootItem = hc.addItem();

            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(getKoodiNimi(e.getKey()) + " (" + e.getValue().size() + ")", false));

            for (HakuViewModel curHaku : e.getValue()) {
                HakuResultRow rowStyleInner = new HakuResultRow(curHaku, getListHakuName(curHaku));
                hc.addItem(curHaku);
                hc.setParent(curHaku, rootItem);
                hc.getContainerProperty(curHaku, COLUMN_A).setValue(rowStyleInner.format(getListHakuName(curHaku), true));
                setHakuProperties(hc, curHaku);
                
                
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

    private void setHakuProperties(HierarchicalContainer hc, HakuViewModel curHaku) {
        Preconditions.checkNotNull("haku cannot be null", curHaku);
        hc.getContainerProperty(curHaku, COLUMN_PVM).setValue(getAjankohtaStr(curHaku));
        hc.getContainerProperty(curHaku, COLUMN_HAKUTAPA).setValue(getHakutapaStr(curHaku));
        hc.getContainerProperty(curHaku, COLUMN_HAKUTYYPPI).setValue(getHakuTyyppiStr(curHaku));
        hc.getContainerProperty(curHaku, COLUMN_TILA).setValue(this.getTilaStr(curHaku));
    }
    
    private String getListHakuName(HakuViewModel curHaku) {
        return TarjontaUIHelper.getClosestHakuName(I18N.getLocale(), curHaku);// + ", " + T(curHaku.getHaunTila());
    }
    
    private String getTilaStr(HakuViewModel curHaku) {
        return T(curHaku.getHaunTila());
    }

    private String getHakuTyyppiStr(HakuViewModel curHaku) {
        return getKoodiNimi(curHaku.getHakutyyppi());
    }
    
    private String getAjankohtaStr(HakuViewModel curHaku) {
        return  getKoodiNimi(curHaku.getKoulutuksenAlkamisKausi()) + " " + curHaku.getKoulutuksenAlkamisvuosi();
    }
    
    private String getHakutapaStr(HakuViewModel curHaku) {
        return  getKoodiNimi(curHaku.getHakutapa());
    }

    private void changeHakuSelections(boolean selected) {
        presenter.getSelectedhaut().clear();
        HierarchicalContainer hc = (HierarchicalContainer)(this.categoryTree.getContainerDataSource());
        for (Object item : hc.getItemIds()) {
            HakuResultRow curRow = (HakuResultRow)(categoryTree.getContainerProperty(item, COLUMN_A).getValue());
            curRow.getIsSelected().setValue(selected);
        }
    }

    private HorizontalLayout buildMiddleResultLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);



        btnLuoUusiHaku = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("LuoUusiHaku"));
        btnLuoUusiHaku.addStyleName(Oph.BUTTON_SMALL);

        btnLuoUusiHaku.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1564545922923588423L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigateToHakuEditForm();

            }
        });

        btnPoista = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("Poista"));
        btnPoista.addStyleName(Oph.BUTTON_SMALL);
        //btnPoista.setEnabled(false);
        btnPoista.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = 4122064768579621095L;

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
        return layout;
    }

    public void showErrorMessage(String msg) {
        if (getWindow() != null) {
        getWindow().showNotification(msg, Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }
    
    /**
     * @param btnListenerMuokkaa( the btnLuoUusiKoulutus to set
     */
    public void setBtnListenerMuokkaa(Button.ClickListener btnKopioiUudelleKaudelle) {
        this.btnLuoUusiHaku.addListener(btnKopioiUudelleKaudelle);
    }

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
    


    public void showNotification(String title, String content, int type) {
        getWindow().showNotification(title, content, type, true);   
    }

    public void toggleRemoveButton(boolean enable) {
        this.btnPoista.setEnabled(enable);
    }
    
    private String getKoodiNimi(String koodiUri) {
        String nimi = _tarjontaUIHelper.getKoodiNimi(koodiUri, I18N.getLocale());
        if ("".equals(nimi)) {
            nimi = koodiUri;
        }
        return nimi;
    }
    
    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }
    
    

    /**
     * Event to signal that the user wants to create a new Haku.
    */
    public class NewHakuEvent extends Component.Event {

        private static final long serialVersionUID = 7115444912604937940L;

        public NewHakuEvent(Component source) {
            super(source);

        }

    }

    /**
     * Event listeneri joka saa viestejä koulutustulospuun muutostarpeista, katso
     * {@link TarjontaPresenter#sendEvent(Object)}
     */
    @Subscribe 
    public void receiveHakuContainerEvent(HakuContainerEvent e) {

        final String eventHakuOid = e.oid;
    
        switch (e.type) {
        case DELETE:
            
            
            for(Object itemid: Iterables.filter(categoryTree.getItemIds(), filter(HakuViewModel.class))){
                    final HakuViewModel currentHaku = (HakuViewModel)itemid;
                    if(currentHaku.getHakuOid().equals(eventHakuOid)) {
                        categoryTree.removeItem(currentHaku);
                    }
            }
            break;

        case UPDATE:
            
            for(Object itemid: Iterables.filter(categoryTree.getItemIds(), filter(HakuViewModel.class))){
                    HakuViewModel currentHaku = (HakuViewModel)itemid;
                    if(currentHaku.getHakuOid().equals(eventHakuOid)) {
                        //hae tuore haku
                        final HakuViewModel freshHaku = presenter.findHakuByOid(eventHakuOid);
                        copyData(currentHaku, freshHaku);
                        final HakuResultRow curRow = (HakuResultRow) (categoryTree.getContainerProperty(itemid, COLUMN_A).getValue());
                        setHakuProperties((HierarchicalContainer)categoryTree.getContainerDataSource(), currentHaku);
                        curRow.reinitMenubar();
                    }
            }
            break;

        case CREATE:

//            final HakuViewModel freshHaku = presenter.findHakuByOid(eventHakuOid);
//            
//            Object parent = categoryTree.getItem(freshHaku.getfindParent(freshHaku.getHakutyyppi());
//            if(parent==null) {
//                //need to add new org to tree, falling back to reload for now!
                reload();
//            } else {
//                addHakuRow(parent, freshHaku);
//                //TODO increase counter?
//            }
            
            
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

    private final Predicate<Object> filter(final Class<?> type) {
        return new Predicate<Object>() {
            public boolean apply(Object o) {
                return o.getClass() == type;
            }
        };
    }
    
    
    private boolean attached = false;
    
    @Override
    public void attach() {
        super.attach();
        if (!attached) {
            attached = true;
            presenter.registerEventListener(this);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        if(presenter!=null) {
            presenter.unregisterEventListener(this);
        }
        super.finalize();
    }
    
}
