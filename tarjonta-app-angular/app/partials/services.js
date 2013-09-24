'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
var app = angular.module('app.services', ['ngResource']);

app.value('version', '0.1');

// http://tutorialzine.com/2013/08/learn-angularjs-5-examples/
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

app.factory('TarjontaService', function($resource) {
    var CONFIG;

//    dataFactory.insertTutkinto = function (cust) {
//        return $http.post(urlBase, cust);
//    };
//
//    dataFactory.updateTutkinto = function (cust) {
//        return $http.put(urlBase + '/' + cust.ID, cust)
//    };
//
//    dataFactory.deleteTutkinto = function (id) {
//        return $http.delete(urlBase + '/' + id);
//    };
//
//     dataFactory.getTutkinto = function (id) {
//        return $http.delete(urlBase + '/' + id);
//    };

//    tarjontaConfig.get(function(jsonObject) {
//        if (CONFIG === undefined) {
//            console.info("Tarjonta configuration file loaded.");
//            CONFIG = jsonObject;
//        }
//    });

    return $resource('partials/kk/edit/koulutusData.json', {}, {
        query: {method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }}
    });

//    return $resource('http://localhost:8585/tarjonta-service/rest/koulutus/:oid', {}, {
//        query: {method: 'GET', headers: {
//                'Content-Type': 'application/json',
//                'Accept': 'application/json'
//            }, params: {
//                host: CONFIG.host,
//                port: CONFIG.port,
//            }, isArray: true, xhrFields: {
//                withCredentials: true
//            }}
//    });
});

app.factory('KoodiService',function($resource, $log,$q){





    return {

        getAllKoodisWithKoodiUri : function(koodistoUriParam, locale) {


            $log.info('getAllKoodisWithKoodiUri called with ' + koodistoUriParam + ' ' + locale);
            var returnKoodis = [];

            var testKoodiUri = 'https://itest-virkailija.oph.ware.fi/koodisto-service/rest/json/:koodistoUri/koodi';

            var resource = $resource(testKoodiUri,{},{getResult : {method : "GET" ,
                //headers : { "Access-Control-Allow-Origin": "*" },
                params: {koodistoUri:'@koodistoUri'},isArray:true}});

            resource.getResult(function(koodis){
                _(koodis).all(function(koodi){
                    log.info('Got koodi' + koodi);
                    var tarjontaKoodi  = {
                        koodiArvo : koodi.koodiArvo,
                        koodiNimi : _.select(koodi.metadata,function(koodiMetaData) {
                            if (koodiMetaData === locale) {
                                return koodiMetaData.nimi;
                            }
                        } )
                    };
                    returnKoodis.push(tarjontaKoodi);
                    $log.debug('Got '+ returnKoodis.length + ' koodis ');
                });
            } );

        } ,
        getKoodistoWithKoodiUri : function(koodiUriParam,locale) {

            var returnKoodi = $q.defer();


            var testKoodiUri = "https://itest-virkailija.oph.ware.fi/koodisto-service/rest/json/:koodistoUri";

            var resource = $resource(testKoodiUri,{koodistoUri : '@koodistoUri'}).get({koodistoUri:koodiUriParam},function(data){
                var returnTarjontaKoodi = {
                    koodistoUri : data.koodistoUri,
                    tila : data.tila
                };
                returnKoodi.resolve(returnTarjontaKoodi);

            });

            return returnKoodi.promise;
        }

    };

});

