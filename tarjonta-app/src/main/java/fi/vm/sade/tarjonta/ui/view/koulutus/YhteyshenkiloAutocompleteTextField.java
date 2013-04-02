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
package fi.vm.sade.tarjonta.ui.view.koulutus;


import com.vaadin.event.Action.Handler;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;

/**
 * Autocomplete text field component for adding yhteyshenkilo for koulutus. Uses
 * UserService from authentication-api to search for available users.
 *
 * @author Jani
 *
 */
public class YhteyshenkiloAutocompleteTextField extends AutocompleteTextField implements Handler {

    private static transient final long serialVersionUID = -5390317333957184659L;
    private YhteyshenkiloModel yhteyshenkilo;

    public YhteyshenkiloAutocompleteTextField(VerticalLayout vl,
            String inputPrompt,
            String nullRepresentation,
            TarjontaPresenter presenter,
            YhteyshenkiloModel koulutusModel) {
        super(vl, inputPrompt, nullRepresentation, presenter, null);
        yhteyshenkilo = presenter.getModel().getKoulutusLukioPerustiedot().getYhteyshenkilo();
    }

    @Override
    protected void clearYhteyshenkiloOid() {
        this.yhteyshenkilo.setYhtHenkiloOid(null);
    }
}
