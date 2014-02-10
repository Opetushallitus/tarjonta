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
                  
                  console.log("event:", event);
                  
                  console.log("scope hakuform:", $scope);
                  
                    var haku = $scope.model.hakux.result;

                    $log.info("saveLuonnos()", haku);

                    HakuV1.update(haku, function(result) {
                        $log.info("saveLuonnos() - OK", result);

                        $scope.model.showError = true;
                        $scope.model.validationmsgs = result.errors;
                        
                        console.log("->saveparameters");
                        $scope.saveParameters();
                        console.log("saveparameters->");

                    }, function (error) {
                        $log.info("saveLuonnos() - FAILED", error);

                        $scope.model.showError = true;
                    });

                    // $scope.model.showError = !$scope.model.showError;
                    // $scope.model.showSuccess = !$scope.model.showError;
                    // $log.info("saveLuonnos()");



                };

                $scope.saveValmis = function(event) {
                    $log.info("saveValmis()");
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


                $scope.checkHaunNimiValidity = function() {
                    // Count number of keys that have content
                    var numKeys = 0;

                    var result = true;
                    angular.forEach($scope.model.hakux.result.nimi, function (value, key) {
                        numKeys++;

                        result = result && !value;

                        // $log.info("  " + key + " == " + value + " --> result = " + result);

                        // regexp check for empty / whitespace
                        // $log.info("key: " + key + " -- value: " + value);
                    });

                    if (numKeys == 0) {
                        result = true;
                    }

                    // TODO check that at leas kieli_fi is defined?
                    // $log.info("checkHaunNimiValidity() : " + result);

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
                            hakuaikas: [
                                {nimi: null, alkaa: new Date(), loppuu: new Date()}
                            ],

                            date1 : new Date(),
                            date2 : 1380081600000,

                            // State of the checkbox for "oma hakulomake" - if uri is given the use it
                            hakulomakeKaytaJarjestemlmanOmaa: !!$route.current.locals.hakux.hakulomakeUri
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
