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


var app = angular.module('app.kk.edit.hakukohde.ctrl',['app.services','Haku','Organisaatio','Koodisto','localisation','Hakukohde','config']);


app.controller('HakukohdeEditController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde, HakuService, $modal ,Config,$location) {







    //Initialize all helper etc. variable in the beginning of the controller
    var postinumero = undefined;
    //All kieles is received from koodistomultiselect
    $scope.allkieles = [];
    $scope.selectedKieliUris = [];

    if ($scope.model.hakukohde.lisatiedot !== undefined) {
        angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto){

            $scope.selectedKieliUris.push(lisatieto.uri);
        });
    }

    $scope.postinumeroarvo = {

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




    //$scope.koodiuriPromise = $q.defer();

    $scope.hakus = [];




    //TODO: get locale from somewhere
    var koodistoPromise = Koodisto.getAllKoodisWithKoodiUri('posti','FI');

    koodistoPromise.then(function(koodisParam){
      $scope.koodis = koodisParam;

      if (postinumero !== undefined) {
          console.log('Changing arvo : ', postinumero);
          var koodi =  findKoodiWithUri(postinumero,$scope.koodis);
          console.log('TO : ', koodi);
          $scope.postinumeroarvo.arvo = koodi.koodiArvo;
      }
    });

    $scope.onKoodistoComboChange = function() {
       var koodi = findKoodiWithArvo($scope.postinumeroarvo.arvo,$scope.koodis);

       $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero = koodi.koodiUri;
       $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = koodi.koodiNimi;

    };
    console.log('HAKUKOHDE MODEL', $scope.model.hakukohde);
    var orgPromise =  OrganisaatioService.byOid($scope.model.hakukohde.tarjoajaOids[0]);
    //When organisaatio is loaded set the liitteiden toimitusosoite on the model
    orgPromise.then(function(data){
        if (data.postiosoite !== undefined) {

            console.log('GOT OSOITE:', data.postiosoite);
            $scope.model.hakukohde.liitteidenToimitusOsoite.osoiterivi1 = data.postiosoite.osoite;
            $scope.model.hakukohde.liitteidenToimitusOsoite.postinumero = data.postiosoite.postinumeroUri;
            $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = data.postiosoite.postitoimipaikka;
            postinumero = data.postiosoite.postinumeroUri;
        }
    });


    $scope.kieliCallback = function(kieliUri) {
        if ($scope.allkieles !== undefined) {
            var lisatietoFound = false;
            //Check that selected kieli does not exist in list
            angular.forEach($scope.model.hakukohde.lisatiedot,function(lisatieto) {
                 if (lisatieto.uri === kieliUri) {
                     lisatietoFound = true;
                 }
            });
            if (!lisatietoFound) {
                var foundKoodi = findKoodiWithUri(kieliUri,$scope.allkieles);
                var newLisatieto = {
                    "uri" : foundKoodi.koodiUri,
                    "nimi" : foundKoodi.koodiNimi,
                    "teksti": ""
                };

                $scope.model.hakukohde.lisatiedot.push(newLisatieto);
            }

        }
    };

    $scope.kieliRemoveCallback = function(kieliUri) {
      removeLisatieto(kieliUri);
    };

    $scope.postinumeroCallback = function(selectedPostinumero) {
       console.log('Postinumero callback : ', selectedPostinumero);

       $scope.model.hakukohde.liitteidenToimitusOsoite.postitoimipaikka = selectedPostinumero.koodiNimi;
    };

    //TODO: Should tila come from constants ?

    $scope.saveValmis = function() {
        if ($scope.model.hakukohde.oid === undefined) {
            $scope.model.hakukohde.tila = "VALMIS";


            console.log('MODEL: ', $scope.model.hakukohde);
            $scope.model.hakukohde.$save();
        }
    };


    $scope.saveLuonnos = function() {
        //TODO: are we inserting or updating figure it from OID
        if ($scope.model.hakukohde.oid === undefined) {
            $scope.model.hakukohde.tila = "LUONNOS";


            console.log('MODEL: ', $scope.model.hakukohde);
            $scope.model.hakukohde.$save();
        }
    };

    $scope.takaisin = function() {
        $location.path('/etusivu');
    };

    var hakuPromise = HakuService.getAllHakus();


    hakuPromise.then(function(hakuDatas) {

        angular.forEach(hakuDatas,function(haku){
            $scope.hakus.push(haku);
        });

    });


    //Hakukohde nimi chooser dialog controller
    var NimiModalInstanceCtrl = function ($scope, $modalInstance) {

        $scope.hakukohdenimi = {};

        $scope.selectedKieliKoodi;

        $scope.kieliComboCallback = function(koodi) {

            $scope.selectedKieliKoodi = koodi;

        };


        $scope.ok = function () {



            $scope.hakukohdenimi.nimi  = $scope.selectedKieliKoodi.koodiNimi;

            $modalInstance.close($scope.hakukohdenimi);
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    };

    $scope.removeNimi = function(hakukohdeNimi){
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

    $scope.openNimiDialog = function() {

        var modalInstance = $modal.open({
            templateUrl: 'partials/hakukohde/edit/hakukohdeNimiChooserDialog.html',
            controller: NimiModalInstanceCtrl,
            scope: $scope

        });

        modalInstance.result.then(function (selectedItem) {


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



});