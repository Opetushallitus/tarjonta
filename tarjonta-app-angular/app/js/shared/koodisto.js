/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */


var app = angular.module('Koodisto', ['ngResource', 'config', 'TarjontaCache', 'Logging']);

app.factory('Koodisto', function($resource, $log, $q, Config, CacheService) {
    $log = $log.getInstance("Koodisto");

    var host = Config.env["tarjontaKoodistoRestUrlPrefix"];

    var nimiWithLocale = function(locale, metadata) {
        var metas = _.select(metadata, function(koodiMetaData) {
            locale = locale || "fi"; // default locale is finnish

            if (koodiMetaData.kieli.toLowerCase() === locale.toLowerCase()) {

                return koodiMetaData.nimi;
            }
        });

        if (metas.length === 1 && metas.length > 0) {
            return metas[0].nimi;
        } else {
            return "";
        }
    };


    /*
     This JS-object is view representation of koodisto koodi.
     Example koodisto Koodi:
     
     {
     koodiArvo :"",
     koodiUri  : "",
     koodiTila : "",
     koodiVersio : "",
     koodiKoodisto : "",
     koodiOrganisaatioOid :"",
     -> Koodinimi is localized with given locale
     koodiNimi : ""
     }
     
     */

    var getKoodiViewModelFromKoodi = function(koodi, locale) {
        var tarjontaKoodi = {
            koodiArvo: koodi.koodiArvo,
            koodiUri: koodi.koodiUri,
            koodiTila: koodi.tila,
            koodiVersio: koodi.versio,
            koodiKoodisto: koodi.koodisto.koodistoUri,
            koodiOrganisaatioOid: koodi.koodisto.organisaatioOid,
            koodiNimi: nimiWithLocale(locale, koodi.metadata)
        };
        return tarjontaKoodi;
    };

    return {
        /*
         @param {array} array of koodis received from Koodisto.
         @param {string} locale in which koodi name should be shown
         @returns {array} array of koodi view model objects
         */

        convertKoodistoKoodiToViewModelKoodi: function(koodisParam, locale) {

            var koodis = [];

            angular.forEach(koodisParam, function(koodi) {
                koodis.push(getKoodiViewModelFromKoodi(koodi, locale));
            });
            return koodis;
        },
        /*
         @param {string} koodistouri from which koodis should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {promise} return promise which contains array of koodi view models
         */

        getYlapuolisetKoodit: function(koodiUriParam, locale) {

            $log.info('getYlapuolisetKoodit called with : ' + koodiUriParam + ' locale : ' + locale);

            var returnYlapuoliKoodis = $q.defer();

            var returnKoodis = [];

            var ylapuoliKoodiUri = host + 'relaatio/sisaltyy-ylakoodit/:koodiUri';

            $resource(ylapuoliKoodiUri, {koodiUri: '@koodiUri'}, {cache: true}).query({koodiUri: koodiUriParam}, function(koodis) {
                angular.forEach(koodis, function(koodi) {

                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                });
                returnYlapuoliKoodis.resolve(returnKoodis);
            });


            return  returnYlapuoliKoodis.promise;

        },
        getAlapuolisetKoodit: function(koodiUriParam, locale) {

            $log.info('getAlapuolisetKoodi called with : ' + koodiUriParam + ' locale : ' + locale);

            var returnYlapuoliKoodis = $q.defer();

            var returnKoodis = [];

            var ylapuoliKoodiUri = host + 'relaatio/sisaltyy-alakoodit/:koodiUri';

            $resource(ylapuoliKoodiUri, {koodiUri: '@koodiUri'}, {cache: true}).query({koodiUri: koodiUriParam}, function(koodis) {
                angular.forEach(koodis, function(koodi) {

                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                });
                returnYlapuoliKoodis.resolve(returnKoodis);
            });


            return  returnYlapuoliKoodis.promise;

        },
        /**
         * koodiUriList palauttaa alapuoliset koodiurit jotka ovat tiettyä tyyppiä (tai kaikki jos ei määritelty)
         * returns object {
         *      uris : [], 
         *      map : {uri-key : koodi-value}
         * }
         */
        getAlapuolisetKoodiUrit: function(koodiUriList, tyyppi, locale) {
            var deferred = $q.defer();
            var result = {
                uris: [],
                map: {}
            };
            var promises = [];

            var uri = host + 'relaatio/sisaltyy-alakoodit/';

            for (var i = 0; i < koodiUriList.length; i++) {

                var koodiUri = koodiUriList[i];
                if (koodiUri.indexOf("#") != -1) {
                    koodiUri = koodiUri.substring(0, koodiUri.indexOf("#"));
                }

                var promise = $resource(uri + koodiUri, {}, {get: {method: "GET", isArray: true}, cache: true}).get().$promise.then(function(koodis) {
//              console.log("alapuoliset:", koodis);
                    angular.forEach(koodis, function(koodi) {
                        if (!tyyppi || koodi.koodisto.koodistoUri === tyyppi) {
                            result.uris.push(koodi.koodiUri);
                            if (angular.isDefined(locale)) {
                                result.map[koodi.koodiUri] = getKoodiViewModelFromKoodi(koodi, locale);
                            }
                        }
                    });
                });
                promises.push(promise);
            }

            $q.all(promises).then(function() {
                deferred.resolve(result);
            });

            return  deferred.promise;

        },
        /**
         * koodiUriList palauttaa alapuoliset koodiurit jotka ovat tiettyä tyyppiä (tai kaikki jos ei määritelty)
         * returns object {
         *      uris : [], 
         *      map : {uri-key : koodi-value}
         * }
         */
        getYlapuolisetKoodiUrit: function(koodiUriList, tyyppi, locale) {

//        $log.info('getYlapuolisetKoodiUrit called with : ' , koodiUriList, tyyppi);

            var deferred = $q.defer();
            var result = {
                uris: [],
                map: {}
            };
            var promises = [];

            var uri = host + 'relaatio/sisaltyy-ylakoodit/';

            for (var i = 0; i < koodiUriList.length; i++) {

                var koodiUri = koodiUriList[i];
                if (koodiUri.indexOf("#") != -1) {
                    koodiUri = koodiUri.substring(0, koodiUri.indexOf("#"));
                }

                var promise = $resource(uri + koodiUri, {}, {get: {method: "GET", isArray: true}, cache: true}).get().$promise.then(function(koodis) {
//            console.log("ylapuoliset:", koodis);
                    angular.forEach(koodis, function(koodi) {
                        if (!tyyppi || koodi.koodisto.koodistoUri === tyyppi) {
                            result.uris.push(koodi.koodiUri);
                            if (angular.isDefined(locale)) {
                                result.map[koodi.koodiUri] = getKoodiViewModelFromKoodi(koodi, locale);
                            }
                        }
                    });
                });
                promises.push(promise);
            }

            $q.all(promises).then(function() {
                deferred.resolve(result);
            });

            return  deferred.promise;

        },
        /*
         @param {string} koodistouri from which koodis should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {promise} return promise which contains array of koodi view models
         */

        getAllKoodisWithKoodiUri: function(koodistoUriParam, locale, includePassiivises) {

            $log.info('getAllKoodisWithKoodiUri called with ' + koodistoUriParam + ' ' + locale);

            return CacheService.lookup("koodisto/" + koodistoUriParam + "/" + locale, function(returnKoodisPromise) {

                var includePassive = false;
                if (includePassiivises !== undefined) {
                    includePassive = includePassiivises;
                }
                var passiivinenTila = "PASSIIVINEN";
                var returnKoodis = [];
                var koodiUri = host + ':koodistoUri/koodi';

                $resource(koodiUri, {koodistoUri: '@koodistoUri'}, {cache: true}).query({koodistoUri: koodistoUriParam}, function(koodis) {
                    angular.forEach(koodis, function(koodi) {

                        if (includePassive) {
                            returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                        } else {
                            if (koodi.tila !== passiivinenTila) {
                                returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                            }

                        }

                    });
                    returnKoodisPromise.resolve(returnKoodis);
                });

            });

        },
        /*
         @param {string} koodisto URI from which koodis should be retrieved
         @param {string} koodi URI from which koodi should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {array} array of koodisto view model objects
         */
        getKoodi: function(koodistoUriParam, koodiUriParam, locale) {
            var returnKoodi = $q.defer();
            var koodiUri = host + ":koodistoUri/koodi/:koodiUri";
            //console.log('Calling getKoodistoWithKoodiUri with : ' + koodistoUriParam + '/koodi/'+ koodiUriParam +' ' + locale);

            $resource(koodiUri, {koodistoUri: '@koodistoUri', koodiUri: '@koodiUri'}, {cache: true}).get({koodistoUri: koodistoUriParam, koodiUri: koodiUriParam}, function(koodi) {
                returnKoodi.resolve(getKoodiViewModelFromKoodi(koodi, locale));
            });
            // console.log('Returning promise from getKoodistoWithKoodiUri');
            return returnKoodi.promise;
        },
        searchKoodi: function(koodiUri, locale) {
        	if (!koodiUri) {
        		var ret = $q.defer();
        		ret.resolve(null);
        		return ret.promise;
        	}
        	
            locale = locale || "fi"; // default locale is finnish

            if (koodiUri.indexOf('#') != -1) {
                koodiUri = koodiUri.substring(0, koodiUri.indexOf('#'));
            }
            
            return CacheService.lookup("koodi/" + koodiUri + "/" + locale, function(ret) {

                //var ret = $q.defer();
                 //          https://itest-virkailija.oph.ware.fi/koodisto-service/rest/json/searchKoodis?koodiUris=haunkohdejoukko_11

                var resourceUrl = host + "searchKoodis";
                $resource(resourceUrl, {}, {'get': {method: 'GET', isArray: true}, cache: true}).get({koodiUris: koodiUri}, function(result) {
                    for (var i = 0; i < result.length; i++) {
                        var koodi = result[i];
                        var metadatas = koodi.metadata;
                        var nimi = {};
                        for (var j = 0; j < metadatas.length; j++) {
                            var metadata = metadatas[j];
                            nimi[metadata.kieli] = metadata.nimi;
                        }
                        ret.resolve(nimi[locale.toUpperCase()] || nimi.FI || nimi.EN || nimi.SV); //fallback

                    }

                });

            });


            //return ret.promise;
        }

    };

});


/**
 * KoodistoURI
 */
app.factory('KoodistoURI', function($log, Config) {
    $log = $log.getInstance("KoodistoURI");

    var getConfigWithDefault = function(envKey, defaultValue) {
        var result = (angular.isDefined(Config.env[envKey])) ? Config.env[envKey] : defaultValue;

        if (!angular.isDefined(result) || result == "") {
            $log.warn("EMPTY koodisto data value for key: " + envKey);
        }
        
        $log.debug("getConfigWithDefault()", envKey, defaultValue, result);
        return result;
    };
    
    function isEmpty(v) {
        return !angular.isDefined(v) || !v;
    }
    
    /**
     * Compares two koodis. Return true if they are consireder to be equal.
     * 
     * @see TarjontaKoodistoHelperTest#testKoodistoUri_compare_versions() test to see how this works. 
     */
    var compareKoodi = function(sourceKoodi, targetKoodi, ignoreVersions) {
        if (isEmpty(sourceKoodi) && isEmpty(targetKoodi)) {
            return true;
        }

        if (isEmpty(sourceKoodi) || isEmpty(targetKoodi)) {
            return false;
        }
        
        ignoreVersions = angular.isDefined(ignoreVersions) ? ignoreVersions : false;
        
        // Use version comparison IFF requested AND sourceKoodi has version information
        var useVersions = koodiHasVersion(sourceKoodi) && !ignoreVersions;
        
        var source = sourceKoodi;
        var target = targetKoodi;

        if (!useVersions) {
            // Use only koodi part, no version information used
            source = splitKoodiToKoodiAndVersion(source)[0];
            target = splitKoodiToKoodiAndVersion(target)[0];
        }

        return (source === target);
    };

    /**
     * @param koodi
     * @return true if koodi is not null and has version information (contains "#" character).
     */
    var koodiHasVersion = function(koodi) {
        return (!isEmpty(koodi) != null && koodi.indexOf("#") > 0);
    };
    
    
   /**
     * Split koodi to "koodi" and version strings
     * 
     * <pre>
     * null -- "", ""
     * kieli_fi -- "kieli_fi", ""
     * kieli_fi#123 -- "kieli_fi", "123"
     * </pre>
     * 
     * @param koodi
     * @return 
     */
    var splitKoodiToKoodiAndVersion = function(koodi) {
        var result = [];
        
        if (koodi == null) {
            result.push("");
            result.push("");
            return result;
        }

        var tmp = koodi.split("#");
        
        if (tmp.length >= 1) {
            result.push(tmp[0]);
        } else {
            result.push("");
        }

        if (tmp.length >= 2) {
            result.push(tmp[1]);
        } else {
            result.push("");
        }

        return result;
    };

    return {
        HAKUTYYPPI_LISAHAKU: getConfigWithDefault("koodisto-uris.lisahaku", ""),
        HAKUTAPA_YHTEISHAKU: getConfigWithDefault("koodisto-uris.yhteishaku", ""),
        HAKUTAPA_JATKUVAHAKU: getConfigWithDefault("koodisto.hakutapa.jatkuvaHaku.uri", ""),
        HAUNKOHDEJOUKKO_KK: getConfigWithDefault("haku.kohdejoukko.kk.uri", ""),
        KOODI_LANG_FI_URI: getConfigWithDefault("koodisto.language.fi.uri", "kieli_fi"),
        KOODI_LANG_EN_URI: getConfigWithDefault("koodisto.language.en.uri", "kieli_en"),
        KOODI_LANG_SV_URI: getConfigWithDefault("koodisto.language.sv.uri", "kieli_sv"),
        KOODISTO_HAKUTAPA_URI: getConfigWithDefault("koodisto-uris.hakutapa", ""),
        KOODISTO_KIELI_URI: getConfigWithDefault("koodisto-uris.kieli", ""),
        KOODISTO_HAKUKOHDE_URI: getConfigWithDefault("koodisto-uris.hakukohde", ""),
        KOODISTO_SUUNNITELTU_KESTO_URI: getConfigWithDefault("koodisto-uris.suunniteltuKesto", ""),
        KOODISTO_KOULUTUSLAJI_URI: getConfigWithDefault("koodisto-uris.koulutuslaji", ""),
        KOODISTO_LIITTEEN_TYYPPI_URI: getConfigWithDefault("koodisto-uris.liitteentyyppi", ""),
        /*
         * Organization navi URIs
         */
        KOODISTO_OPPILAITOSTYYPPI_URI: getConfigWithDefault("koodisto-uris.oppilaitostyyppi", ""),
        /*
         * Top search area URIs
         */
        KOODISTO_ALKAMISKAUSI_URI: getConfigWithDefault("koodisto-uris.alkamiskausi", ""),
        KOODISTO_HAKUTYYPPI_URI: getConfigWithDefault("koodisto-uris.hakutyyppi", ""),
        KOODISTO_HAUN_KOHDEJOUKKO_URI: getConfigWithDefault("koodisto-uris.haunKohdejoukko", ""),
        /*
         * KOMO URIs
         */
        KOODISTO_TUTKINTO_URI: getConfigWithDefault("koodisto-uris.koulutus", ""),
        KOODISTO_TUTKINTO_NIMI_URI: getConfigWithDefault("koodisto-uris.tutkinto", ""),
        KOODISTO_KOULUTUSOHJELMA_URI: getConfigWithDefault("koodisto-uris.koulutusohjelma", ""),
        KOODISTO_KOULUTUSASTE_URI: getConfigWithDefault("koodisto-uris.koulutusaste", ""),
        KOODISTO_KOULUTUSALA_URI: getConfigWithDefault("koodisto-uris.koulutusala", ""),
        KOODISTO_TUTKINTONIMIKE_URI: getConfigWithDefault("koodisto-uris.tutkintonimike", ""),
        KOODISTO_OPINTOALA_URI: getConfigWithDefault("koodisto-uris.opintoala", ""),
        KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI: getConfigWithDefault("koodisto-uris.opintojenLaajuusyksikko", ""),
        KOODISTO_OPINTOJEN_LAAJUUSARVO_URI: getConfigWithDefault("koodisto-uris.opintojenLaajuusarvo", ""),
        KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI: getConfigWithDefault("koodisto-uris.pohjakoulutusvaatimus", ""),
        KOODISTO_EQF_LUOKITUS_URI: getConfigWithDefault("koodisto-uris.eqf-luokitus", ""),
        /*
         * KOMOTO URIs
         */
        KOODISTO_AMMATTINIMIKKEET_URI: getConfigWithDefault("koodisto-uris.ammattinimikkeet", ""),
        KOODISTO_OPETUSMUOTO_URI: getConfigWithDefault("koodisto-uris.opetusmuoto", ""),
        KOODISTO_POSTINUMERO_URI: getConfigWithDefault("koodisto-uris.postinumero", ""),
        /*
         * Valintaperustekuvaus URIs
         */
        KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI: getConfigWithDefault("koodisto-uris.valintaperustekuvausryhma", ""),
        KOODISTO_SORA_KUVAUSRYHMA_URI: getConfigWithDefault("koodisto-uris.sorakuvausryhma", ""),
        /*
         * Lukiotutkinto URIs
         */
        KOODISTO_LUKIOLINJA_URI: getConfigWithDefault("koodisto-uris.lukiolinja", ""),
        KOODI_KOULUTUSLAJI_NUORTEN_KOULUTUS_URI: getConfigWithDefault("koodi-uri.koulutuslaji.nuortenKoulutus", ""),
        LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI: getConfigWithDefault("koodi-uri.lukio.pohjakoulutusvaatimus", ""),
        KOODISTO_LUKIODIPLOMIT_URI: getConfigWithDefault("koodisto-uris.lukiodiplomit", ""),
        /**
         * Oppiaineet
         */
        KOODISTO_OPPIAINEET_URI: getConfigWithDefault("koodisto-uris.oppiaineet", ""),
        /**
         * Hakukohde / valintakoe
         */
        KOODISTO_VALINTAKOE_TYYPPI_URI: getConfigWithDefault("koodisto-uris.valintakokeentyyppi", ""),
        /*
         * For tutkinto dialog
         */
        /*
         * For korkeakoulu
         */
        KOODISTO_TEEMAT_URI: getConfigWithDefault("koodisto-uris.teemat", ""),
        KOODISTO_HAKUKELPOISUUSVAATIMUS_URI: getConfigWithDefault("koodisto-uris.hakukelpoisuusvaatimus", ""),
        KOODISTO_POHJAKOULUTUSVAATIMUKSET_KORKEAKOULU_URI: getConfigWithDefault("koodisto-uris.pohjakoulutusvaatimus_kk", ""),
        KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI: getConfigWithDefault("koodisto-uris.tutkintonimike_kk", ""),
        /*
         * For tutkinto dialog
         */
        KOODISTO_TARJONTA_KOULUTUSTYYPPI: getConfigWithDefault("koodisto-uris.tarjontakoulutustyyppi", ""),
        KOODI_LISAHAKU_URI: getConfigWithDefault("koodisto-uris.lisahaku", "hakutyyppi_03#1"),
        
        // hmmm. typo? "kodisto"
        KOODI_YKSILOLLISTETTY_PERUSOPETUS_URI: getConfigWithDefault("kodisto-uris.yksilollistettyPerusopetus", "pohjakoulutusvaatimustoinenaste_er"),
        KOODI_YHTEISHAKU_URI: getConfigWithDefault("koodisto-uris.yhteishaku", "hakutapa_01#1"),
        KOODI_ERILLISHAKU_URI: getConfigWithDefault("koodisto-uris.erillishaku", "hakutapa_02#1"),
        KOODI_HAASTATTELU_URI: getConfigWithDefault("koodisto-uris.valintakoeHaastattelu", "valintakokeentyyppi_6#1"),
        KOODI_TODISTUKSET_URI: getConfigWithDefault("koodisto-uris.liiteTodistukset", "liitetyypitamm_3#1"),
        KOODI_KOHDEJOUKKO_ERITYISOPETUS_URI: getConfigWithDefault("koodisto-uris.kohdejoukkoErityisopetus", "haunkohdejoukko_15#1"),
        KOODI_KOHDEJOUKKO_VALMENTAVA_URI: getConfigWithDefault("koodisto-uris.valmentavaKuntouttava", "haunkohdejoukko_16#1"),
        KOODI_KOHDEJOUKKO_AMMATILLINEN_LUKIO_URI: getConfigWithDefault("koodisto-uris.ammatillinenLukio", "haunkohdejoukko_11#1"),
        KOODI_POHJAKOULUTUS_PERUSKOULU_URI: getConfigWithDefault("koodisto-uris.pohjakoulutusPeruskoulu", "pohjakoulutusvaatimustoinenaste_pk#1"),
        KOODI_KOHDEJOUKKO_VALMISTAVA_URI: getConfigWithDefault("koodisto-uris.valmistavaOpetus", "haunkohdejoukko_17#1"),
        KOODI_KOHDEJOUKKO_VAPAASIVISTYS_URI: getConfigWithDefault("koodisto-uris.vapaaSivistys", "haunkohdejoukko_18#1"),
        
        //
        // Functionality
        //
        splitKoodiToKoodiAndVersion : splitKoodiToKoodiAndVersion,
        koodiHasVersion : koodiHasVersion,
        compareKoodi : compareKoodi,
        
        foo: "bar"
    };

});
