'use strict';

/* Filters */

angular.module('kkTutkintoApp.filters', []).
        filter('interpolate', ['version', function(version) {
        return function(text) {
            return String(text).replace(/\%VERSION\%/mg, version);
        };
    }]);
