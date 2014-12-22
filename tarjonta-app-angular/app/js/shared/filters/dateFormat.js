/**
 * Päivämäärän ja ajan formatointi selaimen localen mukaan.
 * 
 */
angular.module('DateFormat', []).filter('ldate', function() {
    return function(input) {
        return new Date(input).toLocaleDateString();
    };
}).filter('ltime', function() {
    return function(input) {
        return new Date(input).toLocaleTimeString();
    };
}).filter('ltimedate', function() {
    return function(input) {
        return new Date(input).toLocaletring();
    };
});