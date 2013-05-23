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
package fi.vm.sade.tarjonta.ui.view.haku;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.presenter.HakuPresenter;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 * And editor for "Haku" object.
 *
 * @author mlyly
 * @author mholi
 * @see HakuViewModel the model that is bound this edit form, see the PropertyId
 * annotations.
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class EditHakuFormImpl extends VerticalLayout implements EditHakuForm {

    private static final Logger LOG = LoggerFactory.getLogger(EditHakuFormImpl.class);
    public static final Object[] HAKUAJAT_COLUMNS = new Object[]{"kuvaus", "alkuPvm", "loppuPvm", "poistaB"};
    private static final long serialVersionUID = -8149045959215514422L;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    @Autowired(required = true)
    private HakuPresenter presenter;
    private VerticalLayout _layout;
    @NotNull(message = "{validation.Haku.hakutyyppiNull}")
    @PropertyId("hakutyyppi")
    private KoodistoComponent _hakutyyppi;
    @NotNull(message = "{validation.Haku.hakukausiNull}")
    @PropertyId("hakukausi")
    private KoodistoComponent _hakukausi;
    @NotNull(message = "{validation.Haku.hakuvuosiNull}")
    @Min(value = 2000, message = "{validation.Haku.hakuvuosi}")
    @PropertyId("hakuvuosi")
    private TextField _hakuvuosi;
    @NotNull(message = "{validation.Haku.koulutuksenAlkamisKausiNull}")
    @PropertyId("koulutuksenAlkamisKausi")
    private KoodistoComponent _koulutusAlkamiskausi;
    @NotNull(message = "{validation.Haku.koulutuksenAlkamisVuosiNull}")
    @Min(value = 2000, message = "{validation.Haku.koulutuksenAlkamisVuosiNumber}")
    @PropertyId("koulutuksenAlkamisvuosi")
    private TextField koulutuksenAlkamisvuosi;
    @NotNull(message = "{validation.Haku.kohdejoukkoNull}")
    @PropertyId("haunKohdejoukko")
    private KoodistoComponent _hakuKohdejoukko;
    @NotNull(message = "{validation.Haku.hakutapaNull}")
    @PropertyId("hakutapa")
    private KoodistoComponent _hakutapa;
    @PropertyId("nimiFi")
    private TextField _haunNimiFI;
    @PropertyId("nimiSe")
    private TextField _haunNimiSE;
    @PropertyId("nimiEn")
    private TextField _haunNimiEN;
    @PropertyId("haunTunniste")
    private Label _haunTunniste;
    // TODO hakuaika
    /*@PropertyId("alkamisPvm")
     private DateField hakuAlkaa;
     @PropertyId("paattymisPvm")
     private DateField hakuLoppuu;

     private OptionGroup sisHakuajat;*/
    private Table sisaisetHakuajatTable;
    private HakuajatContainer sisaisetHakuajatContainer;
    @PropertyId("haussaKaytetaanSijoittelua")
    private CheckBox _kaytetaanSijoittelua;
    @PropertyId("kaytetaanJarjestelmanHakulomaketta")
    private CheckBox _kayteaanJarjestelmanHakulomaketta;
    @Pattern(regexp = "[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", message = "{validation.invalid.www}")
    @PropertyId("hakuLomakeUrl")
    private TextField _muuHakulomakeUrl;
    private Button lisaaHakuaika;
    private Form form;
    private transient I18NHelper _i18n;
    private boolean attached = false;

    public EditHakuFormImpl() {
        super();
        HakuViewModel haku = new HakuViewModel();
        initialize(haku);
    }

    @Override
    public void initialize(HakuViewModel hakuViewModel) {
        LOG.info("inititialize()");

        // Clean up old components if any
        if (_layout != null) {
            _layout.removeAllComponents();
        }

        // Create root layout for this component
        _layout = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        addComponent(_layout);
        

        //
        // Init fields
        //

        _hakutyyppi = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_HAKUTYYPPI_URI, null, null, T("Hakutyyppi.prompt"));
        _hakutyyppi.setSizeUndefined();
        _hakukausi = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_ALKAMISKAUSI_URI, null, null, T("Hakukausi.prompt"));
        _hakukausi.setSizeUndefined();
        _hakuvuosi = UiUtil.textField(null, "", T("Hakuvuosi.prompt"), false);
        _hakuvuosi.setSizeUndefined();
        _koulutusAlkamiskausi = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_ALKAMISKAUSI_URI, null, null, T("KoulutuksenAlkamiskausi.prompt"));
        _koulutusAlkamiskausi.setSizeUndefined();
        koulutuksenAlkamisvuosi = UiUtil.textField(null, "", T("KoulutuksenAlkamisvuosi.prompt"), false);
        koulutuksenAlkamisvuosi.setSizeUndefined();
        _hakuKohdejoukko = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_HAUN_KOHDEJOUKKO_URI, null, null, T("HakuKohdejoukko.prompt"));
        _hakuKohdejoukko.setWidth("350px");//_hakuKohdejoukko.setSizeUndefined();
        _hakutapa = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_HAKUTAPA_URI, null, null, T("Hakutapa.prompt"));
        _hakutapa.setWidth("350px");//.setSizeUndefined();
        _haunNimiFI = UiUtil.textField(null, "", T("HaunNimiFI.prompt"), false);
        _haunNimiFI.setWidth("450px");
        _haunNimiSE = UiUtil.textField(null, "", T("HaunNimiSE.prompt"), false);
        _haunNimiSE.setWidth("450px");
        _haunNimiEN = UiUtil.textField(null, "", T("HaunNimiEN.prompt"), false);
        _haunNimiEN.setWidth("450px");
        _haunTunniste = UiUtil.label((AbstractLayout) null, hakuViewModel.getHaunTunniste());
        _haunTunniste.setSizeUndefined();
        // TODO hakuaika
        _kaytetaanSijoittelua = UiUtil.checkbox(null, T("KaytetaanSijoittelua"));
        _kaytetaanSijoittelua.setSizeUndefined();
        _kayteaanJarjestelmanHakulomaketta = UiUtil.checkbox(null, T("KaytetaanJarjestemanHakulomaketta"));
        _kayteaanJarjestelmanHakulomaketta.setSizeUndefined();
        _kayteaanJarjestelmanHakulomaketta.setImmediate(true);
        _kayteaanJarjestelmanHakulomaketta.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                //DEBUGSAWAY:LOG.debug("Value change for kaytetaan jarjestelman hakulomaketta.");
                _muuHakulomakeUrl.setEnabled(!_kayteaanJarjestelmanHakulomaketta.booleanValue());
            }
        });
        _muuHakulomakeUrl = UiUtil.textField(null, "", T("MuuHakulomake.prompt"), false);
        _muuHakulomakeUrl.setSizeUndefined();

        GridLayout grid = new GridLayout(3, 1);
        grid.setSpacing(true);
        grid.setSizeUndefined();
        _layout.addComponent(grid);

        grid.addComponent(UiUtil.label(null, T("Hakutyyppi")));
        grid.addComponent(_hakutyyppi);
        grid.newLine();

        {
            grid.addComponent(UiUtil.label(null, T("HakukausiJaVuosi")));
            HorizontalLayout hl = UiUtil.horizontalLayout();
            hl.setSizeUndefined();
            hl.setSpacing(true);
            hl.addComponent(_hakukausi);
            hl.addComponent(_hakuvuosi);
            grid.addComponent(hl);
            grid.newLine();
        }

        {
            grid.addComponent(UiUtil.label(null, T("KoulutuksenAlkamiskausi")));
            HorizontalLayout hl = UiUtil.horizontalLayout();
            hl.setSizeUndefined();
            hl.setSpacing(true);
            hl.addComponent(_koulutusAlkamiskausi);
            hl.addComponent(koulutuksenAlkamisvuosi);
            grid.addComponent(hl);
            grid.newLine();
        }

        grid.addComponent(UiUtil.label(null, T("HakuKohdejoukko")));
        grid.addComponent(_hakuKohdejoukko);
        grid.newLine();

        grid.addComponent(UiUtil.label(null, T("Hakutapa")));
        grid.addComponent(_hakutapa);
        grid.newLine();

        {
            grid.addComponent(UiUtil.label(null, T("HaunNimi")));
            VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.NONE);
            vl.setSizeUndefined();

            vl.addComponent(_haunNimiFI);
            vl.addComponent(_haunNimiSE);
            vl.addComponent(_haunNimiEN);
            grid.addComponent(vl);
            grid.newLine();
        }

        grid.addComponent(UiUtil.label(null, T("HaunTunniste")));
        grid.addComponent(_haunTunniste);
        grid.newLine();

        {
            grid.addComponent(UiUtil.label(null, T("Hakuaika")));

            VerticalLayout vl = UiUtil.verticalLayout();
            vl.setSizeUndefined();
            //vl.setWidth(850, Sizeable.UNITS_PIXELS);


            this.sisaisetHakuajatTable = new Table();
            this.sisaisetHakuajatTable.setEditable(true);

            lisaaHakuaika = UiUtil.buttonSmallPlus(vl, T("LisaaHakuaika"), new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    getSisaisetHakuajatContainer().addRowToHakuajat();
                }
            });
            lisaaHakuaika.setEnabled(presenter.getPermission().userCanEditHaku());
            vl.addComponent(sisaisetHakuajatTable);

            grid.addComponent(vl);
            grid.newLine();
        }

        grid.space();
        grid.addComponent(_kaytetaanSijoittelua);
        grid.newLine();

        {
            grid.addComponent(UiUtil.label(null, T("Hakulomake")));
            VerticalLayout vl = UiUtil.verticalLayout();
            vl.setSizeUndefined();
            vl.setSpacing(true);
            vl.addComponent(_kayteaanJarjestelmanHakulomaketta);
            vl.addComponent(_muuHakulomakeUrl);
            grid.addComponent(vl);
            grid.newLine();
        }

        grid.setColumnExpandRatio(1, 1);
        grid.setColumnExpandRatio(2, 5);

        if (hakuViewModel.getHakuOid() == null) {
            hakuViewModel.setHakuvuosi(Calendar.getInstance().get(Calendar.YEAR));
            hakuViewModel.setKoulutuksenAlkamisvuosi(Calendar.getInstance().get(Calendar.YEAR));
        }
       
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    
        this.sisaisetHakuajatContainer = new HakuajatContainer(presenter.getHakuModel().getSisaisetHakuajat());
        this.sisaisetHakuajatTable.setContainerDataSource(this.getSisaisetHakuajatContainer());
        this.sisaisetHakuajatTable.setVisibleColumns(HAKUAJAT_COLUMNS);
        this.sisaisetHakuajatTable.setPageLength((this.getSisaisetHakuajatContainer().size() > 5) ? this.getSisaisetHakuajatContainer().size() : 5);
        this.sisaisetHakuajatTable.setColumnHeaders(new String[]{T("Kuvaus"), T("Alkupvm"), T("Loppupvm"), T("Poista")});
        /*this.sisaisetHakuajatTable.setColumnWidth("kuvaus", 180);
         this.sisaisetHakuajatTable.setColumnWidth("alkuPvm", 250);
         this.sisaisetHakuajatTable.setColumnWidth("loppuPvm", 250);
         this.sisaisetHakuajatTable.setColumnWidth("poistaB", 160);*/

    }

    public List<String> checkNimi() {
        List<String> errorMessages = new ArrayList<String>();
        if (fieldEmpty(_haunNimiFI) 
                && fieldEmpty(_haunNimiSE)
                && fieldEmpty(_haunNimiEN)) {
            errorMessages.add(T("validation.nimiNull"));
        }
        return errorMessages;
    }
    
    public boolean fieldEmpty(TextField textField) {
        return (textField == null)
                || (textField.getValue() == null)
                || (((String)textField.getValue()).isEmpty());
    }

    /**
     * Translator helper. Makes code so much more hip... and shorter.
     *
     * Its using I18NHelper so the actual translation key will be deducted like
     * this:
     *
     * T("Jatka") becomes translation value for "EditHakuViewImpl.Jatka".
     *
     * @param key
     * @return
     */
    private String T(String key) {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n.getMessage(key);
    }

    /**
     * @return the sisaisetHakuajatContainer
     */
    public HakuajatContainer getSisaisetHakuajatContainer() {
        return sisaisetHakuajatContainer;
    }

    public class HakuajatContainer extends BeanItemContainer<HakuajatView> implements Serializable {

        public HakuajatContainer(List<HakuaikaViewModel> hakuajat) {
            super(HakuajatView.class);

            initHakuaikaContainer(hakuajat);
        }

        public List<String> bindHakuajat() {
            List<String> errorMessages = new ArrayList<String>();
            List<HakuaikaViewModel> hakuajat = new ArrayList<HakuaikaViewModel>();
            for (HakuajatView curRow : this.getItemIds()) {
                if (curRow.getLoppuPvm().getValue() == null 
                        || curRow.getAlkuPvm().getValue() == null) {
                    errorMessages.add(_i18n.getMessage("HakuaikaVirhe"));
                } else if (curRow.getModel().getAlkamisPvm().after(curRow.getModel().getPaattymisPvm())) {
                    errorMessages.add(_i18n.getMessage("HakuaikaVirheJarjestys"));
                } else {
                    hakuajat.add(curRow.getModel());
                }
            }
            
            if (errorMessages.isEmpty() && hakuajat.isEmpty()) {
                errorMessages.add(_i18n.getMessage("hakuajatEmpty"));
            }
            presenter.getHakuModel().setSisaisetHakuajat(hakuajat);

            return errorMessages;
        }

        public void addRowToHakuajat() {
            final HakuajatView hakuaikaRow = new HakuajatView(new HakuaikaViewModel());
            hakuaikaRow.getPoistaB().addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    removeItem(hakuaikaRow);
                }
            });
            addItem(hakuaikaRow);
            hakuaikaRow.getPoistaB().setVisible(presenter.getPermission().userCanEditHaku());
        }

        private void initHakuaikaContainer(List<HakuaikaViewModel> hakuajat) {
            if (hakuajat == null || hakuajat.size() == 0) {
                addRowToHakuajat();
            }

            for (HakuaikaViewModel curHakuaika : hakuajat) {
                final HakuajatView hakuaikaRow = new HakuajatView(curHakuaika);
                hakuaikaRow.getPoistaB().addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        removeItem(hakuaikaRow);
                    }
                });
                hakuaikaRow.getPoistaB().setVisible(presenter.getPermission().userCanEditHaku());
                addItem(hakuaikaRow);
            }
        }
    }
}
