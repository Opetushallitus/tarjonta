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

var app = angular.module('app.haku.review.ctrl', []);

app.controller('HakuReviewController', ['$scope', '$route', '$log', '$routeParams', 'ParameterService','$location','HakuV1Service',
    function HakuReviewController($scope, $route, $log, $routeParams, ParameterService,$location, HakuV1Service) {

        $log = $log.getInstance("HakuReviewController");

        $log.info("  init, args =", $scope, $route, $routeParams);

        var hakuOid = $route.current.params.id;

        // hakux : $route.current.locals.hakux, // preloaded, see "hakuApp.js" route resolve

        $scope.model = null;

        $scope.goBack = function() {
          $location.path("/haku");
        };

        $scope.doEdit = function() {
          $location.path("/haku/" + hakuOid + "/edit");
        };
        

        $scope.init = function() {
            $log.info("HakuReviewController.init()...");

            $scope.model = {
                formControls: {},
                collapse: {
                    haunTiedot: false,
                    haunAikataulut: true,
                    haunMuistutusviestit: true,
                    haunSisaisetHaut: true,
                    haunHakukohteet: true,
                    model: true
                },
                // Preloaded Haku result
                hakux: $route.current.locals.hakux,
                nimi: HakuV1Service.resolveNimi($route.current.locals.hakux.result),
                koodis: {
                    koodiX: "..."
                },
                haku: {todo: "TODO LOAD ME 1"},
                place: "holder"
            };

            $log.info("HakuReviewController.init()... done.");
        };

        $scope.init();

        $scope.parametrit={};
        ParameterService.haeHaunParametrit(hakuOid, $scope.parametrit);

    }]);
