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


var app = angular.module('app.kk.edit.hakukohde.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','auth','config','ui.tinymce']);


app.controller('HakukohdeEditController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde,AuthService, HakuService, $modal ,Config,$location,$timeout) {

    $scope.model.userLang  =  AuthService.getLanguage();

    $scope.model.showSuccess = false;

    $scope.model.collapse.model = true;

    var showSuccess = function() {
        $scope.model.showSuccess = true;
        $timeout(function(){


            $scope.model.showSuccess = false;
        },5000);
    }

    //Initialize all helper etc. variable in the beginning of the controller
    var postinumero = undefined;
    //All kieles is received from koodistomultiselect
    $scope.model.allkieles = [];
    $scope.model.selectedKieliUris = [];

    if ($scope.model.hakukohde.lisatiedot !== undefined) {
        angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto){

            $scope.model.selectedKieliUris.push(lisatieto.uri);
        });
    }

    //Placeholder for multiselect remove when refactored
    $scope.model.temp = {};




    $scope.model.hakukelpoisuusVaatimusPromise = Koodisto.getAllKoodisWithKoodiUri('hakukelpoisuusvaatimusta',AuthService.getLanguage());

    $scope.model.postinumeroarvo = {

    };


    var removeLisatieto = function(koodi){

        var foundLisatieto;
         angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto) {
               if (lisatieto.uri === koodi) {
                   foundLisatieto = lisatieto;
               }
         });

        if (foundLisatieto !== undefined) {
            var index = $scope.model.hakukohde.lisatiedot.indexOf(foundLisatieto);
            $scope.model.hakukohde.lisatiedot.splice(index,1);
        }

    }

    //Koodisto helper methods
    var findKoodiWithArvo = function(koodi,koodis)  {


        console.log('Trying to find with : ',koodi);
        console.log('From :', koodis.length);
        var foundKoodi;

        angular.forEach(koodis,function(koodiLoop){
            if (koodiLoop.koodiArvo === koodi){
                foundKoodi = koodiLoop;
            }
        });


        return foundKoodi;
    };

    var findKoodiWithUri = function(koodi,koodis)  {


        var foundKoodi;

        angular.forEach(koodis,function(koodiLoop){
            if (koodiLoop.koodiUri === koodi){
                foundKoodi = koodiLoop;
            }
        });


        return foundKoodi;
    };


    $scope.model.canSaveHakukohde = function() {
        if ($scope.editHakukohdeForm !== undefined) {
            return $scope.editHakukohdeForm.$valid;
        } else {
            return false;
        }

    }


    //$scope.model.koodiuriPromise = $q.defer();

    $scope.model.hakus = [];




    //TODO: get locale from somewhere
    var koodistoPromise = Koodisto.getAllKoodisWithKoodiUri('posti','FI');

    koodistoPromise.then(function(koodisParam){
      $scope.model.koodis = koodisParam;

      if (postinumero !== undefined) {
          console.log('Changing arvo : ', postinumero);
          var koodi =  findKoodiWithUri(postinumero,$scope.model.koodis);
          console.log('TO : ', koodi);
          $scope.model.postinumeroarvo.arvo = koodi.koodiArvo;
      }
    });

    $scope.model.onKieliTypeAheadChange = function() {
       var koodi = findKoodiWithArvo($scope.model.postinumeroarvo.arvo,$scope.model.koodis);

       $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero = koodi.koodiUri;
       $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = koodi.koodiNimi;

    };

    console.log('HAKUKOHDE : ' , $scope.model.hakukohde);

    var orgPromise =  OrganisaatioService.byOid($scope.model.hakukohde.tarjoajaOids[0]);
    //When organisaatio is loaded set the liitteiden toimitusosoite on the model
    orgPromise.then(function(data){
        if (data.postiosoite !== undefined) {


            $scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1 = data.postiosoite.osoite;
            $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero = data.postiosoite.postinumeroUri;
            $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = data.postiosoite.postitoimipaikka;
            postinumero = data.postiosoite.postinumeroUri;
        }
    });


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
                var foundKoodi = findKoodiWithUri(kieliUri,$scope.model.allkieles);
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
      removeLisatieto(kieliUri);
    };

    $scope.model.postinumeroCallback = function(selectedPostinumero) {
       console.log('Postinumero callback : ', selectedPostinumero);

       $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = selectedPostinumero.koodiNimi;
    };



    $scope.model.saveValmis = function() {

        $scope.model.hakukohde.tila = "VALMIS";
        if ($scope.model.hakukohde.oid === undefined) {

             console.log('MODEL: ', $scope.model.hakukohde);
           var returnResource =   $scope.model.hakukohde.$save();
           returnResource.then(function(hakukohde){
               $scope.model.hakukohde = new Hakukohde(hakukohde.result);
           });

        } else {

            console.log('UPDATE MODEL : ', $scope.model.hakukohde);
            var returnResource = $scope.model.$update();
            returnResource.then(function(hakukohde){
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);
            });
        }
        showSuccess();
    };


    $scope.model.saveLuonnos = function() {

        $scope.model.hakukohde.tila = "LUONNOS";
        if ($scope.model.hakukohde.oid === undefined) {

            console.log('TILA : ', $scope.model.hakukohde.tila);
            console.log('NIMI ' , $scope.model.hakukohde.hakukohteenNimet);
            console.log('MODEL: ', $scope.model.hakukohde);
           var returnResource =  $scope.model.hakukohde.$save();
            returnResource.then(function(hakukohde) {
               $scope.model.hakukohde = new Hakukohde(hakukohde.result);

            });

        } else {
            console.log('UPDATE MODEL : ', $scope.model.hakukohde);
            var returnResource =  $scope.model.hakukohde.$update();
            returnResource.then(function(hakukohde){
                $scope.model.hakukohde = new Hakukohde(hakukohde.result);
            });
        }
       showSuccess();
    };

    $scope.model.takaisin = function() {
        $location.path('/etusivu');
    };

    var hakuPromise = HakuService.getAllHakus();



    hakuPromise.then(function(hakuDatas) {
        console.log('GOT HAKUS ', hakuDatas.length);
        angular.forEach(hakuDatas,function(haku){

            angular.forEach(haku.nimi,function(nimi){
                //TODO: replace this with localization value
               if (nimi.arvo === "FI") {
                   haku.lokalisoituNimi = nimi.teksti;
               }
            });

            $scope.model.hakus.push(haku);
        });

    });


    //Hakukohde nimi chooser dialog controller
    /*
    var NimiModalInstanceCtrl = function ($scope, $modalInstance) {

        $scope.model.hakukohdenimi = {};

        $scope.model.selectedKieliKoodi;

        $scope.model.kieliComboCallback = function(koodi) {

            $scope.model.selectedKieliKoodi = koodi;

        };


        $scope.model.ok = function () {


            console.log('OK :', $scope.model.selectedKieliKoodi);
            $scope.model.hakukohdenimi.nimi  = $scope.model.selectedKieliKoodi.koodiNimi;

            $modalInstance.close($scope.model.hakukohdenimi);
        };

        $scope.model.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    };

    $scope.model.removeNimi = function(hakukohdeNimi){
        if ($scope.model.hakukohde.hakukohteenNimet.length > 1) {

            var nimiToRemove ;



            angular.forEach($scope.model.hakukohde.hakukohteenNimet,function(hakukohteenNimi){
                if (hakukohteenNimi.nimi === hakukohdeNimi.nimi && hakukohteenNimi.uri === hakukohdeNimi.uri) {
                    nimiToRemove = hakukohteenNimi;
                }
            });

           var index = $scope.model.hakukohde.hakukohteenNimet.indexOf(nimiToRemove);
            $scope.model.hakukohde.hakukohteenNimet.splice(index,1);
        }
    };

    $scope.model.openNimiDialog = function() {

        var modalInstance = $modal.open({
            templateUrl: 'partials/hakukohde/edit/hakukohdeNimiChooserDialog.html',
            controller: NimiModalInstanceCtrl,
            scope: $scope

        });

        modalInstance.result.then(function (selectedItem) {

            console.log('SELECTED ITEM:',selectedItem);

            var selectedItemExists = false;

            angular.forEach($scope.model.hakukohde.hakukohteenNimet,function(hakukohdenimi){
                 if (hakukohdenimi.uri === selectedItem.uri) {
                     selectedItemExists = true;
                 }
            });

            if (!selectedItemExists) {
                $scope.model.hakukohde.hakukohteenNimet.push(selectedItem);
            }



        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });

    };
	*/


});
