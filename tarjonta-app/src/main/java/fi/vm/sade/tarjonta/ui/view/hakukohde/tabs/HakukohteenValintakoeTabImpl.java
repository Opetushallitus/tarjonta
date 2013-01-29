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
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohdeValintakoeRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * Created by: Tuomas Katva
 * Date: 23.1.2013
 */

@Configurable(preConstruction = true)
public class HakukohteenValintakoeTabImpl extends AbstractVerticalNavigationLayout {

    @Autowired
    private TarjontaPresenter presenter;

    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    private Table valintakoeTable;

    private BeanItem<HakukohdeValintakoeRow> valintakoeRowBean;

    private Button uusiValintakoeBtn;

    private Window valintakoeEditWindow;

    private HakukohdeValintakoeViewImpl valintakoeView;

    public HakukohteenValintakoeTabImpl() {
        super();
        setHeight(-1, UNITS_PIXELS);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        uusiValintakoeBtn = UiBuilder.button(null,T("uusiBtn"),new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                  showValintakoeEditWithId(null);
            }
        });

        horizontalLayout.addComponent(uusiValintakoeBtn);
        layout.addComponent(horizontalLayout);

        loadTableData();
    }

    public void closeValintakoeEditWindow() {
       if (valintakoeEditWindow != null) {
           getWindow().removeWindow(valintakoeEditWindow);
           valintakoeEditWindow = null;
       }
    }

    public void showValintakoeEditWithId(String id) {

        if (id == null) {
            presenter.getModel().setSelectedValintaKoe(new ValintakoeViewModel());

        } else {
            presenter.loadValintakoeWithId(id);
        }

        valintakoeView = new HakukohdeValintakoeViewImpl();
        VerticalLayout mainWindowLayout = new VerticalLayout();
        mainWindowLayout.addComponent(valintakoeView);
        valintakoeEditWindow = new Window();
        valintakoeEditWindow.setContent(mainWindowLayout);
        getWindow().addWindow(valintakoeEditWindow);

        mainWindowLayout.setSizeUndefined();
        valintakoeView.setImmediate(true);
        valintakoeView.setWidth("1000px");

        valintakoeEditWindow.setModal(true);
        valintakoeEditWindow.center();

    }

    public void loadTableData() {

        if (valintakoeTable != null) {
              valintakoeTable.removeAllItems();

        }
        else {
            valintakoeTable = new Table();
            valintakoeTable.setWidth(100, UNITS_PERCENTAGE);
            getLayout().addComponent(valintakoeTable);

            valintakoeTable.addGeneratedColumn("sanallinenKuvaus",new Table.ColumnGenerator() {
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
            valintakoeTable.setContainerDataSource(createBeanContainer(presenter.loadHakukohdeValintaKokees()));


            valintakoeTable.setWidth(100, UNITS_PERCENTAGE);
            valintakoeTable.setVisibleColumns(new String[]{"valintakokeenTyyppi", "sanallinenKuvaus", "muokkaaBtn"});
            valintakoeTable.setColumnHeader("valintakokeenTyyppi", T("valinkoeTyyppiHdr"));
            valintakoeTable.setColumnHeader("sanallinenKuvaus",T("sanallinenKuvaus"));

            valintakoeTable.setColumnHeader("muokkaaBtn","");
            valintakoeTable.setImmediate(true);
            valintakoeTable.setSizeFull();
            valintakoeTable.requestRepaint();

        }

    }

    private BeanContainer<String,HakukohdeValintakoeRow> createBeanContainer(List<ValintakoeViewModel> valintaKokees) {

        BeanContainer<String,HakukohdeValintakoeRow> container = new BeanContainer<String, HakukohdeValintakoeRow>(HakukohdeValintakoeRow.class);

        for (ValintakoeViewModel valintakoe:valintaKokees) {
            HakukohdeValintakoeRow row = new HakukohdeValintakoeRow(valintakoe);
            container.addItem(valintakoe.getValintakoeTunniste(),row);
        }

        return container;
    }

}
