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


var app = angular.module('app.kk.edit.hakukohde.ctrl',['app.services','Haku','localisation','Hakukohde','config']);


app.controller('HakukohdeEditController', function($scope,$q, LocalisationService, Koodisto,Hakukohde, HakuService ,Config) {

    $scope.hakus = [];

    //Initialize model and arrays inside it
    $scope.model = new Hakukohde({

        liitteidenToimitusosoite : {},
        hakukelpoisuusvaatimusUris : [],
        hakukohdeKoulutusOids : [],
        opetuskielet : [],
        liitteet : [],
        valintakoes : []
    });

    $scope.postinumeroCallback = function(selectedPostinumero) {
       console.log('Postinumero callback : ', selectedPostinumero);

       $scope.model.liitteidenToimitusosoite.postitoimipaikka = selectedPostinumero.koodiNimi;
    };

    $scope.insert = function() {
      $scope.model.$save();
    };

    var hakuPromise = HakuService.getAllHakus();


    hakuPromise.then(function(hakuDatas) {

        angular.forEach(hakuDatas,function(haku){
            $scope.hakus.push(haku);
        });

    });

});