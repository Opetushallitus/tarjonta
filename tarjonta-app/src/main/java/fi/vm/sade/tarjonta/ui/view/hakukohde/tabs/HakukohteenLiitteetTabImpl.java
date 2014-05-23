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


import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.authentication.service.UserService;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.EditHakukohdeView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Created by: Tuomas Katva Date: 15.1.2013
 */
@Configurable(preConstruction = true)
public class HakukohteenLiitteetTabImpl extends AbstractVerticalNavigationLayout {

    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    private Table hakukohteenLiitteetTable = null;
    private Button uusiLiiteBtn;
    private HakukohdeLiiteetDialog liitteetDialog = null;

    @Autowired(required = true)
    private UserService userService;

    private HorizontalLayout headerLayout;

    public HakukohteenLiitteetTabImpl(TarjontaPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        headerLayout = UiUtil.horizontalLayout();
        headerLayout.setMargin(false,false,true,false);
        layout.addComponent(headerLayout);
        UiUtil.hr(layout);
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

    private AbstractLayout buildHeaderLayout(List<HakukohdeLiiteViewModel> liites) {
        if (headerLayout == null) {
        headerLayout = UiUtil.horizontalLayout();
        } else {
            headerLayout.removeAllComponents();
        }

        Label ohjeLabel = new Label(T("ohjeteksti"));
        ohjeLabel.setStyleName(Oph.LABEL_SMALL);

        headerLayout.addComponent(ohjeLabel);

        Label lastUpdBy = new Label(getLastUpdatedBy(liites));
        headerLayout.addComponent(lastUpdBy);

        headerLayout.setSizeFull();
        headerLayout.setComponentAlignment(ohjeLabel, Alignment.MIDDLE_LEFT);
        headerLayout.setComponentAlignment(lastUpdBy,Alignment.MIDDLE_RIGHT);

        return headerLayout;
    }

    private String getLastUpdatedBy(List<HakukohdeLiiteViewModel> liites) {
        String lastUpdatedBy = null;

        if (liites != null) {
            HakukohdeLiiteViewModel latestAndGreatest = null;
            for (HakukohdeLiiteViewModel model:liites) {
                if (latestAndGreatest == null) {
                    latestAndGreatest = model;
                } else {

                    if (model.getViimeisinPaivitysPvm().after(latestAndGreatest.getViimeisinPaivitysPvm())) {
                        latestAndGreatest = model;
                    }

                }

            }

            if (latestAndGreatest != null) {

                lastUpdatedBy = getLatestUpdaterLabelText(latestAndGreatest);

            }

        } else {
            lastUpdatedBy = "";
        }
        return lastUpdatedBy;
    }

    private String getLatestUpdaterLabelText(HakukohdeLiiteViewModel latestAndGreatest ) {
        String latestUpdaterName = tryGetViimPaivittaja(latestAndGreatest.getViimeisinPaivittaja());
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        sb.append(presenter.getModel().getHakukohde().getTila().value());
        sb.append(", ");
        SimpleDateFormat sdf = new SimpleDateFormat(EditHakukohdeView.DATE_PATTERN);
        sb.append(sdf.format(latestAndGreatest.getViimeisinPaivitysPvm()));
        sb.append(", ");
        sb.append(latestUpdaterName);
        sb.append( " )" );
        return sb.toString();
    }

    private String tryGetViimPaivittaja(String viimPaivittajaOid) {
        try {
            String userName = null;
            HenkiloType henkilo = userService.findByOid(viimPaivittajaOid);
            if (henkilo.getEtunimet() != null && henkilo.getSukunimi() != null) {
                userName = henkilo.getEtunimet() + " " + henkilo.getSukunimi();
            }  else {
                userName = henkilo.getKayttajatiedot().getUsername();
            }
            return userName;
        } catch (Exception exp) {

            return viimPaivittajaOid;
        }
    }

    private String cutString(String stringToCut) {
        if (stringToCut.length() > 100) {
            return stringToCut.substring(0,100) + "...";
        } else {
            return stringToCut;
        }
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
                        Label label = new Label(cutString((String)item.getItemProperty("liitteenSanallinenKuvaus").getValue()));
                        label.setContentMode(Label.CONTENT_XHTML);
                        return label;
                    } else {
                        return null;
                    }
                }
            });

            hakukohteenLiitteetTable.addGeneratedColumn("toimitusOsoite", new Table.ColumnGenerator() {
                @Override
                public Object generateCell(Table table, Object o, Object o2) {
                    if (table != null) {
                        Item item = table.getItem(o);
                        String toimOsoite =  (String)item.getItemProperty("toimitusOsoite").getValue();
                        Label label = new Label(toimOsoite);
                        label.setContentMode(Label.CONTENT_TEXT);
                        return label;
                    } else {
                        return null;
                    }
                }
            });
        }

        if (hakukohteenLiitteetTable != null) {
            List<HakukohdeLiiteViewModel> loadHakukohdeLiitteet = presenter.loadHakukohdeLiitteet(true);
            buildHeaderLayout(loadHakukohdeLiitteet);
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

            hakukohteenLiitteetTable.setColumnExpandRatio("liitteenTyyppi",0.3f);
            hakukohteenLiitteetTable.setColumnExpandRatio("liitteenSanallinenKuvaus",0.4f);
            hakukohteenLiitteetTable.setColumnExpandRatio("toimitettavaMennessa",0.25f);
            hakukohteenLiitteetTable.setColumnExpandRatio("toimitusOsoite",0.4f);
            hakukohteenLiitteetTable.setColumnExpandRatio("muokkaaBtn",0.1f);
            hakukohteenLiitteetTable.setColumnExpandRatio("poistaBtn",0.1f);

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
        boolean isNew = true;
        if (id == null) {
            presenter.getModel().setSelectedLiite(new HakukohdeLiiteViewModel());
            presenter.setCustomLiiteOsoiteSelected(false);
            isNew = true;
        } else {
            presenter.loadHakukohdeLiiteWithId(id);
            isNew = false;
        }

        liitteetDialog = new HakukohdeLiiteetDialog(presenter, uiBuilder,isNew);
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
