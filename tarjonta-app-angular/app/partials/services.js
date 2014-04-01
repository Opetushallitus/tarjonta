'use strict';

/* Services */
var app = angular.module('app.services', ['ngResource']);

// TODO FIXME? Path?

app.factory('TarjontaConfig', function($resource) {
    // TODO hardcoded, read from service
    return $resource('partials/kk/edit/config.json', {}, {
        query: {method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }}
    });
});

app.factory('KoodiService', function($resource, $log, $q) {

    $log = $log.getInstance("KoodiService");

    return {
        getAllKoodisWithKoodiUri: function(koodistoUriParam, locale) {
            $log.info('getAllKoodisWithKoodiUri called with ' + koodistoUriParam + ' ' + locale);
            var returnKoodis = [];

            var testKoodiUri = 'https://itest-virkailija.oph.ware.fi/koodisto-service/rest/json/:koodistoUri/koodi';

            var resource = $resource(testKoodiUri, {}, {getResult: {method: "GET",
                    //headers : { "Access-Control-Allow-Origin": "*" },
                    params: {koodistoUri: '@koodistoUri'}, isArray: true}});

            resource.getResult(function(koodis) {
                _(koodis).all(function(koodi) {
                    $log.info('Got koodi' + koodi);
                    var tarjontaKoodi = {
                        koodiArvo: koodi.koodiArvo,
                        koodiNimi: _.select(koodi.metadata, function(koodiMetaData) {
                            if (koodiMetaData === locale) {
                                return koodiMetaData.nimi;
                            }
                        })
                    };
                    returnKoodis.push(tarjontaKoodi);
                    $log.debug('Got ' + returnKoodis.length + ' koodis ');
                });
            });

        },
        getKoodistoWithKoodiUri: function(koodiUriParam, locale) {
            $log.debug("getKoodistoWithKoodiUri()", koodiUriParam, locale);

            var returnKoodi = $q.defer();

            var testKoodiUri = "https://itest-virkailija.oph.ware.fi/koodisto-service/rest/json/:koodistoUri";

            var resource = $resource(testKoodiUri, {koodistoUri: '@koodistoUri'}).get({koodistoUri: koodiUriParam}, function(data) {
                var returnTarjontaKoodi = {
                    koodistoUri: data.koodistoUri,
                    tila: data.tila
                };
                returnKoodi.resolve(returnTarjontaKoodi);

            });

            return returnKoodi.promise;
        }

    };

});

