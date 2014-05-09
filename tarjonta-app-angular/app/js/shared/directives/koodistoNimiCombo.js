'use strict';

var app = angular.module('KoodistoCombo', ['ngResource', 'Logging']);

app.directive('koodistocombo',function(Koodisto,$log){
    
    $log = $log.getInstance("<koodistocombo>");

    var filterKoodis = function(koodistoFilterUri,koodisParam) {
        var filteredkoodis = [];

        angular.forEach(koodisParam, function(koodi){
            if (koodi.koodiKoodisto === koodistoFilterUri) {
                filteredkoodis.push(koodi);
            }
        });

        return filteredkoodis;
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



    return {

        restrict:'EA',
        require: '^form',
        replace: true,
        templateUrl : "js/shared/directives/koodistoNimiCombo.html",
        scope: {
            koodistouri : "=",
            koodiuri : "=",
            locale : "=",
            isdependent : "=",
            filterwithkoodistouri : "=",
            version : "=",
            isrequired : "=",
            usearvocombo : "=",
            parentkoodiuri : "=",
            filteruris : "=",
            prompt : "=",
            isalakoodi : "=",
            onchangecallback : "="

        },
        controller :  function($scope,Koodisto) {

        	
        	$scope.baseKoodis = [];

            var addVersionToKoodis = function(koodis) {

                if ($scope.version !== undefined && $scope.version) {
                    angular.forEach(koodis,function(koodi){
                        if (koodi.koodiUri.indexOf("#") < 0) {
                            koodi.koodiUri = koodi.koodiUri + "#"+koodi.koodiVersio;
                        } else {
                            $log.warn("addVersionToKoodis - tried to add version to already versioned URI!", koodi);
                        }
                    });
                }

            }

            if ($scope.isrequired !== undefined && $scope.isrequired === "true" || $scope.isrequired) {

                $scope.valuerequired = true;
            }  else {
                $scope.valuerequired = false;

            }


            if ($scope.usearvocombo !== undefined) {
                $scope.combotype = {
                    value : "arvo"
                };
            } else {
                $scope.combotype = {
                    value : "nimi"
                };
            }

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
                           if ($scope.filterwithkoodistouri !== undefined || $scope.koodistouri) {
                               if ($scope.version !== undefined && $scope.version) {
                                   addVersionToKoodis(koodisParam);
                               }
                               $scope.koodis = filterKoodis($scope.filterwithkoodistouri ? $scope.filterwithkoodistouri : $scope.koodistouri,koodisParam);

                           } else {
                               addVersionToKoodis(koodisParam);
                               $scope.koodis = koodisParam;
                           }

                           $scope.baseKoodis = $scope.koodis;
                       });
                   } else {
                   $log.info('PARENT KOODI WAS DEFINED GETTING YLAPUOLISET KOODIT...');
                   var koodisPromise = Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri,$scope.locale);
                   koodisPromise.then(function(koodisParam){
                       $log.info('PARENT KOODI WAS DEFINED YLAPUOLISET KOODIT : ' ,koodisParam);
                       if ($scope.version !== undefined && $scope.version) {
                           angular.forEach(koodisParam,function(koodi){
                               koodi.koodiUri = koodi.koodiUri + "#"+koodi.koodiVersio;
                           });
                       }
                       $scope.koodis = koodisParam;
                       $scope.baseKoodis = $scope.koodis;
                   });
                   }
               }


           } else {
               var koodisPromise = Koodisto.getAllKoodisWithKoodiUri($scope.koodistouri,$scope.locale);
               koodisPromise.then(function(koodisParam){
            	   var cs = angular.copy(koodisParam);
                   addVersionToKoodis(cs);
                   $scope.koodis = cs;
                   $scope.baseKoodis = $scope.koodis;
               });
           }

            //If filter uris is changed then query only those and show those koodis
            $scope.$watch('filteruris',function(){
                if ($scope.filteruris !== undefined && $scope.filteruris.length > 0) {
                    var filteredKoodis = [];
                    angular.forEach($scope.baseKoodis,function(koodi){

                        angular.forEach($scope.filteruris,function(filterUri){
                            if (koodi.koodiUri === filterUri) {
                                filteredKoodis.push(koodi);
                            };
                        });

                    });
                    $scope.koodis = filteredKoodis;
                }
            });

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

                            if ($scope.filterwithkoodistouri !== undefined || $scope.koodistouri) {
                                if ($scope.version !== undefined && $scope.version) {
                                    addVersionToKoodis(koodisParam);
                                }
                                $scope.koodis = filterKoodis($scope.filterwithkoodistouri ? $scope.filterwithkoodistouri : $scope.koodistouri,koodisParam);

                            } else {
                                addVersionToKoodis(koodisParam);
                                $scope.koodis = koodisParam;
                            }

                        });
                    } else {
                        var koodisPromise = Koodisto.getYlapuolisetKoodit($scope.parentkoodiuri,$scope.locale);
                        koodisPromise.then(function(koodisParam){

                            if ($scope.filterwithkoodistouri !== undefined || $scope.koodistouri){
                                addVersionToKoodis(koodisParam);
                               $scope.koodis = filterKoodis($scope.filterwithkoodistouri ? $scope.filterwithkoodistouri : $scope.koodistouri,koodisParam,koodisParam);
                            } else {
                                addVersionToKoodis(koodisParam);
                                $scope.koodis = koodisParam;
                            }


                        });
                    }
                }
            });

          $scope.onKoodistoComboChange = function() {
              if ($scope.onchangecallback !== undefined) {
                  $log.info('Select koodiuri ');

                  $log.info($scope.koodiuri);

                  var koodi = findKoodiWithUri($scope.koodiuri,$scope.koodis);

                  $log.info('Found koodi : ', koodi);

                  $scope.onchangecallback(koodi);

              } else {
                  //$log.info('No onchangecallback defined');
              }
          };

        }

    }
});
