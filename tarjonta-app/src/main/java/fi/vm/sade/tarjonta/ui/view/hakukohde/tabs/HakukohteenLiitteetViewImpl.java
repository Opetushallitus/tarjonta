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

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import javax.validation.constraints.NotNull;

/**
 * Created by: Tuomas Katva
 * Date: 14.1.2013
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable
public class HakukohteenLiitteetViewImpl extends CustomComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PerustiedotViewImpl.class);
    private TarjontaPresenter presenter;

    private VerticalLayout mainLayout;
    private GridLayout itemContainer;
    @PropertyId("liitteenTyyppi")
    @NotNull(message = "validation.HakukohdeLiitteet.liitteenTyyppi.notNull")
    private ComboBox liitteenTyyppi;
    @PropertyId("liitteenSanallinenKuvaus")
    private TextArea liitteenSanallinenKuvausTxtArea;
    @PropertyId("toimitettavaMennessa")
    private DateField toimittettavaMennessa;

    @PropertyId("osoiteRivi1")
    private TextField osoiteRivi1;
    @PropertyId("osoiteRivi2")
    private TextField osoiteRivi2;
    @PropertyId("postinumero")
    private TextField postinumero;
    @PropertyId("postitoimiPaikka")
    private TextField postitoimipaikka;

    private CheckBox voidaanToimittaaSahkoisesti;
    @PropertyId("sahkoinenToimitusOsoite")
    private TextField sahkoinenToimitusOsoite;

    private UiBuilder uiBuilder;
    private ErrorMessage errorView;
    private Form form;
    private BeanItem<HakukohdeLiiteViewModel> hakukohdeLiiteBean;

    Button upRightInfoButton;

    public HakukohteenLiitteetViewImpl(TarjontaPresenter presenter, UiBuilder uiBuilder) {
        super();
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        buildMainLayout();
    }

    private String T(String key) {
        return I18N.getMessage(key);
    }

    private void buildMainLayout() {
         mainLayout = new VerticalLayout();

         mainLayout.addComponent(buildInfoButtonLayout());

         mainLayout.addComponent(buildGridLayout());

         setCompositionRoot(mainLayout);
    }


    public void initForm(HakukohdeLiiteViewModel hakukohdeLiite) {
        hakukohdeLiiteBean = new BeanItem<HakukohdeLiiteViewModel>(hakukohdeLiite);
        form = new ValidatingViewBoundForm(this);
        form.setItemDataSource(hakukohdeLiiteBean);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        this.form.setValidationVisible(false);
        this.form.setValidationVisibleOnCommit(false);
    }

    private HorizontalLayout buildInfoButtonLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT_LEFT);
        upRightInfoButton = UiUtil.buttonSmallInfo(layout);
        layout.setComponentAlignment(upRightInfoButton, Alignment.TOP_RIGHT);
        return layout;
    }

    private void addItemToGrid(String captionKey, AbstractComponent component) {

        if (itemContainer != null) {
            itemContainer.addComponent(UiUtil.label(null, T(captionKey)));
            itemContainer.addComponent(component);
            itemContainer.newLine();
        }

    }

    private ComboBox buildLiitteenTyyppiCombo() {
        liitteenTyyppi = new ComboBox();

        return liitteenTyyppi;
    }

    private TextArea buildLiitteenSanallinenKuvaus() {
        liitteenSanallinenKuvausTxtArea = new TextArea();
        liitteenSanallinenKuvausTxtArea.setWidth("60%");
        return liitteenSanallinenKuvausTxtArea;
    }

    private DateField buildToimitettavaMennessa() {
        toimittettavaMennessa = new DateField();
        toimittettavaMennessa.setResolution(DateField.RESOLUTION_MIN);
        return toimittettavaMennessa;
    }

    private GridLayout buildLiitteidenToimitusOsoite() {
        GridLayout osoiteLayout = new GridLayout(2,3);


        osoiteRivi1 =  UiUtil.textField(null);
        osoiteRivi1.setWidth("100%");
        osoiteRivi1.setInputPrompt(I18N.getMessage("PerustiedotView.osoiteRivi1"));
        osoiteLayout.addComponent(osoiteRivi1,0,0,1,0);

        osoiteRivi2 = UiUtil.textField(null);
        osoiteRivi2.setWidth("100%");
        osoiteRivi2.setInputPrompt(I18N.getMessage("PerustiedotView.osoiteRivi2"));
        osoiteLayout.addComponent(osoiteRivi2,0,1,1,1);

        postinumero = UiUtil.textField(null);
        postinumero.setInputPrompt(I18N.getMessage("PerustiedotView.postinumero"));
        osoiteLayout.addComponent(postinumero,0,2);
        postinumero.setSizeUndefined();

        postitoimipaikka = UiUtil.textField(null);
        postitoimipaikka.setInputPrompt(I18N.getMessage("PerustiedotView.postitoimipaikka"));
        osoiteLayout.addComponent(postitoimipaikka,1,2);
        postitoimipaikka.setSizeUndefined();

        osoiteLayout.setColumnExpandRatio(0,2);
        osoiteLayout.setColumnExpandRatio(1,4);

        return osoiteLayout;
    }


    private HorizontalLayout buildErrorLayout() {
        HorizontalLayout topErrorArea = UiUtil.horizontalLayout();
        HorizontalLayout padding = UiUtil.horizontalLayout();
        padding.setWidth(30, UNITS_PERCENTAGE);
        errorView = new ErrorMessage();
        errorView.setSizeUndefined();

        topErrorArea.addComponent(padding);
        topErrorArea.addComponent(errorView);

        return topErrorArea;
    }

    private VerticalLayout buildSahkoinenToimitusOsoite() {
        VerticalLayout sahkoinenToimitusOsoiteLayout = new VerticalLayout();

        voidaanToimittaaSahkoisesti = new CheckBox();
        voidaanToimittaaSahkoisesti.setCaption(T("HakukohteenLiitteetViewImpl.voidaanToimittaaMyosSahkoisesti"));
        sahkoinenToimitusOsoiteLayout.addComponent(voidaanToimittaaSahkoisesti);
        sahkoinenToimitusOsoite = new TextField();
        sahkoinenToimitusOsoiteLayout.addComponent(sahkoinenToimitusOsoite);

        return sahkoinenToimitusOsoiteLayout;
    }

    private GridLayout buildGridLayout() {
        itemContainer =  new GridLayout(2,1);
        itemContainer.setWidth(UiConstant.PCT100);
        itemContainer.setSpacing(true);
        itemContainer.setMargin(false, true, true, true);

        addItemToGrid("",buildErrorLayout());
        addItemToGrid("HakukohteenLiitteetViewImpl.liitteenTyyppi",buildLiitteenTyyppiCombo());
        addItemToGrid("HakukohteenLiitteetViewImpl.liitteenSanallinenKuvaus",buildLiitteenSanallinenKuvaus());
        addItemToGrid("HakukohteenLiitteetViewImpl.toimitettavaMennessa",buildToimitettavaMennessa());
        addItemToGrid("HakukohteenLiitteetViewImpl.toimitusOsoite",buildLiitteidenToimitusOsoite());
        addItemToGrid("",buildSahkoinenToimitusOsoite());

        itemContainer.setColumnExpandRatio(0, 0f);
        itemContainer.setColumnExpandRatio(1, 1f);
        return itemContainer;
    }

}
