'use strict';

var app = angular.module('app.edit.ctrl.alkamispaiva', ['localisation']);

app.directive('alkamispaivaJaKausi', ['$log', 'LocalisationService', function($log, LocalisationService) {
        function controller($scope, $q, $element, $compile) {

            $scope.ctrl = {
                disabledDate: false,
                disableKausi: false,
            };

            //add default option
            $scope.kausiUiModel=$scope.kausiUiModel||[];
            $scope.kausiUiModel.push({koodiNimi: LocalisationService.t('koulutus.edit.alkamispaiva.ei-valittua-kautta'), koodiUri: -1})

            $scope.$watch("kausiModel.uri", function(valNew, valOld) {
                $scope.ctrl.disabledDate = valNew !== -1;
            });

            $scope.clearKausiSelection = function() {
                $scope.kausiModel.uri = -1
            }

            $scope.$watch("ctrl.disabledKausi", function(valNew, valOld) {
                if (angular.isUndefined(valNew) || valNew === "" || valNew === true) {
                    $scope.clearKausiSelection();
                }
            });
        }

        return {
            restrict: 'E',
            replace: true,
            templateUrl: "partials/koulutus/edit/alkamispaiva-ja-kausi.html",
            controller: controller,
            scope: {
                pvms: "=",
                kausiModel: "=",
                kausiUiModel: "="
            }
        };
    }]);
