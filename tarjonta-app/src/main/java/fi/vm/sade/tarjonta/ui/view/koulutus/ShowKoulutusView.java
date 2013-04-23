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

import java.util.List;
import java.util.Locale;
import java.util.Set;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusListausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.service.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;

/**
 * Show collected information about koulutus.
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class ShowKoulutusView extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusView.class);
    private static final long serialVersionUID = -4381256372874208231L;
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private TarjontaUIHelper tarjontaUIHelper;
    private TarjontaDialogWindow tarjontaDialog;



    private @Value("${koodisto.suomi.uri:suomi}") String suomiUri;

    public ShowKoulutusView(String pageTitle, PageNavigationDTO pageNavigationDTO) {
        super(VerticalLayout.class, pageTitle, null, pageNavigationDTO);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        LOG.debug("buildLayout(): hakutyyppi uri={}", KoodistoURIHelper.KOODISTO_HAKUTYYPPI_URI);

        if (presenter == null) {
            presenter = new TarjontaPresenter();
        }

        if (tarjontaUIHelper == null) {
            tarjontaUIHelper = new TarjontaUIHelper();
        }

        layout.removeAllComponents();

        addNavigationButtons(layout, OrganisaatioContext.getContext(presenter.getModel().getTarjoajaModel().getSelectedOrganisationOid()));

        final LueKoulutusVastausTyyppi koulutus = presenter.getKoulutusByOid(presenter.getModel().getKoulutusPerustiedotModel().getOid());

        // language tabs
        final TabSheet tabs = new TabSheet();

        final Set<String> languages = presenter.getModel()
                .getKoulutusLisatiedotModel().getLisatiedot().keySet();

        if(languages.size()==0) {
            //no languages available, "add" fi 
            presenter.getModel().getKoulutusLisatiedotModel().getLisatiedot(suomiUri);
        }
        
        for (String language : languages) {
            List<KoodiType> koodit = tarjontaUIHelper.getKoodis(language);
            ShowKoulutusViewTab tab = new ShowKoulutusViewTab(language,
                    new Locale(koodit.get(0).getKoodiArvo()), koulutus);
            tabs.addTab(tab, tarjontaUIHelper.getKoodiNimi(language));
        }

        layout.addComponent(tabs);
    }

    public void showHakukohdeRemovalDialog(final String hakukohdeOid, final String hakukohdeNimi) {
        final Window hakukohdeRemovalDialog = new Window();
        RemovalConfirmationDialog removalConfirmationDialog = new RemovalConfirmationDialog(T("removeHakukohdeFromKoulutusQ"), hakukohdeNimi, T("jatkaBtn"), T("peruutaBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(hakukohdeRemovalDialog);
                presenter.removeHakukohdeFromKoulutus(hakukohdeOid);

            }
        }, new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(hakukohdeRemovalDialog);
            }
        });
        hakukohdeRemovalDialog.setContent(removalConfirmationDialog);
        hakukohdeRemovalDialog.setModal(true);
        hakukohdeRemovalDialog.center();
        getWindow().addWindow(hakukohdeRemovalDialog);

    }

    private void addNavigationButtons(VerticalLayout layout, OrganisaatioContext context) {
    	addNavigationButton("", new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {

    			presenter.reloadAndShowMainDefaultView();
    		}
    	}, StyleEnum.STYLE_BUTTON_BACK);




    	final Button poista = addNavigationButton(T(CommonTranslationKeys.POISTA), new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {
    			showRemoveDialog();

    		}
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);


    	final Button kopioiUudeksi = addNavigationButton(T(CommonTranslationKeys.KOPIOI_UUDEKSI), new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {
    			KoulutusKopiointiDialog kopiointiDialog = new KoulutusKopiointiDialog("600px","500px",KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS );
    			getWindow().addWindow(kopiointiDialog);
    		}
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);

    	final Button siirraOsaksiToista = addNavigationButton(T("siirraOsaksiToistaKoulutusta"), new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {
    			getWindow().showNotification("Ei toteutettu");
    		}
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);

    	final Button lisaaToteutus = addNavigationButton(T("lisaaToteutus"), new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {
    			presenter.showLisaaRinnakkainenToteutusEditView(presenter.getModel().getKoulutusPerustiedotModel().getOid());
    		}
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);

    	final Button esikatsele = addNavigationButton(T("esikatsele"), new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {
    			getWindow().showNotification("Ei toteutettu");
    		}
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);

    	//check permissions
    	final TarjontaPermissionServiceImpl permissions = presenter.getPermission(); 
    	poista.setVisible((presenter.getModel().getKoulutusPerustiedotModel().getTila().equals(TarjontaTila.VALMIS) 
    						|| presenter.getModel().getKoulutusPerustiedotModel().getTila().equals(TarjontaTila.LUONNOS)) 
    					    && permissions.userCanDeleteKoulutus(context));
    	kopioiUudeksi.setVisible(permissions.userCanCopyKoulutusAsNew(context));
    	siirraOsaksiToista.setVisible(permissions.userCanMoveKoulutus(context));
    	lisaaToteutus.setVisible(permissions.userCanAddKoulutusInstanceToKoulutus(context));
    }

    public void addLayoutSplit(VerticalLayout layout) {
        VerticalSplitPanel split = new VerticalSplitPanel();
        split.setImmediate(false);
        split.setWidth("100%");
        split.setHeight("2px");
        split.setLocked(true);

        layout.addComponent(split);
    }

    private void showRemoveDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("removeQ"), "", T("removeYes"), T("removeNo"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeKoulutusCreationDialog();
                KoulutusTulos koulutus = new KoulutusTulos();
                KoulutusListausTyyppi koulutusKooste = new KoulutusListausTyyppi();
                koulutusKooste.setKoulutusmoduuliToteutus(getEditViewOid());
                koulutus.setKoulutus(koulutusKooste);
                boolean removeSuccess = presenter.removeKoulutus(koulutus);
                presenter.getHakukohdeListView().reload();
                if (removeSuccess) {
                    presenter.showMainDefaultView();
                }
            }
        }, new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeKoulutusCreationDialog();

            }
        });
        tarjontaDialog = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(tarjontaDialog);
    }

    public void closeKoulutusCreationDialog() {
        if (tarjontaDialog != null) {
            getWindow().removeWindow(tarjontaDialog);
        }
    }

    private String getEditViewOid() {
        KoulutusToisenAsteenPerustiedotViewModel model = presenter.getModel().getKoulutusPerustiedotModel();
        return model.getOid();
    }

}
