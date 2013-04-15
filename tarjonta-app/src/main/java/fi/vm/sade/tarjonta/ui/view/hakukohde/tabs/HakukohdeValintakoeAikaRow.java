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
import com.vaadin.ui.Label;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by: Tuomas Katva Date: 24.1.2013
 */
@Configurable(preConstruction = true)
public class HakukohdeValintakoeAikaRow extends HorizontalLayout{

    private static final String SIJAINTI_FORMAT = "%s<br/>%s %s";
    private final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;
    private transient I18NHelper i18n = new I18NHelper(this);
    private Label sijainti;
    private String ajankohta;
    private String lisatietoja;
    private Button poistaBtn;
    private Button muokkaaBtn;
    private ValintakoeAikaViewModel rowValintakoeAika;
    private HakukohdeValintakoeViewImpl parent;

    public  HakukohdeValintakoeAikaRow(ValintakoeAikaViewModel aika) {
        setHeight("80px");
        rowValintakoeAika = aika;
        resolveFields();
        poistaBtn = UiUtil.buttonLink(null, i18n.getMessage("poistaBtn"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                  tarjontaPresenter.removeValintakoeAikaSelection(rowValintakoeAika);
                  if (parent != null) {
                      parent.loadTableData();
                  }
            }
        });
  
        muokkaaBtn = UiUtil.buttonLink(null,i18n.getMessage("muokkaaBtn") , new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                tarjontaPresenter.getModel().setSelectedValintakoeAika(rowValintakoeAika);
                parent.setEditableValintakoeAika(rowValintakoeAika);

            }
        });
    }

    private void resolveFields() {
        if (rowValintakoeAika != null) {
            if (rowValintakoeAika.getPostinumero() != null) {
                List<KoodiType> postinumeroKoodis = tarjontaUIHelper.getKoodis(rowValintakoeAika.getPostinumero());
                sijainti = new Label(String.format(SIJAINTI_FORMAT, new Object[]{rowValintakoeAika.getOsoiteRivi(), postinumeroKoodis.get(0).getKoodiArvo(), rowValintakoeAika.getPostitoimiPaikka()}), Label.CONTENT_XHTML);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            ajankohta = simpleDateFormat.format(rowValintakoeAika.getAlkamisAika()) + " - " + simpleDateFormat.format(rowValintakoeAika.getPaattymisAika());


            lisatietoja = rowValintakoeAika.getValintakoeAikaTiedot();
        }
    }

    public void setParent(HakukohdeValintakoeViewImpl param) {
        parent = param;
    }

    public Label getSijainti() {
        return sijainti;
    }

    public void setSijainti(Label sijainti) {
        this.sijainti = sijainti;
    }

    public String getAjankohta() {
        return ajankohta;
    }

    public void setAjankohta(String ajankohta) {
        this.ajankohta = ajankohta;
    }

    public String getLisatietoja() {
        return lisatietoja;
    }

    public void setLisatietoja(String lisatietoja) {
        this.lisatietoja = lisatietoja;
    }

    public Button getPoistaBtn() {
        return poistaBtn;
    }

    public void setPoistaBtn(Button poistaBtn) {
        this.poistaBtn = poistaBtn;
    }

    public Button getMuokkaaBtn() {
        return muokkaaBtn;
    }

    public void setMuokkaaBtn(Button muokkaaBtn) {
        this.muokkaaBtn = muokkaaBtn;
    }
}
