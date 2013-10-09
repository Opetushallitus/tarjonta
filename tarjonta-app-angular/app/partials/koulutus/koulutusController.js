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

app.controller('KoulutusRoutingController', ['$scope', '$location', '$log', 'TarjontaService', '$routeParams', '$route',
    function KKReviewController($scope, $location, $log, TarjontaService, $routeParams, $route) {
        $log.info("KoulutusRoutingController()", $routeParams);
        $log.info("$route: ", $route);
        $log.info("SCOPE: ", $scope);

        $scope.koulutusx = $route.current.locals.koulutusx;
        $log.info("  --> koulutusx == ", $scope.koulutusx);

        $scope.getKoulutusPartialName = function(actionType) {
            var result;
            var type = $scope.koulutusx.koulutusasteTyyppi;

            var patt = new RegExp("(AMMATILLINEN_PERUSKOULUTUS|LUKIOKOULUTUS|AMMATTIKORKEAKOULUTUS|YLIOPISTOKOULUTUS|PERUSOPETUKSEN_LISAOPETUS)");

            if (patt.test(type)) {
                result = "partials/koulutus/" + actionType + "/" + type + ".html";
            } else {
                result = "partials/koulutus/" + actionType + "/UNKNOWN.html";
            }

            $log.debug("getKoulutusPartialName() --> ", result);
            return result;
        };

    }
]);
