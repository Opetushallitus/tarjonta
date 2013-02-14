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
package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.LanguageTabSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Tuomas Katva
 */
@Configurable
public abstract class HakukohdeLanguageTabSheet extends LanguageTabSheet {

    @Autowired
    protected TarjontaPresenter presenter;

    public HakukohdeLanguageTabSheet() {
    }

    public HakukohdeLanguageTabSheet(boolean useRichText, String width, String height) {
        super(useRichText, width, height);
    }

    public HakukohdeLanguageTabSheet(TarjontaPresenter presenter, boolean useRichText, String width, String height) {
        super(useRichText, width, height);
        this.presenter = presenter;
    }

    public HakukohdeLanguageTabSheet(boolean useRichText, String tabSheetWidth, String tabSheetHeight, String rtWidth, String rtHeight) {
        super(useRichText, tabSheetWidth, tabSheetHeight, rtWidth, rtHeight);
    }
    
    
}