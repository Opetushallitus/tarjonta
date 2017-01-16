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
var app = angular.module('Koodisto', [
    'ngResource',
    'config',
    'TarjontaCache',
    'Logging'
]);
app.factory('Koodisto', function($resource, $log, $q, Config, CacheService, AuthService) {
    var plainUrls = window.urls().noEncode();
    $log = $log.getInstance('Koodisto');
    var host = Config.env.tarjontaKoodistoRestUrlPrefix;

    function getLocale(locale) {
        return locale || AuthService.getLanguage();
    }

    var nimiWithLocale = function(locale, metadata) {
        var metas = _.select(metadata, function(koodiMetaData) {
            locale = locale || 'fi';
            // default locale is finnish
            if (koodiMetaData.kieli.toLowerCase() === locale.toLowerCase()) {
                return koodiMetaData.nimi;
            }
        });
        if (metas.length === 1 && metas.length > 0) {
            return metas[0].nimi;
        }
        else {
            return '';
        }
    };
    function removeVersion(koodiUri) {
        if (koodiUri.indexOf('#') != -1) {
            console.log('removing version from:', koodiUri);
            koodiUri = koodiUri.substring(0, koodiUri.indexOf('#'));
        }
        return koodiUri;
    }
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
    var checkKoodiValidity = function(koodi, locale) {
        var koodiValid = true;
        if (koodi && koodi.voimassaLoppuPvm) {
            var endDate = null;
            var currentDate = new Date();
            currentDate.setHours(0, 0, 0, 0);
            try {
                var loppuPvmParts = koodi.voimassaLoppuPvm.split('-');
                endDate = new Date(loppuPvmParts[0], loppuPvmParts[1] - 1, loppuPvmParts[2] - 1);
            }
            catch (e) {
                $log.warn('koodin ' + koodi.koodiUri + ' voimassaLoppuPvm ' +
                    JSON.stringify(koodi.voimassaLoppuPvm) + ' ei voitu konvertoida');
            }
            if (endDate && endDate < currentDate) {
                koodiValid = false;
            }
        }
        return koodiValid;
    };

    /**
     * Palauta vain uusin versio jokaisesta koodista ja filtteröi
     * pois vanhentuneet koodit
     * @param {Array} koodis
     * @returns {Array}
     */
    function rejectOldKoodis(koodis) {
        var map = {};
        _.each(koodis, function(koodi) {
            var sameKoodi = map[koodi.koodiUri];
            if (!sameKoodi || sameKoodi.versio < koodi.versio) {
                map[koodi.koodiUri] = koodi;
            }
        });
        return _.chain(map).toArray().filter(checkKoodiValidity).value();
    }
    return {
        /*
         * Utility for checking active Koodisto codes.
         *
         * Use the functions:
         * - filterKoodis([]);
         * - filterKoodisByKoodistoUri([], 'koodisto_uri');
         */
        versionUtil: function() {
            //DISABLE OR ENABLE STATUS OF LUONNOS
            var ALLOW_LUONNOS = true;
            var END_SUFFIX = 'T23:59:59';
            return {
                _inRange: function(d, start, end) {
                    return start <= d && d <= end || start <= d && end === null;
                },
                _isKoodiActive: function(dateNow, koodi) {
                    return koodi.voimassaAlkuPvm !== null && this._inRange(dateNow, new Date(koodi.voimassaAlkuPvm),
                            koodi.voimassaLoppuPvm === null ? null : new Date(koodi.voimassaLoppuPvm + END_SUFFIX));
                },
                _isKoodiApproved: function(koodi, showLuonnos) {
                    return showLuonnos && koodi.tila === 'LUONNOS' || koodi.tila === 'HYVAKSYTTY';
                },
                _filtterKoodiToMap: function(dateNow, mapFiltteredKoodis, koodi, showLuonnos) {
                    if (this._isKoodiActive(dateNow, koodi)) {
                        //is correct status with optional luonnos
                        if (angular.isDefined(mapFiltteredKoodis[koodi.koodiUri]) &&
                            this._isKoodiApproved(koodi, showLuonnos)) {
                            if (koodi.versio > mapFiltteredKoodis[koodi.koodiUri].versio) {
                                //override by latest version
                                mapFiltteredKoodis[koodi.koodiUri] = koodi;
                            }
                        }
                        else if (this._isKoodiApproved(koodi, showLuonnos)) {
                            //first valid item in the map, do not care koodi version
                            mapFiltteredKoodis[koodi.koodiUri] = koodi;
                        }
                    }
                },
                _filterKoodiByTyyppiToMap: function(dateNow, map, koodis, koodistoUri, showLuonnos) {
                    if (koodis === null || angular.isUndefined(koodis) || !angular.isArray(koodis)) {
                        throw new Error('Tarjonta application error - invalid koodi array object! ' + koodis);
                    }
                    for (var i = 0; i < koodis.length; i++) {
                        //is active and on date range
                        if (!koodistoUri || koodis[i].koodisto.koodistoUri === koodistoUri) {
                            //is active and on date range
                            this._filtterKoodiToMap(dateNow, map, koodis[i], showLuonnos);
                        }
                    }
                },
                convertToResultMap: function(resultMap, mapUris, locale) {
                    if (Object.keys(mapUris).length > 0) {
                        for (var key in mapUris) {
                            var modelKoodi = getKoodiViewModelFromKoodi(mapUris[key], locale);
                            resultMap.uris.push(key);
                            resultMap.map[key] = modelKoodi;
                        }
                    }
                },
                /*
                 * Poistaa passivoidut ja vanhentuneet koodit, jos
                 * samaa koodia on useampia palauttaa funktio
                 * vain uusimmat version.
                 *
                 * @param {[KoodistoKoodiObj, KoodistoKoodiObj...]} koodis
                 * @returns {keyUri : {KoodistoKoodiObj}}
                 */
                filterKoodis: function(koodis) {
                    var map = {};
                    //key = uri, value= koodisto koodi object
                    if (koodis !== null && angular.isDefined(koodis) && angular.isArray(koodis)) {
                        this._filterKoodiByTyyppiToMap(new Date(), map, koodis, false, ALLOW_LUONNOS);
                    }
                    return map;
                },
                /*
                 * Poistaa passivoidut ja vanhentuneet koodit, jos
                 * samaa koodia on useampia palauttaa funktio
                 * vain uusimmat version. Fitterinä koodiston uri.
                 *
                 * @param {[KoodistoKoodiObj, KoodistoKoodiObj...]} koodis
                 * @param "koodisto_uri" koodistoUri
                 * @returns {keyUri : {KoodistoKoodiObj}}
                 */
                filterKoodisByKoodistoUri: function(koodis, koodistoUri) {
                    if (koodistoUri === null || angular.isUndefined(koodistoUri) || koodistoUri.length === 0) {
                        new Error('Tarjonta application error - null koodisto uri filtter!');
                    }
                    var map = {};
                    //key = uri, value= koodisto koodi object
                    if (koodis !== null && angular.isDefined(koodis) && angular.isArray(koodis)) {
                        this._filterKoodiByTyyppiToMap(new Date(), map, koodis, koodistoUri, ALLOW_LUONNOS);
                    }
                    return map;
                }
            };
        },
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
            locale = getLocale(locale);
            var returnYlapuoliKoodis = $q.defer();
            var returnKoodis = [];
            $resource(plainUrls.url("koodisto-service.ylakoodi", ":koodiUri"), {
                koodiUri: '@koodiUri'
            }, {
                    cache: true
                }).query({
                koodiUri: koodiUriParam
            }, function(koodis) {
                koodis = rejectOldKoodis(koodis);
                angular.forEach(koodis, function(koodi) {
                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                });
                returnYlapuoliKoodis.resolve(returnKoodis);
            });
            return returnYlapuoliKoodis.promise;
        },
        getAlapuolisetKoodit: function(koodiUriParam, locale) {
            $log.info('getAlapuolisetKoodi called with : ' + koodiUriParam + ' locale : ' + locale);
            locale = getLocale(locale);
            var returnYlapuoliKoodis = $q.defer();
            var returnKoodis = [];
            $resource(plainUrls.url("koodisto-service.alakoodi", ":koodiUri"), {
                koodiUri: '@koodiUri'
            }, {
                    cache: true
                }).query({
                koodiUri: koodiUriParam
            }, function(koodis) {
                koodis = rejectOldKoodis(koodis);
                angular.forEach(koodis, function(koodi) {
                    returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                });
                returnYlapuoliKoodis.resolve(returnKoodis);
            });
            return returnYlapuoliKoodis.promise;
        },
        /**
         * koodiUriList palauttaa alapuoliset koodiurit jotka ovat tiettyä tyyppiä (tai kaikki jos ei määritelty)
         * returns object {
         *      uris : [],
         *      map : {uri-key : koodi-value}
         * }
         */
        getAlapuolisetKoodiUrit: function(koodiUriList, tyyppi, locale) {
            locale = getLocale(locale);
            var deferred = $q.defer();
            var result = {
                uris: [],
                map: {}
            };
            var promises = [];
            var that = this;
            _.each(koodiUriList, function(koodiUri) {
                koodiUri = oph.removeKoodiVersion(koodiUri);
                var vu = that.versionUtil();
                var promise = $resource(window.url("koodisto-service.alakoodi", koodiUri), {}, {
                    get: {
                        method: 'GET',
                        isArray: true
                    },
                    cache: true
                }).get().$promise.then(function(koodis) {
                    koodis = rejectOldKoodis(koodis);
                    vu.convertToResultMap(result, vu.filterKoodisByKoodistoUri(koodis, tyyppi), locale);
                });
                promises.push(promise);
            });
            $q.all(promises).then(function() {
                deferred.resolve(result);
            });
            return deferred.promise;
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
            locale = getLocale(locale);
            var deferred = $q.defer();
            var result = {
                uris: [],
                map: {}
            };
            var promises = [];
            var vu = this.versionUtil();
            _.each(koodiUriList, function(koodiUri) {
                if (koodiUri.indexOf('#') != -1) {
                    koodiUri = koodiUri.substring(0, koodiUri.indexOf('#'));
                }
                var promise = $resource(window.url("koodisto-service.ylakoodi", koodiUri), {}, {
                    get: {
                        method: 'GET',
                        isArray: true
                    },
                    cache: true
                }).get().$promise.then(function(koodis) {
                    koodis = rejectOldKoodis(koodis);
                    vu.convertToResultMap(result, vu.filterKoodisByKoodistoUri(koodis, tyyppi), locale);
                });
                promises.push(promise);
            });
            $q.all(promises).then(function() {
                deferred.resolve(result);
            });
            return deferred.promise;
        },
        /*
         @param {string} koodistouri from which koodis should be retrieved
         @param {string} locale in which koodi name should be shown
         @returns {promise} return promise which contains array of koodi view models
         */
        getAllKoodisWithKoodiUri: function(koodistoUriParam, locale, includePassiivises) {
            $log.info('getAllKoodisWithKoodiUri called with ' + koodistoUriParam + ' ' + locale);
            locale = getLocale(locale);
            return CacheService.lookup('koodisto/' + koodistoUriParam + '/' + locale, function(returnKoodisPromise) {
                var includePassive = false;
                if (includePassiivises !== undefined) {
                    includePassive = includePassiivises;
                }
                var passiivinenTila = 'PASSIIVINEN';
                var returnKoodis = [];
                var useUrl = plainUrls.url("koodisto-service.koodi",":koodistoUri");
                var koodistoArvo = "";
                var prms = {
                    koodistoUri: '@koodistoUri'
                    // ,      koodistoArvo: '@koodistoArvo'
                };

                // haetaan kk:lle rajattu fasetti koodistosta eikä kaikkia
                if(koodistoUriParam == "koulutustyyppifasetti"){
                    useUrl = plainUrls.url("koodisto-service.arvo", ":koodistoUri", ":koodistoArvo");
                    koodistoArvo = "et01.05";
                    prms = {
                        koodistoUri: '@koodistoUri',
                        koodistoArvo: '@koodistoArvo'
                    };
                }

                $resource(useUrl, prms, {
                    cache: true
                }).query({
                    koodistoUri: koodistoUriParam,
                    koodistoArvo: koodistoArvo
                }, function(koodis) {
                    koodis = rejectOldKoodis(koodis);
                    angular.forEach(koodis, function(koodi) {
                        if (includePassive) {
                            returnKoodis.push(getKoodiViewModelFromKoodi(koodi, locale));
                        }
                        else {
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
            koodiUriParam = removeVersion(koodiUriParam);
            locale = getLocale(locale);
            var returnKoodi = $q.defer();
            //console.log('Calling getKoodistoWithKoodiUri with : ' + koodistoUriParam + '/koodi/'+ koodiUriParam +' ' + locale);
            $resource(plainUrls.url("koodisto-service.koodiInKoodisto",":koodistoUri", ":koodiUri"), {
                koodistoUri: '@koodistoUri',
                koodiUri: '@koodiUri'
            }, {
                    cache: true
                }).get({
                koodistoUri: koodistoUriParam,
                koodiUri: koodiUriParam.split('#')[0]
            }, function(koodi) {
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
            locale = getLocale(locale);
            // default locale is finnish
            koodiUri = removeVersion(koodiUri);
            return CacheService.lookup('koodi/' + koodiUri + '/' + locale, function(ret) {
                //var ret = $q.defer();
                //          https://itest-virkailija.oph.ware.fi/koodisto-service/rest/json/searchKoodis?koodiUris=haunkohdejoukko_11
                $resource(window.url("koodisto-service.search"), {}, {
                    'get': {
                        method: 'GET',
                        isArray: true
                    },
                    cache: true
                }).get({
                    koodiUris: koodiUri
                }, function(result) {
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
            }); //return ret.promise;
        }
    };
});
/**
 * KoodistoURI
 */
app.factory('KoodistoURI', function($log, Config) {
    $log = $log.getInstance('KoodistoURI');
    var getConfigWithDefault = function(envKey, defaultValue) {
        var result = angular.isDefined(Config.env[envKey]) ? Config.env[envKey] : defaultValue;
        if (!angular.isDefined(result) || result === '') {
            $log.warn('EMPTY koodisto data value for key: ' + envKey);
        }
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
        return source === target;
    };
    /**
    * @param {type} koodi
    * @return true if koodi is not null and has version information (contains "#" character).
    */
    var koodiHasVersion = function(koodi) {
        return koodi && koodi.indexOf('#') !== -1;
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
    * @param {type} koodi
    * @return
    */
    var splitKoodiToKoodiAndVersion = function(koodi) {
        var result = [];
        if (koodi === null) {
            result.push('');
            result.push('');
            return result;
        }
        var tmp = koodi.split('#');
        if (tmp.length >= 1) {
            result.push(tmp[0]);
        }
        else {
            result.push('');
        }
        if (tmp.length >= 2) {
            result.push(tmp[1]);
        }
        else {
            result.push('');
        }
        return result;
    };
    return {
        HAKUTYYPPI_LISAHAKU: getConfigWithDefault('koodisto-uris.lisahaku', ''),
        HAKUTAPA_YHTEISHAKU: getConfigWithDefault('koodisto-uris.yhteishaku', ''),
        HAKUTAPA_JATKUVAHAKU: getConfigWithDefault('koodisto.hakutapa.jatkuvaHaku.uri', ''),
        KOODI_LANG_FI_URI: getConfigWithDefault('koodisto.language.fi.uri', 'kieli_fi'),
        KOODI_LANG_EN_URI: getConfigWithDefault('koodisto.language.en.uri', 'kieli_en'),
        KOODI_LANG_SV_URI: getConfigWithDefault('koodisto.language.sv.uri', 'kieli_sv'),
        KOODISTO_HAKUTAPA_URI: getConfigWithDefault('koodisto-uris.hakutapa', ''),
        KOODISTO_KIELI_URI: getConfigWithDefault('koodisto-uris.kieli', ''),
        KOODISTO_HAKUKOHDE_URI: getConfigWithDefault('koodisto-uris.hakukohde', ''),
        KOODISTO_SUUNNITELTU_KESTO_URI: getConfigWithDefault('koodisto-uris.suunniteltuKesto', ''),
        KOODISTO_KOULUTUSLAJI_URI: getConfigWithDefault('koodisto-uris.koulutuslaji', ''),
        KOODISTO_LIITTEEN_TYYPPI_URI: getConfigWithDefault('koodisto-uris.liitteentyyppi', ''),
        /*
         * Organization navi URIs
         */
        KOODISTO_OPPILAITOSTYYPPI_URI: getConfigWithDefault('koodisto-uris.oppilaitostyyppi', ''),
        /*
         * Top search area URIs
         */
        KOODISTO_ALKAMISKAUSI_URI: getConfigWithDefault('koodisto-uris.alkamiskausi', ''),
        KOODISTO_HAKUTYYPPI_URI: getConfigWithDefault('koodisto-uris.hakutyyppi', ''),
        KOODISTO_HAUN_KOHDEJOUKKO_URI: getConfigWithDefault('koodisto-uris.haunKohdejoukko', ''),
        /*
         * KOMO URIs
         */
        KOODISTO_TUTKINTO_URI: getConfigWithDefault('koodisto-uris.koulutus', ''),
        KOODISTO_TUTKINTO_NIMI_URI: getConfigWithDefault('koodisto-uris.tutkinto', ''),
        KOODISTO_KOULUTUSOHJELMA_URI: getConfigWithDefault('koodisto-uris.koulutusohjelma', ''),
        KOODISTO_KOULUTUSASTE_URI: getConfigWithDefault('koodisto-uris.koulutusaste', ''),
        KOODISTO_KOULUTUSALA_URI: getConfigWithDefault('koodisto-uris.koulutusala', ''),
        KOODISTO_TUTKINTONIMIKE_URI: getConfigWithDefault('koodisto-uris.tutkintonimike', ''),
        KOODISTO_OPINTOALA_URI: getConfigWithDefault('koodisto-uris.opintoala', ''),
        KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI: getConfigWithDefault('koodisto-uris.opintojenLaajuusyksikko', ''),
        KOODISTO_OPINTOJEN_LAAJUUSARVO_URI: getConfigWithDefault('koodisto-uris.opintojenLaajuusarvo', ''),
        KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI: getConfigWithDefault('koodisto-uris.pohjakoulutusvaatimus', ''),
        KOODISTO_KOULUTUKSENLAAJUUS_URI: getConfigWithDefault('koodisto-uris.arvo', ''),
        KOODISTO_EQF_LUOKITUS_URI: getConfigWithDefault('koodisto-uris.eqf-luokitus', ''),
        /*
         * KOMOTO URIs
         */
        KOODISTO_AMMATTINIMIKKEET_URI: getConfigWithDefault('koodisto-uris.ammattinimikkeet', ''),
        KOODISTO_OPETUSMUOTO_URI: getConfigWithDefault('koodisto-uris.opetusmuoto', ''),
        KOODISTO_POSTINUMERO_URI: getConfigWithDefault('koodisto-uris.postinumero', ''),
        /*
         * Valintaperustekuvaus URIs
         */
        KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI: getConfigWithDefault('koodisto-uris.valintaperustekuvausryhma', ''),
        KOODISTO_SORA_KUVAUSRYHMA_URI: getConfigWithDefault('koodisto-uris.sorakuvausryhma', ''),
        /*
         * Lukiotutkinto URIs
         */
        KOODISTO_LUKIOLINJA_URI: getConfigWithDefault('koodisto-uris.lukiolinja', ''),
        KOODI_KOULUTUSLAJI_NUORTEN_KOULUTUS_URI: getConfigWithDefault('koodi-uri.koulutuslaji.nuortenKoulutus', ''),
        LUKIO_KOODI_POHJAKOULUTUSVAATIMUS_URI: getConfigWithDefault('koodi-uri.lukio.pohjakoulutusvaatimus', ''),
        KOODISTO_LUKIODIPLOMIT_URI: getConfigWithDefault('koodisto-uris.lukiodiplomit', ''),
        /**
         * Oppiaineet
         */
        KOODISTO_OPPIAINEET_URI: getConfigWithDefault('koodisto-uris.oppiaineet', ''),
        /**
         * Hakukohde / valintakoe
         */
        KOODISTO_VALINTAKOE_TYYPPI_URI: getConfigWithDefault('koodisto-uris.valintakokeentyyppi', ''),
        /*
         * For tutkinto dialog
         */
        /*
         * For korkeakoulu
         */
        KOODISTO_TEEMAT_URI: getConfigWithDefault('koodisto-uris.teemat', ''),
        KOODISTO_HAKUKELPOISUUSVAATIMUS_URI: getConfigWithDefault('koodisto-uris.hakukelpoisuusvaatimus', ''),
        KOODISTO_POHJAKOULUTUSVAATIMUKSET_KORKEAKOULU_URI:
            getConfigWithDefault('koodisto-uris.pohjakoulutusvaatimus_kk', ''),
        KOODISTO_TUTKINTONIMIKE_KORKEAKOULU_URI: getConfigWithDefault('koodisto-uris.tutkintonimike_kk', ''),
        /*
         * For tutkinto dialog
         */
        KOODISTO_TARJONTA_KOULUTUSTYYPPI: getConfigWithDefault('koodisto-uris.tarjontakoulutustyyppi', ''),
        KOODI_LISAHAKU_URI: getConfigWithDefault('koodisto-uris.lisahaku', 'hakutyyppi_03#1'),
        // hmmm. typo? "kodisto"
        KOODI_YKSILOLLISTETTY_PERUSOPETUS_URI:
            getConfigWithDefault('kodisto-uris.yksilollistettyPerusopetus', 'pohjakoulutusvaatimustoinenaste_er'),
        KOODI_YHTEISHAKU_URI: getConfigWithDefault('koodisto-uris.yhteishaku', 'hakutapa_01#1'),
        KOODI_ERILLISHAKU_URI: getConfigWithDefault('koodisto-uris.erillishaku', 'hakutapa_02#1'),
        KOODI_HAASTATTELU_URI: getConfigWithDefault('koodisto-uris.valintakoeHaastattelu', 'valintakokeentyyppi_6#1'),
        KOODI_TODISTUKSET_URI: getConfigWithDefault('koodisto-uris.liiteTodistukset', 'liitetyypitamm_3#1'),
        KOODI_KOHDEJOUKKO_ERITYISOPETUS_URI:
            getConfigWithDefault('koodisto-uris.kohdejoukkoErityisopetus', 'haunkohdejoukko_15#1'),
        KOODI_KOHDEJOUKKO_VALMENTAVA_URI:
            getConfigWithDefault('koodisto-uris.valmentavaKuntouttava', 'haunkohdejoukko_16#1'),
        KOODI_KOHDEJOUKKO_AMMATILLINEN_LUKIO_URI:
            getConfigWithDefault('koodisto-uris.ammatillinenLukio', 'haunkohdejoukko_11#1'),
        KOODI_POHJAKOULUTUS_PERUSKOULU_URI:
            getConfigWithDefault('koodisto-uris.pohjakoulutusPeruskoulu', 'pohjakoulutusvaatimustoinenaste_pk#1'),
        KOODI_KOHDEJOUKKO_VALMISTAVA_URI:
            getConfigWithDefault('koodisto-uris.valmistavaOpetus', 'haunkohdejoukko_17#1'),
        KOODI_KOHDEJOUKKO_VAPAASIVISTYS_URI:
            getConfigWithDefault('koodisto-uris.vapaaSivistys', 'haunkohdejoukko_18#1'),
        //
        // Functionality
        //
        splitKoodiToKoodiAndVersion: splitKoodiToKoodiAndVersion,
        koodiHasVersion: koodiHasVersion,
        compareKoodi: compareKoodi,
        foo: 'bar'
    };
});