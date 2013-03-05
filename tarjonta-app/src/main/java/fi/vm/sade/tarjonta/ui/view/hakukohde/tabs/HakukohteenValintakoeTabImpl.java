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
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * Created by: Tuomas Katva Date: 23.1.2013
 */
@Configurable(preConstruction = true)
public class HakukohteenValintakoeTabImpl extends AbstractVerticalNavigationLayout {

    @Autowired
    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    private Table valintakoeTable;
    private Button uusiValintakoeBtn;
    private HakukohdeValintakoeDialog dialog;

    public HakukohteenValintakoeTabImpl() {
        super();
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
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

        if (valintakoeTable != null) {
            valintakoeTable.removeAllItems();

        } else {
            valintakoeTable = new Table();
            valintakoeTable.setWidth(100, UNITS_PERCENTAGE);
            getLayout().setMargin(true);
            getLayout().addComponent(valintakoeTable);

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

    private BeanContainer<String, HakukohdeValintakoeRow> createBeanContainer(List<ValintakoeViewModel> valintaKokees) {
        BeanContainer<String, HakukohdeValintakoeRow> container = new BeanContainer<String, HakukohdeValintakoeRow>(HakukohdeValintakoeRow.class);

        for (ValintakoeViewModel valintakoe : valintaKokees) {
            HakukohdeValintakoeRow row = new HakukohdeValintakoeRow(valintakoe);
            container.addItem(valintakoe.getValintakoeTunniste(), row);
        }

        return container;
    }
}
