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

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created by: Tuomas Katva
 * Date: 23.1.2013
 */

@Configurable(preConstruction = true)
public class HakukohdeValintakoeRow extends HorizontalLayout {

    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;

    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;

    private ValintakoeViewModel valintakoeViewModel;
    private transient I18NHelper i18n = new I18NHelper(this);

    private String valintakokeenTyyppi;
    private String sanallinenKuvaus;
    private Button muokkaaBtn;

    public HakukohdeValintakoeRow(ValintakoeViewModel valintakoe) {
        valintakoeViewModel = valintakoe;
        valintakokeenTyyppi = valintakoeViewModel.getValintakoeTyyppi();
        resolveSanallinenKuvaus();

        muokkaaBtn = UiUtil.buttonLink(null, i18n.getMessage("muokkaaBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

            }
        });
    }

    private void resolveSanallinenKuvaus() {
        if (valintakoeViewModel != null && valintakoeViewModel.getSanallisetKuvaukset() != null) {
            for (KielikaannosViewModel teksti:valintakoeViewModel.getSanallisetKuvaukset()) {
                if (teksti.getKielikoodi().trim().equalsIgnoreCase(I18N.getLocale().getLanguage().trim())) {
                    sanallinenKuvaus = teksti.getNimi();
                }
            }
        }
    }


}
