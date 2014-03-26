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
        ['$scope', '$location', '$log', 'LocalisationService', 'HakuV1', 'dialogService', 'HakuV1Service', 'Koodisto',
            function HakuListController($scope, $location, $log, LocalisationService, Haku, dialogService, HakuV1Service, Koodisto) {
          
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

                  console.log(kaudet);
                  
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
                  console.log("doDelete()", haku);
                  dialogService.showNotImplementedDialog();
                };

                $scope.doDeleteSelected = function() {
                  console.log("doDeleteSelected()");
                  var selected=[];
                  angular.forEach($scope.model.hakus, function(haku){
                    if(haku.selected) selected.push(haku);
                  });
                  
                  console.log("selected:", selected);
                  dialogService.showNotImplementedDialog();
                };

                
                
                $scope.doSearch = function() {
                    $log.info("doSearch()");
                    var params = angular.copy($scope.searchParams);
                    if(params['KOULUTUKSEN_ALKAMISVUOSIKAUSI']) {
                      var kVuosikausi = params['KOULUTUKSEN_ALKAMISVUOSIKAUSI'];
                      delete params['KOULUTUKSEN_ALKAMISVUOSIKAUSI']
                      params['KOULUTUKSEN_ALKAMISVUOSI']=kVuosikausi.vuosi;
                      params['KOULUTUKSEN_ALKAMISKAUSI']=kVuosikausi.kausi;
                    }
                    if(params['HAKUKAUSI']) {
                      var hKausi = params['HAKUKAUSI'];
                      params['HAKUKAUSI']=hKausi.kausi;
                    } 
                    if(params['KOULUTUKSEN_ALKAMISKAUSI']) {
                      var hKausi = params['KOULUTUKSEN_ALKAMISKAUSI'];
                      params['KOULUTUKSEN_ALKAMISKAUSI']=hKausi.kausi;
                    } 
                    
                    HakuV1Service.search(params).then(function(haut){

                      $scope.review=function(haku){
                        $location.path("/haku/" + haku.oid);
                      };
                      
                      //"kirjapinon" linkit
                      var actions = function(haku){
//                        console.log("$scope.doDelete", $scope.doDelete);
                          var actions=[];
                          //#/haku/{{ haku.oid}}/edit
                          actions.push({name:LocalisationService.t("haku.menu.muokkaa"), action:function(){
                            $location.path("/haku/" + haku.oid + "/edit");
                          }});
                          
                          actions.push({name:LocalisationService.t("haku.menu.tarkastele"), action:function(){
                            review(haku);
                          }});
                          
                          actions.push({name:LocalisationService.t("haku.menu.poista"), action:function(){$scope.doDelete(haku)}});
                          
                          return actions;
                      };
                      
                      for(var i=0;i<haut.length;i++) {
                        var haku = haut[i];
                        haku.actions=actions(haku);
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
