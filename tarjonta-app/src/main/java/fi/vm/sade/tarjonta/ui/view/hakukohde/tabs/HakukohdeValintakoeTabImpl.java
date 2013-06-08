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
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakukohdeNameUriModel;
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

    @Autowired(required = true)
    private TarjontaPresenter presenter;
    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;
    private ValintakoeViewImpl formView;

    @Autowired(required = true)
    private UserService userService;

    private HorizontalLayout headerLayout;

    public  HakukohdeValintakoeTabImpl(String oid) {
        super(oid, SisaltoTyyppi.HAKUKOHDE);
        setMargin(true);
        setHeight(-1, UNITS_PIXELS);
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
        headerLayout.setComponentAlignment(lastUpdBy,Alignment.MIDDLE_RIGHT);

        return headerLayout;
    }

    public void refreshLastUpdatedBy()  {
        headerLayout.removeAllComponents();
        Label ohjeLabel = new Label(T("ohjeteksti"));
        ohjeLabel.setStyleName(Oph.LABEL_SMALL);

        headerLayout.addComponent(ohjeLabel);

        Label lastUpdBy = new Label(getLastUpdatedBy());
        headerLayout.addComponent(lastUpdBy);

        headerLayout.setSizeFull();
        headerLayout.setComponentAlignment(ohjeLabel, Alignment.MIDDLE_LEFT);
        headerLayout.setComponentAlignment(lastUpdBy,Alignment.MIDDLE_RIGHT);
    }

    private String getLastUpdatedBy() {
        String lastUpdatedBy = null;

        if (presenter.getModel().getHakukohde() != null && presenter.getModel().getHakukohde().getValintaKokees() != null) {
            ValintakoeViewModel latestAndGreatest = null;
            for (ValintakoeViewModel model:presenter.getModel().getHakukohde().getValintaKokees()) {
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

    private String getLatestUpdaterLabelText(ValintakoeViewModel latestAndGreatest ) {
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
        formView = new ValintakoeViewImpl(presenter, getUiBuilder());



        buildFormLayout(buildHeaderLayout(), presenter, layout, presenter.getModel().getHakukohde(), formView);


        visibleButtonByListener(clickListenerSaveAsDraft, false);
        visibleButtonByListener(clickListenerSaveAsReady, false);
        visibleButtonByListener(clickListenerNext,false);
        visibleButtonByListener(clickListenerBack,false);

       /* if (presenter.getModel().getHakukohde().getKoulukses() == null
                || presenter.getModel().getHakukohde().getKoulukses().isEmpty() 
                || !presenter.getModel().getHakukohde().getKoulukses().get(0).getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
            visibleButtonByListener(clickListenerSaveAsDraft, false);
            visibleButtonByListener(clickListenerSaveAsReady, false);
        }*/
    }

    private static final long serialVersionUID = -6105916942362263403L;

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

    @Override
    public String actionSave(SaveButtonState tila, ClickEvent event)
            throws Exception {
        if (formView.getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
            try {
                errorView.resetErrors();
                boolean pisterajatValidType = formView.getPisterajaTable().validateInputTypes();
                boolean pisterajatCorrect = true; 
                if (pisterajatValidType) {
                    pisterajatCorrect = formView.getPisterajaTable().validateInputRestrictions(); 
                }
                this.formView.getValintakoeComponent().getForm().commit();
                if (!pisterajatValidType) {
                    errorView.addError(T("validation.pisterajatNotValidType"));
                    throw new Validator.InvalidValueException("");
                } else if (!pisterajatCorrect) {
                    errorView.addError(T("validation.pisterajatNotValid"));
                    throw new Validator.InvalidValueException("");
                }
                if (this.formView.getValintakoeComponent().getForm().isValid()) {
                    formView.getPisterajaTable().bindData(presenter.getModel().getSelectedValintaKoe());
                    presenter.getModel().getSelectedValintaKoe().setLisanayttoKuvaukset(formView.getLisanayttoKuvaukset());
                    presenter.getModel().getHakukohde().getValintaKokees().clear();
                    presenter.saveHakukohdeValintakoe(formView.getValintakoeComponent().getValintakokeenKuvaukset());
                    return getHakukohdeOid();
                } else {
                    throw new Validator.InvalidValueException("");
                }
            } catch (Validator.InvalidValueException e) {
                errorView.addError(e);
                throw e;
            } 
        }
        return null;
        
    }
    
    @Override
    protected void eventBack(Button.ClickEvent event) {
        presenter.showMainDefaultView();
        presenter.getHakukohdeListView().reload();
    }
    
    public ValintakoeViewImpl getFormView() {
        return formView;
    }
    
    private String getUriWithVersion(HakukohdeNameUriModel hakukohdeNameUriModel) {
        return hakukohdeNameUriModel.getHakukohdeUri() + TarjontaUIHelper.KOODI_URI_AND_VERSION_SEPARATOR + hakukohdeNameUriModel.getUriVersio();
    }
    
    private String getHakukohdeOid() {
        return presenter.getModel().getHakukohde() != null ? presenter.getModel().getHakukohde().getOid() : null;
    }

    private boolean isLoaded() {
        return getHakukohdeOid() != null ? true : false;
    }

}
