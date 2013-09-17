'use strict';

/* Services */

var app = angular.module("tarjontaApp.services", ['ngResource']);

//
// Register a value
//
app.value('version', '0.1');

//
// Instagram service (testing service)
//
// see: http://tutorialzine.com/2013/08/learn-angularjs-5-examples/
//
app.factory('instagram', function($resource) {

    return {
        fetchPopular: function(callback) {

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
    }

});