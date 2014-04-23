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


var app = angular.module('app.kk.edit.hakukohde.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','MonikielinenTextArea','MultiSelect','ngGrid','TarjontaOsoiteField']);


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

    var commonExceptionMsgKey = "tarjonta.common.unexpected.error.msg";

    //Initialize all variables and scope object in the beginning
    var postinumero = undefined;



    $scope.model.userLang  =  AuthService.getLanguage();

    if ($scope.model.userLang === undefined) {
        $scope.model.userLang = "FI";
    }


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

        return CommonUtilService.canSaveAsLuonnos($scope.model.hakukohde.tila);

    };

    if ($scope.model.hakukohde.lisatiedot !== undefined) {
        angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto){

            $scope.model.selectedKieliUris.push(lisatieto.uri);
        });
    }

    //Placeholder for multiselect remove when refactored
    $scope.model.temp = {};

    var init = function() {
        $scope.loadHakukelpoisuusVaatimukset();
        $scope.loadKoulutukses();
        $scope.haeTarjoajaOppilaitosTyypit();
        $scope.model.continueToReviewEnabled = $scope.checkJatkaBtn($scope.model.hakukohde);
        $scope.checkIsCopy();
        $scope.updateTilaModel($scope.model.hakukohde);

        if ($scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset === undefined) {
            $scope.model.hakukohde.hakukelpoisuusVaatimusKuvaukset = {};
        }

        if ($scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua === undefined) {
            $scope.model.hakukohde.kaytetaanJarjestelmanValintaPalvelua = true;
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


    $scope.model.hakuChanged = function() {


        if ($scope.model.hakukohde.hakuOid !== undefined) {

            $scope.model.hakuaikas.splice(0,$scope.model.hakuaikas.length);
            var haku = $scope.getHakuWithOid($scope.model.hakukohde.hakuOid);

            if (haku && haku.hakuaikas !== undefined && haku.hakuaikas.length > 1) {

                angular.forEach(haku.hakuaikas,function(hakuaika){

                    var formattedStartDate = $scope.createFormattedDateString(hakuaika.alkuPvm);

                    var formattedEndDate = $scope.createFormattedDateString(hakuaika.loppuPvm);

                    hakuaika.formattedNimi = hakuaika.nimi + ", " + formattedStartDate + " - " + formattedEndDate;

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
        $scope.model.showError = false;
        PermissionService.permissionResource().authorize({}, function(authResponse) {
        $scope.emptyErrorMessages();
        if ($scope.model.canSaveHakukohde() && $scope.validateHakukohde()) {
        $scope.model.showError = false;
        if ($scope.model.hakukohde.tila !== $scope.julkaistuVal) {
            $scope.model.hakukohde.tila = $scope.valmisVal;
        }

        $scope.model.hakukohde.modifiedBy = AuthService.getUserOid();
        $scope.removeEmptyKuvaukses();

        if ($scope.model.hakukohde.oid === undefined) {

             $log.debug('SAVE VALMIS MODEL : ', $scope.model.hakukohde);
           var returnResource =   $scope.model.hakukohde.$save();
           returnResource.then(function(hakukohde){

               if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
               $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                   $scope.updateTilaModel($scope.model.hakukohde);
                   $scope.showSuccess();
                   $scope.checkIfSavingCopy($scope.model.hakukohde);
               } else {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.showError(hakukohde.errors);
               }
               if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                   $scope.model.hakukohde.valintaperusteKuvaukset = {};
               }
               if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                   $scope.model.hakukohde.soraKuvaukset = {};
               }
               $scope.canEdit = true;
               $scope.model.continueToReviewEnabled = true;

           },function(error){


               $scope.showCommonUnknownErrorMsg();
           });

        } else {

            $log.debug('UPDATE MODEL : ', $scope.model.hakukohde);

            var returnResource = $scope.model.hakukohde.$update();
            returnResource.then(function(hakukohde){
                if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);

                    $scope.updateTilaModel($scope.model.hakukohde);
                    $scope.showSuccess();
                } else {
                    $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                    $scope.showError(hakukohde.errors);
                }

                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                }
                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                    $scope.model.hakukohde.soraKuvaukset = {};
                }
            },function (error) {
               $scope.showCommonUnknownErrorMsg();
            });

        }
        } else {
            $scope.model.showError = true;
        }
    })
    };

    $scope.model.saveLuonnos = function() {

        $scope.model.showError = false;
        PermissionService.permissionResource().authorize({}, function(authResponse) {

        $log.debug('GOT AUTH RESPONSE : ' , authResponse);
        $scope.emptyErrorMessages();

        if ($scope.model.canSaveHakukohde() && $scope.validateHakukohde()) {
        $scope.model.showError = false;
            if ($scope.model.hakukohde.tila === undefined || $scope.model.hakukohde.tila === $scope.luonnosVal) {
            $scope.model.hakukohde.tila = $scope.luonnosVal;
            }

        $scope.model.hakukohde.modifiedBy = AuthService.getUserOid();
        $scope.removeEmptyKuvaukses();

        //Check if hakukohde is copy, then remove oid and save hakukohde as new
        $scope.checkIsCopy($scope.luonnosVal);
        if ($scope.model.hakukohde.oid === undefined) {

            $log.debug('LISATIEDOT : ' , $scope.model.hakukohde.lisatiedot);

            $log.debug('INSERTING MODEL: ', $scope.model.hakukohde);
           var returnResource =  $scope.model.hakukohde.$save();
            returnResource.then(function(hakukohde) {
               if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                   $scope.model.hakukohdeOid = $scope.model.hakukohde.oid;
                   $scope.updateTilaModel($scope.model.hakukohde);
                   $scope.showSuccess();
                   $scope.checkIfSavingCopy($scope.model.hakukohde);
               } else {
                   $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                  $scope.showError(hakukohde.errors);
               }
                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                }
                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                    $scope.model.hakukohde.soraKuvaukset = {};
                }
                $scope.canEdit = true;
                $scope.model.continueToReviewEnabled = true;
                $log.debug('SAVED MODEL : ', $scope.model.hakukohde);
            },function(error) {
                $log.debug('ERROR INSERTING HAKUKOHDE : ', error);
                $scope.showCommonUnknownErrorMsg();

            });

        } else {
            $log.debug('UPDATE MODEL : ', $scope.model.hakukohde);
            var returnResource =  $scope.model.hakukohde.$update();
            returnResource.then(function(hakukohde){
                if (hakukohde.errors === undefined || hakukohde.errors.length < 1) {
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                $scope.updateTilaModel($scope.model.hakukohde);
                $scope.showSuccess();
                } else {
                    $scope.model.hakukohde = new Hakukohde(hakukohde.result);
                    $scope.showError(hakukohde.errors);

                }
                if ($scope.model.hakukohde.valintaperusteKuvaukset === undefined) {
                    $scope.model.hakukohde.valintaperusteKuvaukset = {};
                }
                if ($scope.model.hakukohde.soraKuvaukset === undefined) {
                    $scope.model.hakukohde.soraKuvaukset = {};
                }
            }, function(error) {

                $log.debug('EXCEPTION UPDATING HAKUKOHDE AS LUONNOS : ', error);
                $scope.showCommonUnknownErrorMsg();
            });
        }
        } else {
            $scope.model.showError = true;
            $log.debug('WHAAT : ' , $scope.model.showError && $scope.editHakukohdeForm.aloituspaikatlkm.$invalid)

        }
    })
    };



});

