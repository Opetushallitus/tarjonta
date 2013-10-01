'use strict';

/* Services */
var app = angular.module('app.services', ['ngResource']);
app.value('version', '0.1');
app.factory('instagram', function($resource) {

    return {
        fetchPopular: function(callback) {

            // The ngResource module gives us the $resource service. It makes working with
            // AJAX easy. Here I am using the client_id of a test app. Replace it with yours.

            var api = $resource('https://api.instagram.com/v1/media/popular?client_id=:client_id&onchangecallback=JSON_CALLBACK', {
                client_id: '642176ece1e7445e99244cec26f4de1f'
            }, {
                // This creates an action which we've chosen to name "fetch". It issues
                // an JSONP request to the URL of the resource. JSONP requires that the
                // onchangecallback=JSON_CALLBACK part is added to the URL.

                fetch: {method: 'JSONP'}
            });

            api.fetch(function(response) {

                // Call the supplied onchangecallback function
                callback(response.data);

            });
        }
    };

});

// FIXME? Path?

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
                    log.info('Got koodi' + koodi);
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

