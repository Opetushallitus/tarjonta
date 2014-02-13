'use strict';

var app = angular.module('app.edit.ctrl.laajuus', ['localisation']);

app.directive('laajuus', ['$log', function($log) {
        function controller($scope, $q, $element, $compile) {
            if (angular.isUndefined($scope.arvoUiModel)) {
                new Error('Tarjonta application error - model cannot be null or undefined!');
            }

            $scope.ctrl = {
                isMulti: false
            };

            $scope.$watch("arvoUiModel.meta", function(valNew, valOld) {
                if (angular.isDefined(valNew) && angular.isObject(valNew)) {
                    var length = Object.keys(valNew).length;
                    $scope.ctrl.isMulti = (length > 1);

                    if (!$scope.ctrl.isMulti) {
                        var key = Object.keys(valNew)[0];
                        $scope.arvoModel.uri = key;
                        $scope.arvoModel.versio = $scope.arvoUiModel.uris[key];
                    }
                }
            });
        }

        return {
            restrict: 'E',
            replace: true,
            templateUrl: "partials/koulutus/edit/laajuus.html",
            controller: controller,
            scope: {
                arvoModel: "=",
                arvoUiModel: "=",
                yksikko: "="
            }
        };
    }]);