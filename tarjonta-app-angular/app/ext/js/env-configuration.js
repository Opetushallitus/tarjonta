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
        "authentication-service.rest.url": "https://itest-virkailija.oph.ware.fi:443/authentication-service/resources/",
        "koodi-uri.koulutuslaji.nuortenKoulutus": "koulutuslaji_n",
        "koodi-uri.lukio.pohjakoulutusvaatimus": "peruskoulu",
        "koodi.public.rest.address": "https://itest-virkailija.oph.ware.fi:443/koodi/rest/",
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
        "koodisto-uris.opetusmuoto": "opetusmuoto",
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
        "koodisto-uris.tutkinto": "koulutus",
        "koodisto-uris.tutkintonimike": "tutkintonimikkeet",
        "koodisto-uris.valintakokeentyyppi": "valintakokeentyyppi",
        "koodisto-uris.valintaperustekuvausryhma": "valintaperustekuvausryhma",
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
        //käsin lisätyt:

        // "tarjontaRestUrlPrefix": "https://itest-virkailija.oph.ware.fi/tarjonta-service/rest/",

        // "tarjontaRestUrlPrefix" : "http://localhost:8084/tarjonta-service/rest/",
        // "tarjontaLocalisationRestUrl" : "http://localhost:8084/tarjonta-service/rest/localisation/",

        "tarjontaRestUrlPrefix" : "http://luokka.hard.ware.fi:8302/tarjonta-service/rest/",
        "tarjontaLocalisationRestUrl" : "http://luokka.hard.ware.fi:8302/tarjonta-service/rest/localisation/",

        "place" : "holder"
    }
};