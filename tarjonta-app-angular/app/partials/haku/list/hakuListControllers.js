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
        ['$route', '$scope', '$location', '$log', '$routeParams', '$window', '$modal', 'LocalisationService', 'HakuV1',
            function HakuListController($route, $scope, $location, $log, $routeParams, $window, $modal, LocalisationService, Haku) {
                $log.info("HakuListController()");

                $scope.model = null;

                function isEmpty(value) {
                    return (typeof value === "undefined" || value == null || value.length === 0);
                }

                $scope.getHakuName = function(haku) {
                    var userLocale = LocalisationService.getLocale();
                    var userKieliUri = "kieli_" + userLocale;

                    var result = haku.nimi[userKieliUri];

                    if (isEmpty(result)) {
                        result = haku.nimi["kieli_fi"];
                        if (!isEmpty(result)) {
                            result = result + " (FI)";
                        }
                    }
                    if (isEmpty(result)) {
                        result = haku.nimi["kieli_sv"];
                        if (!isEmpty(result)) {
                            result = result + " (SV)";
                        }
                    }
                    if (isEmpty(result)) {
                        result = haku.nimi["kieli_en"];
                        if (!isEmpty(result)) {
                            result = result + " (EN)";
                        }
                    }
                    if (isEmpty(result)) {
                        result = "[EI NIMIEÄ]";
                    }

                    return result;
                };

                $scope.init = function() {
                    $log.info("init...");

                    var model = {
                        collapse: {
                            model: true
                        },
                        hakus : [],
                        place: "holder"
                    };

                    // Load all hakus
                    Haku.findAll(function(result) {
                        $log.info("Haku.get() result", result);
                        model.hakus = result.result;
                    }, function(error) {
                        $log.info("Haku.get() error", error);
                        model.hakus = [];
                    });

                    $log.info("init... done.");
                    $scope.model = model;
                };

                $scope.init();
            }]);
