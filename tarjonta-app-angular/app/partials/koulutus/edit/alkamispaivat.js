'use strict';

var app = angular.module('app.edit.ctrl.alkamispaiva');

app.directive('alkamispaivat', ['$log', function($log) {
        function controller($scope, $q, $element, $compile) {

            $scope.ctrl = {
                addedDates: [],
                index: 0
            };

            $scope.clickAddDate = function() {
                $scope.ctrl.addedDates.push({id: $scope.ctrl.index++, date: null});
            };

            $scope.clickRemoveDate = function(date) {
                $scope.removeById($scope.ctrl.addedDates, date.id);
            };

            $scope.searchIndex = function(arrDates, id) {
                if (angular.isUndefined(id)) {
                    throw new Error("Missing ID.");
                }

                for (var i = 0; i < arrDates.length; i++) {
                    if (arrDates[i].id === id) {
                        return i;
                    }
                }

                return -1;
            };


            $scope.removeById = function(arrDates, id) {
                var i = $scope.searchIndex(arrDates, id);
                if (i !== -1) {
                    arrDates.splice(i, 1);
                }
            };

            $scope.$watch("disabledDate", function(valNew, valOld) {
                if (valNew !== valOld && valNew) {
                    //clear date data objects
                    for (var i = 0; i < $scope.ctrl.addedDates.length; i++) {
                        $scope.ctrl.addedDates[i].date = null;
                    }
                }
            });

            $scope.$watch("disabledKausi", function(valNew, valOld) {
                if (valNew !== valOld && !valNew) {
                    //clear price data field
                    var arrRemoveDatesTmp = [];
                    for (var i = 0; i < $scope.ctrl.addedDates.length; i++) {
                        arrRemoveDatesTmp.push($scope.ctrl.addedDates[i]);
                    }

                    //loop starts from arr[1]
                    for (var i = 1; i < arrRemoveDatesTmp.length; i++) {
                        $scope.clickRemoveDate(arrRemoveDatesTmp[i]);
                    }
                } else {
                    $scope.fnClearKausi();
                }
            });

            $scope.clickAddDate(); //add 1 date row
        }

        return {
            restrict: 'E',
            replace: true,
            templateUrl: "partials/koulutus/edit/alkamispaivat.html",
            controller: controller,
            scope: {
                dates: "=", //BaseEditController ui model
                fnClearKausi: "=",
                disabledKausi: "=",
                disabledDate: "="

            }
        };
    }]);
