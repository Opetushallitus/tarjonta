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


var app = angular.module('app.hakukohde.ctrl', []);

app.controller('HakukohdeRoutingController', ['$scope', '$log', '$routeParams', '$route','Hakukohde' ,
    function HakukohdeRoutingController($scope, $log, $routeParams, $route,Hakukohde) {
        $log.info("HakukohdeRoutingController()", $routeParams);
        $log.info("$route: ", $route);
        $log.info("SCOPE: ", $scope);

        $log.info('HAKUKOHDE : ', $route.current.locals.hakukohdex.result);
        if ($route.current.locals.hakukohdex.result === undefined) {

            $scope.model = {
                collapse: {
                    model : true
                },
                hakukohde : {
                    valintaperusteKuvaukset : {},
                    soraKuvaukset : {}

                }
            }


            $scope.model.hakukohde = $route.current.locals.hakukohdex;

        } else {
            var hakukohdeResource = new Hakukohde( $route.current.locals.hakukohdex.result);

            $scope.model = {
                collapse: {
                    model : true
                },
                hakukohde : hakukohdeResource
            }

        }





        $scope.hakukohdex = $route.current.locals.hakukohdex;
        $log.info("  --> hakukohdex == ", $scope.hakukohdex);
    }
]);
