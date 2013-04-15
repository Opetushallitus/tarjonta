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
import com.vaadin.ui.*;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * Created by: Tuomas Katva Date: 15.1.2013
 */
@Configurable(preConstruction = true)
public class HakukohteenLiitteetTabImpl extends AbstractVerticalNavigationLayout {

    @Autowired
    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    private Table hakukohteenLiitteetTable = null;
    private Button uusiLiiteBtn;
    private HakukohdeLiiteetDialog liitteetDialog = null;

    public HakukohteenLiitteetTabImpl() {
        super();
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        uusiLiiteBtn = UiBuilder.button(null, T("uusiLiiteBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showHakukohdeEditWindow(null);
            }
        });


        horizontalLayout.addComponent(uusiLiiteBtn);
        horizontalLayout.setMargin(false, false, true, false);
        layout.addComponent(horizontalLayout);

        loadTableWithData();
    }

    public void loadTableWithData() {

        if (hakukohteenLiitteetTable != null) {
            hakukohteenLiitteetTable.removeAllItems();
        } else {
            hakukohteenLiitteetTable = new Table();
            hakukohteenLiitteetTable.setWidth(100, UNITS_PERCENTAGE);
            getLayout().setMargin(true);
            getLayout().addComponent(hakukohteenLiitteetTable);
            hakukohteenLiitteetTable.addGeneratedColumn("liitteenSanallinenKuvaus", new Table.ColumnGenerator() {
                @Override
                public Object generateCell(Table table, Object o, Object o2) {
                    if (table != null) {
                        Item item = table.getItem(o);
                        Label label = new Label(item.getItemProperty("liitteenSanallinenKuvaus"));
                        label.setContentMode(Label.CONTENT_XHTML);
                        return label;
                    } else {
                        return null;
                    }
                }
            });
        }

        if (hakukohteenLiitteetTable != null) {
            List<HakukohdeLiiteViewModel> loadHakukohdeLiitteet = presenter.loadHakukohdeLiitteet();            
            hakukohteenLiitteetTable.setContainerDataSource(createTableContainer(loadHakukohdeLiitteet));
            hakukohteenLiitteetTable.setVisibleColumns(new String[]{"liitteenTyyppi", "liitteenSanallinenKuvaus", "toimitettavaMennessa",
                "toimitusOsoite", "muokkaaBtn", "poistaBtn"});
            hakukohteenLiitteetTable.setColumnHeader("liitteenTyyppi", T("tableLiitteenTyyppi"));
            hakukohteenLiitteetTable.setColumnHeader("liitteenSanallinenKuvaus", T("tableKuvaus"));

            hakukohteenLiitteetTable.setColumnHeader("toimitettavaMennessa", T("tableToimMennessa"));
            hakukohteenLiitteetTable.setColumnHeader("toimitusOsoite", T("tableToimitusOsoite"));
            hakukohteenLiitteetTable.setColumnHeader("muokkaaBtn", "");
            hakukohteenLiitteetTable.setColumnHeader("poistaBtn", "");
            hakukohteenLiitteetTable.setImmediate(true);
            hakukohteenLiitteetTable.setSizeFull();
            hakukohteenLiitteetTable.requestRepaint();
            hakukohteenLiitteetTable.setPageLength(loadHakukohdeLiitteet.size());
        }
    }

    private BeanContainer<String, HakukohdeLiiteRow> createTableContainer(List<HakukohdeLiiteViewModel> liites) {
        BeanContainer<String, HakukohdeLiiteRow> liiteContainer = new BeanContainer<String, HakukohdeLiiteRow>(HakukohdeLiiteRow.class);
        for (HakukohdeLiiteViewModel liite : liites) {
            HakukohdeLiiteRow liiteRow = new HakukohdeLiiteRow(liite);
            liiteContainer.addItem(liiteRow.getLiiteId(), liiteRow);
        }
        return liiteContainer;
    }

    public void showHakukohdeEditWindow(final String id) {
        if (id == null) {
            presenter.getModel().setSelectedLiite(new HakukohdeLiiteViewModel());
        } else {
            presenter.loadHakukohdeLiiteWithId(id);
        }

        liitteetDialog = new HakukohdeLiiteetDialog(presenter, uiBuilder);
        liitteetDialog.windowOpen();

    }

    public void closeEditWindow() {
        if (liitteetDialog != null) {
            liitteetDialog.windowClose();
        }
    }
    
     public void showWindow() {
        
      
    }
}
