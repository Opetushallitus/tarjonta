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

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * Created by: Tuomas Katva
 * Date: 15.1.2013
 */
@Configurable(preConstruction = true)
public class HakukohteenLiitteetTabImpl extends AbstractVerticalNavigationLayout {

    @Autowired
    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    private Table hakukohteenLiitteetTable = null;

    private Button uusiLiiteBtn;
    private HakukohteenLiitteetViewImpl liitteet;
    private Window hakukohteenLiiteEditWindow = null;


    public HakukohteenLiitteetTabImpl() {
        super();
        setHeight(-1, UNITS_PIXELS);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        uusiLiiteBtn = UiBuilder.button(null,T("uusiLiiteBtn"),new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                 showHakukohdeEditWindow(null);
            }
        });


        horizontalLayout.addComponent(uusiLiiteBtn);
        layout.addComponent(horizontalLayout);

        loadTableWithData();
    }

    public void loadTableWithData() {

        if (hakukohteenLiitteetTable != null) {
            hakukohteenLiitteetTable.removeAllItems();
        }  else {
            hakukohteenLiitteetTable = new Table();
            hakukohteenLiitteetTable.setWidth(100, UNITS_PERCENTAGE);
            getLayout().addComponent(hakukohteenLiitteetTable);

        }

           if (hakukohteenLiitteetTable != null) {
               hakukohteenLiitteetTable.setContainerDataSource(createTableContainer(presenter.loadHakukohdeLiitteet()));


               hakukohteenLiitteetTable.setVisibleColumns(new String[] {"liitteenTyyppi","liitteenSanallinenKuvaus","toimitettavaMennessa",
                       "toimitusOsoite","muokkaaBtn"});
               hakukohteenLiitteetTable.setColumnHeader("liitteenTyyppi",T("tableLiitteenTyyppi"));
               hakukohteenLiitteetTable.setColumnHeader("liitteenSanallinenKuvaus",T("tableKuvaus"));
               hakukohteenLiitteetTable.setColumnHeader("toimitettavaMennessa",T("tableToimMennessa"));
               hakukohteenLiitteetTable.setColumnHeader("toimitusOsoite",T("tableToimitusOsoite"));
               hakukohteenLiitteetTable.setColumnHeader("muokkaaBtn","");
               hakukohteenLiitteetTable.setImmediate(true);





               hakukohteenLiitteetTable.setSizeFull();
               hakukohteenLiitteetTable.requestRepaint();
           }
    }

     public void reloadTableData() {
         if (hakukohteenLiitteetTable != null) {
        hakukohteenLiitteetTable.removeAllItems();
        loadTableWithData();
         }
    }

    private BeanContainer<String,HakukohdeLiiteRow> createTableContainer(List<HakukohdeLiiteViewModel> liites) {
        BeanContainer<String,HakukohdeLiiteRow> liiteContainer = new BeanContainer<String, HakukohdeLiiteRow>(HakukohdeLiiteRow.class);
        for (HakukohdeLiiteViewModel liite:liites) {
             HakukohdeLiiteRow liiteRow = new HakukohdeLiiteRow(liite);
             liiteContainer.addItem(liiteRow.getLiiteId(),liiteRow);
        }
        return liiteContainer;
    }

    public void showHakukohdeEditWindow(String id) {
        if (id == null) {
        presenter.getModel().setSelectedLiite(new HakukohdeLiiteViewModel());
        } else {
           presenter.loadHakukohdeLiiteWithId(id);
        }

        liitteet = new HakukohteenLiitteetViewImpl(presenter,uiBuilder);



        VerticalLayout mainWindowLayout = new VerticalLayout();
        mainWindowLayout.addComponent(liitteet);

        hakukohteenLiiteEditWindow = new Window();
        //hakukohteenLiiteEditWindow.addComponent(liitteet);
        hakukohteenLiiteEditWindow.setContent(mainWindowLayout);
        getWindow().addWindow(hakukohteenLiiteEditWindow);
        mainWindowLayout.setSizeUndefined();
        liitteet.setImmediate(true);
        liitteet.setWidth("900px");


        hakukohteenLiiteEditWindow.setModal(true);
        hakukohteenLiiteEditWindow.center();

    }

    public void closeEditWindow() {
          if (hakukohteenLiiteEditWindow != null) {
              getWindow().removeWindow(hakukohteenLiiteEditWindow);
              hakukohteenLiiteEditWindow = null;
          }
    }

    private void createButtons() {
        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.getModel().setSelectedLiite(null);
                presenter.showMainDefaultView();
                presenter.getHakukohdeListView().reload();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                //presenter.commitHakukohdeForm("VALMIS");

            }
        });

    }



}
