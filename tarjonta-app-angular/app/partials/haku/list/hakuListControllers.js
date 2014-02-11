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

                $scope.states= [];
                
                for (var s in CONFIG.env["tarjonta.tila"]) {
                  $scope.states[s] = LocalisationService.t("tarjonta.tila." + s);
                }
                
                console.log("states:", $scope.states);
                
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
                    HakuV1Service.search({"args":"bargs"});
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

                    // Load all hakus
                    Haku.findAll(function(result) {
                      var userLocale = LocalisationService.getLocale();
                      var userKieliUri = "kieli_" + userLocale;

                        $log.info("Haku.get() result", result);
                        model.hakus = result.result;
                        for(var i=0;i<model.hakus.length;i++){
                          //lokalisoi haun nimi
                          var haku = model.hakus[i];
                          haku.nimi = result=haku.nimi[userKieliUri]||haku.nimi["kieli_fi"]||haku.nimi["kieli_sv"]||haku.nimi["kieli_en"]||"[Ei nimeä]";
                        }
                    }, function(error) {
                        $log.info("Haku.get() error", error);
                        model.hakus = [];
                    });

                    $log.info("init... done.");
                    $scope.model = model;
                };

                $scope.init();
            }]);
