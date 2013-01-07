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

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/*
* Author: Tuomas Katva
*/
@Configurable
public class ShowKoulutusHakukohdeRow extends HorizontalLayout {

    private SimpleHakukohdeViewModel hakukohdeViewModel;
    private I18NHelper i18n = new I18NHelper(this);
    private Button nimiBtn;
    private Button poistaBtn;
    private I18NHelper i18NHelper;

    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;

    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusHakukohdeRow.class);


  public ShowKoulutusHakukohdeRow(SimpleHakukohdeViewModel model) {
      super();
        hakukohdeViewModel = model;
        buildButtons();
    }

    private void buildButtons() {
        nimiBtn = UiUtil.buttonLink(null, hakukohdeViewModel.getHakukohdeNimi(), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
               tarjontaPresenter.showHakukohdeViewImpl(hakukohdeViewModel.getHakukohdeOid());
            }
        });

        poistaBtn = UiUtil.buttonLink(null, i18n.getMessage("poistaBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
            tarjontaPresenter.showRemoveHakukohdeFromKoulutusDialog(hakukohdeViewModel.getHakukohdeOid(),hakukohdeViewModel.getHakukohdeNimi());
            }
        });

    }

    protected String T(String key) {
        if (i18NHelper == null) {
        i18NHelper = new I18NHelper(this);
        }
        return i18NHelper.getMessage(key);
    }

    public Button getNimiBtn() {
        return nimiBtn;
    }

    public void setNimiBtn(Button nimiBtn) {
        this.nimiBtn = nimiBtn;
    }

    public Button getPoistaBtn() {
        return poistaBtn;
    }

    public void setPoistaBtn(Button poistaBtn) {
        this.poistaBtn = poistaBtn;
    }

    public TarjontaPresenter getTarjontaPresenter() {
        return tarjontaPresenter;
    }

    public void setTarjontaPresenter(TarjontaPresenter tarjontaPresenter) {
        this.tarjontaPresenter = tarjontaPresenter;
    }
}
