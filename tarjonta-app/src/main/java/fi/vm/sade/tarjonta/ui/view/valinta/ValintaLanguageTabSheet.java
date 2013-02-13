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
package fi.vm.sade.tarjonta.ui.view.valinta;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.valinta.ValintaperusteModel;
import fi.vm.sade.tarjonta.ui.view.common.LanguageTabSheet;
import fi.vm.sade.tarjonta.ui.presenter.ValintaPresenter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Jani Wilén
 */
@Configurable
public class ValintaLanguageTabSheet extends LanguageTabSheet {

    protected ValintaPresenter presenter;
    protected MetaCategory category;

    public ValintaLanguageTabSheet() {
    }

    public ValintaLanguageTabSheet(boolean useRichText, String width, String height) {
        super(useRichText, width, height);
    }

    public ValintaLanguageTabSheet(boolean useRichText, String tabSheetWidth, String tabSheetHeight, String rtWidth, String rtHeight) {
        super(useRichText, tabSheetWidth, tabSheetHeight, rtWidth, rtHeight);
    }

    public ValintaLanguageTabSheet(ValintaPresenter presenter, MetaCategory category, boolean useRichText, String width, String height) {
        super(useRichText, width, height);
        this.presenter = presenter;
        this.category = category;
    }

    protected void addDefaultLanguage() {
        String soomiKieli = I18N.getMessage("default.tab");
        Set<String> kielet = new HashSet<String>();
        kielet.add(soomiKieli);
        _languageTabsheet.getKcSelection().setValue(kielet);
        _languageTabsheet.setSelectedTab(getTab(soomiKieli));
    }

    protected ValintaperusteModel getValintaModel() {
        return presenter.getValintaperustemodel(category);
    }

    @Override
    protected void initializeTabsheet() {
        ValintaperusteModel model = getValintaModel();

        if (model.getKuvaus() != null) {
            final List<KielikaannosViewModel> kuvaus = model.getKuvaus();
            setInitialValues(kuvaus);

            if (kuvaus.size() < 1) {
                String soomiKieli = I18N.getMessage("default.tab");
                Set<String> kielet = new HashSet<String>();
                kielet.add(soomiKieli);
                _languageTabsheet.getKcSelection().setValue(kielet);
                _languageTabsheet.setSelectedTab(getTab(soomiKieli));
            } else {
                addDefaultLanguage();
            }
        } else {
            addDefaultLanguage();
        }
    }
}