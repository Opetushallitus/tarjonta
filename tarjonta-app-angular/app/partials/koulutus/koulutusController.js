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

app.controller('KoulutusRoutingController', ['$scope', '$log', '$routeParams', '$route', '$location', 'KoulutusConverterFactory', 'TarjontaService', 'PermissionService', 'OrganisaatioService', 'Koodisto', 'LocalisationService',
    function KoulutusRoutingController($scope, $log, $routeParams, $route, $location, converter, TarjontaService, PermissionService, organisaatioService, Koodisto, LocalisationService) {
        $log = $log.getInstance("KoulutusRoutingController");

        var map = {};
        map['koulutustyyppi_1'] = 'amm';
        map['koulutustyyppi_4'] = 'amm';
        map['koulutustyyppi_13'] = 'amm';
        map['koulutustyyppi_2'] = 'lukio';
        map['koulutustyyppi_14'] = 'lukio';
        map['koulutustyyppi_3'] = 'korkeakoulu';

        /*
         * Page routing data
         */
        $scope.resultPageUri;

        $scope.resolvePath = function(actionType) {
            if (!angular.isUndefined($route.current.locals.koulutusModel.result)) {
                /*
                 koulutustyyppi_5	Valmentava ja kuntouttava opetus ja ohjaus
                 koulutustyyppi_12	Erikoisammattitutkinto
                 koulutustyyppi_10	Vapaan sivistystyön koulutus
                 koulutustyyppi_11	Ammattitutkinto
                 koulutustyyppi_2	Lukiokoulutus
                 koulutustyyppi_13	ammatillinen perustutkinto näyttötutkintona
                 koulutustyyppi_14	Lukiokoulutus, aikuisten oppimäärä
                 koulutustyyppi_7	Ammatilliseen peruskoulutukseen ohjaava ja valmistava koulutus
                 koulutustyyppi_4	Ammatillinen peruskoulutus erityisopetuksena
                 koulutustyyppi_1	Ammatillinen perustutkinto
                 koulutustyyppi_8	Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus
                 koulutustyyppi_3	Korkeakoulutus
                 koulutustyyppi_9	Maahanmuuttajien ja vieraskielisten lukiokoulutukseen valmistava koulutus
                 koulutustyyppi_6	Perusopetuksen lisäopetus
                 */

                var type = $route.current.locals.koulutusModel.result.koulutustyyppi;
                var patt = new RegExp("(koulutustyyppi_1|koulutustyyppi_2|koulutustyyppi_3|koulutustyyppi_13)");
                if (patt.test(type)) {
                    $scope.resultPageUri = "partials/koulutus/" + actionType + "/" + map[type] + "/" + type + ".html";
                } else {
                    $scope.resultPageUri = "partials/koulutus/" + actionType + "/UNKNOWN.html";
                }
            } else {
                $location.path("/error");
            }
        };

        $scope.getKoulutusPartialName = function(actionType) {
            $scope.resolvePath(actionType, $scope.koulutusModel);
        };

        return $scope;
    }
]);
