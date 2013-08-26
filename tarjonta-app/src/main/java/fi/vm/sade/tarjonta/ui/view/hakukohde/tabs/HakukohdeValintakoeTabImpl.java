/*
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
package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import com.google.common.base.Preconditions;
import com.vaadin.ui.*;
import fi.vm.sade.authentication.service.UserService;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.view.hakukohde.EditHakukohdeView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Validator;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;

import java.text.SimpleDateFormat;

/**
 *
 * @author Markus
 *
 */
public class HakukohdeValintakoeTabImpl extends AbstractEditLayoutView<HakukohdeViewModel, ValintakoeViewImpl> {

    private static final long serialVersionUID = -6105916942362263403L;
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private ValintakoeViewImpl formView;
    @Autowired(required = true)
    private UserService userService;
    private HorizontalLayout headerLayout;
    private KoulutusasteTyyppi koulutusastetyyppi;

    public HakukohdeValintakoeTabImpl(String oid, KoulutusasteTyyppi koulutusastetyyppi) {
        super(oid, SisaltoTyyppi.HAKUKOHDE);
        Preconditions.checkNotNull(koulutusastetyyppi, "KoulutusasteTyyppi enum cannot be null.");
        setMargin(true);
        setHeight(-1, UNITS_PIXELS);
        this.koulutusastetyyppi = koulutusastetyyppi;
    }

    private AbstractLayout buildHeaderLayout() {
        headerLayout = UiUtil.horizontalLayout();

        Label ohjeLabel = new Label(T("ohjeteksti"));
        ohjeLabel.setStyleName(Oph.LABEL_SMALL);

        headerLayout.addComponent(ohjeLabel);

        Label lastUpdBy = new Label(getLastUpdatedBy());
        headerLayout.addComponent(lastUpdBy);

        headerLayout.setSizeFull();
        headerLayout.setComponentAlignment(ohjeLabel, Alignment.MIDDLE_LEFT);
        headerLayout.setComponentAlignment(lastUpdBy, Alignment.MIDDLE_RIGHT);

        return headerLayout;
    }

    public void refreshLastUpdatedBy() {
        if (headerLayout == null) {
            return;
        }
        headerLayout.removeAllComponents();
        Label ohjeLabel = new Label(T("ohjeteksti"));
        ohjeLabel.setStyleName(Oph.LABEL_SMALL);

        headerLayout.addComponent(ohjeLabel);

        Label lastUpdBy = new Label(getLastUpdatedBy());
        headerLayout.addComponent(lastUpdBy);

        headerLayout.setSizeFull();
        headerLayout.setComponentAlignment(ohjeLabel, Alignment.MIDDLE_LEFT);
        headerLayout.setComponentAlignment(lastUpdBy, Alignment.MIDDLE_RIGHT);
    }

    private String getLastUpdatedBy() {
        String lastUpdatedBy = null;

        if (presenter.getModel().getHakukohde() != null && presenter.getModel().getHakukohde().getValintaKokees() != null) {
            ValintakoeViewModel latestAndGreatest = null;
            for (ValintakoeViewModel model : presenter.getModel().getHakukohde().getValintaKokees()) {
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

    private String getLatestUpdaterLabelText(ValintakoeViewModel latestAndGreatest) {
        String latestUpdaterName = tryGetViimPaivittaja(latestAndGreatest.getViimeisinPaivittaja());
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        sb.append(presenter.getModel().getHakukohde().getTila().value());
        sb.append(", ");
        SimpleDateFormat sdf = new SimpleDateFormat(EditHakukohdeView.DATE_PATTERN);
        sb.append(sdf.format(latestAndGreatest.getViimeisinPaivitysPvm()));
        sb.append(", ");
        sb.append(latestUpdaterName);
        sb.append(" )");
        return sb.toString();
    }

    private String tryGetViimPaivittaja(String viimPaivittajaOid) {
        try {
            String userName = null;
            HenkiloType henkilo = userService.findByOid(viimPaivittajaOid);
            if (henkilo.getEtunimet() != null && henkilo.getSukunimi() != null) {
                userName = henkilo.getEtunimet() + " " + henkilo.getSukunimi();
            } else {
                userName = henkilo.getKayttajatunnus();
            }
            return userName;
        } catch (Exception exp) {

            return viimPaivittajaOid;
        }
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        super.buildLayout(layout); //init base navigation here
        final HakukohdeViewModel hakukohde = presenter.getModel().getHakukohde();
        formView = new ValintakoeViewImpl(presenter, getUiBuilder(), koulutusastetyyppi);

        AbstractLayout headerLayout = null;

        if (koulutusastetyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            headerLayout = buildHeaderLayout();
        }

        buildFormLayout(headerLayout, presenter, layout, hakukohde, formView);

        visibleButtonByListener(clickListenerSaveAsDraft, false);
        visibleButtonByListener(clickListenerSaveAsReady, false);
        visibleButtonByListener(clickListenerNext, false);
        visibleButtonByListener(clickListenerBack, false);
        makeFormDataUnmodified();
    }

    @Override
    public void actionNext(ClickEvent event) {
        if (getHakukohdeOid() != null) {
            presenter.showHakukohdeViewImpl(getHakukohdeOid());
        }
    }

    @Override
    public boolean isformDataLoaded() {
        return isLoaded();
    }

    public void validateLukioValintakoeForm() throws Exception {
        errorView.resetErrors();
        boolean pisterajatValidType = formView.getPisterajaTable().validateInputTypes();
        boolean pisterajatCorrect = true;
        boolean alinHKokonaispisteCorrect = true;
        this.formView.getLukioValintakoeView().getForm().commit();
        if (pisterajatValidType) {
            pisterajatCorrect = formView.getPisterajaTable().validateInputRestrictions();
            alinHKokonaispisteCorrect = formView.getPisterajaTable().validateKpAlinH();
        }
        
        if (!pisterajatValidType) {
            errorView.addError(T("validation.pisterajatNotValidType"));
            throw new Validator.InvalidValueException("");
        } else if (!pisterajatCorrect) {
            errorView.addError(T("validation.pisterajatNotValid"));
            throw new Validator.InvalidValueException("");
        } 
        if (!alinHKokonaispisteCorrect) {
            errorView.addError(T("validation.pisterajatAlinHyvNotValid"));
            throw new Validator.InvalidValueException("");
        }

        if (!this.formView.getLukioValintakoeView().getForm().isValid() || !isValintakoeInSync() || !isLisapisteetInSync()) {
            throw new Validator.InvalidValueException("");
        }
    }

    /**
     * Form validation & save for lukio hakukohde exam points.
     *
     * @param tila
     * @param event
     * @return
     * @throws Exception
     */
    @Override
    public String actionSave(SaveButtonState tila, ClickEvent event) throws Exception {
        if (koulutusastetyyppi.equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
            try {
                validateLukioValintakoeForm();
                formView.getPisterajaTable().bindValintakoeData(presenter.getModel().getSelectedValintaKoe());
                formView.getPisterajaTable().bindLisapisteData(presenter.getModel().getSelectedValintaKoe());
                presenter.getModel().getSelectedValintaKoe().setLisanayttoKuvaukset(formView.getLisanayttoKuvaukset());
                presenter.getModel().getHakukohde().getValintaKokees().clear();
                String pkAlinVal = formView.getPisterajaTable().getPkAlinVal();
                String pkYlinVal = formView.getPisterajaTable().getPkYlinVal();
                String pkAlinHyvVal = formView.getPisterajaTable().getPkAlinHyvVal();
                String lpAlinVal = formView.getPisterajaTable().getLpAlinVal();
                String lpYlinVal = formView.getPisterajaTable().getLpYlinVal();
                String lpAlinHyvVal = formView.getPisterajaTable().getLpAlinHyvVal();
                String kpAlinHyvVal = formView.getPisterajaTable().getKpAlinHyvVal();
                presenter.saveHakukohdeValintakoe(formView.getLukioValintakoeView().getValintakokeenKuvaukset());
                formView.getPisterajaTable().setValues(pkAlinVal, pkYlinVal, pkAlinHyvVal, lpAlinVal, lpYlinVal, lpAlinHyvVal, kpAlinHyvVal);
                //Calculcating new hash after save
                makeFormDataUnmodified();
                return getHakukohdeOid();

            } catch (Validator.InvalidValueException e) {
                errorView.addError(e);
                throw e;
            }
        }
        return null;

    }

    private boolean isValintakoeInSync() throws Exception {
        formView.getPisterajaTable().getValintakoe().setLisanayttoKuvaukset(formView.getLisanayttoKuvaukset());

        if (formView.getPisterajaTable().getPkCb().booleanValue() && (!formView.getPisterajaTable().isValintakoePisterajat() || formView.getLukioValintakoeView().getValintakoeAikasTable().getItemIds().isEmpty())) {
            errorView.addError(T("validation.valintakoeDataIsMissing"));
            throw new Validator.InvalidValueException("");
        }
        return true;
    }

    private boolean isLisapisteetInSync() throws Exception {
        if (formView.getPisterajaTable().getLpCb().booleanValue() && (!formView.getPisterajaTable().isLisapisteetPisterajat() || !formView.getPisterajaTable().isLisapisteetSpecified())) {
            errorView.addError(T("validation.lisapisteetDataIsMissing"));
            throw new Validator.InvalidValueException("");
        }
        return true;
    }

    @Override
    protected void eventBack(Button.ClickEvent event) {
        presenter.showMainDefaultView();
        presenter.getHakukohdeListView().reload();
    }

    public ValintakoeViewImpl getFormView() {
        return formView;
    }

    private String getHakukohdeOid() {
        return presenter.getModel().getHakukohde() != null ? presenter.getModel().getHakukohde().getOid() : null;
    }

    private boolean isLoaded() {
        return getHakukohdeOid() != null ? true : false;
    }
}
