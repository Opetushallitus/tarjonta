'use strict';

/* Directives */

angular.module('kkTutkintoApp.directives', []).
        directive('appVersion', ['version', function(version) {
        return function(scope, elm, attrs) {
            elm.text(version + '100');
        };
    }])
