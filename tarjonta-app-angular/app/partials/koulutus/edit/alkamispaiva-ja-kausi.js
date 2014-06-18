'use strict';

var app = angular.module('app.edit.ctrl.alkamispaiva', ['localisation', 'TarjontaDateTime']);

app.directive('alkamispaivaJaKausi', ['$log', '$modal', 'LocalisationService', function($log, $modal, LocalisationService) {

        $log = $log.getInstance("alkamispaivaJaKausi");

        function controller($scope, $q, $element, $compile) {
            $scope.isKausiVuosiRadioButtonActive = function() {
                return $scope.pvms.length === 0
                        && (!angular.isUndefined($scope.vuosi)
                                && angular.isNumber($scope.vuosi))
                        && (!angular.isUndefined($scope.kausiUri)
                                && $scope.kausiUri.length > 0);
            };

            $scope.ctrl = {
                kausi: $scope.isKausiVuosiRadioButtonActive(),
                multi: $scope.pvms.length > 1,
                koodis: [],
                kausiVaiPvm: angular.isDefined($scope.fieldNamePrefix) && $scope.fieldNamePrefix.length > 0 ? $scope.fieldNamePrefix + "_kausiVaiPvm" : "kausiVaiPvm",
                alkamiskausi: angular.isDefined($scope.fieldNamePrefix) && $scope.fieldNamePrefix.length > 0 ? $scope.fieldNamePrefix + "_alkamiskausi" : "alkamiskausi",
                alkamisvuosi: angular.isDefined($scope.fieldNamePrefix) && $scope.fieldNamePrefix.length > 0 ? $scope.fieldNamePrefix + "_alkamisvuosi" : "alkamisvuosi",
                kausivuosi: angular.isDefined($scope.fieldNamePrefix) && $scope.fieldNamePrefix.length > 0 ? $scope.fieldNamePrefix + "_kausivuosi" : "kausivuosi"
            };

            $scope.minYear = new Date().getFullYear() - 1;
            $scope.maxYear = $scope.minYear + 11;

            $scope.$watch("ctrl.kausi", function(valNew, valOld) {
                $scope.form[$scope.ctrl.kausivuosi] = valNew;
                if (valNew && $scope.kausi) {
                    $scope.kausiUri = '';
                }
            });

            $scope.$watch("kausiUri", function(valNew, valOld) {
                $scope.kausiUiModel.uri = $scope.kausiUri;
            });

            $scope.clearKausiSelection = function() {
                $scope.kausiUri = "";
            };

            $scope.onAddDate = function() {
                $scope.alkamisPaivat.clickAddDate();
            };

            $scope.onEnableKausi = function($event) {
                if ($scope.pvms.length > 0) {
                    $event.preventDefault();
                    $event.stopImmediatePropagation();

                    // alkamispvm:iä valittu -> näytä vahvistusdialogi
                    var ctrl = $scope.ctrl;
                    var modalInstance = $modal.open({
                        scope: $scope,
                        templateUrl: 'partials/koulutus/edit/alkamispaiva-dialog.html',
                        controller: function($scope) {
                            $scope.ok = function() {
                                ctrl.kausi = true;
                                ctrl.multi = false;
                                modalInstance.dismiss();
                            };
                            $scope.cancel = function() {
                                modalInstance.dismiss();
                            };
                            return $scope;
                        }
                    });
                }
            };

            $scope.onToggleManyDates = function($event) {
                if ($scope.pvms.length > 1) {
                    $event.preventDefault();
                    $event.stopImmediatePropagation();
                    var modalInstance = $modal.open({
                        scope: $scope,
                        templateUrl: 'partials/koulutus/edit/alkamispaiva-dialog-na.html',
                        controller: function($scope) {
                            $scope.ok = function() {
                                modalInstance.dismiss();
                            }
                            return $scope;
                        }
                    });
                }
            };

            $scope.$watch("ctrl.mode", function(valNew, valOld) {
                if (angular.isUndefined(valNew) || valNew === "" || valNew === true) {
                    if (!angular.isUndefined(valNew)) {
                        $scope.clearKausiSelection();
                    }
                }
            });

            $scope.kausiUiModel.promise.then(function(result) {
                for (var i = 0; i < $scope.kausiUiModel.koodis.length; i++) {
                    $scope.ctrl.koodis.push($scope.kausiUiModel.koodis[i]);
                }
            });
            return $scope;
        }

        return {
            restrict: 'E',
            replace: true,
            templateUrl: "partials/koulutus/edit/alkamispaiva-ja-kausi.html",
            require: ['^form'],
            link: function(scope, element, attrs, controller) {
                scope.form = controller[0];
            }
            ,
            controller: controller,
            scope: {
                pvms: "=",
                vuosi: "=",
                kausiUiModel: "=",
                kausiUri: "=",
                fieldNamePrefix: "@"
            }
        };
    }]);

app.directive('alkamispaivat', ['$log', function($log) {
        $log = $log.getInstance("alkamispaivat");

        function controller($scope, $q, $element, $compile) {
            $scope.ctrl = {
                addedDates: [],
                index: 0,
                ignoreDateListChanges: false,
                alkamisPvmFieldName: angular.isDefined($scope.fieldNamePrefix) && $scope.fieldNamePrefix.length > 0 ? $scope.fieldNamePrefix + "_alkamisPvm" : "alkamisPvm"
            };

            $scope.thisYear = new Date(new Date().getFullYear(), 0, 1, 0, 0, 0, 0);

            $scope.clickAddDate = function() {
                $scope.ctrl.addedDates.push({id: $scope.ctrl.index++, date: null});
            };

            $scope.onDateAdded = function() {
                for (var i in $scope.ctrl.addedDates) {
                    if ($scope.ctrl.addedDates[i].date == null) {
                        return;
                    }
                }
                if ($scope.multi) {
                    $scope.clickAddDate();
                }
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
                if (!valNew && $scope.ctrl.addedDates.length > 1) {
                    $scope.ctrl.addedDates = [$scope.ctrl.addedDates[0]];
                } else if (valNew && $scope.ctrl.addedDates.length == 1) {
                    $scope.clickAddDate();
                }
            });

            $scope.$watch("enabled", function(valNew, valOld) {
                if (!valNew) {
                    $scope.ctrl.addedDates = [];
                    $scope.clickAddDate();
                } else {
                    if ($scope.multi && $scope.dates.length == 1 && $scope.dates[0] != null) {
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
                if (angular.isDefined(valNew)) {
                    $scope.reset();
                }
            });

            return $scope;
        }

        return {
            restrict: 'E',
            replace: true,
            templateUrl: "partials/koulutus/edit/alkamispaivat.html",
            controller: controller,
            require: ['^alkamispaivaJaKausi'],
            link: function(scope, element, attrs, controller) {
                controller[0].alkamisPaivat = scope;
            },
            scope: {
                dates: "=", //BaseEditController ui model
                kausiUri: "=",
                fnClearKausi: "=",
                enabled: "=",
                multi: "=",
                fieldNamePrefix: "@"
            }
        };
    }]);

