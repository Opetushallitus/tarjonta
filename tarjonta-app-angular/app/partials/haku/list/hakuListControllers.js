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
        ['$q', '$scope', '$location', '$log', '$window', '$modal', 'LocalisationService', 'HakuV1', 'dialogService', 'HakuV1Service', 'Koodisto', 'PermissionService', 'loadingService',
            function HakuListController($q, $scope, $location, $log, $window, $modal, LocalisationService, Haku, dialogService, HakuV1Service, Koodisto, PermissionService, loadingService) {

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
                    		$scope[scopeId][k.koodiUri+"#"+k.koodiVersio] = k.koodiNimi;
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
                }
                
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
                		return LocalisationService.t("tarjonta.tila."+row.tila);
                	default:
                		return row.nimi;
                	}
                }
                $scope.hakuGetIdentifier = function(row) {
                	return row.oid;
                }
                $scope.hakuGetLink = function(row) {
                	return "#/haku/"+row.oid;
                }
                $scope.hakuGetOptions = function(row) {
                	return undefined;
                }
                
                $scope.doDelete = function(haku, doAfter) {
                    $log.info("doDelete()", haku);

                    // Defined: "hakuControllers.js"
                    $scope.doDeleteHaku(haku).then(function(result) {
                    	doAfter();
                    	//$scope.doSearch();
                    });
                    
                };

//                $scope.doDelete = function(haku) {
//                    $log.debug("doDelete()", haku);
//
//                    HakuV1Service.delete(haku.oid).then(function(result) {
//                        $log.info("delete result", result);
//                        if (result.status == "OK") {
//                            $log.info("SHOW DELETE DONE DIALOG");
//                            dialogService.showSimpleDialog(
//                                    LocalisationService.t("haku.delete.ok"),
//                                    LocalisationService.t("haku.delete.ok.description"),
//                                    LocalisationService.t("ok"),
//                                    undefined);
//                            $scope.doSearch();
//                        } else {
//                            var errorMessage = "<ul>"
//                            angular.forEach(result.errors, function(error) {
//                                var msg = LocalisationService.t(error.errorMessageKey, error.errorMessageParameters);
//                                errorMessage = errorMessage + "<li>" + msg + "</li>";
//                            });
//                            errorMessage = errorMessage + "</ul>";
//                            var desciptionParams = [errorMessage];
//
//                            dialogService.showSimpleDialog(
//                                    LocalisationService.t("haku.delete.failed"),
//                                    LocalisationService.t("haku.delete.failed.description", desciptionParams),
//                                    LocalisationService.t("ok"),
//                                    undefined);
//                        }
//                    });
//                    
//                    // dialogService.showNotImplementedDialog();
//                };

                $scope.review = function(haku) {
                    $location.path("/haku/" + haku.oid);
                };

                function changeState(targetState) {
                  return function(haku, doAfter) {
                      $log.debug("changing state with service call...");
                      Haku.changeState({oid: haku.oid, state: targetState}).$promise.then(function(result) {
                          $log.debug("call done:", result);
                          if ("OK" === result.status) {
                              haku.tila = targetState;
                              doAfter();
                          } else {
                              $log.debug("state change did not work?", result);
                          }
                      }, function(reason) {
                              alert('service call failed: ' + reason);
                      });
                  };
              }


                $scope.doPublish = changeState("JULKAISTU");
                $scope.doCancel = changeState("PERUTTU");

                $scope.doDeleteSelected = function() {
                    $log.debug("doDeleteSelected()");
                    $log.debug("selected:", $scope.selection);
                    dialogService.showNotImplementedDialog();
                };
                
                $scope.canDeleteSelected = function() {
                	// TODO käyttöoikeustarkistus -> jos ei saa poistaa mitään, palauta false
                	return $scope.selection.length>0;
                }

                /*function setKausi(params, parameterName) {
                    if (params[parameterName]) {
                        var hKausi = params[parameterName];
                        params[parameterName] = hKausi.kausi;
                    }
                }*/

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
                                $scope.doDelete(haku, actions.delete);
                            }});
                        }

                        //publish
                        if (true === results[0] && true === results[2].result && haku.tila != 'JULKAISTU') {
                        	ret.push({title: LocalisationService.t("haku.menu.julkaise"), action: function() {
                                $scope.doPublish(haku, actions.update);
                            }});
                        }
                        
                        //cancel
                        if (true === results[0] && true === results[3].result && haku.tila != 'PERUTTU') {
                        	ret.push({title: LocalisationService.t("haku.menu.peruuta"), action: function() {
                                $scope.doCancel(haku, actions.update);
                            }});
                        }

                    });
                    
                    return ret;
                }

                $scope.doSearch = function() {
                    $log.info("doSearch()");
                    var params = angular.copy($scope.searchParams);

                    if (params.KAUSIVUOSI && params.KAUSI) {
                    	params[params.KAUSIVUOSI+"KAUSI"] = params.KAUSI.kausi;
                    }
                    if (params.KAUSIVUOSI && params.VUOSI) {
                    	params[params.KAUSIVUOSI+"VUOSI"] = params.VUOSI;
                    }
                    
                    // pois turhat parametrit
                    params.KAUSIVUOSI = undefined;
                    params.KAUSI = undefined;
                    params.VUOSI = undefined;
                    
                    HakuV1Service.search(params).then(function(haut) {
                    	// TODO järjestys backendiin?
                        haut.sort(function(a,b){
                        	var ret = a.tila.localeCompare(b.tila);
                        	if (ret==0) {
                        		ret = a.nimi.localeCompare(b.nimi);
                        	}
                        	return ret;
                        });
                        $scope.model.hakus = haut;
                    }
                    );
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


                    $log.info("init... done.");
                    $scope.model = model;
                };

                $scope.init();
            }]);
