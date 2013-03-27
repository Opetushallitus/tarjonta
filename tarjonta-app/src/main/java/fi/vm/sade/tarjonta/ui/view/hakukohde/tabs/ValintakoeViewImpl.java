package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;/*
 *
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


import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PisterajaTable.PisterajaEvent;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Tuomas Katva Date: 23.1.2013
 */
public class ValintakoeViewImpl extends VerticalLayout {

    private TarjontaPresenter presenter;
    private transient UiBuilder uiBuilder;
    private Table valintakoeTable;
    private Button uusiValintakoeBtn;
    private HakukohdeValintakoeDialog dialog;
    private KoulutusasteTyyppi koulutustyyppi = KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS;


    private PisterajaTable pisterajaTable;


    private VerticalLayout paasykoeLayout;
    private VerticalLayout lisapisteetLayout;
    
    LisanaytotTabSheet lisanaytotKuvaus;
    
    private HakukohdeValintakoeViewImpl valintakoeComponent;
    
    private VerticalLayout layout;
    
    private transient I18NHelper _i18n;

    public ValintakoeViewImpl(TarjontaPresenter presenter, UiBuilder uiBuilder) {
        super();
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        buildLayout();
    }
    

    
    protected void buildLayout() {
        layout = UiUtil.verticalLayout();
        if (!presenter.getModel().getHakukohde().getKoulukses().isEmpty()) {
            koulutustyyppi = presenter.getModel().getHakukohde().getKoulukses().get(0).getKoulutustyyppi();
        }
        
        if (koulutustyyppi.equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
            buildLukioLayout(layout);
        } else if (koulutustyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            buildToinenAsteLayout(layout);
        }
        addComponent(layout);
    }

    /*
    @Override
    protected void buildLayout(VerticalLayout layout) {
       
    }*/
    
    private void buildLukioLayout(VerticalLayout layout) {
       layout.setSpacing(true);
       List<ValintakoeViewModel> valintakokees = presenter.loadHakukohdeValintaKokees();
       
       if (!valintakokees.isEmpty()) {
           presenter.getModel().setSelectedValintaKoe(valintakokees.get(0));
       } else {
           presenter.getModel().setSelectedValintaKoe(new ValintakoeViewModel());
       }
       buildPisterajaLayout(layout);
       buildPaasykoeLayout(layout);
       buildLisanaytotlayout(layout);
    }
    
    
    private void buildPisterajaLayout(VerticalLayout layout) {
        VerticalLayout prL = UiUtil.verticalLayout();
        HorizontalLayout infoLayout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT);
        infoLayout.setWidth(UiConstant.PCT100);
        Label titleLabel = UiUtil.label(infoLayout, T("valintaTiedot"));
        titleLabel.setStyleName(Oph.LABEL_H2); 
        buildInfoButtonLayout(infoLayout);
        prL.addComponent(infoLayout);
        Label ohje = UiUtil.label(prL, T("valintaTiedotOhje"));
        ohje.addStyleName(Oph.LABEL_SMALL);
        buildPisterajaTable(prL);
        layout.addComponent(prL);
        layout.addComponent(buildSplitPanel());
    }
    
    private VerticalSplitPanel buildSplitPanel() {
        VerticalSplitPanel splitPanel = new VerticalSplitPanel();
        splitPanel.setWidth("100%");
        splitPanel.setHeight("2px");
        splitPanel.setLocked(true);
        return splitPanel;
    }
    
    private void buildPisterajaTable(VerticalLayout layout) {
        VerticalLayout lvl = UiUtil.verticalLayout();
        lvl.setMargin(true, false, false, false);
        Label pisterajatLabel = UiUtil.label(lvl, T("pisterajat"));
        pisterajatLabel.setStyleName(Oph.LABEL_H2);
        layout.addComponent(lvl);
        pisterajaTable = new PisterajaTable(presenter.getModel().getSelectedValintaKoe()); 
        pisterajaTable.addListener(new Listener() {

            private static final long serialVersionUID = 5409894266822689283L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof PisterajaEvent) {
                    applyVisiblilities((PisterajaEvent)event);
                }
            }
            
        });
        
        layout.addComponent(pisterajaTable);
    }
    
    protected void applyVisiblilities(PisterajaEvent event) {
        if (event.getType().equals(PisterajaEvent.PAASYKOE)) {
            paasykoeLayout.setVisible(event.isSelected());
        } else if (event.getType().equals(PisterajaEvent.LISAPISTEET)) {
            lisapisteetLayout.setVisible(event.isSelected());
        }
        
    }

    private void buildLisanaytotlayout(VerticalLayout layout) {
        lisapisteetLayout = UiUtil.verticalLayout();
        lisapisteetLayout.setImmediate(true);
        HorizontalLayout infoLayout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT);
        infoLayout.setWidth(UiConstant.PCT100);
        Label lisanaytotLabel = UiUtil.label(infoLayout, T("lisanaytot"));
        lisanaytotLabel.setStyleName(Oph.LABEL_H2); 
        buildInfoButtonLayout(infoLayout);
        lisapisteetLayout.addComponent(infoLayout);
        Label ohje = UiUtil.label(lisapisteetLayout, T("lisanaytotOhje"));
        ohje.addStyleName(Oph.LABEL_SMALL);
        
        GridLayout lisanaytotL = new GridLayout(2,1);
        lisanaytotL.setSpacing(true);
        lisanaytotL.addComponent(UiUtil.label(null, T("lisanayttojenKuvaus")), 0, 0);
        
        lisanaytotKuvaus = new LisanaytotTabSheet(true, "650px", "250px");
        lisanaytotKuvaus.setSizeUndefined();
        lisanaytotL.addComponent(lisanaytotKuvaus, 1, 0);
        
        lisapisteetLayout.addComponent(lisanaytotL);
        layout.addComponent(lisapisteetLayout);
        lisapisteetLayout.setVisible(pisterajaTable.getLpCb().booleanValue());
        layout.addComponent(buildSplitPanel());
    }

    private void buildPaasykoeLayout(VerticalLayout layout) {
        paasykoeLayout = UiUtil.verticalLayout();
        paasykoeLayout.setImmediate(true);
        HorizontalLayout infoLayout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT);
        infoLayout.setWidth(UiConstant.PCT100);
        Label pisterajatLabel = UiUtil.label(infoLayout, T("paasykoe"));
        pisterajatLabel.setStyleName(Oph.LABEL_H2); 
        buildInfoButtonLayout(infoLayout);
        paasykoeLayout.addComponent(infoLayout);
        Label ohje = UiUtil.label(paasykoeLayout, T("paasykoeOhje"));
        ohje.addStyleName(Oph.LABEL_SMALL);   
        //presenter.getSelected
        valintakoeComponent = new HakukohdeValintakoeViewImpl(new ErrorMessage(), presenter, uiBuilder, KoulutusasteTyyppi.LUKIOKOULUTUS);
        paasykoeLayout.addComponent(valintakoeComponent);
        layout.addComponent(paasykoeLayout);
        paasykoeLayout.setVisible(pisterajaTable.getPkCb().booleanValue());
        layout.addComponent(buildSplitPanel());
    }
    
    private HorizontalLayout buildInfoButtonLayout(HorizontalLayout layout) {
        Button upRightInfoButton = UiUtil.buttonSmallInfo(layout);
        layout.setComponentAlignment(upRightInfoButton, Alignment.TOP_RIGHT);
        return layout;
    }

    private void buildToinenAsteLayout(VerticalLayout layout) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        uusiValintakoeBtn = UiBuilder.button(null, T("uusiBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showValintakoeEditWithId(null);
            }
        });

        horizontalLayout.addComponent(uusiValintakoeBtn);
        horizontalLayout.setMargin(false, false, true, false);
        layout.addComponent(horizontalLayout);

        loadTableData();
    }


    public void closeValintakoeEditWindow() {
        if (dialog != null) {
            dialog.windowClose();
        }
    }

    public void showValintakoeEditWithId(String id) {
        if (id == null) {
            presenter.getModel().setSelectedValintaKoe(new ValintakoeViewModel());
        }
        dialog = new HakukohdeValintakoeDialog(presenter, uiBuilder);
        dialog.windowOpen();
    }

    public void loadTableData() {
        if (koulutustyyppi.equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
            return;
        }

        if (valintakoeTable != null) {
            valintakoeTable.removeAllItems();

        } else {
            valintakoeTable = new Table();
            valintakoeTable.setWidth(100, UNITS_PERCENTAGE);
            /*getLayout().setMargin(true);
            getLayout().addComponent(valintakoeTable);*/
            layout.setMargin(true);
            layout.addComponent(valintakoeTable);

            valintakoeTable.addGeneratedColumn("sanallinenKuvaus", new Table.ColumnGenerator() {
                @Override
                public Object generateCell(Table table, Object o, Object o2) {
                    if (table != null) {
                        Item item = table.getItem(o);
                        Label label = new Label(item.getItemProperty("sanallinenKuvaus"));
                        label.setContentMode(Label.CONTENT_XHTML);
                        return label;
                    } else {
                        return null;
                    }
                }
            });
        }
        if (valintakoeTable != null) {
            final List<ValintakoeViewModel> koees = presenter.loadHakukohdeValintaKokees();

            valintakoeTable.setContainerDataSource(createBeanContainer(koees));
            valintakoeTable.setWidth(100, UNITS_PERCENTAGE);
            valintakoeTable.setVisibleColumns(new String[]{"valintakokeenTyyppi", "sanallinenKuvaus", "muokkaaBtn", "poistaBtn"});
            valintakoeTable.setColumnHeader("valintakokeenTyyppi", T("valinkoeTyyppiHdr"));
            valintakoeTable.setColumnHeader("sanallinenKuvaus", T("sanallinenKuvaus"));
            valintakoeTable.setColumnHeader("muokkaaBtn", "");
            valintakoeTable.setColumnHeader("poistaBtn", "");
            valintakoeTable.setImmediate(true);
            valintakoeTable.requestRepaint();
            valintakoeTable.setPageLength(koees.size());

            valintakoeTable.setColumnExpandRatio("valintakokeenTyyppi", 30);
            valintakoeTable.setColumnExpandRatio("sanallinenKuvaus", 50);
            valintakoeTable.setColumnExpandRatio("muokkaaBtn", 10);
            valintakoeTable.setColumnExpandRatio("poistaBtn", 10);
        }
    }
    
    public List<KielikaannosViewModel> getLisanayttoKuvaukset() {
        if (lisanaytotKuvaus != null) {
            return lisanaytotKuvaus.getKieliKaannokset();
        } else {
            return new ArrayList<KielikaannosViewModel>();
        }
    }

    private BeanContainer<String, HakukohdeValintakoeRow> createBeanContainer(List<ValintakoeViewModel> valintaKokees) {
        BeanContainer<String, HakukohdeValintakoeRow> container = new BeanContainer<String, HakukohdeValintakoeRow>(HakukohdeValintakoeRow.class);

        for (ValintakoeViewModel valintakoe : valintaKokees) {
            HakukohdeValintakoeRow row = new HakukohdeValintakoeRow(valintakoe);
            container.addItem(valintakoe.getValintakoeTunniste(), row);
        }

        return container;
    }
    
    public HakukohdeValintakoeViewImpl getValintakoeComponent() {
        return valintakoeComponent;
    }
    
    
    public PisterajaTable getPisterajaTable() {
        return pisterajaTable;
    }

    public KoulutusasteTyyppi getKoulutustyyppi() {
        return koulutustyyppi;
    }
    
    protected String T(String key) {
        return getI18n().getMessage(key);
    }

    protected String T(String key, Object... args) {
        return getI18n().getMessage(key, args);
    }

    protected I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }

}
