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
    }

});
