/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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

var app = angular.module('app.haku.review.ctrl', []);

app.controller('HakuReviewController',
        ['$scope', '$route', '$log',
            '$routeParams', 'ParameterService', '$location',
            'HakuV1Service', 'TarjontaService', 'dialogService',
            'LocalisationService', '$q', "PermissionService",
            function HakuReviewController($scope, $route, $log,
                    $routeParams, ParameterService, $location,
                    HakuV1Service, TarjontaService, dialogService,
                    LocalisationService, $q, PermissionService) {

                $log = $log.getInstance("HakuReviewController");
                $scope.isMutable=false;
                $scope.isRemovable=false;

                var hakuOid = $route.current.params.id;

                
                //permissiot
                $q.all([PermissionService.haku.canEdit(hakuOid), PermissionService.haku.canDelete(hakuOid), HakuV1Service.checkStateChange({oid: hakuOid, state: 'POISTETTU'})]).then(function(results) {
                  $scope.isMutable=results[0];
                  $scope.isRemovable=results[1] && results[2].result;
                });
                
                $log.info("  init, args =", $scope, $route, $routeParams);

                // hakux : $route.current.locals.hakux, // preloaded, see "hakuApp.js" route resolve for "/haku/:id"

                $scope.model = null;

                $scope.goBack = function() {
                    $location.path("/haku");
                };

                $scope.doEdit = function() {
                  if(!$scope.isMutable) {
                    return;
                  }
                  $location.path("/haku/" + hakuOid + "/edit");
                };

                $scope.doDelete = function(event) {
                  if(!$scope.isRemovable) {
                    return;
                  }
                    $log.info("doDelete()", event);
                    
                    dialogService.showSimpleDialog(
                            LocalisationService.t("haku.delete.confirmation"),
                            LocalisationService.t("haku.delete.confirmation.description"),
                            LocalisationService.t("ok"),
                            LocalisationService.t("cancel")).result.then(function(result) {
                        $log.info("Dialog result = ", result);
                        if (result) {
                            // In "hakuControllers.js"
                            $scope.doDeleteHaku($scope.model.hakux.result).then(function(result) {
                                if (result) {
                                    // OK, delete - go away
                                    $scope.goBack();
                                } else {
                                    $log.info("delete failed - stay here.");
                                }
                            });
                        }
                    });
                };

                $scope.init = function() {
                    $log.info("HakuReviewController.init()...");

                    $scope.model = {
                        formControls: {},
                        collapse: {
                            haunTiedot: false,
                            haunAikataulut: true,
                            haunMuistutusviestit: true,
                            haunSisaisetHaut: true,
                            haunHakukohteet: true,
                            model: true
                        },
                        // Preloaded Haku result
                        hakux: $route.current.locals.hakux,
                        nimi: HakuV1Service.resolveNimi($route.current.locals.hakux.result),
                        koodis: {
                            koodiX: "..."
                        },
                        haku: {todo: "TODO LOAD ME 1"},
                        hakukohteet: [],
                        place: "holder"
                    };

                    //
                    // Get hakukohdes for current haku
                    //
                    TarjontaService.haeHakukohteet({hakuOid: hakuOid}).then(function(result) {
                        $log.info("GOT HAKUKOHTEET: ", result.tulokset);

                        // Datan rakenne :
                        //            { "oid": "1.2.246.562.10.82388989657", "version": 0, "nimi": "Aalto-korkeakoulusäätiö", 
                        //              "tulokset": [ { 
                        //                "oid": "1.2.246.562.20.924214830310", 
                        //                "nimi": "ertert hkk", 
                        //                "kausi": { "fi": "Syksy", "sv": "Höst", "en": "Autumn" }, 
                        //                "vuosi": 2014, 
                        //                "tila": "LUONNOS", 
                        //                "hakutapa": "Yhteishaku", 
                        //                "aloituspaikat": 56, 
                        //                "tilaNimi": "Luonnos" 
                        //              } ] 
                        //            }

                        // Result list collected here
                        var tmp = [];

                        // Flatten the result list, its grouped by organisations
                        angular.forEach(result.tulokset, function(orgGroup) {
                            // Organisation group
                            angular.forEach(orgGroup.tulokset, function(hakukohde) {
                                // Hakukohdes in organisation, extract name + oid
                                hakukohde.organisaatioNimi = orgGroup.nimi;
                                hakukohde.organisaatioOid = orgGroup.oid;
                                tmp.push(hakukohde);
                            });
                        });

                        $scope.model.hakukohteet = tmp;
                    }, function(error) {
                        $log.error("Failed to get hakukohdes for current haku!", error);
                        tmp.push({organisaatioNimi: "VIRHE HAKUKOHTEIDEN HAUSSA"});
                    });

                    $log.info("HakuReviewController.init()... done.");
                };

                $scope.init();

                $scope.parametrit = {};
                ParameterService.haeParametritUUSI(hakuOid).then(function(parameters) {
                    $scope.parametrit = parameters;
                });
                // ParameterService.haeHaunParametrit(hakuOid, $scope.parametrit);

            }]);
