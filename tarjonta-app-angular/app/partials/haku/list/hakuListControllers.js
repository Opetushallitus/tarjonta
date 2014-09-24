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


var app = angular.module('app.haku.list.ctrl', ['ResultsTreeTable']);

app.controller('HakuListController',
        ['$q', '$scope', '$location',
            '$log', '$window', '$modal',
            'LocalisationService', 'HakuV1', 'dialogService',
            'HakuV1Service', 'Koodisto', 'PermissionService',
            'loadingService', 'OrganisaatioService', 'AuthService',
            function HakuListController($q, $scope, $location,
                    $log, $window, $modal,
                    LocalisationService, Haku, dialogService,
                    HakuV1Service, Koodisto, PermissionService,
                    loadingService, OrganisaatioService, AuthService) {

//
// OVT-8275 ? Tama on myos tehty tuolla tarjontaApp init:ssä...
// 
//                PermissionService.permissionResource().authorize({}, function(response) {
//                    $log.debug("Authorization check : " + response.result);
//                });

                $log = $log.getInstance("HakuListController");

                //sorting
                $scope.reverse = false;
                $scope.kausi = [];
                $scope.vuosi = [];
                $scope.hakutyypit = [];

                $log.info("HakuListController()");

                // estää hakunapin enabloinnin ennen kuin koodistot on haettu
                $scope.unloadedKoodis = 0;

                //vuodet
                for (var y = new Date().getFullYear() - 2; y < new Date().getFullYear() + 10; y++) {
                    $scope.vuosi.push(y);
                }

                function loadKoodistoAsMap(koodisto, scopeId) {
                    $scope.unloadedKoodis++;
                    loadingService.beforeOperation();

                    Koodisto.getAllKoodisWithKoodiUri(koodisto).then(function(items) {

                        $scope[scopeId] = {};

                        for (var i in items) {
                            var k = items[i];
                            $scope[scopeId][k.koodiUri + "#" + k.koodiVersio] = k.koodiNimi;
                        }

                        $scope.unloadedKoodis--;
                        loadingService.afterOperation();
                    });
                }

                // HUOM! nämä koodistot pitää olla _ladattuna_ ennen kuin hakutulostaulukkoa populoidaan
                loadKoodistoAsMap("kausi", "kaudet");
                loadKoodistoAsMap("hakutyyppi", "hakutyypit");

                $scope.states = [];
                $scope.vuosikausi = [];
                $scope.selection = [];

                for (var s in CONFIG.env["tarjonta.tila"]) {
                    $scope.states[s] = LocalisationService.t("tarjonta.tila." + s);
                }

                $scope.clearSearch = function() {
                    $scope.searchParams = {
                        HAKUSANA: undefined,
                        TILA: undefined,
                        KAUSI: undefined,
                        VUOSI: undefined,
                        KAUSIVUOSI: "HAKU",
                        HAKUTAPA: undefined,
                        HAKUTYYPPI: undefined,
                        KOHDEJOUKKO: undefined
                    };
                };

                $scope.clearSearch();

                $scope.doCreateNew = function() {
                    $log.info("doCreateNew()");
                    $location.path("/haku/NEW");
                };

                $scope.canCreateNew = function() {
                    // TODO käyttöoikeustarkistus
                    return true;
                };

                function kausiVuosiToString(kausi, vuosi) {
                    return kausi ? $scope.kaudet[kausi] + " " + vuosi : vuosi;
                }

                $scope.hakuGetContent = function(row, col) {
                    switch (col) {
                        case "hakutyyppi":
                            return $scope.hakutyypit[row.hakutyyppiUri];
                        case "hakukausi":
                            return kausiVuosiToString(row.hakukausiUri, row.hakukausiVuosi);
                        case "alkamiskausi":
                            return kausiVuosiToString(row.koulutuksenAlkamiskausiUri, row.koulutuksenAlkamisVuosi);
                        case "tila":
                            return LocalisationService.t("tarjonta.tila." + row.tila);
                        default:
                            return row.nimi;
                    }
                };
                $scope.hakuGetIdentifier = function(row) {
                    return row.oid;
                };
                $scope.hakuGetLink = function(row) {
                    return "#/haku/" + row.oid;
                };
                $scope.hakuGetOptions = function(row) {
                    return undefined;
                };

                /**
                 * Deletes single selected haku.
                 * 
                 * @param {type} haku
                 * @param {type} doAfter
                 * @returns {undefined}
                 */
                $scope.doDelete = function(haku, doAfter) {
                    $log.info("doDelete()", haku);

                    // Defined: "hakuControllers.js"
                    $scope.doDeleteHaku(haku).then(function(result) {
                        $log.info("  result = ", result);
                        if (result) {
                            doAfter();
                        }
                    });

                };

                /**
                 * Delete selected hakus.
                 * 
                 * @returns {undefined}
                 */
                $scope.doDeleteSelected = function() {
                    $log.debug("doDeleteSelected()");
                    $log.debug("selected:", $scope.selection);

                    Haku.mget({oid: $scope.selection}).$promise.then(function(hakus) {
                        $log.info("  loaded hakus: ", hakus);
                        if (hakus.status === "OK") {
                            angular.forEach(hakus.result, function(haku) {
                                $scope.doDelete(haku, function() {
                                    $log.info("DO AFTER...", haku);
                                    for (var i in $scope.model.hakus) {
                                    	if ($scope.model.hakus[i].oid == haku.oid) {
                                    		$scope.model.hakus[i].$delete();
                                    	}
                                    }
                                });
                            });
                        }
                    });
                };

                /**
                 * If true, selected hakus "delete" button is active.
                 * 
                 * @returns {Boolean}
                 */
                $scope.canDeleteSelected = function() {
                    // TODO käyttöoikeustarkistus -> jos ei saa poistaa mitään, palauta false
                    return $scope.selection.length > 0;
                };

                /**
                 * Go to review display.
                 * 
                 * @param {type} haku
                 * @returns {undefined}
                 */
                $scope.review = function(haku) {
                    $location.path("/haku/" + haku.oid);
                };

                /**
                 * Change state of haku to targetState.
                 * 
                 * Returns function that will be bound to correct haku row.
                 * 
                 * @param {type} targetState
                 * @returns {Function}
                 */
                function changeState(targetState, prefix, confirmationDescription) {
                  
                  var title = LocalisationService.t(prefix + ".confirmation.title");
                  var description = LocalisationService.t(prefix + ".confirmation.description");
                  var okAckTitle = LocalisationService.t(prefix + ".ack.title");
                  var okAckDescription = LocalisationService.t(prefix + ".ack.description");
                  var errorAckTitle = LocalisationService.t(prefix + ".error.ack.title");
                  var errorAckDescription = LocalisationService.t(prefix + ".error.ack.description");
                  
                    return function(haku, doAfter, onlyHaku) {

                      /**
                       * Kerro käyttäjälle että operaatio suoritettu
                       */
                      function after(ackTitle, ackDescription){
                        dialogService.showSimpleDialog(
                            ackTitle,
                            ackDescription,
                            LocalisationService.t("ok"))
                            .result
                            .then(function(ok) {
                            });
                      }


                      function change(haku, doAfter) {

                        Haku.changeState({oid: haku.oid, state: targetState, "onlyHaku":onlyHaku||false}).$promise.then(function(result) {
                            $log.debug("call done:", result);
                            if ("OK" === result.status) {
                                haku.tila = targetState;
                                doAfter();
                                after(okAckTitle, okAckDescription);
                            } else {
                                $log.debug("state change did not work?", result);
                                after(errorAckTitle, errorAckDescription);
                            }
                        }, function(reason) {
                          
                        });
                      }
                      
                      return dialogService.showSimpleDialog(
                          title,
                          description,
                          LocalisationService.t("ok"),
                          LocalisationService.t("cancel"))
                          .result
                          .then(function(ok) {
                              if (ok) {
                                  $log.info(" -> verified.");
                                  return change(haku, doAfter);
                              } else {
                                  $log.info(" -> cancelled.");
                                  return false;
                              }
                          });
                    };
                }

                $scope.doPublish = changeState("JULKAISTU", "haku.publish");
                $scope.doCancel = changeState("PERUTTU", "haku.cancel");

                $scope.hakuGetOptions = function(haku, actions) {

                    var ret = [];

                    //hae permissiot ja testaa tilasiirtymät
                    $q.all([PermissionService.haku.canEdit(haku.oid), PermissionService.haku.canDelete(haku.oid), Haku.checkStateChange({oid: haku.oid, state: 'JULKAISTU'}).$promise, Haku.checkStateChange({oid: haku.oid, state: 'PERUTTU'}).$promise]).then(function(results) {
                        if (true === results[0]) {
                            //edit
                            ret.push({title: LocalisationService.t("haku.menu.muokkaa"), action:
                                        function() {
                                            $location.path("/haku/" + haku.oid + "/edit");
                                        }
                            });
                        }

                        $log.debug("results", results);

                        //review
                        ret.push({title: LocalisationService.t("haku.menu.tarkastele"), action: function() {
                                $scope.review(haku);
                            }});

                        //delete
                        if (true === results[1] && haku.tila != 'JULKAISTU') {
                            ret.push({title: LocalisationService.t("haku.menu.poista"), action: function() {
                                    $scope.doDelete(haku, haku.$delete);
                                }});
                        }

                        //publish
                        if (true === results[0] && true === results[2].result) {
                            ret.push({title: LocalisationService.t("haku.menu.julkaise"), action: function() {
                                    $scope.doPublish(haku, haku.$update, true);
                                }});

                            //recursively
                            ret.push({title: LocalisationService.t("haku.menu.julkaise.rekursiivisesti"), action: function() {
                                $scope.doPublish(haku, haku.$update);
                            }});

                        }

                        //cancel
                        if (true === results[0] && true === results[3].result && haku.tila != 'PERUTTU') {
                            ret.push({title: LocalisationService.t("haku.menu.peruuta"), action: function() {
                                    $scope.doCancel(haku, haku.$update);
                                }});
                        }

                    });

                    return ret;
                };

                $scope.doSearch = function() {
                    $log.info("doSearch()");
                    var params = angular.copy($scope.searchParams);

                    if (params.KAUSIVUOSI && params.KAUSI) {
                        params[params.KAUSIVUOSI + "KAUSI"] = params.KAUSI;
                    }
                    if (params.KAUSIVUOSI && params.VUOSI) {
                        params[params.KAUSIVUOSI + "VUOSI"] = params.VUOSI;
                    }

                    // pois turhat parametrit
                    params.KAUSIVUOSI = undefined;
                    params.KAUSI = undefined;
                    params.VUOSI = undefined;

                    /**
                     * Allow user only to select values included in the typeahead response. If value is not
                     * in the response (user wrote manually something else) => clear the input to indicate that
                     * the filter value is invalid.
                     */
                    var $organisationFilter = angular.element('#organisationFilter');
                    if ( selectedOrganisation === null || $organisationFilter.val() !== selectedOrganisation.nimi ){
                        delete params.TARJOAJAOID;
                        $organisationFilter.val('');
                        selectedOrganisation = null;
                    }

                    HakuV1Service.search(params).then(function(haut) {
                        // TODO järjestys backendiin?
                        haut.sort(function(a, b) {
                            var ret = a.tila.localeCompare(b.tila);
                            if (ret === 0) {
                                ret = a.nimi.localeCompare(b.nimi);
                            }
                            return ret;
                        });

                        angular.forEach(haut, function (haku) {

                            if(haku.koulutuksenAlkamisVuosi === 0) {
                                haku.koulutuksenAlkamisVuosi = '';

                            }

                        });
                        $scope.model.hakus = haut;
                    }
                    );
                };

                $scope.searchOrganisations = function(qterm) {

                    /**
                     * Recursively process the organizations results, so that all the children are also
                     * included in the final result set.
                     */
                    function processOrganizations(alreadyAddedOrganizations, newOrganizations, hierarchyLevel) {
                        hierarchyLevel = hierarchyLevel || 0;

                        angular.forEach(newOrganizations, function(org) {
                            org.nimi = new Array(hierarchyLevel + 1).join('- ') + org.nimi;
                            alreadyAddedOrganizations.push(org);

                            if ( angular.isArray(org.children) ) {
                                alreadyAddedOrganizations = processOrganizations(alreadyAddedOrganizations, org.children, hierarchyLevel + 1);
                            }
                        });

                        return alreadyAddedOrganizations;
                    }

                    return OrganisaatioService.etsi({
                        searchStr: qterm,
                        lakkautetut: false,
                        skipparents: false,
                        suunnitellut: false
                    }).then(function(result) {
                        return processOrganizations([], result.organisaatiot);
                    });
                };

                var selectedOrganisation;
                $scope.filterByOrganisation = function(organization) {
                    selectedOrganisation = organization;

                    $scope.searchParams.TARJOAJAOID = organization.oid;

                    function includeChildren(parent) {
                        angular.forEach(parent.children, function(child) {
                            $scope.searchParams.TARJOAJAOID += ',' + child.oid;
                            includeChildren(child);
                        });
                    }
                    includeChildren(organization);
                };

                $scope.initOrganisationFilter = function() {
                    var organisations = AuthService.getOrganisations();

                    if (angular.isArray(organisations) && organisations.length > 0) {
                        var organisation = organisations[0];
                        OrganisaatioService.byOid(organisation).then(function(resultOrganisation) {
                            $scope.filterByOrganisation(resultOrganisation);
                            angular.element('#organisationFilter').val(resultOrganisation.nimi);
                        });
                    }
                };

                $scope.init = function() {
                    $log.info("init...");

                    var model = {
                        collapse: {
                            model: true
                        },
                        search: {
                            tila: ""
                        },
                        hakus: [],
                        place: "holder"
                    };

                    $scope.initOrganisationFilter();

                    $log.info("init... done.");
                    $scope.model = model;
                };

                $scope.init();
            }]);
