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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.ui.app.AbstractSadeApplication;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.view.HakuRootView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusView;
import fi.vm.sade.vaadin.dto.ButtonDTO;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;

/**
 * Tarjonta WEB application - used for testing and development. For development
 * purposes the app has methods to switch between Haku and Tarjonta management
 * apps.
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class TarjontaWebApplication extends AbstractSadeApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaWebApplication.class);

    private Window window;
    @Value("${tarjonta-app.dev.redirect:}")
    private String developmentRedirect;
    @Value("${tarjonta-app.dev.theme:}")
    private String developmentTheme;

    @Autowired
    private TarjontaAdminService tarjontaAdminService;

    @Autowired
    SimpleCacheManager _cacheManager;

    @Override
    public synchronized void init() {
        super.init();

        window = new Window("Valitse");
        setMainWindow(window);

       developmentConfiguration();

        Button tarjontaButton = new Button("Tarjontaan", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toTarjonta();
            }
        });
        window.addComponent(tarjontaButton);

        Button hakuButton = new Button("Hakuihin", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toHaku();
            }
        });
        window.addComponent(hakuButton);

        Button xxxButton = new Button("Show Koulutus View", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toKoulutusView();
            }
        });

        window.addComponent(xxxButton);

        Button initData = new Button("Luo testidata", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tarjontaAdminService.initSample(new String());
            }
        });

        window.addComponent(initData);

        // KOODISTO CACHE TESTAUSTA

        final VerticalLayout koodistoVl = new VerticalLayout();

        final ComboBox selectKoodistoCb = UiBuilder.comboBox(window, "VALITSE KOODISTO", new String[] {
            KoodistoURIHelper.KOODISTO_AVAINSANAT_URI,
            KoodistoURIHelper.KOODISTO_HAKUKAUSI_URI,
            KoodistoURIHelper.KOODISTO_HAKUKELPOISUUS_VAATIMUKSET_URI,
            KoodistoURIHelper.KOODISTO_HAKUTAPA_URI,
            KoodistoURIHelper.KOODISTO_KIELI_URI,
            KoodistoURIHelper.KOODISTO_KOULUTUSLAJI_URI,
            KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI,
            KoodistoURIHelper.KOODISTO_SUUNNITELTU_KESTO_URI,
            KoodistoURIHelper.KOODISTO_OPPILAITOSTYYPPI_URI,
            KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI,
            KoodistoURIHelper.KOODISTO_KOULUTUS_URI,
            KoodistoURIHelper.KOODISTO_KOULUTUKSEN_ALKAMISKAUSI_URI,
        });

        Button addNewKoodistoComponentBtn = new Button("Lisää uusi koodisto härdelli", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                LOG.info("adding...");
                for (String cacheName : _cacheManager.getCacheNames()) {
                    Cache cache = _cacheManager.getCache(cacheName);
                    LOG.info("Cache: {}", cacheName);
                    LOG.info("  native cahce = {}", cache.getNativeCache());
                }

                final KoodistoComponent kc = UiBuilder.koodistoComboBox(null, "" + selectKoodistoCb.getValue());
                // UiBuilder.koodistoTwinColSelectUri(null, "" + selectKoodistoCb.getValue());
                koodistoVl.addComponent(kc, 0);

                kc.setImmediate(true);
                kc.addListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        LOG.info(kc.getCaption() + ", values: {}", event.getProperty().getValue());
                    }
                });
            }
        });

        window.addComponent(addNewKoodistoComponentBtn);

        Button evictCachesBtn = new Button("Evict cache", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                LOG.info("evict cacges...");
                for (String cacheName : _cacheManager.getCacheNames()) {
                    LOG.info("  Cache: {}", cacheName);
                    Cache cache = _cacheManager.getCache(cacheName);
                    cache.clear();

                    koodistoVl.removeAllComponents();
                }
            }
        });
        window.addComponent(evictCachesBtn);

        window.addComponent(koodistoVl);

    }

    public void toTarjonta() {
        this.removeWindow(window);
        window = new TarjontaRootView();
        setMainWindow(window);
    }

    public void toHaku() {
        this.removeWindow(window);
        window = new HakuRootView(this);
        setMainWindow(window);
    }

    public void toKoulutusView() {
        this.removeWindow(window);

        window = new Window();
        setMainWindow(window);

        ButtonDTO prev = new ButtonDTO("< EDELLINEN (XXX)", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                LOG.info("PREV!");
            }
        });
        ButtonDTO next = new ButtonDTO("(XXX) SEURAAVA >", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                LOG.info("NEXT!");
            }
        });


        PageNavigationDTO pageNavigationDTO = new PageNavigationDTO(prev, next, "42/43");
        ShowKoulutusView view  = new ShowKoulutusView("PAGE TITLE", pageNavigationDTO);

        window.addComponent(view);
    }


    /*
     * Development configurations, no real use in production environment.
     */
    private void developmentConfiguration(){
         if (developmentTheme != null && developmentTheme.length() > 0) {
            //set a development theme.
            setTheme(developmentTheme);
        }

        if (developmentRedirect != null && developmentRedirect.length() > 0) {
            //This code block is only for making UI development little bit faster
            //Add the property to tarjonta-app.properties:
            //
            //tarjonta-app.dev.redirect=KOULUTUS

            if (developmentRedirect.equalsIgnoreCase("HAKU")) {
                toHaku();
            }

            if (developmentRedirect.equalsIgnoreCase("KOULUTUS") || developmentRedirect.equalsIgnoreCase("TARJONTA")) {
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
