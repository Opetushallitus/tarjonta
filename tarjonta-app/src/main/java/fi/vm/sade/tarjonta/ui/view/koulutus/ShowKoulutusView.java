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

import java.util.*;

import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusPerustiedotViewModel;
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
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
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

    @Value("${koodisto-uris.lisahaku}")
    private String hakutyyppiLisahakuUrl;

    @Value("${koodisto-uris.erillishaku}")
    private String hakutapaErillishaku;

    private Button poista;

    public ShowKoulutusView(String pageTitle, PageNavigationDTO pageNavigationDTO) {
        super(VerticalLayout.class, pageTitle, null, pageNavigationDTO);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {

        LOG.debug("buildLayout(): hakutyyppi uri={}", KoodistoURI.KOODISTO_HAKUTYYPPI_URI);

        if (presenter == null) {
            presenter = new TarjontaPresenter();
        }

        if (tarjontaUIHelper == null) {
            tarjontaUIHelper = new TarjontaUIHelper();
        }

        layout.removeAllComponents();

        addNavigationButtons(layout, OrganisaatioContext.getContext(presenter.getModel().getTarjoajaModel().getSelectedOrganisationOid()));

        // language tabs
        final TabSheet tabs = new TabSheet();

        final Set<String> languages = new TreeSet<String>();
        languages.addAll(presenter.getModel().getKoulutusLisatiedotModel().getLisatiedot().keySet());
        languages.add(presenter.getModel().getKoulutusPerustiedotModel().getOpetuskieli());
        
        if(languages.isEmpty()) {
            //no languages available, "add" fi 
            presenter.getModel().getKoulutusLisatiedotModel().getLisatiedot(suomiUri);
        }
        
        for (String language : languages) {
            final String[] langParts = tarjontaUIHelper.splitKoodiURI(language);
            final List<KoodiType> koodit = tarjontaUIHelper.getKoodis(langParts[0]);

            ShowKoulutusViewTab tab=null;
            if(koodit.size()>0) {
                tab = new ShowKoulutusViewTab(language,
                    new Locale(koodit.get(0).getKoodiArvo()));
            } else {
                tab = new ShowKoulutusViewTab(language,
                        new Locale(language));
            }
            tabs.addTab(tab, tarjontaUIHelper.getKoodiNimi(language));
        }

        layout.addComponent(tabs);
        enableOrDisableButtonsByHaku();
    }

    private void enableOrDisableButtonsByHaku() {
        if (presenter.getModel().getKoulutusPerustiedotModel() != null && presenter.getModel().getKoulutusPerustiedotModel().getOid() != null) {

            KoulutusPerustiedotViewModel perustiedotViewModel = presenter.getModel().getKoulutusPerustiedotModel();

            HakukohteetVastaus hakukohteetVastaus = presenter.getHakukohteetForKoulutus(perustiedotViewModel.getOid());

            //tarkista hakukophteiden määrä==0
            final boolean canDelete = hakukohteetVastaus.getHakukohteet().size()==0;
            
            if(canDelete) {
                if (poista != null) {
                    poista.setEnabled(false);
                }
            }
        }
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



    	
    	

    	poista = addNavigationButton(T(CommonTranslationKeys.POISTA), new Button.ClickListener() {
    	    private static final long serialVersionUID = 5019806363620874205L;

    	    @Override
    	    public void buttonClick(Button.ClickEvent event) {
    	        showRemoveDialog();

    	    }
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);
    	

      /* Removed because functionality is not yet implemented OVT-4450 */
    /*	final Button kopioiUudeksi = addNavigationButton(T(CommonTranslationKeys.KOPIOI_UUDEKSI), new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {
    			KoulutusKopiointiDialog kopiointiDialog = new KoulutusKopiointiDialog("600px","500px",KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS );
    			getWindow().addWindow(kopiointiDialog);
    		}
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);*/

    	/*
    	final Button siirraOsaksiToista = addNavigationButton(T("siirraOsaksiToistaKoulutusta"), new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {
    			getWindow().showNotification("Ei toteutettu");
    		}
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);*/

    	final Button lisaaToteutus = addNavigationButton(T("lisaaToteutus"), new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {
                    SelectPohjakoulutusVaatimusDialog dialog = new SelectPohjakoulutusVaatimusDialog("280px","180px");
                    ShowKoulutusView.this.getWindow().addWindow(dialog);
    		}
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);
           /* Removed because functionality is not yet implemented OVT-4450 */
    	/*addNavigationButton(T("esikatsele"), new Button.ClickListener() {
    		private static final long serialVersionUID = 5019806363620874205L;

    		@Override
    		public void buttonClick(Button.ClickEvent event) {
    			getWindow().showNotification("Ei toteutettu");
    		}
    	}, StyleEnum.STYLE_BUTTON_PRIMARY);*/

    	//check permissions
    	final TarjontaPermissionServiceImpl permissions = presenter.getPermission();
    	
    	final boolean isDeletePermission = permissions.userCanDeleteKoulutus(context);
    	final boolean deleteVisible = isDeletePermission && presenter.getModel().getKoulutusPerustiedotModel().getKoulutuksenHakukohteet().size()==0;
    	
    	poista.setVisible(TarjontaTila.valueOf(presenter.getModel().getKoulutusPerustiedotModel().getTila()).isRemovable()
    			&& deleteVisible);
    	
//    	kopioiUudeksi.setVisible(permissions.userCanCopyKoulutusAsNew(context));
    	//siirraOsaksiToista.setVisible(permissions.userCanMoveKoulutus(context));
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

    public void addErrorMsg(String msg) {
        getWindow().showNotification(T(msg), Window.Notification.TYPE_ERROR_MESSAGE);
    }

    private void showRemoveDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("removeQ"), "", T("removeYes"), T("removeNo"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeKoulutusCreationDialog();
                boolean removeSuccess = presenter.removeKoulutus(getEditViewOid());
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
