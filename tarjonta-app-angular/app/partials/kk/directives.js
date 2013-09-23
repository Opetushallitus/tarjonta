'use strict';

/* Directives */

angular.module('app.kk.directives', []).
        directive('appVersion', ['version', function(version) {
        return function(scope, elm, attrs) {
            elm.text(version + '100');
        };
    }])
