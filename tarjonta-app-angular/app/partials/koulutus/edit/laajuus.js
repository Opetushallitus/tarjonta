var app = angular.module('app.edit.ctrl.laajuus', ['localisation']);
app.directive('laajuus', [
    '$log', function($log) {
        function controller($scope, $q, $element, $compile) {
            'use strict';
            if (angular.isUndefined($scope.arvoUiModel)) {
                new Error('Tarjonta application error - model cannot be null or undefined!');
            }
            $scope.ctrl = {
                isMulti: false
            };
            $scope.$watch('arvoModel.uri', function(newUri, oUri) {
                //change listener
                if (angular.isDefined(newUri) && newUri !== null && newUri.length > 0) {
                    if ($scope.ctrl.isMulti) {
                        $scope.arvoModel.versio = $scope.arvoUiModel.meta[newUri].versio;
                    }
                }
            });
            $scope.$watch('arvoUiModel.meta', function(valNew, valOld) {
                if (angular.isDefined(valNew) && angular.isObject(valNew)) {
                    var length = Object.keys(valNew).length;
                    $scope.ctrl.isMulti = length > 1;
                    if (length === 1) {
                        var key = Object.keys(valNew)[0];
                        $scope.arvoModel.uri = key;
                        $scope.arvoModel.versio = valNew[key].versio;
                    }
                }
            });
        }
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'partials/koulutus/edit/laajuus.html',
            controller: controller,
            scope: {
                arvoModel: '=',
                arvoUiModel: '=',
                yksikko: '='
            }
        };
    }
]);