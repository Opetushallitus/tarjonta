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
package fi.vm.sade.tarjonta.ui.view.koulutus.kk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.OphTokenField;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;

@Configurable(preConstruction = true)
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditKorkeakouluKuvailevatTiedotFormView extends VerticalLayout {

    @Autowired
    private transient TarjontaUIHelper uiHelper;
    private static transient final Logger LOG = LoggerFactory.getLogger(EditKorkeakouluKuvailevatTiedotFormView.class);
    private static transient final long serialVersionUID = 1L;
    private transient I18NHelper i18n;
    private transient TarjontaPresenter presenter;
    private transient UiBuilder uiBuilder;
    private EditKorkeakouluKuvailevatTiedotTekstikentatTabSheet tekstit;
    private OphTokenField tokenField;
    private KorkeakouluKuvailevatTiedotViewModel model;

    public EditKorkeakouluKuvailevatTiedotFormView() {
    }

    public EditKorkeakouluKuvailevatTiedotFormView(final TarjontaPresenter presenter, final UiBuilder uiBuilder,
            final KorkeakouluKuvailevatTiedotViewModel model) {
        this.uiBuilder = uiBuilder;
        this.presenter = presenter;
        this.model = model;
        initializeLayout();
    }

    private AbstractLayout buildAmmattinimikkeet() {
        HorizontalLayout hlAmmattinimike = new HorizontalLayout();
        hlAmmattinimike.setMargin(false, false, true, false);

        PropertysetItem psi = new BeanItem(model.getAmmattinimikkeet());
        tokenField = uiBuilder.koodistoTokenField(hlAmmattinimike, KoodistoURI.KOODISTO_AMMATTINIMIKKEET_URI, psi, "ammattinimikkeet");
        tokenField.setFormatter(new OphTokenField.SelectedTokenToTextFormatter() {
            @Override
            public String formatToken(Object selectedToken) {
                return uiHelper.getKoodiNimi((String) selectedToken);
            }
        });
        return hlAmmattinimike;
    }

    private void initializeLayout() {
        //
        // Ammattinimikkeet
        //
        addComponent(UiBuilder.label((AbstractLayout) null, T("ammattinimikkeet"), LabelStyleEnum.H2));
        addComponent(UiBuilder.label((AbstractLayout) null, T("ammattinimikkeet.help"), LabelStyleEnum.TEXT));

        AbstractLayout ammattinimikkeet = buildAmmattinimikkeet();
        addComponent(ammattinimikkeet);
        setComponentAlignment(ammattinimikkeet, Alignment.TOP_LEFT);


        tekstit = new EditKorkeakouluKuvailevatTiedotTekstikentatTabSheet(presenter.getModel(), uiHelper, uiBuilder);
        addComponent(tekstit);

        // activate all property annotation validations
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    // Generic translation helpers
    private String T(String key) {
        return getI18n().getMessage(key);
    }

    private I18NHelper getI18n() {
        if (i18n == null) {
            i18n = new I18NHelper(this);
        }
        return i18n;
    }

    public void reBuildTabsheet() {
        if (tekstit != null) {
            tekstit.reload();
        }
    }

    @Override
    public void attach() {
        super.attach();

        if (tokenField != null) {
            tokenField.setWidth("900px");
        }
    }
}
