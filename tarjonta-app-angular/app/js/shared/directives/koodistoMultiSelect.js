'use strict';

var app = angular.module('KoodistoMultiSelect', ['ngResource']);

app.directive('koodistomultiselect',function(Koodisto,$log){

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
        templateUrl : "js/shared/directives/koodistoMultiSelect.html",
        scope: {
            koodistouri : "=",
            koodiuris : "=",
            locale : "=",
            isdependent : "=",
            filterwithkoodistouri : "=",
            parentkoodiuri : "=",
            isalakoodi : "=",
            onchangecallback : "="

        },
        controller :  function($scope,Koodisto) {

            $scope.koodiuris = [];

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

            $scope.removeSelection = function(selectedValue) {



                  $log.info('Removing object : ');
                 $log.info(selectedValue);

                 $scope.koodiuris = _($scope.koodiuris).select(function(koodi){
                     if (selectedValue.$$hashKey === koodi.$$hashKey) {
                          return false;
                     }else {
                         return true;
                     }
                 });
            };

            $scope.itemSelected = function(item){
                $log.info('Item selected');
                $log.info(item);
                $scope.koodiuris.push(item);
                $scope.koodiuris = _.uniq($scope.koodiuris);
            };

            $scope.onKoodistoComboChange = function() {
                if ($scope.koodiuri !== undefined) {
                    $scope.koodiuris.push($scope.koodiuri);
                    $scope.koodiuris = _.uniq($scope.koodiuris);
                }

                $log.info('Koodi uris');
                $log.info($scope.koodiuris);
                if ($scope.onchangecallback !== undefined) {
                    $log.info('Calling onchangecallback');

                    $scope.onchangecallback($scope.koodiuris);

                } else {
                    $log.info('No onchangecallback defined');
                }
            };

        }

    }
});
