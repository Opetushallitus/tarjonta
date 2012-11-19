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
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.view.HakuRootView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.koulutus.EditKoulutusLisatiedotForm;
import fi.vm.sade.tarjonta.ui.view.koulutus.EditKoulutusPainotusFormView;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.support.SimpleCacheManager;

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
    private Window window;
    @Value("${tarjonta-app.dev.redirect:}")
    private String developmentRedirect;
    @Value("${tarjonta-app.dev.theme:}")
    private String developmentTheme;
    @Autowired
    private TarjontaAdminService tarjontaAdminService;
    @Autowired
    SimpleCacheManager _cacheManager;
    @Autowired
    private KoulutusKoodistoConverter converter;
    
    @Override
    protected void initApplication() {
        window = new Window("Valitse");
        setMainWindow(window);
        
        developmentConfiguration();
        HorizontalLayout hl = new HorizontalLayout();
        window.addComponent(hl);
        
        Button tarjontaButton = new Button("Tarjontaan", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toTarjonta();
            }
        });
        hl.addComponent(tarjontaButton);
        
        Button hakuButton = new Button("Hakuihin", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toHaku();
            }
        });
        hl.addComponent(hakuButton);
        
        Button xxxButton = new Button("Koulutuksen kuvailevat tiedot muokkaaminen", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                toKoulutusView();
            }
        });
        
        hl.addComponent(xxxButton);
        
//        window.addComponent(test());
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
    
    public void toKoulutusView() {
        this.removeWindow(window);
        
        window = new Window();
        setMainWindow(window);
        EditKoulutusLisatiedotForm view = new EditKoulutusLisatiedotForm();
        window.addComponent(view);
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
    
//    private EditKoulutusPainotusFormView test() {
////        KoulutusToisenAsteenPerustiedotViewModel koulutusToisenAsteenPerustiedotViewModel = new KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus.NEW);
////        koulutusToisenAsteenPerustiedotViewModel.setPainotus(new ArrayList<KielikaannosViewModel>());
////        return new EditKoulutusPainotusFormView(koulutusToisenAsteenPerustiedotViewModel);
//
//        
////        LOG.debug("SEARCH KOODISTO");
////        List<KoulutuskoodiModel> listaaKoulutus = converter.listaaKoulutukset(new Locale("fi"));
////
////        for (KoulutuskoodiModel m : listaaKoulutus) {
////            LOG.debug("Koodisto URI : " + m.getKoodistoUri());
////            LOG.debug("Koodisto uri versio : " + m.getKoodistoUriVersio());
////            LOG.debug("Koodisto koodi : " + m.getKoodi());
////            LOG.debug("Koodisto : " + m);
////        }
//    }
}
