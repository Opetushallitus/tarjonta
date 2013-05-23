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
package fi.vm.sade.tarjonta.ui.view.hakukohde;

import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeNameUriModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.*;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author Tuomas Katva
 */
@Configurable(preConstruction = true)
public class EditHakukohdeView extends AbstractEditLayoutView<HakukohdeViewModel, PerustiedotViewImpl> {
    
    private static final long serialVersionUID = 8806220426371090907L;
    @Autowired
    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    @Autowired(required = true)
    private TarjontaUIHelper uiHelper;
    private TabSheet tabs;
    private TabSheet.Tab perustiedotTab;
    private TabSheet.Tab liitteetTab;
    private TabSheet.Tab valintakokeetTab;
    private HakukohteenLiitteetTabImpl liitteet;
    private HakukohdeValintakoeTabImpl valintakokeet;
    private PerustiedotViewImpl perustiedot;

    public static final String DATE_PATTERN = "dd.MM.yyyy";
    
    public EditHakukohdeView(String oid) {
        super(oid, SisaltoTyyppi.HAKUKOHDE);
        addTopInfoMessage(oid);
        setMargin(true);
        setHeight(-1, UNITS_PIXELS);
    }

    private void addTitleLayout() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        if (presenter.getModel().getHakukohde() != null && presenter.getModel().getHakukohde().getOid() != null) {
           Label hakukohdeNameLbl = new Label(uiHelper.getKoodiNimi(presenter.getModel().getHakukohde().getHakukohdeNimi()));
            hakukohdeNameLbl.setStyleName(Oph.LABEL_H1);

            StringBuilder tilaSb = new StringBuilder();
            tilaSb.append("( ");
            tilaSb.append(presenter.getModel().getHakukohde().getTila().value());
            tilaSb.append(" ,tallennettu ");
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            tilaSb.append(sdf.format(presenter.getModel().getHakukohde().getViimeisinPaivitysPvm()));
            tilaSb.append(", ");
            tilaSb.append(presenter.getModel().getHakukohde().getViimeisinPaivittaja());
            tilaSb.append(")");
            Label tilaLbl = new Label(tilaSb.toString());


            hl.addComponent(hakukohdeNameLbl);
            hl.addComponent(tilaLbl);
            hl.setComponentAlignment(hakukohdeNameLbl,Alignment.MIDDLE_LEFT);
            hl.setComponentAlignment(tilaLbl,Alignment.MIDDLE_RIGHT);
            hl.setMargin(false,false,true,false);
            addComponent(hl);

        }

    }

    private void addTopInfoMessage(String oid) {

        VerticalLayout vl = new VerticalLayout();

        List<KoulutusOidNameViewModel> koulutusOidNameViewModels  = null;
        if (oid != null) {
            koulutusOidNameViewModels =   presenter.getHakukohdeKoulutukses(oid);
        } else {
            koulutusOidNameViewModels = presenter.getModel().getHakukohdeTitleKoulutukses();
        }
        String labelTitle = "";
        StringBuilder sb = new StringBuilder();
        sb.append(getI18n().getMessage("valitutKoulutuksetTitle"));
        sb.append(" ");
        if (koulutusOidNameViewModels != null) {
            int counter = 0;
           for (KoulutusOidNameViewModel oidNameViewModel : koulutusOidNameViewModels) {
               if (counter != 0) {
                  sb.append(", ");
               }
               sb.append(oidNameViewModel.getKoulutusNimi());
           }
        }
        labelTitle = sb.toString();
        Label lbl = new Label(labelTitle);
        lbl.setStyleName(Oph.LABEL_SMALL);
        vl.addComponent(lbl);
        vl.setMargin(false,false,true,false);
        super.setTopInfoLayout(vl);

    }

    public void enableValintakokeetTab() {
        if (valintakokeetTab != null) {
            valintakokeetTab.setEnabled(true);
        }
    }
    
    public void enableLiitteetTab() {
        if (liitteetTab != null) {
            liitteetTab.setEnabled(true);
        }
    }
    
    public void loadLiiteTableWithData() {
        if (liitteet != null) {
            liitteet.loadTableWithData();
        }
    }

    public void loadValintakokees() {
        if (valintakokeet != null) {
            valintakokeet.getFormView().loadTableData();
        }
    }

    public void closeValintakoeEditWindow() {
        if (valintakokeet != null) {
            valintakokeet.getFormView().closeValintakoeEditWindow();
        }
    }
    
    public void closeHakukohdeLiiteEditWindow() {
        if (liitteet != null) {
            liitteet.closeEditWindow();
        }
    }

    public void showHakukohdeValintakoeEditView(String valintakoeId) {
        if (valintakokeet != null) {
            valintakokeet.getFormView().showValintakoeEditWithId(valintakoeId);
        }
    }
    
    public void showHakukohdeEditWindow(String liiteId) {
        if (liitteet != null) {
            liitteet.showHakukohdeEditWindow(liiteId);
        }
    }

    @Override
    public boolean isformDataLoaded() {
        return isLoaded();
    }

    private boolean isLoaded() {
        return getHakukohdeOid() != null ? true : false;
    }

    private String getHakukohdeOid() {
        return presenter.getModel().getHakukohde() != null ? presenter.getModel().getHakukohde().getOid() : null;
    }

    @Override
    public String actionSave(SaveButtonState tila, Button.ClickEvent event) throws Exception {


        HakukohdeViewModel hakukohde = presenter.getModel().getHakukohde();
        hakukohde.getLisatiedot().clear();
        hakukohde.getLisatiedot().addAll(perustiedot.getLisatiedot());
        hakukohde.setHakuaika(perustiedot.getSelectedHakuaika());
        if (!perustiedot.isSahkoinenToimOsoiteChecked()) {
            hakukohde.setLiitteidenSahkoinenToimitusOsoite("");
        }
        // TODO call subform to perform validation (weigthed stdies can FAIL and still the save succeeds)
        // formView.validateExtraData();

        HakukohdeNameUriModel selectedHakukohde = perustiedot.getSelectedHakukohde();
        hakukohde.setHakukohdeNimi(getUriWithVersion(selectedHakukohde));

        for(TextField tf: perustiedot.getPainotettavat()){
            tf.validate();
        }

        //XXXX validoi painotettavat

        if (presenter.getModel().getHakukohde().getKoulukses() != null
                || !presenter.getModel().getHakukohde().getKoulukses().isEmpty()
                || presenter.getModel().getHakukohde().getKoulukses().get(0).getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {

            if (valintakokeetTab.isEnabled()) {
                try {
                    valintakokeet.actionSave(null,null);
                } catch (Validator.InvalidValueException e) {
                    errorView.addError(T("tarkistaValintakoe"));
                    return null;
                }
            }
        }
        presenter.saveHakuKohde(tila);

        return getHakukohdeOid();
    }

    private String getUriWithVersion(HakukohdeNameUriModel hakukohdeNameUriModel) {
        return hakukohdeNameUriModel.getHakukohdeUri() + TarjontaUIHelper.KOODI_URI_AND_VERSION_SEPARATOR + hakukohdeNameUriModel.getUriVersio();
    }

    @Override
    protected void eventBack(Button.ClickEvent event) {
        presenter.showMainDefaultView();
        presenter.getHakukohdeListView().reload();
    }

    @Override
    public void actionNext(Button.ClickEvent event) {

        if (getHakukohdeOid() != null) {
            presenter.showHakukohdeViewImpl(getHakukohdeOid());
        }
    }
    
    @Override
    protected void buildLayout(VerticalLayout layout) {

        super.buildLayout(layout);

        addTitleLayout();
        String hakukohdeOid = null;
        if (presenter.getModel().getHakukohde() != null && presenter.getModel().getHakukohde().getOid() != null) {
            hakukohdeOid = presenter.getModel().getHakukohde().getOid();
        }
        

        tabs = UiBuilder.tabSheet(layout);
        layout.setMargin(false,false,true,false);
        VerticalLayout wrapperVl = new VerticalLayout();
        perustiedot = new PerustiedotViewImpl(presenter,uiBuilder);
        buildFormLayout(null, presenter, wrapperVl, presenter.getModel().getHakukohde(), perustiedot);

        
        liitteet = new HakukohteenLiitteetTabImpl();
        valintakokeet = new HakukohdeValintakoeTabImpl(hakukohdeOid);
        perustiedotTab = tabs.addTab(wrapperVl, T("tabNimi"));
        valintakokeetTab = tabs.addTab(valintakokeet,T("valintakoeTab"));
        liitteetTab = tabs.addTab(liitteet, T("liitteetTab"));
        liitteetTab.setEnabled(hakukohdeOid != null);
        valintakokeetTab.setEnabled(hakukohdeOid != null);

    }

    public void setValintakokeetTabSelected() {
        if (tabs != null && valintakokeetTab != null) {

            tabs.setSelectedTab(valintakokeetTab);
        }
    }

    public void setLiitteetTabSelected() {
        if (tabs != null && liitteetTab != null) {
            tabs.setSelectedTab(liitteetTab);
        }
    }
}
