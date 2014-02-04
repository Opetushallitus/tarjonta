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

app.controller('HakuReviewController', ['$scope', '$location', '$route', '$log', '$routeParams', 'LocalisationService', '$modal',
    function HakuReviewController($scope, $location, $route, $log, $routeParams, LocalisationService, $modal) {
        $log.info("HakuReviewController()", $scope, $route, $routeParams);

        // hakux : $route.current.locals.hakux, // preloaded, see "hakuApp.js" route resolve

        $scope.model = null;

        $scope.goBack = function(event) {
            $log.info("goBack()", event);
        };

        $scope.doEdit = function(event, part) {
            $log.info("goEdit()", event, part);
        };

        $scope.getHakuNimi = function() {
            return "TODO: get haku nimi: " + new Date();
        };

        $scope.init = function() {
            $log.info("HakuReviewController.init()...");

            $scope.model = {
                formControls : {},
                collapse: {
                    haunTiedot : false,
                    haunAikataulut : true,
                    haunMuistutusviestit : true,
                    haunSisaisetHaut : true,
                    haunHakukohteet : true,
                    model: true
                },

                // Preloaded Haku result
                hakux: $route.current.locals.hakux,

                haku: {todo: "TODO LOAD ME 1"},
                place: "holder"
            };

            $log.info("HakuReviewController.init()... done.");
        };

        $scope.init();

    }]);

