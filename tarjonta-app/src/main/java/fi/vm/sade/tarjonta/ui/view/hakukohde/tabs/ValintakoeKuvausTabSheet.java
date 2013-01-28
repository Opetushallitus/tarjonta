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

/**
 * Created by: Tuomas Katva
 * Date: 23.1.2013
 */
public class ValintakoeKuvausTabSheet  extends LanguageTabSheet {

    public ValintakoeKuvausTabSheet(boolean useRichText) {
        super(useRichText);
    }

    @Override
    protected void initializeTabsheet() {
        if (_model.getSelectedValintaKoe() != null) {
            setInitialValues(_model.getSelectedValintaKoe().getSanallisetKuvaukset());
        }
    }
}
