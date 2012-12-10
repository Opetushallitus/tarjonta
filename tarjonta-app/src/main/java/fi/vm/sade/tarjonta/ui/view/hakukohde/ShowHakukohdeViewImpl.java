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
package fi.vm.sade.tarjonta.ui.view.hakukohde;

import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.RequiredRole;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.view.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Autowired;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;

import java.util.List;

/*
* Author: Tuomas Katva
*/
@Configurable(preConstruction = true)
public class ShowHakukohdeViewImpl extends AbstractVerticalInfoLayout  {

    private static final Logger LOG = LoggerFactory.getLogger(ShowHakukohdeViewImpl.class);

    @Autowired(required=true)
    private TarjontaPresenter tarjontaPresenterPresenter;

    public ShowHakukohdeViewImpl (String pageTitle, String message, PageNavigationDTO dto) {
        super(VerticalLayout.class, pageTitle, message, dto);
    }

    @Override
    public void buildLayout(VerticalLayout layout) {

        layout.removeAllComponents();

        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        Panel panel = new Panel();

        panel.setContent(vl);
        layout.addComponent(panel);

        //Build the layout
        addNavigationButtons(vl);
        buildMiddleContentLayout(vl);

    }

    private void buildMiddleContentLayout(VerticalLayout layout) {

        layout.addComponent(buildHeaderLayout(T("perustiedot"), T(CommonTranslationKeys.MUOKKAA), new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Toiminnallisuutta ei vielä toteutettu");
            }
        }, true));
        GridLayout grid = new GridLayout(2, 1);
        grid.setMargin(true);
        grid.setHeight("100%");


        HakukohdeViewModel model = tarjontaPresenterPresenter.getModel().getHakukohde();

        addItemToGrid(grid, "hakukohdeNimi", model.getHakukohdeKoodistoNimi());

        addItemToGrid(grid, "haunNimi", tryGetLocalizedHakuNimi(model.getHakuOid()));
        addItemToGrid(grid,"aloitusPaikat",new Integer(model.getAloitusPaikat()).toString());
        addItemToGrid(grid,"lisatiedot",getLocalizedLisatiedot(model.getLisatiedot()));

        grid.setColumnExpandRatio(0, 1f);
        grid.setColumnExpandRatio(1, 2f);

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1f);

    }

    private String tryGetLocalizedHakuNimi(HakuViewModel hakuViewModel) {
        String haunNimi = null;

        if(I18N.getLocale().getLanguage().trim().equalsIgnoreCase("en")) {
            haunNimi = hakuViewModel.getNimiEn();
        } else if (I18N.getLocale().getLanguage().trim().equalsIgnoreCase("se")) {
            haunNimi = hakuViewModel.getNimiSe();
        }

        if (haunNimi == null || I18N.getLocale().getLanguage().trim().equalsIgnoreCase("fi")) {
            haunNimi = hakuViewModel.getNimiFi();
        }

        return  haunNimi;
    }

    private String getLocalizedLisatiedot(List<KielikaannosViewModel> kielet) {
         String reply = null;
         for (KielikaannosViewModel kieli:kielet) {
             if (kieli.getKielikoodi().trim().equalsIgnoreCase(I18N.getLocale().getLanguage().trim())) {
                 reply = kieli.getNimi();
             }
         }
        return reply;
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener, boolean enable) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiBuilder.buttonSmallPrimary(headerLayout, btnCaption, listener, RequiredRole.UPDATE, tarjontaPresenterPresenter.getPermission());

            // Add default click listener so that we can show that action has not been implemented as of yet
            if (listener == null) {
                btn.addListener(new Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        getWindow().showNotification("Toiminnallisuutta ei vielä toteutettu");
                    }
                });
            }

            headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }

    private void addNavigationButtons(VerticalLayout layout) {
        addNavigationButton("", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                tarjontaPresenterPresenter.showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(T(CommonTranslationKeys.POISTA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // TODO ask confirmation
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        addNavigationButton(T(CommonTranslationKeys.KOPIOI_UUDEKSI), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        addNavigationButton(T("siirraOsaksiToistaKoulutusta"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        addNavigationButton(T("lisaaToteutus"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        addNavigationButton(T("esikatsele"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

    }

    /**
     * Add line with label + textual label value to the grid.
     *
     * @param grid
     * @param labelCaptionKey
     * @param labelCaptionValue
     */
    private void addItemToGrid(GridLayout grid, String labelCaptionKey, String labelCaptionValue) {
        addItemToGrid(grid, labelCaptionKey, new Label(labelCaptionValue));
    }

    /**
     * Add label + component to grid layout.
     *
     * @param grid
     * @param labelCaptionKey
     * @param component
     */
    private void addItemToGrid(GridLayout grid, String labelCaptionKey, Component component) {
        if (grid != null) {
            HorizontalLayout hl = UiUtil.horizontalLayout(false, UiMarginEnum.RIGHT);
            hl.setSizeFull();
            UiUtil.label(hl, T(labelCaptionKey));
            grid.addComponent(hl);
            grid.addComponent(component);
            grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);
            grid.setComponentAlignment(component, Alignment.TOP_LEFT);
            grid.newLine();
        }
    }


    private void backFired() {
        fireEvent(new BackEvent(this));
    }

    private void editFired() {
        fireEvent(new EditEvent(this));
    }

    /**
     * Fired when Back is pressed.
     */
    public class BackEvent extends Component.Event {

        public BackEvent(Component source) {
            super(source);

        }
    }

    /**
     * Fired when Edit is pressed.
     */
    public class EditEvent extends Component.Event {

        public EditEvent(Component source) {
            super(source);

        }
    }


}
