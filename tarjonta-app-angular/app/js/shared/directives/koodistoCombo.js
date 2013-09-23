'use strict';

var app = angular.module('KoodistoCombo', ['ngResource']);

app.directive('koodistocombo',function(Koodisto){
    return {

        restrict:'E',
        replace:true,
        templateUrl : "/js/shared/directives/koodistoCombo.html",
        scope: {
            koodistouri : "=",
           koodiuri : "=",
           locale : "="

        },
        controller :  function($scope,Koodisto) {
           console.log('KoodistoUri : ' + $scope.koodistouri);
           console.log('Locale : ' + $scope.locale);
           var koodisPromise = Koodisto.getAllKoodisWithKoodiUri($scope.koodistouri,$scope.locale);
           koodisPromise.then(function(koodisParam){
               $scope.koodis = koodisParam;
           });
        }

    }
});
