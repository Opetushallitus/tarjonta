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

app.controller('BaseHakuReviewController', ['$scope', '$location', '$route', '$log', '$routeParams', 'LocalisationService', '$modal',
    function BaseReviewController($scope, $location, $route, $log, $routeParams, LocalisationService, $modal) {
        $log.info("BaseHakuReviewController()", $scope, $route, $routeParams);

        $scope.model = {
            routeParams: $routeParams,
            collapse: {
                model: true
            },
            languages: [],
            haku : {todo : "TODO LOAD ME 1"},
            hakux : $route.current.locals.hakux, // preloaded, see "hakuApp.js" route resolve
            place : "holder"
        };

        $scope.init = function() {
            $log.info("init()...");
            $log.info("init()... done.");
        };

        $scope.init();

    }]);

