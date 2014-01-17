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

app.controller('HakuEditController',
        ['$route', '$scope', '$location', '$log', '$routeParams', '$window', '$modal', 'LocalisationService',
            function HakuEditController($route, $scope, $location, $log, $routeParams, $window, $modal, LocalisationService) {
                $log.info("HakuEditController()", $scope);

                // TODO preloaded / resolved haku is where?
                // $route.local.xxx

                $scope.model = null;

                $scope.getLocale = function() {
                    return 'FI';
                };

                $scope.doRemoveHakuaika = function(hakuaika, index) {
                    $log.info("doRemoveHakuaika()", hakuaika, index);
                    if ($scope.model.haku.hakuaikas.length > 1) {
                        $scope.model.haku.hakuaikas.splice(index, 1);
                    }
                };

                $scope.doAddNewHakuaika = function() {
                    $log.info("doAddNewHakuaika()");
                    $scope.model.haku.hakuaikas.push({nimi: "", alkaa: 0, loppuu: 0});
                };

                $scope.goBack = function(event) {
                    $log.info("goBack()");
                };

                $scope.saveLuonnos = function(event) {
                    $scope.model.showError = !$scope.model.showError;
                    $scope.model.showSuccess = !$scope.model.showError;
                    $log.info("saveLuonnos()");
                };

                $scope.saveValmis = function(event) {
                    $log.info("saveValmis()");
                };

                $scope.goToReview = function(event) {
                    $log.info("goToReview()");
                };

                $scope.checkHaunNimiValidity = function() {
                    $log.info("checkHaunNimiValidity()");
                    var result = false;

                    // At least one name should have real value
                    angular.forEach($scope.model.haku.nimi, function (value, key) {
                        result = result || !value;

                        // regexp check for empty / whitespace
                        // $log.info("key: " + key + " -- value: " + value);
                    });

                    // TODO check that at leas kieli_fi is defined?

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
                            "nimi": {
                                "kieli_fi": "suomi",
                                "kieli_sv": "ruotsi",
                                "kieli_en": "englanti",
                                "kieli_ay": "aimara"
                            },
                            "hakutapaUri": "hakutapa_02",
                            "haunkohdejoukkoUri": "haunkohdejoukko_10",
                            "alkamiskausiUri": "kausi_k",
                            "kausiUri": "kausi_s",
                            "hakutyyppiUri": "hakutyyppi_02",
                            "kausiVuosi": 2013,
                            "alkamiskausiVuosi": 2014,
                            hakuaikas: [
                                {nimi: "Hakuajan nimi 1", alkaa: new Date(), loppuu: new Date()},
                                {nimi: "Hakuajan nimi 2", alkaa: new Date(), loppuu: new Date()}
                            ],
                            hakulomakeKaytaJarjestemlmanOmaa: true
                        },

                        parameter: {
                            julkaisunTakaraja : new Date(),
                            aloituspaikkojenMuokkauksenTakaraja : new Date(),
                            koekutsujenMuodostaminen : {
                                start : new Date(),
                                end : new Date()
                            },
                            harkinnanvarainenValintaTallennusPaattyy : new Date()
                        },


                        place: "holder"
                    };

                    $log.info("init... done.");
                    $scope.model = model;
                };

                $scope.init();
            }]);
