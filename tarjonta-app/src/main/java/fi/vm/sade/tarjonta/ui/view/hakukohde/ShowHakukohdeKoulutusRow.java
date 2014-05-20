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
package fi.vm.sade.tarjonta.ui.view.hakukohde;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/*
 * Author: Tuomas Katva
 */
@Configurable(preConstruction=true)
public class ShowHakukohdeKoulutusRow extends HorizontalLayout {
    private static final long serialVersionUID = 6701627452818790862L;

    private KoulutusOidNameViewModel koulutusOidNameViewModel;
    private Button nimiBtn;
    private Button poistaBtn;
    private transient I18NHelper i18n = new I18NHelper(this);
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;
    private static final Logger LOG = LoggerFactory.getLogger(ShowHakukohdeKoulutusRow.class);
    private boolean canRemoveKoulutus;

    public ShowHakukohdeKoulutusRow(KoulutusOidNameViewModel koulutusOidNameViewModel, boolean canRemoveKoulutus) {
        this.koulutusOidNameViewModel = koulutusOidNameViewModel;
        this.canRemoveKoulutus = canRemoveKoulutus;
        buildBtns();
    }

    private void buildBtns() {
        nimiBtn = UiUtil.buttonLink(null, koulutusOidNameViewModel.getKoulutusNimi(), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                tarjontaPresenter.showShowKoulutusView(koulutusOidNameViewModel.getKoulutusOid());
            }
        });
        nimiBtn.setStyleName("link-row");
        if (canRemoveKoulutus) {
        poistaBtn = UiUtil.buttonLink(null, i18n.getMessage("poistaBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                tarjontaPresenter.showRemoveKoulutusFromHakukohdeDialog(koulutusOidNameViewModel);
            }
        });
        poistaBtn.setStyleName("link-row");
        }
        //button permissions:
        Preconditions.checkNotNull(tarjontaPresenter, "Tarjonta presenter cannot be null");
        final OrganisaatioContext context = OrganisaatioContext.getContext(tarjontaPresenter.getTarjoaja().getSelectedOrganisationOid());
        if (poistaBtn != null) {
            //nappula saa olla aktiivinen vain jos hakukohde ei ole julkaistu ja viimeinen koulutus
            final boolean buttonVisible = tarjontaPresenter.getPermission().userCanUpdateHakukohde(context) && tarjontaPresenter.getModel().getHakukohde().getKoulukses().size()!=1;
            poistaBtn.setVisible(buttonVisible);
        }
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
}
