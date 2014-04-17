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

app.controller('HakukohdeRoutingController', ['$scope', '$log', '$routeParams', '$route','Hakukohde' , 'SharedStateService',
    function HakukohdeRoutingController($scope, $log, $routeParams, $route,Hakukohde,SharedStateService) {
        $log.info("HakukohdeRoutingController()", $routeParams);
        $log.info("$route: ", $route);
        $log.info("$route action: ", $route.current.$$route.action);
        $log.info("SCOPE: ", $scope);
        $log.info("CAN EDIT : ", $route.current.locals.canEdit);
        $log.info("CAN CREATE : ", $route.current.locals.canCreate);


        $log.info("IS COPY : " , $route.current.locals.isCopy);
        if ($route.current.locals.isCopy !== undefined) {

            $scope.isCopy = $route.current.locals.isCopy;
        } else {
            $scope.isCopy = false;
        }

        $scope.formControls = {reloadDisplayControls: function() {
        }}; // controls-layouttia varten


        $scope.canCreate = $route.current.locals.canCreate;
        $scope.canEdit =  $route.current.locals.canEdit;

        $log.info('HAKUKOHDE : ', $route.current.locals.hakukohdex.result);
        if ($route.current.locals.hakukohdex.result === undefined) {

            $scope.model = {
                collapse: {
                    model : true
                },
                hakukohdeTabsDisabled : true,
                hakukohde : {
                    valintaperusteKuvaukset : {},
                    soraKuvaukset : {},
                    kaytetaanJarjestelmanValintaPalvelua: true

                }
            }


            $scope.model.hakukohde = $route.current.locals.hakukohdex;




        } else {
            var hakukohdeResource = new Hakukohde( $route.current.locals.hakukohdex.result);

            if (hakukohdeResource.valintaperusteKuvaukset === undefined) {
                hakukohdeResource.valintaperusteKuvaukset = {};
            }

            if (hakukohdeResource.soraKuvaukset === undefined) {
                hakukohdeResource.soraKuvaukset = {};
            }

            $scope.model = {
                collapse: {
                    model : true
                },
                hakukohdeTabsDisabled : false,
                hakukohde : hakukohdeResource
            }

        }

        if ($route.current.$$route.action === "hakukohde.review") {
            console.log('Init languages');

            //Get all kieles from hakukohdes names and additional informaty
            var allKieles = new buckets.Set();

            for (var kieliUri in $scope.model.hakukohde.hakukohteenNimet) {

                allKieles.add(kieliUri);
            }

            for (var kieliUri in $scope.model.hakukohde.lisatiedot) {
                allKieles.add(kieliUri);
            }
            $scope.model.allkieles = allKieles.toArray();
            console.log('ALL KIELES : ' , allKieles.toArray());
        }



        $scope.hakukohdex = $route.current.locals.hakukohdex;
        $log.info("  --> hakukohdex == ", $scope.hakukohdex);

        $scope.getHakukohdePartialUri = function() {

            //var korkeakoulutusHakukohdePartialUri = "partials/hakukohde/edit/korkeakoulu/editKorkeakoulu.html";
            var korkeakoulutusHakukohdePartialUri = "partials/hakukohde/edit/korkeakoulu/editKorkeakoulu.html";
            var aikuLukioHakukohdePartialUri = "partials/hakukohde/edit/aiku/lukio/editAiku.html";
            var korkeakouluTyyppi = "KORKEAKOULUTUS";
            var lukioTyyppi = "LUKIOKOULUTUS";
            //If hakukohdex is defined then we are updating it
            //otherwise try to get selected koulutustyyppi from shared state
            if($route.current.locals.hakukohdex.result) {

                    if ($route.current.locals.hakukohdex.result.koulutusAsteTyyppi === korkeakouluTyyppi) {
                        return korkeakoulutusHakukohdePartialUri;
                    }   //TODO: if not "KORKEAKOULUTUS" then check for "koulutuslaji" to determine if koulutus if "AIKU" or not

            } else {
                var koulutusTyyppi = SharedStateService.getFromState('SelectedKoulutusTyyppi');
                if (koulutusTyyppi.trim() === korkeakouluTyyppi) {
                    return korkeakoulutusHakukohdePartialUri;
                } else if (koulutusTyyppi.trim() === lukioTyyppi) {
                    return aikuLukioHakukohdePartialUri;
                } else {
                    $log.info('KOULUTUSTYYPPI WAS: ' , koulutusTyyppi);
                }

                //TODO: if not "KORKEAKOULUTUS" then check for "koulutuslaji" to determine if koulutus if "AIKU" or not
            }

        };
    }
]);
