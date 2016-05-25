var app = angular.module('app.review.directives', []);

/**
 * Component directive for wrapping start season logic, rendering a span
 * Used in Review of variety of different LOs
 *
 * Looks for extraParams in the LO object. Determines if it should override otherwise
 * uses default implementation of season or date. Provides startSeason or startDates
 * for template.
 */
app.directive('startSeasonOrDate', function () {
    return {
        restrict: 'E',
        scope: {
            koulutus: '=',
            lang: '='
        },
        link: function ($scope, element, attrs) {
            var k = $scope.koulutus,
                lang = $scope.lang,
                extraParams = k.extraParams,
                opintopolkuAlkamisKausi = k.opintopolkuAlkamiskausi,
                aloitusVuosi = k.koulutuksenAlkamisvuosi,
                aloitusKausi;

            // Set startSeason in current lang
            if (extraParams && extraParams.opintopolkuKesaKausi === "true") {
                aloitusKausi = opintopolkuAlkamisKausi[lang];
            } else if (k.koulutuksenAlkamisPvms.length === 0) {
                aloitusKausi = k.koulutuksenAlkamiskausi.meta[lang].nimi;
            }

            // Set scope objects. Prefer season/year combination.
            if (aloitusKausi != null && aloitusVuosi != null) {
                $scope.startSeason = aloitusKausi + " " + aloitusVuosi;
            } else {
                $scope.startDates = k.koulutuksenAlkamisPvms;
            }
        },
        templateUrl: 'partials/koulutus/review/start-season-or-date.html'
    };
});
