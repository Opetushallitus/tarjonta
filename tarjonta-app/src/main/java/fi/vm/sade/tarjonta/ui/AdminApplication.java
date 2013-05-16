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

import com.github.wolfie.blackboard.Blackboard;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.ui.loader.xls.TarjontaKomoData;
import fi.vm.sade.vaadin.Oph;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/*
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class AdminApplication extends AbstractWebApplication {

    private static final long serialVersionUID = 4593403338621758659L;
    private static final Logger LOG = LoggerFactory.getLogger(AdminApplication.class);
    private Window window;
    @Autowired
    private TarjontaKomoData tarjontaKomoData;
    @Value("${tarjonta.public.webservice.url.backend}")
    private String tarjontaBackendUrl;
    private HttpClient httpClient = new HttpClient();

    @Override
    public void initApplication() {
        window = new Window("Admin window");
        setMainWindow(window);
        setTheme(Oph.THEME_NAME);

        HorizontalLayout hl = new HorizontalLayout();
        window.addComponent(hl);

        Button btnKomo = new Button("Luo kaikki komot", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    LOG.debug("Luo komot event");
                    tarjontaKomoData.preLoadAllKoodistot();
                    tarjontaKomoData.createData(true);
                } catch (Exception ex) {
                    LOG.error("Failed to create KOMOs", ex);
                }
            }
        });
        hl.addComponent(btnKomo);

        Button btnKomoTest = new Button("Testaa komon luonti", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    LOG.debug("Luo komot event");
                    tarjontaKomoData.preLoadAllKoodistot();
                    tarjontaKomoData.createData(false);
                } catch (Exception ex) {
                    LOG.error("Failed to create KOMOs", ex);
                }
            }
        });

        hl.addComponent(btnKomoTest);

        final Button btnIndexKoulutukset = new Button("Indeksoi koulutukset", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                String urlString = tarjontaBackendUrl.substring(0, tarjontaBackendUrl.indexOf("/services")) + "/rest/indexer/koulutukset?clear=true";
                try {
                    LOG.debug("Indeksoi koulutukset: {}", urlString);

                    GetMethod get = new GetMethod(urlString);
                    httpClient.executeMethod(get);
                    String responseContent = new String(get.getResponseBodyAsString());
                    LOG.debug("Indeksoi koulutukset done:{}", responseContent);
                } catch (Throwable ex) {
                    LOG.error("Failed to index koulutukset", ex);
                }
            }
        });

        hl.addComponent(btnIndexKoulutukset);
    }

}
