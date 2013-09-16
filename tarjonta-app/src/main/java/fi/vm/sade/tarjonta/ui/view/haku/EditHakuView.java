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
package fi.vm.sade.tarjonta.ui.view.haku;

import java.util.Date;
import java.util.List;

import com.vaadin.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.presenter.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Jani Wilén
 */
public class EditHakuView extends AbstractEditLayoutView<HakuViewModel, EditHakuForm> {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(EditHakuView.class);
	
    private HakuViewModel model;
    private EditHakuForm formView;
    @Autowired(required = true)
    private HakuPresenter presenter;

    public static final String YHTEISHAKU_URI = "hakutapa_01";

    public EditHakuView(String oid) {
        super(oid, SisaltoTyyppi.HAKU);
        setMargin(true);
        setHeight(-1, UNITS_PIXELS);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        super.buildLayout(layout); //init base navigation here

        /*
         *  FORM LAYOUT (form components under navigation buttons)
         */
        model = presenter.getHakuModel();
        formView = new EditHakuForm();
        setTilaNestedProperty("hakuDto.haunTila");

        buildFormLayout("HaunTiedot", presenter, layout, model, formView);
    }

    @Override
    public boolean isformDataLoaded() {
        return model.getHakuOid() != null;
    }
    //Override validateFormDate method to add custom validations to same error messages and validation
    @Override
    public void validateFormData() throws Validator.InvalidValueException {
        errorView.resetErrors();
        List<String> errorMessages = formView.getSisaisetHakuajatContainer().bindHakuajat();
        errorMessages.addAll(formView.checkNimi());
        if (!errorMessages.isEmpty()) {
            for (String curMessage : errorMessages) {
                this.errorView.addError(I18N.getMessage(curMessage));
            }
            form.commit();
            throw new Validator.InvalidValueException("");
        }
        else {
            form.commit();
        }
    }

    private Date getAlkamisaika() {
        Date alkamisPvm = null;
        int counter = 0;
        for (HakuaikaViewModel hakuAika: presenter.getHakuModel().getSisaisetHakuajat()) {
            if (counter == 0) {
              alkamisPvm = hakuAika.getAlkamisPvm();
            } else {
              if (hakuAika.getAlkamisPvm().before(alkamisPvm)) {
                  alkamisPvm = hakuAika.getAlkamisPvm();
              }
            }
            counter++;
        }
        return alkamisPvm;
    }

    @Override
    public String actionSave(SaveButtonState tila, Button.ClickEvent event) throws ExceptionMessage {

        String selectedHakutapa = presenter.getModel().getHakutapa();
        Date today = new Date();
        Date haunAlkamisPvm = getAlkamisaika();
        if (haunAlkamisPvm.before(today)) {
            errorView.addError(getI18n().getMessage("hakualkamisaikaMenneessaMsg"));
            throw new ExceptionMessage(getI18n().getMessage("hakualkamisaikaMenneessaMsg"));
        }
        if (selectedHakutapa.trim().contains(YHTEISHAKU_URI)) {
            if (!presenter.getHakuModel().isHaussaKaytetaanSijoittelua() || !presenter.getHakuModel().isKaytetaanJarjestelmanHakulomaketta()) {
            errorView.addError(getI18n().getMessage("yhteishakuMsg"));
            throw new ExceptionMessage(getI18n().getMessage("yhteishakuMsg"));
            }
        }
        if (presenter.getHakuModel().isKaytetaanJarjestelmanHakulomaketta()) {
            presenter.getHakuModel().setHakuLomakeUrl(null);
        }


        presenter.saveHaku(tila);

        return model.getHakuOid();
    }

    @Override
    public void actionNext(Button.ClickEvent event) {
        presenter.showHakuView(model);
    }

    @Override
    protected void eventBack(Button.ClickEvent event) {
        presenter.showMainDefaultView();
    }
}
