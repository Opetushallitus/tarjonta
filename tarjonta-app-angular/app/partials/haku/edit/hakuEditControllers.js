/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */

var app = angular.module('app.haku.edit.ctrl', []);

app.controller('BaseHakuEditController',
        ['$route', '$scope', '$location', '$log', '$routeParams', '$window', '$modal', 'LocalisationService',
            function BaseEditController($route, $scope, $location, $log, $routeParams, $window, $modal, LocalisationService) {
                $log.info("BaseHakuEditController()");

                // TODO preloaded / resolved haku is where?

                $scope.koodistoLocale = LocalisationService.getLocale();//"fi";
                $scope.model = null;

                $scope.init = function() {
                    $log.info("init...");
                    var model = {
                        validation: {
                            showValidationErrors: false,
                            showError: false,
                            showSuccess: false
                        },
                        collapse: {
                            model: true
                        },
                        place: "holder"
                    };

                    $log.info("init... done.");
                    $scope.model = model;
                };

                /**
                 * Save koulutus data to tarjonta-service database.
                 * TODO: strict data validation, exception handling and optimistic locking
                 */
                $scope.saveLuonnos = function(tila) {
                    $scope.saveByStatus(tila);
                };
                $scope.saveValmis = function(tila) {
                    $scope.saveByStatus(tila);
                };
                $scope.saveByStatus = function(tila) {
                    $log.info("saveByStatus(): tila = " + tila);

                    if (angular.isUndefined(tila)) {
                        throw "save haku with undefined tila?";
                    }

                    if ($scope.hakuForm.$invalid) {
                        $scope.model.valudation.showError = true;
                        return;
                    }

                    // DO THE SAVE
                    $log.info("saveByStatus(): NOT IMPLEMENTED!");
                };


                /*
                 * WATCHES
                 */

//                $scope.$watch("model.opintojenMaksullisuus", function(valNew, valOld) {
//                    if (!valNew && valOld) {
//                        //clear price data field
//                        $scope.model.hinta = '';
//                    }
//                });

                $scope.init();
            }]);

