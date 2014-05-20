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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;

/*
 * Author: Tuomas Katva
 */
@Configurable(preConstruction=true)
public class ShowKoulutusHakukohdeRow extends HorizontalLayout {
    private static final long serialVersionUID = -5600126973923997354L;

    private SimpleHakukohdeViewModel hakukohdeViewModel;
    //this is quick fix because must check hakukohdes hakutapa for enabling poista-button
    private HakukohteetVastaus moreComplexHakukohdeViewModel;
    private transient I18NHelper i18n = new I18NHelper(this);
    private Button nimiBtn;
    private Button poistaBtn;
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;
    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusHakukohdeRow.class);
    private boolean lukioKoulutus = false;

    @Value("${koodisto-uris.erillishaku}")
    private String hakutapaErillishaku;
    @Value("${koodisto-uris.jatkuvahaku}")
    private String hakutapaJatkuvaHaku;

    public ShowKoulutusHakukohdeRow(SimpleHakukohdeViewModel model) {
        super();
        hakukohdeViewModel = model;
        buildButtons();
    }

    public ShowKoulutusHakukohdeRow(SimpleHakukohdeViewModel model, boolean lukio) {
        super();
        hakukohdeViewModel = model;
        buildButtons();
        lukioKoulutus = lukio;
    }

    private void buildButtons() {
        nimiBtn = UiUtil.buttonLink(null, hakukohdeViewModel.getHakukohdeNimi(), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                tarjontaPresenter.showHakukohdeViewImpl(hakukohdeViewModel.getHakukohdeOid());
            }
        });
        nimiBtn.setStyleName("link-row");
        poistaBtn = UiUtil.buttonLink(null, i18n.getMessage("poistaBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (lukioKoulutus) {
                tarjontaPresenter.getLukioPresenter().showRemoveHakukohdeFromLukioKoulutusDialog(hakukohdeViewModel.getHakukohdeOid(), hakukohdeViewModel.getHakukohdeNimi());
                }else {
                tarjontaPresenter.showRemoveHakukohdeFromKoulutusDialog(hakukohdeViewModel.getHakukohdeOid(), hakukohdeViewModel.getHakukohdeNimi());
                }
            }
        });

        moreComplexHakukohdeViewModel = tarjontaPresenter.findHakukohdeByHakukohdeOid(hakukohdeViewModel.getHakukohdeOid());

        final boolean buttonVisible = tarjontaPresenter.isHakukohdeEditableForCurrentUser(hakukohdeViewModel.getHakukohdeOid()) && !TarjontaTila.JULKAISTU.value().equals(hakukohdeViewModel.getHakukohdeTila());
        
        poistaBtn.setVisible(buttonVisible);

        poistaBtn.setStyleName("link-row");
    }

    protected String T(String key) {
        if (i18n == null) {
            i18n = new I18NHelper(this);
        }
        return i18n.getMessage(key);
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
