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
        ['$route', '$scope', '$location', '$log', '$routeParams', '$window', '$modal', 'LocalisationService',
            function HakuListController($route, $scope, $location, $log, $routeParams, $window, $modal, LocalisationService) {
                $log.info("HakuListController()");

                // TODO preloaded / resolved haku is where?

                $scope.model = null;

                $scope.init = function() {
                    $log.info("init...");
                    var model = {
                        collapse: {
                            model: true
                        },
                        place: "holder"
                    };

                    $log.info("init... done.");
                    $scope.model = model;
                };

                $scope.init();
            }]);
