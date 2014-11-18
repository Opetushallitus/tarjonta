/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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

'use strict';

var app = angular.module('Haku', ['ngResource', 'config', 'Logging']);

app.constant('HAKUTAPA', {
    "YHTEISHAKU": "hakutapa_01",
    "ERILLISHAKU": "hakutapa_02",
    "JATKUVA_HAKU": "hakutapa_03"
});

app.constant('HAKUTYYPPI', {
    "VARSINAINEN_HAKU": "hakutyyppi_01",
    "LISAHAKU": "hakutyyppi_03"
});

app.factory('HakuService', function ($http, $q, Config, $log) {

    $log = $log.getInstance("HakuService");

    return {
        getAllHakus: function (params) {
            var hakuPromise = $q.defer();
            var hakuUri = Config.env.tarjontaRestUrlPrefix + "haku/find";

            params = params || {
                addHakukohdes: false
            };

            $http({method: 'GET', url: hakuUri, params: params}).success(function (data, status, headers, config) {
                // this callback will be called asynchronously

                hakuPromise.resolve(data.result);
                // when the response is available
            }).error(function (data, status, headers, config) {

                $log.debug('ERROR OCCURRED GETTING HAKUS: ', status);
                // called asynchronously if an error occurs
                // or server returns response with an error status.
            });

            return hakuPromise.promise;
        },
        getHakuWithOid: function (oid) {
            var hakuPromise = $q.defer();

            var hakuOidUri = Config.env.tarjontaRestUrlPrefix + "haku/" + oid;

            $http({method: 'GET', url: hakuOidUri}).success(function (data, status, headers, config) {

                hakuPromise.resolve(data.result);

            }).error(function (data, status, headers, config) {

                $log.debug('ERROR GETTING HAKU WITH OID');
            });

            return hakuPromise.promise;
        }
    };
});

app.factory('HakuV1', function ($resource, $log, Config) {
    $log = $log.getInstance("HakuV1");

    $log.info("HakuV1()");

    var serviceUrl = Config.env.tarjontaRestUrlPrefix + "haku/:oid";

    return $resource(serviceUrl, {oid: '@oid', state: '@state', processId: '@processId', onlyHaku: '@onlyHaku'}, {
        save: {
            method: 'POST',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        get: {
            method: 'GET',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        findAll: {
            method: 'GET',
            withCredentials: true,
            params: {oid: 'findAll'},
            isArray: false,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        mget: {
            url: Config.env.tarjontaRestUrlPrefix + 'haku/multi',
            method: 'GET',
            withCredentials: true,
            isArray: false
        },
        search: {
            method: 'GET',
            withCredentials: true,
            isArray: false,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        remove: {
            method: 'DELETE',
            withCredentials: true,
            headers: {'Content-Type': 'application/json; charset=UTF-8'}
        },
        checkStateChange: {
            url: Config.env.tarjontaRestUrlPrefix + 'haku/:oid/stateChangeCheck',
            method: 'GET',
            withCredentials: true
        },
        changeState: {
            url: Config.env.tarjontaRestUrlPrefix + 'haku/:oid/state?state=:state',
            method: 'PUT',
            withCredentials: true
        },
        copy: {
            url: Config.env.tarjontaRestUrlPrefix + 'haku/:oid/copy',
            method: 'PUT'
        },
        paste: {
            url: Config.env.tarjontaRestUrlPrefix + 'haku/paste/:oid/:processId',
            method: 'PUT'
        }
    });
});

/**
 * Haku Service
 */
app.factory('HakuV1Service', function ($log, $q, HakuV1, LocalisationService, AuthService) {
    $log = $log.getInstance("HakuV1Service");

    var userKieliUri = LocalisationService.getKieliUri();

    /**
     * Palauttaa haun nimen käyttäjän kielellä, tai fallback fi,sv,en tai "[Ei nimeä]"
     */
    var resolveLocalizedValue = function (key) {
        return key[userKieliUri] || key.kieli_fi || key.kieli_sv || key.kieli_en || "[Ei nimeä]";
    };

    /**
     * palauttaa promisen hakutulokseen, resolvaa nimen valmiiksi
     */
    var mget = function (oids) {
        //$log.debug("mget:", oids);
        return HakuV1.mget({oid: oids}).$promise.then(function (haut) {
            angular.forEach(haut.result, function (haku, key) {
                haku.nimi = resolveLocalizedValue(haku.nimi);
            });
            return haut.result;
        });
    };

    /**
     * Luo uusi tyhjä Haku käyttöliittymää varten. Sama formaatti kuin HakuV1 API:sta ladattavilla.
     */
    var createNewEmptyHaku = function () {
        $log.info("createNewEmptyHaku()");
        // Create new Haku with default values, same format as Haku API's result.
        return {
            "status": "OK",
            "result": {
                "hakukausiUri": "",
                "hakutapaUri": "",
                "hakukausiVuosi": 1900 + new Date().getYear(),
                "hakutyyppiUri": "",
                "kohdejoukkoUri": "",
                "koulutuksenAlkamisVuosi": 1900 + new Date().getYear(),
                "koulutuksenAlkamiskausiUri": "",
                "tila": "LUONNOS",
                "sijoittelu": false,
                "jarjestelmanHakulomake": true,
                "hakuaikas": [
                    {
                        "nimi": "",
                        "alkuPvm": null,
                        "loppuPvm": null
                    }
                ],
                "hakukohdeOids": [],
                "modified": new Date().getTime(),
                "modifiedBy": AuthService.getUserOid(),
                "nimi": {
                    "kieli_fi": "",
                    "kieli_sv": "",
                    "kieli_en": ""
                },
                "maxHakukohdes": 0,
                "usePriority": false
                // "hakulomakeUri" : "http://www.hut.fi",
            }
        };
    };

    var doDelete = function (oid) {
        $log.debug("doDelete(), oid = ", oid);

        return HakuV1.remove({oid: oid}).$promise.then(function (result) {
            $log.info("doDelete() result = ", result);
            return result;
        });
    };

    return {
        /**
         * Tarkista että tilasiirtymä on sallittu
         * oidstate esim: {oid: '123.456.789', state: 'JULKAISTU'}
         * jossa oid on haun oid, state tila johon ollaan siirtymässä.
         *
         * Palauttaa promisen.
         */
        checkStateChange: function (oidstate) {
            return HakuV1.checkStateChange(oidstate).$promise;

        },
        /**
         * Hae hakuja määritellyillä hakuehdoilla
         */
        search: function (parameters) {
            //$log.debug("Searching with: ", parameters);
            return HakuV1.search(parameters).$promise.then(function (data) {
                return mget(data.result);
            });
        },

        resolveLocalizedValue: resolveLocalizedValue,

        createNewEmptyHaku: createNewEmptyHaku,

        /**
         * Poista annettu haku (jos oikeuksia)
         */
        "delete": doDelete,

        /**
         * Kopioi haku (ks myös liitä)
         */
        copy: function (oid) {
            console.log("hakuoid:", oid);
            return HakuV1.copy({oid: oid}).$promise;
        },

        /**
         * Liitä haku
         */
        paste: function (targetOid, processId) {
            return HakuV1.paste({oid: targetOid, processId: processId}).$promise;
        }
    };

});
