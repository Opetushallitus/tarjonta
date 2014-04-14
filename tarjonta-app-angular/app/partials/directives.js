'use strict';

/* Directives */

angular.module('app.directives', []);

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


