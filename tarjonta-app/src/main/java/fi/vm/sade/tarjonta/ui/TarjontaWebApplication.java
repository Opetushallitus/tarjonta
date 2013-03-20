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
package fi.vm.sade.tarjonta.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.loader.xls.TarjontaKomoData;
import fi.vm.sade.tarjonta.ui.view.HakuRootView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.ValintaperustekuvausRootView;
import fi.vm.sade.tarjonta.ui.view.koulutus.lukio.EditLukioKoulutusPerustiedotView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/**
 * Tarjonta WEB application - used for testing and development. For development
 * purposes the app has methods to switch between Haku and Tarjonta management
 * apps.
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class TarjontaWebApplication extends TarjontaApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaWebApplication.class);
    private static final long serialVersionUID = 7402559260126333807L;
    private Window window;
    @Value("${tarjonta-app.dev.redirect:}")
    private String developmentRedirect;
    @Value("${tarjonta-app.dev.theme:}")
    private String developmentTheme;
    @Autowired
    private TarjontaAdminService tarjontaAdminService;
    @Autowired
    private TarjontaKomoData tarjontaKomoData;

    @Override
    protected void initApplication() {

        window = new Window("Valitse");
        setMainWindow(window);

        developmentConfiguration();
        HorizontalLayout hl = new HorizontalLayout();
        window.addComponent(hl);

        final Button tarjontaButton = new Button("Tarjontaan", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                toTarjonta();
            }
        });
        hl.addComponent(tarjontaButton);

        final Button hakuButton = new Button("Hakuihin", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                toHaku();
            }
        });
        hl.addComponent(hakuButton);

        final Button valitaButton = new Button("Valintaperustekuvaus", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                toValintaperustekuvaus();
            }
        });
        hl.addComponent(valitaButton);


        final Button btnKomo = new Button("Luo kaikki komot", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    LOG.debug("Luo komot event");
                    tarjontaKomoData.loadKoodistot();
                    tarjontaKomoData.createData(true);
                } catch (Exception ex) {
                    LOG.error("Failed to create KOMOs", ex);
                }
            }
        });
        hl.addComponent(btnKomo);

        final Button btnKomoTest = new Button("Testaa komon luonti", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    LOG.debug("Luo komot event");
                    tarjontaKomoData.loadKoodistot();
                    tarjontaKomoData.createData(false);
                } catch (Exception ex) {
                    LOG.error("Failed to create KOMOs", ex);
                }
            }
        });

        hl.addComponent(btnKomoTest);

        final Button lukiokoulutus = new Button("Luo lukiokoulutus", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                toLukiokoulutus();
            }
        });

        hl.addComponent(lukiokoulutus);
    }

    public void toTarjonta() {
        this.removeWindow(window);
        window = new TarjontaRootView();
        setMainWindow(window);
    }

    public void toHaku() {
        this.removeWindow(window);
        window = new HakuRootView();
        setMainWindow(window);
    }

    public void toValintaperustekuvaus() {
        this.removeWindow(window);
        window = new ValintaperustekuvausRootView();
        setMainWindow(window);
    }

    public void toLukiokoulutus() {
        this.removeWindow(window);
        TarjontaRootView e = new TarjontaRootView();
        setMainWindow(e);
        e.getTarjontaPresenter().getLukioPresenter().showEditLukioKoulutusPerustiedotView(null, KoulutusActiveTab.PERUSTIEDOT);
        window = e;      
    }

    /*
     * Development configurations, no real use in production environment.
     */
    private void developmentConfiguration() {
        if (developmentTheme != null && developmentTheme.length() > 0) {
            //set a development theme.
            setTheme(developmentTheme);
        }

        if (developmentRedirect != null && developmentRedirect.length() > 0) {
            //This code block is only for making UI development little bit faster
            //Add the property to tarjonta-app.properties:
            //
            //tarjonta-app.dev.redirect=KOULUTUS

            if (developmentRedirect.equalsIgnoreCase("VALINTA")) {
                toValintaperustekuvaus();
            } else if (developmentRedirect.equalsIgnoreCase("HAKU")) {
                toHaku();
            } else if (developmentRedirect.equalsIgnoreCase("KOULUTUS") || developmentRedirect.equalsIgnoreCase("TARJONTA")) {
                toTarjonta();
            }
        }
    }

    /**
     * @return the tarjontaAdminService
     */
    public TarjontaAdminService getTarjontaAdminService() {
        return tarjontaAdminService;
    }

    /**
     * @param tarjontaAdminService the tarjontaAdminService to set
     */
    public void setTarjontaAdminService(TarjontaAdminService tarjontaAdminService) {
        this.tarjontaAdminService = tarjontaAdminService;
    }
}