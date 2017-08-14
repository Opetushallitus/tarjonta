var app = angular.module('TarjontaInvalidHtml', [
    'ngSanitize',
    'localisation'
]);

/**
 * Kuvaukseen voi olla syötettynä rikkinäistä html:ää. Tällöin näytetään ilmoitus "Rikkinäistä HTML:ää".
 * https://github.com/shaunbowe/ngBindHtmlIfSafe
 */
app.directive("invalidHtml", ['$compile', '$sce', 'LocalisationService', function ($compile, $sce, LocalisationService) {
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