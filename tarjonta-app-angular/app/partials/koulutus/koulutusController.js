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


var app = angular.module('app.koulutus.ctrl', []);

app.controller('KoulutusRoutingController', ['$scope', '$log', '$routeParams', '$route',
    function KoulutusRoutingController($scope, $log, $routeParams, $route) {
        $scope.resultPageUri;

        $log.info("KoulutusRoutingController()", $routeParams);
        $log.info("$route: ", $route);
        $log.info("SCOPE: ", $scope);

        $scope.koulutusModel = $route.current.locals.koulutusModel;
        $log.info("  --> koulutusx == ", $scope.koulutusModel);

        $scope.resolvePath = function(actionType, koulutus) {
            if (!angular.isUndefined(koulutus.result)) {
                var type = koulutus.result.koulutusasteTyyppi;
                var patt = new RegExp("(AMMATILLINEN_PERUSKOULUTUS|LUKIOKOULUTUS|KORKEAKOULUTUS|PERUSOPETUKSEN_LISAOPETUS)");

                if (patt.test(type)) {
                    $scope.resultPageUri = "partials/koulutus/" + actionType + "/" + type + ".html";
                } else {
                    $scope.resultPageUri = "partials/koulutus/" + actionType + "/UNKNOWN.html";
                }

            } else {
                console.error("Something went wrong?");
            }
        };

        $scope.getKoulutusPartialName = function(actionType) {
            if (!angular.isUndefined($scope.koulutusModel.$promise)) {
                //load&review koulutus
                $scope.koulutusModel.$promise.then(function(koulutus) {
                    $scope.resolvePath(actionType, koulutus);
                })
            } else {
                //create new koulutus
                $scope.resolvePath(actionType, $scope.koulutusModel);
            }
        };
    }
]);
