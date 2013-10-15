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


app.controller('HakukohdeEditController', function($scope,$q, LocalisationService, OrganisaatioService ,Koodisto,Hakukohde, HakuService ,Config) {

    var postinumero = undefined;

    $scope.postinumeroarvo = {

    };

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


    //Initialize model and arrays inside it
    $scope.model = new Hakukohde({

        liitteidenToimitusosoite : {


        },
        hakukelpoisuusvaatimusUris : [],
        hakukohdeKoulutusOids : [],
        opetuskielet : [],

        liitteet : [],
        valintakoes : [],
        lisatiedot : [
            {
                "uri": "kieli_fi",
                "nimi": "FI",
                "teksti": "<p>Lisätietoa hakemisesta</p>"
            }
        ]
    });

    $scope.koodiuriPromise = $q.defer();

    $scope.hakus = [];


    $scope.orgOid = "1.2.246.562.10.61998115317";

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

       $scope.model.liitteidenToimitusosoite.postinumero = koodi.koodiUri;
       $scope.model.liitteidenToimitusosoite.postitoimipaikka = koodi.koodiNimi;

    };

    var orgPromise =  OrganisaatioService.byOid($scope.orgOid);
    //When organisaatio is loaded set the liitteiden toimitusosoite on the model
    orgPromise.then(function(data){
        if (data.postiosoite !== undefined) {

            console.log('GOT OSOITE:', data.postiosoite);
            $scope.model.liitteidenToimitusosoite.osoiterivi1 = data.postiosoite.osoite;
            $scope.model.liitteidenToimitusosoite.postinumero = data.postiosoite.postinumeroUri;
            $scope.model.liitteidenToimitusosoite.postitoimipaikka = data.postiosoite.postitoimipaikka;
            postinumero = data.postiosoite.postinumeroUri;
        }
    });


    $scope.postinumeroCallback = function(selectedPostinumero) {
       console.log('Postinumero callback : ', selectedPostinumero);

       $scope.model.liitteidenToimitusosoite.postitoimipaikka = selectedPostinumero.koodiNimi;
    };

    $scope.insert = function() {
        console.log('Model : ', $scope.model);
      //$scope.model.$save();
    };

    var hakuPromise = HakuService.getAllHakus();


    hakuPromise.then(function(hakuDatas) {

        angular.forEach(hakuDatas,function(haku){
            $scope.hakus.push(haku);
        });

    });

});