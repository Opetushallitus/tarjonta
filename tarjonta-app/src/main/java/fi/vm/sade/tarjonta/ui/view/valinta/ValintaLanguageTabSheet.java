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

import com.vaadin.ui.TabSheet;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.valinta.ValintaperusteModel;
import fi.vm.sade.tarjonta.ui.view.common.LanguageTabSheet;
import fi.vm.sade.tarjonta.ui.presenter.ValintaPresenter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Jani Wilén
 */
@Configurable
public class ValintaLanguageTabSheet extends LanguageTabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(ValintaLanguageTabSheet.class);
    protected ValintaPresenter presenter;
    protected MetaCategory category;

    public ValintaLanguageTabSheet() {
    }

    public ValintaLanguageTabSheet(boolean useRichText, String width, String height) {
        super(useRichText, width, height);

    }

    public ValintaLanguageTabSheet(ValintaPresenter presenter, MetaCategory category, boolean useRichText, String width, String height) {
        super(useRichText, width, height);
        this.presenter = presenter;
        this.category = category;

    }

    protected void addDefaultLanguage() {
        LOG.debug("default language added.");
        String soomiKieli = I18N.getMessage("default.tab");
        Set<String> kielet = new HashSet<String>();
        kielet.add(soomiKieli);
        _languageTabsheet.addTab(soomiKieli, createRichText(""), _uiHelper.getKoodiNimi(soomiKieli));
        _languageTabsheet.getKcSelection().setValue(kielet);
    }

    protected ValintaperusteModel getValintaModel() {
        return presenter.getValintaperustemodel(category);
    }

    @Override
    protected void initializeTabsheet() {
        ValintaperusteModel model = getValintaModel();

        if (model.getKuvaus() != null && !model.getKuvaus().isEmpty()) {
            final List<KielikaannosViewModel> kuvaus = model.getKuvaus();
            setInitialValues(kuvaus);
        } else {
            addDefaultLanguage();
        }

        String soomiKieli = I18N.getMessage("default.tab");
        TabSheet.Tab tab = getTab(soomiKieli);
        if (tab != null) {
            _languageTabsheet.setSelectedTab(tab);
        }
    }
}