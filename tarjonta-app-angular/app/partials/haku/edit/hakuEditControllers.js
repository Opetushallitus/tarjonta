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
        function HakuEditController(
                $q,
                $route,
                $scope,
                $location,
                $log,
                $modal,
                LocalisationService,
                HakuV1,
                ParameterService,
                Config,
                OrganisaatioService,
                AuthService,
                dialogService,
                KoodistoURI, 
                PermissionService, 
                HakuV1Service,
                TarjontaService) {
            $log = $log.getInstance("HakuEditController");
            $log.debug("initializing (scope, route)", $scope, $route);

            // Reset model to empty
            $scope.model = null;

            var clearErrors = function() {
                $scope.model.validationmsgs = [];
                // NOTE data model for formControl seems to accumulate errors, clear it here even if the doc says no
                $scope.model.formControls.notifs.errorDetail = [];
            };


            var checkIsOphAdmin = function() {


                if (AuthService.isUserOph()) {

                    $scope.filteruris = undefined;

                } else {

                    $scope.filteruris = [];
                    $scope.filteruris.push('hakutapa_01');
                    $log.info('filteruris : ', $scope.filteruris);

                }

            };

            /**
             * Display form validation errors on screen
             */
            var reportFormValidationErrors = function(form) {
                $log.debug("reportFormValidationErrors - form:::::", form);

                $log.debug("form", form);
                angular.forEach(form.$error, function(v, k) {
                    for (var i = 0; i < v.length; i++) {
                        if (v[i].$name) {
                            var key = "error.validation." + v[i].$name + "." + k;
                            $log.debug("k:" + key);
                            $scope.model.validationmsgs.push({errorMessageKey: key});
                        } else {
                            $log.debug("error found for field:", v[i], "key=", k)
                        }
                    }
                });

                $scope.model.showError = true;
                $scope.model.showSuccess = false;
            }

            $scope.doRemoveHakuaika = function(hakuaika, index) {
                $log.info("doRemoveHakuaika()", hakuaika, index);
                if ($scope.model.hakux.result.hakuaikas.length > 1) {
                    $scope.model.hakux.result.hakuaikas.splice(index, 1);
                } else {
                   $log.info("  cowardly refusing to remove the last hakuaika...");
                }
            };

            $scope.doAddNewHakuaika = function() {
                $log.info("doAddNewHakuaika()");
                $scope.model.hakux.result.hakuaikas.push({nimi: "", alkuPvm: null, loppuPvm: null});
            };

            $scope.goBack = function(event, hakuForm) {
                $log.info("goBack()", hakuForm);
                
                var dirty = angular.isDefined(hakuForm.$dirty) ? hakuForm.$dirty : false;
                $log.info("goBack(), dirty?", dirty);
                
                if (dirty) {
                    dialogService.showModifedDialog().result.then(function(result) {
                        if (result) {
                            $scope.navigateBack();
                        }
                    });
                } else {
                    $scope.navigateBack();
                }
            };
            
            $scope.navigateBack = function() {
                // TODO old query parameters?
                $location.path("/haku");
            };

            $scope.saveLuonnos = function(event, form) {
                $log.info("saveLuonnos()", event, form);
                var haku = $scope.model.hakux.result;
                $scope.doSaveHakuAndParameters(haku, "LUONNOS", true, form);
            };

            $scope.saveValmis = function(event, form) {
                $log.info("saveValmis()", event, form);
                var haku = $scope.model.hakux.result;
                $scope.doSaveHakuAndParameters(haku, "VALMIS", true, form);
            };

            $scope.doSaveHakuAndParameters = function(haku, tila, reload, form) {

                $log.info("doSaveHakuAndParameters() [haku, tila, reload, form]", haku, tila, reload, form);

                clearErrors();
                
                if (form.$invalid) {
                    $log.info("form not valid, not saving!");
                    reportFormValidationErrors(form);
                    return;
                }

                // Update haku's tila (state)
                if(haku.tila!="JULKAISTU") { //älä muuta julkaistun tilaa
                  haku.tila = tila;
                }

                // When saving JATKUVA HAKU, reset year and term info
                if ($scope.isJatkuvaHaku()) {
                    $log.info("  this seems to be JATKUVA HALU - maybe reset vuosi kausi info?", haku);
//                    haku.hakukausiUri = null;
//                    haku.hakukausiVuosi = 0;
//                    haku.koulutuksenAlkamiskausiUri = null;
//                    haku.koulutuksenAlkamisVuosi = 0;
                }

                // Save it
                HakuV1.save(haku, function(result) {
                    $log.debug("doSaveHakuAndParameters() - haku save OK", result);

                    // Clear validation messages
                    $log.debug("validation messages:", $scope.model.validationmsgs);
                    $log.debug("$scope.hakuForm B:", $scope.hakuForm)
                    if ($scope.model.validationmsgs && $scope.model.validationmsgs.length > 0) {
                        $scope.model.validationmsgs.splice(0, $scope.model.validationmsgs.length);
                    }
                    $log.debug("validation messages after splice:", $scope.model.validationmsgs);

                    if (result.status == "OK") {
                        $scope.model.showError = false;
                        $scope.model.showSuccess = true;
                        
                        // Reset form to "pristine" ($dirty = false)
                        form.$dirty = false;
                        form.$pristine = true;

                        $log.info("->saveparameters");
                        $scope.saveParameters(result.result);
                        $log.info("saveparameters->");

                        // If this is new haku, then move to edit url
                        if ($scope.isNewHaku()) {
                            $log.debug("  change model to be fresh from server AND update browser URL");
                            // Also update UI model to be fresh from the server
                            // TODO any other changes to UI model needed?
                            $scope.model.hakux = result;
                            // Change url
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
                    $log.info("doSaveHakuAndParameters() - FAILED", error);
                    $scope.model.showError = true;
                });
            };

            $scope.goToReview = function(event, hakuForm) {                
                var dirty = angular.isDefined(hakuForm.$dirty) ? hakuForm.$dirty : false;
                $log.debug("goToReview(), dirty?", dirty);

                if (dirty) {
                    dialogService.showModifedDialog().result.then(function(result) {
                        if (result) {
                            $scope.navigateToReview();
                        }
                    });
                } else {
                    $scope.navigateToReview();
                }
            };

            $scope.navigateToReview = function(event) {
                $location.path("/haku/" + $scope.model.hakux.result.oid);                
            };


            /**
             * Check if Haku is "new".
             *
             * @returns {boolean} true is haku in "model.hakux.result" is NEW (ie. doesn't have OID)
             */
            $scope.isNewHaku = function() {
                var result = !angular.isDefined($scope.model.hakux.result.oid);
                // $log.debug("isNewHaku()", result);
                return result;
            };

            $scope.checkHaunNimiValidity = function() {
            	if (!$scope.model.showError) {
            		return false;
            	}
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
                var kielet = [LocalisationService.getKieliUri(), "kieli_fi", "kieli_sv", "kieli_en"];

                var result;

                // Take first matching name in sequence: [current locale, fi, sv, en]
                angular.forEach(kielet, function(kieli) {
                    if (!angular.isDefined(result) && angular.isDefined(nimi[kieli])) {
                        result = nimi[kieli];
                    }
                });

                if (!angular.isDefined(result)) {
                    result = "HAUN NIMI EI TIEDOSSA";
                }

                return result;
            };

            var stringContainsOther = function (fullString,otherString) {

                if(fullString && otherString) {

                    if(fullString.indexOf(otherString) != -1) {
                        return true;
                    } else {
                        return false;
                    }

                } else {
                    return false;
                }

            };

            $scope.filterKohdejoukkos = function () {

              if(!AuthService.isUserOph())
               {


                    var kkOppilaitosTyypit = {};
                    kkOppilaitosTyypit["oppilaitostyyppi_42"] = "";
                    kkOppilaitosTyypit["oppilaitostyyppi_41"] = "";
                    kkOppilaitosTyypit["oppilaitostyyppi_43"] = "";

                    var userOrgs = AuthService.getOrganisations();

                    //CHeck if user has KK - orgs
                    angular.forEach(userOrgs, function (org) {
                        OrganisaatioService.haeOppilaitostyypit(org).then(function (oppilaitosTyypit) {

                            angular.forEach(oppilaitosTyypit, function (oppilaitosTyyppi) {
                                var oppilaitosTyyppiUriWithoutVersion = oppilaitosTyyppi.split("#");
                                //User belongs to KK - org, check if he/she has CRUD
                                if(oppilaitosTyyppiUriWithoutVersion[0] in kkOppilaitosTyypit) {
                                    AuthService.crudOrg(org).then(function (isCrud) {
                                         console.log('IS CRUD : ', isCrud);
                                        if(isCrud) {
                                               $scope.model.kohdejoukkoFilterUris = ["haunkohdejoukko_12"];

                                        }



                                    });

                                }
                            })

                        });

                    });
                }



            };


            $scope.isJatkuvaHaku = function() {
                var result = $scope.isHakuJatkuvaHaku($scope.model.hakux.result);
                // $log.info("isJatkuvaHaku()", result);
                return result;
            };


            $scope.saveParameters = function(haku) {
                $log.info("saveParameters()");
                ParameterService.tallennaUUSI(haku.oid, $scope.model.parameter);
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
                        },
                        treeId:function(){
                          return "org1";
                        }
                    }
                    // , scope: $scope
                });

                modalInstance.result.then(function(oids) {
                    $log.debug("OK - dialog closed with selected organisations: ", oids);
                    $scope.model.hakux.result.organisaatioOids = oids;
                    $scope.updateSelectedOrganisationsList();
                }, function (oids) {
                    // dismissed - no changes to oids
                });
            };


            /**
             * Loop throuh list of selected / preselected tarjoaja organisations, 
             * fetch them and put them to the scope for display purposes.
             *
             * @returns {undefined}
             */
            $scope.updateSelectedTarjoajaOrganisationsList = function() {
                $log.info("updateSelectedTarjoajaOrganisationsList()");

                $scope.model.selectedTarjoajaOrganisations = [];

                angular.forEach($scope.model.hakux.result.tarjoajaOids, function(organisationOid) {
                    $log.info("  get ", organisationOid);
                    OrganisaatioService.byOid(organisationOid).then(function(organisation) {
                        $log.info("    got ", organisation);
                        $scope.model.selectedTarjoajaOrganisations.push(organisation);
                    });
                });
            };


            /**
             * Opens dialog for selecting tarjoaja organisations.
             * Updates model for the list of selected tarjoaja organisations.
             *
             * @returns {undefined}
             */
            $scope.doSelectTarjoajaOrganisations = function() {
                $log.info("doSelectTarjoajaOrganisations()");
                var modalInstance = $modal.open({
                    controller: 'HakuEditSelectOrganisationsController',
                    templateUrl: "partials/haku/edit/select-organisations-dialog.html",
                    resolve : {
                        organisaatioOids : function() {
                            return $scope.model.hakux.result.tarjoajaOids;
                        },
                        treeId:function(){
                          return "org2";
                        }
                    }
                    // , scope: $scope
                });

                modalInstance.result.then(function(oids) {
                    $log.debug("OK - dialog closed with selected tarjoaja organisations: ", oids);
                    $scope.model.hakux.result.tarjoajaOids = oids;
                    $scope.updateSelectedTarjoajaOrganisationsList();
                }, function (oids) {
                    // dismissed - no changes to oids
                });
            };

            $scope.checkPriorisointi = function () {
                $log.debug("checkPriorisointi()");
                
                if ($scope.model.hakux.result.jarjestelmanHakulomake && $scope.model.hakux.result.sijoittelu) {
                    $scope.model.hakux.result.usePriority = true;
                }
                
                if (!$scope.model.hakux.result.jarjestelmanHakulomake) {
                    $scope.model.hakux.result.usePriority = false;
                }                
            };

            /**
             * Initialize controller and ui state.
             *
             * @returns {undefined}
             */
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
                        // Possible UI state for Haku
                    },
                    parameter: {
                        //parametrit populoituu tänne... ks. haeHaunParametrit(...)
                    },
                    selectedOrganisations: [], // updated in $scope.updateSelectedOrganisationsList()
                    selectedTarjoajaOrganisations: [], // updated in $scope.updateSelectedOrganisationsList()
                    config: Config.env
                };

                $log.info("init... done.");
                $scope.model = model;


                
                if(!$scope.isNewHaku()){
                  // lataa nykyiset parametrit model.parameter objektiin
                  ParameterService.haeParametritUUSI($route.current.params.id).then(function(parameters){
                    model.parameter=parameters;
                  });
                }

                /**
                 * If this is new haku initialize selected organisations with users list of organisations.
                 */
                if ($scope.isNewHaku()) {
                    $scope.model.hakux.result.organisaatioOids = AuthService.getOrganisations();
                    $scope.model.hakux.result.tarjoajaOids = AuthService.getOrganisations();
                    $log.info("NEW HAKU: hakukohde organisationOids: ", $scope.model.hakux.result.organisaatioOids);
                    $log.info("NEW HAKU: tarjoaja organisationOids: ", $scope.model.hakux.result.organisaatioOids);
                }

                // Fetch organisations for display
                $scope.updateSelectedOrganisationsList();
                $scope.updateSelectedTarjoajaOrganisationsList();
                //Filter kohdejoukkos
                $scope.filterKohdejoukkos();
                checkIsOphAdmin();
            };
            $scope.init();
            
            var hakuOid = $route.current.params.id;
            
//
//            TarjontaService.parameterCanEditHakukohde(hakuOid);
//            TarjontaService.parameterCanEditHakukohdeLimited(hakuOid);
//            TarjontaService.parameterCanAddHakukohdeToHaku(hakuOid);
//            TarjontaService.parameterCanRemoveHakukohdeFromHaku(hakuOid);
//

            if(!$scope.isNewHaku()) {
              //permissiot
              $q.all([PermissionService.haku.canEdit(hakuOid), PermissionService.haku.canDelete(hakuOid), HakuV1Service.checkStateChange({oid: hakuOid, state: 'POISTETTU'})]).then(function(results) {
                $scope.isMutable=results[0];
                $scope.isRemovable=results[1] && results[2];
              });
              
              PermissionService.getPermissions("haku", hakuOid).then(function(permissions) {
                  $log.info("got permissions! ", permissions);
              });
              
            } else {
              //uusi haku
              $scope.isMutable=true;
            }

            $scope.isLuonnosOrNew = function(){
              return $scope.isNewHaku() || $scope.model.hakux.result.tila==='LUONNOS';
            };


        });
