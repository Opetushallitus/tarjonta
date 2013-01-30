
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

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.util.UiUtil;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.KoodistoSelectionTabSheet;
import fi.vm.sade.generic.ui.component.OphRichTextArea;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Tuomas Katva
 */
@Configurable
public abstract class LanguageTabSheet extends CustomComponent {

    private static final Logger LOG = LoggerFactory.getLogger(LanguageTabSheet.class);
    private static final ThemeResource TAB_ICON_PLUS = new ThemeResource(UiConstant.RESOURCE_URL_OPH_IMG + "icon-add-black.png");
    private static final long serialVersionUID = -185022467161014683L;
    private boolean attached = false;
    protected TarjontaModel _model;
    @Autowired
    protected TarjontaPresenter presenter;
    @Autowired
    protected TarjontaUIHelper _uiHelper;
    protected KoodistoSelectionTabSheet _languageTabsheet;
    protected VerticalLayout rootLayout = new VerticalLayout();
    @Autowired(required = true)
    protected transient UiBuilder uiBuilder;
    private boolean useRichText = false;

    private String TABSHEET_WIDTH = "500px";
    private String TABSHEET_HEIGHT = "300px";

    private String RICH_TEXT_HEIGHT = null;
    private String RICH_TEXT_WIDTH = null;

    public LanguageTabSheet() {
        setCompositionRoot(rootLayout);
        rootLayout.setSizeUndefined();
    }

    public LanguageTabSheet(boolean useRichText, String width, String height) {
        TABSHEET_WIDTH = width;
        TABSHEET_HEIGHT = height;
        this.useRichText = useRichText;
        rootLayout.setSizeUndefined();
        setCompositionRoot(rootLayout);


    }

    public LanguageTabSheet(boolean useRichText, String tabSheetWidth, String tabSheetHeight,String rtWidth, String rtHeight) {
        TABSHEET_WIDTH = tabSheetWidth;
        TABSHEET_HEIGHT = tabSheetHeight;
        RICH_TEXT_HEIGHT = rtHeight;
        RICH_TEXT_WIDTH = rtWidth;
        this.useRichText = useRichText;
        rootLayout.setSizeUndefined();
        setCompositionRoot(rootLayout);
    }

    @Override
    public void attach() {
        super.attach();
        if (!attached) {
            initialize();
            attached = true;
        }
    }

    private void initialize() {
        _model = presenter.getModel();
        _languageTabsheet = new KoodistoSelectionTabSheet(KoodistoURIHelper.KOODISTO_KIELI_URI, uiBuilder) {
            @Override
            public void doAddTab(String uri) {
                addTab(uri, createRichText(""), _uiHelper.getKoodiNimi(uri));
            }
        };

        rootLayout.addComponent(_languageTabsheet);
        _languageTabsheet.setWidth(TABSHEET_WIDTH);
        _languageTabsheet.setHeight(TABSHEET_HEIGHT);
        initializeTabsheet();
    }

    protected abstract void initializeTabsheet();

    private String retrieveTabText(Tab tab) {
        Component component = tab.getComponent();

        if (component instanceof AbstractField) {
            AbstractField richArea = (AbstractField) component;
            return (String) richArea.getValue();
        } else {
            LOG.warn("Tab component not OphRichTextArea");
            return "";
        }
    }

    public List<KielikaannosViewModel> getKieliKaannokset() {
        List<KielikaannosViewModel> languageValues = new ArrayList<KielikaannosViewModel>();

        for (String key : _languageTabsheet.getTabs().keySet()) {

            KielikaannosViewModel kieli = new KielikaannosViewModel(key, retrieveTabText(_languageTabsheet.getTab(key)));
            languageValues.add(kieli);

        }

        return languageValues;
    }

    protected Tab getTab(String koodiUri) {
        return _languageTabsheet.getTab(koodiUri);
    }

    protected void setInitialValues(List<KielikaannosViewModel> values) {
        if (values != null) {
            Set<String> valitutKielet = new HashSet<String>();
            for (KielikaannosViewModel kieliKaannos : values) {
                valitutKielet.add(kieliKaannos.getKielikoodi());
                _languageTabsheet.addTab(kieliKaannos.getKielikoodi(), createRichText(kieliKaannos.getNimi()), _uiHelper.getKoodiNimi(kieliKaannos.getKielikoodi()));
            }
            _languageTabsheet.getKcSelection().setValue(valitutKielet);
        }
    }

    protected AbstractField createRichText(String value) {
        if (useRichText) {
            OphRichTextArea richText = UiUtil.richTextArea(null,null,null);
            if (RICH_TEXT_WIDTH == null || RICH_TEXT_HEIGHT == null) {
            richText.setHeight(TABSHEET_HEIGHT);
            richText.setWidth(TABSHEET_WIDTH);
            } else {
                richText.setHeight(RICH_TEXT_HEIGHT);
                richText.setWidth(RICH_TEXT_WIDTH);
            }
            richText.setValue(value);
            return richText;
        }  else {
        TextField textField = UiUtil.textField(null);
        textField.setHeight(TABSHEET_HEIGHT);
        textField.setWidth(UiConstant.PCT100);
        textField.setValue(value);
        return textField;
        }
    }
}