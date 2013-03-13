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

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.OphRichTextArea;
import fi.vm.sade.generic.ui.component.OphTokenField;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.KoodistoSelectionTabSheet;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

/**
 * For editing the studies (koulutus) additional information.
 *
 * @author mlyly
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditKoulutusLisatiedotForm extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusLisatiedotForm.class);
    private static final long serialVersionUID = -4054591599209251060L;
    private TarjontaPresenter _presenter;
    private TarjontaUIHelper _uiHelper;
    private transient I18NHelper _i18n;
    private LisatiedotTabSheet tabs;
    private OphTokenField f;
    private HorizontalLayout hlAmmattinimike;

    public EditKoulutusLisatiedotForm(TarjontaPresenter presenter, TarjontaUIHelper uiHelper, UiBuilder uiBuilder, KoulutusLisatiedotModel koulutusLisatiedotModel) {
        this.setWidth(100, UNITS_PERCENTAGE);
        this._presenter = presenter;
        this._uiHelper = uiHelper;

        //
        // Ammattinimikkeet
        //
        addComponent(UiBuilder.label((AbstractLayout) null, T("ammattinimikkeet"), LabelStyleEnum.H2));
        addComponent(UiBuilder.label((AbstractLayout) null, T("ammattinimikkeet.help"), LabelStyleEnum.TEXT));


        hlAmmattinimike = new HorizontalLayout();
        PropertysetItem psi = new BeanItem(koulutusLisatiedotModel);
        f = uiBuilder.koodistoTokenField(hlAmmattinimike, KoodistoURIHelper.KOODISTO_AMMATTINIMIKKEET_URI, psi, "ammattinimikkeet");
        f.setFormatter(new OphTokenField.SelectedTokenToTextFormatter() {
            @Override
            public String formatToken(Object selectedToken) {
                return _uiHelper.getKoodiNimi((String) selectedToken);
            }
        });

        // Create caption formatter for koodisto component.
        KoodistoComponent k = (KoodistoComponent) f.getSelectionComponent();
        k.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType kt = (KoodiType) dto;
                    return _uiHelper.getKoodiNimi(kt, null);
                    // return _uiHelper.getKoodiNimi(kt.getKoodiUri());
                } else {
                    return "??? " + dto;
                }
            }
        });

        hlAmmattinimike.addComponent(f);
        this.addComponent(hlAmmattinimike);

        //
        // Build tabsheet for languages with koodisto select languages
        //
        tabs = new LisatiedotTabSheet(_presenter.getModel(), uiHelper, uiBuilder);
        addComponent(UiBuilder.label((AbstractLayout) null, T("kieliriippuvatTiedot"), LabelStyleEnum.H2));
        addComponent(tabs);
    }

    public void reBuildTabsheet() {
        tabs.reload();
    }

    // Generic translatio helpers
    private String T(String key) {
        return getI18n().getMessage(key);
    }

    private I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }

    @Override
    public void attach() {
        super.attach();
        {
            f.getSelectionLayout().setWidth("900px");
        }
    }
}
