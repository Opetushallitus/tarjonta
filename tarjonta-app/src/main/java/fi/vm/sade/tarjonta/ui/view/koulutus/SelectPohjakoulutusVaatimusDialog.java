package fi.vm.sade.tarjonta.ui.view.koulutus;/*
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

import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author: Tuomas Katva
 * Date: 10.5.2013
 */
@Configurable(preConstruction =  true)
public class SelectPohjakoulutusVaatimusDialog extends Window {

    @Autowired(required = true)
    protected transient UiBuilder uiBuilder;

    @Autowired(required = true)
    protected TarjontaPresenter presenter;

    protected transient I18NHelper _i18n;

    protected ErrorMessage errorView;

    private KoodistoComponent kcPohjakoulutusvaatimus;

    protected Button peruutaBtn;
    protected Button jatkaBtn;


    public SelectPohjakoulutusVaatimusDialog(String width,String height) {
        super();


        _i18n = new I18NHelper(this);
        setWidth(width);
        setHeight(height);
        setContent(buildMainLayout());
        setCaption(_i18n.getMessage("valitsePohjakoulutus"));
        setModal(true);

    }

    private VerticalLayout buildMainLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();
        mainLayout.addComponent(buildPkVaatimusLayout());
        mainLayout.addComponent(buildBtnLayout());

        return mainLayout;
    }

    private VerticalLayout buildPkVaatimusLayout() {
        VerticalLayout layout = new VerticalLayout();

        kcPohjakoulutusvaatimus = buildKoodistoCombobox(KoodistoURIHelper.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI);
        layout.addComponent(kcPohjakoulutusvaatimus);
        layout.setComponentAlignment(kcPohjakoulutusvaatimus, Alignment.MIDDLE_CENTER);

        return layout;
    }

    private HorizontalLayout buildBtnLayout() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        peruutaBtn = UiUtil.button(null, _i18n.getMessage("peruutaBtn"), null);
        peruutaBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getParent().getWindow().removeWindow(SelectPohjakoulutusVaatimusDialog.this);
            }
        });

        hl.addComponent(peruutaBtn);
        hl.setComponentAlignment(peruutaBtn,Alignment.MIDDLE_LEFT);

        jatkaBtn = UiUtil.button(null, _i18n.getMessage("jatkaBtn"),null);
        jatkaBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String pkVaatimus = (String)kcPohjakoulutusvaatimus.getValue();
                if (pkVaatimus != null && pkVaatimus.trim().length() > 0) {
                presenter.showLisaaRinnakkainenToteutusEditView(presenter.getModel().getKoulutusPerustiedotModel().getOid(),pkVaatimus);
                getParent().getWindow().removeWindow(SelectPohjakoulutusVaatimusDialog.this);
                }
            }
        });
        hl.addComponent(jatkaBtn);
        hl.setComponentAlignment(jatkaBtn,Alignment.MIDDLE_RIGHT);

        return hl;
    }

    private KoodistoComponent buildKoodistoCombobox(String koodistoUri) {
        return uiBuilder.koodistoComboBox(null, koodistoUri, null);//KoodistoURIHelper.KOODISTO_TARJONTA_KOULUTUSASTE,null);
    }


}
