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

/**
 * Haku edit controllers.
 *
 * Note: current haku is preloaded in "tarjontaApp.js" route definitions. Extracted in "init()"-method.
 *
 * @param {type} param1
 * @param {type} param2
 */
app.controller('HakuEditController',
        ['$q', '$route', '$scope', '$location', '$log', '$routeParams', '$window', '$modal', 'LocalisationService', 'HakuV1', 'ParameterService',
            function HakuEditController($q, $route, $scope, $location, $log, $routeParams, $window, $modal, LocalisationService, HakuV1, ParameterService) {
                $log.info("HakuEditController()", $scope);

                var hakuOid = $route.current.params.id;

                // TODO preloaded / resolved haku is where?
                // $route.local.xxx
                $scope.model = null;

                $scope.getLocale = function() {
                    return 'FI';
                };

                $scope.doRemoveHakuaika = function(hakuaika, index) {
                    $log.info("doRemoveHakuaika()", hakuaika, index);
                    if ($scope.model.hakux.result.hakuaikas.length > 1) {
                        $scope.model.hakux.result.hakuaikas.splice(index, 1);
                    }
                };

                $scope.doAddNewHakuaika = function() {
                    $log.info("doAddNewHakuaika()");
                    $scope.model.hakux.result.hakuaikas.push({nimi: "", alkuPvm: new Date().getTime(), loppuPvm: new Date().getTime()});
                };

                $scope.goBack = function(event) {
                    $log.info("goBack()");
                };

                $scope.saveLuonnos = function(event) {
                    $log.info("event:", event);
                    $log.info("scope hakuform:", $scope);

                    var haku = $scope.model.hakux.result;
                    haku.tila = "LUONNOS";

                    $log.info("saveLuonnos()", haku);

                    HakuV1.save(haku, function(result) {
                        $log.info("saveLuonnos() - OK", result);
                        $log.info("saveLuonnos() - OK status = ", result.status);

                        // Clear messages
                        if ($scope.model.validationmsgs) {
                            $scope.model.validationmsgs.splice(0, $scope.model.validationmsgs.length);
                        }

                        if (result.status == "OK") {
                            $scope.model.showError = false;
                            $scope.model.showSuccess = true;

                            $log.info("->saveparameters");
                            $scope.saveParameters();
                            $log.info("saveparameters->");

                            // Move broweser to "edit" mode.
                            $location.path("/haku/" + result.result.oid + "/edit");
                        } else {
                            $scope.model.showError = true;
                            $scope.model.showSuccess = false;
                            $scope.model.validationmsgs = result.errors;
                        }

                    }, function (error) {
                        $log.info("saveLuonnos() - FAILED", error);
                        $scope.model.showError = true;
                    });
                };

                $scope.saveValmis = function(event) {
                    $log.info("saveValmis()");
                    $log.info("  event:", event);
                    $log.info("  scope hakuform:", $scope);

                    var haku = $scope.model.hakux.result;
                    haku.tila = "VALMIS";

                    $log.info("saveValmis()", haku);

                    HakuV1.save(haku, function(result) {
                        $log.info("saveValmis() - OK", result);

                        $scope.model.showError = false;
                        $scope.model.showSuccess = true;
                        $scope.model.validationmsgs = result.errors;

                        $log.info("->saveparameters");
                        $scope.saveParameters();
                        $log.info("saveparameters->");

                    }, function (error) {
                        $log.info("saveValmis() - FAILED", error);
                        $scope.model.showError = true;
                    });
                };

                $scope.goToReview = function(event) {
                    $log.info("goToReview()");
                };

                $scope.onStartDateChanged = function(element, hakuaika) {
                    $log.info("onStartDateChanged: " + element + " - " + hakuaika);
                };

                $scope.onEndDateChanged = function(element, hakuaika) {
                    $log.info("onEndDateChanged: " + element + " - " + hakuaika);
                };

                $scope.onDateChanged = function(hakuaika) {
                    $log.info("onDateChanged: " + hakuaika);
                };

                /**
                 * Check if Haku is "new".
                 *
                 * @returns {boolean} true is haku in "model.hakux.result" is NEW (ie. doesn't have OID)
                 */
                $scope.isNewHaku = function() {
                    return angular.isNotDefined($scope.model.hakux.result.oid);
                };

                $scope.checkHaunNimiValidity = function() {
                    // Count number of keys that have content
                    var numKeys = 0;

                    var result = true;
                    angular.forEach($scope.model.hakux.result.nimi, function (value, key) {
                        numKeys++;
                        result = result && !value;
                    });

                    if (numKeys == 0) {
                        result = true;
                    }

                    return result;
                };


                $scope.saveParameters= function() {
                	ParameterService.tallenna(hakuOid, $scope.model.parameter);
                };

                $scope.init = function() {
                    $log.info("init...");


                    var model = {
                        formControls: {},
                        showError: false,
                        showSuccess: false,
                        validationmsgs: [],
                        collapse: {
                            model: true
                        },

                        // Preloaded Haku result
                        hakux : $route.current.locals.hakux,

                        haku: {
                            // State of the checkbox for "oma hakulomake" - if uri is given the use it
                            hakulomakeKaytaJarjestemlmanOmaa: !angular.isDefined($route.current.locals.hakux.result.hakulomakeUri)
                        },

                        parameter: {
                          //parametrit populoituu tänne... ks. haeHaunParametrit(...)

                        }


                    };

                    $log.info("init... done.");
                    $scope.model = model;

                // lataa nykyiset parametrit model.parameter objektiin
                ParameterService.haeHaunParametrit(hakuOid, model.parameter);
              };
              $scope.init();
            } ]);
