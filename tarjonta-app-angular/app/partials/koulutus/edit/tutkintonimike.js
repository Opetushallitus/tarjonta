'use strict';
var app = angular.module('app.edit.ctrl.tutkintonimike', ['localisation']);
app.directive('tutkintonimike', [
    '$log',
    '$modal',
    'LocalisationService', function($log, $modal, LocalisationService) {
        function controller($scope, $q, $element, $compile) {
            if (angular.isUndefined($scope.model)) {
                new Error('Tarjonta application error - model cannot be null or undefined!');
            }
            $scope.ctrl = {
                isMulti: false,
                data: []
            };
            $scope.$watch('model.meta', function(valNew, valOld) {
                if (angular.isDefined(valNew) && angular.isObject(valNew)) {
                    var length = Object.keys(valNew).length;
                    $scope.ctrl.isMulti = length > 1;
                    if (!$scope.ctrl.isMulti) {
                        //single item
                        angular.forEach($scope.model.meta, function(val, key) {
                            $scope.model.uris.push(key);
                        });
                    }
                    else {
                        //multiple items
                        var arr = [];
                        angular.forEach($scope.model.meta, function(val, key) {
                            arr.push({
                                uri: key,
                                nimi: val.nimi
                            });
                        });
                        $scope.ctrl.data = arr;
                    }
                }
            });
        }
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'partials/koulutus/edit/tutkintonimike.html',
            controller: controller,
            scope: {
                model: '='
            }
        };
    }
]);