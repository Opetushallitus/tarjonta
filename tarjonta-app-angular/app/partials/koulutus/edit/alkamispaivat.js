'use strict';

var app = angular.module('app.edit.ctrl.alkamispaiva');

app.directive('alkamispaivat', ['$log', function($log) {
        function controller($scope, $q, $element, $compile) {
            $scope.ctrl = {
                addedDates: [],
                index: 0,
                ignoreDateListChanges: false
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

            /**
             * Initialize buttons and date fields.
             */
            $scope.reset = function() {
                if (!angular.isUndefined($scope.dates) && $scope.dates.length > 0) {
                    //when page is loaded and one or more datea are in the model, then
                    //set kausi to status of disabled and all date fields to active
                    if ($scope.dates.length > 1) {
                        $scope.disabledKausi = true;
                    } else {
                        $scope.fnClearKausi();
                        $scope.disabledKausi = false;
                    }
                    $scope.disabledDate = false;

                    //load data to directive model
                    var a = [];
                    for (var i = 0; i < $scope.dates.length; i++) {
                        a.push({id: $scope.ctrl.index++, date: new Date($scope.dates[i])});
                    }
                    $scope.ctrl.addedDates = a;
                } else if ($scope.kausiUri !== -1 && $scope.dates.length === 0) {
                    //Date field is diabled
                    $scope.disabledKausi = false;
                    $scope.disabledDate = true;
                    $scope.clickAddDate(); //add 1 date row
                } else {
                    //no loaded dates available,  add one ui date object to date list
                    $scope.clickAddDate(); //add 1 date row
                }
            };

            /*
             * Update date model data and filter all invalid date selections.
             */
            $scope.$watch("ctrl.addedDates", function(valNew, valOld) {
                if (valNew !== valOld) {
                    $scope.ctrl.ignoreDateListChanges = true;
                    var map = {};
                    var tmp = angular.copy($scope.ctrl.addedDates);

                    //cleanup date list and leave only unique dates
                    for (var i = 0; i < tmp.length; i++) {
                        var key = null;
                        if (!angular.isUndefined(tmp[i].date) && tmp[i].date !== null) {
                            key = tmp[i].date.getTime();
                        }

                        var id = tmp[i].id;

                        if (angular.isUndefined(map[key])) {
                            map[key] = id;
                        } else {
                            for (var index = 0; index < $scope.ctrl.addedDates.length; index++) {
                                if ($scope.ctrl.addedDates[index].id === id) {
                                    $scope.ctrl.addedDates.splice(index, 1);
                                    break;
                                }
                            }
                        }
                    }

                    $scope.dates.splice(0, $scope.dates.length); //clear data

                    for (var i = 0; i < $scope.ctrl.addedDates.length; i++) {
                        if (!angular.isUndefined($scope.ctrl.addedDates[i].date) && $scope.ctrl.addedDates[i].date !== null) {
                            //date to long
                            $scope.dates.push($scope.ctrl.addedDates[i].date.getTime());
                        }
                    }
                    $scope.ctrl.ignoreDateListChanges = false;
                }
            }, true);

            /*
             * Clear all date objects when an user has
             * chosen to disable the date field.
             * 
             * The list must have at least one item, or select field 
             * is removed from the ui.
             */
            $scope.$watch("disabledDate", function(valNew, valOld) {
                if (valNew !== valOld && valNew) {
                    //clear date data objects
                    $scope.ctrl.addedDates = [];
                    $scope.clickAddDate();
                }
            });

            $scope.$watch("disabledKausi", function(valNew, valOld) {
                if (!valNew) {
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

            /*
             * DATA INIT / RELOAD LISTENER
             * 
             * There is no init, because you can force directive data model
             * to reload by creating new array of dates.
             */
            $scope.$watch("dates", function(valNew, valOld) {
                $scope.reset();
            });

        }

        return {
            restrict: 'E',
            replace: true,
            templateUrl: "partials/koulutus/edit/alkamispaivat.html",
            controller: controller,
            scope: {
                dates: "=", //BaseEditController ui model
                kausiUri: "=",
                fnClearKausi: "=",
                disabledKausi: "=",
                disabledDate: "="

            }
        };
    }]);
