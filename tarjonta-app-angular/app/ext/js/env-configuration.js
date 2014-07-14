/*
 * Help:
 * Add service factory(/js/shared/config.js) to your module.
 * Module name : 'config'.
 * Factory name : 'Config'.
 *
 * FAQ:
 * How to get an environment variable by a key: <factory-object>.env[<string-key>].
 * How to get AngularJS application variable by a key: <factory-object>.app[<string-key>].
 *
 * Example:
 * cfg.env["koodi-uri.koulutuslaji.nuortenKoulutus"];
 * result value : "koulutuslaji_n"
 */
window.CONFIG = {
    "env": {
        "koodi-uri.koulutuslaji.nuortenKoulutus": "koulutuslaji_n",
        "koodi-uri.lukio.pohjakoulutusvaatimus": "peruskoulu",
        "koodi.public.rest.address": "https://itest-virkailija.oph.ware.fi:443/koodi/rest/",
        "koodisto-uris.aiheet": "aiheet",
        "koodisto-uris.alkamiskausi": "kausi",
        "koodisto-uris.ammattinimikkeet": "ammattiluokitus",
        "koodisto-uris.hakukausi": "kausi",
        "koodisto-uris.hakukelpoisuusvaatimus": "hakukelpoisuusvaatimusta",
        "koodisto-uris.hakukohde": "hakukohteet",
        "koodisto-uris.hakutapa": "hakutapa",
        "koodisto-uris.hakutyyppi": "hakutyyppi",
        "koodisto-uris.haunKohdejoukko": "haunkohdejoukko",
        "koodisto-uris.kieli": "kieli",
        "koodisto-uris.koulutuksenAlkamisvuosi": "kausi",
        "koodisto-uris.koulutusala": "koulutusalaoph2002",
        "koodisto-uris.koulutusaste": "koulutusasteoph2002",
        "koodisto-uris.koulutuslaji": "koulutuslaji",
        "koodisto-uris.koulutusohjelma": "koulutusohjelmaamm",
        "koodisto-uris.liitteentyyppi": "liitetyypitamm",
        "koodisto-uris.lisahaku": "hakutyyppi_03#1",
        "koodisto-uris.lukiodiplomit": "lukiodiplomit",
        "koodisto-uris.lukiolinja": "lukiolinjat",
        "koodisto-uris.opetusmuotokk": "opetusmuotokk",
        "koodisto-uris.opetusmuoto": "opetusmuoto",
        "koodisto-uris.koulutuslaji": "koulutuslaji",
        "koodisto-uris.opetusaika": "opetusaikakk",
        "koodisto-uris.opetuspaikka": "opetuspaikkakk",
        "koodisto-uris.opintoala": "opintoalaoph2002",
        "koodisto-uris.opintojenLaajuusarvo": "opintojenlaajuus",
        "koodisto-uris.opintojenLaajuusyksikko": "opintojenlaajuusyksikko",
        "koodisto-uris.oppiaineet": "painotettavatoppiaineetlukiossa",
        "koodisto-uris.oppilaitostyyppi": "oppilaitostyyppi",
        "koodisto-uris.pohjakoulutusvaatimus": "pohjakoulutusvaatimustoinenaste",
        "koodisto-uris.postinumero": "posti",
        "koodisto-uris.sorakuvausryhma": "sorakuvaus",
        "koodisto-uris.suunniteltuKesto": "suunniteltukesto",
        "koodisto-uris.tarjontakoulutustyyppi": "koulutustyyppi",
        "koodisto-uris.teemat": "teemat",
        "koodisto-uris.tutkinto": "tutkinto",
        "koodisto-uris.koulutus": "koulutus",
        "koodisto-uris.tutkintonimike": "tutkintonimikkeet",
        "koodisto-uris.valintakokeentyyppi": "valintakokeentyyppi",
        "koodisto-uris.valintaperustekuvausryhma": "valintaperustekuvausryhma",
        "koodisto-uris.tutkintonimike_kk": "tutkintonimike_kk",
        "koodisto-uris.pohjakoulutusvaatimus_kk": "pohjakoulutusvaatimuskorkeakoulut",
        "koodisto-uris.eqf-luokitus": "eqf",
        "koodisto-uris.yhteishaku": "hakutapa_01#1",
        "koodisto.lang.en.uri": "kieli_en",
        "koodisto.lang.fi.uri": "kieli_fi",
        "koodisto.lang.sv.uri": "kieli_sv",
        "koodisto.public.rest.address": "https://itest-virkailija.oph.ware.fi:443/koodisto/rest/",
        "oid.rest.url.backend": "https://itest-virkailija.oph.ware.fi:443/oid-service/rest/oid",
        "organisaatio.api.rest.url": "https://itest-virkailija.oph.ware.fi:443/organisaatio-service/rest/",
        "root.organisaatio.oid": "1.2.246.562.10.00000000001",
        "tarjonta.admin.webservice.url.backend": "https://itest-virkailija.oph.ware.fi:443/tarjonta-service/services/tarjontaAdminService",
        "tarjonta.koulutusaste.korkeakoulut": "60,61,62,63,70,71,72,73,80,81,82,90",
        "tarjonta.public.webservice.url.backend": "https://itest-virkailija.oph.ware.fi:443/tarjonta-service/services/tarjontaPublicService",
        "tarjonta.showUnderConstruction": "true",
        "tarjonta.solr.baseurl": "http://luokka.hard.ware.fi:8312/solr",
        "valintalaskentakoostepalvelu.tarjonta.rest.url": "https://itest-virkailija.oph.ware.fi:443/tarjonta-service/rest/",
        "host.base-uri": "https://itest-virkailija.oph.ware.fi",
        "haku.uri": "/tarjonta-service/rest/haku",
        "haku.uri.findall": "/tarjonta-service/rest/haku/findAll",
        "haku.kohdejoukko.kk.uri": "haunkohdejoukko_12#1",
        "haku.kohdejoukko.aiku.uri" : "haunkohdejoukko_10#1",
        "haku.hakutapa.erillishaku.uri" : "hakutapa_02",
        "haku.hakutapa.jatkuvahaku.uri" : "hakutapa_03",
        "root.organisaatio.oid":"1.2.246.562.10.00000000001",
        "ui.timeout.short": 10000,
        "ui.timeout.long": 60000,
        "web.url.oppija.preview" : "https://itest-oppija.oph.ware.fi/app/preview.html#!/korkeakoulu/",
        "koodisto-uris.osaamisala": "osaamisala",

        //
        // Uri to "Jatkuva Haku" - UI changes according to this
        //
        "koodisto.hakutapa.jatkuvaHaku.uri" : "hakutapa_03#1",

        //käsin lisätyt:

        //
        // REMEMBER!
        //
        // If you add something here it is ONLY for development - to really setup deployment time
        // properties put them in to "tarjonta-app.properties" and/or
        // module-install-parent/config/common/tarjonta/oph-configuration/tarjonta-app.properties
        // (also check the tarjonta-rest.properties so that the new property will be included to the Angular APP...)
        //

        "tarjontaRestUrlPrefix": "https://itest-virkailija.oph.ware.fi/tarjonta-service/rest/v1/",

        // "tarjontaRestUrlPrefix": "http://luokka.hard.ware.fi:8302/tarjonta-service/rest/v1/",
        // "tarjontaRestUrlPrefix": "http://luokka.hard.ware.fi:8302/tarjonta-service/rest/v1/",
        //"tarjontaRestUrlPrefix": "http://localhost:8585/tarjonta-service/rest/v1/",
        //"tarjontaRestUrlPrefix" : "http://localhost:8084/tarjonta-service/rest/v1/",
         //"tarjontaRestUrlPrefix": "http://localhost:9090/tarjonta-service/rest/v1/",

        //"tarjontaLocalisationRestUrl": "https://itest-virkailija.oph.ware.fi/lokalisointi/cxf/rest/v1/localisation",
       // "tarjontaLocalisationRestUrl": "http://localhost:8319/lokalisointi/cxf/rest/v1/localisation",
         "tarjontaLocalisationRestUrl": "https://itest-virkailija.oph.ware.fi/lokalisointi/cxf/rest/v1/localisation",


        // "tarjontaKoodistoRestUrlPrefix" : "/koodisto-service/rest/json/",
        "tarjontaKoodistoRestUrlPrefix" : "https://itest-virkailija.oph.ware.fi/koodisto-service/rest/json/",

        // NOTE: seems to be koodisto-uris.opintojenLaajuusarvo ???
        "koodistoUriOpintojenLaajuus": "opintojenlaajuus",

        // Only for development, since production reads "/cas/myroles"
        // "casUrl": "cas_me_akuankka.json",
        "casUrl": "cas_me_ophadmin.json",
        //"casUrl": "cas_me_juomas.json",
        // "casUrl" : "https://itest-virkailija.oph.ware.fi/cas/myroles",

        // "authentication-service.rest.url":"https://itest-virkailija.oph.ware.fi/authentication-service/resources/",
         "authentication-service.rest.url":"http://localhost:8308/authentication-service/resources/",
        // henkilöhaku - CORS ongelma, siksi ohjattu tarjontaan - toimii "oikeassa" ympäristössä
       //"authentication-service.henkilo.rest.url":"https://itest-virkailija.oph.ware.fi/authentication-service/resources/henkilo",
        "authentication-service.henkilo.rest.url": "http://localhost:8308/authentication-service/resources/henkilo",

        // Ohjausparametrit
        "tarjontaOhjausparametritRestUrlPrefix":"https://itest-virkailija.oph.ware.fi/ohjausparametrit-service",
        // "tarjontaOhjausparametritRestUrlPrefix":"http://localhost:8085/ohjausparametrit-service",

        "place": "holder"


    }
};
