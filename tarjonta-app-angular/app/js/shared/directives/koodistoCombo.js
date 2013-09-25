'use strict';

var app = angular.module('KoodistoCombo', ['ngResource']);

app.directive('koodistocombo',function(Koodisto,$log){

    var filterKoodis = function(koodistoFilterUri,koodisParam) {
        var filteredkoodis = [];

        angular.forEach(koodisParam, function(koodi){
            if (koodi.koodiKoodisto === koodistoFilterUri) {
                filteredkoodis.push(koodi);
            }
        });
        return filteredkoodis;
    };

    return {

        restrict:'E',
        replace:true,
        templateUrl : "js/shared/directives/koodistoCombo.html",
        scope: {
            koodistouri : "=",
            koodiuri : "=",
            locale : "=",
            isdependent : "=",
            filterwithkoodistouri : "=",
            parentkoodiuri : "=",
            prompt : "=",
            isalakoodi : "=",
            onchangecallback : "="

        },
        controller :  function($scope,Koodisto) {

           if ($scope.isdependent) {

               if ($scope.parentkoodiuri !== undefined) {

                   //Default behaviour is to get alakoodis
                   if ($scope.isalakoodi === undefined) {
                       $log.info('isalakoodi was undefined');
                       $scope.isalakoodi = true;
                   }
                   if ($scope.isalakoodi) {

                       var koodisPromise = Koodisto.getAlapuolisetKoodit($scope.parentkoodiuri,$scope.locale);
                       koodisPromise.then(function(koodisParam){
                           $scope.koodis = koodisParam;
                       });
                   } else {
                   var koodisPromise = Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri,$scope.locale);
                   koodisPromise.then(function(koodisParam){
                       $scope.koodis = koodisParam;
                   });
                   }
               }


           } else {
               var koodisPromise = Koodisto.getAllKoodisWithKoodiUri($scope.koodistouri,$scope.locale);
               koodisPromise.then(function(koodisParam){
                   $scope.koodis = koodisParam;
               });
           }

            $scope.$watch('parentkoodiuri',function(){
                $log.info('Parent koodi uri changed');
                if ($scope.parentkoodiuri !== undefined) {
                    //Default behaviour is to get alakoodis
                    if ($scope.isalakoodi === undefined) {
                        $log.info('isalakoodi was undefined');
                        $scope.isalakoodi = true;
                    }
                    if ($scope.isalakoodi) {

                        var koodisPromise = Koodisto.getAlapuolisetKoodit($scope.parentkoodiuri,$scope.locale);
                        koodisPromise.then(function(koodisParam){
                            if ($scope.filterwithkoodistouri !== undefined) {

                                $scope.koodis = filterKoodis($scope.filterwithkoodistouri,koodisParam);

                            } else {
                                $scope.koodis = koodisParam;
                            }

                        });
                    } else {
                        var koodisPromise = Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri,$scope.locale);
                        koodisPromise.then(function(koodisParam){

                            if ($scope.filterwithkoodistouri !== undefined){
                               $scope.koodis = filterKoodis($scope.filterwithkoodistouri,koodisParam);
                            } else {
                                $scope.koodis = koodisParam;
                            }


                        });
                    }
                }
            });

          $scope.onKoodistoComboChange = function() {
              if ($scope.onchangecallback !== undefined) {
                  $log.info('Calling onchangecallback');

                  $scope.onchangecallback($scope.koodiuri);

              } else {
                  $log.info('No onchangecallback defined');
              }
          };

        }

    }
});
