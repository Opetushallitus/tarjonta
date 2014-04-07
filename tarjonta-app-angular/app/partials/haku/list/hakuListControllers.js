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


var app = angular.module('app.haku.list.ctrl', []);

app.controller('HakuListController',
        ['$q', '$scope', '$location', '$log', '$window', '$modal', 'LocalisationService', 'HakuV1', 'dialogService', 'HakuV1Service', 'Koodisto', 'PermissionService', 'loadingService', 
            function HakuListController($q, $scope, $location, $log, $window, $modal, LocalisationService, Haku, dialogService, HakuV1Service, Koodisto, PermissionService, loadingService) {

          $log = $log.getInstance("HakuListController");

          //sorting
          $scope.predicate='tila';
          $scope.reverse=false;
          $scope.kausi=[];
          $scope.vuosi=[];

          $log.info("HakuListController()");

                Koodisto.getAllKoodisWithKoodiUri('kausi').then(function(kaudet){
                  var k = kaudet[0].koodi_uri=="kausi_k"?0:1;

                  var kevat = kaudet[k];
                  var syksy = kaudet[(k+1)%1];

                  $log.debug(kaudet);

                  //vuodet
                  for (var y = new Date().getFullYear()-2; y < new Date().getFullYear() + 10; y++){
                    $scope.vuosi.push(y);
                  }

                  //kaudet
                  $scope.kausi.push({kausi:kevat.koodiUri + "#" + kevat.koodiVersio, label:kevat.koodiNimi});
                  $scope.kausi.push({kausi:syksy.koodiUri + "#" + syksy.koodiVersio, label:syksy.koodiNimi});

                });

                $scope.states=[];
                $scope.vuosikausi=[];

                for (var s in CONFIG.env["tarjonta.tila"]) {
                  $scope.states[s] = LocalisationService.t("tarjonta.tila." + s);
                }

                $scope.clearSearch = function() {
                  $scope.searchParams=  {
                    HAKUSANA: undefined,
                    TILA : undefined,
                    HAKUKAUSI : undefined,
                    HAKUVUOSI : undefined,
                    KOULUTUKSEN_ALKAMISKAUSI : undefined,
                    KOULUTUKSEN_ALKAMISVUOSI : undefined,
                    HAKUTAPA : undefined,
                    HAKUTYYPPI : undefined,
                    KOHDEJOUKKO : undefined
                  };
                };

                $scope.clearSearch();

                $scope.doCreateNew = function() {
                    $log.info("doCreateNew()");
                    $location.path("/haku/NEW");
                };

                $scope.doDelete = function(haku) {
                  $log.debug("doDelete()", haku);
                  dialogService.showNotImplementedDialog();
                };

                $scope.review=function(haku){
                  $location.path("/haku/" + haku.oid);
                };

                function changeState(targetState) {
                  return function(haku) {
                    Haku.changeState({oid:haku.oid, state:targetState}).$promise.then(function(result){
                      if("OK"===result.status) {
                        haku.tila=targetState;
                      } else {
                        $log.debug("state change did not work?", result);
                      }
                    });
                  };
                }
                
                $scope.doPublish = changeState("JULKAISTU");
                $scope.doCancel = changeState("PERUTTU");

                $scope.doDeleteSelected = function() {
                  $log.debug("doDeleteSelected()");
                  var selected=[];
                  angular.forEach($scope.model.hakus, function(haku){
                    if(haku.selected) selected.push(haku);
                  });

                  $log.debug("selected:", selected);
                  dialogService.showNotImplementedDialog();
                };

                function setKausi(params, parameterName){
                  if(params[parameterName]) {
                    var hKausi = params[parameterName];
                    params[parameterName]=hKausi.kausi;
                  }
                }

                /**
                 * populoi menu laiskasti (permissiot/tila vaikuttaa)
                 */
                $scope.initMenu=function(haku) {
                  haku.actions.splice(0);
                  //hae permissiot ja testaa tilasiirtymät
                  $q.all([PermissionService.haku.canEdit(haku.oid), PermissionService.haku.canDelete(haku.oid), Haku.checkStateChange({oid:haku.oid, state: 'JULKAISTU'}), Haku.checkStateChange({oid:haku.oid, state: 'PERUTTU'})]).then(function(results){
                    if(results[0]) {
                      //edit
                      haku.actions.push({name:LocalisationService.t("haku.menu.muokkaa"), action:
                        function(){
                          $location.path("/haku/" + haku.oid + "/edit");
                        }
                      });
                    }
                    
                    $log.debug("results", results);
                    
                    //review
                    haku.actions.push({name:LocalisationService.t("haku.menu.tarkastele"), action:function(){
                      $scope.review(haku);
                    }});

                    //delete
                    if(results[1]) {
                      haku.actions.push({name:LocalisationService.t("haku.menu.poista"), action:function(){$scope.doDelete(haku)}});
                    }
                    
                    //publish
                    if(results[0] && results[2].result && haku.tila!='JULKAISTU') {
                      haku.actions.push({name:LocalisationService.t("haku.menu.julkaise"), action:function(){$scope.doPublish(haku)}});
                    }
                    //cancel
                    if(results[0] && results[3].result && haku.tila!='PERUTTU') {
                      haku.actions.push({name:LocalisationService.t("haku.menu.peruuta"), action:function(){$scope.doCancel(haku)}});
                    }

                  });
                  
                }

                
                $scope.doSearch = function() {
                    $log.info("doSearch()");
                    var params = angular.copy($scope.searchParams);
		    
	            setKausi(params, 'HAKUKAUSI');
		    setKausi(params, 'KOULUTUKSEN_ALKAMISKAUSI');

                    HakuV1Service.search(params).then(function(haut){

                      for(var i=0;i<haut.length;i++) {
                        var haku = haut[i].actions=[];
                      }
                      $scope.model.hakus=haut;
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
                            tila : ""
                        },
                        hakus : [],
                        place: "holder"
                    };


                    $log.info("init... done.");
                    $scope.model = model;
                };

                $scope.init();
            }]);
