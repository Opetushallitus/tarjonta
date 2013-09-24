'use strict';

/* Directives */

angular.module('app.kk.directives', []).
        directive('appVersion', ['version', function(version) {
        return function(scope, elm, attrs) {
            elm.text(version + '100');
        };
    }]);


// http://stackoverflow.com/a/17364716
angular.module('app.directives').directive('ngEnter', function() {
        return function(scope, element, attrs) {
            element.bind("keydown keypress", function(event) {
                if(event.which === 13) {
                    scope.$apply(function(){
                        scope.$eval(attrs.ngEnter);
                    });

                    event.preventDefault();
                }
            });
        };
    });

