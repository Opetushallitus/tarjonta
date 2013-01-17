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

    private BeanItem<HakukohdeLiiteViewModel> hakukohdeLiiteBean;

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
        initTable(layout);
    }

    private void loadTableWithData(List<HakukohdeLiiteViewModel> liitteet) {
           if (hakukohteenLiitteetTable != null) {
               hakukohteenLiitteetTable.setContainerDataSource(createTableContainer(liitteet));
               hakukohteenLiitteetTable.setSelectable(true);
               hakukohteenLiitteetTable.setVisibleColumns(new String[] {"liitteeTyyppiKoodistoNimi","localizedKuvaus","toimitusPvmTablePresentation",
               "toimitusOsoiteConcat"});
               hakukohteenLiitteetTable.setColumnHeader("liitteeTyyppiKoodistoNimi",T("tableLiitteenTyyppi"));
               hakukohteenLiitteetTable.setColumnHeader("localizedKuvaus",T("tableKuvaus"));
               hakukohteenLiitteetTable.setColumnHeader("toimitusPvmTablePresentation",T("tableToimMennessa"));
               hakukohteenLiitteetTable.setColumnHeader("toimitusOsoiteConcat",T("tableToimitusOsoite"));

               hakukohteenLiitteetTable.setImmediate(true);
               hakukohteenLiitteetTable.setMultiSelect(false);
               hakukohteenLiitteetTable.setSizeFull();
               hakukohteenLiitteetTable.requestRepaint();
           }
    }

    public void initTable(AbstractLayout layout) {

        if (hakukohteenLiitteetTable != null) {
            hakukohteenLiitteetTable.removeAllItems();
        }  else {
            hakukohteenLiitteetTable = new Table();
            layout.addComponent(hakukohteenLiitteetTable);
        }



    }

     public void reloadTableData() {
         if (hakukohteenLiitteetTable != null) {
        hakukohteenLiitteetTable.removeAllItems();
        loadTableWithData(presenter.loadHakukohdeLiitteet());
         }
    }

    private BeanContainer<String,HakukohdeLiiteViewModel> createTableContainer(List<HakukohdeLiiteViewModel> liites) {
        BeanContainer<String,HakukohdeLiiteViewModel> liiteContainer = new BeanContainer<String, HakukohdeLiiteViewModel>(HakukohdeLiiteViewModel.class);
        for (HakukohdeLiiteViewModel liite:liites) {
             liiteContainer.addItem(liite.getHakukohdeLiiteId(),liite);
        }
        return liiteContainer;
    }

    private void showHakukohdeEditWindow(String id) {
        if (id == null) {
        presenter.getModel().setSelectedLiite(new HakukohdeLiiteViewModel());
        } else {
            //TODO load liite from database and show it
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

        /*addNavigationButton(T("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

            }
        });*/

        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                //presenter.commitHakukohdeForm("VALMIS");

            }
        });

    }



   /* private void saveForm() {
        try {
            errorView.resetErrors();

            presenter.getModel().getSelectedLiite().getLiitteenSanallinenKuvaus().addAll(liitteet.getLiitteenSanallisetKuvaukset());
            presenter.saveHakukohdeLiite();
        }   catch (Validator.InvalidValueException e) {
            errorView.addError(e);
            presenter.showNotification(UserNotification.GENERIC_VALIDATION_FAILED);
        }
    }*/
}
