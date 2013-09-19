'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
var app = angular.module('tarjontaApp.services', ['ngResource']);

app.value('version', '0.1');

// http://tutorialzine.com/2013/08/learn-angularjs-5-examples/
app.factory('instagram', function($resource) {

    return {
        fetchPopular: function(callback) {

            // The ngResource module gives us the $resource service. It makes working with
            // AJAX easy. Here I am using the client_id of a test app. Replace it with yours.

            var api = $resource('https://api.instagram.com/v1/media/popular?client_id=:client_id&callback=JSON_CALLBACK', {
                client_id: '642176ece1e7445e99244cec26f4de1f'
            }, {
                // This creates an action which we've chosen to name "fetch". It issues
                // an JSONP request to the URL of the resource. JSONP requires that the
                // callback=JSON_CALLBACK part is added to the URL.

                fetch: {method: 'JSONP'}
            });

            api.fetch(function(response) {

                // Call the supplied callback function
                callback(response.data);

            });
        }
    };

});

app.factory('TarjontaConfig', function($resource) {
    // TODO hardcoded, read from service
    return $resource('config.json', {}, {
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

    return $resource("js/" + KK_TUTKINTO + '/koulutusData.json', {}, {
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

