'use strict';

var app = angular.module('app.edit.ctrl.alkamispaiva');

app.directive('alkamispaivat', ['$log', function($log) {
        function controller($scope, $q, $element, $compile) {
            $scope.ctrl = {
                addedDates: [],
                index: 0,
                ignoreDateListChanges: false
            };
            
            $scope.onDateAdded = function() {
            	for (var i in $scope.ctrl.addedDates) {
            		if ($scope.ctrl.addedDates[i].date==null) {
            			return;
            		}
            	}
            	if ($scope.multi) {
            		$scope.clickAddDate();
            	}
            }

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
            	if (!$scope.dates) {
            		$scope.dates = [];
            	}
                if ($scope.dates.length > 0) {
                    //when page is loaded and one or more datea are in the model, then
                    //set kausi to status of disabled and all date fields to active
                                    	
                    //load data to directive model
                    var a = [];
                    for (var i = 0; i < $scope.dates.length; i++) {
                        a.push({id: $scope.ctrl.index++, date: new Date($scope.dates[i])});
                    }
                    $scope.ctrl.addedDates = a;
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

            $scope.$watch("multi", function(valNew, valOld) {
            	if (!valNew && $scope.ctrl.addedDates.length>1) {
            		$scope.ctrl.addedDates = [$scope.ctrl.addedDates[0]];
            	} else if (valNew && $scope.ctrl.addedDates.length==1) {
            		$scope.clickAddDate();
            	}
            });            
            
            $scope.$watch("enabled", function(valNew, valOld) {
                if (!valNew) {
                	$scope.ctrl.addedDates = [];
                	$scope.clickAddDate();                	
                } else {
	             	if ($scope.multi && $scope.dates.length==1 && $scope.dates[0]!=null) {
	                     $scope.clickAddDate(); //add 1 date row
	             	}
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
                enabled: "=",
                multi: "="

            }
        };
    }]);
