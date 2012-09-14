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
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.vaadin.oph.enums.LabelStyle;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 *
 * @author mlyly
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = false)
public class EditorHakuView extends CustomComponent {

    private static final Logger LOG = LoggerFactory.getLogger(EditorHakuView.class);

    private VerticalLayout _layout;

    @PropertyId("hakutyyppi")
    private KoodistoComponent _hakutyyppi;
    @PropertyId("hakukausi")
    private KoodistoComponent _hakukausi;
    @PropertyId("hakuvuosi")
    private TextField _hakuvuosi;
    @PropertyId("koulutuksenAlkamiskausi")
    private KoodistoComponent _koulutusAlkamiskausi;
    @PropertyId("kohdejoukko")
    private KoodistoComponent _hakuKohdejoukko;
    @PropertyId("hakutapa")
    private KoodistoComponent _hakutapa;

    @PropertyId("haunNimiFI")
    private TextField _haunNimiFI;
    @PropertyId("haunNimiFI")
    private TextField _haunNimiSE;
    @PropertyId("haunNimiFI")
    private TextField _haunNimiEN;

    @PropertyId("haunTunniste")
    private Label _haunTunniste;

    // TODO hakuaika

    @PropertyId("kaytetaanSijoittelua")
    private CheckBox _kaytetaanSijoittelua;

    @PropertyId("kaytetaanJarjestelmanHakulomaketta")
    private CheckBox _kayteaanJarjestelmanHakulomaketta;

    @PropertyId("muuHakulomakeUrl")
    private TextField _muuHakulomakeUrl;


    @Value("${koodisto-uris.kieli:http://hakutyyppi}")
    private String _koodistoUriHakutyyppi;
    @Value("${koodisto-uris.kieli:http://hakukausi}")
    private String _koodistoUriHakukausi;
    @Value("${koodisto-uris.kieli:http://alkamiskausi}")
    private String _koodistoUriAlkamiskausi;
    @Value("${koodisto-uris.kieli:http://kohdejoukko}")
    private String _koodistoUriKohdejoukko;
    @Value("${koodisto-uris.kieli:http://hakutapa}")
    private String _koodistoUriHakutapa;

    private I18NHelper _i18n = new I18NHelper(this);

    public EditorHakuView() {
        super();
    }

    @Override
    public void attach() {
        super.attach();
        initialize();
    }

    private void initialize() {
        LOG.info("inititialize()");

        _layout = UiBuilder.newVerticalLayout();
        _layout.setSpacing(true);
        setCompositionRoot(_layout);

        //
        // Init fields
        //
        _hakutyyppi = UiBuilder.newKoodistoComboBox(_koodistoUriHakutyyppi, null, null, T("Hakutyyppi.prompt"), null);
        _hakukausi = UiBuilder.newKoodistoComboBox(_koodistoUriHakukausi, null, null, T("Hakukausi.prompt"), null);
        _hakuvuosi = UiBuilder.newTextField("", T("Hakuvuosi.prompt"), false);
        _koulutusAlkamiskausi = UiBuilder.newKoodistoComboBox(_koodistoUriAlkamiskausi, null, null, T("KoulutuksenAlkamiskausi.prompt"), null);
        _hakuKohdejoukko = UiBuilder.newKoodistoComboBox(_koodistoUriKohdejoukko, null, null, T("HakuKohdejoukko.prompt"), null);
        _hakutapa = UiBuilder.newKoodistoComboBox(_koodistoUriHakutapa, null, null, T("Hakutapa.prompt"), null);
        _haunNimiFI = UiBuilder.newTextField("", T("HaunNimiFI.prompt"), false);
        _haunNimiSE = UiBuilder.newTextField("", T("HaunNimiSE.prompt"), false);
        _haunNimiEN = UiBuilder.newTextField("", T("HaunNimiEN.prompt"), false);
        _haunTunniste = UiBuilder.newLabel("haunTunniste", (AbstractLayout) null);
        // TODO hakuaika
        _kaytetaanSijoittelua = UiBuilder.newCheckbox(T("KaytetaanSijoittelua"), null);
        _kayteaanJarjestelmanHakulomaketta = UiBuilder.newCheckbox(T("KaytetaanJarjestemanHakulomaketta"), null);
        _muuHakulomakeUrl = UiBuilder.newTextField("", T("MuuHakulomake.prompt"), false);


        createButtonBar(_layout);

        UiBuilder.newLabel(T("HaunTiedot"), _layout, LabelStyle.H2);
        UiBuilder.newHR(_layout);

        GridLayout grid = new GridLayout(3, 1);
        grid.setSpacing(true);
        _layout.addComponent(grid);

        grid.addComponent(UiBuilder.newLabel(T("Hakutyyppi"), (AbstractLayout) null));
        grid.addComponent(_hakutyyppi);
        grid.newLine();

        {
            grid.addComponent(UiBuilder.newLabel(T("HakukausiJaVuosi"), (AbstractLayout) null));
            HorizontalLayout hl = UiBuilder.newHorizontalLayout();
            hl.setSpacing(true);
            hl.addComponent(_hakukausi);
            hl.addComponent(_hakuvuosi);
            grid.addComponent(hl);
            grid.newLine();
        }

        grid.addComponent(UiBuilder.newLabel(T("KoulutuksenAlkamiskausi"), (AbstractLayout) null));
        grid.addComponent(_koulutusAlkamiskausi);
        grid.newLine();

        grid.addComponent(UiBuilder.newLabel(T("HakuKohdejoukko"), (AbstractLayout) null));
        grid.addComponent(_hakuKohdejoukko);
        grid.newLine();

        grid.addComponent(UiBuilder.newLabel(T("Hakutapa"), (AbstractLayout) null));
        grid.addComponent(_hakutapa);
        grid.newLine();

        {
            grid.addComponent(UiBuilder.newLabel(T("HaunNimi"), (AbstractLayout) null));
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.setSpacing(true);
            vl.addComponent(_haunNimiFI);
            vl.addComponent(_haunNimiSE);
            vl.addComponent(_haunNimiEN);
            grid.addComponent(vl);
            grid.newLine();
        }

        grid.addComponent(UiBuilder.newLabel(T("HaunTunniste"), (AbstractLayout) null));
        grid.addComponent(_haunTunniste);
        grid.newLine();

        grid.addComponent(UiBuilder.newLabel(T("Hakuaika"), (AbstractLayout) null));
        grid.addComponent(UiBuilder.newLabel("*** TODO ***", (AbstractLayout) null));
        grid.newLine();

        grid.space();
        grid.addComponent(_kaytetaanSijoittelua);
        grid.newLine();

        {
            grid.addComponent(UiBuilder.newLabel(T("Hakulomake"), (AbstractLayout) null));
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.setSpacing(true);
            vl.addComponent(_kayteaanJarjestelmanHakulomaketta);
            vl.addComponent(_muuHakulomakeUrl);
            grid.addComponent(vl);
            grid.newLine();
        }

        createButtonBar(_layout);
    }


    /**
     * Top and botton button bars.
     *
     * @param layout
     * @return
     */
    private HorizontalLayout createButtonBar(VerticalLayout layout) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        if (layout != null) {
            layout.addComponent(hl);
        }

        Button btnCancel = UiBuilder.newButton(T("Peruuta"), hl);
        btnCancel.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                fireEvent(new CancelEvent(EditorHakuView.this));
            }
        });

        Button btnSaveUncomplete = UiBuilder.newButton(T("TallennaLuonnoksena"), hl);
        btnSaveUncomplete.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                fireEvent(new SaveEvent(EditorHakuView.this, false));
            }
        });

        Button btnSaveComplete = UiBuilder.newButton(T("TallennaValmiina"), hl);
        btnSaveComplete.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                fireEvent(new SaveEvent(EditorHakuView.this, true));
            }
        });

        Button btnContinue = UiBuilder.newButton(T("Jatka"), hl);
        btnContinue.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                fireEvent(new ContinueEvent(EditorHakuView.this));
            }
        });


        return hl;
    }

    /**
     * Translator helper.
     *
     * @param key
     * @return
     */
    private String T(String key) {
        return _i18n.getMessage(key);
    }


    /**
     * Fired when save is pressed.
     */
    public class CancelEvent extends Component.Event {
        public CancelEvent(Component source) {
            super(source);
        }
    }

    /**
     * Fired when save is pressed.
     */
    public class SaveEvent extends Component.Event {
        boolean _complete = false;
        public SaveEvent(Component source, boolean complete) {
            super(source);
            _complete = complete;
        }

        public boolean isComplete() {
            return _complete;
        }
    }

    /**
     * Fired when delete is pressed.
     */
    public class DeleteEvent extends Component.Event {
        public DeleteEvent(Component source) {
            super(source);
        }
    }

    /**
     * Fired when Continue is pressed.
     */
    public class ContinueEvent extends Component.Event {
        public ContinueEvent(Component source) {
            super(source);
        }
    }

}
