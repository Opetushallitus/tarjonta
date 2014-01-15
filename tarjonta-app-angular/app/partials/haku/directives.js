/* Directives */

'use strict';

angular.module('app.haku.directives', []);

app.directive('ttt', ['$log', 'LocalisationService', function($log, LocalisationService) {
        return {
            restrict: 'EA',
            replace: true,
            //template: '<div tt="this.is.key" locale="fi">Default saved for the given key</div>',
            scope: false,
            compile: function(tElement, tAttrs, transclude) {
                $log.info("ttt compile", tElement, tAttrs, transclude);

                // Grab the original / placeholder text in the template
                var originalText = "";

                var localName = tElement[0].localName;
                if (localName === "input") {
                    originalText = tAttrs["value"];
                } else {
                    originalText = tElement.html();
                }

                // Get the desired locale if any
                // TODO get the users locale in place of the "fi"
                var locale = (tAttrs["locale"] === undefined) ? "fi" : tAttrs["locale"];

                // Create translation placeholder
                // TODO get the translated value if possible!
                // TODO Use originalText if not available!
                var t = "[" + tAttrs["ttt"] + "-" + locale + "]";

                $log.info("  key " + t + " --> " + originalText);

                t = originalText;

                if (localName === "input") {
                    tElement.attr("value", t);
                } else {
                    tElement.html(t);
                }

                return function postLink(scope, iElement, iAttrs, controller) {
                    // $timeout(scope.$destroy.bind(scope), 0);
                };
            }
        };
    }]);
