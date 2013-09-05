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

import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import static fi.vm.sade.generic.common.validation.ValidationConstants.EMAIL_PATTERN;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 *
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class YhteyshenkiloViewForm extends VerticalLayout {

    private static transient final Logger LOG = LoggerFactory.getLogger(YhteyshenkiloViewForm.class);
    private static final long serialVersionUID = -3571709365318709818L;
    @Size(min = 1, max = 255, message = "{validation.koulutus.yhteyshenkilo.tooLong.nimi}")
    @PropertyId("yhtHenkKokoNimi")
    private TextField yhtHenkKokoNimi;
    @Size(min = 1, max = 255, message = "{validation.koulutus.yhteyshenkilo.tooLong.titteli}")
    @PropertyId("yhtHenkTitteli")
    private TextField yhtHenkTitteli;
    @Pattern(regexp = EMAIL_PATTERN, message = "{validation.koulutus.yhteyshenkilo.invalid.email}")
    @Size(min = 1, max = 255, message = "{validation.koulutus.yhteyshenkilo.tooLong.email}")
    @PropertyId("yhtHenkEmail")
    private TextField yhtHenkEmail;
    @Pattern(regexp = "[+|-| |\\(|\\)|[0-9]]{3,100}", message = "{validation.koulutus.yhteyshenkilo.invalid.phone}")
    @PropertyId("yhtHenkPuhelin")
    private TextField yhtHenkPuhelin;
    private String initialYhtHenkTitteli;
    private String initialYhtHenkEmail;
    private String initialYhtHenkPuhelin;
    private YhteyshenkiloModel model;
    private TarjontaPresenter presenter;
    private VerticalLayout selectionFieldLayout;

    public YhteyshenkiloViewForm(TarjontaPresenter presenter, YhteyshenkiloModel model) {
        this.model = model;
        this.presenter = presenter;
        init();
        //activate all property annotation validations
    }

    /**
     * Populating the yhteyshenkilo fields based on user's selection from the
     * autocomplete list
     *
     * @param henkiloType
     */
    public void populateYhtHenkiloFields(HenkiloType henkiloType) {

        if (henkiloType == null) {
            return;
        }
        LOG.info(henkiloType.getEtunimet());

        this.yhtHenkKokoNimi.setValue(henkiloType.getEtunimet() + " " + henkiloType.getSukunimi());
        this.model.setYhtHenkiloOid(henkiloType.getOidHenkilo());
        if (henkiloType.getOrganisaatioHenkilos() != null && !henkiloType.getOrganisaatioHenkilos().isEmpty()) {
            this.getYhtHenkEmail().setValue(henkiloType.getOrganisaatioHenkilos().get(0).getSahkopostiosoite());
            this.getYhtHenkPuhelin().setValue(henkiloType.getOrganisaatioHenkilos().get(0).getPuhelinnumero());
            this.getYhtHenkTitteli().setValue(henkiloType.getOrganisaatioHenkilos().get(0).getTehtavanimike());
        } else {
            this.getYhtHenkEmail().setValue(null);
            this.getYhtHenkPuhelin().setValue(null);
            this.getYhtHenkTitteli().setValue(null);
        }
    }

    /*
     * Restoring the initial values to yhteyshenkilo fields. this functionality is to enable the user
     * to try different yhteyshenkilos from search but then return to the old one. This is
     * important in the cases that the current yhteyshenkilo is not in the user register but is
     * created by the editor of this koulutus (to not loose data).
     */
    private void restoreInitialValuesToYhtHenkiloFields() {
        this.getYhtHenkEmail().setValue(getInitialYhtHenkEmail());
        this.getYhtHenkPuhelin().setValue(getInitialYhtHenkPuhelin());
        this.getYhtHenkTitteli().setValue(getInitialYhtHenkTitteli());
        this.model.setYhtHenkiloOid(null);
    }

    /*
     * Nullifying the initial values to yhteyshenkilo fields. When the user decides to
     * remove the existing yhteyshenkilo and possibly add a new one.
     */
    public void clearInitialValuestoYhtHenkiloFields() {
        initialYhtHenkEmail = null;
        initialYhtHenkPuhelin = null;
        initialYhtHenkTitteli = null;
        restoreInitialValuesToYhtHenkiloFields();
    }

    /**
     * Builds the yhteyshenkilo part of the form.
     *
     * @param grid
     * @param propertyKey
     */
    public void init() {
        selectionFieldLayout = new VerticalLayout();

        yhtHenkKokoNimi = new YhteyshenkiloAutocompleteTextField(selectionFieldLayout, "prompt.kokoNimi", "", presenter, model);
        yhtHenkKokoNimi.addListener(new Listener() {
            private static final long serialVersionUID = 6680073663370984689L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof AutocompleteTextField.HenkiloAutocompleteEvent
                        && ((AutocompleteTextField.HenkiloAutocompleteEvent) event).getEventType() == AutocompleteTextField.HenkiloAutocompleteEvent.SELECTED) {
                    populateYhtHenkiloFields(((AutocompleteTextField.HenkiloAutocompleteEvent) event).getHenkilo());
                } else if (event instanceof AutocompleteTextField.HenkiloAutocompleteEvent
                        && ((AutocompleteTextField.HenkiloAutocompleteEvent) event).getEventType() == AutocompleteTextField.HenkiloAutocompleteEvent.NOT_SELECTED) {
                    restoreInitialValuesToYhtHenkiloFields();
                } else if (event instanceof AutocompleteTextField.HenkiloAutocompleteEvent
                        && ((AutocompleteTextField.HenkiloAutocompleteEvent) event).getEventType() == AutocompleteTextField.HenkiloAutocompleteEvent.CLEAR) {
                    clearInitialValuestoYhtHenkiloFields();
                }
            }
        });
        yhtHenkTitteli = UiUtil.textField(null, "", "", true);
        yhtHenkEmail = UiUtil.textField(null, "", "", true);
        yhtHenkPuhelin = UiUtil.textField(null, "", "", true);

        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    public VerticalLayout getSelectionFieldLayout() {
        return selectionFieldLayout;
    }

    /**
     * @return the yhtHenkKokoNimi
     */
    public TextField getYhtHenkKokoNimi() {
        return yhtHenkKokoNimi;
    }

    /**
     * @return the yhtHenkTitteli
     */
    public TextField getYhtHenkTitteli() {
        return yhtHenkTitteli;
    }

    /**
     * @return the yhtHenkEmail
     */
    public TextField getYhtHenkEmail() {
        return yhtHenkEmail;
    }

    /**
     * @return the yhtHenkPuhelin
     */
    public TextField getYhtHenkPuhelin() {
        return yhtHenkPuhelin;
    }

    /**
     * @return the initialYhtHenkTitteli
     */
    public String getInitialYhtHenkTitteli() {
        return initialYhtHenkTitteli;
    }

    /**
     * @return the initialYhtHenkEmail
     */
    public String getInitialYhtHenkEmail() {
        return initialYhtHenkEmail;
    }

    /**
     * @return the initialYhtHenkPuhelin
     */
    public String getInitialYhtHenkPuhelin() {
        return initialYhtHenkPuhelin;
    }

    public void initialize() {
        initialYhtHenkTitteli = model.getYhtHenkTitteli();
        initialYhtHenkEmail = model.getYhtHenkEmail();
        initialYhtHenkPuhelin = model.getYhtHenkPuhelin();
    }

    public void setFieldWidth(int width) {
        yhtHenkTitteli.setWidth(width, UNITS_PIXELS);
        yhtHenkEmail.setWidth(width, UNITS_PIXELS);
        yhtHenkKokoNimi.setWidth(width, UNITS_PIXELS);
        yhtHenkPuhelin.setWidth(width, UNITS_PIXELS);
    }
}
