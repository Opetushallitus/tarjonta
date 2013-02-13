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
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * Created by: Tuomas Katva
 * Date: 21.1.2013
 */
@Configurable(preConstruction = true)
public class HakukohdeLiiteRow extends HorizontalLayout {


    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;

    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;

    private HakukohdeLiiteViewModel hakukohdeLiiteViewModel;
    private transient I18NHelper i18n = new I18NHelper(this);

    private String liiteId;
    private String liitteenTyyppi;
    private String liitteenSanallinenKuvaus;
    private String toimitettavaMennessa;
    private String toimitusOsoite;
    private Button muokkaaBtn;

    public HakukohdeLiiteRow(HakukohdeLiiteViewModel param) {
        setHakukohdeLiiteViewModel(param);
        liiteId = param.getHakukohdeLiiteId();
         liitteenTyyppi = param.getLiitteeTyyppiKoodistoNimi();
         tryGetLocalizedSanallinenKuvaus(param);
         toimitettavaMennessa = param.getToimitusPvmTablePresentation();
         toimitusOsoite = getStringConcat();
         muokkaaBtn = UiUtil.buttonLink(null,i18n.getMessage("muokkaaBtn"), new Button.ClickListener() {
             @Override
             public void buttonClick(Button.ClickEvent clickEvent) {
                 tarjontaPresenter.showHakukohdeLiiteEditWindow(liiteId);
             }
         });

    }

    private void tryGetLocalizedSanallinenKuvaus(HakukohdeLiiteViewModel param) {
         for (KielikaannosViewModel teksti : param.getLiitteenSanallinenKuvaus()) {
             if (teksti.getKielikoodi().trim().contains("Suomi")) {
                 liitteenSanallinenKuvaus = teksti.getNimi();
             }
             if (teksti.getKielikoodi().trim().contains(I18N.getLocale().getLanguage())) {
                 liitteenSanallinenKuvaus = teksti.getNimi();
             }
             if (liitteenSanallinenKuvaus == null || liitteenSanallinenKuvaus.trim().length() < 1) {
                 liitteenSanallinenKuvaus = teksti.getNimi();
             }
         }
    }

    private String getStringConcat() {
        StringBuilder stringBuilder = new StringBuilder();
        if (hakukohdeLiiteViewModel != null) {
        stringBuilder.append(hakukohdeLiiteViewModel.getOsoiteRivi1());
        stringBuilder.append("\n");
        List<KoodiType> postinumeroKoodis = tarjontaUIHelper.gethKoodis(hakukohdeLiiteViewModel.getPostinumero());
        if (postinumeroKoodis != null) {
        stringBuilder.append(postinumeroKoodis.get(0).getKoodiArvo());
        }
        stringBuilder.append("\n");
        stringBuilder.append(tarjontaUIHelper.getKoodiNimi(hakukohdeLiiteViewModel.getPostinumero(), I18N.getLocale()));
        }
        return stringBuilder.toString();
    }

    public TarjontaPresenter getTarjontaPresenter() {
        return tarjontaPresenter;
    }

    public void setTarjontaPresenter(TarjontaPresenter tarjontaPresenter) {
        this.tarjontaPresenter = tarjontaPresenter;
    }

    public HakukohdeLiiteViewModel getHakukohdeLiiteViewModel() {
        return hakukohdeLiiteViewModel;
    }

    public void setHakukohdeLiiteViewModel(HakukohdeLiiteViewModel hakukohdeLiiteViewModel) {
        this.hakukohdeLiiteViewModel = hakukohdeLiiteViewModel;
    }

    public String getLiitteenTyyppi() {
        return liitteenTyyppi;
    }

    public void setLiitteenTyyppi(String liitteenTyyppi) {
        this.liitteenTyyppi = liitteenTyyppi;
    }

    public String getLiitteenSanallinenKuvaus() {
        return liitteenSanallinenKuvaus;
    }

    public void setLiitteenSanallinenKuvaus(String liitteenSanallinenKuvaus) {
        this.liitteenSanallinenKuvaus = liitteenSanallinenKuvaus;
    }

    public String getToimitettavaMennessa() {
        return toimitettavaMennessa;
    }

    public void setToimitettavaMennessa(String toimitettavaMennessa) {
        this.toimitettavaMennessa = toimitettavaMennessa;
    }

    public String getToimitusOsoite() {
        return toimitusOsoite;
    }

    public void setToimitusOsoite(String toimitusOsoite) {
        this.toimitusOsoite = toimitusOsoite;
    }

    public Button getMuokkaaBtn() {
        return muokkaaBtn;
    }

    public void setMuokkaaBtn(Button muokkaaBtn) {
        this.muokkaaBtn = muokkaaBtn;
    }

    public String getLiiteId() {
        return liiteId;
    }

    public void setLiiteId(String liiteId) {
        this.liiteId = liiteId;
    }
}
