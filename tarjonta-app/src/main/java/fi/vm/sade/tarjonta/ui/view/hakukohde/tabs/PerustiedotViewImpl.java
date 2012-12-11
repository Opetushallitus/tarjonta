
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

package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.WidgetFactory;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HaunNimi;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import java.util.ArrayList;
import java.util.List;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;
/**
 *
 * @author Tuomas Katva
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable
public class PerustiedotViewImpl extends CustomComponent implements PerustiedotView{

    private static final Logger LOG = LoggerFactory.getLogger(PerustiedotViewImpl.class);

    private TarjontaPresenter presenter;

    //MainLayout element
    VerticalLayout mainLayout;
    GridLayout itemContainer;

    //Fields
    @PropertyId("hakukohdeNimi")
    KoodistoComponent hakukohteenNimiCombo;
    @PropertyId("tunnisteKoodi")
    TextField tunnisteKoodiText;
    @PropertyId("hakuOid")
    ComboBox hakuCombo;
    @PropertyId("aloitusPaikat")
    TextField aloitusPaikatText;
    @PropertyId("hakukelpoisuusVaatimus")
    KoodistoComponent hakuKelpoisuusVaatimuksetCombo;
//    LanguageTabSheet valintaPerusteidenKuvausTabs;
    LanguageTabSheet lisatiedotTabs;
    Label serverMessage = new Label("");

    //Info buttons
    Button upRightInfoButton;
    Button downRightInfoButton;

    private Form form;
    private BeanItem<HakukohdeViewModel> hakukohdeBean;

    /*
     *
     * Init view with new model
     *
     */

    public PerustiedotViewImpl(TarjontaPresenter presenter) {
        super();
        buildMainLayout();
        this.presenter = presenter;

        this.presenter.initHakukohdeForm(this);

    }

    @Override
    public void setTunnisteKoodi(String tunnistekoodi) {
        tunnisteKoodiText.setValue(tunnistekoodi);
    }



    @Override
    public void commitForm(String tila) {
        form.commit();
        if (form.isValid()) {
        	presenter.getModel().getHakukohde().setHakukohdeKoodistoNimi(resolveHakukohdeKoodistoNimi() + " " + tila);
            presenter.saveHakuKohde(tila);
        }
    }


    @Override
    public void initForm(HakukohdeViewModel model) {
        hakukohdeBean = new BeanItem<HakukohdeViewModel>(model);
        form = new ValidatingViewBoundForm(this);
        form.setItemDataSource(hakukohdeBean);
        form.getFooter().addComponent(serverMessage);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
        this.form.setValidationVisible(false);
        this.form.setValidationVisibleOnCommit(false);
        hakukohteenNimiCombo.setImmediate(true);
        hakukohteenNimiCombo.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                 if (event.getProperty().getValue() instanceof String) {
                     presenter.setTunnisteKoodi(event.getProperty().getValue().toString());
                 } else {
                     //DEBUGSAWAY:LOG.debug("class" + event.getProperty().getValue().getClass().getName());
                 }
            }
        });
    }

    private String resolveHakukohdeKoodistoNimi() {
        // TODO tuomas korjaa :)

        String nimi = presenter.resolveHakukohdeKoodistonimiFields();
    	return nimi;
    }

    /*
     * Main layout building method.
     *
     */

    private void buildMainLayout() {
        mainLayout = new VerticalLayout();
        //Add top info button layout
        mainLayout.addComponent(buildInfoButtonLayout());

        //Build main item container
        mainLayout.addComponent(buildGrid());

        //Add bottom addtional info text areas and info button
        mainLayout.addComponent(buildBottomAreaLanguageTab());

        setCompositionRoot(mainLayout);
    }

    private GridLayout buildGrid() {
        itemContainer = new GridLayout(2, 1);
        itemContainer.setWidth(UiConstant.PCT100);
        itemContainer.setSpacing(true);
        itemContainer.setMargin(false, true, true, true);

        addItemToGrid("PerustiedotView.hakukohteenNimi", buildHakukode());
        addItemToGrid("PerustiedotView.hakuValinta", buildHakuCombo());

        addItemToGrid("PerustiedotView.aloitusPaikat", buildAloitusPaikat());
        addItemToGrid("PerustiedotView.hakukelpoisuusVaatimukset", buildHakukelpoisuusVaatimukset());


        itemContainer.setColumnExpandRatio(0, 0f);
        itemContainer.setColumnExpandRatio(1, 1f);

        return itemContainer;
    }

    private void addItemToGrid(String captionKey, AbstractComponent component) {

        if (itemContainer != null) {
            itemContainer.addComponent(UiUtil.label(null, T(captionKey)));
            itemContainer.addComponent(component);
            itemContainer.newLine();
        }

    }

    private KoodistoComponent buildHakukelpoisuusVaatimukset() {
        hakuKelpoisuusVaatimuksetCombo = UiBuilder.koodistoComboBox(null,KoodistoURIHelper.KOODISTO_HAKUKELPOISUUS_VAATIMUKSET_URI);


        return hakuKelpoisuusVaatimuksetCombo;
    }

    private TextField buildAloitusPaikat() {
        aloitusPaikatText = UiUtil.textField(null);

        return aloitusPaikatText;
    }

    private String tryGetHaunNimi(HakuViewModel haku) {
       if (I18N.getLocale().getLanguage().trim().equals("fi")) {
           return haku.getNimiFi();
       } else if (I18N.getLocale().getLanguage().trim().equals("se")) {
           return haku.getNimiSe();
       } else if (I18N.getLocale().getLanguage().trim().equals("en")) {
           return haku.getNimiEn();
       } else {
           return haku.getNimiFi();
       }
    }

    /*
     *
     * This method is called from presenter, it sets HakuTyyppis for the Haku-ComboBox
     *
     */

    @Override
    public void addItemsToHakuCombobox(List<HakuViewModel> haut) {
        BeanItemContainer<HakuViewModel> hakuContainer = new BeanItemContainer<HakuViewModel>(HakuViewModel.class);
        hakuContainer.addAll(haut);
        hakuCombo.setContainerDataSource(hakuContainer);
        hakuCombo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
    }

    @Override
    public void setSelectedHaku(HakuViewModel haku) {
        hakuCombo.setValue(haku);
    }



    private ComboBox buildHakuCombo() {
        hakuCombo = new ComboBox();



        return hakuCombo;
    }

    private KoodistoComponent buildHaku() {

        hakukohteenNimiCombo = UiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_HAKUKOHDE_URI);


        return hakukohteenNimiCombo;
    }





    /*
     *
     * Build hakukohteen nimi ComboBox and tunnistekoodi textfield
     *
     */


    private HorizontalLayout buildHakukode() {

        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        hakukohteenNimiCombo = buildHaku();

        hl.addComponent(hakukohteenNimiCombo);
        tunnisteKoodiText = UiUtil.textField(hl, "",  T("tunnistekoodi"), true);
        tunnisteKoodiText.setEnabled(false);

        hl.setComponentAlignment(hakukohteenNimiCombo, Alignment.TOP_RIGHT);
//        hl.setExpandRatio(tunnisteKoodiText, 5l);
        hl.setComponentAlignment(tunnisteKoodiText, Alignment.TOP_LEFT);
        return hl;
    }

    private HorizontalLayout buildInfoButtonLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_RIGHT_LEFT);
        upRightInfoButton = UiUtil.buttonSmallInfo(layout);
        layout.setComponentAlignment(upRightInfoButton, Alignment.TOP_RIGHT);
        return layout;
    }

    @Override
    public List<KielikaannosViewModel> getLisatiedot() {
        return this.lisatiedotTabs.getKieliKaannokset();
    }

    private VerticalLayout buildBottomAreaLanguageTab() {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label label = UiUtil.label(hl, T("PerustiedotView.lisatiedot"), LabelStyleEnum.H2);
        downRightInfoButton = UiUtil.buttonSmallInfo(hl);
        hl.setExpandRatio(label, 1l);
        hl.setExpandRatio(downRightInfoButton, 3l);
        hl.setComponentAlignment(label, Alignment.TOP_LEFT);
        hl.setComponentAlignment(downRightInfoButton, Alignment.TOP_RIGHT);
        vl.addComponent(hl);
        lisatiedotTabs = buildLanguageTab();
        vl.addComponent(lisatiedotTabs);
        return vl;
    }

    private LanguageTabSheet buildLanguageTab(List<KielikaannosViewModel> arvot) {
        return new LanguageTabSheet();
    }

    private LanguageTabSheet buildLanguageTab() {
        return new LanguageTabSheet();
    }



     private String T(String key) {
         return I18N.getMessage(key);
     }

}