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
package fi.vm.sade.tarjonta.ui.view.koulutus.aste2;

import com.vaadin.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.OidCreationException;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 * @author Jani Wilén
 */
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotToinenAsteView extends AbstractEditLayoutView<KoulutusToisenAsteenPerustiedotViewModel, EditKoulutusPerustiedotFormView> {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotToinenAsteView.class);
    private static final long serialVersionUID = -2238485065851932687L;
    private KoulutusToisenAsteenPerustiedotViewModel model;
    @Autowired(required = true)
    private TarjontaPresenter presenter;

    public EditKoulutusPerustiedotToinenAsteView(String oid) {
        super(oid, SisaltoTyyppi.KOMOTO);
        setMargin(true);
        setHeight(-1, UNITS_PIXELS);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        super.buildLayout(layout); //init base navigation here

        /*
         *  FORM LAYOUT (form components under navigation buttons)
         */
        model = presenter.getModel().getKoulutusPerustiedotModel();
        final EditKoulutusPerustiedotFormView formView = new EditKoulutusPerustiedotFormView(presenter, getUiBuilder(), model);

        buildFormLayout("KoulutuksenPerustiedot", presenter, layout, model, formView);
        this.makeFormDataUnmodified();
        
        final boolean draftActive = !model.isLoaded()
                                    || TarjontaTila.LUONNOS.equals(model.getTila())
                                    || TarjontaTila.KOPIOITU.equals(model.getTila()); //enabloitu jos uusi tai tila==draft,kopioitu
        enableButtonByListener(clickListenerSaveAsDraft,draftActive);
    }

    @Override
    public boolean isformDataLoaded() {
        return model.isLoaded();
    }

    @Override
    public String actionSave(SaveButtonState tila, Button.ClickEvent event) throws OidCreationException {
        try {
        presenter.saveKoulutus(tila, KoulutusActiveTab.PERUSTIEDOT);
        return model.getOid();
        } catch (OidCreationException exceptionMessage) {
            if (exceptionMessage.getMessage().equalsIgnoreCase("EditKoulutusPerustiedotYhteystietoView.koulutusExistsMessage")) {
                throw new Validator.InvalidValueException(I18N.getMessage(exceptionMessage.getMessage()));
            }  else {
                throw exceptionMessage;
            }
        }
    }

    @Override
    public void actionNext(ClickEvent event) {
        presenter.showShowKoulutusView();
    }
}
