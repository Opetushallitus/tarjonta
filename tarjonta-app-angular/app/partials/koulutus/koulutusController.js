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
app.controller('KoulutusRoutingController', [
    '$scope',
    '$log',
    '$routeParams',
    '$route',
    '$location',
    'KoulutusConverterFactory',
    'TarjontaService',
    'PermissionService',
    'OrganisaatioService',
    'Koodisto',
    'LocalisationService', function KoulutusRoutingController($scope, $log, $routeParams, $route, $location,
       converter, TarjontaService, PermissionService, organisaatioService, Koodisto, LocalisationService) {
        /*
         * Page routing data
         */
        $scope.resolvePath = function(actionType) {
            if (!angular.isUndefined($route.current.locals.koulutusModel.result)) {
                var type = $route.current.locals.koulutusModel.result.toteutustyyppi;
                if (angular.isDefined(converter.STRUCTURE[type])) {
                    if (converter.STRUCTURE[type].templates && converter.STRUCTURE[type].templates[actionType]) {
                        $scope.resultPageUri = 'partials/koulutus/' + actionType + '/' +
                            converter.STRUCTURE[type].templates[actionType] + '.html';
                    }
                    else {
                        $scope.resultPageUri = 'partials/koulutus/' + actionType + '/' + type + '.html';
                    }
                }
                else {
                    $scope.resultPageUri = 'partials/koulutus/' + actionType + '/UNKNOWN.html';
                }
            }
            else {
                $location.path('/error');
            }
        };
        $scope.getKoulutusPartialName = function(actionType) {
            $scope.resolvePath(actionType, $scope.koulutusModel);
        };
        return $scope;
    }
]);