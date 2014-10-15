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


'use strict';

/* Controllers */


var app = angular.module('app.kk.edit.hakukohde.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea','MultiSelect','ngGrid','TarjontaOsoiteField','ExportToParent']);


app.controller('HakukohdeEditController', 
    function($scope,
             $q, 
             $log,
             LocalisationService, 
             OrganisaatioService,
             Koodisto,
             Hakukohde,
             AuthService, 
             HakuService,
             $route , 
             $modal ,
             Config,
             $location,
             $timeout,
             TarjontaService,
             Kuvaus,
             CommonUtilService, 
             PermissionService) {

    $log = $log.getInstance("HakukohdeEditController");

    // Get organisation groups, see "tarjontaApp.js" routing resolve for hakukohde/id/edit
    var organisationGroups = $route.current.locals.organisationGroups || [];

    var commonExceptionMsgKey = "tarjonta.common.unexpected.error.msg";

    //Initialize all variables and scope object in the beginning
    var postinumero = undefined;


    /*

        ----> Scope function to express whether hakukohde can be saved or not

     */

    $scope.model.canSaveHakukohde = function() {

        if ($scope.editHakukohdeForm !== undefined) {


            return $scope.editHakukohdeForm.$valid && $scope.checkCanCreateOrEditHakukohde($scope.model.hakukohde);
        } else {
            return false;
        }

    };


    $scope.model.canSaveAsLuonnos = function() {

        if ($scope.model.isDeEnabled && $scope.model.isPartiallyDeEnabled) {
            var canSave = !$scope.model.isDeEnabled;
            $log.info('CanSaveAsLuonnos, parameter says not ok');
            return canSave;
        } else {
            $log.info('CanSaveAsLuonnos, parameter says ok. Tila : ', $scope.model.hakukohde.tila);
            var canSaveAsLuonnosByTila = CommonUtilService.canSaveAsLuonnos($scope.model.hakukohde.tila);
            return canSaveAsLuonnosByTila;
        }



    };

    $scope.model.canSaveAsValmis = function () {

        return $scope.model.isDeEnabled && $scope.model.isPartiallyDeEnabled;


    };

    if ($scope.model.hakukohde.lisatiedot !== undefined) {
        angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto){

            $scope.model.selectedKieliUris.push(lisatieto.uri);
        });
    }

    var filterHakuWithAikaAndKohdejoukko = function(hakus) {

        var filteredHakus = [];
        $log.info('filterHakuWithAikaAndKohdejoukko, ALL HAKUS :', hakus);
        angular.forEach(hakus,function(haku){

            // rajaus kk-hakukohteisiin; ks. OVT-6452
            // TODO selvitä uri valitun koulutuksen perusteella

            var kohdeJoukkoUriNoVersion = $scope.splitUri(haku.kohdejoukkoUri);

            if (kohdeJoukkoUriNoVersion==window.CONFIG.app['haku.kohdejoukko.kk.uri']) {

                if (haku.koulutuksenAlkamiskausiUri && haku.koulutuksenAlkamisVuosi) {
                    //OVT-6800 --> Rajataan koulutuksen alkamiskaudella ja vuodella
                    if (haku.koulutuksenAlkamiskausiUri === $scope.koulutusKausiUri && haku.koulutuksenAlkamisVuosi === $scope.model.koulutusVuosi) {
                        filteredHakus.push(haku);
                    }
                } else {
                    filteredHakus.push(haku);
                }



            }
        });
        $log.info('filterHakuWithAikaAndKohdejoukko, FILTERED HAKUS : ', filteredHakus);
        return filteredHakus;

    };

        var filterHakus = function(hakus) {
            return  filterHakuWithAikaAndKohdejoukko($scope.filterHakusWithOrgs(hakus));

        };



    //Placeholder for multiselect remove when refactored
    $scope.model.temp = {};
    
    $scope.model.ryhmaChange = function() {
        $log.info("ryhmaChange()", $scope.model.hakukohde.ryhmatX);
    };

    var init = function() {
        $log.info("init()");
        
        $scope.model.userLang  =  AuthService.getLanguage();

        if ($scope.model.userLang === undefined) {
            $scope.model.userLang = "FI";
        }
        
        $log.debug('CHECKING PERMISSIONS : ', $scope.model.hakukohde);
        if ($scope.model.hakukohde.oid) {
            $scope.checkPermissions($scope.model.hakukohde.oid);
        }
        $scope.loadHakukelpoisuusVaatimukset();
        $scope.loadKoulutukses(filterHakus);
        $scope.canSaveParam($scope.model.hakukohde.hakuOid);
        $scope.haeTarjoajaOppilaitosTyypit();
        $scope.model.continueToReviewEnabled = $scope.checkJatkaBtn($scope.model.hakukohde);
        $scope.checkIsCopy();

        if ($scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset === undefined) {
            $scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset = {};
        }
        
        if ($scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua === undefined) {
            $scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua = true;
        }

        // Mahdolliset Organisaatiopalvelun hakukohdetyhmät joissa hakukohde voi olla, routerissa resolvattuna
        // [{ key: XXX, value: YYY}, ...]
        $scope.model.hakukohdeRyhmat = organisationGroups;

        // Alusta ryhmälista tyhjäksi jos ei valintoja
        if (!$scope.model.hakukohde.organisaatioRyhmaOids) {
            $scope.model.hakukohde.organisaatioRyhmaOids = [];
        }

        $scope.enableOrDisableTabs();
    };

    init();

    //$scope.model.koodiuriPromise = $q.defer();

    /*

        ---> If creating new hakukohde then tabs are disabled, when hakukohde has oid then
        tabs are enabled

     */




    $scope.model.kieliCallback = function(kieliUri) {
        if ($scope.model.allkieles !== undefined) {
            var lisatietoFound = false;
            //Check that selected kieli does not exist in list
            angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto) {
                 if (lisatieto.uri === kieliUri) {
                     lisatietoFound = true;
                 }
            });
            if (!lisatietoFound) {
                var foundKoodi = $scope.findKoodiWithUri(kieliUri,$scope.model.allkieles);
                var newLisatieto = {
                    "uri" : foundKoodi.koodiUri,
                    "nimi" : foundKoodi.koodiNimi,
                    "teksti": ""
                };

                $scope.model.hakukohde.lisatiedot.push(newLisatieto);
            }

        }
    };

    $scope.model.kieliRemoveCallback = function(kieliUri) {
      $scope.removeLisatieto(kieliUri);
    };


    /*

        -----> Checkbox change listener to retrieve hakus end time if selected

     */

    $scope.model.checkboxChange = function() {



        if ($scope.model.hakukohde.kaytetaanHaunPaattymisenAikaa) {
            var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);

            var hakuPaattymisAika;

            angular.forEach(haku.hakuaikas,function(hakuaika){
                if (hakuPaattymisAika === undefined) {
                    hakuPaattymisAika = hakuaika.loppuPvm;
                } else {
                    if (hakuPaattymisAika < hakuaika.loppuPvm) {
                        hakuPaattymisAika = hakuaika.loppuPvm;
                    }
                }

            });

            if (hakuPaattymisAika !== undefined) {
                $scope.model.hakukohde.liitteidenToimitusPvm = hakuPaattymisAika;

            }


        }





    };



    /*

        ------> Haku combobox listener -> listens to selected haku to check whether it contains inner application periods

     */

    var resolveLocalizedValue = function(key) {
        var userKieliUri = LocalisationService.getKieliUri();
        return key[userKieliUri] || key["kieli_fi"] || key["kieli_sv"] || key["kieli_en"] || "[Ei nimeä]";
    };

    $scope.model.hakuChanged = function() {

        if ($scope.model.hakukohde.hakuOid !== undefined) {

            $scope.model.hakuaikas.splice(0,$scope.model.hakuaikas.length);
            var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);

            if (haku && haku.hakuaikas !== undefined && haku.hakuaikas.length > 1) {

                angular.forEach(haku.hakuaikas,function(hakuaika){

                    var formattedStartDate = $scope.createFormattedDateString(hakuaika.alkuPvm);

                    var formattedEndDate = $scope.createFormattedDateString(hakuaika.loppuPvm);

                    hakuaika.formattedNimi = resolveLocalizedValue(hakuaika.nimet) + ", " + formattedStartDate + " - " + formattedEndDate;

                    $scope.model.hakuaikas.push(hakuaika);
                });

                $log.debug('HAKUAIKAS : '  ,$scope.model.hakuaikas);

                $scope.model.showHakuaikas = true;

            } else {

                $scope.model.showHakuaikas = false;

            }

        }


    };

    /*

        ------> Hakukohde save functions

     */

    $scope.model.saveValmis = function() {
        $scope.model.saveParent("VALMIS", $scope.validateHakukohde);
    };

    $scope.model.saveLuonnos = function() {
        $scope.model.saveParent("LUONNOS", $scope.validateHakukohde);
    };

    $scope.$watch(function(){ return angular.toJson($scope.model.hakukohde.valintaperusteKuvaukset); }, function(n, o){
    	if (!angular.equals(n,o) && o!="{}") {
    		$scope.status.dirty = true;
    	}
	});

    $scope.$watch(function(){ return angular.toJson($scope.model.hakukohde.soraKuvaukset); }, function(n, o){
    	if (!angular.equals(n,o) && o!="{}") {
    		$scope.status.dirty = true;
    	}
	});	

});

