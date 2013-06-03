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
package fi.vm.sade.tarjonta.ui.view.koulutus.kk;

import com.vaadin.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author Jani Wilén
 */
@Configurable(preConstruction = true)
public class EditKorkeakouluPerustiedotView extends AbstractEditLayoutView<KorkeakouluPerustiedotViewModel, EditKorkeakouluPerustiedotFormView> {

    private static final Logger LOG = LoggerFactory.getLogger(EditKorkeakouluPerustiedotView.class);
    private static final long serialVersionUID = 2756886453541825771L;
    private KorkeakouluPerustiedotViewModel model;
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    @Autowired
    private transient TarjontaUIHelper uiHelper;

    public EditKorkeakouluPerustiedotView(String oid) {
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
        model = presenter.getModel().getKorkeakouluPerustiedot();
        EditKorkeakouluPerustiedotFormView formView = new EditKorkeakouluPerustiedotFormView(presenter, getUiBuilder(), uiHelper, model);
        buildFormLayout("KoulutuksenPerustiedot", presenter, layout, model, formView);
    }

    @Override
    public boolean isformDataLoaded() {
        return model.isLoaded();
    }

    @Override
    public String actionSave(SaveButtonState tila, Button.ClickEvent event) throws ExceptionMessage {
        try {
            presenter.getKorkeakouluPresenter().saveKoulutus(tila);
            presenter.getKorkeakouluPresenter().getReloadKoulutusListData();
            return model.getKomotoOid();
        } catch (ExceptionMessage exceptionMessage) {
            if (exceptionMessage.getMessage().equalsIgnoreCase("EditKoulutusPerustiedotYhteystietoView.koulutusExistsMessage")) {
                throw new Validator.InvalidValueException(I18N.getMessage(exceptionMessage.getMessage()));
            } else {
                throw exceptionMessage;
            }
        }
    }

    @Override
    public void actionNext(ClickEvent event) {
        presenter.getKorkeakouluPresenter().showSummaryKoulutusView();
    }
}
