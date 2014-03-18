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
        function HakuEditController($q, $route, $scope, $location, $log, $routeParams, $window, $modal, LocalisationService, HakuV1, ParameterService, Config, OrganisaatioService) {
            $log.info("HakuEditController()", $scope);

            var clearErrors = function() {
                $scope.model.validationmsgs = [];
                //XXX data model for formControl seems to accumulate errors, clear it here even if the doc says no
                $scope.model.formControls.notifs.errorDetail = [];
            };

            /**
             * Display form validation errors on screen
             */
            var reportFormValidationErrors = function(form) {
                console.log("reportFormValidationErrors - form:::::", form);

//                    angular.forEach(form, function(value,name){
//                      if(value.$invalid===true) {
//                        var key = "error.validation." + name + "." + name;
//                        console.log("k:" + key);
//                        $scope.model.validationmsgs.push({errorMessageKey:key});
//                      }
//
//                    });

                console.log("form", form);
                angular.forEach(form.$error, function(v, k) {
                    for (var i = 0; i < v.length; i++) {
                        if (v[i].$name) {
                            var key = "error.validation." + v[i].$name + "." + k;
                            console.log("k:" + key);
                            $scope.model.validationmsgs.push({errorMessageKey: key});
                        } else {
                            console.log("error found for field:", v[i], "key=", k)
                        }
                    }
                });

                $scope.model.showError = true;
                $scope.model.showSuccess = false;
            }


            var hakuOid = $route.current.params.id;

            // TODO preloaded / resolved haku is where?
            // $route.local.xxx
            $scope.model = null;

//                $scope.getLocale = function() {
//                    return 'FI';
//                };

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
                // TODO old query parameters?
                $location.path("/haku");
            };

            $scope.saveLuonnos = function(event) {
                $log.info("event:", event);
                $log.info("scope hakuform:", $scope);

                var haku = $scope.model.hakux.result;
                $scope.doSaveHakuAndParameters(haku, "LUONNOS", true);
            };

            $scope.saveValmis = function(event) {
                $log.info("saveValmis()");
                $log.info("  event:", event);
                $log.info("  scope hakuform:", $scope);

                var haku = $scope.model.hakux.result;
                $scope.doSaveHakuAndParameters(haku, "VALMIS", true);
            };

            $scope.doSaveHakuAndParameters = function(haku, tila, reload) {

                clearErrors();
                var form = $scope.hakuForm;
                if (form.$invalid) {
                    $log.info("form not valid, not saving!");
                    reportFormValidationErrors(form);
                    return;
                }



                $log.info("doSave()", tila, haku);
                // Update haku's tila (state)
                haku.tila = tila;

                // Save it
                HakuV1.save(haku, function(result) {
                    $log.info("doSave() - OK", result);
                    $log.info("doSave() - OK status = ", result.status);

                    // Clear validation messages
                    console.log("validation messages:", $scope.model.validationmsgs);
                    console.log("fc:", $scope.formControl)
                    if ($scope.model.validationmsgs && $scope.model.validationmsgs.length > 0) {
                        $scope.model.validationmsgs.splice(0, $scope.model.validationmsgs.length);
                    }
                    console.log("validation messages after splice:", $scope.model.validationmsgs);

                    if (result.status == "OK") {
                        $scope.model.showError = false;
                        $scope.model.showSuccess = true;

                        $log.info("->saveparameters");
                        $scope.saveParameters(result.result);
                        $log.info("saveparameters->");

                        // Move broweser to "edit" mode.
                        if (reload) {
                            $location.path("/haku/" + result.result.oid + "/edit");
                        }
                    } else {
                        // Failed to save Haku... show errors
                        $scope.model.showError = true;
                        $scope.model.showSuccess = false;
                        $scope.model.validationmsgs = result.errors;
                    }

                }, function(error) {
                    // Mainly 50x errors
                    $log.info("saveLuonnos() - FAILED", error);
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
                angular.forEach($scope.model.hakux.result.nimi, function(value, key) {
                    numKeys++;
                    result = result && !value;
                });

                if (numKeys == 0) {
                    result = true;
                }

                return result;
            };

            /**
             * Try to get name of the Haku.
             *
             * @returns {String}
             */
            $scope.getHaunNimi = function() {
                var nimi = $scope.model.hakux.result.nimi;
                var kieliUri = LocalisationService.getKieliUri();

                var kielet = [LocalisationService.getKieliUri(), "kieli_fi", "kieli_sv", "kieli_en"];

                var result;

                // Take first matching name
                angular.forEach(kielet, function(kieli) {
                    if (!angular.isDefined(result) && angular.isDefined(nimi[kieli])) {
                        result = nimi[kieli];
                    }
                });

                if (!angular.isDefined(result)) {
                    result = "EI TIEDOSSA";
                }

                return result;
            };


            /**
             *
             * @returns true if current haku is JATKUVA_HAKU
             */
            $scope.isJatkuvaHaku = function() {
                return $scope.model.hakux.result.hakutapaUri == Config.env["koodisto.hakutapa.jatkuvaHaku.uri"];
            };


            $scope.saveParameters = function(haku) {
                $log.info("saveParameters()");
                ParameterService.tallenna(haku.oid, $scope.model.parameter);
            };


            /**
             * Loop throuh list of selected / preselected organisations, fetch them and put them to the scope for display purposes.
             *
             * @returns {undefined}
             */
            $scope.updateSelectedOrganisationsList = function() {
                $log.info("updateSelectedOrganisationsList()");

                $scope.model.selectedOrganisations = [];

                angular.forEach($scope.model.hakux.result.organisaatioOids, function(organisationOid) {
                    $log.info("  get ", organisationOid);
                    OrganisaatioService.byOid(organisationOid).then(function(organisation) {
                        $log.info("    got ", organisation);
                        $scope.model.selectedOrganisations.push(organisation);
                    });
                });
            };

            /**
             * Opens dialog for selecting organisations.
             * Updates model for the list of selected organisations.
             *
             * @returns {undefined}
             */
            $scope.doSelectOrganisations = function() {
                $log.info("doSelectOrganisations()");
                var modalInstance = $modal.open({
                    controller: 'HakuEditSelectOrganisationsController',
                    templateUrl: "partials/haku/edit/select-organisations-dialog.html",
                    resolve : {
                        organisaatioOids : function() {
                            return $scope.model.hakux.result.organisaatioOids;
                        }
                    }
                    // , scope: $scope
                });

                modalInstance.result.then(function(oids) {
                    $log.info("OK: ", oids);
                    $scope.model.hakux.result.organisaatioOids = oids;
                    $scope.updateSelectedOrganisationsList();
                }, function (oids) {
                    $log.info("DISMISS?: ", oids);
                });
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
                    hakux: $route.current.locals.hakux,
                    haku: {
                        // State of the checkbox for "oma hakulomake" - if uri is given the use it
                        hakulomakeKaytaJarjestemlmanOmaa: !angular.isDefined($route.current.locals.hakux.result.hakulomakeUri)
                    },
                    parameter: {
                        //parametrit populoituu tänne... ks. haeHaunParametrit(...)

                    },
                    selectedOrganisations: [], // updated in $scope.updateSelectedOrganisationsList()
                    config: Config.env
                };

                $log.info("init... done.");
                $scope.model = model;

                // lataa nykyiset parametrit model.parameter objektiin
                ParameterService.haeHaunParametrit(hakuOid, model.parameter);

                // Fetch organisations for display
                $scope.updateSelectedOrganisationsList();
            };
            $scope.init();
        });
