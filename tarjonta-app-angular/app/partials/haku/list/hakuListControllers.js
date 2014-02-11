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
        ['$route', '$scope', '$location', '$log', '$routeParams', '$window', '$modal', 'LocalisationService', 'HakuV1', 'dialogService', 'HakuV1Service',
            function HakuListController($route, $scope, $location, $log, $routeParams, $window, $modal, LocalisationService, Haku, dialogService, HakuV1Service) {
                $log.info("HakuListController()");

                $scope.states=[];
                $scope.vuosikausi=[]
                
                for (var s in CONFIG.env["tarjonta.tila"]) {
                  $scope.states[s] = LocalisationService.t("tarjonta.tila." + s);
                }
                
                //vuosi-kaudet
                for (var y = new Date().getFullYear()-2; y < new Date().getFullYear() + 10; y++) {
                  $scope.vuosikausi.push({vuosi:y,kausi:'kausi_k',label:y + 'kausi_k'});
                  $scope.vuosikausi.push({vuosi:y,kausi:'kausi_s',label : y + 'kausi_s'});
                }
                
                $scope.clearSearch = function() {
                  $scope.searchParams=  {
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
                    dialogService.showNotImplementedDialog();
                };

                $scope.doDelete = function() {
                    $log.info("doDelete()");
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
                    if(params['HAKUVUOSIKAUSI']) {
                      var hVuosikausi = params['HAKUVUOSIKAUSI'];
                      delete params['HAKUVUOSIKAUSI'];
                      params['KOULUTUKSEN_ALKAMISVUOSI']=hVuosikausi.vuosi;
                      params['KOULUTUKSEN_ALKAMISKAUSI']=hVuosikausi.kausi;
                    } 
                    
                    HakuV1Service.search(params).then(function(haut){$scope.model.hakus=haut;});
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
