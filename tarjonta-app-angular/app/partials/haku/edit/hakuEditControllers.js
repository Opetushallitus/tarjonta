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
        ['$route', '$scope', '$location', '$log', '$routeParams', '$window', '$modal', 'LocalisationService', 'HakuV1',
            function HakuEditController($route, $scope, $location, $log, $routeParams, $window, $modal, LocalisationService, HakuV1) {
                $log.info("HakuEditController()", $scope);

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
                    var haku = $scope.model.hakux.result;

                    $log.info("saveLuonnos()", haku);

                    HakuV1.update(haku, function(result) {
                        $log.info("saveLuonnos() - OK", result);

                        $scope.model.showError = true;
                        $scope.model.validationmsgs = result.errors;

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
                            // Tarjonnan julkaisu ja hakuaika
                            PH_TJT : new Date(),
                            PH_HKLPT : new Date(),
                            PH_HKMT : new Date(),

                            // Valinnat ja sijoittelu
                            PH_KKM_S : new Date(),
                            PH_KKM_E : new Date(),
                            PH_HVVPTP : new Date(),
                            PH_KTT_S : new Date(),
                            PH_KTT_E : new Date(),
                            PH_OLVVPKE_S : new Date(),
                            PH_OLVVPKE_E : new Date(),
                            PH_VLS_S : new Date(),
                            PH_VLS_E : new Date(),
                            PH_SS_S : new Date(),
                            PH_SS_E : new Date(),
                            PH_SSAVTM : true,
                            PH_SST : 48,
                            PH_SSKA : "23:59",
                            PH_VTSSV : new Date(), // kk
                            PH_VSSAV : new Date(), // kk

                            // Tulokset ja paikan vastaanotto
                            PH_JKLIP : new Date(),
                            PH_HKP : new Date(),
                            PH_VTJH_S : new Date(),
                            PH_VTJH_E : new Date(),
                            PH_EVR : new Date(),
                            PH_OPVP : new Date(),
                            PH_HPVOA : 7,

                            // Lisähaku
                            PH_HKTA : new Date(),
                            // PH_HKP : new Date(),

                            // Hakukauden parametrit
                            PHK_PLPS_S : new Date(),
                            PHK_PLPS_E : new Date(),
                            PHK_PLAS_S : new Date(),
                            PHK_PLAS_E : new Date(),
                            PHK_LPAS_S : new Date(),
                            PHK_LPAS_E : new Date(),

                            // Tiedonsiirto
                            PHK_KTTS : new Date(),
                            PHK_TAVS_S : new Date(),
                            PHK_TAVS_E : new Date(),
                            PHK_TAVSM : true,
                            PHK_KAVS_S : new Date(),
                            PHK_KAVS_E : new Date(),
                            PHK_KAVSM : true,
                            PHK_VTST : 2,
                            PHK_VTSAK : "23:59",

                            place: "Holder"
                        },


                        place: "holder"
                    };

                    $log.info("init... done.");
                    $scope.model = model;
                };

                $scope.init();
            }]);
