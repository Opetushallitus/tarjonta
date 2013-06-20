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


import com.vaadin.data.Property;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import javax.validation.constraints.NotNull;


/**
 * Created by: Tuomas Katva
 * Date: 25.1.2013
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class HakukohdeValintaKoeAikaEditView extends CustomComponent {


    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;

    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    @NotNull(message = "{validation.HakukohdeValintaKoeAikaEditView.osoiteNotNull}")
    @PropertyId("osoiteRivi")
    private TextField osoiteRiviTxt;

    @NotNull(message = "{validation.HakukohdeValintaKoeAikaEditView.postinumeroNotNull}")
    @PropertyId("postinumero")
    private KoodistoComponent postinumeroCombo;
    @NotNull(message = "{validation.HakukohdeValintaKoeAikaEditView.postitoimipaikkaNotNull}")
    @PropertyId("postitoimiPaikka")
    private TextField postitoimiPaikka;
    @NotNull(message = "{validation.HakukohdeValintaKoeAikaEditView.alkamisAikaNotNull}")
    @PropertyId("alkamisAika")
    private DateField alkupvm;
    @NotNull (message = "{validation.HakukohdeValintaKoeAikaEditView.paattymisAikaNotNull}")
    @PropertyId("paattymisAika")
    private DateField loppuPvm;

    @PropertyId("valintakoeAikaTiedot")
    private TextField lisatietoja;

    private Button lisaaBtn;

    private VerticalLayout mainLayout;

    public HakukohdeValintaKoeAikaEditView() {
         buildLayout();

    }

    private void buildLayout() {
        mainLayout = new VerticalLayout();

        mainLayout.addComponent(buildOsoiteEditLayout());
        //mainLayout.addComponent(buildValintakoeAikaLayout());




        setCompositionRoot(mainLayout);
    }

    private HorizontalLayout buildLisatietoLayout() {
        HorizontalLayout lisaTietoLayout = new HorizontalLayout();

        lisatietoja = UiUtil.textField(null);
        lisatietoja.setWidth("482px");
        lisatietoja.setInputPrompt(T("HakukohdeValintakoeViewImpl.lisatietojaValintakokeesta"));
        lisaTietoLayout.addComponent(lisatietoja);

        lisaaBtn = UiBuilder.button(null,T("HakukohdeValintakoeViewImpl.lisaaBtn"),null);
        lisaTietoLayout.addComponent(lisaaBtn);
        //lisaTietoLayout.setExpandRatio(lisatietoja,1.0f);
        return lisaTietoLayout;
    }

    private HorizontalLayout buildOsoiteEditLayout() {
        VerticalLayout vl = new VerticalLayout();
        HorizontalLayout osoiteAddLayout = new HorizontalLayout();

        osoiteRiviTxt = UiUtil.textField(null);
        osoiteRiviTxt.setInputPrompt(T("HakukohdeValintakoeViewImpl.osoiteRivi"));
        osoiteAddLayout.addComponent(osoiteRiviTxt);

        postinumeroCombo = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_POSTINUMERO_URI);
        osoiteAddLayout.addComponent(postinumeroCombo);

        postitoimiPaikka = UiUtil.textField(null);
        postitoimiPaikka.setInputPrompt(T("HakukohdeValintakoeViewImpl.postitoimipaikka"));
        osoiteAddLayout.addComponent(postitoimiPaikka);

        postinumeroCombo.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType koodi = (KoodiType) dto;
                    return koodi.getKoodiUri();
                } else {
                    return dto;
                }
            }
        });

        postinumeroCombo.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                if(dto instanceof KoodiType) {
                    KoodiType koodi = (KoodiType)dto;
                    return  koodi.getKoodiArvo();
                } else {
                    return dto.toString();
                }
            }
        });

        postinumeroCombo.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (tarjontaUIHelper != null) {
                    String koodiUri = (String) valueChangeEvent.getProperty().getValue();
                    if (koodiUri != null) {
                    String postitoimipaikkaStr = tarjontaUIHelper.getKoodiNimi(koodiUri, I18N.getLocale());
                    postitoimiPaikka.setValue(postitoimipaikkaStr);
                    }
                }
            }
        });

        postinumeroCombo.setImmediate(true);

        return osoiteAddLayout;
    }

    public void addClickListenerToLisaaButton(Button.ClickListener clickListener) {
        if (lisaaBtn != null) {
            lisaaBtn.addListener(clickListener);
        }
    }

    public VerticalLayout buildValintakoeAikaLayout() {
        VerticalLayout vl = new VerticalLayout();
        HorizontalLayout valintakoeAikaLayout = new HorizontalLayout();

        alkupvm = new DateField();
        alkupvm.setResolution(DateField.RESOLUTION_MIN);
        valintakoeAikaLayout.addComponent(alkupvm);

        Label hyphen = new Label();
        hyphen.setValue(" - ");
        valintakoeAikaLayout.addComponent(hyphen);

        loppuPvm = new DateField();
        loppuPvm.setResolution(DateField.RESOLUTION_MIN);
        valintakoeAikaLayout.addComponent(loppuPvm);
        vl.addComponent(valintakoeAikaLayout);
        vl.addComponent(buildLisatietoLayout());

        return vl;
    }
    
    public void clearData() {
        osoiteRiviTxt.setValue(null);
        postinumeroCombo.setValue(null);
        postitoimiPaikka.setValue(null);
        alkupvm.setValue(null);
        loppuPvm.setValue(null);
    }

    private String T(String key) {
        return I18N.getMessage(key);
    }

    public TextField getOsoiteRiviTxt() {
        return osoiteRiviTxt;
    }

    public void setOsoiteRiviTxt(TextField osoiteRiviTxt) {
        this.osoiteRiviTxt = osoiteRiviTxt;
    }

    public KoodistoComponent getPostinumeroCombo() {
        return postinumeroCombo;
    }

    public void setPostinumeroCombo(KoodistoComponent postinumeroCombo) {
        this.postinumeroCombo = postinumeroCombo;
    }

    public TextField getPostitoimiPaikka() {
        return postitoimiPaikka;
    }

    public void setPostitoimiPaikka(TextField postitoimiPaikka) {
        this.postitoimiPaikka = postitoimiPaikka;
    }

    public DateField getAlkupvm() {
        return alkupvm;
    }

    public void setAlkupvm(DateField alkupvm) {
        this.alkupvm = alkupvm;
    }

    public DateField getLoppuPvm() {
        return loppuPvm;
    }

    public void setLoppuPvm(DateField loppuPvm) {
        this.loppuPvm = loppuPvm;
    }

    public TextField getLisatietoja() {
        return lisatietoja;
    }

    public void setLisatietoja(TextField lisatietoja) {
        this.lisatietoja = lisatietoja;
    }
}
