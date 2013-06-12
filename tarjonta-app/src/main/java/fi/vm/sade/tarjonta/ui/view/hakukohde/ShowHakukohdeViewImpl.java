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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Value;

/*
 * Author: Tuomas Katva
 */
@Configurable(preConstruction = true)
public class ShowHakukohdeViewImpl extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowHakukohdeViewImpl.class);
    private static final long serialVersionUID = -4485798240650803109L;
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenterPresenter;
    @Autowired(required = true)
    private TarjontaUIHelper tarjontaUIHelper;
    private Window confirmationWindow;

    private @Value("${koodisto.suomi.uri:suomi}") String suomiUri;
    public ShowHakukohdeViewImpl(String pageTitle, String message, PageNavigationDTO dto) {
        super(VerticalLayout.class, pageTitle, message, dto);
        LOG.debug(this.getClass().getName() + "()");
    }

    @Override
    public void buildLayout(VerticalLayout layout) {

        layout.removeAllComponents();

        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        Panel panel = new Panel();

        panel.setContent(vl);
        layout.addComponent(panel);

        //Build the layout

        //XXX oid not set
        addNavigationButtons(vl, OrganisaatioContext.getContext(tarjontaPresenterPresenter.getTarjoaja().getSelectedOrganisationOid()));
        Set<String> allLangs = getAllKielet();
        final TabSheet tabs = new TabSheet();
        for (String lang:allLangs) {
           ShowHakukohdeTab hakukohdeTab = new ShowHakukohdeTab(lang);
           tabs.addTab(hakukohdeTab, tarjontaUIHelper.getKoodiNimi(lang));
           if (lang.trim().equalsIgnoreCase(suomiUri)) {
               tabs.setSelectedTab(hakukohdeTab);
           }
        }
        vl.addComponent(tabs);


    }

    public void showErrorMsg(String msg) {
        getWindow().showNotification(T(msg), Window.Notification.TYPE_ERROR_MESSAGE);
    }

    private Set<String> getAllKielet() {
        Set<String> kielet = new HashSet<String>();
        List<KielikaannosViewModel> lisatietoKielet = tarjontaPresenterPresenter.getModel().getHakukohde().getLisatiedot();
        for (KielikaannosViewModel kieli : lisatietoKielet) {
            kielet.add(kieli.getKielikoodi());
        }
        List<KielikaannosViewModel> valintakoeKielet = tarjontaPresenterPresenter.getModel().getHakukohde().getValintaPerusteidenKuvaus();
        for (KielikaannosViewModel kieli:valintakoeKielet) {
            kielet.add(kieli.getKielikoodi());
        }

        return kielet;
    }


    private void addNavigationButtons(VerticalLayout layout, OrganisaatioContext context) {
        addNavigationButton("", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                tarjontaPresenterPresenter.reloadAndShowMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        Button poista = addNavigationButton(T(CommonTranslationKeys.POISTA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (checkHaunAlkaminen()) {
                    showConfirmationDialog();
                } else {
                    getWindow().showNotification(T("hakukohdePoistoEpaonnistui"), Window.Notification.TYPE_ERROR_MESSAGE);
                }

            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        /*Button kopioiUudeksi = addNavigationButton(T(CommonTranslationKeys.KOPIOI_UUDEKSI), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);*/

        //permissions
        poista.setVisible(tarjontaPresenterPresenter.getPermission().userCanDeleteHakukohde(context));
        /*kopioiUudeksi.setVisible(tarjontaPresenterPresenter.getPermission().userCanCopyHakukohdAsNew(context));*/
    }

    private void showConfirmationDialog() {
        RemovalConfirmationDialog confirmationDialog = new RemovalConfirmationDialog(T("poistoVarmistus"),
                tarjontaPresenterPresenter.getModel().getHakukohde().getHakukohdeKoodistoNimi(), T("poistaPainike"), T("peruutaPainike"),
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(confirmationWindow);
                tarjontaPresenterPresenter.removeSelectedHakukohde();


            }
        },
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (confirmationWindow != null) {
                    getWindow().removeWindow(confirmationWindow);
                }
            }
        });
        confirmationWindow = new Window();
        confirmationWindow.setContent(confirmationDialog);
        confirmationWindow.setModal(true);
        confirmationWindow.center();
        getWindow().addWindow(confirmationWindow);


    }

    public void showKoulutusRemovalDialog(final KoulutusOidNameViewModel koulutus) {
        final Window koulutusRemovalDialog = new Window();
        RemovalConfirmationDialog confirmationDialog = new RemovalConfirmationDialog(T("removeKoulutusFromHakukohde"), koulutus.getKoulutusNimi(), T("poistaRemoveKoulutusFromHakukohde"),
                T("peruutaRemoveKoulutusFromHakukohde"),
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(koulutusRemovalDialog);
                tarjontaPresenterPresenter.removeKoulutusFromHakukohde(koulutus);
            }
        },
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(koulutusRemovalDialog);
            }
        });
        koulutusRemovalDialog.setContent(confirmationDialog);
        koulutusRemovalDialog.setModal(true);
        koulutusRemovalDialog.center();
        getWindow().addWindow(koulutusRemovalDialog);
    }

    private boolean checkHaunAlkaminen() {
        tarjontaPresenterPresenter.loadHakukohdeHakuPvm();
        Date haunPaattymisPvm = tarjontaPresenterPresenter.getModel().getHakukohde().getHakuOid().getPaattymisPvm();
        Date haunAlkamisPvm = tarjontaPresenterPresenter.getModel().getHakukohde().getHakuOid().getAlkamisPvm();
        Date tanaan = new Date();
        if (tanaan.after(haunAlkamisPvm) && tanaan.before(haunPaattymisPvm)) {
            return false;
        } else {
            return true;
        }
    }



    public void addLayoutSplit(VerticalLayout layout) {
        VerticalSplitPanel split = new VerticalSplitPanel();
        split.setImmediate(false);
        split.setWidth("100%");
        split.setHeight("2px");
        split.setLocked(true);

        layout.addComponent(split);
    }

    /*private void backFired() {
        fireEvent(new BackEvent(this));
    }

    private void editFired() {
        fireEvent(new EditEvent(this));
    }*/

    /**
     * Fired when Back is pressed.
     */
    public class BackEvent extends Component.Event {

        private static final long serialVersionUID = -1576894176022341609L;

        public BackEvent(Component source) {
            super(source);

        }
    }

    /**
     * Fired when Edit is pressed.
     */
    public class EditEvent extends Component.Event {

        private static final long serialVersionUID = -5412731409384095606L;

        public EditEvent(Component source) {
            super(source);

        }
    }
}
