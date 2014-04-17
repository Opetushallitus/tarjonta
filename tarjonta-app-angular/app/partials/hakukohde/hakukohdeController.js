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

/**
 *
 * This controller acts as routing and parent controller of all hakukohdes,
 * it contains all common controller variables and functions
 * @type {module|*}
 */

var app = angular.module('app.hakukohde.ctrl', []);

app.controller('HakukohdeRoutingController', ['$scope', '$log', '$routeParams', '$route','$q', 'Hakukohde' , '$location' , 'SharedStateService', 'TarjontaService',
    function HakukohdeRoutingController($scope, $log, $routeParams, $route,$q, Hakukohde, $location, SharedStateService,TarjontaService) {




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

        /*
         *
         * Common hakukohde controller variables
         *
         */


        $scope.modifiedObj = {

            modifiedBy : '',
            modified : 0,
            tila : ''

        };

        $scope.model.showSuccess = false;
        $scope.model.showError = false;
        $scope.model.validationmsgs = [];
        $scope.model.hakukohdeTabsDisabled = false;
        $scope.model.koulutusnimet = [];
        $scope.model.continueToReviewEnabled = false;
        $scope.model.organisaatioNimet = [];
        $scope.model.hakukohdeOppilaitosTyyppis = [];
        $scope.model.nimiValidationFailed = false;
        $scope.model.hakukelpoisuusValidationErrMsg = false;
        $scope.model.tallennaValmiinaEnabled = true;
        $scope.model.tallennaLuonnoksenaEnabled = true;
        $scope.model.liitteidenToimitusOsoite = {};
        var deferredOsoite = $q.defer();
        $scope.model.liitteenToimitusOsoitePromise = deferredOsoite.promise;
        $scope.model.liitteidenToimitusPvm = new Date();
        $scope.userLangs = window.CONFIG.app.userLanguages; // liitteiden ja valintakokeiden kielien esijärjestystä varten
        $scope.model.defaultLang = 'kieli_fi';

        //All kieles is received from koodistomultiselect
        $scope.model.allkieles = [];
        $scope.model.selectedKieliUris = [];
        var koulutusKausiUri;
        $scope.model.koulutusVuosi;
        $scope.model.integerval=/^\d*$/;

        $scope.koulutusKausiUri;

        $scope.julkaistuVal = "JULKAISTU";

        $scope.luonnosVal = "LUONNOS";

        $scope.valmisVal = "VALMIS";

        $scope.peruttuVal = "PERUTTU";

        $scope.showSuccess = function() {
            $scope.model.showSuccess = true;
            $scope.model.showError = false;
            $scope.model.validationmsgs = [];
            $scope.model.hakukohdeTabsDisabled = false;
        };

        $scope.showError = function(errorArray) {

            $scope.model.validationmsgs.splice(0,$scope.model.validationmsgs.length);

            angular.forEach(errorArray,function(error) {


                $scope.model.validationmsgs.push(error.errorMessageKey);


            });
            $scope.model.showError = true;
            $scope.model.showSuccess = false;
        };


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


        $scope.validateNameLengths = function(hakukohteenNimet) {

            var retval = true;

            angular.forEach(hakukohteenNimet, function(hakukohdeNimi){

                if (hakukohdeNimi.length > 225) {
                    retval = false;
                }

            });

            return retval;

        };

        $scope.checkJatkaBtn =   function(hakukohde) {

            if (hakukohde === undefined || hakukohde.oid === undefined) {
                $log.debug('HAKUKOHDE OR HAKUKOHDE OID UNDEFINED');

                return false;
            } else {
                return true;
            }

        };

        $scope.updateTilaModel = function(hakukohde) {

            if (hakukohde) {
                $scope.modifiedObj.modifiedBy = hakukohde.modifiedBy;
                $scope.modifiedObj.modified = hakukohde.modified;
                $scope.modifiedObj.tila = hakukohde.tila;
            }
            console.log('FORM CONTROLS : ', $scope.formControls);
            if ($scope.formControls && $scope.formControls.reloadDisplayControls) {
                $log.debug('RELOADING FORM CONTROLS : ', $scope.formControls);
                $scope.formControls.reloadDisplayControls();
            }


        };

        $scope.emptyErrorMessages = function() {

            $scope.model.validationmsgs.splice(0,$scope.model.validationmsgs.length);

            $scope.model.showError = false;

        };

        $scope.checkCanCreateOrEditHakukohde = function(hakukohde) {

            if (hakukohde.oid !== undefined) {

                if ($scope.canEdit !== undefined) {
                    return $scope.canEdit;
                } else {
                    return true;
                }


            } else {

                if ($scope.canCreate !== undefined) {

                    return $scope.canCreate;

                } else {

                    return true;

                }



            }



        };


        $scope.checkIfSavingCopy = function(hakukohde) {

            if ($scope.model.isCopy) {

                if (hakukohde.oid !== undefined) {

                    $scope.model.isCopy = false;

                    $location.path('/hakukohde/'+hakukohde.oid +'/edit');
                }




            }

        };

        $scope.showCommonUnknownErrorMsg = function() {

            var errors = [];

            var error = {};

            error.errorMessageKey =  commonExceptionMsgKey;

            errors.push(error);

            $scope.showError(errors);

        };

        $scope.checkIsCopy = function(tilaParam) {

            //If scope or route has isCopy parameter defined as true remove oid,
            //so that new hakukohde will be created
            $log.debug('IS THIS COPY ROUTE : ',$route.current.locals.isCopy);

            if ($route.current.locals.isCopy) {
                $log.debug('HAKUKOHDE IS COPY, SETTING OID UNDEFINED');
                $scope.model.hakukohde.oid = undefined;
                $scope.model.hakukohde.tila = tilaParam;

            }

            $log.debug('IS COPY : ' , $scope.isCopy);
            if ($scope.isCopy !== undefined && $scope.isCopy) {
                $scope.model.hakukohde.oid = undefined;
                $scope.model.hakukohde.tila = tilaParam;

            }


            $scope.model.isCopy = true;

        };

        $scope.createFormattedDateString = function(date) {

            return moment(date).format('DD.MM.YYYY HH:mm');

        };






    }
]);
