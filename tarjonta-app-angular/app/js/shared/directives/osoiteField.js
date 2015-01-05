var app = angular.module('TarjontaOsoiteField', [
    'localisation',
    'Koodisto'
]);
app.directive('osoiteField', function($log, LocalisationService, Koodisto) {
    'use strict';
    function controller($scope) {
        $scope.tt = {
            osoite: LocalisationService.t('osoitefield.osoiterivi'),
            postinumero: LocalisationService.t('osoitefield.postinumero'),
            postitoimipaikka: LocalisationService.t('osoitefield.postitoimipaikka')
        };
        $scope.postinumeroArvo = '';
        $scope.postinumerot = [];
        if (!$scope.model) {
            $scope.model = {
                osoiterivi1: '',
                postinumero: '',
                postitoimipaikka: ''
            };
        }
        function findPostinumeroWithUri(koodi) {
            for (var i in $scope.postinumerot) {
                if ($scope.postinumerot[i].koodiUri == koodi) {
                    return $scope.postinumerot[i];
                }
            }
            return null;
        }
        $scope.updatePostinumero = function() {
            if ($scope.postinumeroArvo && $scope.postinumeroArvo.trim().length > 0) {
                for (var i in $scope.postinumerot) {
                    if ($scope.postinumerot[i].koodiArvo == $scope.postinumeroArvo) {
                        $scope.model.postinumero = $scope.postinumerot[i].koodiUri;
                        break;
                    }
                }
            }
            if ($scope.model && $scope.model.postinumero) {
                var pn = findPostinumeroWithUri($scope.model.postinumero);
                $scope.postinumeroArvo = pn && pn.koodiArvo;
                $scope.model.postitoimipaikka = pn && pn.koodiNimi;
            }
        };
        $scope.$watch('model', function(nv, ov) {
            $scope.postinumeroArvo = null;
            $scope.updatePostinumero();
        }, true);
        Koodisto.getAllKoodisWithKoodiUri('posti', LocalisationService.getLocale()).then(function(ret) {
            $scope.postinumerot = ret;
            $scope.updatePostinumero();
        });
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: 'js/shared/directives/osoiteField.html',
        controller: controller,
        require: '^?form',
        link: function(scope, element, attrs, controller) {
            scope.isDisabled = function() {
                return attrs.disabled || scope.ngDisabled();
            };
            scope.isRequired = function() {
                return attrs.required || scope.ngRequired();
            };
            if (scope.name) {
                controller.$addControl({
                    '$name': scope.name,
                    '$error': scope.errors
                });
            }
        },
        scope: {
            model: '=',
            // arvo
            // disablointi
            disabled: '@',
            ngDisabled: '&',
            // angular-form-logiikkaa varten
            name: '@',
            // nimi formissa
            required: '@',
            // pakollisuus
            ngRequired: '&',
            // vastaava ng
            onChange: '&' // funktio, jota kutsutaan modelin muuttuessa
        }
    };
});
app.filter('osoite', function() {
    'use strict';
    // HUOM! ei hae postitoimipaikkaa koodistosta!
    return function(osoite) {
        if (!osoite) {
            return '';
        }
        var ret = [];
        if (osoite.osoiterivi1) {
            ret.push(osoite.osoiterivi1);
        }
        if (osoite.postinumero) {
            var p = osoite.postinumero.indexOf('_');
            ret.push(p == -1 ? osoite.postinumero : osoite.postinumero.substring(p + 1));
        }
        if (osoite.postitoimipaikka) {
            ret.push(osoite.postitoimipaikka);
        }
        return ret.join(', ');
    };
});