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

        /*
         * Page routing data
         */
        $scope.resultPageUri;

        $scope.resolvePath = function(actionType) {
            if (!angular.isUndefined($route.current.locals.koulutusModel.result)) {
                /*
                 VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS("koulutustyyppi_5"), Valmentava ja kuntouttava opetus ja ohjaus
                 ERIKOISAMMATTITUTKINTO("koulutustyyppi_12"), Erikoisammattitutkinto
                 VAPAAN_SIVISTYSTYON_KOULUTUS("koulutustyyppi_10"), Vapaan sivistystyön koulutus
                 AMMATTITUTKINTO("koulutustyyppi_11"), Ammattitutkinto
                 LUKIOKOULUTUS("koulutustyyppi_2"), Lukiokoulutus
                 AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA("koulutustyyppi_13"), ammatillinen perustutkinto näyttötutkintona
                 LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA("koulutustyyppi_14"), Lukiokoulutus, aikuisten oppimäärä
                 AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS("koulutustyyppi_7"), Ammatilliseen peruskoulutukseen ohjaava ja valmistava koulutus
                 AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA("koulutustyyppi_4"), Ammatillinen peruskoulutus erityisopetuksena
                 AMMATILLINEN_PERUSTUTKINTO("koulutustyyppi_1"), Ammatillinen perustutkinto
                 MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS("koulutustyyppi_8"), Maahanmuuttajien ammatilliseen peruskoulutukseen valmistava koulutus
                 KORKEAKOULUTUS("koulutustyyppi_3"), Korkeakoulutus
                 MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS("koulutustyyppi_9"), Maahanmuuttajien ja vieraskielisten lukiokoulutukseen valmistava koulutus
                 PERUSOPETUKSEN_LISAOPETUS("koulutustyyppi_6"), Perusopetuksen lisäopetus
                 */

                var type = $route.current.locals.koulutusModel.result.toteutustyyppi;
                var patt = new RegExp("(AMMATILLINEN_PERUSTUTKINTO|AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA|KORKEAKOULUTUS|LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA)");
                if (patt.test(type)) {
                    $scope.resultPageUri = "partials/koulutus/" + actionType  + "/" + type + ".html";
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
