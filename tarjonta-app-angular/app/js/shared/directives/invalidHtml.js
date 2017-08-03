var app = angular.module('TarjontaInvalidHtml', [
    'ngSanitize',
    'Logging',
    'localisation'
]);

/**
 * Kuvaukseen voi olla syötettynä rikkinäistä html:ää. Tällöin näytetään ilmoitus "Rikkinäistä HTML:ää".
 * https://github.com/shaunbowe/ngBindHtmlIfSafe
 */
app.directive("invalidHtml", ['$compile', '$sce', '$log', 'LocalisationService', function ($compile, $sce, $log, LocalisationService) {
    $log = $log.getInstance('invalidHtml');

    return function (scope, element, attrs) {
        scope.$watch(
            function (scope) {
                return scope.$eval(attrs.invalidHtml);
            },
            function (value) {
                if (value) {
                    try {
                        $sce.getTrustedHtml(value);
                    } catch (exception) {
                        element.text(LocalisationService.t('tarjonta.invalid-html.virheilmoitus'));
                        element.addClass("msgError");
                    }

                    $compile(element.contents())(scope);
                }
            }
        );
    }
}]);