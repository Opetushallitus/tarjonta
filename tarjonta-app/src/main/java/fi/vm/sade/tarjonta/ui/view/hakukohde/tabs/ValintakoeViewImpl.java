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


import com.google.common.base.Preconditions;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import com.vaadin.ui.*;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PisterajaTable.PisterajaEvent;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Tuomas Katva Date: 23.1.2013
 */
public class ValintakoeViewImpl extends VerticalLayout {

    private static final long serialVersionUID = -4186294139779319030L;
    private TarjontaPresenter presenter;
    private transient UiBuilder uiBuilder;
    private Table ammValintakoeTable;
    private Button uusiValintakoeBtn;
    private HakukohdeAmmatillinenValintakoeDialog dialog;
    private PisterajaTable pisterajaTable;
    private VerticalLayout paasykoeLayout;
    private VerticalLayout lisapisteetLayout;
    private LisanaytotTabSheet lisanaytotKuvaus;
    private HakukohdeValintakoeViewImpl lukioValintakoe;
    private transient I18NHelper _i18n;
    private KoulutusasteTyyppi koulutustyyppi;

    public ValintakoeViewImpl(TarjontaPresenter presenter, UiBuilder uiBuilder, KoulutusasteTyyppi koulutusasteTyyppi) {
        super();
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        this.koulutustyyppi = koulutusasteTyyppi;
        buildLayout();
    }

    protected void buildLayout() {
        Preconditions.checkNotNull(koulutustyyppi, "KoulutusasteTyyppi enum cannot be null.");
        reloadTableDataValintaKokees();
    }

    private void buildLukioLayout(VerticalLayout layout) {
        layout.setSpacing(true);
        buildLukioPisterajaLayout(layout);
        buildLukioPaasykoeLayout(layout);
        buildLukioLisanaytotlayout(layout);
    }

    private void buildLukioPisterajaLayout(VerticalLayout layout) {
        VerticalLayout prL = UiUtil.verticalLayout();
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
        lvl.setSpacing(true);
        lvl.setMargin(true, false, false, false);

        HorizontalLayout infoLayout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT);
        infoLayout.setWidth(UiConstant.PCT100);

        Label pisterajatLabel = UiUtil.label(infoLayout, T("pisterajat"));
        pisterajatLabel.setStyleName(Oph.LABEL_H2);
        lvl.addComponent(infoLayout);
        Label pisterajaOhje = UiUtil.label(lvl, T("pisterajaohje"));
        pisterajaOhje.setStyleName(Oph.LABEL_SMALL);
        layout.addComponent(lvl);
        pisterajaTable = new PisterajaTable(presenter.getModel().getSelectedValintaKoe());
        pisterajaTable.addListener(new Listener() {
            private static final long serialVersionUID = 5409894266822689283L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof PisterajaEvent) {
                    applyVisiblilities((PisterajaEvent) event);
                }
            }
        });

        layout.addComponent(pisterajaTable);
    }

    protected void applyVisiblilities(PisterajaEvent event) {
        if (event.getType().equals(PisterajaEvent.PAASYKOE)) {
            setPaasykoeVisiblities(event);
        } else if (event.getType().equals(PisterajaEvent.LISAPISTEET)) {
            setLisapisteVisiblities(event);
        }
    }

    private void setLisapisteVisiblities(PisterajaEvent event) {
        lisapisteetLayout.setVisible(event.isSelected());
        if (!event.isSelected()) {
            presenter.getModel().getSelectedValintaKoe().getLisanayttoKuvaukset().clear();
            lisanaytotKuvaus.resetTabSheets();
            lisanaytotKuvaus.initializeTabsheet();
        }

    }

    private void setPaasykoeVisiblities(PisterajaEvent event) {
        paasykoeLayout.setVisible(event.isSelected());
        if (!event.isSelected()) {
            presenter.getModel().getSelectedValintaKoe().getSanallisetKuvaukset().clear();
            presenter.getModel().getSelectedValintaKoe().getValintakoeAjat().clear();
            lukioValintakoe.clearValintakoeAikasTableData();
        }
    }

    private void buildLukioLisanaytotlayout(VerticalLayout layout) {
        HorizontalLayout infoLayout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT);
        infoLayout.setWidth(UiConstant.PCT100);
        Label lisanaytotLabel = UiUtil.label(infoLayout, T("lisanaytot"));
        lisanaytotLabel.setStyleName(Oph.LABEL_H2);
        layout.addComponent(infoLayout);
        lisapisteetLayout = UiUtil.verticalLayout();
        lisapisteetLayout.setImmediate(true);
        Label ohje = UiUtil.label(lisapisteetLayout, T("lisanaytotOhje"));
        ohje.addStyleName(Oph.LABEL_SMALL);

        GridLayout lisanaytotL = new GridLayout(2, 1);
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

    private void buildLukioPaasykoeLayout(VerticalLayout layout) {
        HorizontalLayout infoLayout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT);
        infoLayout.setWidth(UiConstant.PCT100);
        Label pisterajatLabel = UiUtil.label(infoLayout, T("paasykoe"));
        pisterajatLabel.setStyleName(Oph.LABEL_H2);
        layout.addComponent(infoLayout);
        paasykoeLayout = UiUtil.verticalLayout();
        paasykoeLayout.setImmediate(true);
        Label ohje = UiUtil.label(paasykoeLayout, T("paasykoeOhje"));
        ohje.addStyleName(Oph.LABEL_SMALL);
        //presenter.getSelected
        lukioValintakoe = new HakukohdeValintakoeViewImpl(new ErrorMessage(), presenter, uiBuilder, KoulutusasteTyyppi.LUKIOKOULUTUS);
        paasykoeLayout.addComponent(lukioValintakoe.getForm());
        layout.addComponent(paasykoeLayout);
        paasykoeLayout.setVisible(pisterajaTable.getPkCb().booleanValue());
        layout.addComponent(buildSplitPanel());
    }

    private void buildToinenAsteLayout(VerticalLayout layout) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        uusiValintakoeBtn = UiBuilder.button(null, T("uusiBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = -2568610745253769065L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showValintakoeEditWithId(null);
            }
        });

        horizontalLayout.addComponent(uusiValintakoeBtn);
        horizontalLayout.setMargin(false, false, true, false);
        layout.addComponent(horizontalLayout);

    }

    public void closeValintakoeEditWindow() {
        if (dialog != null) {
            dialog.windowClose();
        }
    }

    public void showValintakoeEditWithId(String id) {
        boolean isNew = true;
        if (id == null) {
            isNew = true;
            presenter.getModel().setSelectedValintaKoe(new ValintakoeViewModel());
        } else {
            isNew = false;
        }
        dialog = new HakukohdeAmmatillinenValintakoeDialog(presenter, uiBuilder,isNew);
        dialog.windowOpen();
    }

    /*
     * Initialize data to table ui component.
     * - lukio
     * - amm
     */
    public void reloadTableDataValintaKokees() {
        //load data to model
        presenter.loadHakukohdeValintaKokees();

        //set data to ui components
        final List<ValintakoeViewModel> valintaKokees = presenter.getModel().getHakukohde().getValintaKokees();

        switch (koulutustyyppi) {
            case AMMATILLINEN_PERUSKOULUTUS:
                if (ammValintakoeTable == null) {
                    //data table in a window dialog
                    buildToinenAsteLayout(this);
                    buildAmmatillinenValintakoeKoeTable(this);
                }
                reloadAmmatillinenValintakoeKoeTable(valintaKokees);
                break;
            case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS:
                if (ammValintakoeTable == null) {
                    //data table in a window dialog
                    buildToinenAsteLayout(this);
                    buildAmmatillinenValintakoeKoeTable(this);
                }
                reloadAmmatillinenValintakoeKoeTable(valintaKokees);
                break;
            case LUKIOKOULUTUS:
                if (!valintaKokees.isEmpty()) {
                    //only one exam, just take the first item
                    presenter.getModel().setSelectedValintaKoe(valintaKokees.get(0));
                } else {
                    presenter.getModel().setSelectedValintaKoe(new ValintakoeViewModel());
                }

                if (lukioValintakoe == null) {
                    buildLukioLayout(this);
                }

                lukioValintakoe.reloadValintakoeAikasTableData();
                break;
            default:
                throw new RuntimeException("Reload not implemented for koulutustyyppi " + koulutustyyppi);
        }
    }

    private void reloadAmmatillinenValintakoeKoeTable(final List<ValintakoeViewModel> loadHakukohdeValintaKokees) {
        ammValintakoeTable.removeAllItems();
        ammValintakoeTable.setContainerDataSource(createBeanContainer(loadHakukohdeValintaKokees));
        ammValintakoeTable.setVisibleColumns(new String[]{"valintakokeenTyyppi", "sanallinenKuvaus", "muokkaaBtn", "poistaBtn"});
        ammValintakoeTable.setPageLength(loadHakukohdeValintaKokees.size() > 0 ? loadHakukohdeValintaKokees.size() + 1 : 1);
    }

    private void buildAmmatillinenValintakoeKoeTable(VerticalLayout layout) {
        ammValintakoeTable = new Table();
        ammValintakoeTable.addGeneratedColumn("sanallinenKuvaus", new Table.ColumnGenerator() {
            private static final long serialVersionUID = 1414950227419848014L;

            @Override
            public Object generateCell(Table table, Object o, Object o2) {
                if (table != null) {
                    Item item = table.getItem(o);
                    Label label = new Label(cutString((String) item.getItemProperty("sanallinenKuvaus").getValue()));
                    label.setContentMode(Label.CONTENT_XHTML);
                    return label;
                } else {
                    return null;
                }
            }
        });
        final List<ValintakoeViewModel> loadHakukohdeValintaKokees = presenter.loadHakukohdeValintaKokees();

        ammValintakoeTable.setContainerDataSource(createBeanContainer(loadHakukohdeValintaKokees));
        ammValintakoeTable.setWidth(100, UNITS_PERCENTAGE);
        ammValintakoeTable.setImmediate(true);
        ammValintakoeTable.setColumnHeader("valintakokeenTyyppi", T("valinkoeTyyppiHdr"));
        ammValintakoeTable.setColumnHeader("sanallinenKuvaus", T("sanallinenKuvaus"));
        ammValintakoeTable.setColumnHeader("muokkaaBtn", "");
        ammValintakoeTable.setColumnHeader("poistaBtn", "");

        ammValintakoeTable.setColumnExpandRatio("valintakokeenTyyppi", 0.2f);
        ammValintakoeTable.setColumnExpandRatio("sanallinenKuvaus", 0.6f);
        ammValintakoeTable.setColumnExpandRatio("muokkaaBtn", 0.1f);
        ammValintakoeTable.setColumnExpandRatio("poistaBtn", 0.1f);

        layout.addComponent(ammValintakoeTable);
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

    private String cutString(String stringToCut) {
        if (stringToCut.length() > 90) {
            return stringToCut.substring(0, 87) + "...";
        } else {
            return stringToCut;
        }
    }

    public PisterajaTable getPisterajaTable() {
        return pisterajaTable;
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

    public HakukohdeValintakoeViewImpl getLukioValintakoeView() {
        return lukioValintakoe;
    }
}
