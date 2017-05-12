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
package fi.vm.sade.tarjonta.rest.bean;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.rest.dto.JsonConfigObject;
import fi.vm.sade.tarjonta.rest.helper.PropertyPlaceholder;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/")
public class JsonConfiguration {
    private static String configurationJson;

    private static final List<String> WHITELIST = Arrays.asList(
            "callerid.tarjonta.tarjonta-app.frontend",
            "callerid.tarjonta.tarjonta-service.backend",
            "casUrl",
            "koodi-uri.koulutuslaji.nuortenKoulutus",
            "koodi-uri.lukio.pohjakoulutusvaatimus",
            "koodi-uri.ontutkinto",
            "koodisto-uris.aiheet",
            "koodisto-uris.alkamiskausi",
            "koodisto-uris.ammatillinenLukio",
            "koodisto-uris.ammattinimikkeet",
            "koodisto-uris.arvo",
            "koodisto-uris.eqf-luokitus",
            "koodisto-uris.erillishaku",
            "koodisto-uris.hakukausi",
            "koodisto-uris.hakukelpoisuusvaatimus",
            "koodisto-uris.hakukohde",
            "koodisto-uris.hakutapa",
            "koodisto-uris.hakutyyppi",
            "koodisto-uris.haunKohdejoukko",
            "koodisto-uris.jatkuvahaku",
            "koodisto-uris.kieli",
            "koodisto-uris.kohdejoukkoErityisopetus",
            "koodisto-uris.koulutuksenAlkamisvuosi",
            "koodisto-uris.koulutus",
            "koodisto-uris.koulutusala",
            "koodisto-uris.koulutusaste",
            "koodisto-uris.koulutuslaji",
            "koodisto-uris.koulutusohjelma",
            "koodisto-uris.liiteTodistukset",
            "koodisto-uris.liitteentyyppi",
            "koodisto-uris.lisahaku",
            "koodisto-uris.lukiodiplomit",
            "koodisto-uris.lukiolinja",
            "koodisto-uris.opetusaika",
            "koodisto-uris.opetusmuoto",
            "koodisto-uris.opetusmuotokk",
            "koodisto-uris.opetuspaikka",
            "koodisto-uris.opintoala",
            "koodisto-uris.opintojenLaajuusarvo",
            "koodisto-uris.opintojenLaajuusyksikko",
            "koodisto-uris.oppiaineet",
            "koodisto-uris.oppilaitostyyppi",
            "koodisto-uris.osaamisala",
            "koodisto-uris.pohjakoulutusPeruskoulu",
            "koodisto-uris.pohjakoulutusvaatimus",
            "koodisto-uris.pohjakoulutusvaatimus_er",
            "koodisto-uris.pohjakoulutusvaatimus_kk",
            "koodisto-uris.postinumero",
            "koodisto-uris.sorakuvausryhma",
            "koodisto-uris.suunniteltuKesto",
            "koodisto-uris.tarjontakoulutustyyppi",
            "koodisto-uris.teemat",
            "koodisto-uris.tutkinto",
            "koodisto-uris.tutkintonimike",
            "koodisto-uris.tutkintonimike_kk",
            "koodisto-uris.tutkintoonjohtavakoulutus",
            "koodisto-uris.valintakoeHaastattelu",
            "koodisto-uris.valintakokeentyyppi",
            "koodisto-uris.valintaperustekuvausryhma",
            "koodisto-uris.valmentavaKuntouttava",
            "koodisto-uris.valmistavaOpetus",
            "koodisto-uris.vapaaSivistys",
            "koodisto-uris.yhteishaku",
            "koodisto.hakutapa.jatkuvaHaku.uri",
            "koodisto.lang.en.uri",
            "koodisto.lang.fi.uri",
            "koodisto.lang.sv.uri",
            "koodisto.public.webservice.url.backend",
            "koodisto.suomi.uri",
            "oid.rest.url.backend",
            "organisaatio.api.rest.url",
            "root.organisaatio.oid",
            "tarjonta-app.identifier",
            "tarjonta.admin.webservice.url.backend",
            "tarjonta.koulutusaste.korkeakoulut",
            "tarjonta.public.webservice.url.backend",
            "tarjonta.showUnderConstruction",
            "tarjonta.solr.baseurl",
            "tarjontaKoodistoRestUrlPrefix",
            "tarjontaLocalisationRestUrl",
            "tarjontaOhjausparametritRestUrlPrefix",
            "tarjontaRestUrlPrefix",
            "ui.timeout.long",
            "ui.timeout.short",
            "web.url.oppija",
            "web.url.oppija.preview");

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String help() {
        return "<p>"
                + "Output configuration JSON object : <environment><a href=\"json/env-configuration.json\">/kk/ext/json/env-configuration.json</a>.<br/>"
                + "Output configuration JSON object in JavaScript variable : <environment>/<a href=\"js/env-configuration.js\">/kk/ext/js/env-configuration.js</a>."
                + "</p>";
    }

    @RequestMapping(value = "/json/env-configuration.json", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getEnvJsonConfig() {
        if (configurationJson == null) {
            final Properties properties = PropertyPlaceholder.getProperties();
            configurationJson = createJsonConfiguration(properties);
        }

        return configurationJson;
    }

    @RequestMapping(value = "/js/env-configuration.js", method = RequestMethod.GET, produces = "text/javascript")
    @ResponseBody
    public String getEnvConfigurationJsFile() {
        if (configurationJson == null) {
            final Properties properties = PropertyPlaceholder.getProperties();
            configurationJson = createJsonConfiguration(properties);
        }

        return "window.CONFIG = " + configurationJson + ";";
    }

    private String createJsonConfiguration(Properties properties) {
        List<JsonConfigObject> jsons = Lists.<JsonConfigObject>newLinkedList();
        for (final String key : whitelistedKeys(properties)) {
            jsons.add(new JsonConfigObject(key, PropertyPlaceholder.getProperty(key)));
        }

        Collections.sort(jsons, new Comparator<JsonConfigObject>() {
            @Override
            public int compare(JsonConfigObject a, JsonConfigObject b) {
                int ret = a.getKey().compareTo(b.getKey());
                return ret != 0 ? ret : a.getKey().compareTo(b.getKey());
            }
        });

        StringBuilder outputJson = new StringBuilder();
        outputJson.append("{ \"env\" : {");
        outputJson.append(StringUtils.join(jsons.toArray(), ","));
        outputJson.append("}}");
        return outputJson.toString();
    }

    private Set<String> whitelistedKeys(Properties propertyes) {
        Set<String> keysNeeded = new HashSet<>();
        for (Object o : propertyes.keySet()) {
            String key = (String) o;
            if (WHITELIST.contains(key)) {
                keysNeeded.add(key);
            }
        }
        return keysNeeded;
    }
}
