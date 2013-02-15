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


import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by: Tuomas Katva Date: 16.1.2013
 */
public class LiitteenSanallinenKuvausTabSheet extends HakukohdeLanguageTabSheet {

    public LiitteenSanallinenKuvausTabSheet(boolean useRichText, String width, String height) {
        super(useRichText, width, height);
    }


    @Override
    protected void initializeTabsheet() {
        final TarjontaModel model = presenter.getModel();

        if (model.getSelectedLiite() != null) {
            setInitialValues(model.getSelectedLiite().getLiitteenSanallinenKuvaus());
            if (model.getSelectedLiite().getLiitteenSanallinenKuvaus() == null || model.getSelectedLiite().getLiitteenSanallinenKuvaus().size() < 1) {
                String soomiKieli = I18N.getMessage("default.tab");
                _languageTabsheet.setSelectedTab(getTab(soomiKieli));
                Set<String> valittuKieli = new HashSet<String>();
                valittuKieli.add(soomiKieli);
                _languageTabsheet.getKcSelection().setValue(valittuKieli);
                _languageTabsheet.setSelectedTab(getTab(soomiKieli));
            }
        }
    }
}
