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
            postitoimipaikka: LocalisationService.t('osoitefield.postitoimipaikka'),
            hakutoimistonNimi: LocalisationService.t('osoitefield.hakutoimistonNimi'),
            puhelinnumero: LocalisationService.t('osoitefield.puhelinnumero'),
            sahkopostiosoite: LocalisationService.t('osoitefield.sahkopostiosoite'),
            wwwOsoite: LocalisationService.t('osoitefield.wwwOsoite'),
            postiosoite: LocalisationService.t('osoitefield.postiosoite'),
            kayntiosoite: LocalisationService.t('osoitefield.kayntiosoite')
        };
        var blankValues = {
            osoiterivi1: '',
            postinumero: '',
            postitoimipaikka: ''
        };
        $scope.osoitemuodot = [
            {
                key: 'KANSAINVALINEN',
                label: LocalisationService.t('osoitefield.kansainvalinenOsoitemuoto')
            },
            {
                key: 'SUOMALAINEN',
                label: LocalisationService.t('osoitefield.suomalainenOsoitemuoto')
            }
        ];
        $scope.$watch('model.osoitemuoto', function(osoitemuoto) {
            if (osoitemuoto === 'SUOMALAINEN') {
                $scope.model.kansainvalinenOsoite = '';
                $scope.model.kayntiosoite.kansainvalinenOsoite = '';
            }
            else if (osoitemuoto === 'KANSAINVALINEN') {
                _.extend($scope.model, blankValues);
                _.extend($scope.model.kayntiosoite, blankValues);
                initPostinumerot();
            }
        });
        $scope.postinumerot = [];
        Koodisto.getAllKoodisWithKoodiUri('posti', LocalisationService.getLocale()).then(function(ret) {
            $scope.postinumerot = ret;
            initPostinumerot();
        });
        if (!$scope.model) {
            $scope.model = blankValues;
        }

        function initPostinumerot() {
            $scope.updatePostinumero($scope.model);
            if ($scope.model.kayntiosoite) {
                $scope.updatePostinumero($scope.model.kayntiosoite);
            }
        }

        $scope.$watch('model.postinumero', initPostinumerot);
        $scope.$watch('model.kayntiosoite.postinumero', initPostinumerot);
        $scope.$watch('model.initPostinumerot', initPostinumerot);

        $scope.updatePostinumero = function(model) {
            var koodi;
            var uiValue = model.postinumeroUi;
            if (uiValue) {
                koodi = _.findWhere($scope.postinumerot, {koodiArvo: uiValue});
            }
            else {
                koodi = _.findWhere($scope.postinumerot, {koodiUri: model.postinumero});
            }
            if (koodi) {
                model.postitoimipaikka = koodi.koodiNimi;
                model.postitoimipaikkaUi = koodi.koodiNimi;
                model.postinumeroUi = koodi.koodiArvo;
                model.postinumero = koodi.koodiUri;
            }
        };

        $scope.clearPostinumeroIfEmpty = function(model) {
            var uiValue = model.postinumeroUi;
            if (uiValue.trim() === '') {
                model.postitoimipaikka = null;
                model.postitoimipaikkaUi = '';
                model.postinumero = null;
                model.postinumeroUi = '';
            }
        };
    }
    return {
        restrict: 'E',
        replace: true,
        templateUrl: function(elem, attr) {
            if (attr.extendedOsoite === 'true') {
                return 'js/shared/directives/osoiteFieldExtended.html';
            }
            return 'js/shared/directives/osoiteField.html';
        },
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